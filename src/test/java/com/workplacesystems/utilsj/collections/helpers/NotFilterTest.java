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

import junit.framework.TestCase;
import com.workplacesystems.utilsj.collections.Filter;

/**
 * Test to invert the behavior of a given filter.
 */
public class NotFilterTest extends TestCase
{
    public void testExcludeFilter()
    {
        Filter not_filter_1 = new NotFilter(
                new Filter() {
                    public boolean isValid(Object obj)
                    {
                        return true;
                    }
                });

        Filter not_filter_2 = new NotFilter(
                new Filter() {
                    public boolean isValid(Object obj)
                    {
                        return false;
                    }
                });

       assertFalse(not_filter_1.isValid(new Object()));
    
       assertTrue(not_filter_2.isValid(new Object()));
    }
}
