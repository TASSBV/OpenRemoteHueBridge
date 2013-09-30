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
 * Represents a GRAFIK Eye component on the Lutron bus.
 * This class sends command to the HomeWorks processor for action.
 * It also listen for feedback from the processor and keeps its state up to date.
 * 
 * @author <a href="mailto:eric@openremote.org">Eric Bariaux</a>
 *
 */
public class GrafikEye extends HomeWorksDevice {

  /**
   * Lutron logger. Uses a common category for all Lutron related logging.
   */
  private final static Logger log = Logger.getLogger(LutronHomeWorksCommandBuilder.LUTRON_LOG_CATEGORY);

	// Private Instance Fields ----------------------------------------------------------------------

	/**
	 * Currently selected scene, as reported by the system. Null if we don't have this info.
	 */
	private Integer selectedScene;
	
	// Constructors ---------------------------------------------------------------------------------

	public GrafikEye(LutronHomeWorksGateway gateway, LutronHomeWorksAddress address) {
		super(gateway, address);
	}
	
	// Command methods ------------------------------------------------------------------------------

	/**
	 * Selects the requested local scene on the GRAFIK Eye.
	 * 
	 * @param scene scene to select
	 */
	public void selectScene(Integer scene) {
		this.gateway.sendCommand("GSS", address, Integer.toString(scene)); 
	}
	
	/**
	 * Requests currently selected local scene on the GRAFIK Eye
	 */
	public void queryScene() {
	  this.gateway.sendCommand("RGS", address, null);
	}
	
	// Feedback method from HomeWorksDevice ---------------------------------------------------------

	@Override
	public void processUpdate(String info) {
	  try {
	    selectedScene = Integer.parseInt(info);
	  } catch (NumberFormatException e) {
	    // Not understood as a scene, do not update ourself
	    log.warn("Invalid feedback received " + info, e);
	  }
	    
     super.processUpdate(info);
	}

	// Getters/Setters ------------------------------------------------------------------------------

	public Integer getSelectedScene() {
		return selectedScene;
	}

}
