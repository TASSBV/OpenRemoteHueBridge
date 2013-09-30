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
package org.openremote.controller.model.xml;

import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;
import org.openremote.controller.Constants;
import org.openremote.controller.deployer.Version20ModelBuilder;
import org.openremote.controller.deployer.SensorBuilder;
import org.openremote.controller.deployer.AbstractModelBuilder;
import org.openremote.controller.deployer.ModelBuilder;
import org.openremote.controller.statuscache.StatusCache;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandFactory;
import org.openremote.controller.component.LevelSensor;
import org.openremote.controller.component.RangeSensor;
import org.openremote.controller.exception.InitializationException;
import org.openremote.controller.exception.XMLParsingException;
import org.openremote.controller.model.XMLMapping;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.sensor.StateSensor;
import org.openremote.controller.model.sensor.SwitchSensor;
import org.openremote.controller.protocol.EventProducer;
import org.openremote.controller.utils.Logger;

/**
 * XML Binding from XML document to sensor object model. TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class Version20SensorBuilder implements SensorBuilder<Version20ModelBuilder>
{

  // TODO :
  //  - current implementation is using JDOM through-out so sticking to that, converting to
  //    JAXB longer term might make sense though.
  //                                                                                  [JPL]


  // Sensor Element XML Constants -----------------------------------------------------------------

  /**
   * Sensor element 'type' attribute in controller.xml file, i.e.
   *
   * <pre>
   * {@code
   *       <sensor id = "nnn" name = "sensor-name" type = "[range|level|switch|custom]">
   *         ...
   *       </sensor>
   * }</pre>
   */
  public final static String XML_SENSOR_ELEMENT_TYPE_ATTR = "type";


  /**
   * State element in controller.xml state sensor definition, i.e.
   *
   * <pre>
   * {@code
   *        <sensor id = "nnn" name = "sensor-name" type = "custom">
   *          <state name = "Rain Clouds"   value = "1" />
   *          <state name = "Cloudy"        value = "2" />
   *          <state name = "Partly Cloudy" value = "3" />
   *          <state name = "Sunny"         value = "4" />
   *
   *          <include type = "command" ref = "mmm"/>
   *        </sensor>
   * }</pre>
   */
  public final static String XML_SENSOR_STATE_ELEMENT_NAME = "state";

  /**
   * State element 'name' attribute in controller.xml state sensor definition, i.e.
   *
   * <pre>
   * {@code
   *          <state name = "Rain Clouds"   value = "1" />
   * }</pre>
   */
  public final static String XML_SENSOR_STATE_NAME_ATTR = "name";

  /**
   * State element 'value' attribute in controller.xml state sensor definition, i.e.
   *
   * <pre>
   * {@code
   *          <state name = "Rain Clouds"   value = "1" />
   * }</pre>
   */
  public final static String XML_SENSOR_STATE_VALUE_ATTR = "value";

  /**
   * A valid 'on' value in state element's 'value' attribute in switch sensor definition, i.e.
   *
   * <pre>
   * {@code
   *          <state name = "Ouvert"   value = "on" />
   * }</pre>
   */
  public final static String XML_SWITCH_STATE_VALUE_ON = "on";

  /**
   * A valid 'off' value in state element's 'value' attribute in switch sensor definition, i.e.
   *
   * <pre>
   * {@code
   *          <state name = "Ouvert"   value = "off" />
   * }</pre>
   */
  public final static String XML_SWITCH_STATE_VALUE_OFF = "off";


  /**
   * Max element in controller.xml range sensor definition, i.e.
   *
   * <pre>
   * {@code
   *    <sensor id = "nnn" name = "Outdoor Temp" type = "range">
   *      <min value = "-50"/>
   *      <max value = "50"/>
   *      ...
   *    </sensor>
   * }</pre>
   */
  public final static String XML_RANGE_MAX_ELEMENT_NAME = "max";

  /**
   * Min element in controller.xml range sensor definition, i.e.
   *
   * <pre>
   * {@code
   *    <sensor id = "nnn" name = "Outdoor Temp" type = "range">
   *      <min value = "-50"/>
   *      <max value = "50"/>
   *      ...
   *    </sensor>
   * }</pre>
   */
  public final static String XML_RANGE_MIN_ELEMENT_NAME = "min";

  /**
   * Max element 'value' attribute in controller.xml range sensor definition, i.e.
   *
   * <pre>
   * {@code
   *     <max value = "50"/>
   * }</pre>
   */
  public final static String XML_RANGE_MAX_VALUE_ATTR = "value";

  /**
   * Min element 'value' attribute in controller.xml range sensor definition, i.e.
   *
   * <pre>
   * {@code
   *     <min value = "50"/>
   * }</pre>
   */
  public final static String XML_RANGE_MIN_VALUE_ATTR = "value";



  // Enums ----------------------------------------------------------------------------------------

  // TODO : sensor type should be externalized to make it easier to plug in new sensor implementations

  private enum SensorType
  {
    RANGE, LEVEL, SWITCH, CUSTOM
  }



  // Class Members --------------------------------------------------------------------------------

  /**
   * TODO
   */
  private static Namespace _orNamespace = Namespace.getNamespace(Constants.OPENREMOTE_WEBSITE);
  
  /**
   * Common log category for all XML parsing related activities.
   */
  private final static Logger log = Logger.getLogger(Constants.SENSOR_XML_PARSER_LOG_CATEGORY);




  // Private Instance Fields ----------------------------------------------------------------------


  /**
   * TODO : this reference should go away once ORCJAVA-201 and ORCJAVA-202 are complete
   */
  private CommandFactory protocolHandlerFactory;

  /**
   * This is a reference to the deployer's state cache. The created sensors should register
   * themselves with the state cache.
   */
