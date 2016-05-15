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
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.SortedSet;

/**
 *
 * @author dave
 */
public class SynchronizedNavigableSet<E> extends SynchronizedSortedSet<E> implements NavigableSet<E> {
    /**
     * Factory method to create a synchronized set.
     * 
     * @param set  the set to decorate, must not be null
     * @throws IllegalArgumentException if set is null
     */
    public static <E> NavigableSet<E> decorate(NavigableSet<E> set) {
        return new SynchronizedNavigableSet<E>(set);
    }

    /**
     * Constructor that wraps (not copies).
     * 
     * @param set  the set to decorate, must not be null
     * @throws IllegalArgumentException if set is null
     */
    protected SynchronizedNavigableSet(NavigableSet<E> set) {
        super(set);
    }

    /**
     * Constructor that wraps (not copies).
     * 
     * @param set  the set to decorate, must not be null
     * @param lock  the lock object to use, must not be null
     * @throws IllegalArgumentException if set is null
     */
    protected SynchronizedNavigableSet(NavigableSet<E> set, Object lock) {
        super(set, lock);
    }

    protected NavigableSet<E> getNavigableSet()
    {
        return (NavigableSet<E>)collection;
    }

    //-----------------------------------------------------------------------

    @Override
    public E lower(final E e) {
        return SyncUtils.synchronizeRead(lock, new Callback<E>() {
            @Override
            protected void doAction() {
                _return(getNavigableSet().lower(e));
            }
        });
    }

    @Override
    public E floor(final E e) {
        return SyncUtils.synchronizeRead(lock, new Callback<E>() {
            @Override
            protected void doAction() {
                _return(getNavigableSet().floor(e));
            }
        });
    }

    @Override
    public E ceiling(final E e) {
        return SyncUtils.synchronizeRead(lock, new Callback<E>() {
            @Override
            protected void doAction() {
                _return(getNavigableSet().ceiling(e));
            }
        });
    }

    @Override
    public E higher(final E e) {
        return SyncUtils.synchronizeRead(lock, new Callback<E>() {
            @Override
            protected void doAction() {
                _return(getNavigableSet().higher(e));
            }
        });
    }

    @Override
    public E pollFirst() {
        return SyncUtils.synchronizeRead(lock, new Callback<E>() {
            @Override
            protected void doAction() {
                _return(getNavigableSet().pollFirst());
            }
        });
    }

    @Override
    public E pollLast() {
        return SyncUtils.synchronizeRead(lock, new Callback<E>() {
            @Override
            protected void doAction() {
                _return(getNavigableSet().pollLast());
            }
        });
    }

    @Override
    public NavigableSet<E> descendingSet() {
        return SyncUtils.synchronizeRead(lock, new Callback<NavigableSet<E>>() {
            @Override
            protected void doAction() {
                NavigableSet<E> _set = getNavigableSet().descendingSet();
                _return(new SynchronizedNavigableSet<E>(_set, lock));
            }
        });
    }

    @Override
    public Iterator<E> descendingIterator() {
        return getNavigableSet().descendingIterator();
    }

    @Override
    public NavigableSet<E> subSet(final E fromElement, final boolean fromInclusive, final E toElement, final boolean toInclusive) {
        return SyncUtils.synchronizeRead(lock, new Callback<NavigableSet<E>>() {
            @Override
            protected void doAction() {
                NavigableSet<E> _set = getNavigableSet().subSet(fromElement, fromInclusive, toElement, toInclusive);
                _return(new SynchronizedNavigableSet<E>(_set, lock));
            }
        });
    }

    @Override
    public NavigableSet<E> headSet(final E toElement, final boolean inclusive) {
        return SyncUtils.synchronizeRead(lock, new Callback<NavigableSet<E>>() {
            @Override
            protected void doAction() {
                NavigableSet<E> _set = getNavigableSet().headSet(toElement, inclusive);
                _return(new SynchronizedNavigableSet<E>(_set, lock));
            }
        });
    }

    @Override
    public NavigableSet<E> tailSet(final E fromElement, final boolean inclusive) {
        return SyncUtils.synchronizeRead(lock, new Callback<NavigableSet<E>>() {
            @Override
            protected void doAction() {
                NavigableSet<E> _set = getNavigableSet().tailSet(fromElement, inclusive);
                _return(new SynchronizedNavigableSet<E>(_set, lock));
            }
        });
    }
}
