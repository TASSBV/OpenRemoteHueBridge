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
package org.openremote.controller.protocol.amx_ni;

import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.sensor.SwitchSensor;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.protocol.amx_ni.model.AMXNIDevice;
import org.openremote.controller.protocol.amx_ni.model.AMXNIDeviceChannels;
import org.openremote.controller.utils.Logger;

/**
 * Command to be sent to a channel to actuate or query status.
 * This includes : ON, OFF, PULSE, CHANNEL_STATUS
 * 
 * @author <a href="mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class ChannelCommand extends AMXNICommand implements ExecutableCommand, EventListener {

   /**
    * AMX NI logger. Uses a common category for all AMX NI related logging.
    */
   private final static Logger log = Logger.getLogger(AMXNICommandBuilder.AMX_NI_LOG_CATEGORY);

   public static ChannelCommand createCommand(String name, AMXNIGateway gateway, Integer deviceIndex, Integer channel, Integer level, String value, Integer pulseTime, String statusFilter, Integer statusFilterGroup) {
      // Check for mandatory attributes
      if (deviceIndex == null) {
        throw new NoSuchCommandException("DeviceIndex is required for any AMX command");
      }
      if (channel == null) {
         throw new NoSuchCommandException("Channel is required for any AMX command working with channels");
      }

      return new ChannelCommand(name, gateway, deviceIndex, channel, pulseTime);
    }

    // Private Instance Fields ----------------------------------------------------------------------

    /**
     * Channel this command will act on.
     */
    private Integer channel;

    /**
     * Pulse time used if command is PULSE, optional.
     */
    private Integer pulseTime;
    
    // Constructors ---------------------------------------------------------------------------------

    public ChannelCommand(String name, AMXNIGateway gateway, Integer deviceIndex, Integer channel, Integer pulseTime) {
      super(name, gateway, deviceIndex);
      this.channel = channel;
      this.pulseTime = pulseTime;
    }

    // Implements ExecutableCommand -----------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public void send() {
      try {
        AMXNIDeviceChannels channelDevice = (AMXNIDeviceChannels) gateway.getAMXNIDevice(deviceIndex, AMXNIDeviceChannels.class);
        if ("ON".equals(name)) {
           channelDevice.on(channel);
        } else if ("OFF".equals(name)) {
           channelDevice.off(channel);
        } else if ("PULSE".equals(name)) {
           channelDevice.pulse(channel, pulseTime);
        }
      } catch (AMXNIDeviceException e) {
        log.error("Impossible to get device", e);
      }
    }
    
    // Implements EventListener -------------------------------------------------------------------

    @Override
    public void setSensor(Sensor sensor) {
        if (sensors.isEmpty()) {
           // First sensor registered, we also need to register ourself with the device
           try {
              AMXNIDeviceChannels channelDevice = (AMXNIDeviceChannels) gateway.getAMXNIDevice(deviceIndex, AMXNIDeviceChannels.class);
              if (channelDevice == null) {
                // This should never happen as above command is supposed to create device
                log.warn("Gateway could not create a device we're receiving feedback for (" + deviceIndex + ")");
              }

              // Register ourself with the device so it can propagate update when received
              channelDevice.addCommand(this);
              addSensor(sensor);

              // Trigger a query to get the initial value
              channelDevice.queryChannelStatus(channel);
           } catch (AMXNIDeviceException e) {
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
             AMXNIDeviceChannels channelDevice = (AMXNIDeviceChannels) gateway.getAMXNIDevice(deviceIndex, AMXNIDeviceChannels.class);
             if (channelDevice == null) {
               // This should never happen as above command is supposed to create device
                log.warn("Gateway could not create a device we're receiving feedback for (" + deviceIndex + ")");
             }

             channelDevice.removeCommand(this);
          } catch (AMXNIDeviceException e) {
             log.error("Impossible to get device", e);
          }
       }
    }

    @Override
    protected void updateSensor(AMXNIDevice device, Sensor sensor) {
       AMXNIDeviceChannels channelDevice = (AMXNIDeviceChannels)device;
       Boolean channelStatus = channelDevice.getChannelStatus(channel);
       if (channelStatus != null) {
          if (sensor instanceof SwitchSensor) {
             sensor.update(channelStatus ? "on" : "off");
           } else {
             log.warn("Query channel status for incompatible sensor type (" + sensor + ")");
           }
       }
    }

}
