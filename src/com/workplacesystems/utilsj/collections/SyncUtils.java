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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.workplacesystems.utilsj.Callback;
import com.workplacesystems.utilsj.Condition;
import com.workplacesystems.utilsj.UtilsjException;
import com.workplacesystems.utilsj.collections.decorators.SynchronizedDecorator;

/**
 *
 * @author  dave
 */
public abstract class SyncUtils
{
    private final static SyncUtils sync_utils_instance;

    static
    {
        SyncUtils local_sync_util;

        boolean java16 = false;
        try
        {
            Class.forName("java.util.ArrayDeque");
            // We have JDK 1.6 at least if we're here
            java16 = true;
        } catch (ClassNotFoundException cnfe) {
            // swallow as we've hit the max class version that we have
        }

        if (java16)
        {
            try
            {
                Class<?> sync_jdk16_class = Class.forName("com.workplacesystems.utilsj.collections.SyncUtilsJdk16");
                local_sync_util = (SyncUtils)sync_jdk16_class.newInstance();
            }
            catch (Exception e)
            {
                new UtilsjException("JDK 1.6 was detected but SyncUtilsJdk16 class cannot be found.");
                local_sync_util = new SyncUtilsReentrant();
            }
        }
        else
            local_sync_util = new SyncUtilsReentrant();

        sync_utils_instance = local_sync_util;
    }

    enum LockType {
        READ,
        WRITE;
    }

    /** Creates a new instance of SyncUtils */
    SyncUtils() {}
    
    public final static Object getLockObject(final Map<?, ?> map)
    {
        return getObjectToLock(map);
    }
    
    public final static Object getLockObject(final Collection<?> col)
    {
        return getObjectToLock(col);
    }
    
    protected final static Object getObjectToLock(final Object obj)
    {
        if (obj == null) // Lets be kind to developers so they don't need to worry if the lock object maybe null
            return createMutex(null);

        // If the obj is a SynchronizedDecorator, recurse to get the real object to lock against
        if (obj instanceof SynchronizedDecorator)
        {
            SynchronizedDecorator sd = (SynchronizedDecorator)obj;
            return sd.getLockObject();
        }
        
        // Otherwise just lock on the object passed in
        return obj;
    }

    public static SyncCondition getSyncCondition(final Object obj)
    {
        return sync_utils_instance.getSyncConditionImpl(getObjectToLock(obj));
    }

    public static abstract class SyncWrapper
    {
        private final List<Object> objects_to_lock = new ArrayList<Object>();
        private final HashMap<Object, Callback> release_callbacks = new HashMap<Object, Callback>();
        private final List<Object> locked = new ArrayList<Object>();

        SyncWrapper() {}

        public final void addObjectToLock(Object obj)
        {
            addObjectToLock(obj, null);
        }

        public final void addObjectToLock(Object obj, Callback<?> release_callback)
        {
            Object mutex = getObjectToLock(obj);
            if (!objects_to_lock.contains(mutex))
            {
                objects_to_lock.add(mutex);
                if (release_callback != null)
                    release_callbacks.put(mutex, release_callback);
            }
        }

        final Callback<?> getReleaseCallback(Object mutex)
        {
            return release_callbacks.get(mutex);
        }

        final boolean isLegacy()
        {
            boolean legacy = false;
            for (Object mutex : objects_to_lock)
            {
                legacy = legacy || isLegacy(mutex);
                if (legacy)
                    break;
            }

            if (legacy)
            {
                if (objects_to_lock.size() > 1)
                    throw new UnsupportedOperationException("Legacy sync lists not supported");
                else if (objects_to_lock.size() == 1 && release_callbacks.get(objects_to_lock.get(0)) != null)
                    throw new UnsupportedOperationException("Release callbacks not supported for legacy sync");
            }

            return legacy;
        }

        final Object getMutex(int idx)
        {
            return objects_to_lock.get(idx);
        }

        final void lock(LockType lockType)
        {
            if (objects_to_lock.size() > 1)
            {
                boolean lock_complete = false;
                try
                {
                    if (!locked.isEmpty())
                        throw new IllegalStateException("lock already called");

                    boolean all_locked = false;
                    while (!all_locked)
                    {
                        all_locked = true;

                        for (Object mutex: objects_to_lock)
                        {
                            if (tryLock(lockType, mutex))
                                locked.add(mutex);
                            else
                            {
                                unlock(lockType, false);
                                all_locked = false;
                                /*try
                                {
                                    Thread.sleep((long)(100L + Math.random() * 400L));
                                }
                                catch (InterruptedException e) {}*/
                                waitForLock(lockType, mutex);
                                break;
                            }
                        }
                    }
                    lock_complete = true;
                }
                finally
                {
                    if (!lock_complete)
                        unlock(lockType);
                }
            }
            else
            {
                if (!objects_to_lock.isEmpty())
                    lock(lockType, objects_to_lock.get(0));
            }
        }

