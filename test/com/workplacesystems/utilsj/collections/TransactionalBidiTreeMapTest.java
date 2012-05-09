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
 * TransactionalBidiTreeMapTest.java
 * JUnit based test
 *
 * Created on 15 October 2003, 16:34
 */

package com.workplacesystems.utilsj.collections;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author jdonnelly
 */
public class TransactionalBidiTreeMapTest extends TestCase {
    
    public TransactionalBidiTreeMapTest(java.lang.String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(TransactionalBidiTreeMapTest.class);
        return suite;
    }
    
    public static void main(java.lang.String[] args) {        
        junit.textui.TestRunner.run(suite());    
    }
    
    private TransactionalBidiTreeMap GetDefault(){
        TransactionalBidiTreeMap tbtm = new TransactionalBidiTreeMap();
        tbtm.put(new Integer(2), "C");
        tbtm.put(new Integer(1), "D");
        tbtm.put(new Integer(3), "A");
        tbtm.put(new Integer(4), "B");
        return tbtm;
    }

    public void testInstantiationEmpty() {
        TransactionalBidiTreeMap tbtm = new TransactionalBidiTreeMap();
        assertTrue(tbtm != null);
    }
    public void testPutValue() {
        TransactionalBidiTreeMap tbtm = new TransactionalBidiTreeMap();
        tbtm.put(new Integer(1), "D");
        assertEquals("D", tbtm.get(new Integer(1)));
    }
    public void testFirstKey() {
        TransactionalBidiTreeMap tbtm = GetDefault();
        assertEquals(new Integer(1), tbtm.firstKey());
    }
    public void testFirstValue() {
        TransactionalBidiTreeMap tbtm = GetDefault();
        assertEquals("D", tbtm.firstValue());
    }
    public void testLastKey() {
        TransactionalBidiTreeMap tbtm = GetDefault();
        assertEquals(new Integer(4), tbtm.lastKey());
    }
    public void testLastValue() {
        TransactionalBidiTreeMap tbtm = GetDefault();
        assertEquals( "B", tbtm.lastValue());
    }
    public void testFirstKeyByValue() {
        TransactionalBidiTreeMap tbtm = GetDefault();
        assertEquals(new Integer(3), tbtm.firstKeyByValue());
    }
    public void testFirstValueByValue() {
        TransactionalBidiTreeMap tbtm = GetDefault();
        assertEquals("A", tbtm.firstValueByValue());
    }
    public void testLastKeyByValue() {
        TransactionalBidiTreeMap tbtm = GetDefault();
        assertEquals(new Integer(1), tbtm.lastKeyByValue());
    }
    public void testLastValueByValue() {
        TransactionalBidiTreeMap tbtm = GetDefault();
        assertEquals( "D", tbtm.lastValueByValue());
    }
    /*
     *Test the head map by key
     *
     * 1 -> D   <<<<<<
     * 2 -> C   <<<<<<
     * 3 -> A   <<<
     * 4 -> B
     *
     */
    public void testHeadMap_firstKey(){
        TransactionalBidiTreeMap tbtm = GetDefault();
        SortedMap sm = tbtm.headMap(new Integer(3));
        assertEquals(new Integer(1), sm.firstKey());
    }
    public void testHeadMap_lastKey(){
        TransactionalBidiTreeMap tbtm = GetDefault();
        SortedMap sm = tbtm.headMap(new Integer(3));
        assertEquals(new Integer(2), sm.lastKey());
    }

    /*
     *Test the tail map by key
     *
     * 1 -> D
     * 2 -> C
     * 3 -> A   <<<<<<
     * 4 -> B   <<<<<<
     *
     */
    public void testTailMap_firstKey(){
        TransactionalBidiTreeMap tbtm = GetDefault();
        SortedMap sm = tbtm.tailMap(new Integer(3));
        assertEquals(new Integer(3), sm.firstKey());
    }
    public void testTailMap_lastKey(){
        TransactionalBidiTreeMap tbtm = GetDefault();
        SortedMap sm = tbtm.tailMap(new Integer(3));
        assertEquals(new Integer(4), sm.lastKey());
    }
    
    
    public void testTailMap_entryset_remove_rollback(){
        TransactionalBidiTreeMap tbtm = GetDefault();
        tbtm.setAutoCommit(false);
        
        Set entrySet = tbtm.tailMap(new Integer(3)).entrySet();
        entrySet.remove(entrySet.iterator().next());

        tbtm.rollback();
        assertTrue(tbtm.containsKey(new Integer(3)));
    }


    /*
     *Test the head map by key
     *
     * A -> 3   <<<<<<
     * B -> 4   <<<<<<
     * C -> 2   <<<
     * D -> 1
     */
    public void testHeadMapByValue_firstValue(){
        TransactionalBidiTreeMap tbtm = GetDefault();
        SortedBidiMap sbm = tbtm.headMapByValue("C");
        assertEquals( "A", sbm.firstValue());
    }    
    public void testHeadMapByValue_firstKey(){
        TransactionalBidiTreeMap tbtm = GetDefault();
        SortedBidiMap sbm = tbtm.headMapByValue("C");
        assertEquals( new Integer(3), sbm.firstKey());
    }
    public void testHeadMapByValue_lastValue(){
        TransactionalBidiTreeMap tbtm = GetDefault();
        SortedBidiMap sbm = tbtm.headMapByValue("C");
        assertEquals( "B", sbm.lastValue());
    }
    public void testHeadMapByValue_lastKey(){
        TransactionalBidiTreeMap tbtm = GetDefault();
        SortedBidiMap sbm = tbtm.headMapByValue("C");
        assertEquals( new Integer(4), sbm.lastKey());
    }
    public void testHeadMapByValue_firstKeyByValue(){
        TransactionalBidiTreeMap tbtm = GetDefault();
        SortedBidiMap sbm = tbtm.headMapByValue("C");
        assertEquals( new Integer(3), sbm.firstKeyByValue());
    }
    public void testHeadMapByValue_lastKeyByValue(){
        TransactionalBidiTreeMap tbtm = GetDefault();
        SortedBidiMap sbm = tbtm.headMapByValue("C");
        assertEquals( new Integer(4), sbm.lastKeyByValue());
    }
    public void testHeadMapByValue_firstValueByValue(){
        TransactionalBidiTreeMap tbtm = GetDefault();
        SortedBidiMap sbm = tbtm.headMapByValue("C");
        assertEquals( "A", sbm.firstValueByValue());
    }
    public void testHeadMapByValue_lastValueByValue(){
        TransactionalBidiTreeMap tbtm = GetDefault();
        SortedBidiMap sbm = tbtm.headMapByValue("C");
        assertEquals( "B", sbm.lastValueByValue());
    }

    /*
     *Test the tail map by value
     *
     * A -> 3   
     * B -> 4   
     * C -> 2   <<<<<<
     * D -> 1   <<<<<<
     */
    public void testTailMapByValue_lastKey(){
        TransactionalBidiTreeMap tbtm = GetDefault();
        SortedBidiMap sbm = tbtm.tailMapByValue("C");
        assertEquals( new Integer(2), sbm.lastKey());
    }
    public void testTailMapByValue_firstKey(){
        TransactionalBidiTreeMap tbtm = GetDefault();
        SortedBidiMap sbm = tbtm.tailMapByValue("C");
        assertEquals( new Integer(1), sbm.firstKey());
    }
    public void testTailMapByValue_lastValue(){
        TransactionalBidiTreeMap tbtm = GetDefault();
        SortedBidiMap sbm = tbtm.tailMapByValue("C");
        assertEquals( "C", sbm.lastValue());
    }
    public void testTailMapByValue_firstValue(){
        TransactionalBidiTreeMap tbtm = GetDefault();
        SortedBidiMap sbm = tbtm.tailMapByValue("C");
        assertEquals( "D", sbm.firstValue());
    }
    public void testTailMapByValue_firstKeyByValue(){
        TransactionalBidiTreeMap tbtm = GetDefault();
        SortedBidiMap sbm = tbtm.tailMapByValue("C");
        assertEquals( new Integer(2), sbm.firstKeyByValue());
    }
    public void testTailMapByValue_lastKeyByValue(){
        TransactionalBidiTreeMap tbtm = GetDefault();
        SortedBidiMap sbm = tbtm.tailMapByValue("C");
        assertEquals( new Integer(1), sbm.lastKeyByValue());
    }
    public void testTailMapByValue_firstValueByValue(){
        TransactionalBidiTreeMap tbtm = GetDefault();
        SortedBidiMap sbm = tbtm.tailMapByValue("C");
        assertEquals( "C", sbm.firstValueByValue());
    }
    public void testTailMapByValue_lastValueByValue(){
        TransactionalBidiTreeMap tbtm = GetDefault();
        SortedBidiMap sbm = tbtm.tailMapByValue("C");
        assertEquals( "D", sbm.lastValueByValue());
    }

