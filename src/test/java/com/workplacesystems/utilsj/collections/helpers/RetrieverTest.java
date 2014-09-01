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

public class RetrieverTest extends TestCase
{
    public void testRetriever()
    {
        // some lists to iterate
        FilterableArrayList empty_list = new FilterableArrayList();
        FilterableArrayList list = new FilterableArrayList(Arrays.asList(
                new Integer[]{new Integer(1), new Integer(2)})
        );

        // ensure nothing retrieved from an empty list
        Integer retrieved = (Integer) new Retriever().iterate(empty_list);
        assertNull(retrieved);
        
        // ensure first element retrieved from a populated list
        retrieved = (Integer) new Retriever().iterate(list);
        assertEquals(1, retrieved.intValue());
    }
}
