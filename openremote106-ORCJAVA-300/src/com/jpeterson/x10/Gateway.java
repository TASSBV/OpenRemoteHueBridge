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

public abstract interface Gateway
{
    /**
     * Bit of state that is set when a <CODE>Gateway</CODE> is in the
     * deallocated state. A deallocated gateway does not have the resources
     * necessary for it to carry out its basic functions.
     * <P>
     * In the <CODE>DEALLOCATED</CODE> state, many of the methods of a
     * <CODE>Gateway</CODE> throw an exception when called. The
     * <CODE>DEALLOCATED</CODE> state has no sub-states.
     * <P>
     * A <CODE>Gateway</CODE> is always created in the <CODE>DEALLOCATED</CODE>
     * state. A <CODE>DEALLOCATED</CODE> can transition to the
     * <CODE>ALLOCATED</CODE> state via the <CODE>ALLOCATING_RESOURCES</CODE>
     * state following a call to the <CODE>allocate</CODE> method. A
     * <CODE>Gateway</CODE> returns to the <CODE>DEALLOCATED</CODE> state via
     * the <CODE>DEALLOCATING_RESOURCES</CODE> state with a call to the
     * <CODE>deallocate</CODE> method.
     *
     * @see allocate
     * @see deallocate
     * @see getGatewayState
     * @see waitGatewayState
     */
    public static final long DEALLOCATED = 0x01;

    /**
     * Bit of state that is set when a <CODE>Gateway</CODE> is being
     * allocated - the transition state between <CODE>DEALLOCATED</CODE>
     * to <CODE>ALLOCATED</CODE> following a call to the
     * <CODE>allocate</CODE> method. The <CODE>ALLOCATING_RESOURCES</CODE>
     * state has no sub-states. In the <CODE>ALLOCATING_RESOURCES</CODE>
     * state, many of the methods of <CODE>Gateway</CODE>,
     * <CODE>Transmitter</CODE>, and <CODE>Receiver</CODE> will block
     * unitl the <CODE>Gateway</CODE> reaches the <CODE>ALLOCATED</CODE>
     * state and the action can be performed.
     *
     * @see getGatewayState
     * @see waitGatewayState
     */
    public static final long ALLOCATING_RESOURCES = 0x02;

    /**
     * Bit of state that is set when a <CODE>Gateway</CODE> is in the
     * allocated state. A gateway in the <CODE>ALLOCATED</CODE> state
     * has acquired the resources required for it to carry out its core
     * functions.
     * <P>
     * A <CODE>Gateway</CODE> is always created in the
     * <CODE>DEALLOCATED</CODE> state. It reaches the <CODE>ALLOCATED</CODE>
     * state via the <CODE>ALLOCATING_RESOURCES</CODE> state with a call to
     * the <CODE>allocate</CODE> method.
     * 
     * @see Transmitter
     * @see Receiver
     * @see getGatewayState
     * @see waitGatewayState
     */
    public static final long ALLOCATED = 0x04;

    /**
     * Bit of state that is set when a <CODE>Gateway</CODE> is being
     * deallocated - the transition state between <CODE>ALLOCATED</CODE>
     * to <CODE>DEALLOCATED</CODE>. The <CODE>DEALLOCATING_RESOURCES</CODE>
     * state has no sub-states. In the <CODE>DEALLOCATING_RESOURCE</CODE>
     * state, most methods of <CODE>Gateway</CODE>, <CODE>Transmitter</CODE>
     * and <CODE>Receiver</CODE> throw an exception.
     *
     * @see getGatewayState
     * @see waitGatewayState
     */
    public static final long DEALLOCATING_RESOURCES = 0x08;

