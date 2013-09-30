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
import org.openremote.controller.component.LevelSensor;
import org.openremote.controller.component.RangeSensor;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.sensor.SwitchSensor;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.protocol.lutron.model.Dimmer;
import org.openremote.controller.protocol.lutron.model.HomeWorksDevice;
import org.openremote.controller.utils.Logger;

/**
 * Command to be sent to a dimmer device to actuate or query status.
 * This includes : RAISE, LOWER, STOP, FADE, STATUS_DIMMER
 * 
 * @author <a href="mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class DimmerCommand extends LutronHomeWorksCommand implements ExecutableCommand, EventListener {

  // Class Members --------------------------------------------------------------------------------

  /**
   * Lutron logger. Uses a common category for all Lutron related logging.
   */
  private final static Logger log = Logger.getLogger(LutronHomeWorksCommandBuilder.LUTRON_LOG_CATEGORY);

  public static LutronHomeWorksCommand createCommand(String name, LutronHomeWorksGateway gateway, LutronHomeWorksAddress address, Integer scene, Integer key, Integer level) {
    // Check for mandatory attributes
    if (address == null) {
      throw new NoSuchCommandException("Address is required for any dimmer command");
    }

    if (!address.isValidDimmerAddress() && !address.isValidGrafikEyeSingleZoneAddress()) {
      throw new NoSuchCommandException("Address must be one of a dimmer or of a GRAFIK Eye zone");
    }

    if ("FADE".equalsIgnoreCase(name) && level == null) {
      throw new NoSuchCommandException("Level is required for a dimmer Fade command");
    }

    return new DimmerCommand(name, gateway, address, level);
  }

  // Private Instance Fields ----------------------------------------------------------------------

  /**
   * Destination address for this command.
   */
  private LutronHomeWorksAddress address;

  /**
   * Level to set when sending the command. Also used to compare with reported level for Switch type sensors.
   */
  private Integer level;

  // Constructors ---------------------------------------------------------------------------------

  public DimmerCommand(String name, LutronHomeWorksGateway gateway, LutronHomeWorksAddress address, Integer level) {
    super(name, gateway);
    this.address = address;
    this.level = level;
  }

  // Implements ExecutableCommand -----------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  public void send() {
    try {
      Dimmer dimmer = (Dimmer) gateway.getHomeWorksDevice(address, Dimmer.class);
      if ("RAISE".equals(name)) {
        dimmer.raise();
      } else if ("LOWER".equals(name)) {
        dimmer.lower();
      } else if ("STOP".equals(name)) {
        dimmer.stop();
      } else if ("FADE".equals(name)) {
        dimmer.fade(level);
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
            Dimmer dimmer = (Dimmer) gateway.getHomeWorksDevice(address, Dimmer.class);
            if (dimmer == null) {
              // This should never happen as above command is supposed to create device
              log.warn("Gateway could not create a Dimmer we're receiving feedback for (" + address + ")");
            }

            // Register ourself with the Keypad so it can propagate update when received
            dimmer.addCommand(this);
            addSensor(sensor);

            // Trigger a query to get the initial value
            dimmer.queryLevel();
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
           Dimmer dimmer = (Dimmer) gateway.getHomeWorksDevice(address, Dimmer.class);
           if (dimmer == null) {
             // This should never happen as above command is supposed to create device
             log.warn("Gateway could not create a Dimmer we're receiving feedback for (" + address + ")");
           }

           dimmer.removeCommand(this);
        } catch (LutronHomeWorksDeviceException e) {
           log.error("Impossible to get device", e);
        }
     }
  }

  @Override
  protected void updateSensor(HomeWorksDevice device, Sensor sensor) {
     Dimmer dimmer = (Dimmer)device;
     if (sensor instanceof SwitchSensor) {
        sensor.update((dimmer.getLevel().intValue() != 0) ? "on" : "off");
      } else if (sensor instanceof RangeSensor) {
         sensor.update(Integer.toString(dimmer.getLevel()));
      } else if (sensor instanceof LevelSensor) {
        sensor.update(Integer.toString(dimmer.getLevel()));
      } else {
        log.warn("Query dimmer status for incompatible sensor type (" + sensor + ")");
      }
  }

}
