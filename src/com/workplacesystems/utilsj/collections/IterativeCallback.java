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

import java.util.Iterator;
import com.workplacesystems.utilsj.Callback;

import com.workplacesystems.utilsj.UtilsjException;
import com.workplacesystems.utilsj.collections.decorators.SynchronizedFilterableCollection;

/** IterativeCallback provides a means of threadsafe iteration on collections of records.
 *
 * @author  Dave
 */
abstract public class IterativeCallback<T,R>
{    
    private IterativeState<T,R> state;

    /** Creates a new instance of IterativeCallback */
    public IterativeCallback()
    {
        initState();
    }
 
    /** Creates a new instance of IterativeCallback, accepting an object that may be amended throughout the iteration process */
    public IterativeCallback(R amended_object) 
    {
        this();
        state.amended_object = amended_object;
    }

    private void initState()
    {
        // Only create a new state if one doesn't exist already.
        // newState and setState can be used if more state control is required.
        if (state == null)
            newState();
    }

    /**
     * Creates a new state for this IterativeCallback.
     * 
     * @return previous state
     */
    public IterativeState<T,R> newState()
    {
        return setState(new IterativeState<T,R>());
    }

    /**
     * Sets the supplied state for this IterativeCallback.
     * 
     * @return previous state
     */
    public IterativeState<T,R> setState(IterativeState<T,R> new_state)
    {
        IterativeState<T,R> old_state = this.state;
        this.state = new_state;
        return old_state;
    }

    /** public wrapper for the iteration */
    public R iterate(final FilterableCollection<? extends T> c)
    {
        initState();

        checkUsed();

        // If collection is decorated with a syncronized wrapper then synchronize the iteration
        if (c instanceof SynchronizedFilterableCollection)
        {
            return SyncUtils.synchronizeRead(c, new Callback<R>() {
                @Override
                protected void doAction()
                {
                    _return(doIteration(c.iterator()));
                }
            });
        }
        return doIteration(c.iterator());
    }

    /**
     * Find out if callback has been used.
     * @return true if used, false if not
     */
    protected boolean hasBeenUsed() { return state.iterative_callback_used; }

    /** check callback used only once */
    protected final void checkUsed()
    {
        if (state.iterative_callback_used)
            throw new UtilsjException("IterativeCallback can not be used more than once.");
        state.iterative_callback_used = true;
    }

    /** do the actual iteration */
    private R doIteration(Iterator<? extends T> it)
    {
        // save the iterator into member variable
        state.i = it;
        state.iterations = 0;
        
        if (state.do_break == true)
            return state.return_object;

        // do the iteration calling nextobject on each
        while (state.i.hasNext())
        {
            T o = state.i.next();

            if (delegate != null)
                delegate.delegate(o);
            else
                iterateObject(o);

            if (state.do_break == true)
                return state.return_object;
        }
        return state.amended_object;
    }

    void iterateObject(T o)
    {
        if (state.iterations == 0)
            firstObject(o);
        else
            nextObject(o);
        state.iterations++;
    }

    protected void firstObject(T o)
    {
        nextObject(o);
    }

    protected final boolean hasNext()
    {
        return state.i.hasNext();
    }
    
    /** allows remove to be called on the iterator */
    public void remove()
    {
        state.i.remove();
    }

    /** called by each iteration step */
    protected abstract void nextObject(T obj);

    protected void _return(R o)
    {
        state.return_object = o;
        state.do_break = true;
    }
    
    protected void _returnNotNull(R o)
    {
        if (o != null)
        {
            state.return_object = o;
            state.do_break = true;
        }
    }
    
    protected void _break()
    {
        state.do_break = true;
    }
    
    protected void setAmendedObject(R o)
    {
        state.amended_object = o;
    }
    
    protected R getAmendedObject()
    {
        return state.amended_object;
    }

    private IterativeDelegate<T> delegate;

    public void addDelegation(IterativeDelegate<T> delegate)
    {
        delegate.setIterativeCallback(this);

        if (this.delegate == null)
            this.delegate = delegate;
        else
            this.delegate.setNextDelegation(delegate);
    }    
}