    /**
     * Bit of state that is set when a <CODE>Gateway</CODE> is in the
     * <CODE>ALLOCATED</CODE> state and is <CODE>PAUSED</CODE>. In the
     * <CODE>PAUSED</CODE> state, event transmission or reception is stopped.
     * <P>
     * An <CODE>ALLOCATED</CODE> gateway is always in either the
     * <CODE>PAUSED</CODE> or <CODE>RESUMED</CODE> state. The
     * <CODE>PAUSED</CODE> and <CODE>RESUMED</CODE> states are sub-states of
     * the <CODE>ALLOCATED</CODE> state.
     *
     * @see RESUMED
     * @see ALLOCATED
     * @see getGatewayState
     * @see waitGatewayState
     */
    public static final long PAUSED = 0x10;

    /**
     * Bit of state that is set when a <CODE>Gateway</CODE> is in the
     * <CODE>ALLOCATED</CODE> state and is <CODE>RESUMED</CODE>. In the 
     * <CODE>RESUMED</CODE> state, event transmission or reception is active.
     * <P>
     * An <CODE>ALLOCATED</CODE> gateway is always in either the
     * <CODE>PAUSED</CODE> or <CODE>RESUMED</CODE> state. The
     * <CODE>PAUSED</CODE> and <CODE>RESUMED</CODE> states are sub-states of
     * the <CODE>ALLOCATED</CODE> state.
     *
     * @see RESUMED
     * @see ALLOCATED
     * @see getGatewayState
     * @see waitGatewayState
     */
    public static final long RESUMED = 0x20;

    /**
     * Returns a OR'ed set of flags indicating the current state of a
     * <CODE>Gateway</CODE>. The format of the returned <CODE>state</CODE>
     * value is described above.
     * <P>
     * A <CODE>GatewayEvent</CODE> is issued each time the <CODE>Gateway</CODE>
     * changes state.
     * <P>
     * The <CODE>getGatewayState</CODE> method can be called successfully
     * in any <CODE>Gateway</CODE> state.
     *
     * @see getGatewayState
     * @see waitGatewayState
     * @see getNewGatewayState
     * @see getOldGatewayState
     */
    public long getGatewayState();

    /**
     * Blocks the calling thread until the <CODE>Gateway</CODE> is in a
     * specified state. The format of the <CODE>state</CODE> value is
     * described above.
     * <P>
     * All state bits specified in the <CODE>state</CODE> parameter must be
     * set in order for the method to return, as defined for the
     * <CODE>testGatewayState</CODE> method. If the <CODE>state</CODE> parameter
     * defines an unreachable state (e.g. <CODE>ALLOCATED | DEALLOCATED</CODE>) an
     * exception is thrown.
     * <P>
     * The <CODE>waitGatewayState</CODE> method can be called successfully in
     * any <CODE>Gateway</CODE> state.
     *
     * @exception InterruptedException if another thread has interrupted this
     *            thread
     * @exception IllegalArgumentException if the specified state is
     *            unreachable
     * @see testGatewayState
     * @see getGatewayState
     */
    public void waitGatewayState(long state) throws InterruptedException,
	IllegalArgumentException;

    /**
     * Returns true if the current gateway state matches the specified state.
     * The format of the <CODE>state</CODE> value is described above.
     * <P>
     * The test performed is not an exact match to the current state. Only
     * the specified states are tested. For example the following returns
     * true only is the <CODE>Transmitter</CODE> queue is empty, irrespective
     * of the allocation states.
     * <BLOCKQUOTE>
     * <PRE>
     * if (trans.testGatewayState(Transmitter.QUEUE_EMPTY)) ...
     * </PRE>
     * </BLOCKQUOTE>
     * The <CODE>testGatewayState</CODE> method is equivalent to:
     * <BLOCKQUOTE>
     * <PRE>
     * if ((gateway.getGatewayState() & state) == state)
     * </PRE>
     * </BLOCKQUOTE>
     * The <CODE>testGatewayState</CODE> method can be called successfully
     * in any <CODE>Gateway</CODE> state.
     *
     * @exception IllegalArgumentException - if the specified state is
     *            unreachable
     */
    public boolean testGatewayState(long state) throws IllegalArgumentException;

