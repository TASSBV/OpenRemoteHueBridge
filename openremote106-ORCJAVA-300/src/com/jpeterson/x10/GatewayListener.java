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

import java.util.EventListener;

/**
 * Interface defining methods to be called when state-change events for a
 * gateway occur. To receive gateway events an application attaches
 * a listener by calling the <CODE>addGatewayListener</CODE> method of an
 * <CODE>Gateway</CODE>. A listener is removed by a call to the
 * <CODE>removeGatewayListener</CODE> method.
 * <P>
 * The event dispatch policy is defined in the documentation for the
 * <CODE>ControlEvent</CODE> class.
 * <P>
 * This interface is extended by the <CODE>ReceiverListener</CODE> and
 * <CODE>TransmitterListener</CODE> interfaces to handle the specialized
 * events of transmitters and receivers.
 * <P>
 * A trivial implementation of <CODE>GatewayListener</CODE> is provided by
 * the <CODE>GatewayAdapter</CODE> class. <CODE>TransmitterAdapter</CODE> and
 * <CODE>ReceiverAdapter</CODE> classes are also available.
 *
 * @see ControlEvent
 * @see GatewayAdapter
 * @see TransmitterListener
 * @see TransmitterAdapter
 *
 * @author Jesse Peterson <6/29/99>
 * @version 1.0
 */
public abstract interface GatewayListener extends EventListener
{
    /**
     * The <CODE>Gateway</CODE> has been paused.
     *
     * @see <CODE>GATEWAY_PAUSED</CODE>
     */
    public void gatewayPaused(GatewayEvent e);

    /**
     * The <CODE>Gateway</CODE> has been resumed.
     *
     * @see <CODE>GATEWAY_RESUMED</CODE>
     */
    public void gatewayResumed(GatewayEvent e);

    /**
     * The <CODE>Gateway</CODE> has been allocated.
     *
     * @see <CODE>GATEWAY_ALLOCATED</CODE>
     */
    public void gatewayAllocated(GatewayEvent e);

    /**
     * The <CODE>Gateway</CODE> has been deallocated.
     *
     * @see <CODE>GATEWAY_DEALLOCATED</CODE>
     */
    public void gatewayDeallocated(GatewayEvent e);

    /**
     * The <CODE>Gateway</CODE> is being allocated.
     *
     * @see <CODE>GATEWAY_ALLOCATING_RESOURCES</CODE>
     */
    public void gatewayAllocatingResources(GatewayEvent e);

    /**
     * The <CODE>Gateway</CODE> is being deallocated.
     *
     * @see <CODE>GATEWAY_DEALLOCATING_RESOURCES</CODE>
     */
    public void gatewayDeallocatingResources(GatewayEvent e);

    /**
     * An <CODE>GatewayErrorEvent</CODE> has occurred and the
     * <CODE>Gateway</CODE> is unable to continue normal operation.
     *
     * @see GatewayErrorEvent
     */
    public void gatewayError(GatewayErrorEvent e);
}
