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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import junit.framework.TestCase;

public class HashMap3Test extends TestCase
{
    private static final String LOCATION_BAR = "LOC_BAR";
    private static final String LOCATION_TILL = "LOC_TILL";
    private static final String LOCATION_CAFE = "LOC_CAFE";
    
    private static final String PAYTYPE_WORK = "PT_WORK";
    private static final String PAYTYPE_BREAK = "PT_BREAK";
    private static final String PAYTYPE_ABSENCE = "PT_ABSENCE";
    
    private static final String STORE_HOME = "STORE_HOME";
    private static final String STORE_LOAN = "STORE_LOAN";

    private final HashMap3 map = new HashMap3();

    public void testContainsKey()
    {
        map.put(LOCATION_BAR, PAYTYPE_WORK,    STORE_HOME, new Integer(1));
        map.put(LOCATION_BAR, PAYTYPE_WORK,    STORE_LOAN, new Integer(2));
        map.put(LOCATION_BAR, PAYTYPE_WORK,    STORE_HOME, new Integer(3));
        map.put(LOCATION_BAR, PAYTYPE_WORK,    STORE_LOAN, new Integer(4));
        map.put(LOCATION_BAR, PAYTYPE_ABSENCE, STORE_HOME, new Integer(5));
        map.put(LOCATION_BAR, PAYTYPE_ABSENCE, STORE_LOAN, new Integer(6));
        
        assertFalse(map.containsKey(LOCATION_BAR, PAYTYPE_BREAK, STORE_HOME));
        assertFalse(map.containsKey(LOCATION_BAR, PAYTYPE_BREAK, STORE_LOAN));
        
        assertFalse(map.containsKey(LOCATION_TILL, PAYTYPE_WORK, STORE_HOME));
        assertFalse(map.containsKey(LOCATION_TILL, PAYTYPE_WORK, STORE_LOAN));
        assertFalse(map.containsKey(LOCATION_TILL, PAYTYPE_BREAK, STORE_HOME));
        assertFalse(map.containsKey(LOCATION_TILL, PAYTYPE_BREAK, STORE_LOAN));
        assertFalse(map.containsKey(LOCATION_TILL, PAYTYPE_ABSENCE, STORE_HOME));
        assertFalse(map.containsKey(LOCATION_TILL, PAYTYPE_ABSENCE, STORE_LOAN));
        
        assertFalse(map.containsKey(LOCATION_CAFE, PAYTYPE_WORK, STORE_HOME));
        assertFalse(map.containsKey(LOCATION_CAFE, PAYTYPE_WORK, STORE_LOAN));
        assertFalse(map.containsKey(LOCATION_CAFE, PAYTYPE_BREAK, STORE_HOME));
        assertFalse(map.containsKey(LOCATION_CAFE, PAYTYPE_BREAK, STORE_LOAN));
        assertFalse(map.containsKey(LOCATION_CAFE, PAYTYPE_ABSENCE, STORE_HOME));
        assertFalse(map.containsKey(LOCATION_CAFE, PAYTYPE_ABSENCE, STORE_LOAN));

        assertTrue(map.containsKey(LOCATION_BAR, PAYTYPE_WORK,    STORE_HOME));
        assertTrue(map.containsKey(LOCATION_BAR, PAYTYPE_WORK,    STORE_LOAN));
        assertTrue(map.containsKey(LOCATION_BAR, PAYTYPE_WORK,    STORE_HOME));
        assertTrue(map.containsKey(LOCATION_BAR, PAYTYPE_WORK,    STORE_LOAN));
        assertTrue(map.containsKey(LOCATION_BAR, PAYTYPE_ABSENCE, STORE_HOME));
        assertTrue(map.containsKey(LOCATION_BAR, PAYTYPE_ABSENCE, STORE_LOAN));
    }
    
