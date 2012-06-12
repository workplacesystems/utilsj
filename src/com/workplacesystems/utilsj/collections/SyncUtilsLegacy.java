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

/**
 *
 * @author dave
 */
abstract class SyncUtilsLegacy extends SyncUtils
{
    /**
     * Creates a new instance of SyncUtilsLegacy
     */
    SyncUtilsLegacy()
    {
    }

    class SyncConditionLegacy implements SyncCondition
    {
        private final Object mutex;

        private SyncConditionLegacy(Object mutex)
        {
            this.mutex = mutex;
        }

        @SuppressWarnings("WaitWhileNotSynced")
        public void await() throws InterruptedException
        {
            mutex.wait();
        }

        @SuppressWarnings("NotifyWhileNotSynced")
        public void signal()
        {
            mutex.notify();
        }

        @SuppressWarnings("NotifyWhileNotSynced")
        public void signalAll()
        {
            mutex.notifyAll();
        }
    }

    @Override
    void dumpDebugReadLocksImpl(StringBuffer buffer) {}

    @Override
    SyncCondition getSyncConditionImpl(Object suggested_mutex)
    {
        return new SyncConditionLegacy(suggested_mutex);
    }

    @Override
    <T> T synchronizeWriteImpl(SyncWrapper mutex, Callback<T> callback)
    {
        synchronized (mutex.getMutex(0))
        {
            return callback.action();
        }
    }

    @Override
    <T> T synchronizeReadImpl(SyncWrapper mutex, Callback<T> callback)
    {
        synchronized (mutex.getMutex(0))
        {
            return callback.action();
        }
    }

    @Override
    <T> T synchronizeWriteThenReadImpl(SyncWrapper write_mutex, Callback<?> write_callback, SyncWrapper read_mutex, Callback<T> read_callback)
    {
        synchronized (read_mutex.getMutex(0))
        {
            synchronized (write_mutex.getMutex(0))
            {
                write_callback.action();
            }
            return read_callback.action();
        }
    }

    @Override
    <T> T synchronizeConditionalWriteImpl(SyncWrapper mutex, Condition write_condition, Callback<T> write_callback)
    {
        synchronized (mutex.getMutex(0))
        {
            if (write_condition.isTrue(0))
                return write_callback.action();
            return null;
        }
    }

    @Override
    <T> T synchronizeConditionalWriteThenReadImpl(SyncWrapper write_mutex, Condition write_condition, Callback<?> write_callback, SyncWrapper read_mutex, Callback<T> read_callback)
    {
        synchronized (read_mutex.getMutex(0))
        {
            synchronized (write_mutex.getMutex(0))
            {
                if (write_condition.isTrue(0))
                    write_callback.action();
            }
            return read_callback.action();
        }
    }
}
