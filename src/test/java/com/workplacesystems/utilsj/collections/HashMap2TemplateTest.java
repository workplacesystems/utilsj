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

public class HashMap2TemplateTest extends TestCase {

	HashMap2Template map = new HashMap2Template()
	{
		@Override
        protected Object create(Object key1, Object key2) {
			return "" + key1 + key2;
		}
	};

	public void test()
	{
		// ensure two-level keys are counted towards size
		map.put( "key1a", "key2a", "old_key1akey2a" );
		map.put( "key1a", "key2b", "key1akey2b" );
		assertEquals( 2, map.size() );
        assertEquals("old_key1akey2a", map.getOrCreate("key1a", "key2a"));
        assertEquals("key1akey2b", map.getOrCreate("key1a", "key2b"));

		// ensure new items created on demand
		map.getOrCreate( "key1b", "key2a" );
		map.getOrCreate( "key1b", "key2b" );
		assertEquals( 4, map.size() );

		// ensure existing keys get their values replaced
		map.put( "key1a", "key2a", "key1akey2a" );
		map.getOrCreate( "key1b", "key2b" );
		assertEquals( 4, map.size() );

		// ensure conversions to Collection preserve size and type
		assertEquals( 4, map.values().size() );
		assertEquals( 4, map.values().size() );
		assertEquals( String.class, map.values().get(0).getClass() );
		
		assertEquals( "key1akey2a",  map.get("key1a","key2a") );
		assertEquals( "key1akey2b", map.get("key1a","key2b") );
		assertEquals( "key1bkey2a", map.get("key1b","key2a") );
		assertEquals( "key1bkey2b", map.get("key1b","key2b") );
	}
}
