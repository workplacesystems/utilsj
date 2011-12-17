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

import static com.workplacesystems.utilsj.collections.helpers.CollectionUtilities.like;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.easymock.EasyMock;

public class CollectionUtilitiesTest extends TestCase
{
    public void testReverse()
    {
        Map source = new HashMap();
        source.put("1", "a");
        source.put("2", "b");
        source.put("3", "c");
        source.put("4", "d");
        Map target = new HashMap();
        CollectionUtilities.reverse(source, target);
        
        assertEquals(4, target.size());
        assertEquals("1", target.get("a"));
        assertEquals("2", target.get("b"));
        assertEquals("3", target.get("c"));
        assertEquals("4", target.get("d"));
    }
    
    public void testToList()
    {
        Map source = new HashMap();
        source.put("1", "a");
        source.put("2", "b");
        source.put("3", "c");
        source.put("4", "d");
        
        List values = CollectionUtilities.toValueList(source);
        assertNotNull(values);
        Collections.sort(values);
        assertEquals(4, values.size());
        assertEquals("a", values.get(0));
        assertEquals("b", values.get(1));
        assertEquals("c", values.get(2));
        assertEquals("d", values.get(3));
    }
    
    public void testLike()
    {
        Collection<String> result = null;
        
        Comparator<String> leq = new Comparator<String>(){
            public int compare(String o1, String o2) { return 0; }
        };
        
        try
        {
            // known concrete types
            result = (ArrayList<String>) like(new ArrayList<String>());
            result = (LinkedList<String>) like(new LinkedList<String>());
            result = (TreeSet<String>) like(new TreeSet<String>(leq));
            assertEquals(leq, ((TreeSet)result).comparator());
            result = (LinkedHashSet<String>) like(new LinkedHashSet<String>());
            result = (HashSet<String>) like(new HashSet<String>());
            result = (Stack<String>) like(new Stack<String>());
            result = (PriorityQueue<String>) like(new PriorityQueue<String>(10,leq));
            assertEquals(leq, ((PriorityQueue)result).comparator());
            
            // unknown concrete types which conform to known abstractions
            result = (Queue<String>) like(EasyMock.createMock(Queue.class));
            
            SortedSet s = EasyMock.createMock(SortedSet.class);
            EasyMock.expect(s.comparator()).andReturn(leq);
            EasyMock.replay(s);
            result = (SortedSet<String>) like(s);
            
            result = (Set<String>) like(EasyMock.createMock(Set.class));
            result = (List<String>) like(EasyMock.createMock(List.class));
        }
        catch (ClassCastException e) {fail();}
    }
    
}