    private void checkNextIteration(Iterator i, Object key, Object value){
        Map.Entry entry;
        assertEquals(true, i.hasNext());
        entry = (Map.Entry) i.next();
        assertEquals(key, entry.getKey());
        assertEquals(value, entry.getValue());
    }
    /*
     *Test iterator returned by TransactionalBidiTreeMap
     */
    public void testTransactionalBidiTreeMap_entrySetIterator(){
        TransactionalBidiTreeMap tbtm = GetDefault();
        Set s = tbtm.entrySet();
		assertEquals(4, s.size());
        Iterator i = s.iterator();
        checkNextIteration(i, new Integer(1), "D");
        checkNextIteration(i, new Integer(2), "C");
        checkNextIteration(i, new Integer(3), "A");
        checkNextIteration(i, new Integer(4), "B");
        assertEquals(false, i.hasNext());
    }
    public void testTransactionalBidiTreeMap_entrySetByValueIterator(){
        TransactionalBidiTreeMap tbtm = GetDefault();
        Set s = tbtm.entrySetByValue();
		assertEquals(4, s.size());
        Iterator i = s.iterator();
        checkNextIteration(i, new Integer(3), "A");
        checkNextIteration(i, new Integer(4), "B");
        checkNextIteration(i, new Integer(2), "C");
        checkNextIteration(i, new Integer(1), "D");
        assertEquals(false, i.hasNext());
    }

    public void testSortedBidiMap_entrySetIterator(){
        TransactionalBidiTreeMap tbtm = GetDefault();
        SortedBidiMap sbm = tbtm.tailMapByValue("C");
        Set s = sbm.entrySet();
		assertEquals(2, s.size());
        Iterator i = s.iterator();
        checkNextIteration(i, new Integer(1), "D");
        checkNextIteration(i, new Integer(2), "C");
        assertEquals(false, i.hasNext());
    }
    
    public void testSortedBidiMap_entrySetByValueIterator(){
        TransactionalBidiTreeMap tbtm = GetDefault();
        SortedBidiMap sbm = tbtm.tailMapByValue("B");
        Set s = sbm.entrySetByValue();
		assertEquals(3, s.size());
        Iterator i = s.iterator();
        checkNextIteration(i, new Integer(4), "B");
        checkNextIteration(i, new Integer(2), "C");
        checkNextIteration(i, new Integer(1), "D");
        assertEquals(false, i.hasNext());
    }
    
    private transient boolean running;
    private transient boolean inner_running;

    public void testTransactional_put(){
        TransactionalBidiTreeMap tbtm = new TransactionalBidiTreeMap();
        tbtm.setAutoCommit(false);
        
        Integer one = new Integer(1);
        tbtm.put(one, "A");
        tbtm.commit();
        tbtm.remove(one);
        tbtm.put(one, "A");
    }
        
    private class TestData implements TransactionalComparable
    {
        public Integer value;
        public Integer new_value;
        
        public TestData(int value)
        {
            this.value = new Integer(value);
        }
        
        public int compareTo(Object other)
        {
            return value.compareTo(((TestData)other).value);
        }
        
        private Integer getCompareValue(int transaction_status)
        {
			if (transaction_status == COMMITTED)
				return value;
			return new_value != null ? new_value : value;
        }

		public int compareTo(int this_transaction_status, Object other_obj, int other_transaction_status)
		{
			return getCompareValue(this_transaction_status).compareTo(((TestData)other_obj).getCompareValue(other_transaction_status));
		}
    }
    
    public void testTransactional_remove_put(){
        TransactionalBidiTreeMap tbtm = new TransactionalBidiTreeMap();
        
        TestData one = new TestData(1);
        TestData two = new TestData(2);
        TestData three = new TestData(3);
        TestData four = new TestData(4);
        
        tbtm.put(one, one);
        tbtm.put(three, three);
        tbtm.put(four, four);
        
        tbtm.setAutoCommit(false);
        
        tbtm.remove(one);
        tbtm.put(one, one);
        tbtm.put(two, two);
        
        assertTrue(tbtm.containsValue(one));
        assertTrue(tbtm.containsValue(two));
        assertTrue(tbtm.containsValue(three));
        assertTrue(tbtm.containsValue(four));
    }
     
    public void testTransactional_remove_put_2(){
        TransactionalBidiTreeMap tbtm = new TransactionalBidiTreeMap();
        TestData one = new TestData(1);
        TestData two = new TestData(2);
        tbtm.put(one, one);
        tbtm.put(two, two);
        
        tbtm.setAutoCommit(false);
        
        tbtm.remove(one);
        tbtm.remove(two);
        tbtm.put(one, one);
        
        assertTrue(tbtm.containsValue(one));
    }
    
    public void testTransactional_iterator_remove_put_rollback(){
        TransactionalBidiTreeMap tbtm = new TransactionalBidiTreeMap();
        
        TestData one = new TestData(1);
        
        tbtm.setAutoCommit(false);
        
        tbtm.put(one, one);
        
        final Iterator iterator = tbtm.valuesByValue().iterator();
        iterator.next();
        iterator.remove();

        tbtm.rollback();
        
        assertTrue(tbtm.isEmpty());
    }
     
    public void testTransactional_entryset_remove_put_rollback(){
        TransactionalBidiTreeMap tbtm = new TransactionalBidiTreeMap();
        
        TestData one = new TestData(1);
        
        tbtm.setAutoCommit(false);
        
        tbtm.put(one, one);
        
        Set entrySet = tbtm.entrySet();
        entrySet.remove(entrySet.iterator().next());

        assertTrue(tbtm.isEmpty());
        tbtm.rollback();
        assertTrue(tbtm.isEmpty());
    }
     
    public void testTransactional_entrysetbyvalue_remove_put_rollback(){
        TransactionalBidiTreeMap tbtm = new TransactionalBidiTreeMap();
        
        TestData one = new TestData(1);
        
        tbtm.setAutoCommit(false);
        
        tbtm.put(one, one);
        
        Set entrySet = tbtm.entrySetByValue();
        entrySet.remove(entrySet.iterator().next());

        assertTrue(tbtm.isEmpty());
        tbtm.rollback();
        assertTrue(tbtm.isEmpty());
    }
     
    public void testTransactional_tailmap_entryset_remove_put_rollback(){
        TransactionalBidiTreeMap tbtm = new TransactionalBidiTreeMap();
        
        TestData one = new TestData(1);
        
        tbtm.setAutoCommit(false);
        
        tbtm.put(one, one);
        
        Set entrySet = tbtm.tailMap(one).entrySet();
        entrySet.remove(entrySet.iterator().next());

        assertTrue(tbtm.isEmpty());
        tbtm.rollback();
        assertTrue(tbtm.isEmpty());
    }
     
    public void testTransactional_remove_alter_put(){
        TransactionalBidiTreeMap tbtm = new TransactionalBidiTreeMap();
        tbtm.setAutoCommit(false);
        
        TestData one = new TestData(1);
        TestData two = new TestData(2);
        TestData three = new TestData(3);
        tbtm.put(three, three);
        tbtm.put(one, one);
        tbtm.put(two, two);
        tbtm.commit();
        tbtm.removeValue(two);
        two.new_value = new Integer(0);
        tbtm.put(two, two);
        
        Set s = tbtm.entrySetByValue();
		assertEquals(3, s.size());
        Iterator i = s.iterator();
        TestData data = (TestData)((Map.Entry)i.next()).getValue();
        assertEquals(data, two);
        data = (TestData)((Map.Entry)i.next()).getValue();
        assertEquals(data, one);
        data = (TestData)((Map.Entry)i.next()).getValue();
        assertEquals(data, three);
        
        assertEquals(false, i.hasNext());
        
        assertTrue(tbtm.containsValue(two));
        assertTrue(tbtm.containsValue(one));
        assertTrue(tbtm.containsValue(three));
        
        tbtm.removeValue(two);
		assertEquals(2, s.size());
    
    }
    
    public void testTransactional_clear(){
        TransactionalBidiTreeMap tbtm = new TransactionalBidiTreeMap();
        tbtm.setAutoCommit(false);
        
        Integer one = new Integer(1);
        Integer two = new Integer(2);
        
        tbtm.put(one, "A");
        tbtm.clear();
        assertTrue(tbtm.isEmpty());
        tbtm.commit();
        assertTrue(tbtm.isEmpty());
        
        tbtm.put(one, "A");
        tbtm.commit();
        tbtm.put(two, "B");
        tbtm.clear();
        assertTrue(tbtm.isEmpty());
        tbtm.rollback();
        assertEquals(1, tbtm.size());
    }

