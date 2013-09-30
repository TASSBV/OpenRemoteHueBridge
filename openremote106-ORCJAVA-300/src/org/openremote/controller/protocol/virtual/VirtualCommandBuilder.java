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
package org.openremote.controller.protocol.virtual;

import java.util.List;

import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.command.Command;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.Constants;
import org.jdom.Element;

/**
 * Represents a virtual OpenRemote rooms/devices that can be used for demonstrations and
 * let users get quickly started without learning any particular automation protocol details.  <p>
 *
 * This is not any real protocol, but represents a simple abstraction of many procotols with
 * an address and command strings.
 *
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class VirtualCommandBuilder implements CommandBuilder
{

  // Constants ------------------------------------------------------------------------------------

  /**
   * A common log category name intended to be used across all classes related to
   * OpenRemote virtual protocol implementation.
   */
  public final static String LOG_CATEGORY = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "virtual";

  /**
   * String constant for parsing virtual protocol XML entries from controller.xml file.
   *
   * This constant is the expected property name value for virtual protocol addresses
   * (<code>{@value}</code>):
   *
   * <pre>{@code
   * <command protocol = "virtual" >
   *   <property name = "address" value = "1"/>
   *   <property name = "command" value = "ON"/>
   * </command>
   * }</pre>
   */
  public final static String XML_ADDRESS = "address";

  /**
   * String constant for parsing virtual protocol XML entries from controller.xml file.
   *
   * This constant is the expected property name value for virtual protocol commands ({@value}):
   *
   * <pre>{@code
   * <command protocol = "virtual" >
   *   <property name = "address" value = "1"/>
   *   <property name = "command" value = "ON"/>
   * </command>
   * }</pre>
   */
  public final static String XML_COMMAND = "command";



  // Class Members --------------------------------------------------------------------------------

  /**
   * Logging. Use common log category for all related classes.
   */
  private static Logger log = Logger.getLogger(LOG_CATEGORY);



  // Implements CommandBuilder --------------------------------------------------------------------

  /**
   * Parses the OpenRemote virtual room command XML snippets and builds a
   * corresponding virtual command instance.  <p>
   *
   * The expected XML structure is:
   *
   * <pre>{@code
   * <command protocol = "virtual" >
   *   <property name = "address" value = "1"/>
   *   <property name = "command" value = "ON"/>
   * </command>
   * }</pre>
   *
   *
   * @see VirtualCommand
   *
   * @throws org.openremote.controller.exception.NoSuchCommandException
   *            if the virtual command instance cannot be constructed from the XML snippet
   *            for any reason
   *
   * @return a virtual command instance with known configured properties set
   */
  public Command build(Element element)
  {
    String address = null;
    String command = null;

    // Properties come in as child elements...

    List<Element> propertyElements = getCommandProperties(element);


    // Parse 'address' and 'command' properties...

    for (Element el : propertyElements)
    {
      String propertyName = el.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_NAME);
      String propertyValue = el.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_VALUE);

      if (XML_ADDRESS.equalsIgnoreCase(propertyName))
      {
        address = propertyValue;
      }
      else if (XML_COMMAND.equalsIgnoreCase(propertyName))
      {
        command = propertyValue;
      }
      else
      {
        log.warn(
            "Unknown virtual protocol property '<" + XML_ELEMENT_PROPERTY + " " +
            XML_ATTRIBUTENAME_NAME + " = \"" + propertyName + "\" " +
            XML_ATTRIBUTENAME_VALUE + " = \"" + propertyValue + "\"/>'."
        );
      }
    }

    // sanity checks...

    if (command == null || ("").equals(command))
    {
      throw new NoSuchCommandException(
         "OpenRemote virtual protocol command is missing '" + XML_COMMAND + "' property"
      );
    }

    if (address == null || ("").equals(address))
    {
      throw new NoSuchCommandException(
         "OpenRemote virtual protocol command is missing '" + XML_ADDRESS + "' property"
      );
    }

    // grab the command parameter coming through the REST interface (for sliders et al)
    // it's an ugly hack but there it is...

    String commandParam = element.getAttributeValue(Command.DYNAMIC_VALUE_ATTR_NAME);
    Command cmd;


    // Handle couple of special commands that will start an event listener to generate
    // events (instead of polling read commands or write commands)...

    if (command.equalsIgnoreCase("TemperatureSensor") ||
        command.equalsIgnoreCase("Temperature_Sensor") ||
        command.equalsIgnoreCase("Temperature-Sensor"))
    {
      // thermo will produce range values between [-50..50] going higher or lower once
      // ever 5 seconds...

      cmd = new ThermometerListener();
    }


    else if (command.equalsIgnoreCase("BlinkLight") ||
        command.equalsIgnoreCase("BlinkLights") )
    {
      // Blink light will produce a switch on/off event every two seconds...

      cmd = new BlinkLightListener();
    }


    // if it's not one of the special keywords for event listeners, then create the
    // command as normal (with command param included if found)...

    else if (commandParam == null || commandParam.equals(""))
    {
      cmd = new VirtualCommand(address, command);
    }
    else
    {
      cmd = new VirtualCommand(address, command, commandParam);
    }

    // Done!


    return cmd;
  }


  // Private Instance Methods ---------------------------------------------------------------------

  /**
   * Isolated here to limit the scope of the unchecked warnings to this one method (JDOM does not
   * use Java generics).
   *
   * @param   element   the element corresponding to {@code <command} in controller.xml
   *
   * @return    child elements of {@code <command} i.e. a list of {@code <property}) elements
   */
  @SuppressWarnings("unchecked")
  private List<Element> getCommandProperties(Element element)
  {
    return element.getChildren(
        CommandBuilder.XML_ELEMENT_PROPERTY,
        element.getNamespace()
    );
  }



  // Nested Classes -------------------------------------------------------------------------------

  /**
   * TODO
   */
  static class BlinkLightListener implements EventListener, Runnable
  {

    private Sensor sensor;

    private volatile boolean running = true;

    private Thread listenerThread;


    @Override public void setSensor(Sensor sensor)
    {
      this.sensor = sensor;

      sensor.update("off");

      log.info(
          "Initialized 'Blinking Light' sensor ''{0}'' (ID = ''{1}'') to value 'off'.",
          sensor.getName(), sensor.getSensorID()
      );

      String name =
          "Event producer thread for 'blink lights' " +
          "(Sensor Name : '" + sensor.getName() + "', ID : '" + sensor.getSensorID() + "')";

      listenerThread = new Thread(this);
      listenerThread.setDaemon(true);
      listenerThread.setName(name);

      listenerThread.start();

      log.info("Started " + name);
    }

    @Override public void run()
    {
      boolean setOn = true;

      while (running)
      {
        try
        {
          Thread.sleep(2000);

          if (setOn)
          {
            sensor.update("on");

            setOn = false;
          }

          else
          {
            sensor.update("off");

            setOn = true;
          }
        }
        catch (InterruptedException e)
        {
          // wind out of the while loop...

          running = false;
        }
      }

      log.info("Stopped " + listenerThread.getName());
    }

    @Override public void stop(Sensor s)
    {
      log.info("Stopping " + listenerThread.getName());

      running = false;

      listenerThread.interrupt(); // TODO : sec manager
    }
  }


  /**
   * TODO
   * 
   * A fake temp sensor implemented as a listener that fluxuates the values between -50 and 50
   * on 5 second intervals.
   */
  static class ThermometerListener implements EventListener, Runnable
  {

    private Sensor sensor;

    private Thread listenerThread;

    private volatile boolean running = true;


    @Override public void setSensor(Sensor sensor)
    {
      this.sensor = sensor;

      sensor.update("0");
      
      listenerThread = new Thread(this);
      listenerThread.start();
    }

    @Override public void run()
    {
      int temp = 0;
      int step = 1;

      while (running)
      {
        try
        {
          Thread.sleep(5000);

          temp = temp + step;

          if (temp >= 50)
            step = -1;

          if (temp <= -50)
            step = 1;

          sensor.update(Integer.toString(temp));
        }
        catch (InterruptedException e)
        {
          running = false;
        }
      }
    }

    @Override public void stop(Sensor sensor)
    {
      running = false;

      listenerThread.interrupt();
    }
  }
}

