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
import java.util.Arrays;
import java.util.Collection;

import com.workplacesystems.utilsj.collections.FilterableArrayList;
import junit.framework.TestCase;

public class CollectorTest extends TestCase
{
    public void testCollector()
    {
        // some lists to iterate
        FilterableArrayList empty_list = new FilterableArrayList();
        FilterableArrayList list = new FilterableArrayList(Arrays.asList(
                new Integer[]{new Integer(1), new Integer(2)})
        );

        // ensure nothing collected from an empty list
        Collection collected = (Collection) new Collector(){
            @Override
            public Object result(Object obj)
            {
                return new Integer(((Integer)obj).intValue() + 1);
            }}.iterate(empty_list);
        assertTrue(collected.isEmpty());
        
        // ensure everything collected from a populated list
        collected = (Collection) new Collector(){
            @Override
            public Object result(Object obj)
            {
                return new Integer(((Integer)obj).intValue() + 1);
            }
        }.iterate(list);

        assertEquals(list.size(), collected.size());
        assertTrue(collected.contains(new Integer(2)));
        assertTrue(collected.contains(new Integer(3)));

        // ensure cumulative collection doesn't damage original value
        collected = new ArrayList(Arrays.asList(new Integer[]{new Integer(0)}));
        new Collector(collected){
            @Override
            public Object result(Object obj)
            {
                return new Integer(((Integer)obj).intValue() + 1);
            }
        }.iterate(list);

        assertEquals(list.size()+1, collected.size());
        assertTrue(collected.contains(new Integer(0)));
        assertTrue(collected.contains(new Integer(2)));
        assertTrue(collected.contains(new Integer(3)));
    }
}
