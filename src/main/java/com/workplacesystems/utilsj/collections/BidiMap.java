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

import java.util.Map;

/**
 *
 * @author  Dave
 */
public interface BidiMap<K,V> extends Map<K,V>
{
    K getKeyForValue(final Object value);

    K removeValue(final Object value);

    FilterableSet<K> keySetByValue();
    
    FilterableCollection<V> valuesByValue();
    
    FilterableCollection<V> valuesByValueDescending();
    
    FilterableSet<Map.Entry<K,V>> entrySetByValue();

    FilterableSet<Map.Entry<K,V>> entrySetByValueDescending();
}
