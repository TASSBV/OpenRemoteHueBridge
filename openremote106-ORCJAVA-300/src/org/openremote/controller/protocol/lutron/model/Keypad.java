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
 * 
 * @author <a href="mailto:eric@openremote.org">Eric Bariaux</a>
 *
 */
public class Keypad extends HomeWorksDevice {

  /**
   * Lutron logger. Uses a common category for all Lutron related logging.
   */
  private final static Logger log = Logger.getLogger(LutronHomeWorksCommandBuilder.LUTRON_LOG_CATEGORY);

	// Private Instance Fields ----------------------------------------------------------------------

  /**
   * Current status of key LEDs, as reported by the system. Null if we don't have this info.
   */
  private Integer[] ledStatuses;
	
	// Constructors ---------------------------------------------------------------------------------

  public Keypad(LutronHomeWorksGateway gateway, LutronHomeWorksAddress address) {
		super(gateway, address);
	}
	
	// Command methods ------------------------------------------------------------------------------

	public void press(Integer key) {
		this.gateway.sendCommand("KBP", address, Integer.toString(key));
	}

	public void release(Integer key)
  {
		this.gateway.sendCommand("KBR", address, Integer.toString(key)); 
	}

	public void hold(Integer key)
  {
		this.gateway.sendCommand("KBH", address, Integer.toString(key)); 
	}

	public void doubleTap(Integer key)
  {
		this.gateway.sendCommand("KBDT", address, Integer.toString(key)); 
	}
	
	public void queryLedStatus() {
	  this.gateway.sendCommand("RKLS", address, null);
	}
	
	//Feedback method from HomeWorksDevice ---------------------------------------------------------

  @Override
  public void processUpdate(String info) {
    log.info("Will update keypad (" + address + ") status with string " + info);
    
    // Test we receive a 24 character string with only 0->3 values.
    if (!info.matches("^[0-3]{24}$")) {
      log.warn("Invalid feedback received " + info);
    }
    
    // Parse it, key 1 is at start of string
    if (ledStatuses == null) {
      ledStatuses = new Integer[24];
    }
    for (int i = 0; i < info.length(); i++) {
      try {
        ledStatuses[i] = Integer.parseInt(info.substring(i, i + 1));
      } catch (NumberFormatException e) {
        log.warn("Invalid feedback received " + info + ", skipping to next LED", e);
      }
    }
    
    super.processUpdate(info);
  }
  
  // Getters/Setters ------------------------------------------------------------------------------

  public Integer[] getLedStatuses() {
    return ledStatuses;
  }

}
