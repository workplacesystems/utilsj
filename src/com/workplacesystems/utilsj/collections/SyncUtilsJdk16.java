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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.workplacesystems.utilsj.Callback;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

/**
 * IMPORTANT: This source must be kept in sync with SyncUtilsReentrant
 *
 * @author dave
 */
class SyncUtilsJdk16 extends SyncUtilsReentrant
{
    private final static boolean disableFairSyncLocks = Boolean.getBoolean("com.workplacesystems.utilsj.disableFairSyncLocks");
    private final static boolean debugReadLocks = Boolean.getBoolean("com.workplacesystems.utilsj.debugReadLocks");
    private final static String newLine = System.getProperty("line.separator");

    protected final static Map<DebugReentrantReadWriteLock,Set<Thread>> threadReadLocks =
            debugReadLocks ? Collections.synchronizedMap(new WeakHashMap<DebugReentrantReadWriteLock,Set<Thread>>()) : null;

    /**
     * Creates a new instance of SyncUtilsJdk16
     */
    SyncUtilsJdk16()
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

        if (debugReadLocks)
            return new DebugReentrantReadWriteLock(disableFairSyncLocks ? false : true);
        else
            return new ReentrantReadWriteLock(disableFairSyncLocks ? false : true);
    }

    private static class DebugReentrantReadWriteLock extends ReentrantReadWriteLock {

        private final ReadLock readerLock;

        private DebugReentrantReadWriteLock(boolean fairMode) {
            super(fairMode);
            readerLock = new ReadLock(this);
        }

        @Override
        public ReentrantReadWriteLock.ReadLock  readLock()  { return readerLock; }

        public Thread getOwner0() {
            return getOwner();
        }

        public Collection<Thread> getQueuedWriterThreads0() {
            return getQueuedWriterThreads();
        }

        public Collection<Thread> getQueuedReaderThreads0() {
            return getQueuedReaderThreads();
        }

        public static class ReadLock extends ReentrantReadWriteLock.ReadLock {
            private final Set<Thread> readers = Collections.synchronizedSet(new HashSet<Thread>());

            protected ReadLock(DebugReentrantReadWriteLock lock) {
                super(lock);
                threadReadLocks.put(lock, readers);
            }

            @Override
            public void lock() {
                super.lock();
                readers.add(Thread.currentThread());
            }

            @Override
            public void lockInterruptibly() throws InterruptedException {
                super.lockInterruptibly();
                readers.add(Thread.currentThread());
            }

            @Override
            public  boolean tryLock() {
                boolean locked = super.tryLock();
                if (locked)
                    readers.add(Thread.currentThread());
                return locked;
            }

            @Override
            public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException {
                boolean locked = super.tryLock(timeout, unit);
                if (locked)
                    readers.add(Thread.currentThread());
                return locked;
            }

            @Override
            public  void unlock() {
                super.unlock();
                readers.remove(Thread.currentThread());
            }
        }
    }

    @Override
    void dumpDebugReadLocksImpl(StringBuffer buffer) {
        if (!debugReadLocks)
            return;

        synchronized (threadReadLocks) {
            for (DebugReentrantReadWriteLock debugLock : threadReadLocks.keySet()) {
                if (debugLock == null)
                    continue;

                Set<Thread> readers = threadReadLocks.get(debugLock);

                buffer.append("Thread details for lock ");
                buffer.append(debugLock.toString());
                buffer.append(":");
                buffer.append(newLine);
                dumpThread("  Holding Writer", debugLock.getOwner0(), buffer);
                synchronized (readers) {
                    for (Thread thread : readers)
                        dumpThread("  Holding Reader", thread, buffer);
                }
                for (Thread thread : debugLock.getQueuedWriterThreads0())
                    dumpThread("  Waiting Writer", thread, buffer);
                for (Thread thread : debugLock.getQueuedReaderThreads0())
                    dumpThread("  Waiting Reader", thread, buffer);
            }
        }
    }

    private void dumpThread(String prefix, Thread thread, StringBuffer buffer) {
        if (thread == null)
            return;
        buffer.append(prefix);
        buffer.append(": \"");
        buffer.append(thread.getName());
        buffer.append("\" Thread id: ");
        buffer.append(thread.getId());
        buffer.append(newLine);
        for (StackTraceElement stack : thread.getStackTrace()) {
            buffer.append("      ");
            buffer.append(stack.toString());
            buffer.append(newLine);
        }
        buffer.append(newLine);
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
            boolean interrupted = false;
            try {
                switch (lockType)
                {
                case WRITE:
                    if (lock.getReadHoldCount() > 0 && lock.getWriteHoldCount() == 0)
                        throw new IllegalStateException("Lock cannot be upgraded from read to write");

                    while (true) {
                        try {
                            // Use tryLock(long timeout, TimeUnit unit) with wait time of 0 instead of tryLock() to honour the fairness policy
                            return lock.writeLock().tryLock(0, TimeUnit.SECONDS);
                        }
                        catch (InterruptedException ie) {
                            interrupted = true;
                        }
                    }

                case READ:
                    while (true) {
                        try {
                            // Use tryLock(long timeout, TimeUnit unit) with wait time of 0 instead of tryLock() to honour the fairness policy
                            return lock.readLock().tryLock(0, TimeUnit.SECONDS);
                        }
                        catch (InterruptedException ie) {
                            interrupted = true;
                        }
                    }
                }

                return false;
            }
            finally {
                if (interrupted)
                    Thread.currentThread().interrupt();
            }
        }

        @Override
        void waitForLock(LockType lockType, Object mutex)
        {
            lock(lockType, mutex);
            unlock(lockType, mutex, false);
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
