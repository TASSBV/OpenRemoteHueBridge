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
package org.openremote.controller.protocol;

import org.openremote.controller.model.sensor.Sensor;


/**
 * Event listeners are intended for collecting events from "active" devices which broadcast
 * them over various transport mechanisms -- examples include bus installations such as
 * KNX bus or IP-network broadcasts. In some cases it is also possible to use event listener
 * interface to implement polling mechanism to "passive" devices -- normally a default
 * polling implementation is provided by {@link org.openremote.controller.protocol.ReadCommand}
 * implementation but event listener can be used to override this default implementation. <p>
 *
 * An event listener can be used with the same 'sensor' abstraction (seen for example in
 * the controller's <tt>controller.xml</tt) configuration file) as
 * {@link org.openremote.controller.protocol.ReadCommand read commands}. Both
 * <tt>EventListener</tt> and <tt>ReadCommand</tt> are treated as
 * {@link org.openremote.controller.protocol.EventProducer event producers}. <p>
 *
 * Creating an event listener may occur via the common
 * {@link org.openremote.controller.command.CommandBuilder} interface.  <p>
 *
 * The <tt>EventListener</tt> implementations are disconnected from the sensor polling
 * threads used with (polling) read commands. No active threads are associated with an
 * <tt>EventListener</tt> instance on behalf of the controller framework. Instead,
 * <tt>EventListener</tt> instances are expected to create their own threads
 * which implement the listening functionality and also directly push received events to the
 * (global) state cache of the controller using the sensor callback API provided.  <p>
 *
 * Event listener implementations can use the sensor
 * {@link org.openremote.controller.model.sensor.Sensor#update} method to push state changes
 * into the controller, as shown in the example below:
 *
 * <pre>{@code
 *
 *  public class BusListener implements EventListener, Runnable
 *  {
 *    // This implementation assumes a listener instance per sensor, so only deals with a
 *    // single sensor reference...
 *
 *    private Sensor sensor;
 *
 *    @Override public void setSensor(Sensor sensor)
 *    {
 *      this.sensor = sensor;
 *
 *      // Initialize the sensor with a default value. The sensor implementation may provide
 *      // additional validation or translations to these values...
 *
 *      sensor.update("0");
 *
 *      // This implementation starts a listening thread per sensor. If you want multiple
 *      // listeners / sensors to share same resources or threads, this can be managed in
 *      // the command builder implementation...
 *
 *      Thread t = new Thread(this);
 *      t.start();
 *    }
 *
 *    @Override public void run()
 *    {
 *      String sensorValue = implementYourListenerLogic()
 *
 *      sensor.update(sensorValue);
 *    }
 *  }
 *
 * }</pre>
 *
 *
 *
 * <b>NOTE:</b> The controller at this point does not implement any flow control on its side --
 *              therefore a very busy listener can overflow it with too many events. Control
 *              flow must be implemented co-operatively by the listener implementation ensuring
 *              that not too many events are created.
 *
 * @see EventProducer
 * @see ReadCommand
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public interface EventListener extends EventProducer
{

  // TODO : update the javadoc with stop()

  // TODO :
  //   - the API can (and eventually should) be improved for use case where a listener is bound to
  //     multiple sensors by giving a single callback ID for listener implementor to deal with,
  //     and mapping that to multiple sensor's on controller framework side so the implementer
  //     does not need to deal with updating multiple sensors explicitly.


  /**
   * Each event listener is initialized with one or more sensor references the listener is bound to.
   * If the listener is used as an input for multiple sensors, this callback is invoked multiple
   * times, once for each associated sensor.
   * 
   * @param sensor    sensor this event listener is bound to
   */
  public void setSensor(Sensor sensor);

  // TODO
  public void stop(Sensor sensor);
}
