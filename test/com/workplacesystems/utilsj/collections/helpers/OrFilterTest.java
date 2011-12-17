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

import com.workplacesystems.utilsj.collections.Filter;
import com.workplacesystems.utilsj.collections.FilterFactory;
import junit.framework.TestCase;

public class OrFilterTest extends TestCase
{
    public void testAll()
    {
        Filter all = FilterFactory.getAcceptAll();
        Filter none = FilterFactory.getRejectAll();
        
        assertTrue((new OrFilter(all, none)).isValid(""));
        assertTrue((new OrFilter(all, all, none)).isValid(""));
        assertTrue((new OrFilter(none, all)).isValid(""));
        assertTrue((new OrFilter(all, none, all)).isValid(""));
        assertTrue((new OrFilter(none, all, all)).isValid(""));
        
        assertFalse((new OrFilter(none, none)).isValid(""));
        assertFalse((new OrFilter(none, none, none)).isValid(""));
        assertFalse((new OrFilter(none, none, none, none)).isValid(""));
    }
}
