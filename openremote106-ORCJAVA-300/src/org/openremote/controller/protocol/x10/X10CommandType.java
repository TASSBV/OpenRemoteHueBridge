/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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
package org.openremote.controller.protocol.x10;

/**
 * Enumeration of supported X10 commands that can be sent over the power line.
 * 
 * Right now supports ON, OFF and ALL UNITS OFF, ALL LIGHTS ON, BRIGHT, DIM.
 *
 * @author Jerome Velociter
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 * @author Fekete Kamosh
 */
public enum X10CommandType
{

  /**
   * Switches on the target unit
   */
  SWITCH_ON(new String[] { "ON" }, true),

  /**
   * Switches off the target unit
   */
  SWITCH_OFF(new String[] { "OFF" }, true),

  /**
   * Switches off all units within the house code
   */
  ALL_UNITS_OFF(new String[] { "ALL_OFF", "ALL_UNITS_OFF",
                               "ALL OFF", "ALL UNITS OFF" },
                               false),
  
  /**
   * Switches on all light units within the house code
   */
  ALL_LIGHTS_ON(new String[] { "ALL_LIGHTS_ON", "ALL LIGHTS ON",
                               "LIGHTS ON"},
                               false),
  
  /**
   * Dims the light unit  
   */
  DIM(new String[] {"DIM"}, true),
  
  /**
   * Brights the light unit 
   */
  BRIGHT(new String[] {"BRIGHT"}, true);


  // Enum Fields ----------------------------------------------------------------------------------

  private String[] commandTranslations = null;
  
  private boolean requiresDeviceCode = true;


  // Enum Constructors ----------------------------------------------------------------------------

  private X10CommandType(String[] commandTranslations, boolean requiresDeviceCode)
  {
    this.commandTranslations = commandTranslations;
    this.requiresDeviceCode = requiresDeviceCode;
  }


  /**
   * Does this command type require also device code along with house code? 
   */  
  public boolean requiresDeviceCode() {
	  return requiresDeviceCode;
  }

  // Enum Methods ---------------------------------------------------------------------------------

  /**
   * @return true if the passed command is equal to this command, false otherwise
   */
  boolean isEqual(String command)
  {
    for (String translation : commandTranslations)
    {
      if (translation.equalsIgnoreCase(command))
      {
        return true;
      }
    }

    return false;
  }
}