    public void testContainsValue()
    {
        map.put(LOCATION_BAR, PAYTYPE_WORK,    STORE_HOME, new Integer(1));
        map.put(LOCATION_BAR, PAYTYPE_WORK,    STORE_LOAN, new Integer(2));
        // overwrite two first ones with different values
        map.put(LOCATION_BAR, PAYTYPE_WORK,    STORE_HOME, new Integer(3));
        map.put(LOCATION_BAR, PAYTYPE_WORK,    STORE_LOAN, new Integer(4));
        map.put(LOCATION_BAR, PAYTYPE_ABSENCE, STORE_HOME, new Integer(5));
        map.put(LOCATION_BAR, PAYTYPE_ABSENCE, STORE_LOAN, new Integer(6));
        
        assertFalse(map.containsValue(new Integer(1)));
        assertFalse(map.containsValue(new Integer(2)));
        assertTrue(map.containsValue(new Integer(3)));
        assertTrue(map.containsValue(new Integer(4)));
        assertTrue(map.containsValue(new Integer(5)));
        assertTrue(map.containsValue(new Integer(6)));
        
        assertFalse(map.containsValue(new Integer(10)));
        assertFalse(map.containsValue(new Integer(12)));
    }
    
    public void testGet()
    {
        map.put(LOCATION_BAR, PAYTYPE_WORK,    STORE_HOME, new Integer(1));
        map.put(LOCATION_BAR, PAYTYPE_WORK,    STORE_LOAN, new Integer(2));
        map.put(LOCATION_BAR, PAYTYPE_BREAK,   STORE_HOME, new Integer(3));
        map.put(LOCATION_BAR, PAYTYPE_BREAK,   STORE_LOAN, new Integer(4));
        map.put(LOCATION_BAR, PAYTYPE_ABSENCE, STORE_HOME, new Integer(5));
        map.put(LOCATION_BAR, PAYTYPE_ABSENCE, STORE_LOAN, new Integer(6));
        
        assertEquals(new Integer(1), map.get(LOCATION_BAR, PAYTYPE_WORK,    STORE_HOME));
        assertEquals(new Integer(2), map.get(LOCATION_BAR, PAYTYPE_WORK,    STORE_LOAN));
        assertEquals(new Integer(3), map.get(LOCATION_BAR, PAYTYPE_BREAK,   STORE_HOME));
        assertEquals(new Integer(4), map.get(LOCATION_BAR, PAYTYPE_BREAK,   STORE_LOAN));
        assertEquals(new Integer(5), map.get(LOCATION_BAR, PAYTYPE_ABSENCE, STORE_HOME));
        assertEquals(new Integer(6), map.get(LOCATION_BAR, PAYTYPE_ABSENCE, STORE_LOAN));
    }
    
    public void testGetMap()
    {
        map.put(LOCATION_BAR,  PAYTYPE_WORK,    STORE_HOME, new Integer(1));
        map.put(LOCATION_BAR,  PAYTYPE_WORK,    STORE_LOAN, new Integer(2));
        map.put(LOCATION_TILL, PAYTYPE_WORK,    STORE_HOME, new Integer(3));
        map.put(LOCATION_TILL, PAYTYPE_WORK,    STORE_LOAN, new Integer(4));
        map.put(LOCATION_CAFE, PAYTYPE_ABSENCE, STORE_HOME, new Integer(5));
        map.put(LOCATION_CAFE, PAYTYPE_ABSENCE, STORE_LOAN, new Integer(6));

        Map2 loc_map = map.getMap(LOCATION_BAR);
        assertNotNull(loc_map);
        assertEquals(2, loc_map.size());
        assertTrue(loc_map.containsKey(PAYTYPE_WORK, STORE_HOME));
        assertTrue(loc_map.containsKey(PAYTYPE_WORK, STORE_LOAN));
        assertFalse(loc_map.containsKey(PAYTYPE_ABSENCE, STORE_LOAN));
        
        loc_map = map.getMap(LOCATION_CAFE);
        assertNotNull(loc_map);
        assertEquals(2, loc_map.size());
        assertTrue(loc_map.containsKey(PAYTYPE_ABSENCE, STORE_HOME));
        assertTrue(loc_map.containsKey(PAYTYPE_ABSENCE, STORE_LOAN));
        assertFalse(loc_map.containsKey(PAYTYPE_WORK, STORE_LOAN));
    }
    
