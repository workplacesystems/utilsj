/*
 * Copyright 2016 Workplace Systems PLC (http://www.workplacesystems.com/).
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

package com.workplacesystems.utilsj.collections.decorators;

import com.workplacesystems.utilsj.Callback;
import com.workplacesystems.utilsj.collections.SyncUtils;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.SortedMap;

/**
 *
 * @author dave
 */
public class SynchronizedNavigableMap<K,V> extends SynchronizedSortedMap<K,V> implements NavigableMap<K,V> {
    /**
     * Factory method to create a synchronized map.
     * 
     * @param map  the map to decorate, must not be null
     * @throws IllegalArgumentException if map is null
     */
    public static <K,V> NavigableMap<K,V> decorate(NavigableMap<K,V> map) {
        return new SynchronizedNavigableMap<K,V>(map);
    }

    /**
     * Constructor that wraps (not copies).
     * 
     * @param map  the map to decorate, must not be null
     * @throws IllegalArgumentException if map is null
     */
    protected SynchronizedNavigableMap(SortedMap<K,V> map) {
        super(map);
    }

    /**
     * Constructor that wraps (not copies).
     * 
     * @param map  the map to decorate, must not be null
     * @param lock  the lock object to use, must not be null
     * @throws IllegalArgumentException if map is null
     */
    protected SynchronizedNavigableMap(SortedMap<K,V> map, Object lock) {
        super(map, lock);
    }

    protected NavigableMap<K,V> getNavigableMap()
    {
        return (NavigableMap<K,V>)map;
    }

    //-----------------------------------------------------------------------

    @Override
    public Entry<K, V> lowerEntry(final K key) {
        return SyncUtils.synchronizeRead(lock, new Callback<Entry<K, V>>() {
            @Override
            protected void doAction() {
                _return(getNavigableMap().lowerEntry(key));
            }
        });
    }

    @Override
    public K lowerKey(final K key) {
        return SyncUtils.synchronizeRead(lock, new Callback<K>() {
            @Override
            protected void doAction() {
                _return(getNavigableMap().lowerKey(key));
            }
        });
    }

    @Override
    public Entry<K, V> floorEntry(final K key) {
        return SyncUtils.synchronizeRead(lock, new Callback<Entry<K, V>>() {
            @Override
            protected void doAction() {
                _return(getNavigableMap().floorEntry(key));
            }
        });
    }

    @Override
    public K floorKey(final K key) {
        return SyncUtils.synchronizeRead(lock, new Callback<K>() {
            @Override
            protected void doAction() {
                _return(getNavigableMap().floorKey(key));
            }
        });
    }

    @Override
    public Entry<K, V> ceilingEntry(final K key) {
        return SyncUtils.synchronizeRead(lock, new Callback<Entry<K, V>>() {
            @Override
            protected void doAction() {
                _return(getNavigableMap().ceilingEntry(key));
            }
        });
    }

    @Override
    public K ceilingKey(final K key) {
        return SyncUtils.synchronizeRead(lock, new Callback<K>() {
            @Override
            protected void doAction() {
                _return(getNavigableMap().ceilingKey(key));
            }
        });
    }

    @Override
    public Entry<K, V> higherEntry(final K key) {
        return SyncUtils.synchronizeRead(lock, new Callback<Entry<K, V>>() {
            @Override
            protected void doAction() {
                _return(getNavigableMap().higherEntry(key));
            }
        });
    }

    @Override
    public K higherKey(final K key) {
        return SyncUtils.synchronizeRead(lock, new Callback<K>() {
            @Override
            protected void doAction() {
                _return(getNavigableMap().higherKey(key));
            }
        });
    }

    @Override
    public Entry<K, V> firstEntry() {
        return SyncUtils.synchronizeRead(lock, new Callback<Entry<K, V>>() {
            @Override
            protected void doAction() {
                _return(getNavigableMap().firstEntry());
            }
        });
    }

    @Override
    public Entry<K, V> lastEntry() {
        return SyncUtils.synchronizeRead(lock, new Callback<Entry<K, V>>() {
            @Override
            protected void doAction() {
                _return(getNavigableMap().lastEntry());
            }
        });
    }

    @Override
    public Entry<K, V> pollFirstEntry() {
        return SyncUtils.synchronizeRead(lock, new Callback<Entry<K, V>>() {
            @Override
            protected void doAction() {
                _return(getNavigableMap().pollFirstEntry());
            }
        });
    }

    @Override
    public Entry<K, V> pollLastEntry() {
        return SyncUtils.synchronizeRead(lock, new Callback<Entry<K, V>>() {
            @Override
            protected void doAction() {
                _return(getNavigableMap().pollLastEntry());
            }
        });
    }

    @Override
    public NavigableMap<K, V> descendingMap() {
        return SyncUtils.synchronizeRead(lock, new Callback<NavigableMap<K,V>>() {
            @Override
            protected void doAction() {
                NavigableMap<K,V> _map = getNavigableMap().descendingMap();
                _return(new SynchronizedNavigableMap<K,V>(_map, lock));
            }
        });
    }

    @Override
    public NavigableSet<K> navigableKeySet() {
        return SyncUtils.synchronizeRead(lock, new Callback<NavigableSet<K>>() {
            @Override
            protected void doAction() {
                NavigableSet<K> _set = getNavigableMap().navigableKeySet();
                _return(new SynchronizedNavigableSet<K>(_set, lock));
            }
        });
    }

    @Override
    public NavigableSet<K> descendingKeySet() {
        return SyncUtils.synchronizeRead(lock, new Callback<NavigableSet<K>>() {
            @Override
            protected void doAction() {
                NavigableSet<K> _set = getNavigableMap().descendingKeySet();
                _return(new SynchronizedNavigableSet<K>(_set, lock));
            }
        });
    }

    @Override
    public NavigableMap<K, V> subMap(final K fromKey, final boolean fromInclusive, final K toKey, final boolean toInclusive) {
        return SyncUtils.synchronizeRead(lock, new Callback<NavigableMap<K,V>>() {
            @Override
            protected void doAction() {
                NavigableMap<K,V> _map = getNavigableMap().subMap(fromKey, fromInclusive, toKey, toInclusive);
                _return(new SynchronizedNavigableMap<K,V>(_map, lock));
            }
        });
    }

    @Override
    public NavigableMap<K, V> headMap(final K toKey, final boolean inclusive) {
        return SyncUtils.synchronizeRead(lock, new Callback<NavigableMap<K,V>>() {
            @Override
            protected void doAction() {
                NavigableMap<K,V> _map = getNavigableMap().headMap(toKey, inclusive);
                _return(new SynchronizedNavigableMap<K,V>(_map, lock));
            }
        });
    }

    @Override
    public NavigableMap<K, V> tailMap(final K fromKey, final boolean inclusive) {
        return SyncUtils.synchronizeRead(lock, new Callback<NavigableMap<K,V>>() {
            @Override
            protected void doAction() {
                NavigableMap<K,V> _map = getNavigableMap().tailMap(fromKey, inclusive);
                _return(new SynchronizedNavigableMap<K,V>(_map, lock));
            }
        });
    }
}
