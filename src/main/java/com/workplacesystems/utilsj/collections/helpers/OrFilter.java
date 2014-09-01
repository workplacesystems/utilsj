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

import java.util.ArrayList;
import java.util.List;

import com.workplacesystems.utilsj.collections.Filter;

/**
 * Logical 'or' of a sequence of filters.  Short-circuit boolean evaluation is used as for Java '||' operation.
 * That is, first success will return true overall without evaluating the rest of the filters.
 * <p>
 * Boundary case: the 'or' of no filters is false (consistent with normal predicate logic rules for quantifying over the empty set)
 *
 * @author henningt
 */
public class OrFilter<E> implements Filter<E>
{
    /** List of 'or' conditions to short-circuit evaluate */
    private final List<Filter<? super E>> conditions = new ArrayList();

    public OrFilter(final Filter<? super E>... filters)
    {
        for (Filter<? super E> filter : filters)
            conditions.add(filter);
    }

    public OrFilter(final List<Filter<? super E>> filters) 
    {
        conditions.addAll(filters);
    }

    public boolean isValid(E obj)
    {
        for (Filter<? super E> condition : conditions)
            if (condition.isValid(obj)) return true;
        return false;
    }
}