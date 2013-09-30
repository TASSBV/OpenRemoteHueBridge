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

import java.io.IOException;
import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openremote.controller.ControllerConfiguration;

import com.jpeterson.x10.Gateway;
import com.jpeterson.x10.GatewayException;
import com.jpeterson.x10.GatewayStateError;
import com.jpeterson.x10.SerialGateway;
import com.jpeterson.x10.Transmitter;
import com.jpeterson.x10.event.AddressEvent;
import com.jpeterson.x10.event.AllLightsOffEvent;
import com.jpeterson.x10.event.AllLightsOnEvent;
import com.jpeterson.x10.event.AllUnitsOffEvent;
import com.jpeterson.x10.event.BrightEvent;
import com.jpeterson.x10.event.DimEvent;
import com.jpeterson.x10.event.OffEvent;
import com.jpeterson.x10.event.OnEvent;
import com.jpeterson.x10.module.CM11A;
import com.jpeterson.x10.module.CM17A;

/**
 * As the {@link org.openremote.controller.protocol.knx.KNXConnectionManager} does,
 * the X10ControllerManager provides isolation between the generic OR event system and X10
 * implementing libraries as well as a higher level X10 API. This allow to change one of the
 * underlying library or to add support for new devices in a more transparent manner.
 *
 * @author Jerome Velociter
 * @author <a href = "mailto:juha@openremote.org">Juha Lindfors</a>
 * @author Fekete Kamosh
 */
public class X10ControllerManager {

   /**
    * X10 logger. Uses a common category for all KNX related logging.
    */
   private final static Logger log = Logger.getLogger(X10CommandBuilder.X10_LOG_CATEGORY);

   /**
    * The default serial port to use when none is indicated in the configuration file.
    */
   private static final String DEFAULT_SERIAL_PORT = "/dev/ttyS0";

   /**
    * Hint for the default transmitter to use if none is indicated in the configuration file.
    */
   private static final String DEFAULT_TRANSMITTER_HINT = "CM11A";

   /**
    * @return the transmitter implementation that correspond to the passed hint.
    */
   protected Transmitter getTransmitterForHint(String hint) {
      // Ideally the proper implementation should be obtained by service
      // resolution, in a component-based architecture.
      // For a first version this will do the trick.
      if (hint.toUpperCase().equals("CM11A")) {
         return new CM11A();
      } else if (hint.toUpperCase().equals("CM17A")) {
         return new CM17A();
      } else {
         return null;
      }
   }

   /**
    * @return a ready-to-send {@link X10Controller}
    */
   public X10Controller getDevice() throws ConnectionException
   {
     // TODO :
     //   fix this -- it's part of the X10 send() loop so reading the XML config every time
     //   doesn't make sense
     ControllerConfiguration config = ControllerConfiguration.readXML();

      String transmitterHint = StringUtils.defaultIfEmpty(config.getX10transmitter(), DEFAULT_TRANSMITTER_HINT);

      Transmitter transmitter = this.getTransmitterForHint(transmitterHint);

      if (transmitter == null) {
         throw new ConnectionException(MessageFormat.format("Could not create transmitter for hint [{0}]",
               transmitterHint));
      }

      String port = StringUtils.defaultIfEmpty(config.getComPort(), DEFAULT_SERIAL_PORT);

      // This cast is safe for now, both CM11A and CM17A (supported devices)
      // are serial gateways.
      // TODO Untie the device/transmitter abstraction from jpeterson library, as we might want to use
      // other libraries to support new devices, like USB.
      ((SerialGateway) transmitter).setPortName(port);

      return new SerialX10Controller(transmitter);
   }

   /**
    * Default implementation of a X10 device, that is a serial device.
    */
   private class SerialX10Controller implements X10Controller {

      private Transmitter transmitter;

      public SerialX10Controller(Transmitter transmitter) {
         this.transmitter = transmitter;
      }

      @Override
      public void send(String address, X10CommandType commandType) {

         final char houseCodeChar = address.charAt(0);

         com.jpeterson.x10.event.X10Event[] events;
         com.jpeterson.x10.event.X10Event commandEvent;
         
         switch (commandType) {
         case SWITCH_ON:
            commandEvent = new OnEvent(this, houseCodeChar);
            break;
         case SWITCH_OFF:
            commandEvent = new OffEvent(this, houseCodeChar);
            break;
         case ALL_UNITS_OFF:
            commandEvent = new AllUnitsOffEvent(this, houseCodeChar);
            break;
         case ALL_LIGHTS_ON:        	 
        	 commandEvent = new AllLightsOnEvent(this, houseCodeChar);        	 
        	 break;
         case DIM:
        	 commandEvent = new DimEvent(this, houseCodeChar, 5, 22);
        	 break;
         case BRIGHT:
        	 commandEvent = new BrightEvent(this, houseCodeChar, 5, 22);
        	 break;        	 
         default:
            throw new IllegalArgumentException(MessageFormat.format("The command code [{0}] is not supported", commandType));
         }

         if (commandType.requiresDeviceCode()) {
         	// Only some commands require device code
         	int deviceCodeInt = Integer.valueOf(address.substring(1));
            events = new com.jpeterson.x10.event.X10Event[2];
            events[0] = new AddressEvent(this, houseCodeChar, deviceCodeInt);
         } else {
            events = new com.jpeterson.x10.event.X10Event[1];
         }
         events[events.length - 1] = commandEvent;

         try {
            // The following cast is safe, both CM11A and CM17A transmitters
            // are gateways
            ((Gateway) transmitter).allocate();

         }

         // In case we cannot allocate the transmitter, it's useless to continue and try to send
         // the events over the wire...

         catch (GatewayException e) {

            log.error(e);

            // TODO :
            //        The Gateway Exception should carry the type information of the underlying
            //        exception. In this case we don't necessarily want to bother the client
            //        with an error if the serial port is just busy at the moment, on the other
            //        hand some more severe I/O exceptions might make sense to propagate back to
            //        give a clear indication that the controller is not working...
            //
            //        For now, the most common case for GatewayException seems to be an underlying
            //        gnu.io.PortInUseException, so I'm logging it but not propagating it any
            //        further.
            //                                                                  [JPL]

            // Can't continue, so get out...

            return;

         } catch (GatewayStateError e1) {
            log.error(e1);
            throw new RuntimeException(e1);
         }

         // transmit the event queue over the physical layer

         for (int j = 0; j < events.length; j++) {
            try {
               transmitter.transmit(events[j]);
            } catch (IOException e) {
               log.error(e);
            }
         }

         if (transmitter instanceof Gateway) {
            Gateway gateway = (Gateway) transmitter;

            try {
               gateway.waitGatewayState(Transmitter.QUEUE_EMPTY);
            } catch (InterruptedException e) {
               log.error(e);
            }

            try {
               gateway.deallocate();
            } catch (GatewayException e) {
               log.error(e);
            }
         }
      }
   }

}
