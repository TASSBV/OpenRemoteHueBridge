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

/**
 * Trivial implementation of the <CODE>GatewayListener</CODE> interface that
 * receives a <CODE>GatewayEvent</CODE>. The methods in this class are empty,
 * this class is provided as a convenience for easily creating listeners by
 * extending this class and overriding only the methods of interest.
 *
 * @see TransmitterAdapter
 *
 * @author Jesse Peterson <6/29/99>
 * @version 1.0
 */
public class GatewayAdapter extends Object implements GatewayListener
{
    /**
     * Constructor does nothing.
     *
     */
    public GatewayAdapter()
    {
	// empty
    }

    /**
     * The <CODE>Gateway</CODE> has been paused.
     *
     * @see <CODE>GATEWAY_PAUSED</CODE>
     *
     * @author Jesse Peterson <6/29/99>
     */
    public void gatewayPaused(GatewayEvent e)
    {
	// empty
    }

    /**
     * The <CODE>Gateway</CODE> has been resumed.
     *
     * @see <CODE>GATEWAY_RESUMED</CODE>
     *
     * @author Jesse Peterson <6/29/99>
     */
    public void gatewayResumed(GatewayEvent e)
    {
	// empty
    }

    /**
     * The <CODE>Gateway</CODE> has been allocated.
     *
     * @see <CODE>GATEWAY_ALLOCATED</CODE>
     *
     * @author Jesse Peterson <6/29/99>
     */
    public void gatewayAllocated(GatewayEvent e)
    {
	// empty
    }

    /**
     * The <CODE>Gateway</CODE> has been deallocated.
     *
     * @see <CODE>GATEWAY_DEALLOCATED</CODE>
     *
     * @author Jesse Peterson <6/29/99>
     */
    public void gatewayDeallocated(GatewayEvent e)
    {
	// empty
    }

    /**
     * The <CODE>Gateway</CODE> is being allocated.
     *
     * @see <CODE>GATEWAY_ALLOCATING_RESOURCES</CODE>
     *
     * @author Jesse Peterson <6/29/99>
     */
    public void gatewayAllocatingResources(GatewayEvent e)
    {
	// empty
    }

    /**
     * The <CODE>Gateway</CODE> is being deallocated.
     *
     * @see <CODE>GATEWAY_DEALLOCATING_RESOURCES</CODE>
     *
     * @author Jesse Peterson <6/29/99>
     */
    public void gatewayDeallocatingResources(GatewayEvent e)
    {
	// empty
    }

    /**
     * A <CODE>GatewayErrorEvent</CODE> has occurred and the
     * <CODE>Gateway</CODE> is unable to continue normal operation.
     *
     * @author Jesse Peterson <6/29/99>
     */
    public void gatewayError(GatewayErrorEvent e)
    {
	// empty
    }
}
