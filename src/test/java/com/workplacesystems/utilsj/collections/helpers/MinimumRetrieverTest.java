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

import java.util.Arrays;
import java.util.Comparator;

import junit.framework.TestCase;
import com.workplacesystems.utilsj.collections.FilterableArrayList;

public class MinimumRetrieverTest extends TestCase
{
    public void testMinimumRetriever()
    {
        // some lists to iterate
        FilterableArrayList empty_list = new FilterableArrayList();
        FilterableArrayList singleton = new FilterableArrayList(
                Arrays.asList(new Integer[]{ new Integer(0) }));
        
        FilterableArrayList list = new FilterableArrayList(Arrays.asList(
                new Integer[]{
                        new Integer(1), 
                        new Integer(2),
                        new Integer(3),
                        new Integer(4),
                        new Integer(5)
                        })
        );

        // ensure nothing detected in an empty list
        Integer selected = (Integer) new MinimumRetriever().iterate(empty_list);
        assertNull(selected);
        
        // ensure that the callback is valid for a singleton.
        selected = (Integer) new MinimumRetriever().iterate(singleton);
        assertEquals(new Integer(0), selected);       
        
        // ensure something detected in a populated list
        selected = (Integer) new MinimumRetriever().iterate(list);
        assertEquals(new Integer(1), selected);
        
        // reverse Comparator to verify that the Comparator aspect is valid
        selected = (Integer) new MinimumRetriever(new Comparator() {
            public int compare(Object obj1, Object obj2)
            {
                return -((Comparable)obj1).compareTo(obj2);
            }
            
        }).iterate(list);
        assertEquals(new Integer(5), selected);        
    }
}
