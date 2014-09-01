/*
 * Copyright 2012 Workplace Systems PLC (http://www.workplacesystems.com/).
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

import com.workplacesystems.utilsj.UtilsjException;
import com.workplacesystems.utilsj.collections.FilterableArrayList;
import com.workplacesystems.utilsj.collections.helpers.HasLessThan;

import java.util.Arrays;
import junit.framework.TestCase;

/**
 *
 * @author dave
 */
public class HasLessThanTest extends TestCase {

    public void testHasLessThan() {
        // some lists to iterate
        FilterableArrayList empty_list = new FilterableArrayList();
        FilterableArrayList list = new FilterableArrayList(Arrays.asList(
                new Integer[]{new Integer(1), new Integer(2)})
        );

        HasLessThan<Integer> max = new HasLessThan<Integer>(2);
        max.iterate(list);
        assertFalse(max.hasLess());
        assertEquals(0, max.getRemaining());

        max = new HasLessThan<Integer>(2);
        max.iterate(empty_list);
        assertTrue(max.hasLess());
        assertEquals(2, max.getRemaining());

        max = new HasLessThan<Integer>(2);
        max = max.iterate(list);
        HasLessThan<Integer> max2 = max.iterate(empty_list);
        // HasLessThan reached total therefore returned object should be the same
        assertTrue(max == max2);
        assertFalse(max.hasLess());
        assertEquals(0, max.getRemaining());

        max = new HasLessThan<Integer>(2);
        max.iterate(list);
        try
        {
            // Use same copy of HasLessThan a second time
            max.iterate(list);
            fail();
        }
        catch (UtilsjException uje) {}

        max = new HasLessThan<Integer>(2);
        try
        {
            // Check hasLess without iterating a collection
            max.hasLess();
            fail();
        }
        catch (UtilsjException uje) {}
    }
}
