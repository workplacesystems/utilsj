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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Dave
 */
public class ThreadDumper {

    private final static ThreadDumper thread_dump_instance;
    private final static Log log = LogFactory.getLog(ThreadDumper.class);

    static
    {
        ThreadDumper local_thread_dump_instance;

        boolean java16 = false;
        boolean java15 = false;
        try {
            Class.forName("java.util.ArrayDeque");
            // We have JDK 1.6 at least if we're here
            java16 = true;
        } catch (ClassNotFoundException cnfe) {
            try {
                // swallow as we've hit the max class version that we have
                Class.forName("java.util.AbstractQueue");
                // We have JDK 1.5 at least if we're here
                java15 = true;
            } catch (ClassNotFoundException cnfe2) {
                // swallow as we've hit the max class version that we have
            }
        }

        if (java16) {
            try
            {
                Class<?> td_jdk16_class = Class.forName("com.workplacesystems.utilsj.ThreadDumperJdk16");
                local_thread_dump_instance = (ThreadDumper)td_jdk16_class.newInstance();
            }
            catch (Exception e)
            {
                new UtilsjException("JDK 1.6 was detected but ThreadDumperJdk16 class cannot be found.");
                local_thread_dump_instance = new ThreadDumper();
            }
        }
        else if (java15) {
            try
            {
                Class<?> td_jdk15_class = Class.forName("com.workplacesystems.utilsj.ThreadDumperJdk15");
                local_thread_dump_instance = (ThreadDumper)td_jdk15_class.newInstance();
            }
            catch (Exception e)
            {
                new UtilsjException("JDK 1.5 was detected but ThreadDumperJdk15 class cannot be found.");
                local_thread_dump_instance = new ThreadDumper();
            }
        }
        else
            local_thread_dump_instance = new ThreadDumper();

        thread_dump_instance = local_thread_dump_instance;
    }

    ThreadDumper() {}

    public static void dumpThreads() {
        log.info(thread_dump_instance.dumpThreads0());
    }

    String dumpThreads0() {
        return "Thread dump not available on this version of the JVM.";
    }
}
