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

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Comparator;

import junit.framework.TestCase;

public class ReverseOrderTest extends TestCase
{
    public void testIt()
    {
        Comparator c = createStrictMock(Comparator.class);
        expect(c.compare(anyObject(), anyObject())).andReturn(0);
        expect(c.compare(anyObject(), anyObject())).andReturn(-1);
        expect(c.compare(anyObject(), anyObject())).andReturn(1);
        replay(c);
        
        ReverseOrder reverse = new ReverseOrder(c);
        
        assertEquals(0, reverse.compare(null, null));
        assertEquals(1, reverse.compare(null, null));
        assertEquals(-1, reverse.compare(null, null));
        
        verify(c);
    }
}