    public void testEmpty()
    {
        assertTrue(map.isEmpty());
        
        map.put(LOCATION_BAR, PAYTYPE_WORK,    STORE_HOME, new Integer(1));
        
        assertFalse(map.isEmpty());
        
        map.remove(LOCATION_BAR, PAYTYPE_WORK,    STORE_HOME);

        assertTrue(map.isEmpty());
    }

    public void testSize()
    {
        map.put(LOCATION_BAR, PAYTYPE_WORK, STORE_HOME, new Integer(1));
        map.put(LOCATION_BAR, PAYTYPE_WORK, STORE_LOAN, new Integer(2));
        map.put(LOCATION_BAR, PAYTYPE_ABSENCE, STORE_HOME, new Integer(5));
        map.put(LOCATION_BAR, PAYTYPE_ABSENCE, STORE_LOAN, new Integer(6));
        
        assertEquals(4, map.size());
    }
    
    public void testRemove()
    {
        assertTrue(map.isEmpty());
        
        map.put(LOCATION_BAR, PAYTYPE_WORK,    STORE_HOME, new Integer(1));
        map.put(LOCATION_BAR, PAYTYPE_BREAK,   STORE_HOME, new Integer(2));
        
        assertEquals(2, map.size());
        
        Integer removed_item = (Integer) map.remove(LOCATION_BAR, PAYTYPE_BREAK, STORE_HOME);
        assertNotNull(removed_item);
        assertEquals(new Integer(2), removed_item);
        
        assertEquals(1, map.size());
        
        removed_item = (Integer) map.remove(LOCATION_BAR, PAYTYPE_BREAK, STORE_HOME);
        assertNull(removed_item);
    }
    
    public void testValues()
    {
        map.put(LOCATION_BAR,  PAYTYPE_WORK,    STORE_HOME, new Integer(1));
        map.put(LOCATION_BAR,  PAYTYPE_WORK,    STORE_LOAN, new Integer(2));
        map.put(LOCATION_TILL, PAYTYPE_WORK,    STORE_HOME, new Integer(3));
        map.put(LOCATION_TILL, PAYTYPE_WORK,    STORE_LOAN, new Integer(4));
        map.put(LOCATION_BAR,  PAYTYPE_ABSENCE, STORE_HOME, new Integer(5));
        map.put(LOCATION_BAR,  PAYTYPE_ABSENCE, STORE_LOAN, new Integer(6));
        
        List values = map.values();
        assertNotNull(values);
        Collections.sort(values);
        assertEquals(6, values.size());
        Iterator it = values.iterator();
        assertEquals(new Integer(1), it.next());
        assertEquals(new Integer(2), it.next());
        assertEquals(new Integer(3), it.next());
        assertEquals(new Integer(4), it.next());
        assertEquals(new Integer(5), it.next());
        assertEquals(new Integer(6), it.next());
    }
    
    public void testKey1Set()
    {
        map.put(LOCATION_BAR, PAYTYPE_WORK,    STORE_HOME, new Integer(1));
        map.put(LOCATION_BAR, PAYTYPE_WORK,    STORE_LOAN, new Integer(2));
        map.put(LOCATION_BAR, PAYTYPE_WORK,    STORE_HOME, new Integer(3));
        map.put(LOCATION_TILL, PAYTYPE_WORK,    STORE_LOAN, new Integer(4));
        map.put(LOCATION_TILL, PAYTYPE_ABSENCE, STORE_HOME, new Integer(5));
        map.put(LOCATION_BAR, PAYTYPE_ABSENCE, STORE_LOAN, new Integer(6));
        
        Set set = map.key1Set();
        assertTrue(set.contains(LOCATION_TILL));
        assertTrue(set.contains(LOCATION_BAR));
        assertFalse(set.contains(LOCATION_CAFE));
    }
}