    /**
     * Alocate the resources required for the <CODE>Gateway</CODE> and put it
     * into the <CODE>ALLOCATED</CODE> state. When this method returns
     * successfully the <CODE>ALLOCATED</CODE> bit of the gateway state is
     * set, and the <CODE>testGatewayState(Gateway.ALLOCATED)</CODE> method
     * returns <CODE>true</CODE>. During the processing of the method, the
     * <CODE>Gateway</CODE> is temporarily in the
     * <CODE>ALLOCATING_RESOURCES<CODE> state.
     * <P>
     * When the <CODE>Gateway</CODE> reaches the <CODE>ALLOCATED</CODE> state
     * other engine states are determined:
     * <UL>
     * <LI><CODE>PAUSED</CODE> or <CODE>RESUMED</CODE>: the pause state
     *     depends upon the existing state of the gateway. In a multi-app
     *     environment, the pause/resume state of the gateway is shared
     *     by all apps.</LI>
     * <LI>A <CODE>Transmitter</CODE> always starts in the
     *     <CODE>QUEUE_EMPTY</CODE> state when newly allocated<LI>
     * </UL>
     * While this method is being processed events are issued to any
     * <CODE>GatewayListeners</CODE> attached to the <CODE>Gateway</CODE>
     * to indicate state changes. First, as the <CODE>Gateway</CODE> changes
     * from the <CODE>DEALLOCATED</CODE> to the
     * <CODE>ALLOCATING_RESOURCES</CODE> state, a
     * <CODE>GATEWAY_ALLOCATING_RESOURCES</CODE> event is issued. As the
     * allocation process completes, the gateway moves from the
     * <CODE>ALLOCATING_RESOURCES</CODE> state to the <CODE>ALLOCATED</CODE>
     * state and an <CODE>GATEWAY_ALLOCATED</CODE> event is issued.
     * <P>
     * The <CODE>allocate</CODE> method should be called for a
     * <CODE>Gateway</CODE> in the <CODE>DEALLOCATED</CODE> state. The method
     * has no effect for a <CODE>Gateway</CODE> in either the
     * <CODE>ALLOCATING_RESOURCES</CODE> or <CODE>ALLOCATED</CODE> states.
     * The method throws an exception in the
     * <CODE>DEALLOCATING_RESOURCES</CODE> state.
     * <P>
     * If any problems are encountered during the allocation process so that
     * the gateway cannot be allocated, the gateway returns to the
     * <CODE>DEALLOCATED</CODE> state (with a <CODE>GATEWAY_DEALLOCATED</CODE>
     * event), and an <CODE>GatewayException</CODE> is thrown.
     * <P>
     * Allocating the resources for a gateway may be fast (less than a second)
     * or slow (several 10s of seconds) depending upon a range of factors.
     * Since the <CODE>allocate</CODE> method does not return until allocation
     * is completed applications may want to perform allocation in a background
     * thread and proceed with other activities. 
     *
     * @exception GatewayException if an allocation error occurred or the
     *            gateway is not operational.
     * @exception GatewayStateError is called for an engine in the
     *            <CODE>DEALLOCATING_RESOURCES</CODE> state
     * @see getGatewayState
     * @see deallocate
     * @see ALLOCATED
     * @see GATEWAY_ALLOCATED
     */
    public void allocate() throws GatewayException, GatewayStateError;

