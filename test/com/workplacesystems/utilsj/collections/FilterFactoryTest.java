package com.workplacesystems.utilsj.collections;

import junit.framework.TestCase;

public class FilterFactoryTest extends TestCase {
    
    public void testGetDistinct() {
        Filter<Integer> filter = FilterFactory.<Integer>getDistinct();
        assertTrue(filter.isValid(1));
        assertTrue(filter.isValid(2));
        assertFalse(filter.isValid(2));
        assertFalse(filter.isValid(1));
        assertTrue(filter.isValid(3));
        
        //each returned filter should be independent
        Filter<Integer> filter2 = FilterFactory.<Integer>getDistinct();
        assertTrue(filter2.isValid(1));
        assertTrue(filter2.isValid(2));
    }
    
}
