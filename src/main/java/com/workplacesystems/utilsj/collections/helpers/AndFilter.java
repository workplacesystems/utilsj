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
 * Logical 'and' of a sequence of filters.  Short-circuit boolean evaluation is used as for Java '&amp;&amp;' operation.
 * That is, first fail will return false overall without evaluating the rest of the filters.
 * <p>
 * Boundary case: the 'and' of no filters is true (consistent with normal predicate logic rules for quantifying over the empty set)
 *
 * @author henningt
 */
public final class AndFilter<E> implements Filter<E> {
    
    /** List of 'and' conditions to short-circuit evaluate */
    private final List<Filter<? super E>> conditions = new ArrayList();

    public AndFilter(final Filter<? super E>... filters)
    {
        for (Filter filter : filters)
            conditions.add(filter);
    }
    
    public AndFilter(final List<Filter<? super E>> filters) 
    {
        conditions.addAll(filters);
    }

    public boolean isValid(E obj) 
    {
        for (Filter<? super E> condition : conditions)
            if (!condition.isValid(obj)) return false;
        return true;
    }
}