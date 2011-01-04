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

package com.workplacesystems.utilsj.collections.helpers;

import com.workplacesystems.utilsj.UtilsjException;
import com.workplacesystems.utilsj.collections.FilterableCollection;
import com.workplacesystems.utilsj.collections.IterativeCallback;

/**
 *
 * @author dave
 */
public class HasLessThan<T> extends IterativeCallback<T, HasLessThan<T>>
{
    private int total;
    private int count = 0;

    private HasLessThan() {}

    public HasLessThan(int total) {
        this.total = total;
    }

    @Override
    public HasLessThan<T> iterate(final FilterableCollection<? extends T> c)
    {
        if (total <= 0)
            return this;

        super.iterate(c);

        return new HasLessThan<T>(total - count);
    }

    @Override
    public void nextObject(T obj) {
        count++;
        if (count >= total)
            _break();
    }

    /**
     * Returns whether the total has been reached.
     * @throws QueujException if the iterative callback has not been used yet
     */
    public boolean hasLess() throws UtilsjException
    {
        if (!hasBeenUsed())
            throw new UtilsjException("HasLessThan.hasLess() called without having been used");
        return count < total;
    }
}