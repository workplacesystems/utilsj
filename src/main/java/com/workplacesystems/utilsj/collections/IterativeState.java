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

import java.util.Iterator;

/**
 *
 * @author Dave
 */
public class IterativeState<T,R>
{
    // Save iterator so that remove can be called
    Iterator<? extends T> i;

    boolean iterative_callback_used = false;
    int iterations = 0;

    R return_object = null;
    R amended_object = null;
    boolean do_break = false;
    
    IterativeState() {}

    public void copyTo(IterativeState<T,R> copy)
    {
        copy.return_object = this.return_object;
        copy.amended_object = this.amended_object;
        copy.do_break = this.do_break;
    }
}
