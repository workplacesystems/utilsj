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

public class HashMap3TemplateTest extends TestCase {

    HashMap3Template map = new HashMap3Template()
    {
        @Override
        protected Object create(Object key1, Object key2, Object key3) {
            return "" + key1 + key2 + key3;
        }
    };

    public void test()
    {
        // ensure two-level keys are counted towards size
        map.put("key1a", "key2a", "key3a", "marzipancake");
        map.put("key1a", "key2a", "key3b", "pecanpie");
        assertEquals(2, map.size());
        assertEquals("marzipancake", map.getOrCreate("key1a", "key2a", "key3a"));
        assertEquals("pecanpie", map.getOrCreate("key1a", "key2a", "key3b"));
        // size should remain the same
        assertEquals(2, map.size());

        // ensure new items created on demand
        map.getOrCreate("key1b", "key2a", "key3a");
        map.getOrCreate("key1b", "key2b", "key3a");
        assertEquals(4, map.size());
        assertEquals("key1b" + "key2a" + "key3a", map.getOrCreate("key1b", "key2a", "key3a"));
        assertEquals("key1b" + "key2b" + "key3a", map.getOrCreate("key1b", "key2b", "key3a"));

        // ensure existing keys get their values replaced
        map.put("key1a", "key2a", "key3a", "cakemarzipan");
        assertEquals(4, map.size());

        // ensure conversions to Collection preserve size and type
        assertEquals(4, map.values().size());
        assertEquals(String.class, map.values().get(0).getClass());
        
        assertEquals("cakemarzipan", map.get("key1a", "key2a", "key3a"));
        assertEquals("pecanpie",     map.get("key1a", "key2a", "key3b"));
        assertEquals("key1b" + "key2a" + "key3a", map.get("key1b", "key2a", "key3a"));
        assertEquals("key1b" + "key2b" + "key3a", map.get("key1b", "key2b", "key3a"));
    }
}
