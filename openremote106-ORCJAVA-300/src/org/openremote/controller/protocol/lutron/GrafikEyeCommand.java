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
import org.openremote.controller.component.RangeSensor;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.sensor.SwitchSensor;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.protocol.lutron.model.GrafikEye;
import org.openremote.controller.protocol.lutron.model.HomeWorksDevice;
import org.openremote.controller.utils.Logger;

/**
 * 
 * @author <a href="mailto:eric@openremote.org">Eric Bariaux</a>
 *
 */
public class GrafikEyeCommand extends LutronHomeWorksCommand implements ExecutableCommand, EventListener {

  // Class Members --------------------------------------------------------------------------------

  /**
   * Lutron logger. Uses a common category for all Lutron related logging.
   */
  private final static Logger log = Logger.getLogger(LutronHomeWorksCommandBuilder.LUTRON_LOG_CATEGORY);

  public static LutronHomeWorksCommand createCommand(String name, LutronHomeWorksGateway gateway, LutronHomeWorksAddress address, Integer scene, Integer key, Integer level) {

    log.info("createCommand (" + name + "," + gateway + "," + address + "," + scene + "," + key + "," + level + ")");

    // Check for mandatory attributes
    if (address == null) {
      throw new NoSuchCommandException("Address is required for any GRAFIK Eye command");
    }

    if (!"STATUS_SCENE".equalsIgnoreCase(name) && scene == null) {
      throw new NoSuchCommandException("Scene is required for 'write' GRAFIK Eye command");
    }

    if (!address.isValidGrafikEyeAddress()) {
      throw new NoSuchCommandException("Address must be one of a GRAFIK Eye");
    }

    return new GrafikEyeCommand(name, gateway, address, scene);
  }

  // Private Instance Fields
  // ----------------------------------------------------------------------

  /**
   * Destination address for this command.
   */
  private LutronHomeWorksAddress address;

  /**
   * Number of the scene this command must select.
   */
  private Integer scene;

  // Constructors
  // ---------------------------------------------------------------------------------

  public GrafikEyeCommand(String name, LutronHomeWorksGateway gateway, LutronHomeWorksAddress address, Integer scene) {
    super(name, gateway);
    this.address = address;
    this.scene = scene;
  }

  // Implements ExecutableCommand -----------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  public void send() {
    try {
      GrafikEye grafikEye = (GrafikEye) gateway.getHomeWorksDevice(address, GrafikEye.class);
      grafikEye.selectScene(scene);
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
            GrafikEye grafikEye = (GrafikEye) gateway.getHomeWorksDevice(address, GrafikEye.class);
            if (grafikEye == null) {
              // This should never happen as above command is supposed to create device
              log.warn("Gateway could not create a GRAFIK Eye we're receiving feedback for (" + address + ")");
            }

            // Register ourself with the Keypad so it can propagate update when received
            grafikEye.addCommand(this);
            addSensor(sensor);

            // Trigger a query to get the initial value
            grafikEye.queryScene();
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
           GrafikEye grafikEye = (GrafikEye) gateway.getHomeWorksDevice(address, GrafikEye.class);
           if (grafikEye == null) {
             // This should never happen as above command is supposed to create device
             log.warn("Gateway could not create a GRAFIK Eye we're receiving feedback for (" + address + ")");
           }

           grafikEye.removeCommand(this);
        } catch (LutronHomeWorksDeviceException e) {
           log.error("Impossible to get device", e);
        }
     }
  }

  @Override
  protected void updateSensor(HomeWorksDevice device, Sensor sensor) {
     GrafikEye grafikEye = (GrafikEye)device;
     if (sensor instanceof SwitchSensor) {
        sensor.update((grafikEye.getSelectedScene() == scene) ? "on" : "off");
      } else if (sensor instanceof RangeSensor) {
         sensor.update(Integer.toString(grafikEye.getSelectedScene()));
      } else {
         log.warn("Query GRAFIK Eye status for incompatible sensor type (" + sensor + ")");
      }
  }
}
