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
package org.openremote.controller.deployer;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.openremote.controller.ControllerConfiguration;
import org.openremote.controller.command.CommandFactory;
import org.openremote.controller.exception.ControllerDefinitionNotFoundException;
import org.openremote.controller.exception.InitializationException;
import org.openremote.controller.exception.XMLParsingException;
import org.openremote.controller.model.Command;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.statuscache.StatusCache;
import org.openremote.controller.utils.PathUtil;


/**
 * Controller's object model builder for the current 2.0 version of the implementation. <p>
 *
 * Certain build tasks are delegated to related sub-components, for example sensor and command
 * model builds.
 *
 * @see org.openremote.controller.model.xml.Version20SensorBuilder
 * @see DeviceProtocolBuilder
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class Version20ModelBuilder extends AbstractModelBuilder
{

  //
  //  TODO:
  //
  //     ORCJAVA-209 -- merge command models between this pkg and org.openremote.controller.command
  //     ORCJAVA-210 -- rename schema file
  //


  // Constants ------------------------------------------------------------------------------------


  /**
   * File name of the controller definition XML document. The file is located in
   * the 'resource path' directory that can be found from the controller configuration object.
   *
   * @see ControllerConfiguration#getResourcePath()
   */
  public final static String CONTROLLER_XML = "controller.xml";

  /**
   * We are using JAXP API to access the XML parser -- this property name is used to configure
   * XML schema validation (value is defined in {@link #W3C_XML_SCHEMA}). <p>
   *
   * Should it happen that JAXP is not used to access the XML parser (currently done implicitly
   * by the JDOM library), a different property would likely be needed to enable schema validation.
   */
  private final static String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

  /**
   * The value for {@link #JAXP_SCHEMA_LANGUAGE} property -- using W3C XML schema as validation
   * language.
   */
  private final static String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

  /**
   * We are using JAXP API to access the XML parser (via use of JDOM library) -- this property
   * name is used to configure XML schema location. It only applies if {@link #JAXP_SCHEMA_LANGUAGE}
   * has been defined first. <p>
   *
   * NOTE: Configuring schema source explicitly in code will override schema definitions given in
   * the XML document.
   */
  private final static String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";

  /**
   * TODO : see ORCJAVA-210
   *   - relative path seems potentially problematic, could use path from config or 'resource.path'
   *   - rename to remove M7 suffix
   */
  private final static String CONTROLLER_XSD_PATH = "/controller-2.0-M7.xsd";


  /**
   * Config property element's "type" attribute in controller.xml file, i.e.
   *
   * <pre>
   * {@code
   *       <config>
   *         <property name = "config.property.name" value = "property-value"/>
   *         ...
   *       </config>
   * }</pre>
   */
  private final static String XML_CONFIG_PROPERTY_NAME_ATTR = "name";

  /**
   * Config property element's "value" attribute in controller.xml file, i.e.
   *
   * <pre>
   * {@code
   *       <config>
   *         <property name = "config.property.name" value = "property-value"/>
   *         ...
   *       </config>
   * }</pre>
   */
  private final static String XML_CONFIG_PROPERTY_VALUE_ATTR = "value";



  // Enums ----------------------------------------------------------------------------------------


  /**
   * Identifiers for specific segments in the XML schema this model builder implementation parses.
   */
  protected enum XMLSegment
  {
    /**
     * Enum for {@code<sensors>} section in the XML document instance.
     */
    SENSORS("sensors"),

    /**
     * Enum for {@code<config>} section in the XML document instance.
     */
    CONFIG("config"),

    /**
     * Enum for {@code<commands>} section in the XML document instance.
     */
    COMMANDS("commands");


    // --------------------------------------------------------------------------------------------

    /**
     * Actual element name in the XML document instance.
     */
    private String elementName;


    /**
     * @param elementName   actual element name in the XML document instance
     */
    private XMLSegment(String elementName)
    {
      this.elementName = elementName;
    }


    /**
     * Runs an XPath query on the given model builder's associated XML definition to retrieve
     * the element nodes indicated by this enum's element name.
     *
     * @param   doc   reference to the in-memory XML document model that contains this controller's
     *                definition
     *
     * @return  Returns the XML element and sub-elements indicated by this enum element name.
     *
     * @throws  InitializationException
     *              if the model builder's XML definition file cannot be read or accessed otherwise;
     *              if there's an error in attempting to retrieve the XML nodes indicated by
     *              this enum's element name
     */
    protected Element query(Document doc) throws InitializationException
    {
      return Version20ModelBuilder.queryElementFromXML(doc, elementName);
    }

  }



  // Class Members ------------------------------------------------------------------------------

  /**
   * Checks if the controller.xml file exists in the configured location.
   *
   * @param   config    controller's user configuration
   *
   * @return  true if file exists; false if file does not exists or access was denied by
   *          security manager
   */
  public static boolean checkControllerDefinitionExists(ControllerConfiguration config)
  {
    final File file = getControllerDefinitionFile(config);

    try
    {
      // BEGIN PRIVILEGED CODE BLOCK ------------------------------------------------------------

      return AccessController.doPrivilegedWithCombiner(new PrivilegedAction<Boolean>()
      {
        @Override public Boolean run()
        {
          return file.exists();
        }
      });

      // END PRIVILEGED CODE BLOCK --------------------------------------------------------------
    }

    catch (SecurityException e)
    {
      log.error(
          "Security manager prevented read access to file ''{0}'' : {1}",
          e, file.getAbsoluteFile(), e.getMessage()
      );

      return false;
    }
  }


  /**
   * Utility method to return a Java I/O File instance representing the artifact with
   * controller runtime object model (version 2.0) definition.
   *
   * @param   config    controller's user configuration
   *
   * @return  file representing an object model definition for a controller
   */
  public static File getControllerDefinitionFile(ControllerConfiguration config)
  {
    try
    {
      URI uri = new URI(config.getResourcePath());

      log.trace(
          "Converted controller definition file location from URI ''{0}''",
          uri.resolve(CONTROLLER_XML)
      );

      return new File(uri.resolve(CONTROLLER_XML));
    }

    catch (Throwable t)
    {
      // legacy...

      String xmlPath = PathUtil.addSlashSuffix(config.getResourcePath()) + CONTROLLER_XML;

      log.trace("Applied legacy resource path conversion to non-compatible URI ''{0}''", xmlPath);

      return new File(xmlPath);
    }
  }





  // Instance Fields ----------------------------------------------------------------------------


  /**
   * Reference to status cache instance that manages the sensors. The sensor instances created
   * by this builder will be registered with this cache.
   */
  private StatusCache deviceStateCache;


  /**
   * User defined controller configuration variables.
   *
   * TODO : see ORCJAVA-183, ORCJAVA-193, ORCJAVA-170
   */
  private ControllerConfiguration config;


  /**
   * Indicates whether the controller.xml for this schema implementation has been found.
   *
   * @see #hasControllerDefinitionChanged()
   */
  private boolean controllerDefinitionIsPresent;


  /**
   * Last known timestamp of controller.xml file.
   */
  private long lastTimeStamp = 0L;


  /**
   * This model builder delegates parsing of {@code <sensors>} segment in XML schema to this
   * sub-builder.
   */
  private SensorBuilder sensorBuilder;


  /**
   * This model builder delegates parsing of {@code <commands>} segment in XML schema to this
   * sub-builder.
   */
  private DeviceProtocolBuilder deviceProtocolBuilder;


  /**
   * The commandFactory should update it's commandBuilder with the new configuration before building the model
   */
  private CommandFactory commandFactory;
  
  // Constructors -------------------------------------------------------------------------------

  /**
   * Constructs a builder for version 2.0 of controller schema.
   *
   * @param cache
   *            Reference to a device state cache associated with this object model builder.
   *            This implementation initializes the cache with components that are deployed
   *            as part of this model creation.
   *
   * @param config
   *            User controller configuration for this controller.
   *
   * @param sensorBuilder
   *            Reference to a delegate object that handles the details of parsing the
   *            {@code <sensors>} segment of the controller definition schema.
   *
   * @param deviceProtocolBuilder
   *            Reference to a delegate object that handles the details of parsing the
   *            {@code <commands>} segment of the controller definition schema.
   *
   * @param commandFactory
   *            Reference to the commandFactory
   *            
   * @throws InitializationException
   *            if the XML document containing this controller's definition cannot be read
   *            for any reason
   */
  public Version20ModelBuilder(StatusCache cache, ControllerConfiguration config,
                               SensorBuilder<Version20ModelBuilder> sensorBuilder,
                               DeviceProtocolBuilder deviceProtocolBuilder,
                               CommandFactory commandFactory)
      throws InitializationException
  {
    if (cache == null)
    {
      throw new IllegalArgumentException("Must include a reference to device state cache.");
    }

    this.deviceStateCache = cache;


    if (sensorBuilder == null)
    {
      throw new IllegalArgumentException("Must include a reference to sensor builder.");
    }

    this.sensorBuilder = sensorBuilder;

    // make sure we pass our reference back to the sensor builder to use (mandatory)...

    sensorBuilder.setModelBuilder(this);
    

    if (deviceProtocolBuilder == null)
    {
      throw new IllegalArgumentException("Must include a reference to a device protocol builder.");
    }

    this.deviceProtocolBuilder = deviceProtocolBuilder;

    
    if (commandFactory == null)
    {
      throw new IllegalArgumentException("Must include a reference to a command factory.");
    }

    this.commandFactory = commandFactory;
    

    // TODO : see ORCJAVA-183

    this.config = config;


    // check for the required artifacts (XML document) are present to build the object model...

    controllerDefinitionIsPresent = checkControllerDefinitionExists(config);

    if (controllerDefinitionIsPresent)
    {
      lastTimeStamp = getControllerXMLTimeStamp();
    }
  }



  // Public Instance Methods ----------------------------------------------------------------------


  /**
   * Returns a map of configuration properties defined in controller's definition within
   * {@code <config>} section. These properties should be used to override the system default
   * properties.
   *
   * @return  map of name,value strings representing configuration properties
   */
  public Map<String, String> getConfigurationProperties()
  {
    Element element;

    try
    {
      element = XMLSegment.CONFIG.query(controllerXMLDefinition);

      if (element == null)
      {
        log.info("No configuration overrides in this deployment -- using defaults.");

        return new HashMap<String, String>(0);
      }
    }

    catch (InitializationException e)
    {
      log.error(
          "Error in applying configuration properties -- using defaults : {0}",
          e, e.getMessage()
      );

      return new HashMap<String, String>(0);
    }


    Map<String, String> propertyMap = new HashMap<String, String>();
    List<Element> properties = getChildElements(element);

    for (Element property : properties)
    {
      String name = property.getAttributeValue(XML_CONFIG_PROPERTY_NAME_ATTR);
      String value = property.getAttributeValue(XML_CONFIG_PROPERTY_VALUE_ATTR);

      propertyMap.put(name, value);
    }

    return propertyMap;
  }


  /**
   * Returns a reference to a device state cache associated with this model builder. This is
   * intended for delegate builders which need to associate model objects (e.g. sensors)
   * with the cache.
   * 
   * @return    state cache reference
   */
  public StatusCache getDeviceStateCache()
  {
    return deviceStateCache;
  }
  


  // Implements ModelBuilder --------------------------------------------------------------------


  /**
   * Attempts to determine whether the controller.xml 'last modified' timestamp has changed,
   * or if the file has been removed altogether, or if the file was not present earlier but
   * has been added since last check. <p>
   *
   * All the above cases yield an indication that the controller's model definition has changed
   * which can in turn result in reloading the model by the deployer (see
   * {@link org.openremote.controller.service.Deployer.ControllerDefinitionWatch} for more
   * details).
   *
   * @return  true if controller.xml has been changed, removed or added since last check,
   *          false otherwise
   */
  @Override public boolean hasControllerDefinitionChanged()
  {
    if (controllerDefinitionIsPresent)
    {
      if (!checkControllerDefinitionExists(config))
      {
        // it was there before, now it's gone...
        controllerDefinitionIsPresent = false;

        return true;
      }

      long lastModified = getControllerXMLTimeStamp();

      if (lastModified > lastTimeStamp)
      {
        lastTimeStamp = lastModified;

        return true;
      }
    }

    else
    {
      if (checkControllerDefinitionExists(config))
      {
        controllerDefinitionIsPresent = true;

        return true;
      }
    }

    return false;
  }



  // Implements AbstractModelBuilder --------------------------------------------------------------


  /**
   * Sequence of actions to build object model based on the current 2.0 schema.
   */
  @Override protected void build()
  {
     commandFactory.updateCommandBuilders(getConfigurationProperties(), deployer);
     
    // TODO : at the moment only contains sensor model and partial command model

    buildCommandModel();
    buildSensorModel();
  }


  /**
   * Returns an XML document instance built from {@link #CONTROLLER_XML}. The XML parser is
   * located with JAXP API (via JDOM library). By default the document instance is validated
   * with XML schema.
   *
   * @throws ControllerDefinitionNotFoundException
   *            if {@link #CONTROLLER_XML} cannot be found in the location specified by
   *            {@link ControllerConfiguration#setResourcePath(String)}.
   *
   * @throws XMLParsingException
   *            if the {@link #CONTROLLER_XML} cannot be parsed for any reason
   *
   * @throws InitializationException
   *            if {@link #CONTROLLER_XML} cannot be accessed for any reason or any other
   *            initialization error occurs
   *
   * @return a built JDOM document model for controller.xml
   */
  @Override protected Document readControllerXMLDocument() throws InitializationException
  {
    SAXBuilder builder = new SAXBuilder();

    File controllerXMLFile = getControllerDefinitionFile(config);

    if (!checkControllerDefinitionExists(config))
    {
      try
      {
        throw new ControllerDefinitionNotFoundException(
           "Controller.xml not found -- make sure it's in " + controllerXMLFile.getAbsoluteFile()  
        );
      }

      catch (SecurityException e)
      {
        throw new InitializationException("Cannot access controller.xml : {0}", e, e.getMessage());
      }
    }


    String xsdPath = CONTROLLER_XSD_PATH;

    try
    {
      URL xsdResource = Version20ModelBuilder.class.getResource(CONTROLLER_XSD_PATH);

      if (xsdResource == null)
      {
        log.error("Cannot find XSD schema ''{0}''. Disabling validation...", CONTROLLER_XSD_PATH);
      }

      else
      {
        xsdPath = xsdResource.getPath();

        builder.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);

        // JAXP_SCHEMA_SOURCE property allows values in following types:
        //
        //  - A string that points to the URI of the schema
        //  - An InputStream with the contents of the schema
        //  - A SAX InputSource
        //  - A File 
        //  - An array of Objects, each of which is one of the types defined here (e.g. an
        //    array of strings when multiple schema locations are required).
        //
        //  Above as per the JAXP specification: Properties for Enabling Schema Validation

        builder.setProperty(JAXP_SCHEMA_SOURCE, xsdPath);

        builder.setValidation(true);
      }

      return builder.build(controllerXMLFile);
    }

    catch (Throwable t)
    {
      throw new XMLParsingException(
          "Unable to parse controller definition from " +
          "''{0}'' (accessing schema from ''{1}'') : {2}",
          t, controllerXMLFile.getAbsoluteFile(), xsdPath, t.getMessage()
      );
    }
  }




  // Protected Instance Methods -------------------------------------------------------------------

  /**
   * Build concrete sensor Java instances from the XML declaration. <p>
   *
   * NOTE: this implementation will register and start the sensors at build time automatically.
   */
  protected void buildSensorModel()
  {
    deviceStateCache.start();
    
    // Build...

    Set<Sensor> sensors = buildSensorObjectModelFromXML();

    // TODO :
    //   Should register as lifecycle component and let deployer manage the lifecycle method
    //   calls, see ORCJAVA-188

    for (Sensor sensor : sensors)
    {
      deviceStateCache.registerSensor(sensor);

      sensor.start();
    }
  }


  /**
   * Builds a concrete Java instances which represent commands from the controller.xml definition.
   * <p>
   *
   * Note that this command model is 'anemic' -- a complete model should merge with the command
   * model defined in org.openremote.controller.command package. This is currently left undone
   * to avoid breaking the existing protocol integration API. See ORCJAVA-209 for more details. <p>
   *
   * NOTE: The event context used in event processing chain of incoming events to status cache is
   * initialized as part of this method implementation with the parsed command object model.
   */
  protected void buildCommandModel()
  {

    // TODO : ORCJAVA-209 -- merge command models

    Set<Command> commands = buildCommandObjectModelFromXML();

    // Initialize the status cache's event context so commands can be used directly
    // from within scripts and rules...

    deviceStateCache.initializeEventContext(commands);
  }


  /**
   * Parse command definitions from controller.xml and create the corresponding Java objects. <p>
   *
   * See to-do notes about the issues with the object model in {@link #buildCommandModel()}.
   *
   * @return  list of command instances that were succesfully built from the controller.xml
   *          document instance
   */
  protected Set<Command> buildCommandObjectModelFromXML()
  {
    Element commandsElement = null;

    try
    {
      // Parse <commands> element from the controller.xml...

      commandsElement = XMLSegment.COMMANDS.query(controllerXMLDefinition);


      if (commandsElement == null)
      {
        log.warn("No commands found.");

        return new HashSet<Command>(0);
      }
    }

    catch (InitializationException e)
    {
      log.error(
          "Error loading command definitions from {0} : {1}",
          e, CONTROLLER_XML, e.getMessage()
      );
    }


    // Get the list of <command> elements from within <commands>...

    Iterator<Element> commandElementIterator = getChildElements(commandsElement).iterator();
    Set<Command> commandModels = new HashSet<Command>();

    while (commandElementIterator.hasNext())
    {
      Element commandElement = commandElementIterator.next();

      try
      {
        Command cmd = deviceProtocolBuilder.build(commandElement);

        log.debug("Created object model for {0}.", cmd);

        commandModels.add(cmd);
      }

      catch (Throwable t)
      {
        // If building the command fails for any reason, log it and skip it...

        log.error(
            "Creating command failed. Error: {0} \n XML Element : {1}",
            t, t.getMessage(), new XMLOutputter().outputString(commandElement)
        );
      }
    }

    return commandModels;
  }


  /**
   * Parse sensor definitions from controller.xml and create the corresponding Java objects. <p>
   *
   * @return  list of sensor instances that were succesfully built from the controller.xml
   *          document instance
   */
  protected Set<Sensor> buildSensorObjectModelFromXML()
  {
    Element sensorsElement = null;

    try
    {
      // Parse <sensors> element from the controller.xml...

      sensorsElement = XMLSegment.SENSORS.query(controllerXMLDefinition);


      if (sensorsElement == null)
      {
        log.info("No sensors found.");

        return new HashSet<Sensor>(0);
      }
    }

    catch (InitializationException e)
    {
      log.error(
          "Error loading sensor definitions from {0} : {1}",
          e, CONTROLLER_XML, e.getMessage()
      );
    }


    // Get the list of <sensor> elements from within <sensors>...

    Iterator<Element> sensorElementIterator = getChildElements(sensorsElement).iterator();
    Set<Sensor> sensorModels = new HashSet<Sensor>();

    while (sensorElementIterator.hasNext())
    {
      Element sensorElement = sensorElementIterator.next();

      try
      {
        Sensor sensor = sensorBuilder.build(sensorElement);

        log.debug(
            "Created object model for sensor ''{0}'' (ID = ''{1}'').",
            sensor.getName(), sensor.getSensorID()
        );

        sensorModels.add(sensor);
      }

      catch (Throwable t)
      {
        // If building the sensor fails for any reason, log it and skip it...

        log.error(
            "Creating sensor failed. Error : {0} \n XML Element : {1}",
            t, t.getMessage(), new XMLOutputter().outputString(sensorElement)
        );
      }
    }

    return sensorModels;
  }




  // Private Instance Methods ---------------------------------------------------------------------


  /**
   * Returns the timestamp of controller.xml file of this controller object model.
   *
   * @return  last modified timestamp, or zero if the timestamp cannot be accessed
   */
  private long getControllerXMLTimeStamp()
  {
    final File controllerXML = getControllerDefinitionFile(config);

    try
    {
      // ----- BEGIN PRIVILEGED CODE BLOCK --------------------------------------------------------

      return AccessController.doPrivilegedWithCombiner(new PrivilegedAction<Long>()
      {
        @Override public Long run()
        {
          return controllerXML.lastModified();
        }
      });

      // ----- END PRIVILEGED CODE BLOCK ----------------------------------------------------------
    }

    catch (SecurityException e)
    {
      log.error(
          "Security manager prevented access to timestamp of file ''{0}'' ({1}). " +
          "Automatic detection of controller.xml file modifications are disabled.",
          e, controllerXML, e.getMessage()
      );

      return 0L;
    }
  }


}