    public void testTransactions_commit(){
        final TransactionalBidiTreeMap tbtm = GetDefault();
        tbtm.setAutoCommit(false);

        final Thread parentThread = Thread.currentThread();
        running = true;
        inner_running = true;
        final Thread thread = new Thread(){
            @Override
            public void run()
            {
                tbtm.put(new Integer(0), "E");
				tbtm.remove(new Integer(3));
				tbtm.remove(new Integer(4));
				tbtm.put(new Integer(3), "G");
                assertEquals(new Integer(0), tbtm.firstKey());
                assertEquals("G", tbtm.lastValueByValue());
                assertEquals(new Integer(3), tbtm.lastKey());
                SortedMap sm = tbtm.headMap(new Integer(3));
                assertEquals(new Integer(0), sm.firstKey());
                SortedBidiMap sbm = tbtm.tailMapByValue("C");
                assertEquals( new Integer(3), sbm.lastKeyByValue());

                Set s = tbtm.entrySet();
				assertEquals(4, s.size());
                Iterator i = s.iterator();
                checkNextIteration(i, new Integer(0), "E");
                checkNextIteration(i, new Integer(1), "D");
                checkNextIteration(i, new Integer(2), "C");
                checkNextIteration(i, new Integer(3), "G");
                assertEquals(false, i.hasNext());
                
                s = tbtm.entrySetByValue();
				assertEquals(4, s.size());
                i = s.iterator();
                checkNextIteration(i, new Integer(2), "C");
                checkNextIteration(i, new Integer(1), "D");
                checkNextIteration(i, new Integer(0), "E");
                checkNextIteration(i, new Integer(3), "G");
                assertEquals(false, i.hasNext());

                sbm = tbtm.tailMapByValue("C");
                s = sbm.entrySet();
				assertEquals(4, s.size());
                i = s.iterator();
                checkNextIteration(i, new Integer(0), "E");
                checkNextIteration(i, new Integer(1), "D");
                checkNextIteration(i, new Integer(2), "C");
                checkNextIteration(i, new Integer(3), "G");
                assertEquals(false, i.hasNext());

                sbm = tbtm.tailMapByValue("B");
                s = sbm.entrySetByValue();
				assertEquals(4, s.size());
                i = s.iterator();
                checkNextIteration(i, new Integer(2), "C");
                checkNextIteration(i, new Integer(1), "D");
                checkNextIteration(i, new Integer(0), "E");
                checkNextIteration(i, new Integer(3), "G");
                assertEquals(false, i.hasNext());

                FilterableMap fm = tbtm.filteredMap(new Filter() {
                    public boolean isValid(Object obj) {
                        return ((Integer)obj).compareTo(new Integer(3)) < 0;
                    }
                });
                fm = fm.filteredMap(new Filter() {
                    public boolean isValid(Object obj) {
                        return ((Integer)obj).compareTo(new Integer(1)) != 0;
                    }
                });
                s = fm.entrySet();
				assertEquals(2, s.size());
                i = s.iterator();
                checkNextIteration(i, new Integer(0), "E");
                checkNextIteration(i, new Integer(2), "C");
                assertEquals(false, i.hasNext());

                running = false;
                parentThread.interrupt();
                
                while (inner_running)
                {
                    try
                    {
                        sleep(10);
                    }
                    catch (Exception e) {}
                }
                inner_running = true;
                tbtm.commit();
                running = false;
                parentThread.interrupt();
            }
        };

        thread.start();
        
        while (running)
        {
            try
            {
                Thread.sleep(10);
            }
            catch (Exception e) {}
        }
        running = true;

        try {
            tbtm.remove(new Integer(3));
            fail("should have caught ConcurrentModificationException");
        } catch (ConcurrentModificationException ignored) {}
        try {
            tbtm.remove(new Integer(4));
            fail("should have caught ConcurrentModificationException");
        } catch (ConcurrentModificationException ignored) {}
        try {
            tbtm.put(new Integer(3), "H");
            fail("should have caught IllegalArgumentException");
        } catch (IllegalArgumentException ignored) {}
        try {
            tbtm.put(new Integer(0), "I");
            fail("should have caught ConcurrentModificationException");
        } catch (ConcurrentModificationException ignored) {}

        assertEquals(new Integer(1), tbtm.firstKey());
        assertEquals("D", tbtm.lastValueByValue());
        assertEquals(new Integer(4), tbtm.lastKey());
        SortedMap sm = tbtm.headMap(new Integer(3));
        assertEquals(new Integer(1), sm.firstKey());
        SortedBidiMap sbm = tbtm.tailMapByValue("C");
        assertEquals( new Integer(1), sbm.lastKeyByValue());
        
        Set s = tbtm.entrySet();
		assertEquals(4, s.size());
        Iterator i = s.iterator();
        checkNextIteration(i, new Integer(1), "D"); 
        checkNextIteration(i, new Integer(2), "C");
        checkNextIteration(i, new Integer(3), "A");
        checkNextIteration(i, new Integer(4), "B");
        assertEquals(false, i.hasNext());
                
        s = tbtm.entrySetByValue();
		assertEquals(4, s.size());
        i = s.iterator();
        checkNextIteration(i, new Integer(3), "A");
        checkNextIteration(i, new Integer(4), "B");
        checkNextIteration(i, new Integer(2), "C");
        checkNextIteration(i, new Integer(1), "D");
        assertEquals(false, i.hasNext());

        sbm = tbtm.tailMapByValue("C");
        s = sbm.entrySet();
		assertEquals(2, s.size());
        i = s.iterator();
        checkNextIteration(i, new Integer(1), "D");
        checkNextIteration(i, new Integer(2), "C");
        assertEquals(false, i.hasNext());

        sbm = tbtm.tailMapByValue("B");
        s = sbm.entrySetByValue();
		assertEquals(3, s.size());
        i = s.iterator();
        checkNextIteration(i, new Integer(4), "B");
        checkNextIteration(i, new Integer(2), "C");
        checkNextIteration(i, new Integer(1), "D");
        assertEquals(false, i.hasNext());

        FilterableMap fm = tbtm.filteredMap(new Filter() {
            public boolean isValid(Object obj) {
                return ((Integer)obj).compareTo(new Integer(3)) < 0;
            }
        });
        fm = fm.filteredMap(new Filter() {
            public boolean isValid(Object obj) {
                return ((Integer)obj).compareTo(new Integer(1)) != 0;
            }
        });
        s = fm.entrySet();
		assertEquals(1, s.size());
        i = s.iterator();
        checkNextIteration(i, new Integer(2), "C");
        assertEquals(false, i.hasNext());

        inner_running = false;
        thread.interrupt();
        while (running)
        {
            try
            {
                Thread.sleep(10);
            }
            catch (Exception e) {}
        }
        running = true;

        assertEquals(new Integer(0), tbtm.firstKey());
        assertEquals("G", tbtm.lastValueByValue());
        assertEquals(new Integer(3), tbtm.lastKey());
        sm = tbtm.headMap(new Integer(3));
        assertEquals(new Integer(0), sm.firstKey());
        sbm = tbtm.tailMapByValue("C");
        assertEquals( new Integer(3), sbm.lastKeyByValue());

        s = tbtm.entrySet();
		assertEquals(4, s.size());
        i = s.iterator();
        checkNextIteration(i, new Integer(0), "E");
        checkNextIteration(i, new Integer(1), "D");
        checkNextIteration(i, new Integer(2), "C");
        checkNextIteration(i, new Integer(3), "G");
        assertEquals(false, i.hasNext());

        s = tbtm.entrySetByValue();
		assertEquals(4, s.size());
        i = s.iterator();
        checkNextIteration(i, new Integer(2), "C");
        checkNextIteration(i, new Integer(1), "D");
        checkNextIteration(i, new Integer(0), "E");
        checkNextIteration(i, new Integer(3), "G");
        assertEquals(false, i.hasNext());

        sbm = tbtm.tailMapByValue("C");
        s = sbm.entrySet();
		assertEquals(4, s.size());
        i = s.iterator();
        checkNextIteration(i, new Integer(0), "E");
        checkNextIteration(i, new Integer(1), "D");
        checkNextIteration(i, new Integer(2), "C");
        checkNextIteration(i, new Integer(3), "G");
        assertEquals(false, i.hasNext());

        sbm = tbtm.tailMapByValue("B");
        s = sbm.entrySetByValue();
		assertEquals(4, s.size());
        i = s.iterator();
        checkNextIteration(i, new Integer(2), "C");
        checkNextIteration(i, new Integer(1), "D");
        checkNextIteration(i, new Integer(0), "E");
        checkNextIteration(i, new Integer(3), "G");
        assertEquals(false, i.hasNext());
        
        fm = tbtm.filteredMap(new Filter() {
            public boolean isValid(Object obj) {
                return ((Integer)obj).compareTo(new Integer(3)) < 0;
            }
        });
        fm = fm.filteredMap(new Filter() {
            public boolean isValid(Object obj) {
                return ((Integer)obj).compareTo(new Integer(1)) != 0;
            }
        });
        s = fm.entrySet();
		assertEquals(2, s.size());
        i = s.iterator();
        checkNextIteration(i, new Integer(0), "E");
        checkNextIteration(i, new Integer(2), "C");
        assertEquals(false, i.hasNext());
    }

