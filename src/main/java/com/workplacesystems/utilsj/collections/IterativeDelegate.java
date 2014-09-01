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

/**
 *
 * @author Dave
 */
public abstract class IterativeDelegate<T>
{
    private IterativeCallback<T,?> ic;
    private IterativeDelegate<T> next = null;

    void setNextDelegation(IterativeDelegate<T> next)
    {
        if (this.next == null)
            this.next = next;
        else
            this.next.setNextDelegation(next);
    }

    void setIterativeCallback(IterativeCallback<T,?> ic)
    {
        this.ic = ic;
    }

    protected abstract void delegate(T obj);

    protected final void iterateObject(T o)
    {
        if (next == null)
            ic.iterateObject(o);
        else
            next.delegate(o);
        
    }
}