        final void unlock(LockType lockType)
        {
            unlock(lockType, true);
        }

        final void unlock(LockType lockType, boolean run_release_callback)
        {
            if (objects_to_lock.size() > 1)
            {
                UtilsjException ce = null;
                for (Iterator<Object> i = locked.iterator(); i.hasNext(); )
                {
                    Object mutex = i.next();
                    try
                    {
                        unlock(lockType, mutex, run_release_callback);
                    }
                    // Try best to unlock everything before throwing
                    catch (Exception e)
                    {
                        UtilsjException _ce = new UtilsjException(e);
                        if (ce == null)
                            ce = _ce;
                    }
                    i.remove();
                }

                if (ce != null)
                    throw ce;
            }
            else
            {
                if (!objects_to_lock.isEmpty())
                    unlock(lockType, objects_to_lock.get(0), run_release_callback);
            }
        }

        final int getHoldCount(LockType lockType)
        {
            int hold_count = 0;
            for (Object mutex : objects_to_lock)
                hold_count += getHoldCount(lockType, mutex);
            return hold_count;
        }

        abstract boolean isLegacy(Object mutex);

        abstract void waitForLock(LockType lockType, Object mutex);

        abstract boolean tryLock(LockType lockType, Object mutex);

        abstract void lock(LockType lockType, Object mutex);

        abstract void unlock(LockType lockType, Object mutex, boolean run_release_callback);

        abstract int getHoldCount(LockType lockType, Object mutex);
    }

    public static SyncWrapper getNewSyncWrapper(Object suggested_mutex, Callback<?> release_callback)
    {
        SyncWrapper sync = getNewSyncWrapper();
        sync.addObjectToLock(suggested_mutex, release_callback);
        return sync;
    }

    public static SyncWrapper getNewSyncWrapper()
    {
        return sync_utils_instance.getNewSyncWrapperImpl();
    }

    public static Object createMutex(Object suggested_mutex)
    {
        return sync_utils_instance.createMutexImpl(suggested_mutex);
    }

    public static <T> T synchronizeWrite(Object mutex, Callback<T> callback)
    {
        return synchronizeWrite(mutex, callback, null);
    }

    public static <T> T synchronizeWrite(Object mutex, Callback<T> callback, Callback<?> release_callback)
    {
        return sync_utils_instance.synchronizeWriteImpl(getNewSyncWrapper(mutex, release_callback), callback);
    }

    public static <T> T synchronizeWrite(SyncWrapper sync, Callback<T> callback)
    {
        return sync_utils_instance.synchronizeWriteImpl(sync, callback);
    }

    public static <T> T synchronizeRead(Object mutex, Callback<T> callback)
    {
        return synchronizeRead(mutex, callback, null);
    }

    public static <T> T synchronizeRead(Object mutex, Callback<T> callback, Callback<?> release_callback)
    {
        return sync_utils_instance.synchronizeReadImpl(getNewSyncWrapper(mutex, release_callback), callback);
    }

    public static <T> T synchronizeRead(SyncWrapper sync, Callback<T> callback)
    {
        return sync_utils_instance.synchronizeReadImpl(sync, callback);
    }

    public static <T> T synchronizeWriteThenRead(Object mutex, Callback<?> write_callback, Callback<T> read_callback)
    {
        return synchronizeWriteThenRead(mutex, write_callback, null, mutex, read_callback, null);
    }

    public static <T> T synchronizeWriteThenRead(Object write_mutex, Callback<?> write_callback, Object read_mutex, Callback<T> read_callback)
    {
        return synchronizeWriteThenRead(write_mutex, write_callback, null, read_mutex, read_callback, null);
    }

    public static <T> T synchronizeWriteThenRead(Object mutex, Callback<?> write_callback, Callback<T> read_callback, Callback<?> release_callback)
    {
        return synchronizeWriteThenRead(mutex, write_callback, release_callback, mutex, read_callback, release_callback);
    }

    public static <T> T synchronizeWriteThenRead(Object write_mutex, Callback<?> write_callback, Callback<?> write_release_callback, Object read_mutex, Callback<T> read_callback, Callback<?> read_release_callback)
    {
        return sync_utils_instance.synchronizeWriteThenReadImpl(getNewSyncWrapper(write_mutex, write_release_callback), write_callback, getNewSyncWrapper(read_mutex, read_release_callback), read_callback);
    }

    public static <T> T synchronizeWriteThenRead(SyncWrapper sync, Callback<?> write_callback, Callback<T> read_callback)
    {
        return synchronizeWriteThenRead(sync, write_callback, sync, read_callback);
    }

