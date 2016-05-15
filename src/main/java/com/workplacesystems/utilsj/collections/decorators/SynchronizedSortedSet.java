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
import java.util.SortedSet;

/**
 *
 * @author dave
 */
public class SynchronizedSortedSet<E> extends SynchronizedSet<E> implements SortedSet<E> {
    /**
     * Factory method to create a synchronized set.
     * 
     * @param set  the set to decorate, must not be null
     * @throws IllegalArgumentException if set is null
     */
    public static <E> SortedSet<E> decorate(SortedSet<E> set) {
        return new SynchronizedSortedSet<E>(set);
    }

    /**
     * Constructor that wraps (not copies).
     * 
     * @param set  the set to decorate, must not be null
     * @throws IllegalArgumentException if set is null
     */
    protected SynchronizedSortedSet(SortedSet<E> set) {
        super(set);
    }

    /**
     * Constructor that wraps (not copies).
     * 
     * @param set  the set to decorate, must not be null
     * @param lock  the lock object to use, must not be null
     * @throws IllegalArgumentException if set is null
     */
    protected SynchronizedSortedSet(SortedSet<E> set, Object lock) {
        super(set, lock);
    }

    protected SortedSet<E> getSortedSet()
    {
        return (SortedSet<E>)collection;
    }

    //-----------------------------------------------------------------------

    @Override
    public SortedSet<E> subSet(final E fromElement, final E toElement) {
        return SyncUtils.synchronizeRead(lock, new Callback<SortedSet<E>>() {
            @Override
            protected void doAction() {
                SortedSet<E> _set = getSortedSet().subSet(fromElement, toElement);
                _return(new SynchronizedSortedSet<E>(_set, lock));
            }
        });
    }

    @Override
    public SortedSet<E> headSet(final E toElement) {
        return SyncUtils.synchronizeRead(lock, new Callback<SortedSet<E>>() {
            @Override
            protected void doAction() {
                SortedSet<E> _set = getSortedSet().headSet(toElement);
                _return(new SynchronizedSortedSet<E>(_set, lock));
            }
        });
    }

    @Override
    public SortedSet<E> tailSet(final E fromElement) {
        return SyncUtils.synchronizeRead(lock, new Callback<SortedSet<E>>() {
            @Override
            protected void doAction() {
                SortedSet<E> _set = getSortedSet().tailSet(fromElement);
                _return(new SynchronizedSortedSet<E>(_set, lock));
            }
        });
    }

    @Override
    public Comparator<? super E> comparator() {
        return SyncUtils.synchronizeRead(lock, new Callback<Comparator<? super E>>() {
            @Override
            protected void doAction() {
                _return(getSortedSet().comparator());
            }
        });
    }

    @Override
    public E first() {
        return SyncUtils.synchronizeRead(lock, new Callback<E>() {
            @Override
            protected void doAction() {
                _return(getSortedSet().first());
            }
        });
    }

    @Override
    public E last() {
        return SyncUtils.synchronizeRead(lock, new Callback<E>() {
            @Override
            protected void doAction() {
                _return(getSortedSet().last());
            }
        });
    }
}
