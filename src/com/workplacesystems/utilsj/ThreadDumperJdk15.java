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
import com.workplacesystems.utilsj.collections.SyncUtils;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Map;

/**
 *
 * @author Dave
 */
class ThreadDumperJdk15 extends ThreadDumper {

    ThreadDumperJdk15() {}

    @Override
    String dumpThreads0() {
        try {
            ThreadMXBean bean = ManagementFactory.getThreadMXBean();

            StringBuffer buffer = new StringBuffer();

            outputDeadlockedThreads(bean, buffer);

            ThreadInfo[] infos = getThreadInfos(bean);
            Map<Long, ExtraLockInfo> extraLockInfos = SyncUtils.getExtraLockInfos();

            buffer.append("Thread Dump\n");

            for (int i = 0; i<infos.length; i++)
            {
                ThreadInfo info = infos[i];

                long tid = info.getThreadId();
                ExtraLockInfo exLockInfo = extraLockInfos != null ? extraLockInfos.get(tid) : null;

                buffer.append("\"");
                buffer.append(info.getThreadName());
                buffer.append("\" tid=");
                buffer.append(tid);
                buffer.append(" State: ");
                buffer.append(info.getThreadState().toString());
                buffer.append("\n");

                StackTraceElement[] stack_trace = info.getStackTrace();
                int monitor_pointer = 0;
                for (int t = 0; t<stack_trace.length; t++)
                {
                    buffer.append("        at ");
                    buffer.append(stack_trace[t].toString());
                    buffer.append("\n");

                    if (t == 0)
                        outputWaitingInfo(info, exLockInfo, buffer);

                    monitor_pointer = outputMonitors(info, t, monitor_pointer, buffer);
                }
                buffer.append("\n");

                outputLockedSynchronizers(info, exLockInfo, buffer);
            }
            return buffer.toString();
        }
        catch (Exception e) {
            new UtilsjException(e);
            return "Thread dump not available.";
        }
    }

    void outputDeadlockedThreads(ThreadMXBean bean, StringBuffer buffer) {
        // Not available for Jdk1.5
    }

    ThreadInfo[] getThreadInfos(ThreadMXBean bean) {
        long[] threads = bean.getAllThreadIds();
        return bean.getThreadInfo(threads, Integer.MAX_VALUE);
    }

    void outputWaitingInfo(ThreadInfo info, ExtraLockInfo exLockInfo, StringBuffer buffer) {
        // Not available for Jdk1.5
    }

    int outputMonitors(ThreadInfo info, int stack_depth, int monitor_pointer, StringBuffer buffer) {
        // Not available for Jdk1.5
        return 0;
    }

    void outputLockedSynchronizers(ThreadInfo info, ExtraLockInfo exLockInfo, StringBuffer buffer) {
        // Not available for Jdk1.5
    }
}