    public void testTransactions_rollback(){
        final TransactionalBidiTreeMap tbtm = GetDefault();
        tbtm.setAutoCommit(false);

        final Thread parentThread = Thread.currentThread();
        running = true;
        inner_running = true;
        final Thread thread = new Thread(){
            @Override
            public void run()
            {
                tbtm.put(new Integer(0), "E");
				tbtm.remove(new Integer(3));
				tbtm.remove(new Integer(4));
				tbtm.put(new Integer(3), "G");
                assertEquals(new Integer(0), tbtm.firstKey());
                assertEquals("G", tbtm.lastValueByValue());
                assertEquals(new Integer(3), tbtm.lastKey());
                SortedMap sm = tbtm.headMap(new Integer(3));
                assertEquals(new Integer(0), sm.firstKey());
                SortedBidiMap sbm = tbtm.tailMapByValue("C");
                assertEquals( new Integer(3), sbm.lastKeyByValue());

                Set s = tbtm.entrySet();
				assertEquals(4, s.size());
                Iterator i = s.iterator();
                checkNextIteration(i, new Integer(0), "E");
                checkNextIteration(i, new Integer(1), "D");
                checkNextIteration(i, new Integer(2), "C");
                checkNextIteration(i, new Integer(3), "G");
                assertEquals(false, i.hasNext());
                
                s = tbtm.entrySetByValue();
				assertEquals(4, s.size());
                i = s.iterator();
                checkNextIteration(i, new Integer(2), "C");
                checkNextIteration(i, new Integer(1), "D");
                checkNextIteration(i, new Integer(0), "E");
                checkNextIteration(i, new Integer(3), "G");
                assertEquals(false, i.hasNext());

                sbm = tbtm.tailMapByValue("C");
                s = sbm.entrySet();
				assertEquals(4, s.size());
                i = s.iterator();
                checkNextIteration(i, new Integer(0), "E");
                checkNextIteration(i, new Integer(1), "D");
                checkNextIteration(i, new Integer(2), "C");
                checkNextIteration(i, new Integer(3), "G");
                assertEquals(false, i.hasNext());

                sbm = tbtm.tailMapByValue("B");
                s = sbm.entrySetByValue();
				assertEquals(4, s.size());
                i = s.iterator();
                checkNextIteration(i, new Integer(2), "C");
                checkNextIteration(i, new Integer(1), "D");
                checkNextIteration(i, new Integer(0), "E");
                checkNextIteration(i, new Integer(3), "G");
                assertEquals(false, i.hasNext());

                FilterableMap fm = tbtm.filteredMap(new Filter() {
                    public boolean isValid(Object obj) {
                        return ((Integer)obj).compareTo(new Integer(3)) < 0;
                    }
                });
                fm = fm.filteredMap(new Filter() {
                    public boolean isValid(Object obj) {
                        return ((Integer)obj).compareTo(new Integer(1)) != 0;
                    }
                });
                s = fm.entrySet();
				assertEquals(2, s.size());
                i = s.iterator();
                checkNextIteration(i, new Integer(0), "E");
                checkNextIteration(i, new Integer(2), "C");
                assertEquals(false, i.hasNext());

                running = false;
                parentThread.interrupt();
                
                while (inner_running)
                {
                    try
                    {
                        sleep(10);
                    }
                    catch (Exception e) {}
                }
                inner_running = true;
                tbtm.rollback();
                running = false;
                parentThread.interrupt();
            }
        };

        thread.start();
        
        while (running)
        {
            try
            {
                Thread.sleep(10);
            }
            catch (Exception e) {}
        }
        running = true;

        try {
            tbtm.remove(new Integer(3));
            fail("should have caught ConcurrentModificationException");
        } catch (ConcurrentModificationException ignored) {}
        try {
            tbtm.remove(new Integer(4));
            fail("should have caught ConcurrentModificationException");
        } catch (ConcurrentModificationException ignored) {}
        try {
            tbtm.put(new Integer(3), "H");
            fail("should have caught IllegalArgumentException");
        } catch (IllegalArgumentException ignored) {}
        try {
            tbtm.put(new Integer(0), "I");
            fail("should have caught ConcurrentModificationException");
        } catch (ConcurrentModificationException ignored) {}

        assertEquals(new Integer(1), tbtm.firstKey());
        assertEquals("D", tbtm.lastValueByValue());
        assertEquals(new Integer(4), tbtm.lastKey());
        SortedMap sm = tbtm.headMap(new Integer(3));
        assertEquals(new Integer(1), sm.firstKey());
        SortedBidiMap sbm = tbtm.tailMapByValue("C");
        assertEquals( new Integer(1), sbm.lastKeyByValue());
        
        Set s = tbtm.entrySet();
		assertEquals(4, s.size());
        Iterator i = s.iterator();
        checkNextIteration(i, new Integer(1), "D");
        checkNextIteration(i, new Integer(2), "C");
        checkNextIteration(i, new Integer(3), "A");
        checkNextIteration(i, new Integer(4), "B");
        assertEquals(false, i.hasNext());
                
        s = tbtm.entrySetByValue();
		assertEquals(4, s.size());
        i = s.iterator();
        checkNextIteration(i, new Integer(3), "A");
        checkNextIteration(i, new Integer(4), "B");
        checkNextIteration(i, new Integer(2), "C");
        checkNextIteration(i, new Integer(1), "D");
        assertEquals(false, i.hasNext());

        sbm = tbtm.tailMapByValue("C");
        s = sbm.entrySet();
		assertEquals(2, s.size());
        i = s.iterator();
        checkNextIteration(i, new Integer(1), "D");
        checkNextIteration(i, new Integer(2), "C");
        assertEquals(false, i.hasNext());

        sbm = tbtm.tailMapByValue("B");
        s = sbm.entrySetByValue();
		assertEquals(3, s.size());
        i = s.iterator();
        checkNextIteration(i, new Integer(4), "B");
        checkNextIteration(i, new Integer(2), "C");
        checkNextIteration(i, new Integer(1), "D");
        assertEquals(false, i.hasNext());

        FilterableMap fm = tbtm.filteredMap(new Filter() {
            public boolean isValid(Object obj) {
                return ((Integer)obj).compareTo(new Integer(3)) < 0;
            }
        });
        fm = fm.filteredMap(new Filter() {
            public boolean isValid(Object obj) {
                return ((Integer)obj).compareTo(new Integer(1)) != 0;
            }
        });
        s = fm.entrySet();
		assertEquals(1, s.size());
        i = s.iterator();
        checkNextIteration(i, new Integer(2), "C");
        assertEquals(false, i.hasNext());

        inner_running = false;
        thread.interrupt();
        while (running)
        {
            try
            {
                Thread.sleep(10);
            }
            catch (Exception e) {}
        }
        running = true;

        assertEquals(new Integer(1), tbtm.firstKey());
        assertEquals("D", tbtm.lastValueByValue());
        assertEquals(new Integer(4), tbtm.lastKey());
        sm = tbtm.headMap(new Integer(3));
        assertEquals(new Integer(1), sm.firstKey());
        sbm = tbtm.tailMapByValue("C");
        assertEquals( new Integer(1), sbm.lastKeyByValue());
        
        s = tbtm.entrySet();
		assertEquals(4, s.size());
        i = s.iterator();
        checkNextIteration(i, new Integer(1), "D");
        checkNextIteration(i, new Integer(2), "C");
        checkNextIteration(i, new Integer(3), "A");
        checkNextIteration(i, new Integer(4), "B");
        assertEquals(false, i.hasNext());
                
        s = tbtm.entrySetByValue();
		assertEquals(4, s.size());
        i = s.iterator();
        checkNextIteration(i, new Integer(3), "A");
        checkNextIteration(i, new Integer(4), "B");
        checkNextIteration(i, new Integer(2), "C");
        checkNextIteration(i, new Integer(1), "D");
        assertEquals(false, i.hasNext());

        sbm = tbtm.tailMapByValue("C");
        s = sbm.entrySet();
		assertEquals(2, s.size());
        i = s.iterator();
        checkNextIteration(i, new Integer(1), "D");
        checkNextIteration(i, new Integer(2), "C");
        assertEquals(false, i.hasNext());

        sbm = tbtm.tailMapByValue("B");
        s = sbm.entrySetByValue();
		assertEquals(3, s.size());
        i = s.iterator();
        checkNextIteration(i, new Integer(4), "B");
        checkNextIteration(i, new Integer(2), "C");
        checkNextIteration(i, new Integer(1), "D");
        assertEquals(false, i.hasNext());

        fm = tbtm.filteredMap(new Filter() {
            public boolean isValid(Object obj) {
                return ((Integer)obj).compareTo(new Integer(3)) < 0;
            }
        });
        fm = fm.filteredMap(new Filter() {
            public boolean isValid(Object obj) {
                return ((Integer)obj).compareTo(new Integer(1)) != 0;
            }
        });
        s = fm.entrySet();
		assertEquals(1, s.size());
        i = s.iterator();
        checkNextIteration(i, new Integer(2), "C");
        assertEquals(false, i.hasNext());
    }


	public void testTransactions_size(){
		final TransactionalBidiTreeMap tbtm = GetDefault();
		tbtm.setAutoCommit(false);

		final Thread parentThread = Thread.currentThread();
		running = true;
		inner_running = true;
		final Thread thread = new Thread(){
			@Override
            public void run()
			{
				assertEquals(4, tbtm.size());
				
				tbtm.put(new Integer(0), "E");
				tbtm.remove(new Integer(3));
				tbtm.remove(new Integer(4));
				tbtm.put(new Integer(3), "G");
				tbtm.removeValue("E");
				tbtm.put(new Integer(5), "I");
				tbtm.put(new Integer(6), "H");

				assertEquals(5, tbtm.size());

				running = false;
				parentThread.interrupt();
                
				while (inner_running)
				{
					try
					{
						sleep(10);
					}
					catch (Exception e) {}
				}
				inner_running = true;
				tbtm.commit();
				running = false;
				parentThread.interrupt();
			}
		};

		thread.start();
        
		while (running)
		{
			try
			{
				Thread.sleep(10);
			}
			catch (Exception e) {}
		}
		running = true;

		assertEquals(4, tbtm.size());

		inner_running = false;
		thread.interrupt();
		while (running)
		{
			try
			{
				Thread.sleep(10);
			}
			catch (Exception e) {}
		}
		running = true;

		assertEquals(5, tbtm.size());
	}