    /**
     * Free the resources of the gateway that were acquired during allocation
     * and during operation and return the gateway to the
     * <CODE>DEALLOCATED</CODE> state. When this method returns the
     * <CODE>DEALLOCATED</CODE> bit of gateway state is set so the
     * <CODE>testGatewayState(Gateway.DEALLOCATED)</CODE> method returns
     * <CODE>true</CODE>. During the processing of the method, the
     * <CODE>Gateway</CODE> is temporarily in the
     * <CODE>DEALLOCATING_RESOURCES</CODE> state.
     * <P>
     * A deallocated gateway can be re-started with a subsequent call to
     * <CODE>allocate</CODE>.
     * <P>
     * Gateways need to clean up current activities before being deallocated.
     * A <CODE>Transmitter</CODE> must be in the <CODE>QUEUE_EMPTY</CODE>
     * state before being deallocated. If the queue is not empty, any objects
     * on the transmit queue must be cancelled with appropriate events
     * issued.
     * <P>
     * While this method is being processed events are issued to any
     * <CODE>GatewayListeners</CODE> attached to the <CODE>Gateway</CODE>
     * to indicate state changes. First, as the <CODE>Gateway</CODE> changes
     * from the <CODE>ALLOCATED</CODE> to the
     * <CODE>DEALLOCATING_RESOURCES</CODE> state, a
     * <CODE>GATEWAY_DEALLOCATING_RESOURCES</CODE> event is issued. As the
     * deallocation process completes, the gateway moves from the
     * <CODE>DEALLOCATING_RESOURCES</CODE> state to the
     * <CODE>DEALLOCATED</CODE> state and an <CODE>GATEWAY_DEALLOCATED</CODE>
     * event is issued.
     * <P>
     * The <CODE>deallocate</CODE> method should only be called for a
     * <CODE>Gateway</CODE> in the <CODE>ALLOCATED</CODE> state. The method
     * has no effect for a <CODE>Gateway</CODE> in either the
     * <CODE>DEALLOCATING_RESOURCES</CODE> or <CODE>DEALLOCATED</CODE> states.
     * The method throws an exception in the <CODE>ALLOCATING_RESOURCES</CODE>
     * state.
     * <P>
     * Deallocating resources for a gateway is not always immediate. Since the
     * deallocate method does not return until complete, applications may want
     * to perform deallocation in a separate thread.
     *
     * @exception GatewayException if a deallocation error occurs.
     * @exception GatewayStateError if called for a gateway in the
     *            <CODE>ALLOCATING_RESOURCES</CODE> state
     * @see allocate
     * @see <CODE>GATEWAY_DEALLOCATED</CODE>
     * @see <CODE>QUEUE_EMPTY</CODE>
     */
    public void deallocate() throws GatewayException, GatewayStateError;

    /**
     * Pause the event transmission for the gateway and put the <CODE>Gateway</CODE>
     * into the <CODE>PAUSED</CODE> state. Pausing a gateway pauses the
     * underlying gateway for <I>all applications</I> that are connected to 
     * that gateway. Gateways are typically paused and resumed by request from
     * a user.
     * <P>
     * Applications may pause a gateway indefinately. When a gateway moves from
     * the <CODE>RESUMED</CODE> state to the <CODE>PAUSED</CODE> state, an
     * <CODE>GATEWAY_PAUSED</CODE> event is issued to each
     * <CODE>GatewayListener</CODE> attached to the <CODE>Gateway</CODE>. The
     * <CODE>PAUSED</CODE> bit of the gateway state is set to <CODE>true</CODE>
     * when paused, and can be tested by the <CODE>getGatewayState</CODE> method
     * and other gateway state methods.
     * <P>
     * The <CODE>PAUSED</CODE> state is a sub-state of the
     * <CODE>ALLOCATED</CODE> state. An <CODE>ALLOCATED Gateway</CODE> is always
     * in either the <CODE>PAUSED</CODE> or the <CODE>RESUMED</CODE> state.
     * <P>
     * It is not an exception to pause a <CODE>Gateway</CODE> that is already
     * paused.
     * <P>
     * The <CODE>pause</CODE> method oeprates as defined for gateways in the
     * <CODE>ALLOCATED</CODE> state. When pause is called for a gateway in the
     * <CODE>ALLOCATING_RESOURCES</CODE> state, the method blocks (waits)
     * until the <CODE>ALLOCATED</CODE> state is reached and then operates
     * normally. An error is thrown when pause is called for a gateway in
     * either the <CODE>DEALLOCATED</CODE> or
     * <CODE>DEALLOCATING_RESOURCES</CODE> states.
     * <P>
     * The <CODE>pause</CODE> method does not always return immediately. Some
     * application need to execute pause in a separate thread.
     * <P>
     *
     * @exception GatewayStateError if called for a gateway in the
     *            <CODE>DEALLOCATED</CODE> or
     *            <CODE>DEALLOCATING_RESOURCES</CODE> states
     * @see resume
     * @see getGatewayState
     * @see <CODE>GATEWAY_PAUSED</CODE>
     */
    public void pause() throws GatewayStateError;

