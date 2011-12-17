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

import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;

public class MapBuilderTest extends TestCase
{
    public void testConstructor()
    {
        // no-arg constructor should create a fresh empty map
        Map<String,Integer> result = new MapBuilder<String,Integer>().toMap();
        assertTrue(result.isEmpty());

        // constructor with map should use that map
        Map<String,Integer> original = new HashMap<String, Integer>();
        original.put("Arsenal", 3);
        original.put("Chelsea", 1);
        result = new MapBuilder<String,Integer>(original).toMap();
        assertEquals(2, result.size());
        assertEquals(3, (int)result.get("Arsenal"));
        assertEquals(1, (int)result.get("Chelsea"));
    }

    public void testPut1()
    {
        Map<String,Integer> result = new MapBuilder<String,Integer>()
            .put("oranges", 5)
            .toMap();
        assertEquals(1, result.size());
        assertEquals(5, (int)result.get("oranges"));
    }

    public void testPut2()
    {
        Map<String,Integer> result = new MapBuilder<String,Integer>()
            .put("oranges", 5)
            .put("pomegranates", 3)
            .toMap();
        assertEquals(2, result.size());
        assertEquals(5, (int)result.get("oranges"));
        assertEquals(3, (int)result.get("pomegranates"));
    }

    public void testPut3()
    {
        Map<String,Integer> result = new MapBuilder<String,Integer>()
            .put("oranges", 5)
            .put("pomegranates", 3)
            .put("kiwi fruit", 7)
            .toMap();
        assertEquals(3, result.size());
        assertEquals(5, (int)result.get("oranges"));
        assertEquals(3, (int)result.get("pomegranates"));
        assertEquals(7, (int)result.get("kiwi fruit"));
    }

    public void testPut4()
    {
        Map<String,Integer> result = new MapBuilder<String,Integer>()
            .put("oranges", 5)
            .put("pomegranates", 3)
            .put("kiwi fruit", 7)
            .put("coconuts", 2)
            .toMap();
        assertEquals(4, result.size());
        assertEquals(5, (int)result.get("oranges"));
        assertEquals(3, (int)result.get("pomegranates"));
        assertEquals(7, (int)result.get("kiwi fruit"));
        assertEquals(2, (int)result.get("coconuts"));
    }

    public void testMap1()
    {
        Map<String,Integer> result = MapBuilder.map("oranges", 5);
        assertEquals(1, result.size());
        assertEquals(5, (int)result.get("oranges"));
    }

    public void testMap2()
    {
        Map<String,Integer> result = MapBuilder.map("oranges", 5, "pomegranates", 3);
        assertEquals(2, result.size());
        assertEquals(5, (int)result.get("oranges"));
        assertEquals(3, (int)result.get("pomegranates"));
    }
}
