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

import java.util.Collection;
import java.util.Iterator;

import com.workplacesystems.utilsj.collections.FilterableArrayList;
import junit.framework.TestCase;

public class ComparisonFilterFactoryTest extends TestCase
{
    public void testSameAsFilter() 
    {
        FilterableArrayList empty_list = new FilterableArrayList();
        FilterableArrayList list = new FilterableArrayList();
        list.add("A");
        list.add("B");

        // ensure nothing selected from an empty list
        Collection selected = empty_list.filteredCollection(ComparisonFilterFactory.createSameAsFilter("A"));
        assertTrue(selected.isEmpty());
        
        // ensure that "A" is selected
        selected = list.filteredCollection(ComparisonFilterFactory.createSameAsFilter("A"));
        assertFalse(selected.isEmpty());
        assertEquals(1, selected.size());
        assertEquals("A", selected.iterator().next());

        // ensure that a new Object with a different referenece is NOT selected.
        selected = list.filteredCollection(ComparisonFilterFactory.createSameAsFilter(new String("A")));
        assertTrue(selected.isEmpty());
    }

    public void testEqualToFilter()
    {
        FilterableArrayList empty_list = new FilterableArrayList();
        FilterableArrayList list = new FilterableArrayList();
        list.add("A");
        list.add("B");

        // ensure nothing selected from an empty list
        Collection selected = empty_list.filteredCollection(ComparisonFilterFactory.createEqualToFilter("A"));
        assertTrue(selected.isEmpty());
         
        // ensure that "A" is selected (Same reference)
        selected = list.filteredCollection(ComparisonFilterFactory.createEqualToFilter("A"));
        Iterator itr = selected.iterator();
        assertFalse(selected.isEmpty());
        assertEquals(1, selected.size());
        assertEquals("A", itr.next());

        // ensure that a new Object with a different reference is selected.
        selected = list.filteredCollection(ComparisonFilterFactory.createEqualToFilter(new String("A")));
        itr = selected.iterator();
        assertFalse(selected.isEmpty());
        assertEquals(1, selected.size());
        assertEquals("A", itr.next());
    }
    
    public void testGreaterThanFilter()
    {
        // some lists to iterate
        FilterableArrayList empty_list = new FilterableArrayList();
        FilterableArrayList list = new FilterableArrayList();
        list.add("A");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("E");
        
        // ensure nothing selected from an empty list
        Collection selected = empty_list.filteredCollection(ComparisonFilterFactory.createGreaterThanFilter("C"));
        assertTrue(selected.isEmpty());
         
        // ensure that D, E are selected (String is a Comparable)
        selected = list.filteredCollection(ComparisonFilterFactory.createGreaterThanFilter("C"));
        Iterator itr = selected.iterator();

        assertFalse(selected.isEmpty());
        assertEquals(2, selected.size());
        assertEquals("D", itr.next());
        assertEquals("E", itr.next());

        // ensure that nothing after E are selected (String is a Comparable)
        selected = list.filteredCollection(ComparisonFilterFactory.createGreaterThanFilter("E"));
        assertTrue(selected.isEmpty());
    }

    public void testGreaterThanOrEqualToFilter()
    {
        // some lists to iterate
        FilterableArrayList empty_list = new FilterableArrayList();
        FilterableArrayList list = new FilterableArrayList();
        list.add("A");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("E");
        
        // ensure nothing selected from an empty list
        Collection selected = empty_list.filteredCollection(ComparisonFilterFactory.createGreaterThanOrEqualToFilter("C"));
        assertTrue(selected.isEmpty());
         
        // ensure that D, E are selected (String is a Comparable)
        selected = list.filteredCollection(ComparisonFilterFactory.createGreaterThanOrEqualToFilter("C"));
        Iterator itr = selected.iterator();
        assertFalse(selected.isEmpty());
        assertEquals(3, selected.size());
        assertEquals("C", itr.next());
        assertEquals("D", itr.next());
        assertEquals("E", itr.next());

        // ensure that nothing after E are selected (String is a Comparable)
        selected = list.filteredCollection(ComparisonFilterFactory.createGreaterThanOrEqualToFilter("E"));
        itr = selected.iterator();
        assertFalse(selected.isEmpty());
        assertEquals(1, selected.size());
        assertEquals("E", itr.next());
        
        // ensure that nothing after F are selected (String is a Comparable)
        selected = list.filteredCollection(ComparisonFilterFactory.createGreaterThanOrEqualToFilter("F"));
        assertTrue(selected.isEmpty());
    }
    
    public void testLessThanFilter()
    {
        // some lists to iterate
        FilterableArrayList empty_list = new FilterableArrayList();
        FilterableArrayList list = new FilterableArrayList();
        list.add("A");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("E");
        
        // ensure nothing selected from an empty list
        Collection selected = empty_list.filteredCollection(ComparisonFilterFactory.createLessThanFilter("C"));
        assertTrue(selected.isEmpty());
         
        // ensure that A, B are selected (String is a Comparable)
        selected = list.filteredCollection(ComparisonFilterFactory.createLessThanFilter("C"));
        Iterator itr = selected.iterator();

        assertFalse(selected.isEmpty());
        assertEquals(2, selected.size());
        assertEquals("A", itr.next());
        assertEquals("B", itr.next());

        // ensure that nothing before A are selected (String is a Comparable)
        selected = list.filteredCollection(ComparisonFilterFactory.createLessThanFilter("A"));
        assertTrue(selected.isEmpty());
    }

    public void testLessThanOrEqualToFilter()
    {
        // some lists to iterate
        FilterableArrayList empty_list = new FilterableArrayList();
        FilterableArrayList list = new FilterableArrayList();
        list.add("A");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("E");
        
        // ensure nothing selected from an empty list
        Collection selected = empty_list.filteredCollection(ComparisonFilterFactory.createLessThanOrEqualToFilter("C"));
        assertTrue(selected.isEmpty());
         
        // ensure that D, E are selected (String is a Comparable)
        selected = list.filteredCollection(ComparisonFilterFactory.createLessThanOrEqualToFilter("C"));
        Iterator itr = selected.iterator();
        assertFalse(selected.isEmpty());
        assertEquals(3, selected.size());
        assertEquals("A", itr.next());
        assertEquals("B", itr.next());
        assertEquals("C", itr.next());

        // ensure that nothing after ' ' are selected (String is a Comparable)
        selected = list.filteredCollection(ComparisonFilterFactory.createLessThanOrEqualToFilter(" "));
        assertTrue(selected.isEmpty());

        // ensure that nothing after A are selected (String is a Comparable)
        selected = list.filteredCollection(ComparisonFilterFactory.createLessThanOrEqualToFilter("A"));
        itr = selected.iterator();
        assertFalse(selected.isEmpty());
        assertEquals(1, selected.size());
        assertEquals("A", itr.next());
    }
}