    /**
     * Put the <CODE>Gateway</CODE> in the <CODE>RESUMED</CODE> state to resume
     * event transmission or reception for a paused gateway. Resuming a gateway resumes
     * the underlying gateway for <I>all applications</I> that are connected
     * to that gateway. Gateways are typically paused and resumed by request from
     * a user.
     * <P>
     * The specific pause/resume bejavior of tramistters and receivers
     * is defined in the documentation for the <CODE>pause</CODE> method.
     * <P>
     * When a gateway moves from the <CODE>PAUSED</CODE> state to the 
     * <CODE>RESUMED</CODE> state, a <CODE>GATEWAY_RESUMED</CODE> event
     * is issued to each <CODE>GatewayListener</CODE> attached to the
     * <CODE>Gateway</CODE>. The <CODE>RESUMED</CODE> bit of the gateway state
     * is set to <CODE>true</CODE> when resumed, and can be tested by the
     * <CODE>getGatewayState</CODE> method and other gateway state methods.
     * <P>
     * The <CODE>RESUMED</CODE> state is a sub-state of the
     * <CODE>ALLOCATED</CODE> state. An <CODE>ALLOCATED Gateway</CODE> is
     * always in either the <CODE>PAUSED</CODE> or the <CODE>RESUMED</CODE>
     * state.
     * <P>
     * It is not an exception to resume a <CODE>gateway</CODE> that is already
     * in the <CODE>RESUMED</CODE> state.
     * <P>
     * The <CODE>resume</CODE> method operates as defined for gateways in the
     * <CODE>ALLOCATED</CODE> state. When resume is called for a gateway in
     * the <CODE>ALLOCATING_RESOURCES</CODE> state, the method blocks (waits)
     * until the <CODE>ALLOCATED</CODE> state is reached and then operates
     * normally. An error is thrown when resume is called for a gateway in
     * either the <CODE>DEALLOCATED</CODE> or
     * <CODE>DEALLOCATING_RESOURCES</CODE> state.
     * <P>
     * The <CODE>resume</CODE> method does not always return immediately. Some
     * applications need to execute resume in a separate thread.
     *
     * @exception GatewayStateError if called for a gateway in the 
     *            <CODE>DEALLOCATED</CODE> or
     *            <CODE>DEALLOCATING_RESOURCES</CODE> states.
     * @see pause
     * @see getGatewayState
     * @see <CODE>GATEWAY_RESUMED</CODE>
     */
    public void resume() throws GatewayStateError;

    /**
     * Request notifications of events related to the <CODE>Gateway</CODE>. An
     * application can attache multiple listeners to a <CODE>Gateway</CODE>.
     * A single listener can be attached to multiple gateways.
     * <P>
     * The <CODE>GatewayListener</CODE> is extended for both transmission and
     * reception. Typically, a <CODE>ReceptionListener</CODE> is attached
     * to a <CODE>Receiver</CODE> and a <CODE>TransmissionListener</CODE>
     * is attached to a <CODE>Transmitter</CODE>.
     * <P>
     * A <CODE>GatewayListener</CODE> can be attached or removed in any state
     * of a <CODE>Gateway</CODE>.
     *
     * @param listener the listener that will reveive <CODE>GatewayEvents</CODE>
     * @see Receiver
     * @see ReceptionListener
     * @see Transmitter
     * @see TransmissionListener
     */
    public void addGatewayListener(GatewayListener listener);

    /**
     * Remove a listener from this <CODE>Gateway</CODE>. A
     * <CODE>GatewayListener</CODE> can be attached or removed in any state of
     * a <CODE>Gateway</CODE>.
     *
     * @param listener the listener to be removed
     */
    public void removeGatewayListener(GatewayListener listener);
}
