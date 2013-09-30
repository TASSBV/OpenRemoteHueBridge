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

import java.util.Map;

import org.openremote.controller.command.ExecutableCommand;
import org.apache.log4j.Logger;

import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.component.EnumSensorType;


/**
 * TODO
 * 
 * @author Jerome Velociter
 * @author <a href="mailto:juha@openremote.org>Juha Lindfors</a>
 * @author Dan 2009-4-20
 */
public class X10Command implements ExecutableCommand, StatusCommand
{

  // Class Members --------------------------------------------------------------------------------

  private final static Logger log = Logger.getLogger(X10CommandBuilder.X10_LOG_CATEGORY);


  // Private Instance Fields ----------------------------------------------------------------------

  private String address;
   
  private X10CommandType commandType;

  private X10ControllerManager controllerManager;



  // Constructors ---------------------------------------------------------------------------------

  public X10Command(X10ControllerManager manager, String address, X10CommandType commandType)
  {
     this.controllerManager = manager;
     this.address = address;
     this.commandType = commandType;
  }


  // Implements ExecutableCommand -----------------------------------------------------------------

  @Override public void send()
  {
    X10Controller device;

    try
    {
      device = this.controllerManager.getDevice();

      device.send(address, commandType);

    }
    catch (ConnectionException e)
    {
        log.error(e);
    }
  }

  // Implements StatusCommand ---------------------------------------------------------------------

  @Override public String read(EnumSensorType sensoryType, Map<String, String> statusMap)
  {
    // TODO Auto-generated method stub
    return null;
  }
   
   
}