//  private StatusCache deviceStateCache;


  private Version20ModelBuilder modelBuilder;



  // Constructors ---------------------------------------------------------------------------------

//  /**
//   * Constructs a new sensor builder and registers it with the given deployer.
//   *
//   * @param deployer
//   *          deployer instance this sensor builder will register with
//   *
//   * @param cache
//   *          deployer's associated device state cache -- the created sensors will use
//   *          this state cache to register themselves with
//   */
//  public SensorBuilder(StatusCache cache)
//  {
//    //super(builder);
//
//    this.deviceStateCache = cache;
//    //this.modelBuilder = builder;
//  }


  // Public Instance Methods ----------------------------------------------------------------------


  /**
   * TODO
   *
   * @param modelBuilder    reference of the model builder this sensor builder is associated with
   */
  @Override public void setModelBuilder(Version20ModelBuilder modelBuilder)
  {
    this.modelBuilder = modelBuilder;
  }


  /**
   * Constructs a sensor instance from controller's definition using a JDOM XML element pointing
   * to <tt>{@code <sensor id = "nnn" name = "sensor-name" type = "<datatype>"/>}</tt> entry.
   *
   * @param sensorElement
   *            JDOM element for sensor
   *
   * @throws InitializationException
   *            if the sensor model cannot be build from the given XML element
   *
   * @return initialized sensor instance -- notice that sensor must be
   *         {@link org.openremote.controller.model.sensor.Sensor#start() started} explicitly.
   */
  @Override public Sensor build(Element sensorElement) throws InitializationException
  {
    String sensorIDValue = sensorElement.getAttributeValue("id");
    String sensorName = sensorElement.getAttributeValue("name");

    SensorType type = parseSensorType(sensorElement);

    try
    {
      int sensorID = Integer.parseInt(sensorIDValue);
      EventProducer ep = parseSensorEventProducer(sensorElement);

      StatusCache deviceStateCache = modelBuilder.getDeviceStateCache();

      switch (type)
      {
        case RANGE:

          int min = getMinProperty(sensorElement);
          int max = getMaxProperty(sensorElement);

          return new RangeSensor(sensorName, sensorID, deviceStateCache, ep, min, max);

        case LEVEL:

          return new LevelSensor(sensorName, sensorID, deviceStateCache, ep);

        case SWITCH:

          StateSensor.DistinctStates states = getSwitchStateMapping(sensorElement);

          return new SwitchSensor(sensorName, sensorID, deviceStateCache, ep, states);

        case CUSTOM:

          StateSensor.DistinctStates stateMapping = getDistinctStateMapping(sensorElement);

          StateSensor sensor = new StateSensor(sensorName, sensorID, deviceStateCache, ep, stateMapping);

          sensor.setStrictStateMapping(false);

          return sensor;

        default:

          throw new InitializationException(
              "Using an unknown sensor type {0} -- SensorBuilder implementation must be " +
              "updated to handle this new type.", type
          );
      }
    }

    catch (NumberFormatException e)
    {
        throw new XMLParsingException(
            "Currently only integer values are accepted as unique sensor ids. " +
            "Could not parse {0} to integer.", sensorIDValue
        );
    }
  }



  // Service Dependencies -------------------------------------------------------------------------

  public void setCommandFactory(CommandFactory protocolHandlerFactory)
  {
     this.protocolHandlerFactory = protocolHandlerFactory;
  }


  // Private Instance Methods ---------------------------------------------------------------------


  /**
   * Determines the type of sensor from the type attribute in
   * <tt>{@code <sensor name = "..." type = "XXX"/>}</tt> <p>
   *
   * Currently valid values are defined in
   * {@link Version20SensorBuilder.SensorType}.
   *
   * @param   sensorElement  JDOM element pointing to the sensor structure
   *
   * @return sensor's type
   *
   * @throws XMLParsingException if the type attribute value cannot be recognized
   */
  private SensorType parseSensorType(Element sensorElement) throws XMLParsingException
  {
    String typeValue = sensorElement.getAttributeValue(XML_SENSOR_ELEMENT_TYPE_ATTR);

    try
    {
      return SensorType.valueOf(typeValue.toUpperCase());
    }
    catch (IllegalArgumentException e)
    {
      throw new XMLParsingException(
          "Sensor type {0} is not a valid sensor datatype.", typeValue
      );
    }
  }


  /**
   * Resolves an <tt>{@code <include .../>}</tt> entry from within sensor element
   * that references the command sensor uses to retrieve device state.
   *
   * @param sensorElement   JDOM element pointing to sensor structure
   *
   * @return  event producer instance that the sensor uses to retrieve device state
   *
   * @throws InitializationException
   *            if the sensor element does not have a child include element, or if the
   *            referenced command in include element could not be created
   */
  private EventProducer parseSensorEventProducer(Element sensorElement)
    throws InitializationException
  {
    List<Element> sensorPropertyElements = AbstractModelBuilder.getChildElements(sensorElement);
    String sensorIDValue = sensorElement.getAttributeValue("id");

    for (Element sensorProperty : sensorPropertyElements)
    {
      if (sensorProperty.getName().equalsIgnoreCase(XMLMapping.XML_INCLUDE_ELEMENT_NAME))
      {
        String includeTypeAttrValue =
            sensorProperty.getAttributeValue(XMLMapping.XML_INCLUDE_ELEMENT_TYPE_ATTR);

        if (includeTypeAttrValue.equalsIgnoreCase(XMLMapping.XML_INCLUDE_ELEMENT_TYPE_COMMAND))
        {
          String eventProducerRefValue =
              sensorProperty.getAttributeValue(XMLMapping.XML_INCLUDE_ELEMENT_REF_ATTR);

          try
          {
            int eventProducerID = Integer.parseInt(eventProducerRefValue);

            Element eventProducerElement = modelBuilder.queryElementById(eventProducerID);


            // TODO : this should go through deployer API, see ORCJAVA-202 and ORCJAVA-201

            Command eventProducer = protocolHandlerFactory.getCommand(eventProducerElement);

            if (eventProducer instanceof EventProducer)
            {
              return (EventProducer) eventProducer;
            }

            else
            {
              throw new XMLParsingException(
                  "The included 'command' reference in a sensor (ID = {0}) is not " +
                  "an event producer (Command id : {0}, Type : {1})",
                  sensorIDValue, eventProducerID, eventProducer.getClass().getName()
              );
            }
          }

          catch (NumberFormatException e)
          {
            // This ought to be caught by schema validation but in case schema changes or
            // validation is off...

            throw new XMLParsingException(
                "The <include> element in sensor (ID = {0}) contains an invalid reference " +
                "identifier. The value is not a valid integer : {1}",
                sensorIDValue, eventProducerRefValue
            );
          }
        }
      }
    }

    throw new XMLParsingException(
        "Sensor (ID = {0}) does not include a reference to an event producer.", sensorIDValue
    );
  }



  /**
   * Parses the <tt>{@code<min>}</tt> element on a range sensor. <p>
   *
   * The section of the XML being parsed is following:
   *
   * <pre>
   * {@code
   *   <sensor id = "nnn" name = "Outdoor Temp" type = "range">
   *     <min value = "-50"/>
   *     <max value = "50"/>
   *
   *     <include type = "command" ref = "mmm"/>
   *   </sensor>
   * }
   * </pre>
   *
   * @param sensorElement   JDOM element pointing to <tt>{@code<sensor id = "nnn" name = "xxx"
   *                        type = "range">}</tt> section in controller.xml file.
   *
   * @throws  XMLParsingException   if the range sensor element does not define a min element
   *                                or if the value of the min is not a valid integer
   *
   * @return  range min value
   */
  private int getMinProperty(Element sensorElement) throws XMLParsingException
  {
    Element min = sensorElement.getChild(
        XML_RANGE_MIN_ELEMENT_NAME,
        ModelBuilder.SchemaVersion.OPENREMOTE_NAMESPACE
    );

    String sensorID = sensorElement.getAttributeValue("id");

    if (min == null)
    {
      throw new XMLParsingException(
          "Range sensor (ID = {0}) does not define <min> element.", sensorID
      );
    }

    String minAttrValue = min.getAttributeValue(XML_RANGE_MIN_VALUE_ATTR);

    try
    {
      return Integer.parseInt(minAttrValue);
    }

    catch (NumberFormatException e)
    {
      throw new XMLParsingException(
          "Range sensor (ID = {0}) <min value = {1}/> is not a valid integer number.", sensorID, minAttrValue
      );
    }
  }


  /**
   * Parses the <tt>{@code<max>}</tt> element on a range sensor. <p>
   *
   * The section of the XML being parsed is following:
   *
   * <pre>
   * {@code
   *   <sensor id = "nnn" name = "Outdoor Temp" type = "range">
   *     <min value = "-50"/>
   *     <max value = "50"/>
   *
   *     <include type = "command" ref = "mmm"/>
   *   </sensor>
   * }
   * </pre>
   *
   * @param sensorElement   JDOM element pointing to <tt>{@code<sensor id = "nnn" name = "xxx"
   *                        type = "range">}</tt> section in controller.xml file.
   *
   * @throws  XMLParsingException   if the range sensor element does not define a max element
   *                                or if the value of the max is not a valid integer
   *
   * @return  range max value
   */
  private int getMaxProperty(Element sensorElement) throws XMLParsingException
  {
    Element max = sensorElement.getChild(
        XML_RANGE_MAX_ELEMENT_NAME,
        ModelBuilder.SchemaVersion.OPENREMOTE_NAMESPACE
    );
    
    String sensorID = sensorElement.getAttributeValue("id");

    if (max == null)
    {
      throw new XMLParsingException(
          "Range sensor (ID = {0}) does not define <max> element.", sensorID
      );
    }

    String maxAttrValue = max.getAttributeValue(XML_RANGE_MAX_VALUE_ATTR);

    try
    {
      return Integer.parseInt(maxAttrValue);
    }

    catch (NumberFormatException e)
    {
      throw new XMLParsingException(
          "Range sensor (ID = {0}) <max value = {1}/> is not a valid integer number.", sensorID, maxAttrValue
      );
    }
  }




  /**
   * Parses the <tt>{@code<state>}</tt> elements on a state (a.k.a 'custom') sensor. <p>
   *
   * The section of the XML being parsed is following:
   *
   * <pre>
   * {@code
   *        <sensor id = "nnn" name = "sensor-name" type = "custom">
   *          <state name = "Rain Clouds"   value = "1" />
   *          <state name = "Cloudy"        value = "2" />
   *          <state name = "Partly Cloudy" value = "3" />
   *          <state name = "Sunny"         value = "4" />
   *
   *          <include type = "command" ref = "mmm"/>
   *        </sensor>
   * }
   * </pre>
   *
   * @param sensorElement   JDOM element pointing to <tt>{@code<sensor id = "nnn" name = "xxx"
   *                        type = "custom">}</tt> section in controller.xml file.
   *
   * @return  state values and mappings if specified
   */
  private StateSensor.DistinctStates getDistinctStateMapping(Element sensorElement)
  {
    List<Element>sensorChildren = AbstractModelBuilder.getChildElements(sensorElement);
    StateSensor.DistinctStates states = new StateSensor.DistinctStates();

    for (Element sensorChild : sensorChildren)
    {
      if (sensorChild.getName().equalsIgnoreCase(XML_SENSOR_STATE_ELEMENT_NAME))
      {
        states.addStateMapping(
            sensorChild.getAttributeValue(XML_SENSOR_STATE_VALUE_ATTR),
            sensorChild.getAttributeValue(XML_SENSOR_STATE_NAME_ATTR)
        );
      }
    }

    return states;
  }

  /**
   * Parses the <tt>{@code<state>}</tt> elements on a switch sensor. Only accepts values for
   * "on" and "off" states, rest are ignored. <p>
   *
   * The section of the XML being parsed is following:
   *
   * <pre>
   * {@code
   *        <sensor id = "nnn" name = "Localized Door Sensor" type = "switch">
   *          <state name = "Ouvert"   value = "on" />
   *          <state name = "Ferme"    value = "off" />
   *
   *          <include type = "command" ref = "mmm"/>
   *        </sensor>
   * }
   * </pre>
   *
   * @param sensorElement   JDOM element pointing to <tt>{@code<sensor id = "nnn" name = "xxx"
   *                        type = "switch">}</tt> section in controller.xml file. Only applies
   *                        to 'switch' type sensors.
   *
   * @return  state mapping for 'on' and 'off' values for a switch sensor
   */
  private StateSensor.DistinctStates getSwitchStateMapping(Element sensorElement)
  {
    String sensorIDValue = sensorElement.getAttributeValue("id");
    String sensorName = sensorElement.getAttributeValue(XML_SENSOR_STATE_NAME_ATTR);

    List<Element> sensorChildren = AbstractModelBuilder.getChildElements(sensorElement);
    StateSensor.DistinctStates mapping = new StateSensor.DistinctStates();
    
    for (Element sensorChild : sensorChildren)
    {
      if (sensorChild.getName().equalsIgnoreCase(XML_SENSOR_STATE_ELEMENT_NAME))
      {
        String nameAttr = sensorChild.getAttributeValue(XML_SENSOR_STATE_NAME_ATTR);
        String valueAttr = sensorChild.getAttributeValue(XML_SENSOR_STATE_VALUE_ATTR);

        if (valueAttr == null)
        {
          // TODO :
          //   - this is really an error, declaring the states for switch sensor without mapping
          //     is completely redundant (as they're always 'on' and 'off'), however the tooling
          //     in its current state practices this redundancy in the XML documents it creates
          //     so generating model that redundantly maps <state name = 'on' value = 'on'>
          //     and <state name = 'off' value = 'off'>

          log.debug(
            "A switch sensor (Name = ''{0}'', ID = {1}) has an incomplete <state> element mapping, " +
            "the 'value' attribute is missing in <state name = {2}/>.",
            sensorName, sensorIDValue, nameAttr
          );

          mapping.addStateMapping(nameAttr, nameAttr);

          continue;
        }

        if (valueAttr.equalsIgnoreCase(XML_SWITCH_STATE_VALUE_ON))
        {
          mapping.addStateMapping("on", nameAttr);
        }

        else if (valueAttr.equalsIgnoreCase(XML_SWITCH_STATE_VALUE_OFF))
        {
          mapping.addStateMapping("off", nameAttr);
        }
      }
    }

    // Check if states have been filled in by explicit XML settings. If not, create them
    // for our object model. Thus we ensure 'switch' will always have on/off states in our
    // internal model.

    if (!mapping.hasState("on"))
    {
      mapping.addState("on");
    }

    if (!mapping.hasState("off"))
    {
      mapping.addState("off");
    }

    // Done!

    return mapping;
  }


//  /**
//   * This method only exists to limit the suppress warnings on an unchecked operation (due to
//   * JDOM API) in a single location
//   *
//   * @param el    JDOM element
//   *
//   * @return      child elements of the given JDOM element
//   */
//  @SuppressWarnings("unchecked") private List<Element> _getChildren(Element el)
//  {
//    return el.getChildren();
//  }


}
