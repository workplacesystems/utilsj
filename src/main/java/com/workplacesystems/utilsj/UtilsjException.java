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

import com.workplacesystems.utilsj.collections.FilterableArrayList;
import com.workplacesystems.utilsj.collections.FilterableCollection;
import com.workplacesystems.utilsj.collections.IterativeCallback;
import com.workplacesystems.utilsj.collections.decorators.SynchronizedFilterableCollection;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author dave
 */
public class UtilsjException extends RuntimeException
{
    private final static Log log = LogFactory.getLog(UtilsjException.class);

    private final static String new_line = System.getProperty("line.separator");

    private final static FilterableCollection<Handler> handlers = SynchronizedFilterableCollection.decorate(new FilterableArrayList<Handler>());

    /**
     *
     */
    public UtilsjException()
    {
        this(null, null);
    }

    /**
     *
     * @param e
     */
    public UtilsjException(Exception e)
    {
        this(e.getMessage(), e);
    }

    /**
     *
     * @param response
     */
    public UtilsjException(String response)
    {
        this(response, null);
    }

    /**
     *
     * @param response
     * @param e
     */
    public UtilsjException(String response, Exception e)
    {
        super(response, e);

        String message = getMessage();

        Throwable traced_exception = e;
        while (traced_exception instanceof InvocationTargetException)
        {
            InvocationTargetException ite = (InvocationTargetException) traced_exception;
            traced_exception = ite.getTargetException();
        }
        if (traced_exception instanceof UtilsjException)
            return;

        StringWriter sw = new StringWriter();
        PrintWriter pr = new PrintWriter(sw);
        printStackTrace(pr);
        message += new_line + new_line + sw.toString();

        log.fatal(message);

        (new IterativeCallback<Handler, Void>() {
            @Override
            protected void nextObject(Handler h)
            {
                h.handle(UtilsjException.this);
            }
        }).iterate(handlers);
    }

    public interface Handler {
        public void handle(UtilsjException ue);
    }

    public static void addHandler(Handler h) {
        handlers.add(h);
    }

    public static void removeHandler(Handler h) {
        handlers.remove(h);
    }
}
