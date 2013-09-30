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
package org.openremote.controller.protocol.virtual;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.component.EnumSensorType;
import org.openremote.controller.utils.Logger;

/**
 * OpenRemote virtual command implementation.  <p>
 *
 * Maintains a virtual-machine-wide state for each address. This can be used for testing.
 * Incoming read() and send() requests operate on a map where the command's {@code <address>}
 * field is used as a key and either the command name, or command parameter (if provided) is
 * stored as a virtual device state value for that address.  <p>
 *
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class VirtualCommand implements ExecutableCommand, StatusCommand
{

  // Class Members --------------------------------------------------------------------------------

  /**
   * Map of address to state.
   */
  private final static Map<String, String>virtualDevices = new ConcurrentHashMap<String, String>(20);

  /**
   * Logging. Use common log category for all related classes.
   */
  private final static Logger log = Logger.getLogger(VirtualCommandBuilder.LOG_CATEGORY);




  // Instance Fields ------------------------------------------------------------------------------

  /**
   * The address for this particular 'command' instance.
   */
  private String address = null;

  /**
   * The command string for this particular 'command' instance.
   */
  private String command = null;

  /**
   * TODO
   */
  private String commandParam = null;



  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a new "virtual" device command with a given address and command. <p>
   *
   * When this command's {@link #send()} method is called, the command name is stored as a
   * virtual device state value for the given device address.
   *
   * @param address
   *            arbitrary address string that is used to store the command value in memory
   *
   * @param command
   *            arbitrary command string that is stored in memory and can be later retrieved
   *            via invoking {@link #read}.
   */
  public VirtualCommand(String address, String command)
  {
    this.address = address;
    this.command = command;
  }

  /**
   * Constructs a new "virtual" device command with a given address, command, and command parameter. <p>
   *
   * When this command's {@link #send()} method is called, the command's parameter value is stored
   * as a virtual device state value for the given address. This type of command can therefore
   * be used to test components such as sliders that send values as command parameters.
   *
   * @param address       arbitrary address string that is used to store the command parameter in
   *                      memory
   * @param command       command name TODO (not used)
   * @param commandParam  command parameter value -- stored as a virtual device state value in
   *                      memory and can be later retrieved using this command's {@link #read}
   *                      method.
   */
  public VirtualCommand(String address, String command, String commandParam)
  {
    this(address, command);

    this.commandParam = commandParam;
  }


  // Implements ExecutableCommand -----------------------------------------------------------------

  /**
   * Allows this command implementation to be used with components that require write command
   * implementations.  <p>
   *
   * Stores the command name (or command parameter, if provided) as a "virtual" device state
   * value when a send() is triggered. The state is stored and indexed by this command's address. <p>
   *
   * If command parameter has been provided (such as slider value) then that is stored as device
   * state instead of command's name.
   */
  @Override public void send()
  {
    if (commandParam == null)
    {
      virtualDevices.put(address, command);
    }

    else
    {
      virtualDevices.put(address, commandParam);
    }
  }



  // Implements StatusCommand ---------------------------------------------------------------------

  /**
   * Allows this command implementation to be used as read commands for sensors. <p>
   *
   * Stored values are looked up using address string as key on read() requests. Device state
   * values can be stored for any given address using {@link #send} method of this class. <p>
   *
   * If the sensor associated with this command is of 'switch' type, the stored value should
   * match 'on' or 'off' ('off' will be returned if the stored value cannot be translated or
   * if no value has been stored for this command's address yet). <p>
   *
   * If the sensor associated with this command is of 'level' type, the stored value should
   * be an integer string within range of [0-100]. String '0' will be returned if the stored
   * device value for this address cannot be translated to integer or if no value has been
   * stored for this command's addres yet. <p>
   *
   * Range behaves the same as 'level' but allows arbitrary integer to be stored. <p>
   *
   * Sensor type 'custom' allows arbitrary string values to be returned from this implementation.
   *
   *
   * @param sensorType
   *          type of the sensor that is associated to this command: 'switch', 'level'
   *          'range', 'custom', etc.
   *
   * @param sensorProperties
   *          additional properties passed by the sensor to this read command
   *          implementation, if any
   *
   *
   * @return    the value stored for this command's address (in memory) or a default value based
   *            on the associated sensor type, if no value has been stored yet in memory
   */
  @Override public String read(EnumSensorType sensorType, Map<String, String> sensorProperties)
  {
    String state = virtualDevices.get(address);

    switch (sensorType)
    {

      case SWITCH:

        if (state == null)
        {
          return "off";
        }

        else if (state.trim().equalsIgnoreCase("on"))
        {
          return "on";
        }

        else if (state.trim().equalsIgnoreCase("off"))
        {
          return "off";
        }

        else
        {
          log.warn(
              "Was expecting either 'on' or 'off' for 'switch' type sensor, got ''{0}''.", state
          );

          return "off";
        }


      case LEVEL:

        if (state == null)
        {
          return "0";
        }

        else
        {
          try
          {
            int value = Integer.parseInt(state.trim());

            if (value > 100)
            {
              return "100";
            }

            if (value < 0)
            {
              return "0";
            }

            return "" + value;
          }
          catch (NumberFormatException e)
          {
            log.warn("Can't parse LEVEL sensor value into a valid number: {0}", e, e.getMessage());

            return "0";
          }
        }


      case RANGE:

        if (state == null)
        {
          return "0";
        }

        else
        {
          try
          {
            int value = Integer.parseInt(state.trim());

            return "" + value;
          }

          catch (NumberFormatException e)
          {
            log.warn("Can't parse RANGE sensor value into a valid number: {0}", e, e.getMessage());

            return "0";
          }
        }


      case CUSTOM:

        return (state == null) ? "" : state;


      default:

        throw new Error(
            "Unrecognized sensor type '" + sensorType + "'. Virtual command implementation must " +
            "be updated to support this sensor type."
        );
    }
  }
}

