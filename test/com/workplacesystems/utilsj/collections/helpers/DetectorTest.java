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

import java.util.Arrays;

import com.workplacesystems.utilsj.collections.FilterableArrayList;
import junit.framework.TestCase;

public class DetectorTest extends TestCase
{
    public void testDetector()
    {
        // some lists to iterate
        FilterableArrayList empty_list = new FilterableArrayList();
        FilterableArrayList list = new FilterableArrayList(Arrays.asList(
                new Integer[]{new Integer(1), new Integer(2)})
        );

        // ensure nothing detected in an empty list
        Boolean detected = (Boolean) new Detector().iterate(empty_list);
        assertFalse(detected.booleanValue());
        
        // ensure something detected in a populated list
        detected = (Boolean) new Detector().iterate(list);
        assertTrue(detected.booleanValue());
    }
}