    protected Map makeMap() {
        return new TransactionalBidiTreeMap();
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

        try {
            m.containsKey(new Object());
            fail("should have caught ClassCastException");
        } catch (ClassCastException ignored) {}

        try {
            m.containsKey(null);
            fail("should have caught NullPointerException");
        } catch (NullPointerException ignored) {}

        assertTrue(!m.containsKey("foo"));

        LocalTestNode nodes[] = makeLocalNodes();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
            assertTrue(m.containsKey(nodes[k].getKey()));
        }

        assertTrue(!m.containsKey(new Integer(-1)));

        try {
            m.containsKey("foo");
            fail("Should have caught ClassCastException");
        } catch (ClassCastException ignored) {}

        for (int k = 0; k < nodes.length; k++) {
            m.remove(nodes[k].getKey());
            assertTrue(!m.containsKey(nodes[k].getKey()));
        }
    }

    /**
     * test containsValue() method
     */
    public void testContainsValue() {

        Map           m       = (TransactionalBidiTreeMap) makeMap();
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

        try {
            m.get(new Object());
            fail("should have caught ClassCastException");
        } catch (ClassCastException ignored) {}

        try {
            m.get(null);
            fail("should have caught NullPointerException");
        } catch (NullPointerException ignored) {}

        assertNull(m.get("foo"));

        LocalTestNode nodes[] = makeLocalNodes();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
            assertSame(m.get(nodes[k].getKey()), nodes[k]);
        }

        assertNull(m.get(new Integer(-1)));

        try {
            m.get("foo");
            fail("Should have caught ClassCastException");
        } catch (ClassCastException ignored) {}

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

        try {
            m.put(new Object(), "foo");
            fail("should have caught ClassCastException");
        } catch (ClassCastException ignored) {}

        try {
            m.put(null, "foo");
            fail("should have caught NullPointerException");
        } catch (NullPointerException ignored) {}

        try {
            m.put("foo", null);
            fail("should have caught NullPointerException");
        } catch (NullPointerException ignored) {}

        try {
            m.put("foo", new Object());
            fail("should have caught ClassCastException");
        } catch (ClassCastException ignored) {}

        LocalTestNode[] nodes = makeLocalNodes();

        for (int k = 0; k < nodes.length; k++) {
            assertNull(m.put(nodes[k].getKey(), nodes[k].getValue()));
            
            //duplicate key
            try {
                m.put(nodes[k].getKey(), "foo");
                fail("should have caught IllegalArgumentException");
            } catch (IllegalArgumentException ignored) {}
            //duplicate value
            try {
                m.put(new Integer(-1), nodes[k].getValue());
                fail("should have caught IllegalArgumentException");
            } catch (IllegalArgumentException ignored) {}
        }
    }

    /**
     * test remove() method
     */
    public void testRemove() {

        TransactionalBidiTreeMap m       = (TransactionalBidiTreeMap) makeMap();
        LocalTestNode    nodes[] = makeLocalNodes();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }

        try {
            m.remove(null);
            fail("should have caught NullPointerException");
        } catch (NullPointerException ignored) {}

        try {
            m.remove(new Object());
            fail("should have caught ClassCastException");
        } catch (ClassCastException ignored) {}

        assertNull(m.remove(new Integer(-1)));

        try {
            m.remove("foo");
            fail("should have caught ClassCastException");
        } catch (ClassCastException ignored) {}

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

        Map           m       = (TransactionalBidiTreeMap) makeMap();
        LocalTestNode nodes[] = makeLocalNodes();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }

        Map m1 = new HashMap();

        m1.put(null, "foo");

        try {
            m.putAll(m1);
            fail("Should have caught NullPointerException");
        } catch (NullPointerException ignored) {}

        m1 = new HashMap();

        m1.put(new Object(), "bar");

        try {
            m.putAll(m1);
            fail("Should have caught ClassCastException");
        } catch (ClassCastException ignored) {}

        m1 = new HashMap();

        m1.put("fubar", null);

        try {
            m.putAll(m1);
            fail("Should have caught NullPointerException");
        } catch (NullPointerException ignored) {}

        m1 = new HashMap();

        m1.put("fubar", new Object());

        try {
            m.putAll(m1);
            fail("Should have caught ClassCastException");
        } catch (ClassCastException ignored) {}

        assertEquals(nodes.length, m.size());

        m  = (TransactionalBidiTreeMap) makeMap();
        m1 = new HashMap();

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

        Map           m       = (TransactionalBidiTreeMap) makeMap();
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

        testKeySet((TransactionalBidiTreeMap) makeMap());

        Map           m       = (TransactionalBidiTreeMap) makeMap();
        LocalTestNode nodes[] = makeLocalNodes();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }

        testKeySet(m);

        m = (TransactionalBidiTreeMap) makeMap();

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

        m = (TransactionalBidiTreeMap) makeMap();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }

        Set s = m.keySet();

        try {
            s.remove(null);
            fail("should have caught NullPointerException");
        } catch (NullPointerException ignored) {}

        try {
            s.remove(new Object());
            fail("should have caught ClassCastException");
        } catch (ClassCastException ignored) {}

        for (int k = 0; k < nodes.length; k++) {
            Comparable key = nodes[k].getKey();

            assertTrue(s.remove(key));
            assertTrue(!s.contains(key));
            assertTrue(!m.containsKey(key));
            assertTrue(!m.containsValue(nodes[k]));
        }

        assertTrue(m.isEmpty());

        m = (TransactionalBidiTreeMap) makeMap();

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

        m  = (TransactionalBidiTreeMap) makeMap();
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

        m  = (TransactionalBidiTreeMap) makeMap();
        c1 = new LinkedList();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);

            if (k % 2 == 1) {
                c1.add(nodes[k].getKey());
            }
        }

        assertTrue(m.keySet().retainAll(c1));
        assertEquals(nodes.length / 2, m.size());

        m  = (TransactionalBidiTreeMap) makeMap();
        c1 = new LinkedList();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }

        assertTrue(m.keySet().retainAll(c1));
        assertEquals(0, m.size());

        m  = (TransactionalBidiTreeMap) makeMap();
        c1 = new LinkedList();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }

        assertTrue(!m.keySet().removeAll(c1));
        assertEquals(nodes.length, m.size());

        m  = (TransactionalBidiTreeMap) makeMap();
        c1 = new LinkedList();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);

            if (k % 2 == 0) {
                c1.add(nodes[k].getKey());
            }
        }

        assertTrue(m.keySet().removeAll(c1));
        assertEquals(nodes.length / 2, m.size());

        m  = (TransactionalBidiTreeMap) makeMap();
        c1 = new LinkedList();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
            c1.add(nodes[k].getKey());
        }

        assertTrue(m.keySet().removeAll(c1));
        assertEquals(0, m.size());

        m = (TransactionalBidiTreeMap) makeMap();

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

        testValues((TransactionalBidiTreeMap) makeMap());

        Map           m       = (TransactionalBidiTreeMap) makeMap();
        LocalTestNode nodes[] = makeLocalNodes();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }

        testValues(m);

        m = (TransactionalBidiTreeMap) makeMap();

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

        m = (TransactionalBidiTreeMap) makeMap();

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

        m = (TransactionalBidiTreeMap) makeMap();

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

        m  = (TransactionalBidiTreeMap) makeMap();
        c1 = new LinkedList();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
            c1.add(nodes[k]);
        }

        try {
            m.values().addAll(c1);
            fail("should have caught exception of addAll()");
        } catch (UnsupportedOperationException ignored) {}

        m  = (TransactionalBidiTreeMap) makeMap();
        c1 = new LinkedList();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
            c1.add(nodes[k]);
        }

        assertTrue(!m.values().retainAll(c1));
        assertEquals(nodes.length, m.size());

        m  = (TransactionalBidiTreeMap) makeMap();
        c1 = new LinkedList();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);

            if (k % 2 == 1) {
                c1.add(nodes[k]);
            }
        }

        assertTrue(m.values().retainAll(c1));
        assertEquals(nodes.length / 2, m.size());

        m  = (TransactionalBidiTreeMap) makeMap();
        c1 = new LinkedList();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }

        assertTrue(m.values().retainAll(c1));
        assertEquals(0, m.size());

        m  = (TransactionalBidiTreeMap) makeMap();
        c1 = new LinkedList();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }

        assertTrue(!m.values().removeAll(c1));
        assertEquals(nodes.length, m.size());

        m  = (TransactionalBidiTreeMap) makeMap();
        c1 = new LinkedList();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);

            if (k % 2 == 0) {
                c1.add(nodes[k]);
            }
        }

        assertTrue(m.values().removeAll(c1));
        assertEquals(nodes.length / 2, m.size());

        m  = (TransactionalBidiTreeMap) makeMap();
        c1 = new LinkedList();

        for (int k = 0; k < nodes.length; k++)
        {
            m.put(nodes[k].getKey(), nodes[k]);
            c1.add(nodes[k]);
        }

        assertTrue(m.values().removeAll(c1));
        assertEquals(0, m.size());

        m = (TransactionalBidiTreeMap) makeMap();

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

        testEntrySet((TransactionalBidiTreeMap) makeMap());

        Map           m       = (TransactionalBidiTreeMap) makeMap();
        LocalTestNode nodes[] = makeLocalNodes();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }

        testEntrySet(m);

        m = (TransactionalBidiTreeMap) makeMap();

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

        m = (TransactionalBidiTreeMap) makeMap();

        Collection c1 = new LinkedList();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
            c1.add(nodes[k].getKey());
        }

        try {
            m.entrySet().addAll(c1);
            fail("should have caught exception of addAll()");
        } catch (UnsupportedOperationException ignored) {}

        m = (TransactionalBidiTreeMap) makeMap();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }

        m.entrySet().clear();
        assertEquals(0, m.size());

        m = (TransactionalBidiTreeMap) makeMap();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }

        int x = 0;

        for (Iterator iter = m.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iter.next();

            assertSame(entry.getKey(), nodes[x].getKey());
            assertSame(entry.getValue(), nodes[x]);

            x++;
        }
    }

    /**
     * Method testEquals
     */
    public void testEquals() {

        Map           m       = (TransactionalBidiTreeMap) makeMap();
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

        m1 = (TransactionalBidiTreeMap) makeMap();

        for (int k = 0; k < (nodes.length - 1); k++) {
            m1.put(nodes[k].getKey(), nodes[k]);
        }

        assertTrue(!m.equals(m1));

        m1 = (TransactionalBidiTreeMap) makeMap();

        for (int k = 0; k < nodes.length; k++) {
            m1.put(nodes[k].getKey(), nodes[k]);
        }

        LocalTestNode node1 = new LocalTestNode(-1000);

        m1.put(node1.getKey(), node1);
        assertTrue(!m.equals(m1));

        m1 = (TransactionalBidiTreeMap) makeMap();

        for (int k = 0; k < nodes.length; k++) {
            m1.put(nodes[k].getKey(), nodes[nodes.length - (k + 1)]);
        }

        assertTrue(!m.equals(m1));

        m1 = (TransactionalBidiTreeMap) makeMap();

        for (int k = nodes.length - 1; k >= 0; k--) {
            m1.put(nodes[k].getKey(), nodes[k]);
        }

        assertEquals(m, m1);
    }

    /**
     * test hashCode() method
     */
    public void testHashCode() {

        Map           m       = (TransactionalBidiTreeMap) makeMap();
        LocalTestNode nodes[] = makeLocalNodes();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }

        Map m1 = (TransactionalBidiTreeMap) makeMap();

        for (int k = nodes.length - 1; k >= 0; k--) {
            m1.put(nodes[k].getKey(), nodes[k]);
        }

        assertEquals(m.hashCode(), m1.hashCode());
    }

    /**
     * test constructors
     */
    public void testConstructors() {

        TransactionalBidiTreeMap m = (TransactionalBidiTreeMap) makeMap();

        assertTrue(m.isEmpty());

        TransactionalBidiTreeMap m1 = new TransactionalBidiTreeMap(m);

        assertTrue(m1.isEmpty());

        m1 = (TransactionalBidiTreeMap) makeMap();

        LocalTestNode nodes[] = makeLocalNodes();

        for (int k = 0; k < nodes.length; k++) {
            m1.put(nodes[k].getKey(), nodes[k]);
        }

        m = new TransactionalBidiTreeMap(m1);

        assertEquals(m, m1);

        Map m2 = new HashMap();

        for (int k = 0; k < nodes.length; k++) {
            m2.put(nodes[k].getKey(), nodes[k]);
        }

        m = new TransactionalBidiTreeMap(m2);

        assertEquals(m, m2);

        // reject duplicated values
        m2 = new HashMap();

        m2.put("1", "foo");
        m2.put("2", "foo");

        try {
            m = new TransactionalBidiTreeMap(m2);

            fail("Should have caught IllegalArgumentException");
        } catch (IllegalArgumentException ignored) {}

        // reject null values
        m2.put("2", null);

        try {
            m = new TransactionalBidiTreeMap(m2);

            fail("Should have caught NullPointerException");
        } catch (NullPointerException ignored) {}

        // reject non-Comparable values
        m2.put("2", new Object());

        try {
            m = new TransactionalBidiTreeMap(m2);

            fail("Should have caught ClassCastException");
        } catch (ClassCastException ignored) {}

        // reject incompatible values
        m2.put("2", new Integer(2));

        try {
            m = new TransactionalBidiTreeMap(m2);

            fail("Should have caught ClassCastException");
        } catch (ClassCastException ignored) {}

        // reject incompatible keys
        m2.remove("2");
        m2.put(new Integer(2), "bad key");

        try {
            m = new TransactionalBidiTreeMap(m2);

            fail("Should have caught ClassCastException");
        } catch (ClassCastException ignored) {}

        // reject non-Comparable keys
        m2.clear();
        m2.put("1", "foo");
        m2.put(new Object(), "bad key");

        try {
            m = new TransactionalBidiTreeMap(m2);

            fail("Should have caught ClassCastException");
        } catch (ClassCastException ignored) {}
    }

    /**
     * test getKeyForValue() method
     */
    public void testGetKeyForValue() {

        TransactionalBidiTreeMap m = (TransactionalBidiTreeMap) makeMap();

        try {
            m.getKeyForValue(new Object());
            fail("should have caught ClassCastException");
        } catch (ClassCastException ignored) {}

        try {
            m.getKeyForValue(null);
            fail("should have caught NullPointerException");
        } catch (NullPointerException ignored) {}

        assertNull(m.getKeyForValue("foo"));

        LocalTestNode nodes[] = makeLocalNodes();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
            assertSame(m.getKeyForValue(nodes[k]), nodes[k].getKey());
        }

        assertNull(m.getKeyForValue(new LocalTestNode(-1)));

        try {
            m.getKeyForValue("foo");
            fail("Should have caught ClassCastException");
        } catch (ClassCastException ignored) {}

        for (int k = 0; k < nodes.length; k++) {
            assertNotNull(m.getKeyForValue(nodes[k]));
            m.remove(nodes[k].getKey());
            assertNull(m.getKeyForValue(nodes[k]));
        }
    }

    /**
     * test removeValue() method
     */
    public void testRemoveValue() {

        TransactionalBidiTreeMap m       = (TransactionalBidiTreeMap) makeMap();
        LocalTestNode    nodes[] = makeLocalNodes();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }

        try {
            m.removeValue(null);
            fail("should have caught NullPointerException");
        } catch (NullPointerException ignored) {}

        try {
            m.removeValue(new Object());
            fail("should have caught ClassCastException");
        } catch (ClassCastException ignored) {}

        assertNull(m.remove(new Integer(-1)));

        try {
            m.removeValue("foo");
            fail("should have caught ClassCastException");
        } catch (ClassCastException ignored) {}

        for (int k = 0; k < nodes.length; k += 2) {
            assertNotNull(m.getKeyForValue(nodes[k]));
            assertSame(nodes[k].getKey(), m.removeValue(nodes[k]));
            assertNull(m.removeValue(nodes[k]));
            assertNull(m.getKeyForValue(nodes[k]));
        }

        for (int k = 1; k < nodes.length; k += 2) {
            assertNotNull(m.getKeyForValue(nodes[k]));
            assertSame(nodes[k].getKey(), m.removeValue(nodes[k]));
            assertNull(m.removeValue(nodes[k]));
            assertNull(m.getKeyForValue(nodes[k]));
        }

        assertTrue(m.isEmpty());
    }

    /**
     * test entrySetByValue() method
     */
    public void testEntrySetByValue() {

        testEntrySetByValue((TransactionalBidiTreeMap) makeMap());

        TransactionalBidiTreeMap m       = (TransactionalBidiTreeMap) makeMap();
        LocalTestNode    nodes[] = makeLocalNodes();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }

        testEntrySetByValue(m);

        m = (TransactionalBidiTreeMap) makeMap();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }

        try {
            ((Map.Entry) m.entrySetByValue().iterator().next())
                .setValue(new LocalTestNode(-1));
            fail("Should have caught UnsupportedOperationException");
        } catch (UnsupportedOperationException ignored) {}

        int count = m.size();

        for (Iterator iter = m.entrySetByValue().iterator();
                iter.hasNext(); ) {
            iter.next();
            iter.remove();

            --count;

            assertEquals(count, m.size());
        }

        assertTrue(m.isEmpty());

        m = (TransactionalBidiTreeMap) makeMap();

        Collection c1 = new LinkedList();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
            c1.add(nodes[k].getKey());
        }

        try {
            m.entrySetByValue().addAll(c1);
            fail("should have caught exception of addAll()");
        } catch (UnsupportedOperationException ignored) {}

        m = (TransactionalBidiTreeMap) makeMap();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }

        m.entrySetByValue().clear();
        assertEquals(0, m.size());

        m = (TransactionalBidiTreeMap) makeMap();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }

        int x = 0;

        for (Iterator iter = m.entrySetByValue().iterator();
                iter.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iter.next();

            assertSame(entry.getKey(), nodes[x].getKey());
            assertSame(entry.getValue(), nodes[x]);

            x++;
        }
    }

    /**
     * test keySetByValue() method
     */
    public void testKeySetByValue() {

        testKeySetByValue((TransactionalBidiTreeMap) makeMap());

        TransactionalBidiTreeMap m       = (TransactionalBidiTreeMap) makeMap();
        LocalTestNode    nodes[] = makeLocalNodes();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }

        testKeySetByValue(m);

        m = (TransactionalBidiTreeMap) makeMap();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }

        int count = m.size();

        for (Iterator iter = m.keySetByValue().iterator(); iter.hasNext(); ) 
{
            iter.next();
            iter.remove();

            --count;

            assertEquals(count, m.size());
        }

        assertTrue(m.isEmpty());

        m = (TransactionalBidiTreeMap) makeMap();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }

        Set s = m.keySetByValue();

        try {
            s.remove(null);
            fail("should have caught NullPointerException");
        } catch (NullPointerException ignored) {}

        try {
            s.remove(new Object());
            fail("should have caught ClassCastException");
        } catch (ClassCastException ignored) {}

        for (int k = 0; k < nodes.length; k++) {
            Comparable key = nodes[k].getKey();

            assertTrue(s.remove(key));
            assertTrue(!s.contains(key));
            assertTrue(!m.containsKey(key));
            assertTrue(!m.containsValue(nodes[k]));
        }

        assertTrue(m.isEmpty());

        m = (TransactionalBidiTreeMap) makeMap();

        Collection c1 = new LinkedList();
        Collection c2 = new LinkedList();

        c2.add(new Integer(-99));

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
            c1.add(nodes[k].getKey());
            c2.add(nodes[k].getKey());
        }

        assertTrue(m.keySetByValue().containsAll(c1));
        assertTrue(!m.keySetByValue().containsAll(c2));

        m  = (TransactionalBidiTreeMap) makeMap();
        c1 = new LinkedList();

        c1.add(new Integer(-55));

        try {
            m.keySetByValue().addAll(c1);
            fail("should have caught exception of addAll()");
        } catch (UnsupportedOperationException ignored) {}

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
            c1.add(nodes[k].getKey());
        }

        assertTrue(!m.keySetByValue().retainAll(c1));
        assertEquals(nodes.length, m.size());

        m  = (TransactionalBidiTreeMap) makeMap();
        c1 = new LinkedList();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);

            if (k % 2 == 1) {
                c1.add(nodes[k].getKey());
            }
        }

        assertTrue(m.keySetByValue().retainAll(c1));
        assertEquals(nodes.length / 2, m.size());

        m  = (TransactionalBidiTreeMap) makeMap();
        c1 = new LinkedList();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }

        assertTrue(m.keySetByValue().retainAll(c1));
        assertEquals(0, m.size());

        m  = (TransactionalBidiTreeMap) makeMap();
        c1 = new LinkedList();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }

        assertTrue(!m.keySetByValue().removeAll(c1));
        assertEquals(nodes.length, m.size());

        m  = (TransactionalBidiTreeMap) makeMap();
        c1 = new LinkedList();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);

            if (k % 2 == 0) {
                c1.add(nodes[k].getKey());
            }
        }

        assertTrue(m.keySetByValue().removeAll(c1));
        assertEquals(nodes.length / 2, m.size());

        m  = (TransactionalBidiTreeMap) makeMap();
        c1 = new LinkedList();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
            c1.add(nodes[k].getKey());
        }

        assertTrue(m.keySetByValue().removeAll(c1));
        assertEquals(0, m.size());

        m = (TransactionalBidiTreeMap) makeMap();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }

        m.keySetByValue().clear();
        assertEquals(0, m.size());
    }

    /**
     * test valuesByValue() method
     */
    public void testValuesByValue() {

        testValuesByValue((TransactionalBidiTreeMap) makeMap());

        TransactionalBidiTreeMap m       = (TransactionalBidiTreeMap) makeMap();
        LocalTestNode    nodes[] = makeLocalNodes();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }

        testValuesByValue(m);

        m = (TransactionalBidiTreeMap) makeMap();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }

        int count = m.size();

        for (Iterator iter = m.valuesByValue().iterator(); iter.hasNext(); ) 
{
            iter.next();
            iter.remove();

            --count;

            assertEquals(count, m.size());
        }

        assertTrue(m.isEmpty());

        m = (TransactionalBidiTreeMap) makeMap();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }

        count = m.size();

        Collection s = m.valuesByValue();

        for (int k = 0; k < count; k++) {
            assertTrue(s.remove(nodes[k]));
            assertTrue(!s.contains(nodes[k]));
            assertTrue(!m.containsKey(nodes[k].getKey()));
            assertTrue(!m.containsValue(nodes[k]));
        }

        assertTrue(m.isEmpty());

        m = (TransactionalBidiTreeMap) makeMap();

        Collection c1 = new LinkedList();
        Collection c2 = new LinkedList();

        c2.add(new LocalTestNode(-123));

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
            c1.add(nodes[k]);
            c2.add(nodes[k]);
        }

        assertTrue(m.valuesByValue().containsAll(c1));
        assertTrue(!m.valuesByValue().containsAll(c2));

        m  = (TransactionalBidiTreeMap) makeMap();
        c1 = new LinkedList();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
            c1.add(nodes[k]);
        }

        try {
            m.valuesByValue().addAll(c1);
            fail("should have caught exception of addAll()");
        } catch (UnsupportedOperationException ignored) {}

        m  = (TransactionalBidiTreeMap) makeMap();
        c1 = new LinkedList();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
            c1.add(nodes[k]);
        }

        assertTrue(!m.valuesByValue().retainAll(c1));
        assertEquals(nodes.length, m.size());

        m  = (TransactionalBidiTreeMap) makeMap();
        c1 = new LinkedList();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);

            if (k % 2 == 1) {
                c1.add(nodes[k]);
            }
        }

        assertTrue(m.valuesByValue().retainAll(c1));
        assertEquals(nodes.length / 2, m.size());

        m  = (TransactionalBidiTreeMap) makeMap();
        c1 = new LinkedList();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }

        assertTrue(m.valuesByValue().retainAll(c1));
        assertEquals(0, m.size());

        m  = (TransactionalBidiTreeMap) makeMap();
        c1 = new LinkedList();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }

        assertTrue(!m.valuesByValue().removeAll(c1));
        assertEquals(nodes.length, m.size());

        m  = (TransactionalBidiTreeMap) makeMap();
        c1 = new LinkedList();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);

            if (k % 2 == 0) {
                c1.add(nodes[k]);
            }
        }

        assertTrue(m.valuesByValue().removeAll(c1));
        assertEquals(nodes.length / 2, m.size());

        m  = (TransactionalBidiTreeMap) makeMap();
        c1 = new LinkedList();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
            c1.add(nodes[k]);
        }

        assertTrue(m.valuesByValue().removeAll(c1));
        assertEquals(0, m.size());

        m = (TransactionalBidiTreeMap) makeMap();

        for (int k = 0; k < nodes.length; k++) {
            m.put(nodes[k].getKey(), nodes[k]);
        }

        m.valuesByValue().clear();
        assertEquals(0, m.size());
    }

    public void testRemovalOrder() {
        Value[] values = {new Value("1"), new Value("2"),
                          new Value("3"), new Value("4"),
                          new Value("5"), new Value("6"),
                          new Value("7")};
        TransactionalBidiTreeMap map = new TransactionalBidiTreeMap();
        map.setAutoCommit(false);
        
        // -- TEST1: add, then remove from first to last item
        for (int i=0 ; i<values.length ; i++)
        {
            Value v = values[i];
            map.put(v.getKey(), v);
            assertTrue("could not find " + v + " just inserted", map.containsKey(values[i].getKey()));
        }
        map.commit();
        for (int i=0 ; i<values.length ; i++)
        {
            Value v = values[i];
            if (map.removeValue(v) == null)
                fail("map could not find " + v + " in removeValue() call");
            if (map.containsKey(v.getKey()))
                fail("map found "  + v + " by key after removeValue()");
            if (map.containsValue(v))
                fail("map found " + v + " by value after removeValue()");
        }
        // tidy up after deletes
        map.commit();
        
        // -- TEST2: add, then remove in reverse order
        for (int i=0 ; i<values.length ; i++)
        {
            Value v = values[i];
            map.put(v.getKey(), v);
            assertTrue("could not find " + v + " just inserted", map.containsKey(values[i].getKey()));
        }
        map.commit();
        for (int i=values.length-1 ; i>=0 ; i--)
        {
            Value v = values[i];
            if (map.removeValue(v) == null)
                fail("map could not find " + v + " in removeValue() call");
            if (map.containsKey(v.getKey()))
                fail("map found "  + v + " by key after removeValue()");
            if (map.containsValue(v))
                fail("map found " + v + " by value after removeValue()");
        }
        // tidy up deletes
        map.commit();
        
        // --TEST3: add, then remove in random order
        for (int i=0 ; i<values.length ; i++)
        {
            Value v = values[i];
            map.put(v.getKey(), v);
            assertTrue("could not find " + v + " just inserted", map.containsKey(values[i].getKey()));
        }
        map.commit();
        boolean[] is_removed = new boolean[values.length];
        int removed = 0;
        while (removed < values.length)
        {
            int index = (int)(Math.random()*values.length);
            if (is_removed[index])
                continue;
            Value v = values[index];
            if (map.removeValue(v) == null)
                fail("map could not find " + v + " in removeValue() call");
            if (map.containsKey(v.getKey()))
                fail("map found "  + v + " by key after removeValue()");
            if (map.containsValue(v))
                fail("map found " + v + " by value after removeValue()");
            is_removed[index] = true;
            removed++;
        }
        // tidy up deletes
        map.commit();
        
        // -- TEST4: add remove in specific order
        int[] insert_order = {6,5,4,3,2,1,0};
        for (int i=0 ; i<values.length ; i++)
        {
            int index = insert_order[i];
            Value v = values[index];
            map.put(v.getKey(), v);
            if (!map.containsKey(v.getKey()))
                fail("could not find " + v + " with containsKey(), just inserted");
            if (!map.containsValue(v))
                fail("could not find " + v + " with containsValue(), just inserted");
        }
        map.commit();
        int[] remove_order = {6,5,4,3,2,1,0};
        for (int i=0 ; i<remove_order.length-1 ; i++)
        {
            Value v = values[remove_order[i]];
            if (map.removeValue(v) == null)
                fail("map could not find " + v + " in removeValue() call");
            if (map.containsKey(v.getKey()))
                fail("map found "  + v + " by key after removeValue()");
            if (map.containsValue(v))
                fail("map found " + v + " by value after removeValue()");
        }
    }

    private class FilterCounter implements Filter<Object> {

        private int count = 0;

        public boolean isValid(Object obj) {
            count++;
            return true;
        }
    }

    public void testFilter() {
        TransactionalBidiTreeMap tbtm = GetDefault();
        FilterCounter filter = new FilterCounter();
        FilterableCollection fc = tbtm.valuesByValue().filteredCollection(filter);
        Iterator i = fc.iterator();

        assertEquals(1, filter.count);
        i.hasNext();
        assertEquals(1, filter.count);
        i.next();
        assertEquals(2, filter.count);
        i.hasNext();
        assertEquals(2, filter.count);
        i.next();
        assertEquals(3, filter.count);
   }

    /* ********** START helper methods ********** */
    
    class Value implements Comparable {
        private final String key;
        
        Value(String key) {
            this.key = key;
        }
          
        public String getKey() { return key; }
            
        @Override
        public boolean equals(Object o) {
            if (o instanceof Value) {
                Value other = (Value)o;
                return key.equals(other.key);
            }
            else return false;
        }
        
        @Override
        public String toString() { return key; }

        public int compareTo(Object o)
        {
            Value v = (Value)o;
            return key.compareTo(v.key);
        }
    }
    
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

        try {
            s.contains(null);
            fail("should have caught NullPointerException");
        } catch (NullPointerException ignored) {}

        try {
            s.contains(new Object());
            fail("should have caught ClassCastException");
        } catch (ClassCastException ignored) {}

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

        if (a1.length > 1) {
            Comparable first = (Comparable) a1[0];

            for (int k = 1; k < a1.length; k++) {
                Comparable second = (Comparable) a1[k];

                assertTrue(first.compareTo(second) < 0);

                first = second;
            }

            iter  = s.iterator();
            first = (Comparable) iter.next();

            for (; iter.hasNext(); ) {
                Comparable second = (Comparable) iter.next();

                assertTrue(first.compareTo(second) < 0);

                first = second;
            }
        }

        try {
            s.toArray(new String[0]);

            if (s.size() != 0) {
                fail("should have caught exception creating an invalid array");
            }
        } catch (ArrayStoreException ignored) {}

        s.toArray(new Comparable[0]);
        Integer    array3[] = (Integer[]) s.toArray(new Integer[s.size()]);

        if (array3.length > 1) {
            Integer first = array3[0];

            for (int k = 1; k < array3.length; k++) {
                Integer second = array3[k];

                assertTrue(first.compareTo(second) < 0);

                first = second;
            }
        }

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

    private void testKeySetByValue(final TransactionalBidiTreeMap m) {

        Set s = m.keySetByValue();

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

        try {
            s.contains(null);
            fail("should have caught NullPointerException");
        } catch (NullPointerException ignored) {}

        try {
            s.contains(new Object());
            fail("should have caught ClassCastException");
        } catch (ClassCastException ignored) {}

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

        Iterator      iter  = m.keySetByValue().iterator();
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

        //          if (a1.length > 1)
        //          {
        //              Comparable first = ( Comparable ) a1[ 0 ];
        //              for (int k = 1; k < a1.length; k++)
        //              {
        //                  Comparable second = ( Comparable ) a1[ k ];
        //                  assertTrue(first.compareTo(second) < 0);
        //                  first = second;
        //              }
        //              iter  = s.iterator();
        //              first = ( Comparable ) iter.next();
        //              for (; iter.hasNext(); )
        //              {
        //                  Comparable second = ( Comparable ) iter.next();
        //                  assertTrue(first.compareTo(second) < 0);
        //                  first = second;
        //              }
        //          }
        try {
            s.toArray(new String[0]);

            if (s.size() != 0) {
                fail("should have caught exception creating an invalid array");
            }
        } catch (ArrayStoreException ignored) {}

        s.toArray(new Comparable[0]);
        s.toArray(new Integer[s.size()]);

        //          if (array3.length > 1)
        //          {
        //              Integer first = array3[ 0 ];
        //              for (int k = 1; k < array3.length; k++)
        //              {
        //                  Integer second = array3[ k ];
        //                  assertTrue(first.compareTo(second) < 0);
        //                  first = second;
        //              }
        //          }
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

        if (a1.length > 1) {
            Comparable first = (Comparable) a1[0];

            for (int k = 1; k < a1.length; k++) {
                Comparable second = (Comparable) a1[k];

                assertTrue(first.compareTo(second) < 0);

                first = second;
            }

            iter  = s.iterator();
            first = (Comparable) iter.next();

            for (; iter.hasNext(); ) {
                Comparable second = (Comparable) iter.next();

                assertTrue(first.compareTo(second) < 0);

                first = second;
            }
        }

        try {
            s.toArray(new String[0]);

            if (s.size() != 0) {
                fail("should have caught exception creating an invalid array");
            }
        } catch (ArrayStoreException ignored) {}

        m.remove(node.getKey());
        m.remove(node2.getKey());
        m.remove(node3.getKey());

        s.toArray(new LocalTestNode[0]);
        LocalTestNode array3[] =
            (LocalTestNode[]) s.toArray(new LocalTestNode[s.size()]);

        if (array3.length > 1) {
            LocalTestNode first = array3[0];

            for (int k = 1; k < array3.length; k++) {
                LocalTestNode second = array3[k];

                assertTrue(first.compareTo(second) < 0);

                first = second;
            }
        }

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

    private void testValuesByValue(TransactionalBidiTreeMap m) {

        Collection s = m.valuesByValue();

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

        s.toArray(new LocalTestNode[0]);
        s.toArray(new LocalTestNode[s.size()]);

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

        if (a1.length > 1) {
            Map.Entry first = (Map.Entry) a1[0];

            for (int k = 1; k < a1.length; k++) {
                Map.Entry second = (Map.Entry) a1[k];

                assertTrue(((Comparable) first.getKey())
                    .compareTo((Comparable) second.getKey()) < 0);

                first = second;
            }

            iter  = s.iterator();
            first = (Map.Entry) iter.next();

            for (; iter.hasNext(); ) {
                Map.Entry second = (Map.Entry) iter.next();

                assertTrue(((Comparable) first.getKey())
                    .compareTo((Comparable) second.getKey()) < 0);

                first = second;
            }
        }

        try {
            s.toArray(new Integer[0]);

            if (s.size() != 0) {
                fail("should have caught exception creating an invalid array");
            }
        } catch (ArrayStoreException ignored) {}

        s.toArray(new Map.Entry[0]);
        Map.Entry array3[] = (Map.Entry[]) s.toArray(new Map.Entry[s.size()]);

        if (array3.length > 1) {
            Comparable first = (Comparable) ((Map.Entry) array3[0]).getKey();

            for (int k = 1; k < array3.length; k++) {
                Comparable second =
                    (Comparable) ((Map.Entry) array3[k]).getKey();

                assertTrue(first.compareTo(second) < 0);

                first = second;
            }
        }

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

    private void testEntrySetByValue(TransactionalBidiTreeMap m) {

        Set s = m.entrySetByValue();

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

        if (a1.length > 1) {
            Map.Entry first = (Map.Entry) a1[0];

            for (int k = 1; k < a1.length; k++) {
                Map.Entry second = (Map.Entry) a1[k];

                assertTrue(((Comparable) first.getKey())
                    .compareTo((Comparable) second.getKey()) < 0);

                first = second;
            }

            iter  = s.iterator();
            first = (Map.Entry) iter.next();

            for (; iter.hasNext(); ) {
                Map.Entry second = (Map.Entry) iter.next();

                assertTrue(((Comparable) first.getKey())
                    .compareTo((Comparable) second.getKey()) < 0);

                first = second;
            }
        }

        try {
            s.toArray(new Integer[0]);

            if (s.size() != 0) {
                fail("should have caught exception creating an invalid array");
            }
        } catch (ArrayStoreException ignored) {}

        s.toArray(new Map.Entry[0]);
        Map.Entry array3[] = (Map.Entry[]) s.toArray(new Map.Entry[s.size()]);

        if (array3.length > 1) {
            Comparable first =
                (Comparable) ((Map.Entry) array3[0]).getValue();

            for (int k = 1; k < array3.length; k++) {
                Comparable second =
                    (Comparable) ((Map.Entry) array3[k]).getValue();

                assertTrue(first.compareTo(second) < 0);

                first = second;
            }
        }

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

    /* **********  END  helper methods ********** */


}