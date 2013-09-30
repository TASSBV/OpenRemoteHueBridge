/*
 * Copyright (C) 1999  Jesse E. Peterson
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 *
 */

package com.jpeterson.x10;

import java.util.Vector;

/**
 * Base GatewayImpl.  Must be subclassed to do something useful.  By
 * default, a NullGatewayQueue is registered as the GatewayQueue.  If you
 * require event handling synchronized with the AWT event queue, you can
 * manually register an AWTGatewayQueue.
 */
public abstract class GatewayImpl extends Object
        implements Gateway, ControlEventDispatcher
{
    private GatewayState gatewayState;
    protected GatewayQueue gatewayQueue;

    protected Vector<GatewayListener> gatewayListeners = new Vector<GatewayListener>();

    protected GatewayImpl()
    {
        gatewayState = new GatewayState();

        // Assign a default gateway queue.
        setGatewayQueue(new NullGatewayQueue(this));

        // initial state
        setGatewayState(Gateway.DEALLOCATED);
    }

    /**
     * Returns a or'ed set of flags indicating the current state of
     * a Gateway.
     * <P>
     * A GatewayEvent is issued each time the Gateway changes state.
     * <P>
     * The getGatewayState method can be called successfully in any Gateway
     * state.
     *
     * @author Jesse Peterson <6/29/99>
     */
    public long getGatewayState()
    {
        return(gatewayState.getState());
    }

    /**
     * Assign a new gateway state.  This method works with waitGatewayState()
     * to correctly perform blocking while waiting for specified gateway
     *  states.
     *
     * @author Jesse Peterson <6/29/99>
     */
    protected void setGatewayState(long state)
    {
        synchronized (gatewayState)
        {
            gatewayState.setState(state);
            gatewayState.notifyAll();
        }
    }

    /**
     * Blocks the calling thread until the Gateway is in a specified state.
     * <P>
     * All state bits specified in the state parameter must be set in order
     * for the method to return, as defined for the testGatewayState method.
     * If the state parameter defines an unreachable state (e.g. ALLOCATED |
     * DEALLOCATED) an exception is thrown.
     * <P>
     * The waitGatewayState method can be called successfully in any
     * Gateway state.
     *
     * @exception InterruptedException if another thread has interrupted this
     *            thread.
     * @exception IllegalArgumentException if the specified state is
     *            unreachable
     *
     * @author Jesse Peterson <6/29/99>
     */
    public void waitGatewayState(long state) throws InterruptedException,
	IllegalArgumentException
    {
        synchronized (gatewayState)
        {
            while (!testGatewayState(state))
            {
                gatewayState.wait();
            }
        }
    }

    /**
     * Performs state transitions for Gateway.ALLOCATED,
     * Gateway.ALLOCATING_RESOURCES, Gateway.DEALLOCATED,
     * Gateway.DEALLOCATING_RESOURCES, Gateway.PAUSED, Gateway.RESUMED.
     * This method fires a GatewayEvent for valid state transitions.
     *
     * @author Jesse Peterson <6/29/99>
     */
    protected void stateTransition(long state)
    {
        int id = 0;
        long oldGatewayState, newGatewayState;
        oldGatewayState = getGatewayState();
        // initialize new gateway state to the old state
        newGatewayState = oldGatewayState;

        if (Gateway.ALLOCATED == state)
        {
            id = GatewayEvent.GATEWAY_ALLOCATED;
            newGatewayState &= ~(Gateway.ALLOCATING_RESOURCES |
				                 Gateway.DEALLOCATED |
                                 Gateway.DEALLOCATING_RESOURCES);
            newGatewayState |= state;
        }
        else if (Gateway.ALLOCATING_RESOURCES == state)
        {
            id = GatewayEvent.GATEWAY_ALLOCATING_RESOURCES;
            newGatewayState &= ~(Gateway.ALLOCATED |
				                 Gateway.DEALLOCATED |
                                 Gateway.DEALLOCATING_RESOURCES);
            newGatewayState |= state;
        }
        else if (Gateway.DEALLOCATED == state)
        {
            id = GatewayEvent.GATEWAY_DEALLOCATED;
            newGatewayState &= ~(Gateway.ALLOCATED |
				                 Gateway.ALLOCATING_RESOURCES |
                                 Gateway.DEALLOCATING_RESOURCES);
            newGatewayState |= state;
        }
        else if (Gateway.DEALLOCATING_RESOURCES == state)
        {
            id = GatewayEvent.GATEWAY_DEALLOCATING_RESOURCES;
            newGatewayState &= ~(Gateway.ALLOCATED |
				                 Gateway.ALLOCATING_RESOURCES |
                                 Gateway.DEALLOCATED);
            newGatewayState |= state;
        }
        else if (Gateway.PAUSED == state)
        {
            id = GatewayEvent.GATEWAY_PAUSED;
            newGatewayState &= ~Gateway.RESUMED;
            newGatewayState |= state;
        }
        else if (Gateway.RESUMED == state)
        {
            id = GatewayEvent.GATEWAY_RESUMED;
            newGatewayState &= ~Gateway.PAUSED;
            newGatewayState |= state;
        }

        if (newGatewayState != oldGatewayState)
        {
            setGatewayState(newGatewayState);
            fireControlEvent(new GatewayEvent(this, id, oldGatewayState,
				             newGatewayState));
        }
    }

    /**
     * Returns true if the current gateway state matches the specified state.
     * <P>
     * The test performed is not an exact match to the current state. Only
     * the specified states are tested. For example the following returns
     * true only if the Transmitter queue is empty, irrespective of the
     * pause/resume and allocation states.
     * <P>
     * <CODE>if (transmitter.testGatewayState(Transmitter.QUEUE_EMPTY)) ...</CODE>
     * <P>
     * The testGatewayState method is equivalent to:
     * <P>
     * <CODE>if ((gateway.getGatewayState() & state) == state)</CODE>
     * <P>
     * The testGatewayState method can be called successfully in any
     * Gateway state.
     *
     * @exception IllegalArgumentException if the specified state is
     *            unreachable
     *
     * @author Jesse Peterson <6/29/99>
     */
    public boolean testGatewayState(long state) throws IllegalArgumentException
    {
        long currentState = getGatewayState();
        // TODO: Make sure "state" is a valid state

        return((currentState & state) == state);
    }

    /**
     * Allocate a gateway.
     *
     * @author Jesse Peterson <6/29/99>
     */
    public void allocate() throws GatewayException, GatewayStateError
    {
        // If deallocating resouces, throw an error
        if (testGatewayState(Gateway.DEALLOCATING_RESOURCES))
        {
            throw new GatewayStateError("Gateway can not be allocated.  It is currently in the DEALLOCATING_RESOURCES state.");
        }

        // If already being allocated or allocated, just return
        if (testGatewayState(Gateway.ALLOCATED | Gateway.ALLOCATING_RESOURCES))
        {
            // already allocating
            return;
        }

        // set the new state
        stateTransition(Gateway.ALLOCATING_RESOURCES);

        // Put actual allocation stuff here

        // allocated
        setGatewayState(Gateway.RESUMED);  // clears out state
        stateTransition(Gateway.ALLOCATED);
    }

    /**
     * Deallocate a gateway.
     *
     * @author Jesse Peterson <6/29/99>
     */
    public void deallocate() throws GatewayException, GatewayStateError   
    {
        // If allocating resouces, throw an error
        if (testGatewayState(Gateway.ALLOCATING_RESOURCES))
        {
            throw new GatewayStateError("Gateway can not be deallocated.  It is currently in the ALLOCATING_RESOURCES state.");
        }

        // If already being deallocated or deallocated, just return
        if (testGatewayState(Gateway.DEALLOCATED |
			                 Gateway.DEALLOCATING_RESOURCES))
        {
            // already deallocating
            return;
        }

        // set the new state
        stateTransition(Gateway.DEALLOCATING_RESOURCES);

        // Put actual deallocation stuff here

        // deallocated
        stateTransition(Gateway.DEALLOCATED);
    }

    /**
     * Pause a gateway.
     *
     * @author Jesse Peterson <6/29/99>
     */
    public void pause() throws GatewayStateError
    {
        // If allocating resouces, throw an error
        if (testGatewayState(Gateway.DEALLOCATING_RESOURCES |
                             Gateway.DEALLOCATED))
        {
            throw new GatewayStateError("Gateway can not be paused.  It is currently in the DEALLOCATING_RESOURCES or DEALLOCATED state.");
        }

        try
        {
            waitGatewayState(Gateway.ALLOCATED);
        }
        catch (InterruptedException e)
        {
            throw new GatewayStateError("Caught InterruptedException while waiting for the gateway to enter an ALLOCATED state:  " + e.getMessage());
        }
        catch (IllegalArgumentException e)
        {
            throw new GatewayStateError("Caught IllegalArgumentException while waiting for the gateway to enter an ALLOCATED state:  " + e.getMessage());
        }

        if (testGatewayState(Gateway.PAUSED))
        {
            // already paused
            return;
        }

        // Put actual pause stuff here

        stateTransition(Gateway.PAUSED);
    }

    /**
     * Resume a gateway.
     *
     * @author Jesse Peterson <6/29/99>
     */
    public void resume() throws GatewayStateError
    {
        // If deallocating resouces, throw an error
        if (testGatewayState(Gateway.DEALLOCATING_RESOURCES |
                             Gateway.DEALLOCATED))
        {
            throw new GatewayStateError("Gateway can not be resumed.  It is currently in the DEALLOCATING_RESOURCES or DEALLOCATED state.");
        }

        try
        {
            waitGatewayState(Gateway.ALLOCATED);
        }
        catch (InterruptedException e)
        {
            throw new GatewayStateError("Caught InterruptedException while waiting for the gateway to enter an ALLOCATED state:  " + e.getMessage());
        }
        catch (IllegalArgumentException e)
        {
            throw new GatewayStateError("Caught IllegalArgumentException while waiting for the gateway to enter an ALLOCATED state:  " + e.getMessage());
        }

        if (testGatewayState(Gateway.RESUMED))
        {
            // already paused
            return;
        }

        // Put actual resume implementation here

        stateTransition(Gateway.RESUMED);
    }

    /**
     * Add a gateway listener.
     *
     * @author Jesse Peterson <6/29/99>
     */
    public void addGatewayListener(GatewayListener listener)
    {
        // add a listener if it is not already registered
        if (!gatewayListeners.contains(listener))
        {
            gatewayListeners.addElement(listener);
        }
    }

    /**
     * Remove a gateway listener.
     *
     * @author Jesse Peterson <6/29/99>
     */
    public void removeGatewayListener(GatewayListener listener)
    {
        // remove it if it is registered
        if (!gatewayListeners.contains(listener))
        {
            gatewayListeners.removeElement(listener);
        }
    }

    /**
     * Set the gateway queue used to process ControlEvents sent by the method
     * <code>fireControlEvent</code>.
     *
     * @param gatewayQueue GatewayQueue assigned to handle ControlEvents.
     * @author Jesse Peterson (<a href="mailto:jesse@jpeterson.com">jesse@jpeterson.com</a>)
     */
    public void setGatewayQueue(GatewayQueue gatewayQueue)
    {
        this.gatewayQueue = gatewayQueue;
    }

    /**
     * Retrieve the gateway queue used to process ControlEvents sent by the
     * method <code>fireControlEvent</code>.
     *
     * @return The gateway queue used to process ControlEvents.
     * @author Jesse Peterson (<a href="mailto:jesse@jpeterson.com">jesse@jpeterson.com</a>)
     */
    public GatewayQueue getGatewayQueue()
    {
        return(gatewayQueue);
    }

    /**
     * Queues ControlEvent on registered GatewayQueue for subsequent dispatch
     * to registered gateway listeners.
     *
     * @see setGatewayQueue
     * @author Jesse Peterson <7/18/99>
     */
    public void fireControlEvent(ControlEvent event)
    {
        gatewayQueue.post(event);
    }

    /**
     * Method to actually send a GatewayEvent to registered listeners.
     * <P>
     * This method should be overridden by subclass implementations of
     * Transmitter and Receiver to handle their specific events.  However,
     * this method must be called to handle any events that they do not.
     * <P>
     * This method handles plane GatewayEvent and GatewayErrorEvent
     * X10Events.
     *
     * @param event the ControlEvent to be propagated to the listeners
     * @see fireControlEvent
     *
     * @author Jesse Peterson <6/29/99>
     */
    @SuppressWarnings("unchecked")
    public void dispatchControlEvent(ControlEvent event)
    {
        if (event instanceof GatewayEvent)
        {
            // make a copy of the listener object vector so that it cannot
            // be changed while we are firing event
            Vector<GatewayListener> v;
            GatewayListener client;
            synchronized(this)
            {
                v = (Vector<GatewayListener>) gatewayListeners.clone();
            }
	    
    	    // fire the event to all listeners
            int cnt = v.size();
            for (int i = 0; i < cnt; i++)
            {
                switch (event.getId())
                {
                    case GatewayEvent.GATEWAY_ALLOCATED:
                    client = (GatewayListener)v.elementAt(i);
                    client.gatewayAllocated((GatewayEvent)event);
                    break;

                    case GatewayEvent.GATEWAY_DEALLOCATED:
                    client = (GatewayListener)v.elementAt(i);
                    client.gatewayDeallocated((GatewayEvent)event);
                    break;

                    case GatewayEvent.GATEWAY_ALLOCATING_RESOURCES:
                    client = (GatewayListener)v.elementAt(i);
                    client.gatewayAllocatingResources((GatewayEvent)event);
                    break;

                    case GatewayEvent.GATEWAY_DEALLOCATING_RESOURCES:
                    client = (GatewayListener)v.elementAt(i);
                    client.gatewayDeallocatingResources((GatewayEvent)event);
                    break;

                    case GatewayEvent.GATEWAY_PAUSED:
                    client = (GatewayListener)v.elementAt(i);
                    client.gatewayPaused((GatewayEvent)event);
                    break;

                    case GatewayEvent.GATEWAY_RESUMED:
                    client = (GatewayListener)v.elementAt(i);
                    client.gatewayResumed((GatewayEvent)event);
                    break;

                    case GatewayErrorEvent.GATEWAY_ERROR:
                    if (event instanceof GatewayErrorEvent)
                    {
                        client = (GatewayListener)v.elementAt(i);
                        client.gatewayError((GatewayErrorEvent)event);
                    }
                    break;
                }
            }
        }
        else
        {
            // no where else to go... consume it
        }
    }
}
