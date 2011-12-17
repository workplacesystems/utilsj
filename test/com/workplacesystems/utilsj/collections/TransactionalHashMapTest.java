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

/*
 * TransactionalHashMapTest.java
 * JUnit based test
 *
 * Created on 15 October 2003, 16:34
 */

package com.workplacesystems.utilsj.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author andy bell
 */
public class TransactionalHashMapTest extends TestCase {
    
    public TransactionalHashMapTest(java.lang.String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(TransactionalHashMapTest.class);
        return suite;
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    private TransactionalHashMap GetDefault(){
        TransactionalHashMap thm = new TransactionalHashMap();
        thm.put(new Integer(2), "C");
        thm.put(new Integer(1), "D");
        thm.put(new Integer(3), "A");
        thm.put(new Integer(4), "B");
        return thm;
    }
    
    public void testInstantiationEmpty() {
        TransactionalHashMap thm = new TransactionalHashMap();
        assertTrue(thm != null);
    }
    public void testPutValue() {
        TransactionalHashMap thm = new TransactionalHashMap();
        thm.put(new Integer(1), "D");
        assertEquals("D", thm.get(new Integer(1)));
    }
    
    private void checkIterations(Iterator i, ArrayList test_entries){
        while (i.hasNext())
        {
            Map.Entry entry = (Map.Entry) i.next();
            boolean found = false;
            for (int index=0 ; index<test_entries.size(); index++)
            {
                TestEntry test_entry = (TestEntry)test_entries.get(index);
                if (entry.getKey().equals(test_entry.key) && entry.getValue().equals(test_entry.value))
                {
                    found = true;
                    test_entries.remove(index);
                    break;
                }
            }
            if (!found)
                fail("Iterator contains extra entry");
        }
        if (test_entries.size() > 0)
            fail("Iterator is missing an entry");
    }
    /*
     *Test iterator returned by TransactionalHashMap
     */
    public void testTransactionalHashMap_entrySetIterator(){
        TransactionalHashMap thm = GetDefault();
        Set s = thm.entrySet();
        assertEquals(4, s.size());
        Iterator i = s.iterator();
        ArrayList test_entries = new ArrayList(4);
        test_entries.add(new TestEntry(new Integer(1), "D"));
        test_entries.add(new TestEntry(new Integer(2), "C"));
        test_entries.add(new TestEntry(new Integer(3), "A"));
        test_entries.add(new TestEntry(new Integer(4), "B"));
        checkIterations(i, test_entries);
        assertEquals(false, i.hasNext());
    }
    
    private transient boolean running;
    private transient boolean inner_running;
    
    public void testTransactional_put(){
        TransactionalHashMap thm = new TransactionalHashMap();
        thm.setAutoCommit(false);
        
        Integer one = new Integer(1);
        thm.put(one, "A");
        thm.commit();
        thm.remove(one);
        thm.put(one, "A");
        thm.commit();
        // Current thread should be able to replace the key as many times as it wants. i.e. Shouldn't throw ConcurrentModificationException
        thm.put(one, "A");
        thm.put(one, "A");
    }
    
    public void testTransactions_commit(){
        final TransactionalHashMap thm = GetDefault();
        thm.setAutoCommit(false);
        
        final Thread parentThread = Thread.currentThread();
        running = true;
        inner_running = true;
        final Thread thread = new Thread(){
            @Override
            public void run() {
                thm.put(new Integer(0), "E");
                thm.remove(new Integer(3));
                thm.remove(new Integer(4));
                thm.put(new Integer(3), "G");
                
                Set s = thm.entrySet();
                assertEquals(4, s.size());
                Iterator i = s.iterator();
                ArrayList test_entries = new ArrayList(4);
                test_entries.add(new TestEntry(new Integer(0), "E"));
                test_entries.add(new TestEntry(new Integer(1), "D"));
                test_entries.add(new TestEntry(new Integer(2), "C"));
                test_entries.add(new TestEntry(new Integer(3), "G"));
                checkIterations(i, test_entries);
                assertEquals(false, i.hasNext());
                
                running = false;
                parentThread.interrupt();
                
                while (inner_running) {
                    try {
                        sleep(10);
                    } catch (Exception e) {}
                }
                inner_running = true;
                thm.commit();
                running = false;
                parentThread.interrupt();
            }
        };
        
        thread.start();
        
        while (running) {
            try {
                Thread.sleep(10);
            } catch (Exception e) {}
        }
        running = true;
        
        try {
            thm.remove(new Integer(3));
            fail("should have caught ConcurrentModificationException");
        } catch (ConcurrentModificationException ignored) {}
        try {
            thm.remove(new Integer(4));
            fail("should have caught ConcurrentModificationException");
        } catch (ConcurrentModificationException ignored) {}
        try {
            thm.put(new Integer(3), "H");
            fail("should have caught ConcurrentModificationException");
        } catch (ConcurrentModificationException ignored) {}
        try {
            thm.put(new Integer(0), "I");
            fail("should have caught ConcurrentModificationException");
        } catch (ConcurrentModificationException ignored) {}
        
        Set s = thm.entrySet();
        assertEquals(4, s.size());
        Iterator i = s.iterator();
        ArrayList test_entries = new ArrayList(4);
        test_entries.add(new TestEntry(new Integer(1), "D"));
        test_entries.add(new TestEntry(new Integer(2), "C"));
        test_entries.add(new TestEntry(new Integer(3), "A"));
        test_entries.add(new TestEntry(new Integer(4), "B"));
        checkIterations(i, test_entries);
        assertEquals(false, i.hasNext());
        
        inner_running = false;
        thread.interrupt();
        while (running) {
            try {
                Thread.sleep(10);
            } catch (Exception e) {}
        }
        running = true;
        
        s = thm.entrySet();
        assertEquals(4, s.size());
        i = s.iterator();
        test_entries = new ArrayList(4);
        test_entries.add(new TestEntry(new Integer(1), "D"));
        test_entries.add(new TestEntry(new Integer(2), "C"));
        test_entries.add(new TestEntry(new Integer(3), "G"));
        test_entries.add(new TestEntry(new Integer(0), "E"));
        checkIterations(i, test_entries);
        assertEquals(false, i.hasNext());
        
    }
    
    public void testTransactions_rollback(){
        final TransactionalHashMap thm = GetDefault();
        thm.setAutoCommit(false);
        
        final Thread parentThread = Thread.currentThread();
        running = true;
        inner_running = true;
        final Thread thread = new Thread(){
            @Override
            public void run() {
                thm.put(new Integer(0), "E");
                thm.remove(new Integer(3));
                thm.remove(new Integer(4));
                thm.put(new Integer(3), "G");
                Set s = thm.entrySet();
                assertEquals(4, s.size());
                Iterator i = s.iterator();
                ArrayList test_entries = new ArrayList(4);
                test_entries.add(new TestEntry(new Integer(1), "D"));
                test_entries.add(new TestEntry(new Integer(2), "C"));
                test_entries.add(new TestEntry(new Integer(3), "G"));
                test_entries.add(new TestEntry(new Integer(0), "E"));
                checkIterations(i, test_entries);
                assertEquals(false, i.hasNext());
                
                running = false;
                parentThread.interrupt();
                
                while (inner_running) {
                    try {
                        sleep(10);
                    } catch (Exception e) {}
                }
                inner_running = true;
                thm.rollback();
                running = false;
                parentThread.interrupt();
            }
        };
        
        thread.start();
        
        while (running) {
            try {
                Thread.sleep(10);
            } catch (Exception e) {}
        }
        running = true;
        
        try {
            thm.remove(new Integer(3));
            fail("should have caught ConcurrentModificationException");
        } catch (ConcurrentModificationException ignored) {}
        try {
            thm.remove(new Integer(4));
            fail("should have caught ConcurrentModificationException");
        } catch (ConcurrentModificationException ignored) {}
        try {
            thm.put(new Integer(3), "H");
            fail("should have caught ConcurrentModificationException");
        } catch (ConcurrentModificationException ignored) {}
        try {
            thm.put(new Integer(0), "I");
            fail("should have caught ConcurrentModificationException");
        } catch (ConcurrentModificationException ignored) {}
        
        Set s = thm.entrySet();
        assertEquals(4, s.size());
        Iterator i = s.iterator();
        ArrayList test_entries = new ArrayList(4);
        test_entries.add(new TestEntry(new Integer(1), "D"));
        test_entries.add(new TestEntry(new Integer(2), "C"));
        test_entries.add(new TestEntry(new Integer(3), "A"));
        test_entries.add(new TestEntry(new Integer(4), "B"));
        checkIterations(i, test_entries);
        assertEquals(false, i.hasNext());
        
        
        inner_running = false;
        thread.interrupt();
        while (running) {
            try {
                Thread.sleep(10);
            } catch (Exception e) {}
        }
        running = true;
        
        s = thm.entrySet();
        assertEquals(4, s.size());
        i = s.iterator();
        test_entries = new ArrayList(4);
        test_entries.add(new TestEntry(new Integer(1), "D"));
        test_entries.add(new TestEntry(new Integer(2), "C"));
        test_entries.add(new TestEntry(new Integer(3), "A"));
        test_entries.add(new TestEntry(new Integer(4), "B"));
        checkIterations(i, test_entries);
        assertEquals(false, i.hasNext());
        
    }
    
    
    public void testTransactions_size(){
        final TransactionalHashMap thm = GetDefault();
        thm.setAutoCommit(false);
        
        final Thread parentThread = Thread.currentThread();
        running = true;
        inner_running = true;
        final Thread thread = new Thread(){
            @Override
            public void run() {
                assertEquals(4, thm.size());
                
                thm.put(new Integer(0), "E");
                thm.remove(new Integer(3));
                thm.remove(new Integer(4));
                thm.put(new Integer(3), "G");
                thm.put(new Integer(5), "I");
                thm.put(new Integer(6), "H");
                
                assertEquals(6, thm.size());
                
                running = false;
                parentThread.interrupt();
                
                while (inner_running) {
                    try {
                        sleep(10);
                    } catch (Exception e) {}
                }
                inner_running = true;
                thm.commit();
                running = false;
                parentThread.interrupt();
            }
        };
        
        thread.start();
        
        while (running) {
            try {
                Thread.sleep(10);
            } catch (Exception e) {}
        }
        running = true;
        
        assertEquals(4, thm.size());
        
        inner_running = false;
        thread.interrupt();
        while (running) {
            try {
                Thread.sleep(10);
            } catch (Exception e) {}
        }
        running = true;
        
        assertEquals(6, thm.size());
    }
    
    protected Map makeMap() {
        return new TransactionalHashMap();
    }
    
    /**
     * test size() method
     */
    public void testSize() {
        
        Map m = makeMap();
        
        assertEquals(0, m.size());
        
        LocalTestNode nodes[] = makeLocalNodes();
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k].getValue());
            assertEquals(k + 1, m.size());
        }
        
        int count = m.size();
        
        for (int k = 0; k < nodes.length; k++) {
            m.remove(nodes[k].getKey());
            
            --count;
            
            assertEquals(count, m.size());
            
            // failed remove should not affect size
            m.remove(nodes[k].getKey());
            assertEquals(count, m.size());
        }
    }
    
    /**
     * test IsEmpty() method
     */
    public void testIsEmpty() {
        
        Map m = makeMap();
        
        assertTrue(m.isEmpty());
        
        LocalTestNode nodes[] = makeLocalNodes();
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k].getValue());
            assertTrue(!m.isEmpty());
        }
        
        int count = m.size();
        
        for (int k = 0; k < nodes.length; k++) {
            m.remove(nodes[k].getKey());
            
            --count;
            
            if (count == 0) {
                assertTrue(m.isEmpty());
            } else {
                assertTrue(!m.isEmpty());
            }
            
            // failed remove should not affect emptiness
            m.remove(nodes[k].getKey());
            
            if (count == 0) {
                assertTrue(m.isEmpty());
            } else {
                assertTrue(!m.isEmpty());
            }
        }
    }
    
    /**
     * test containsKey() method
     */
    public void testContainsKey() {
        
        Map m = makeMap();
        
        assertTrue(!m.containsKey("foo"));
        
        LocalTestNode nodes[] = makeLocalNodes();
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
            assertTrue(m.containsKey(nodes[k].getKey()));
        }
        
        assertTrue(!m.containsKey(new Integer(-1)));
        
        assertTrue(!m.containsKey("foo"));

        for (int k = 0; k < nodes.length; k++) {
            m.remove(nodes[k].getKey());
            assertTrue(!m.containsKey(nodes[k].getKey()));
        }
    }
    
    /**
     * test containsValue() method
     */
    public void testContainsValue() {
        
        Map           m       = (TransactionalHashMap) makeMap();
        LocalTestNode nodes[] = makeLocalNodes();
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
            assertTrue(m.containsValue(nodes[k]));
        }
        
        for (int k = 0; k < nodes.length; k++) {
            m.remove(nodes[k].getKey());
            assertTrue(!m.containsValue(nodes[k]));
        }
    }
    
    /**
     * test get() method
     */
    public void testGet() {
        
        Map m = makeMap();
        
        assertNull(m.get("foo"));
        
        LocalTestNode nodes[] = makeLocalNodes();
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
            assertSame(m.get(nodes[k].getKey()), nodes[k]);
        }
        
        assertNull(m.get(new Integer(-1)));
        
        assertNull(m.get("foo"));

        for (int k = 0; k < nodes.length; k++) {
            assertNotNull(m.get(nodes[k].getKey()));
            m.remove(nodes[k].getKey());
            assertNull(m.get(nodes[k].getKey()));
        }
    }
    
    /**
     * test put() method
     */
    public void testPut() {
        
        Map m = makeMap();
        
        LocalTestNode[] nodes = makeLocalNodes();
        
        for (int k = 0; k < nodes.length; k++) {
            assertNull(m.put(nodes[k].getKey(), nodes[k].getValue()));
            
            assertNotNull(m.put(nodes[k].getKey(), "foo"));
                
            assertNull(m.put(new Integer(-((Integer)nodes[k].getKey()).intValue()-1), nodes[k].getValue()));
        }
    }
    
    /**
     * test remove() method
     */
    public void testRemove() {
        
        TransactionalHashMap m       = (TransactionalHashMap) makeMap();
        LocalTestNode    nodes[] = makeLocalNodes();
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }
        
        m.remove(null);
        
        m.remove(new Object());
        
        assertNull(m.remove(new Integer(-1)));
        
        m.remove("foo");
        
        for (int k = 0; k < nodes.length; k += 2) {
            Comparable key = nodes[k].getKey();
            
            assertNotNull(m.get(key));
            assertSame(nodes[k], m.remove(key));
            assertNull(m.remove(key));
            assertNull(m.get(key));
        }
        
        for (int k = 1; k < nodes.length; k += 2) {
            Comparable key = nodes[k].getKey();
            
            assertNotNull(m.get(key));
            assertSame(nodes[k], m.remove(key));
            assertNull(m.remove(key));
            assertNull(m.get(key));
        }
        
        assertTrue(m.isEmpty());
    }
    
    /**
     * Method testPutAll
     */
    public void testPutAll() {
        
        Map           m       = (TransactionalHashMap) makeMap();
        LocalTestNode nodes[] = makeLocalNodes();
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }
        
        assertEquals(nodes.length, m.size());
        
        m  = (TransactionalHashMap) makeMap();
        HashMap m1 = new HashMap();
        
        for (int k = 0; k < nodes.length; k++) {
            m1.put(nodes[k].getKey(), nodes[k].getValue());
        }
        
        m.putAll(m1);
        assertEquals(nodes.length, m.size());
        
        for (int k = 0; k < nodes.length; k++) {
            assertSame(nodes[k].getValue(), m.get(nodes[k].getKey()));
        }
    }
    
    /**
     * test clear() method
     */
    public void testClear() {
        
        Map           m       = (TransactionalHashMap) makeMap();
        LocalTestNode nodes[] = makeLocalNodes();
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k].getValue());
            assertTrue(!m.isEmpty());
        }
        
        assertTrue(!m.isEmpty());
        
        for (int k = 0; k < nodes.length; k++) {
            assertTrue(m.containsKey(nodes[k].getKey()));
            assertTrue(m.containsValue(nodes[k].getValue()));
        }
        
        m.clear();
        assertTrue(m.isEmpty());
        
        for (int k = 0; k < nodes.length; k++) {
            assertTrue(!m.containsKey(nodes[k].getKey()));
            assertTrue(!m.containsValue(nodes[k].getValue()));
        }
    }
    
    /**
     * test keySet() method
     */
    public void testKeySet() {
        
        testKeySet((TransactionalHashMap) makeMap());
        
        Map           m       = (TransactionalHashMap) makeMap();
        LocalTestNode nodes[] = makeLocalNodes();
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }
        
        testKeySet(m);
        
        m = (TransactionalHashMap) makeMap();
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }
        
        int count = m.size();
        
        for (Iterator iter = m.keySet().iterator(); iter.hasNext(); ) {
            iter.next();
            iter.remove();
            
            --count;
            
            assertEquals(count, m.size());
        }
        
        assertTrue(m.isEmpty());
        
        m = (TransactionalHashMap) makeMap();
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }
        
        Set s = m.keySet();
        
        assertFalse(s.remove(null));
        
        assertFalse(s.remove(new Object()));
        
        for (int k = 0; k < nodes.length; k++) {
            Comparable key = nodes[k].getKey();
            
            assertTrue(s.remove(key));
            assertTrue(!s.contains(key));
            assertTrue(!m.containsKey(key));
            assertTrue(!m.containsValue(nodes[k]));
        }
        
        assertTrue(m.isEmpty());
        
        m = (TransactionalHashMap) makeMap();
        
        Collection c1 = new LinkedList();
        Collection c2 = new LinkedList();
        
        c2.add(new Integer(-99));
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
            c1.add(nodes[k].getKey());
            c2.add(nodes[k].getKey());
        }
        
        assertTrue(m.keySet().containsAll(c1));
        assertTrue(!m.keySet().containsAll(c2));
        
        m  = (TransactionalHashMap) makeMap();
        c1 = new LinkedList();
        
        c1.add(new Integer(-55));
        
        try {
            m.keySet().addAll(c1);
            fail("should have caught exception of addAll()");
        } catch (UnsupportedOperationException ignored) {}
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
            c1.add(nodes[k].getKey());
        }
        
        assertTrue(!m.keySet().retainAll(c1));
        assertEquals(nodes.length, m.size());
        
        m  = (TransactionalHashMap) makeMap();
        c1 = new LinkedList();
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
            
            if (k % 2 == 1) {
                c1.add(nodes[k].getKey());
            }
        }
        
        assertTrue(m.keySet().retainAll(c1));
        assertEquals(nodes.length / 2, m.size());
        
        m  = (TransactionalHashMap) makeMap();
        c1 = new LinkedList();
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }
        
        assertTrue(m.keySet().retainAll(c1));
        assertEquals(0, m.size());
        
        m  = (TransactionalHashMap) makeMap();
        c1 = new LinkedList();
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }
        
        assertTrue(!m.keySet().removeAll(c1));
        assertEquals(nodes.length, m.size());
        
        m  = (TransactionalHashMap) makeMap();
        c1 = new LinkedList();
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
            
            if (k % 2 == 0) {
                c1.add(nodes[k].getKey());
            }
        }
        
        assertTrue(m.keySet().removeAll(c1));
        assertEquals(nodes.length / 2, m.size());
        
        m  = (TransactionalHashMap) makeMap();
        c1 = new LinkedList();
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
            c1.add(nodes[k].getKey());
        }
        
        assertTrue(m.keySet().removeAll(c1));
        assertEquals(0, m.size());
        
        m = (TransactionalHashMap) makeMap();
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }
        
        m.keySet().clear();
        assertEquals(0, m.size());
    }
    
    /**
     * test values() method
     */
    public void testValues() {
        
        testValues((TransactionalHashMap) makeMap());
        
        Map           m       = (TransactionalHashMap) makeMap();
        LocalTestNode nodes[] = makeLocalNodes();
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }
        
        testValues(m);
        
        m = (TransactionalHashMap) makeMap();
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }
        
        int count = m.size();
        
        for (Iterator iter = m.values().iterator(); iter.hasNext(); ) {
            iter.next();
            iter.remove();
            
            --count;
            
            assertEquals(count, m.size());
        }
        
        assertTrue(m.isEmpty());
        
        m = (TransactionalHashMap) makeMap();
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }
        
        count = m.size();
        
        Collection s = m.values();
        
        for (int k = 0; k < count; k++) {
            assertTrue(s.remove(nodes[k]));
            assertTrue(!s.contains(nodes[k]));
            assertTrue(!m.containsKey(nodes[k].getKey()));
            assertTrue(!m.containsValue(nodes[k]));
        }
        
        assertTrue(m.isEmpty());
        
        m = (TransactionalHashMap) makeMap();
        
        Collection c1 = new LinkedList();
        Collection c2 = new LinkedList();
        
        c2.add(new LocalTestNode(-123));
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
            c1.add(nodes[k]);
            c2.add(nodes[k]);
        }
        
        assertTrue(m.values().containsAll(c1));
        assertTrue(!m.values().containsAll(c2));
        
        m  = (TransactionalHashMap) makeMap();
        c1 = new LinkedList();
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
            c1.add(nodes[k]);
        }
        
        try {
            m.values().addAll(c1);
            fail("should have caught exception of addAll()");
        } catch (UnsupportedOperationException ignored) {}
        
        m  = (TransactionalHashMap) makeMap();
        c1 = new LinkedList();
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
            c1.add(nodes[k]);
        }
        
        assertTrue(!m.values().retainAll(c1));
        assertEquals(nodes.length, m.size());
        
        m  = (TransactionalHashMap) makeMap();
        c1 = new LinkedList();
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
            
            if (k % 2 == 1) {
                c1.add(nodes[k]);
            }
        }
        
        assertTrue(m.values().retainAll(c1));
        assertEquals(nodes.length / 2, m.size());
        
        m  = (TransactionalHashMap) makeMap();
        c1 = new LinkedList();
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }
        
        assertTrue(m.values().retainAll(c1));
        assertEquals(0, m.size());
        
        m  = (TransactionalHashMap) makeMap();
        c1 = new LinkedList();
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }
        
        assertTrue(!m.values().removeAll(c1));
        assertEquals(nodes.length, m.size());
        
        m  = (TransactionalHashMap) makeMap();
        c1 = new LinkedList();
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
            
            if (k % 2 == 0) {
                c1.add(nodes[k]);
            }
        }
        
        assertTrue(m.values().removeAll(c1));
        assertEquals(nodes.length / 2, m.size());
        
        m  = (TransactionalHashMap) makeMap();
        c1 = new LinkedList();
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
            c1.add(nodes[k]);
        }
        
        assertTrue(m.values().removeAll(c1));
        assertEquals(0, m.size());
        
        m = (TransactionalHashMap) makeMap();
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }
        
        m.values().clear();
        assertEquals(0, m.size());
    }
    
    /**
     * test entrySet() method
     */
    public void testEntrySet() {
        
        testEntrySet((TransactionalHashMap) makeMap());
        
        Map           m       = (TransactionalHashMap) makeMap();
        LocalTestNode nodes[] = makeLocalNodes();
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }
        
        testEntrySet(m);
        
        m = (TransactionalHashMap) makeMap();
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }
        
        try {
            ((Map.Entry) m.entrySet().iterator().next())
            .setValue(new LocalTestNode(-1));
            fail("Should have caught UnsupportedOperationException");
        } catch (UnsupportedOperationException ignored) {}
        
        int count = m.size();
        
        for (Iterator iter = m.entrySet().iterator(); iter.hasNext(); ) {
            iter.next();
            iter.remove();
            
            --count;
            
            assertEquals(count, m.size());
        }
        
        assertTrue(m.isEmpty());
        
        m = (TransactionalHashMap) makeMap();
        
        Collection c1 = new LinkedList();
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
            c1.add(nodes[k].getKey());
        }
        
        try {
            m.entrySet().addAll(c1);
            fail("should have caught exception of addAll()");
        } catch (UnsupportedOperationException ignored) {}
        
        m = (TransactionalHashMap) makeMap();
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }
        
        m.entrySet().clear();
        assertEquals(0, m.size());
        
        m = (TransactionalHashMap) makeMap();
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }
        
    }
    
    /**
     * Method testEquals
     */
    public void testEquals() {
        
        Map           m       = (TransactionalHashMap) makeMap();
        LocalTestNode nodes[] = makeLocalNodes();
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }
        
        assertTrue(!m.equals(null));
        assertEquals(m, m);
        
        Map m1 = new HashMap();
        
        for (int k = 0; k < nodes.length; k++) {
            m1.put(nodes[k].getKey(), nodes[k]);
        }
        
        assertEquals(m, m1);
        
        m1 = (TransactionalHashMap) makeMap();
        
        for (int k = 0; k < (nodes.length - 1); k++) {
            m1.put(nodes[k].getKey(), nodes[k]);
        }
        
        assertTrue(!m.equals(m1));
        
        m1 = (TransactionalHashMap) makeMap();
        
        for (int k = 0; k < nodes.length; k++) {
            m1.put(nodes[k].getKey(), nodes[k]);
        }
        
        LocalTestNode node1 = new LocalTestNode(-1000);
        
        m1.put(node1.getKey(), node1);
        assertTrue(!m.equals(m1));
        
        m1 = (TransactionalHashMap) makeMap();
        
        for (int k = 0; k < nodes.length; k++) {
            m1.put(nodes[k].getKey(), nodes[nodes.length - (k + 1)]);
        }
        
        assertTrue(!m.equals(m1));
        
        m1 = (TransactionalHashMap) makeMap();
        
        for (int k = nodes.length - 1; k >= 0; k--) {
            m1.put(nodes[k].getKey(), nodes[k]);
        }
        
        assertEquals(m, m1);
    }
    
    /**
     * test hashCode() method
     */
    public void testHashCode() {
        
        Map           m       = (TransactionalHashMap) makeMap();
        LocalTestNode nodes[] = makeLocalNodes();
        
        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }
        
        Map m1 = (TransactionalHashMap) makeMap();
        
        for (int k = nodes.length - 1; k >= 0; k--) {
            m1.put(nodes[k].getKey(), nodes[k]);
        }
        
        assertEquals(m.hashCode(), m1.hashCode());
    }
    
    /**
     * test constructors
     */
    public void testConstructors() {
        
        TransactionalHashMap m = (TransactionalHashMap) makeMap();
        
        assertTrue(m.isEmpty());
        
        TransactionalHashMap m1 = new TransactionalHashMap(m);
        
        assertTrue(m1.isEmpty());
        
        m1 = (TransactionalHashMap) makeMap();
        
        LocalTestNode nodes[] = makeLocalNodes();
        
        for (int k = 0; k < nodes.length; k++) {
            m1.put(nodes[k].getKey(), nodes[k]);
        }
        
        m = new TransactionalHashMap(m1);
        
        assertEquals(m, m1);
        
        Map m2 = new HashMap();
        
        for (int k = 0; k < nodes.length; k++) {
            m2.put(nodes[k].getKey(), nodes[k]);
        }
        
        m = new TransactionalHashMap(m2);
        
        assertEquals(m, m2);
        
        // accept duplicated values
        m2 = new HashMap();
        
        m2.put("1", "foo");
        m2.put("2", "foo");
        
        m = new TransactionalHashMap(m2);
        
        // accept null values
        m2.put("2", null);
        
        m = new TransactionalHashMap(m2);

        // accept non-Comparable values
        m2.put("2", new Object());
        
        m = new TransactionalHashMap(m2);
        
        // accept incompatible values
        m2.put("2", new Integer(2));
        
        m = new TransactionalHashMap(m2);
        
        // reject incompatible keys
        m2.remove("2");
        m2.put(new Integer(2), "bad key");
        
        m = new TransactionalHashMap(m2);
        
        // accept non-Comparable keys
        m2.clear();
        m2.put("1", "foo");
        m2.put(new Object(), "bad key");
        
        m = new TransactionalHashMap(m2);
    }
    
    /* ********** START helper methods ********** */
    private void testKeySet(final Map m) {
        
        Set s = m.keySet();
        
        assertEquals(m.size(), s.size());
        assertEquals(m.isEmpty(), s.isEmpty());
        
        LocalTestNode node = new LocalTestNode(-1);
        
        m.put(node.getKey(), node);
        assertTrue(s.contains(node.getKey()));
        assertEquals(m.size(), s.size());
        assertEquals(m.isEmpty(), s.isEmpty());
        m.remove(node.getKey());
        assertTrue(!s.contains(node.getKey()));
        assertEquals(m.size(), s.size());
        assertEquals(m.isEmpty(), s.isEmpty());
        
        assertFalse(s.contains(null));
        
        assertFalse(s.contains(new Object()));
        
        for (int k = 0; k < m.size(); k++) {
            assertTrue(s.contains(new Integer(k)));
        }
        
        int count = 0;
        
        for (Iterator iter = s.iterator(); iter.hasNext(); ) {
            iter.next();
            
            ++count;
        }
        
        assertEquals(count, s.size());
        
        // force the map to have some content
        m.put(node.getKey(), node);
        
        Iterator      iter  = m.keySet().iterator();
        LocalTestNode node2 = new LocalTestNode(-2);
        
        m.put(node2.getKey(), node2);
        
        try {
            iter.next();
            fail("next() should have thrown an exception after a put");
        } catch (ConcurrentModificationException ignored) {}
        
        m.remove(node2.getKey());
        
        iter = s.iterator();
        
        m.remove(node.getKey());
        
        try {
            iter.next();
            fail("next() should have thrown an exception after a Map remove");
        } catch (ConcurrentModificationException ignored) {}
        
        m.put(node.getKey(), node);
        
        iter = s.iterator();
        
        s.remove(node.getKey());
        
        try {
            iter.next();
            fail("next() should have thrown an exception after a Set remove");
        } catch (ConcurrentModificationException ignored) {}
        
        iter  = s.iterator();
        count = 0;
        
        boolean terminated = false;
        
        try {
            while (true) {
                iter.next();
                
                ++count;
            }
        } catch (NoSuchElementException ignored) {
            terminated = true;
        }
        
        assertTrue(terminated);
        assertEquals(m.size(), count);
        
        iter = s.iterator();
        
        try {
            iter.remove();
            fail("Should have thrown exception");
        } catch (IllegalStateException ignored) {}
        
        m.put(node.getKey(), node);
        
        iter = s.iterator();
        
        iter.next();
        m.put(node2.getKey(), node2);
        
        try {
            iter.remove();
            fail("should have thrown exception");
        } catch (ConcurrentModificationException ignored) {}
        
        Iterator iter2 = s.iterator();
        
        iter2.next();
        
        LocalTestNode node3 = new LocalTestNode(-3);
        
        m.put(node3.getKey(), node3);
        
        try {
            iter2.remove();
            fail("should have thrown exception");
        } catch (ConcurrentModificationException ignored) {}
        
        int removalCount = 0;
        
        for (iter = s.iterator(); iter.hasNext(); ) {
            if (iter.next().equals(node.getKey())) {
                try {
                    iter.remove();
                    
                    ++removalCount;
                    
                    iter.remove();
                    fail("2nd remove should have failed");
                } catch (IllegalStateException ignored) {
                    assertEquals(1, removalCount);
                }
            }
        }
        
        assertEquals(1, removalCount);
        assertTrue(!s.contains(node.getKey()));
        
        removalCount = 0;
        
        m.put(node.getKey(), node);
        
        Object[] a1 = s.toArray();
        
        assertEquals(s.size(), a1.length);
                
        try {
            s.toArray(new String[0]);
            
            if (s.size() != 0) {
                fail("should have caught exception creating an invalid array");
            }
        } catch (ArrayStoreException ignored) {}
        
        
        try {
            s.add("foo");
            fail("should have thrown an exception");
        } catch (UnsupportedOperationException ignored) {}
        
        assertTrue(!s.equals(null));
        assertEquals(s, s);
        
        Set hs = new HashSet(s);
        
        assertEquals(s, hs);
        assertEquals(hs, s);
        assertEquals(s.hashCode(), hs.hashCode());
    }
    
    private void testValues(Map m) {
        
        Collection s = m.values();
        
        assertEquals(m.size(), s.size());
        assertEquals(m.isEmpty(), s.isEmpty());
        
        LocalTestNode node = new LocalTestNode(-1);
        
        m.put(node.getKey(), node);
        assertEquals(m.size(), s.size());
        assertEquals(m.isEmpty(), s.isEmpty());
        m.remove(node.getKey());
        assertEquals(m.size(), s.size());
        assertEquals(m.isEmpty(), s.isEmpty());
        assertTrue(!s.contains(node));
        
        for (int k = 0; k < m.size(); k++) {
            assertTrue(s.contains(new LocalTestNode(k)));
        }
        
        m.put(node.getKey(), node);
        assertTrue(s.contains(node));
        m.remove(node.getKey());
        assertTrue(!s.contains(node));
        
        int count = 0;
        
        for (Iterator iter = s.iterator(); iter.hasNext(); ) {
            iter.next();
            
            ++count;
        }
        
        assertEquals(s.size(), count);
        
        LocalTestNode node4 = new LocalTestNode(-4);
        
        m.put(node4.getKey(), node4);
        
        Iterator iter = s.iterator();
        
        m.put(node.getKey(), node);
        
        try {
            iter.next();
            fail("next() should have thrown an exception after a put");
        } catch (ConcurrentModificationException ignored) {}
        
        iter = s.iterator();
        
        m.remove(node.getKey());
        
        try {
            iter.next();
            fail("next() should have thrown an exception after a Map remove");
        } catch (ConcurrentModificationException ignored) {}
        
        m.put(node.getKey(), node);
        
        iter = s.iterator();
        
        s.remove(node);
        
        try {
            iter.next();
            fail("next() should have thrown an exception after a Set remove");
        } catch (ConcurrentModificationException ignored) {}
        
        iter  = s.iterator();
        count = 0;
        
        boolean terminated = false;
        
        try {
            while (true) {
                iter.next();
                
                ++count;
            }
        } catch (NoSuchElementException ignored) {
            terminated = true;
        }
        
        assertTrue(terminated);
        assertEquals(m.size(), count);
        
        iter = s.iterator();
        
        try {
            iter.remove();
            fail("Should have thrown exception");
        } catch (IllegalStateException ignored) {}
        
        Iterator iter2 = s.iterator();
        
        try {
            iter2.remove();
            fail("Should have thrown exception");
        } catch (IllegalStateException ignored) {}
        
        m.put(node.getKey(), node);
        
        iter = s.iterator();
        
        iter.next();
        
        LocalTestNode node2 = new LocalTestNode(-2);
        
        m.put(node2.getKey(), node2);
        
        try {
            iter.remove();
            fail("should have thrown exception");
        } catch (ConcurrentModificationException ignored) {}
        
        LocalTestNode node3 = new LocalTestNode(-3);
        
        m.put(node3.getKey(), node3);
        
        iter2 = s.iterator();
        
        while (iter2.hasNext()) {
            iter2.next();
        }
        
        int removalCount = 0;
        
        for (iter = s.iterator(); iter.hasNext(); ) {
            if (iter.next().equals(node3)) {
                try {
                    iter.remove();
                    
                    ++removalCount;
                    
                    iter.remove();
                    fail("2nd remove should have failed");
                } catch (IllegalStateException ignored) {
                    assertEquals(1, removalCount);
                }
            }
        }
        
        assertEquals(1, removalCount);
        assertTrue(!s.contains(node3));
        
        Object[] a1 = s.toArray();
        
        assertEquals(s.size(), a1.length);
        
        try {
            s.toArray(new String[0]);
            
            if (s.size() != 0) {
                fail("should have caught exception creating an invalid array");
            }
        } catch (ArrayStoreException ignored) {}
        
        m.remove(node.getKey());
        m.remove(node2.getKey());
        m.remove(node3.getKey());
        
        try {
            s.add(node.getKey());
            fail("should have thrown an exception");
        } catch (UnsupportedOperationException ignored) {}
        
        assertTrue(!s.equals(null));
        assertEquals(s, s);
        
        Set hs = new HashSet(s);
        
        assertTrue(!s.equals(hs));
        assertTrue(!hs.equals(s));
    }
    
    private void testEntrySet(Map m) {
        
        Set s = m.entrySet();
        
        assertEquals(m.size(), s.size());
        assertEquals(m.isEmpty(), s.isEmpty());
        
        LocalTestNode node = new LocalTestNode(-1);
        
        m.put(node.getKey(), node);
        assertEquals(m.size(), s.size());
        assertEquals(m.isEmpty(), s.isEmpty());
        m.remove(node.getKey());
        assertEquals(m.size(), s.size());
        assertEquals(m.isEmpty(), s.isEmpty());
        
        int count = 0;
        
        for (Iterator iter = s.iterator(); iter.hasNext(); ) {
            iter.next();
            
            ++count;
        }
        
        assertEquals(s.size(), count);
        
        LocalTestNode node2 = new LocalTestNode(-2);
        
        if (m.size() == 0) {
            m.put(node2.getKey(), node2);
        }
        
        Iterator iter = s.iterator();
        
        m.put(node.getKey(), node);
        
        try {
            iter.next();
            fail("next() should have thrown an exception after a put");
        } catch (ConcurrentModificationException ignored) {}
        
        m.remove(node2.getKey());
        
        iter = s.iterator();
        
        m.remove(node.getKey());
        
        try {
            iter.next();
            fail("next() should have thrown an exception after a Map remove");
        } catch (ConcurrentModificationException ignored) {}
        
        m.put(node.getKey(), node);
        
        iter  = s.iterator();
        count = 0;
        
        boolean terminated = false;
        
        try {
            while (true) {
                iter.next();
                
                ++count;
            }
        } catch (NoSuchElementException ignored) {
            terminated = true;
        }
        
        assertTrue(terminated);
        assertEquals(m.size(), count);
        
        iter = s.iterator();
        
        try {
            iter.remove();
            fail("Should have thrown exception");
        } catch (IllegalStateException ignored) {}
        
        iter = s.iterator();
        
        iter.next();
        
        LocalTestNode node3 = new LocalTestNode(-3);
        
        m.put(node3.getKey(), node3);
        
        try {
            iter.remove();
            fail("should have thrown exception");
        } catch (ConcurrentModificationException ignored) {}
        
        int removalCount = 0;
        int when         = m.size() / 2;
        int timer        = 0;
        
        for (iter = s.iterator(); iter.hasNext(); ) {
            iter.next();
            
            if (timer == when) {
                try {
                    iter.remove();
                    
                    ++removalCount;
                    
                    iter.remove();
                    fail("2nd remove should have failed");
                } catch (IllegalStateException ignored) {
                    assertEquals(1, removalCount);
                }
            }
            
            timer++;
        }
        
        assertEquals(1, removalCount);
        
        Iterator iter2 = s.iterator();
        
        try {
            iter2.remove();
            fail("Should have thrown exception");
        } catch (IllegalStateException ignored) {}
        
        iter2 = s.iterator();
        
        while (iter2.hasNext()) {
            iter2.next();
        }
        
        LocalTestNode node4 = new LocalTestNode(-4);
        
        m.put(node4.getKey(), node4);
        
        try {
            iter2.remove();
            fail("should have thrown exception");
        } catch (ConcurrentModificationException ignored) {}
        
        Object[] a1 = s.toArray();
        
        assertEquals(s.size(), a1.length);
                
        try {
            s.toArray(new Integer[0]);
            
            if (s.size() != 0) {
                fail("should have caught exception creating an invalid array");
            }
        } catch (ArrayStoreException ignored) {}
        
        try {
            s.add(node.getKey());
            fail("should have thrown an exception");
        } catch (UnsupportedOperationException ignored) {}
        
        assertTrue(!s.equals(null));
        assertEquals("SetEquality 1", s, s);
        
        Set hs = new HashSet(s);
        
        assertEquals("SetEquality 2", s, hs);
        assertEquals("SetEquality 3", hs, s);
        assertEquals(s.hashCode(), hs.hashCode());
    }
    
    private LocalTestNode[] makeLocalNodes() {
        
        LocalTestNode nodes[] = new LocalTestNode[1023];
        
        for (int k = 0; k < nodes.length; k++) {
            nodes[k] = new LocalTestNode(k);
        }
        
        return nodes;
    }
    
    private class TestEntry
    {
        private Object key;
        private Object value;
        private TestEntry(Object key, Object value)
        {
            this.key = key;
            this.value = value;
        }
        
    }
    
    /* **********  END  helper methods ********** */
    
    
}