    public static <T> T synchronizeWriteThenRead(SyncWrapper write_sync, Callback<?> write_callback, SyncWrapper read_sync, Callback<T> read_callback)
    {
        return sync_utils_instance.synchronizeWriteThenReadImpl(write_sync, write_callback, read_sync, read_callback);
    }

    public static <T> T synchronizeConditionalWrite(Object mutex, Condition write_condition, Callback<T> write_callback)
    {
        return synchronizeConditionalWrite(mutex, write_condition, write_callback, null);
    }

    public static <T> T synchronizeConditionalWrite(Object mutex, Condition write_condition, Callback<T> write_callback, Callback<?> release_callback)
    {
        return sync_utils_instance.synchronizeConditionalWriteImpl(getNewSyncWrapper(mutex, release_callback), write_condition, write_callback);
    }

    public static <T> T synchronizeConditionalWrite(SyncWrapper sync, Condition write_condition, Callback<T> write_callback)
    {
        return sync_utils_instance.synchronizeConditionalWriteImpl(sync, write_condition, write_callback);
    }

    public static <T> T synchronizeConditionalWriteThenRead(Object mutex, Condition write_condition, Callback<?> write_callback, Callback<T> read_callback)
    {
        return synchronizeConditionalWriteThenRead(mutex, write_condition, write_callback, null, mutex, read_callback, null);
    }

    public static <T> T synchronizeConditionalWriteThenRead(Object write_mutex, Condition write_condition, Callback<?> write_callback, Object read_mutex, Callback<T> read_callback)
    {
        return synchronizeConditionalWriteThenRead(write_mutex, write_condition, write_callback, null, read_mutex, read_callback, null);
    }

    public static <T> T synchronizeConditionalWriteThenRead(Object mutex, Condition write_condition, Callback<?> write_callback, Callback<T> read_callback, Callback<?> release_callback)
    {
        return synchronizeConditionalWriteThenRead(mutex, write_condition, write_callback, release_callback, mutex, read_callback, release_callback);
    }

    public static <T> T synchronizeConditionalWriteThenRead(Object write_mutex, Condition write_condition, Callback<?> write_callback, Object read_mutex, Callback<T> read_callback, Callback<?> release_callback)
    {
        return synchronizeConditionalWriteThenRead(write_mutex, write_condition, write_callback, release_callback, read_mutex, read_callback, release_callback);
    }

    public static <T> T synchronizeConditionalWriteThenRead(Object write_mutex, Condition write_condition, Callback<?> write_callback, Callback<?> write_release_callback, Object read_mutex, Callback<T> read_callback, Callback<?> read_release_callback)
    {
        return sync_utils_instance.synchronizeConditionalWriteThenReadImpl(getNewSyncWrapper(write_mutex, write_release_callback), write_condition, write_callback, getNewSyncWrapper(read_mutex, read_release_callback), read_callback);
    }

    public static <T> T synchronizeConditionalWriteThenRead(SyncWrapper sync, Condition write_condition, Callback<?> write_callback, Callback<T> read_callback)
    {
        return synchronizeConditionalWriteThenRead(sync, write_condition, write_callback, sync, read_callback);
    }

    public static <T> T synchronizeConditionalWriteThenRead(SyncWrapper write_sync, Condition write_condition, Callback<?> write_callback, SyncWrapper read_sync, Callback<T> read_callback)
    {
        return sync_utils_instance.synchronizeConditionalWriteThenReadImpl(write_sync, write_condition, write_callback, read_sync, read_callback);
    }

    public static void dumpDebugReadLocks(StringBuffer buffer) {
        sync_utils_instance.dumpDebugReadLocksImpl(buffer);
    }

    abstract SyncWrapper getNewSyncWrapperImpl();

    abstract Object createMutexImpl(Object suggested_mutex);

    abstract SyncCondition getSyncConditionImpl(Object suggested_mutex);

    abstract <T> T synchronizeWriteImpl(SyncWrapper mutex, Callback<T> callback);

    abstract <T> T synchronizeReadImpl(SyncWrapper mutex, Callback<T> callback);

    abstract <T> T synchronizeWriteThenReadImpl(SyncWrapper write_mutex, Callback<?> write_callback, SyncWrapper read_mutex, Callback<T> read_callback);

    abstract <T> T synchronizeConditionalWriteThenReadImpl(SyncWrapper write_mutex, Condition write_condition, Callback<?> write_callback, SyncWrapper read_mutex, Callback<T> read_callback);

    abstract <T> T synchronizeConditionalWriteImpl(SyncWrapper mutex, Condition write_condition, Callback<T> write_callback);

    abstract void dumpDebugReadLocksImpl(StringBuffer buffer);
}
