/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.openremote.controller.protocol.lutron.model;

import org.openremote.controller.protocol.lutron.LutronHomeWorksAddress;
import org.openremote.controller.protocol.lutron.LutronHomeWorksCommandBuilder;
import org.openremote.controller.protocol.lutron.LutronHomeWorksGateway;
import org.openremote.controller.utils.Logger;

/**
 * Represents a dimmer device on the Lutron bus.
 * 
 * @author <a href="mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class Dimmer extends HomeWorksDevice {

  /**
   * Lutron logger. Uses a common category for all Lutron related logging.
   */
  private final static Logger log = Logger.getLogger(LutronHomeWorksCommandBuilder.LUTRON_LOG_CATEGORY);

	// Private Instance Fields ----------------------------------------------------------------------

	/**
	 * Current level, as reported by the system. Null if we don't have this info.
	 */
	private Integer level;
	
	// Constructors ---------------------------------------------------------------------------------

	public Dimmer(LutronHomeWorksGateway gateway, LutronHomeWorksAddress address) {
		super(gateway, address);
	}
	
	// Command methods ------------------------------------------------------------------------------

	/**
	 * Starts raising the value of the dimmer
	 */
	public void raise() {
		this.gateway.sendCommand("RAISEDIM", address, null); 
	}
	
  /**
   * Starts lowering the value of the dimmer
   */
	public void lower() {
		this.gateway.sendCommand("LOWERDIM", address, null); 
	}

  /**
   * Stops raising or lowering the value of the dimmer
   */
	public void stop() {
		this.gateway.sendCommand("STOPDIM", address, null); 
	}
	
	/**
	 * Immediately sets the value of the dimmer to the given value
	 * 
	 * @param level Level to set the dimmer to, expressed in %
	 */
	public void fade(Integer level) {
		this.gateway.sendCommand("FADEDIM, " + level + ", 1, 0", address, null);
	}
	
	/**
	 * Requests level of dimmer from Lutron processor.
	 */
	public void queryLevel() {
	  this.gateway.sendCommand("RDL", address, null);
	}

	// Feedback method from HomeWorksDevice ---------------------------------------------------------

	@Override
	public void processUpdate(String info) {
		// Value reported as float, we're using integer precision
	  try {
	    level = (int)Float.parseFloat(info);
	  } catch (NumberFormatException e) {
	    // Not understood as a level, do not update ourself
	    log.warn("Invalid feedback received " + info, e);
	  }

	    
     super.processUpdate(info);
  }

	// Getters/Setters ------------------------------------------------------------------------------
	
  /**
   * Returns the currently known level of the dimmer
   * @return current level
   */
	public Integer getLevel() {
		return level;
	}

}
