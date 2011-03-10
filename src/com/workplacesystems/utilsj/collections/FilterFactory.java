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

import java.util.HashSet;
import java.util.Set;

public final class FilterFactory
{
    private static final Filter<?> ACCEPT_ALL = new Filter<Object>()
    { 
        public boolean isValid(Object o) 
        { 
            return true; 
        }
    };
    
    private static final Filter<?> REJECT_ALL = new Filter<Object>()
    { 
        public boolean isValid(Object o) 
        { 
            return false; 
        }
    };
    
    
    private static final Filter<?> REJECT_NULL = new Filter<Object>()
    { 
        public boolean isValid(Object o) 
        { 
            return o != null; 
        }
    };

    private static final Filter<?> DISTINCT = new Filter<Object>()
    { 
        Set seen = new HashSet();

        public boolean isValid(Object o) 
        { 
            return seen.add(o);
        }
    };

    public static <T> Filter<T> getAcceptAll()
    {
        return (Filter<T>)ACCEPT_ALL;
    }
    
    public static <T> Filter<T> getAcceptAll(Class<T> subject_class)
    {
        return (Filter<T>)ACCEPT_ALL;
    }
    
    public static <T> Filter<T> getRejectAll()
    {
        return (Filter<T>)REJECT_ALL;
    }
    
    public static <T> Filter<T> getRejectNull()
    {
        return (Filter<T>)REJECT_NULL;
    }

    public static <T> Filter<T> getRejectNull(Class<T> subject_class)
    {
        return (Filter<T>)REJECT_NULL;
    }

    /** Filter which accepts distinct objects during its lifetime.  Any repeats will be rejected */
    public static <T> Filter<T> getDistinct()
    {
        return (Filter<T>)DISTINCT;
    }

    // enforce non-instantiability
    private FilterFactory() {}
}
