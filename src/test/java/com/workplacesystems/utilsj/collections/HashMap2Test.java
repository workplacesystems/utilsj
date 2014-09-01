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

import junit.framework.TestCase;

public class HashMap2Test extends TestCase {

	public void test()
	{
	    HashMap2 map = new HashMap2();
		// ensure two-level keys are counted towards size
		map.put( "key1a", "key2a", "old_key1akey2a" );
		map.put( "key1a", "key2b", "key1akey2b" );
		assertEquals( 2, map.size() );

		// ensure absence can be detected
		assertFalse( map.containsKey("key1b","key2a") );
		assertFalse( map.containsKey("key1b","key2b") );

		// ensure existing keys get their values replaced
		Object old = map.put( "key1a", "key2a", "key1akey2a" );
		assertEquals( 2, map.size() );
		assertEquals( "old_key1akey2a", (String)old );
		assertEquals( "key1akey2a", map.get("key1a","key2a") );

		// ensure conversions to Collection preserve size and type
		assertEquals( 2, map.size() );
		assertEquals( 2, map.values().size() );
		assertEquals( String.class, map.values().get(0).getClass() );
		
		assertEquals( "key1akey2a",  map.get("key1a","key2a") );
		assertEquals( "key1akey2b", map.get("key1a","key2b") );
        
        // Ensure keys don't overwrite
        
        map.put("INTERVAL_WORKINTERVAL_START", "INTERVAL_WORKINTERVAL_END", "A");
        map.put("INTERVAL_BREAKINTERVAL_START", "INTERVAL_BREAKINTERVAL_END", "B");
        
        assertEquals("A", map.get("INTERVAL_WORKINTERVAL_START", "INTERVAL_WORKINTERVAL_END"));
        assertEquals("B", map.get("INTERVAL_BREAKINTERVAL_START", "INTERVAL_BREAKINTERVAL_END"));
        
    }
	
	public void testEquals()
	{
	    HashMap2 map1 = new HashMap2();
	    HashMap2 map2 = new HashMap2();
	    assertTrue(map1.equals(map2));
	    assertTrue(map2.equals(map1));
	    
	    map1.put("key1", "key2", "value");
	    assertFalse(map1.equals(map2));
	    assertFalse(map2.equals(map1));
	    
        map2.put("key1", "key2", "value");
        assertTrue(map1.equals(map2));
        assertTrue(map2.equals(map1));
        
        map1.put("key1", "key2A", "value");
        assertFalse(map1.equals(map2));
        assertFalse(map2.equals(map1));

        map2.put("key2", "key2A", "value");
        assertFalse(map1.equals(map2));
        assertFalse(map2.equals(map1));

        map1.put("key2", "key2A", "value");
        map2.put("key1", "key2A", "value");
        assertTrue(map1.equals(map2));
        assertTrue(map2.equals(map1));

        map1.put("key3", "key3", "value");
        map2.put("key1", "key3", "value");
        assertFalse(map1.equals(map2));
        assertFalse(map2.equals(map1));

        map2.put("key3", "key3", "value");
        map1.put("key1", "key3", "value");
        assertTrue(map1.equals(map2));
        assertTrue(map2.equals(map1));

        map1.put("key4", "key4", "valueA");
        map2.put("key4", "key4", "valueB");
        assertFalse(map1.equals(map2));
        assertFalse(map2.equals(map1));

        map1.put("key4", "key4", "valueB");
        assertTrue(map1.equals(map2));
        assertTrue(map2.equals(map1));
        
	}
}
