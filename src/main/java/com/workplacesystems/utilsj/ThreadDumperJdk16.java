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

package com.workplacesystems.utilsj;

import com.workplacesystems.utilsj.collections.ExtraLockInfo;
import java.lang.management.LockInfo;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class ThreadDumperJdk16 extends ThreadDumperJdk15 {

    private final static Log log = LogFactory.getLog(ThreadDumperJdk16.class);

    ThreadDumperJdk16() {}

    @Override
    void outputDeadlockedThreads(ThreadMXBean bean, StringBuffer buffer) {
        long[] deadlocked = bean.findDeadlockedThreads();

        if (deadlocked!=null && deadlocked.length>0)
        {
            buffer.append("Deadlock detected. Threads: ");
            for (int i=0; i<deadlocked.length; i++)
            {
                if (i!=0)
                    buffer.append(", ");
                buffer.append(deadlocked[i]);
            }
            buffer.append("\n");
        }
    }

    @Override
    ThreadInfo[] getThreadInfos(ThreadMXBean bean) {
        return bean.dumpAllThreads(bean.isObjectMonitorUsageSupported(), bean.isSynchronizerUsageSupported());
    }

    @Override
    void outputWaitingInfo(ThreadInfo info, ExtraLockInfo exLockInfo, StringBuffer buffer) {
        final LockInfo lock_info = info.getLockInfo();
        if (lock_info != null)
        {
            formatLock(lock_info, "waiting on", buffer);
            if (exLockInfo != null)
            {
                if (exLockInfo.isWaitingFor(ExtraLockInfo.WaitingFor.WRITE, lock_info))
                    buffer.append(" for write");
                if (exLockInfo.isWaitingFor(ExtraLockInfo.WaitingFor.READ, lock_info))
                    buffer.append(" for read");
            }
            buffer.append(".");
            long lock_owner = info.getLockOwnerId();
            if (lock_owner!=-1)
            {
                buffer.append(" Locked by \"");
                buffer.append(info.getLockOwnerName());
                buffer.append("\" tid=");
                buffer.append(lock_owner);
            }
            buffer.append("\n");
        }
    }

    @Override
    int outputMonitors(ThreadInfo info, int stack_depth, int monitor_pointer, StringBuffer buffer) {
        MonitorInfo[] locked_monitors = info.getLockedMonitors();
        boolean check_next_monitor;
        do
        {
            check_next_monitor = false;
            if (monitor_pointer < locked_monitors.length)
            {
                MonitorInfo monitor_info = locked_monitors[monitor_pointer];
                if (monitor_info.getLockedStackDepth() == stack_depth)
                {
                    formatLock(monitor_info, "locked", buffer);
                    buffer.append("\n");
                    monitor_pointer++;
                    check_next_monitor = true;
                }
            }
        } while (check_next_monitor);

        return monitor_pointer;
    }

    @Override
    void outputLockedSynchronizers(ThreadInfo info, ExtraLockInfo exLockInfo, StringBuffer buffer) {
        List<LockInfo> locked_synchronizers = Arrays.asList(info.getLockedSynchronizers());
        if (!locked_synchronizers.isEmpty() || (exLockInfo != null && exLockInfo.hasHeldLocks()))
        {
            buffer.append("   Locked Synchronizers:\n");
            ArrayList<String> reportedSyncs = new ArrayList<String>();
            for (LockInfo lockInfo : locked_synchronizers)
            {
                reportedSyncs.add(lockInfo.toString());
                formatLock(lockInfo, null, buffer);
                if (exLockInfo != null)
                {
                    if (exLockInfo.heldWritesContains(lockInfo))
                        buffer.append(" for write");
                    if (exLockInfo.heldReadsContains(lockInfo))
                        buffer.append(" for read");
                }
                buffer.append("\n");
            }

            if (exLockInfo != null)
            {
                for (LockInfo writeLock : exLockInfo.getHeldWriteLocks())
                {
                    if (!reportedSyncs.contains(writeLock.toString()))
                    {
                        formatLock(writeLock, null, buffer);
                        buffer.append(" for write\n");
                    }
                }
                for (LockInfo readLock : exLockInfo.getHeldReadLocks())
                {
                    if (!reportedSyncs.contains(readLock.toString()))
                    {
                        formatLock(readLock, null, buffer);
                        buffer.append(" for read\n");
                    }
                }
            }
            buffer.append("\n");
        }
    }

    private void formatLock(LockInfo lockInfo, String message, StringBuffer buffer)
    {
        buffer.append("        - ");
        if (message != null && !message.equals(""))
        {
            buffer.append(message);
            buffer.append(" ");
        }
        buffer.append("<");
        buffer.append(Integer.toHexString(lockInfo.getIdentityHashCode()));
        buffer.append("> (a ");
        buffer.append(lockInfo.getClassName());
        buffer.append(")");
    }
}
