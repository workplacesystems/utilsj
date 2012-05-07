/*
 * Copyright 2010 Workplace Systems PLC (http://www.workplacesystems.com/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.workplacesystems.utilsj.collections;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.workplacesystems.utilsj.Callback;

/**
 * IMPORTANT: This source must be kept in sync with SyncUtilsReentrant
 *
 * @author dave
 */
class SyncUtilsJdk16 extends SyncUtilsReentrant
{
    private final boolean disableFairSyncLocks;

    /**
     * Creates a new instance of SyncUtilsJdk16
     */
    SyncUtilsJdk16()
    {
        this.disableFairSyncLocks = Boolean.getBoolean("com.workplacesystems.utilsj.disableFairSyncLocks");
    }

    @Override
    Object createMutexImpl(Object suggested_mutex)
    {
        if (suggested_mutex instanceof ReentrantReadWriteLock)
            return suggested_mutex;

        suggested_mutex = getObjectToLock(suggested_mutex);
        if (suggested_mutex instanceof ReentrantReadWriteLock)
            return suggested_mutex;

        return new ReentrantReadWriteLock(disableFairSyncLocks ? false : true);
    }

    class SyncConditionJdk16 implements SyncCondition
    {
        private final Condition condition;

        private SyncConditionJdk16(Condition condition)
        {
            this.condition = condition;
        }

        public void await() throws InterruptedException
        {
            condition.await();
        }

        public void signal()
        {
            condition.signal();
        }

        public void signalAll()
        {
            condition.signalAll();
        }
    }

    @Override
    SyncCondition getSyncConditionImpl(Object suggested_mutex)
    {
        if (suggested_mutex instanceof ReentrantReadWriteLock)
            return new SyncConditionJdk16(((ReentrantReadWriteLock)suggested_mutex).writeLock().newCondition());

        return super.getSyncConditionImpl(suggested_mutex);
    }

    static class SyncWrapperJdk16 extends SyncWrapper
    {
        SyncWrapperJdk16()
        {
            super();
        }

        @Override
        boolean isLegacy(Object mutex)
        {
            return !(mutex instanceof ReentrantReadWriteLock);
        }

        @Override
        protected void lock(LockType lockType, Object mutex)
        {
            ReentrantReadWriteLock lock = (ReentrantReadWriteLock)mutex;
            switch (lockType)
            {
            case WRITE:
                if (lock.getReadHoldCount() > 0 && lock.getWriteHoldCount() == 0)
                    throw new IllegalStateException("Lock cannot be upgraded from read to write");

                lock.writeLock().lock();
                break;

            case READ:
                lock.readLock().lock();
                break;
            }
        }

        @Override
        protected void unlock(LockType lockType, Object mutex, boolean run_release_callback)
        {
            ReentrantReadWriteLock lock = (ReentrantReadWriteLock)mutex;
            Callback<?> release_callback = getReleaseCallback(mutex);
            try
            {
                if (run_release_callback)
                {
                    switch (lockType)
                    {
                    case WRITE:
                        if (lock.getReadHoldCount() == 0 && lock.getWriteHoldCount() == 1 && release_callback != null)
                            release_callback.action();
                        break;

                    case READ:
                        if (lock.getReadHoldCount() == 1 && lock.getWriteHoldCount() == 0 && release_callback != null)
                            release_callback.action();
                        break;
                    }
                }
            }
            finally
            {
                switch (lockType)
                {
                case WRITE:
                    lock.writeLock().unlock();
                    break;

                case READ:
                    lock.readLock().unlock();
                    break;
                }
            }
        }

        @Override
        boolean tryLock(LockType lockType, Object mutex)
        {
            ReentrantReadWriteLock lock = (ReentrantReadWriteLock)mutex;
            switch (lockType)
            {
            case WRITE:
                if (lock.getReadHoldCount() > 0 && lock.getWriteHoldCount() == 0)
                    throw new IllegalStateException("Lock cannot be upgraded from read to write");

                return lock.writeLock().tryLock();

            case READ:
                return lock.readLock().tryLock();
            }

            return false;
        }

        @Override
        void waitForLock(LockType lockType, Object mutex)
        {
            ReentrantReadWriteLock lock = (ReentrantReadWriteLock)mutex;

            switch (lockType)
            {
            case WRITE:
                if (lock.getReadHoldCount() > 0 && lock.getWriteHoldCount() == 0)
                    throw new IllegalStateException("Lock cannot be upgraded from read to write");

                lock.writeLock().lock();
                lock.writeLock().unlock();

                break;

            case READ:
                lock.readLock().lock();
                lock.readLock().unlock();

                break;
            }
        }

        @Override
        int getHoldCount(LockType lockType, Object mutex)
        {
            ReentrantReadWriteLock lock = (ReentrantReadWriteLock)mutex;

            switch (lockType)
            {
            case WRITE:
                return lock.getWriteHoldCount();

            case READ:
                return lock.getReadHoldCount();
            }

            return 0;
        }
    }

    @Override
    SyncWrapper getNewSyncWrapperImpl()
    {
        return new SyncWrapperJdk16();
    }
}
