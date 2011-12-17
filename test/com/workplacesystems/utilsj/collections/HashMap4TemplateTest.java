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

public class HashMap4TemplateTest extends TestCase {

    HashMap4Template map = new HashMap4Template()
    {
        @Override
        protected Object create(Object key1, Object key2, Object key3, Object key4) {
            return "" + key1 + key2 + key3 + key4;
        }
    };

    public void test()
    {
        // ensure 4-level keys are counted towards size
        map.put("key1a", "key2a", "key3a", "key4a", "marzipancake");
        map.put("key1a", "key2a", "key3a", "key4b", "pecanpie");
        assertEquals(2, map.size());
        assertEquals("marzipancake", map.getOrCreate("key1a", "key2a", "key3a", "key4a"));
        assertEquals("pecanpie", map.getOrCreate("key1a", "key2a", "key3a", "key4b"));
        // size should remain the same
        assertEquals(2, map.size());

        // ensure new items created on demand
        map.getOrCreate("key1b", "key2a", "key3a", "key4a");
        map.getOrCreate("key1b", "key2b", "key3a", "key4a");
        assertEquals(4, map.size());
        assertEquals("key1b" + "key2a" + "key3a" + "key4a", map.getOrCreate("key1b", "key2a", "key3a", "key4a"));
        assertEquals("key1b" + "key2b" + "key3a" + "key4a", map.getOrCreate("key1b", "key2b", "key3a", "key4a"));

        // ensure existing keys get their values replaced
        map.put("key1a", "key2a", "key3a", "key4a", "cakemarzipan");
        assertEquals(4, map.size());

        // check values
        assertEquals(4, map.values().size());
        assertEquals("cakemarzipan", map.get("key1a", "key2a", "key3a", "key4a"));
        assertEquals("pecanpie",     map.get("key1a", "key2a", "key3a", "key4b"));
        assertEquals("key1b" + "key2a" + "key3a" + "key4a", map.get("key1b", "key2a", "key3a", "key4a"));
        assertEquals("key1b" + "key2b" + "key3a" + "key4a", map.get("key1b", "key2b", "key3a", "key4a"));
    }
}
