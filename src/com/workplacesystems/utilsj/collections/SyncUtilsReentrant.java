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

import com.workplacesystems.utilsj.Callback;
import com.workplacesystems.utilsj.Condition;
import edu.emory.mathcs.backport.java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * IMPORTANT: This source must be kept in sync with SyncUtilsJdk16
 *
 * @author dave
 */
class SyncUtilsReentrant extends SyncUtilsLegacy
{
    /**
     * Creates a new instance of SyncUtilsReentrant
     */
    SyncUtilsReentrant()
    {
    }

    @Override
    Object createMutexImpl(Object suggested_mutex)
    {
        if (suggested_mutex instanceof ReentrantReadWriteLock)
            return suggested_mutex;

        suggested_mutex = getObjectToLock(suggested_mutex);
        if (suggested_mutex instanceof ReentrantReadWriteLock)
            return suggested_mutex;

        return new ReentrantReadWriteLock();
    }

    class SyncConditionReentrant implements SyncCondition
    {
        private final edu.emory.mathcs.backport.java.util.concurrent.locks.Condition condition;

        private SyncConditionReentrant(edu.emory.mathcs.backport.java.util.concurrent.locks.Condition condition)
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
            return new SyncConditionReentrant(((ReentrantReadWriteLock)suggested_mutex).writeLock().newCondition());

        return super.getSyncConditionImpl(suggested_mutex);
    }

    static class SyncWrapperReentrant extends SyncWrapper
    {
        SyncWrapperReentrant()
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
        return new SyncWrapperReentrant();
    }

    @Override
    <T> T synchronizeWriteImpl(SyncWrapper mutex, Callback<T> callback)
    {
        if (mutex.isLegacy())
            return super.synchronizeWriteImpl(mutex, callback);

        mutex.lock(LockType.WRITE);

        try
        {
            return callback.action();
        }
        finally
        {
            mutex.unlock(LockType.WRITE);
        }
    }

    @Override
    <T> T synchronizeReadImpl(SyncWrapper mutex, Callback<T> callback)
    {
        if (mutex.isLegacy())
            return super.synchronizeReadImpl(mutex, callback);

        mutex.lock(LockType.READ);
        try
        {
            return callback.action();
        }
        finally
        {
            mutex.unlock(LockType.READ);
        }
    }

    @Override
    <T> T synchronizeWriteThenReadImpl(SyncWrapper write_mutex, Callback<?> write_callback, SyncWrapper read_mutex, Callback<T> read_callback)
    {
        if (write_mutex.isLegacy() || read_mutex.isLegacy())
        {
            if (write_mutex.isLegacy() != read_mutex.isLegacy())
                throw new UnsupportedOperationException("Legacy sync cannot be mixed with non-legacy sync.");
            return super.synchronizeWriteThenReadImpl(write_mutex, write_callback, read_mutex, read_callback);
        }

        boolean read_lock = false;
        boolean write_lock = false;

        try
        {
            write_mutex.lock(LockType.WRITE);
            write_lock = true;

            read_mutex.lock(LockType.READ);
            read_lock = true;

            write_callback.action();

            write_mutex.unlock(LockType.WRITE);
            write_lock = false;

            return read_callback.action();
        }
        finally
        {
            if (write_lock)
                write_mutex.unlock(LockType.WRITE);

            if (read_lock)
                read_mutex.unlock(LockType.READ);
        }
    }

    @Override
    <T> T synchronizeConditionalWriteImpl(SyncWrapper mutex, Condition write_condition, Callback<T> write_callback)
    {
        if (mutex.isLegacy())
            return super.synchronizeConditionalWriteImpl(mutex, write_condition, write_callback);

        boolean read_lock = false;
        boolean write_lock = false;

        try
        {
            int read_hold_count = mutex.getHoldCount(LockType.READ);
            if (read_hold_count > 0 && write_condition.isTrue(read_hold_count))
                throw new IllegalStateException("Lock cannot be upgraded from read to write");

            // First take the read lock
            mutex.lock(LockType.READ);
            read_lock = true;

            // Check whether the write condition is true
            if (write_condition.isTrue(read_hold_count))
            {
                mutex.unlock(LockType.READ, false);
                read_lock = false;

                // At this point there is no lock and therefore the write condition maybe altered
                mutex.lock(LockType.WRITE);
                write_lock = true;

                // So check again now we have the write lock
                if (write_condition.isTrue(read_hold_count))
                {
                    write_callback.action();

                    mutex.unlock(LockType.WRITE);
                    write_lock = false;
                }
            }

            return null;
        }
        finally
        {
            if (write_lock)
                mutex.unlock(LockType.WRITE);

            if (read_lock)
                mutex.unlock(LockType.READ);
        }
    }

    @Override
    <T> T synchronizeConditionalWriteThenReadImpl(SyncWrapper write_mutex, Condition write_condition, Callback<?> write_callback, SyncWrapper read_mutex, Callback<T> read_callback)
    {
        if (write_mutex.isLegacy() || read_mutex.isLegacy())
        {
            if (write_mutex.isLegacy() != read_mutex.isLegacy())
                throw new UnsupportedOperationException("Legacy sync cannot be mixed with non-legacy sync.");
            return super.synchronizeConditionalWriteThenReadImpl(write_mutex, write_condition, write_callback, read_mutex, read_callback);
        }

        boolean read_lock = false;
        boolean write_lock = false;

        try
        {
            int read_hold_count = write_mutex.getHoldCount(LockType.READ);
            if (read_hold_count > 0 && write_condition.isTrue(read_hold_count))
                throw new IllegalStateException("Lock cannot be upgraded from read to write");

            // First take the read lock
            read_mutex.lock(LockType.READ);
            read_lock = true;

            // Check whether the write condition is true
            if (write_condition.isTrue(read_hold_count))
            {
                read_mutex.unlock(LockType.READ, false);
                read_lock = false;

                // At this point there is no lock and therefore the write condition maybe altered
                write_mutex.lock(LockType.WRITE);
                write_lock = true;

                // Retake the read lock now we have the write lock
                read_mutex.lock(LockType.READ);
                read_lock = true;

                // So check again now we have the write lock
                if (write_condition.isTrue(read_hold_count))
                {
                    write_callback.action();

                    write_mutex.unlock(LockType.WRITE);
                    write_lock = false;
                }
            }

            return read_callback.action();
        }
        finally
        {
            if (write_lock)
                write_mutex.unlock(LockType.WRITE);

            if (read_lock)
                read_mutex.unlock(LockType.READ);
        }
    }
}
