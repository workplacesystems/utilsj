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
import java.util.Iterator;

import com.workplacesystems.utilsj.collections.FilterableArrayList;
import junit.framework.TestCase;

public class SelectorTest extends TestCase
{
    public void testSelector()
    {
        // some lists to iterate
        FilterableArrayList empty_list = new FilterableArrayList();
        FilterableArrayList list = new FilterableArrayList(Arrays.asList(
                new Integer[]{new Integer(1), new Integer(2)})
        );

        // ensure nothing selected from an empty list
        Collection selected = (Collection) new Selector().iterate(empty_list);
        assertTrue(selected.isEmpty());
        
        // ensure everything selected from a populated list
        selected = (Collection) new Selector().iterate(list);
        
        assertEquals(list.size(), selected.size());
        for (Iterator elements = list.iterator(); elements.hasNext();)
            assertTrue(selected.contains(elements.next()));

        // ensure cumulative selection doesn't damage original value
        selected = new ArrayList(Arrays.asList(new Integer[]{new Integer(0)}));
        new Selector(selected).iterate(list);

        assertEquals(list.size()+1, selected.size());
        assertTrue(selected.contains(new Integer(0)));
        for (Iterator elements = list.iterator(); elements.hasNext();)
            assertTrue(selected.contains(elements.next()));
    }
}
