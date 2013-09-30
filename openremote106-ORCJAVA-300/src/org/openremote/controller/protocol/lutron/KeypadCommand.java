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
package org.openremote.controller.protocol.lutron;

import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.sensor.SwitchSensor;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.protocol.lutron.model.HomeWorksDevice;
import org.openremote.controller.protocol.lutron.model.Keypad;
import org.openremote.controller.utils.Logger;

/**
 * 
 * @author <a href="mailto:eric@openremote.org">Eric Bariaux</a>
 *
 */
public class KeypadCommand extends LutronHomeWorksCommand implements ExecutableCommand, EventListener {

  // Class Members --------------------------------------------------------------------------------

  /**
   * Lutron logger. Uses a common category for all Lutron related logging.
   */
  private final static Logger log = Logger.getLogger(LutronHomeWorksCommandBuilder.LUTRON_LOG_CATEGORY);

  public static LutronHomeWorksCommand createCommand(String name, LutronHomeWorksGateway gateway, LutronHomeWorksAddress address, Integer scene, Integer key, Integer level) {
    // Check for mandatory attributes
    if (address == null) {
      throw new NoSuchCommandException("Address is required for any Keypad command");
    }

    if (key == null) {
      throw new NoSuchCommandException("Key is required for any Keypad command");
    }

    if (!address.isValidKeypadAddress()) {
      throw new NoSuchCommandException("Address must be one of a Keypad");
    }

    return new KeypadCommand(name, gateway, address, key);
  }

  // Private Instance Fields ----------------------------------------------------------------------

  /**
   * Destination address for this command.
   */
  private LutronHomeWorksAddress address;

  /**
   * Number of key on the keypad this command must act upon.
   */
  private Integer key;

  // Constructors ---------------------------------------------------------------------------------

  public KeypadCommand(String name, LutronHomeWorksGateway gateway, LutronHomeWorksAddress address, Integer key) {
    super(name, gateway);
    this.address = address;
    this.key = key;
  }

  // Implements ExecutableCommand -----------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  public void send() {
    try {
      Keypad keypad = (Keypad) gateway.getHomeWorksDevice(address, Keypad.class);
      log.info("Will execute command for keypad " + keypad);
      if ("PRESS".equals(name)) {
        keypad.press(key);
      } else if ("RELEASE".equals(name)) {
        keypad.release(key);
      } else if ("HOLD".equals(name)) {
        keypad.hold(key);
      } else if ("DOUBLE_TAP".equals(name)) {
        keypad.doubleTap(key);
      }
    } catch (LutronHomeWorksDeviceException e) {
      log.error("Impossible to get device", e);
    }
  }

  // Implements EventListener -------------------------------------------------------------------

  @Override
  public void setSensor(Sensor sensor) {
      if (sensors.isEmpty()) {
         // First sensor registered, we also need to register ourself with the device
         try {
            Keypad keypad = (Keypad) gateway.getHomeWorksDevice(address, Keypad.class);
            if (keypad == null) {
               // This should never happen as above command is supposed to create device
               log.warn("Gateway could not create a Keypad we're receiving feedback for (" + address + ")");
               return;
            }

            // Register ourself with the Keypad so it can propagate update when received
            keypad.addCommand(this);
            addSensor(sensor);

            // Trigger a query to get the initial value
            keypad.queryLedStatus();
         } catch (LutronHomeWorksDeviceException e) {
            log.error("Impossible to get device", e);
         }
      } else {
         addSensor(sensor);
      }
  }
  
  @Override
  public void stop(Sensor sensor) {
     removeSensor(sensor);
     if (sensors.isEmpty()) {
        // Last sensor removed, we may unregister ourself from device
        try {
           Keypad keypad = (Keypad) gateway.getHomeWorksDevice(address, Keypad.class);
           if (keypad == null) {
              // This should never happen as above command is supposed to create device
              log.warn("Gateway could not create a Keypad we're receiving feedback for (" + address + ")");
              return;
           }

           keypad.removeCommand(this);
        } catch (LutronHomeWorksDeviceException e) {
           log.error("Impossible to get device", e);
        }
     }
  }

  @Override
  protected void updateSensor(HomeWorksDevice device, Sensor sensor) {
     Keypad keypad = (Keypad)device;
     if (sensor instanceof SwitchSensor) {
        sensor.update((keypad.getLedStatuses()[key - 1] == 1) ? "on" : "off");
     } else {
        log.warn("Query Keypad status for incompatible sensor type (" + sensor + ")");
     }
  }

}
