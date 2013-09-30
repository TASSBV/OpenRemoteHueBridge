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
package org.openremote.controller.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.jdom.Attribute;
import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.ControllerConfiguration;
import org.openremote.controller.OpenRemoteRuntime;
import org.openremote.controller.deployer.ModelBuilder;
import org.openremote.controller.deployer.Version20ModelBuilder;
import org.openremote.controller.deployer.Version30ModelBuilder;
import org.openremote.controller.exception.ConfigurationException;
import org.openremote.controller.exception.ConnectionException;
import org.openremote.controller.exception.ControllerDefinitionNotFoundException;
import org.openremote.controller.exception.InitializationException;
import org.openremote.controller.exception.XMLParsingException;
import org.openremote.controller.model.XMLMapping;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.statuscache.StatusCache;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.utils.NetworkUtil;
import org.openremote.devicediscovery.domain.DiscoveredDeviceDTO;
import org.openremote.rest.GenericResourceResultWithErrorMessage;
import org.openremote.useraccount.domain.ControllerDTO;
import org.openremote.useraccount.domain.UserDTO;
import org.restlet.data.ChallengeScheme;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.springframework.security.providers.encoding.Md5PasswordEncoder;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * Deployer service centralizes access to the controller's runtime state information. It maintains
 * the controller object model (declared in the XML documents controller deploys), and also
 * acts as a mediator for some other key services in the controller. <p>
 *
 * Mainly the tasks relate to objects and services that maintain some state in-memory of
 * the controller, and where access to such objects or services needs to be shared across
 * multiple threads (rather than created per thread or invocation). Deployer manages the lifecycle
 * of such stateful objects and services to ensure proper state transitions when new controller
 * definitions (from the XML model) are loaded. <p>
 *
 * Main parts of this implementation relate to managing the XML to Java object mapping --
 * transferring the information from the XML document instance that describe controller
 * behavior into a runtime object model -- and managing the lifecycle of services through
 * restarts and reloading the controller descriptions. In addition, this deployer provides
 * access to the object model instances it generates (such as references to the sensor
 * implementations).
 *
 *
 * @see #softRestart
 * @see #getSensor
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class Deployer
{

  /*
   *   TODO:
   * 
   *     -  ORCJAVA-123 (http://jira.openremote.org/browse/ORCJAVA-123) : introduce an immutable
   *        sensor interface for plugins to use.
   *     -  ORCJAVA-173 (http://jira.openremote.org/browse/ORCJAVA-123) : expose some of the admin
   *        related functions through a secured REST interface -- deployFromZip, deployFromOnline,
   *        softRestart
   *     -  ORCJAVA-174 (http://jira.openremote.org/browse/ORCJAVA-174) : start sensors
   *        asynchronously
   *     -  ORCJAVA-179 (http://jira.openremote.org/browse/ORCJAVA-179) : guard against multiple
   *        invocations of startController()
   *     -  ORCJAVA-180 (http://jira.openremote.org/browse/ORCJAVA-180) : add shutdown hook
   *     -  ORCJAVA-188 (http://jira.openremote.org/browse/ORCJAVA-188) : introduce lifecycle
   *        interface
   */



  
  // Class Members --------------------------------------------------------------------------------

  /**
   * Common log category for startup logging, with a specific sub-category for this deployer
   * implementation.
   */
  private final static Logger log = Logger.getLogger(Constants.DEPLOYER_LOG_CATEGORY);




  // Private Fields -------------------------------------------------------------------------------


  /**
   * Reference to status cache instance that does the actual lifecycle management of sensors
   * (and receives the event updates). This implementation delegates these tasks to it.
   */
  private StatusCache deviceStateCache;


  /**
   * User defined controller configuration variables.
   *
   * TODO : See ORCJAVA-183
   */
  private ControllerConfiguration controllerConfig;


  /**
   * Configured model builder implementations for this deployer, per schema version.
   */
  private Map<ModelBuilder.SchemaVersion, ModelBuilder> builders;

  private Map<Integer, Element> controllerXMLElementCache = new HashMap<Integer, Element>();
  
  /**
   * Cache parsed XML elements to avoid triggering XML parser/XPath multiple times during
   * runtime execution.
   *
   * This is a temporary performance fix to an issue described in ORCJAVA-190 -- once the
   * relevant overhaul of the rest of the object model is complete (see the referenced
   * issues in ORCJAVA-190) this fix is also redundant.
   */
  private Map<Integer, Element> xmlElementCache = new HashMap<Integer, Element>();


  /**
   * Model builders are sequences of actions to construct the controller's object model (a.k.a
   * strategy pattern). Different model builders may therefore act on differently
   * structured XML document instances. <p>
   *
   * Model builder implementations in general delegate sub-tasks to various object builders
   * that have been registered for the relevant XML schema. <p>
   *
   * This model builder's lifecycle is delimited by the controller's soft restart 
   * lifecycle (see {@link #softRestart()}. Each deploy lifecycle represents one object model
   * (and therefore one model builder instance) that matches a particular XML schema structure.
   *
   * @see ModelBuilder
   * @see #softRestart
   */
  private ModelBuilder modelBuilder = null;


  /**
   * This is a file watch service for the controller definition associated with the current
   * object model builder (i.e. the currently deployed controller XML schema). <p>
   *
   * Depending on the implementation (deletegated to current model builder instance) it may
   * detect changes from the file's timestamp, adding/deleting particular files, etc. and
   * control the deployer lifecycle accordingly, initiating soft restarts, shutdowns, and so on.
   */
  private ControllerDefinitionWatch controllerDefinitionWatch;

  
  /**
   * This is a generic state flag indicator for this deployer service that its operations are
   * in a 'paused' state -- these may occur during periods where the internal object model is
   * changed, such as during a soft restart. <p>
   *
   * Method implementations in this class may use this flag to check whether to service the
   * incoming call, block it until pause flag is cleared, or immediately return back to the
   * caller.
   */
  private boolean isPaused = false;

  /**
   * Flag to indicate if the controller has been started (is ready to deploy controller
   * definitions).  <p>
   *
   * This is used internally to enforce API call requirements of {@link #startController}
   * (should only be called once).
   */
  private boolean started = false;


  /**
   * Human readable service name for this deployer service. Useful for some logging and
   * diagnostics.
   */
  private String name = "<undefined>";

  /**
   * Reference to controller database object from Beehive. This object also links to the AccountDTO
   * which can be used to perform automated deploy features and is needed for device discovery.
   */
  private ControllerDTO controllerDTO;

  /**
   * This list holds all discovered devices which are not announced to beehive yet 
   */
  private List<DiscoveredDeviceDTO> discoveredDevicesToAnnounce = new ArrayList<DiscoveredDeviceDTO>();

  /**
   * Reference to the thread handling the controller announcement notifications
   */
  private ControllerAnnouncement controllerAnnouncement;
  
  /**
   * Reference to the thread handling the announcement of discovered devices
   */
  private DiscoveredDevicesAnnouncement discoveredDevicesAnnouncement;
  
  /**
   * Reference to the service which checks Beehive regularly for any new actions that this controller should perform<br>
   * For example unlink from Beehive, download new design, start proxy, update controller, .... 
   */
  private BeehiveCommandCheckService beehiveCommandCheckService;
  
  // Constructors ---------------------------------------------------------------------------------

  /**
   * Creates a new deployer service with a given device state cache implementation and user
   * configuration variables. <p>
   *
   * Creating a deployer instance will not make it 'active' -- no controller object model is
   * loaded (or attempted to be loaded) until a {@link #startController()} method is called.
   * The <tt>startController</tt> therefore acts as an initializer method for the controller
   * runtime.
   *
   * @see #startController()
   *
   * @param serviceName         human-readable name for this deployer service
   * @param deviceStateCache    device cache instance for this deployer
   * @param controllerConfig    user configuration of this deployer's controller
   * @param builders            model builder implementations (controller schema versions)
   *                            available for this deployer
   *
   * @throws InitializationException      if an unrecognized model builder schema version has
   *                                      been configured for this deployer
   */
  public Deployer(String serviceName, StatusCache deviceStateCache,
                  ControllerConfiguration controllerConfig, BeehiveCommandCheckService beehiveCommandCheckService,
                  Map<String, ModelBuilder> builders) throws InitializationException
  {
    if (deviceStateCache == null || controllerConfig == null)
    {
      throw new IllegalArgumentException("Null parameters are not allowed.");
    }
    
    this.beehiveCommandCheckService = beehiveCommandCheckService;
    this.beehiveCommandCheckService.setDeployer(this);
    this.deviceStateCache = deviceStateCache;
    this.controllerConfig = controllerConfig;
    this.builders = createTypeSafeBuilderMap(builders);

    if (name != null)
    {
      this.name = serviceName;
    }
    
    this.controllerDefinitionWatch = new ControllerDefinitionWatch(this);

    log.debug("Deployer ''{0}'' initialized.", name);
  }



  // Public Instance Methods ----------------------------------------------------------------------


  /**
   * This method initializes the controller's runtime model, making it 'active'. The method should
   * be called once during the lifecycle of the controller JVM -- subsequent re-deployments of
   * controller's runtime should go via {@link #softRestart()} method. <p>
   *
   * If a controller definition is present, it is loaded and the object model created accordingly.
   * If no definition is found, the controller is left in an init state where adding the
   * required artifacts to the controller will trigger the deployment of controller definition.
   *
   * @see #softRestart()
   */
  public synchronized void startController()
  {
    if (started)
    {
      log.error(
          "Method startController() should only be called once per VM process. Use softRestart() " +
          "for hot-deploying new controller state."
      );
      return;
    }

    if (controllerConfig.getBeehiveSyncing())
    {
      // Start controller announcement thread

      controllerAnnouncement = new ControllerAnnouncement();
      controllerAnnouncement.start();
   
      // Start discovered devices announcement thread

      discoveredDevicesAnnouncement = new DiscoveredDevicesAnnouncement();
      discoveredDevicesAnnouncement.start();
    }

    try
    {
      startup();
    }

    catch (ControllerDefinitionNotFoundException e)
    {
      log.info(
         "\n\n" +
         "********************************************************************************\n" +
         "\n" +
         " Controller definition was not found in this OpenRemote Controller instance.      \n" +
         "\n" +
         " If you are starting the controller for the first time, please use your web     \n" +
         " browser to connect to the controller home page and synchronize it with your    \n" +
         " online account. \n" +
         "\n" +
         "********************************************************************************\n\n" +

         "\n" + e.getMessage()
      );
    }

    catch (Throwable t)
    {
      log.error("!!! CONTROLLER STARTUP FAILED : {0} !!!", t, t.getMessage());
    }


    controllerDefinitionWatch.start();

    started = true;
    
    // TODO : ORCJAVA-180, register shutdown hook
  }


  /**
   * Indicates the current state of the deployer. Deployer may be 'paused' during certain
   * lifecycle stages, such as reloading the controller's internal object model. During those
   * phases, other deployer operations may opt to block calling threads until deployer has
   * resumed, or return calls immediately without servicing them.
   * 
   * @return    true to indicate deployer is currently paused, false otherwise
   */
  public boolean isPaused()
  {
    // TODO :
    //   the use of this method is restricted to REST API implementation and the original use looks
    //   dubious -- once those parts of REST API have been reworked, this method may be candidate
    //   for removal.

    return isPaused;
  }





  /**
   * Initiate a shutdown/startup sequence.  <p>
   *
   * Shutdown phase will undeploy the current runtime object model and manage service lifecycles
   * that are dependent on the object model. Resources will be stopped and freed. <p>
   *
   * The soft restart only impacts the runtime object model and associated component lifecycles
   * in the controller. The controller itself stays at an init level where a new object model can
   * be loaded into the system. The JVM process will not exit. <p>
   *
   * Startup phase loads back a runtime object model and starts dependent services of the controller.
   * The object model is loaded from the controller definition file, path of which is indicated by
   * the {@link org.openremote.controller.ControllerConfiguration#getResourcePath()} method. <p>
   *
   * After the startup phase is done, a complete functional controller definition has been
   * loaded into the controller (unless fatal errors occured), it has been initialized and
   * started and it is ready to handle incoming requests. <p>
   *
   * <b>NOTE : </b> This method call should only be used after {@link #startController()}
   * has been invoked once. The <tt>startController</tt> method will initialize this deployer
   * instance's lifecycle, and perform first deployment of controller definition, if the
   * required artifacts are present in the controller. </p>
   *
   * Subsequent soft restarts of this controller/deployer should use this method.
   *
   * @see #startController()
   * @see org.openremote.controller.ControllerConfiguration#getResourcePath
   * @see #softShutdown
   * @see #startup()
   *
   * @throws ControllerDefinitionNotFoundException
   *            If there are no controller definitions to load from. This exception indicates that
   *            the {@link #startup} phase of the restart cannot complete. The controller/deployer
   *            is left in an init state where the previous controller object model has been
   *            undeployed, and a new one will be deployed once the required artifacts have been
   *            added to the controller.
   */
  public void softRestart() throws ControllerDefinitionNotFoundException
  {
    // TODO : ORCJAVA-184 -- queue concurrent calls
    
    try
    {
      pause();

      softShutdown();

      startup();
    }

    finally
    {
      resume();
    }
  }


  /**
   * Deploys a controller configuration from a given ZIP archive. This can be used when the
   * controller configuration is already present on the local system. <p>
   *
   * The contents of the ZIP archive will be extracted on the file system location pointed
   * by the 'resource.path' property. <p>
   *
   * If the {@link ControllerConfiguration#RESOURCE_UPLOAD_ALLOWED} has been configured to
   * 'false', this method will throw an exception.
   *
   * @see org.openremote.controller.ControllerConfiguration#getResourcePath()
   * @see org.openremote.controller.ControllerConfiguration#isResourceUploadAllowed()
   *
   * @see #deployFromOnline(String, String)
   *
   * @param inputStream     Input stream to the zip file to deploy. Note that this method will
   *                        attempt to close the input stream on exit.
   *
   * @throws ConfigurationException   If new deployments through admin interface have been disabled,
   *                                  or if the target path to extract the new deployment archive
   *                                  cannot be resolved, or if the target path does not exist
   *
   * @throws IOException              If there was an unrecovable I/O error when extracting the
   *                                  deployment archive. Note that errors in extracting individual
   *                                  files from within the deployment archive may be logged as
   *                                  errors or warnings instead of raising an exception.
   */
  public void deployFromZip(InputStream inputStream) throws ConfigurationException, IOException
  {
    // TODO:
    //   May need a proper permissions object -- this would allow specific
    //   username/credentials to upload only. Maybe there would be some benefit
    //   in having this master all on/off check also implemented as a permission
    //   rather than configuration option. Dunno.
    //                                                    [JPL]

    if (!controllerConfig.isResourceUploadAllowed())
    {
      throw new ConfigurationException(
          "Updating controller through web interface has been disabled. " +
          "You must update controller files manually instead."
      );
    }

    String resourcePath = controllerConfig.getResourcePath();

    if (resourcePath == null || resourcePath.equals(""))
    {
      throw new ConfigurationException(
          "Configuration option 'resource.path' was not found or contains empty value."
      );
    }

    URI resourceDirURI = new File(controllerConfig.getResourcePath()).toURI();

    unzip(inputStream, resourceDirURI);

    copyLircdConf(resourceDirURI, controllerConfig);
  }


  /**
   * Deploys a controller configuration directly from user's account stored on the backend.
   * A HTTP connection is created to Beehive server and account information is downloaded to
   * this controller using the given user name and credentials.
   *
   * @see #deployFromZip(java.io.InputStream)
   *
   * @param username        user name to download account configuration through Beehive's REST
   *                        interface
   *
   * @param credentials     credentials to authenticate to use Beehive's REST interface
   *
   * @throws ConfigurationException   If the connection to backend cannot be created due to
   *                                  configuration errors, or deploying new configuration has
   *                                  been disabled, or there were other configuration errors
   *                                  which prevented the deployment archive from being extracted.
   *
   * @throws ConnectionException      If connection creation failed, or reading from the connection
   *                                  failed for any reason
   */
  public void deployFromOnline(String username, String credentials) throws ConfigurationException,
                                                                           ConnectionException
  {
    BeehiveConnection connection = new BeehiveConnection(this);

    InputStream stream = connection.downloadZip(username, credentials);

    try
    {
      deployFromZip(stream);
    }

    catch (IOException e)
    {
      throw new ConnectionException(
          "Extracting controller configuration from Beehive account failed : {0}",
          e, e.getMessage()
      );
    }

    finally
    {
      if (stream != null)
      {
        try
        {
          stream.close();
        }

        catch (IOException e)
        {
          log.warn(
              "Could not close I/O stream to downloaded user configuration : {0}",
              e, e.getMessage()
          );
        }
      }
    }
  }


  /**
   * TODO :
   *
   *   This is temporarily here, part of the refactoring of deprecating ComponentBuilder and
   *   migrating to a proper ObjectBuilder implementation. The existing component builders
   *   externalize their XML parsing which is currently serviced here. Over the long term,
   *   the object builders should be dependents of model builder which provides XML parsing
   *   services like this method.
   *
   *   See ORCJAVA-143, ORCJAVA-144, ORCJAVA-151, ORCJAVA-152, ORCJAVA-153, ORCJAVA-155,
   *       ORCJAVA-158, ORCJAVA-182, ORCJAVA-186
   *
   */
  public Element queryElementById(int id) throws InitializationException
  {

    if (modelBuilder == null)
    {
      throw new IllegalStateException("Runtime object model has not been initialized.");
    }


    // Quick fix to cache parsed XML elements. See ORCJAVA-190
    
    Element tmp = xmlElementCache.get(id);

    if (tmp == null)
    {
      tmp = ((Version20ModelBuilder)modelBuilder).queryElementById(id);

      xmlElementCache.put(id, tmp);
    }

    return tmp;
  }


  /**
   * TODO
   *
   * @return
   */
  public Map<String, String> getConfigurationProperties()
  {
    if (modelBuilder == null)
    {
      // TODO : See ORCJAVA-183

      log.debug("Runtime object model has not been initialized. Using default configuration only.");

      return new HashMap<String, String>(0);
    }

    return ((Version20ModelBuilder)modelBuilder).getConfigurationProperties();
  }

  
  /**
   * TODO
   *
   *   This is temporarily here, part of refactoring to internalize Java-to-XML mapping to their
   *   corresponding services. This is used by deprecated ComponentBuilder implementations
   *   (see ORCJAVA-143) which rely on sensors for state input.
   *
   *   See ORCJAVA-143, ORCJAVA-144, ORCJAVA-147, ORCJAVA-151, ORCJVA-152, ORCJAVA-153
   *
   *
   * @param componentIncludeElement   JDOM element for sensor
   *
   * @throws InitializationException    if the sensor model cannot be built from the given XML
   *                                    element
   *
   * @return sensor
   */
  public Sensor getSensorFromComponentInclude(Element componentIncludeElement)
      throws InitializationException
  {

    if (componentIncludeElement == null)
    {
      throw new InitializationException(
          "Implementation error, null reference on expected " +
          "<include type = \"sensor\" ref = \"nnn\"/> element."
      );
    }

    Attribute includeTypeAttr =
        componentIncludeElement.getAttribute(XMLMapping.XML_INCLUDE_ELEMENT_TYPE_ATTR);

    String typeAttributeValue = includeTypeAttr.getValue();

    if (!typeAttributeValue.equals(XMLMapping.XML_INCLUDE_ELEMENT_TYPE_SENSOR))
    {
      throw new XMLParsingException(
          "Expected to include 'sensor' type, got {0} instead.", typeAttributeValue
      );
    }


    Attribute includeRefAttr =
        componentIncludeElement.getAttribute(XMLMapping.XML_INCLUDE_ELEMENT_REF_ATTR);

    String refAttributeValue = includeRefAttr.getValue();


    try
    {
      int sensorID = Integer.parseInt(refAttributeValue);

      return getSensor(sensorID);
    }

    catch (NumberFormatException e)
    {
        throw new InitializationException(
            "Currently only integer values are accepted as unique sensor ids. " +
            "Could not parse {0} to integer.", refAttributeValue
        );
    }
  }


  /**
   * Method is called by the commandBuilder of a protocol if the commandBuilders discovers devices for his
   * protocol that should be announced to Beehive.
   * 
   * @param list - the list of devices to announce
   */
  public void announceDiscoveredDevices(List<DiscoveredDeviceDTO> list) {
     synchronized (discoveredDevicesToAnnounce) {
        discoveredDevicesToAnnounce.addAll(list);
     }
  }

  /**
   * Method is called by the ConfigManageController which is invoked by index.html
   * @return a String with the linked account Id or the MAC address with a leading '-'
   */
  public String getLinkedAccountId() throws Exception {
     if (controllerConfig.getBeehiveSyncing()) {
        if ((controllerDTO != null) && (controllerDTO.getAccount() != null)) {
           return controllerDTO.getAccount().getOid().toString();
        } else {
           return "-"+NetworkUtil.getMACAddresses();
        }
     } else {
        return "no";
     }
  }

  // Protected Instance Methods -------------------------------------------------------------------

  /**
   * Returns a registered sensor instance. Sensor instances are shared across threads.
   * Retrieving a sensor with the same ID will yield a same instance. <p>
   *
   * If the sensor with given ID is not found, a <tt>null</tt> is returned.
   *
   * @see org.openremote.controller.model.sensor.Sensor#getSensorID()
   *
   * @param id    sensor ID
   *
   * @return      sensor instance, or null if sensor with given ID was not found
   */
  protected Sensor getSensor(int id)
  {
    // TODO :
    //   This is currently used by getSensorFromComponentInclude() which is a temporary method
    //   on this interface, as part of ongoing refactoring. This method may not need to stay
    //   once those refactorings are complete. Also notice that this method is currently the
    //   only consumer of the deviceStateCache.getSensor() call.
    //
    //   The visibility could be lowered to private once unit tests do not directly use this
    //   API anymore -- they could instead use the StatusCache API.

    Sensor sensor = deviceStateCache.getSensor(id);

    if (sensor == null)
    {
      // Write a log entry on accessing sensor ID that does not exist. At the moment letting
      // this continue as it is, that is, returning a null pointer which is likely to blow up
      // elsewhere unless the calling code guards against it. May consider other ways of
      // handling it later.
      //                                                                                  [JPL]
      log.error(
          "Attempted to access sensor with id ''{0}'' which did not exist in device " +
          "state cache.", id
      );
    }

    return sensor;
  }



  // Private Instance Methods ---------------------------------------------------------------------


  /**
   * Implements the sequence of shutting down the currently deployed controller
   * runtime (but will not exit the VM process). <p>
   *
   * After this method completes, the controller has no active runtime object model but is
   * at an init level where a new one can be loaded in.
   */
  private void softShutdown()
  {
    log.info(
        "\n\n" +
        "--------------------------------------------------------------------\n\n" +
        "  UNDEPLOYING CURRENT CONTROLLER RUNTIME...\n\n" +
        "--------------------------------------------------------------------\n"
    );


    // TODO : ORCJAVA-188, introduce lifecycle management for event processors
    // TODO : ORCJAVA-188, introduce lifecycle management for connection managers
    // TODO : ORCJAVA-188, introduce and use generic LifeCycle interface for state cache

    deviceStateCache.shutdown();
    controllerXMLElementCache.clear();
    
    xmlElementCache.clear();
    
    modelBuilder = null;                // null here indicates to other services that this deployer
                                        // installer currently has no object model deployed
    
    log.info("Shutdown complete.");
  }


  /**
   * Manages the build-up of controller runtime with object model creation
   * from the XML document instance(s). Attempts to detect from configuration files
   * which version of object model and corresponding XML schema should be used to
   * build the runtime model. <p>
   *
   * Once this method returns, the controller runtime is 'ready' -- that is, the object
   * model has been created and also initialized, registered and started so it is fully
   * functional and able to receive requests.   <p>
   *
   * Note that partial failures (such as errors in the controller's object model definition) may
   * not prevent the startup from completing. Such errors may be logged instead, leaving the
   * controller with an object model that is only partial from the intended one.
   *
   * @throws ControllerDefinitionNotFoundException
   *              If the startup could not be completed because no controller definition
   *              was found.
   */
  private void startup() throws ControllerDefinitionNotFoundException
  {
    ModelBuilder.SchemaVersion version = detectVersion();

    log.info(
        "\n\n" +
        "--------------------------------------------------------------------\n\n" +
        "  DEPLOYING NEW CONTROLLER RUNTIME...\n\n" +
        "--------------------------------------------------------------------\n"
    );

    switch (version)
    {
      case VERSION_3_0:

        modelBuilder = builders.get(ModelBuilder.SchemaVersion.VERSION_3_0);

        break;

      case VERSION_2_0:

        modelBuilder = builders.get(ModelBuilder.SchemaVersion.VERSION_2_0);

        break;

      default:

        throw new Error("Unrecognized schema version " + version);
    }

    // NOTE:  the schema 2.0 builder auto-starts all the sensors it locates in the
    //        controller.xml definition
    //
    // TODO: ORCJAVA-188
    //        generalizing the sensor start mechanism through a lifecycle interface --
    //        the builder should register the sensors and leave the lifecycle management
    //        to the managing framework

    modelBuilder.buildModel();

    Map<String, String> props = getConfigurationProperties();
    controllerConfig.setConfigurationProperties(props);
    
    log.info("Startup complete.");
  }


  /**
   * Sets this deployer in 'paused' state.
   *
   * @see #resume
   */
  private void pause()
  {
    isPaused = true;

    // TODO : ORCJAVA-188 -- pause() candidate for more generic lifecycle interface

    controllerDefinitionWatch.pause();
  }


  /**
   * Resumes this deployer from a previously 'paused' state.
   *
   * @see #pause
   */
  private void resume()
  {
    try
    {
      // TODO : ORCJAVA-188 -- resume() candidate for more generic lifecycle interface

      controllerDefinitionWatch.resume();
    }

    finally
    {
      isPaused = false;
    }
  }

  /**
   * A simplistic attempt at detecting which schema version we should use to build
   * the object model.
   *
   * TODO -- MODELER-256, ORCJAVA-189 : xml schema should include explicit version info
   *
   * @return  the detected schema version
   *
   * @throws  ControllerDefinitionNotFoundException
   *              if we can't find any controller definition files to load
   */
  private ModelBuilder.SchemaVersion detectVersion() throws ControllerDefinitionNotFoundException
  {

    // Check if 3.0 schema instance is in place (TODO : this doesn't actually exist yet...)

    if (Version30ModelBuilder.checkControllerDefinitionExists(controllerConfig))
    {
      return ModelBuilder.SchemaVersion.VERSION_3_0;
    }

    // Check for 2.0 schema instance...

    if (Version20ModelBuilder.checkControllerDefinitionExists(controllerConfig))
    {
      return ModelBuilder.SchemaVersion.VERSION_2_0;
    }

    // TODO - update message below once 3.0 is in place

    throw new ControllerDefinitionNotFoundException(
        "Could not find a controller definition to load at path ''{0}'' (for version 2.0)",
        Version20ModelBuilder.getControllerDefinitionFile(controllerConfig)
    );
  }


  /**
   * Extracts an OpenRemote deployment archive into a given target file directory.
   *
   * @param inputStream     Input stream for reading the ZIP archive. Note that this method will
   *                        attempt to close the stream on exiting.
   *
   * @param targetDir       URI that points to the root directory where the extracted files should
   *                        be placed. Note that the URI must be an absolute file URI.
   *
   * @throws ConfigurationException   If target file URI cannot be resolved, or if the target
   *                                  file path does not exist
   *
   * @throws IOException              If there was an unrecovable I/O error reading or extracting
   *                                  the ZIP archive. Note that errors on individual files within
   *                                  the archive may not generate exceptions but be logged as
   *                                  errors or warnings instead.
   */
  private void unzip(InputStream inputStream, URI targetDir) throws ConfigurationException,
                                                                    IOException
  {
    if (targetDir == null || targetDir.getPath().equals("") || !targetDir.isAbsolute())
    {
      throw new ConfigurationException(
          "Target dir must be absolute file: protocol URI, got '" + targetDir + +'.'
      );
    }

    File checkedTargetDir = new File(targetDir);

    if (!checkedTargetDir.exists())
    {
      throw new ConfigurationException("The path ''{0}'' doesn't exist.", targetDir);
    }

    ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(inputStream));
    ZipEntry zipEntry = null;

    BufferedOutputStream fileOutputStream = null;

    try
    {
      while ((zipEntry = zipInputStream.getNextEntry()) != null)
      {
        if (zipEntry.isDirectory())
        {
          continue;
        }

        try
        {
          URI extractFileURI = targetDir.resolve(new URI(null, null, zipEntry.getName(), null));

          log.debug("Resolved URI to ''{0}''", extractFileURI);

          File zippedFile = new File(extractFileURI);

          log.debug("Attempting to extract ''{0}'' to ''{1}''.", zipEntry, zippedFile);

          try
          {
            fileOutputStream = new BufferedOutputStream(new FileOutputStream(zippedFile));

            int b;

            while ((b = zipInputStream.read()) != -1)
            {
              fileOutputStream.write(b);
            }
          }

          catch (FileNotFoundException e)
          {
            log.error(
                "Could not extract ''{0}'' -- file ''{1}'' could not be created : {2}",
                e, zipEntry.getName(), zippedFile, e.getMessage()
            );
          }

          catch (IOException e)
          {
            log.warn(
                "Zip extraction of ''{0}'' to ''{1}'' failed : {2}",
                e, zipEntry, zippedFile, e.getMessage()
            );
          }

          finally
          {
            if (fileOutputStream != null)
            {
              try
              {
                fileOutputStream.close();

                log.debug("Extraction of ''{0}'' to ''{1}'' completed.", zipEntry, zippedFile);
              }

              catch (Throwable t)
              {
                log.warn(
                    "Failed to close file ''{0}'' : {1}",
                    t, zippedFile, t.getMessage()
                );
              }
            }

            if (zipInputStream != null)
            {
              if (zipEntry != null)
              {
                try
                {
                  zipInputStream.closeEntry();
                }

                catch (IOException e)
                {
                  log.warn(
                      "Failed to close ZIP file entry ''{0}'' : {1}",
                      e, zipEntry, e.getMessage()
                  );
                }
              }
            }
          }
        }

        catch (URISyntaxException e)
        {
          log.warn("Cannot extract {0} from zip : {1}", e, zipEntry, e.getMessage());
        }
      }
    }

    finally
    {
      try
      {
        if (zipInputStream != null)
        {
          zipInputStream.close();
        }
      }

      catch (IOException e)
      {
        log.warn(
            "Failed to close zip file : {0}", e, e.getMessage()
        );
      }

      if (fileOutputStream != null)
      {
        try
        {
          fileOutputStream.close();
        }

        catch (IOException e)
        {
          log.warn("Failed to close file : {0}", e, e.getMessage());
        }
      }
    }
  }


  /**
   * TODO
   *
   * @param resourcePath
   * @param config
   */
  private void copyLircdConf(URI resourcePath, ControllerConfiguration config)
  {
    File lircdConfFile = new File(resourcePath.resolve(Constants.LIRCD_CONF).getPath());
    File lircdconfDir = new File(config.getLircdconfPath().replaceAll(Constants.LIRCD_CONF, ""));

    try
    {
      if (lircdconfDir.exists() && lircdConfFile.exists())
      {
         // this needs root user to put lircd.conf into /etc.
         // because it's readonly, or it won't be modified.
         if (config.isCopyLircdconf())
         {
            FileUtils.copyFileToDirectory(lircdConfFile, lircdconfDir);
            log.info("copy lircd.conf to" + config.getLircdconfPath());
         }
      }
    }

    catch (IOException e)
    {
      log.error("Can't copy lircd.conf to " + config.getLircdconfPath(), e);
    }
  }



  /**
   * This is a helper method that translates the string based version keys from the DI
   * configuration to typesafe enums.
   *
   * @param builders    the model builder implementations (one per schema) configured for
   *                    this deploer
   *
   * @return            builder map with a schema as key and a model builder Java object
   *                    instance as value
   *
   *
   * @see ModelBuilder.SchemaVersion#toSchemaVersion(String)
   *
   * @throws InitializationException    if an unrecognized model builder schema version has been
   *                                    configured for this deployer
   */
  private Map<ModelBuilder.SchemaVersion, ModelBuilder> createTypeSafeBuilderMap(
      Map<String, ModelBuilder> builders) throws InitializationException
  {
    Map<ModelBuilder.SchemaVersion, ModelBuilder> map =
        new HashMap<ModelBuilder.SchemaVersion, ModelBuilder>();

    for (String key : builders.keySet())
    {
      ModelBuilder.SchemaVersion schema = ModelBuilder.SchemaVersion.toSchemaVersion(key);
      ModelBuilder builder = builders.get(key);

      builder.setDeployer(this);  //We inject the deployer manually, since Spring has a problem with circular dependencies

      map.put(schema, builder);
    }

    return map;

  }
  




  // Nested Classes -------------------------------------------------------------------------------


  /**
   * This service performs the automated file watching of the controller definition artifacts
   * (depending on the model builder implementation). <p>
   *
   * Per the rules defined in this implementation and in combination with those provided by
   * model builders via their
   * {@link org.openremote.controller.deployer.ModelBuilder#hasControllerDefinitionChanged()}
   * method implementations, this service controls the deployer lifecycle through
   * {@link org.openremote.controller.service.Deployer#softRestart()} and
   * {@link Deployer#softShutdown()} methods.
   *
   * @see org.openremote.controller.service.Deployer#softRestart()
   * @see org.openremote.controller.service.Deployer#softShutdown()
   */
  private static class ControllerDefinitionWatch implements Runnable
  {

    // TODO : ORCJAVA-188 -- should implement lifecycle interface


    // Instance Fields ----------------------------------------------------------------------------

    /**
     * Deployer reference for this service to control the deployer lifecycle.
     */
    private Deployer deployer;

    /**
     * Indicates the watcher thread is running.
     */
    private volatile boolean running = true;

    /**
     * Indicates the watcher thread should temporarily pause and not trigger any actions
     * on the deployer.
     */
    private volatile boolean paused = false;

    /**
     * The actual thread reference.
     */
    private Thread watcherThread;


    // Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new controller file watcher for a given deployer. Use {@link #start()} to
     * make this service active (start the relevant thread(s)).
     *
     * @param deployer  reference to the deployer whose lifecycle this watcher service controls
     */
    private ControllerDefinitionWatch(Deployer deployer)
    {
      this.deployer = deployer;
    }


    // Instance Methods ---------------------------------------------------------------------------

    /**
     * Starts the controller definition watcher thread.
     */
    public void start()
    {
      watcherThread = OpenRemoteRuntime.createThread(
          "Controller Definition File Watcher for " + deployer.name, this
      );

      watcherThread.start();

      log.info("{0} started.", watcherThread.getName());
    }


    /**
     * Stops (and kills) the controller definition watcher thread.
     */
    public void stop()
    {
      running = false;

      watcherThread.interrupt();
    }

    /**
     * Temporarily pauses the controller definition watcher thread, preventing any state
     * modifications to the associated deployment service.
     *
     * @see #resume
     */
    public void pause()
    {
      paused = true;
    }

    /**
     * Resumes the controller definition watcher thread after it has been {@link #pause() paused}.
     *
     * @see #pause
     */
    public void resume()
    {
      paused = false;
    }


    // Implements Runnable ------------------------------------------------------------------------

    /**
     * Runs the watcher thread using the following logic:  <p>
     *
     * - If paused, do nothing  <br>
     *
     * - If cannot detect controller definition files for any known schemas, keep waiting <br>
     *
     * - If detects a controller definition has been added but not deployed, run
     *   deployer.softRestart()  <br>
     *
     * - If has an existing controller object model deployed but the model builder reports
     *   a change in it (what constitutes a change depends on deployed model builder implementation),
     *   then run deployer.softRestart() <br>
     *
     * - If an existing controller model was deployed but the controller definition is removed
     *   (as reported by {@link org.openremote.controller.service.Deployer#detectVersion()})
     *   then undeploy the object model.
     */
    @Override public void run()
    {
      while (running)
      {
        if (paused)
          continue;

        try
        {
          deployer.detectVersion();     // will throw an exception if no known schemas are found...


          if (deployer.modelBuilder == null || deployer.modelBuilder.hasControllerDefinitionChanged())
          {
            try
            {
              deployer.softRestart();
            }

            catch (ControllerDefinitionNotFoundException e)
            {
              log.error(
                  "Soft restart cannot complete, controller definition not found : {0}",
                  e.getMessage()
              );
            }

            catch (Throwable t)
            {
              log.error(
                  "Controller soft restart failed : {0}",
                  t, t.getMessage()
              );
            }
          }
        }

        catch (ControllerDefinitionNotFoundException e)
        {
          if (deployer.modelBuilder != null)
          {
            deployer.softShutdown();
          }

          else
          {
            log.trace("Did not locate controller definitions for any known schema...");
          }
        }

        try
        {
          Thread.sleep(2000);
        }
        catch (InterruptedException e)
        {
          running = false;

          Thread.currentThread().interrupt();
        }
      }

      log.info("{0} has been stopped.", watcherThread.getName());
    }
  }




  /**
   * Abstracts the connectivity from controller to back-end in this nested class. <p>
   *
   * Currently only the deployer is making connections to backend. This will be later
   * expanded with local device discovery data import, log analytics, data collection
   * history, remote access and other operations. At that point this class visibility may
   * be expanded and made more generic.
   */
  private static class BeehiveConnection
  {

    /**
     * Part of the Beehive URL to retrieve user's controller configuration from their
     * account. <p>
     *
     * The complete URL should be :
     * [Beehive REST Base URL]/BEEHIVE_REST_USER_DIR/[username]/BEEHIVE_REST_OPENREMOTE_ZIP
     */
    private final static String BEEHIVE_REST_USER_DIR = "user";

    /**
     * Part of the Beehive URL to retrieve user's controller configuration from their
     * account. <p>
     *
     * The complete URL should be :
     * [Beehive REST Base URL]/BEEHIVE_REST_USER_DIR/[username]/BEEHIVE_REST_OPENREMOTE_ZIP
     */
    private final static String BEEHIVE_REST_OPENREMOTE_ZIP = "openremote.zip";

    /**
     * Reference to the deployer that uses this connection.
     */
    private Deployer deployer;


    // Constructors -------------------------------------------------------------------------------

    /**
     * Constructs a new connection object for a given deployer.
     *
     * @param deployer    reference to the deployer instance that owns this connection
     */
    private BeehiveConnection(Deployer deployer)
    {
      this.deployer = deployer;
    }


    // Instance Methods ---------------------------------------------------------------------------


    /**
     * Downloads user's configuration from Beehive using the user's account name and credentials.
     *
     * @param username      user's account name -- part of the HTTP GET URL used to retrieve the
     *                      account configuration
     * @param credentials   user's credentials to access their account
     *
     * @return  I/O stream to read the incoming ZIP file from user's account. Note that it is up
     *          to the caller to close the stream when appropriate. The incoming stream has basic
     *          buffering for read operations enabled.
     *
     * @throws ConfigurationException   if the connection to backend cannot be created due to
     *                                  configuration errors in the controller
     *
     * @throws ConnectionException      if the connection creation fails for any reason
     */
    private InputStream downloadZip(String username, String credentials)
        throws ConfigurationException, ConnectionException
    {
      // TODO :
      //   Could eventually return a list of URLs if multiple backend targets are enabled which
      //   can be used for transparent failover. See ORCJAVA-191.
      
      String beehiveBase = deployer.controllerConfig.getBeehiveRESTRootUrl();
      String httpURI = BEEHIVE_REST_USER_DIR + "/" + username +"/" + BEEHIVE_REST_OPENREMOTE_ZIP;

      try
      {
        URL beehiveBaseURL = new URL(beehiveBase);
        URI beehiveUserURI = beehiveBaseURL.toURI().resolve(httpURI);

        // TODO : Should force to go over HTTPS always. See ORCJAVA-192.

        URLConnection connection = beehiveUserURI.toURL().openConnection();

        if (!(connection instanceof HttpURLConnection))
        {
          throw new ConfigurationException(
              "The ''{0}'' property ''{1}'' must be a URL with http:// schema.",
              ControllerConfiguration.BEEHIVE_REST_ROOT_URL, beehiveBase
          );
        }

        HttpURLConnection http = (HttpURLConnection)connection;

        http.setDoInput(true);
        http.setRequestMethod("GET");

        http.addRequestProperty(Constants.HTTP_AUTHORIZATION_HEADER,
                                Constants.HTTP_BASIC_AUTHORIZATION + encode(username, credentials));

        http.connect();

        int response = http.getResponseCode();

        switch (response)
        {
          case HttpURLConnection.HTTP_OK:

            return new BufferedInputStream(http.getInputStream());

          case HttpURLConnection.HTTP_UNAUTHORIZED:
          case HttpURLConnection.HTTP_NOT_FOUND:

            throw new ConnectionException(
                "Authentication failed, please check your username and password."
            );

          default:

            throw new ConnectionException(
                "Connection to ''{0}'' failed, HTTP error code {1} - {2}",
                beehiveUserURI, response, http.getResponseMessage()
            );
        }
      }

      catch (MalformedURLException e)
      {
        throw new ConfigurationException(
            "Configuration property ''{0}'' with value ''{1}'' is not a valid URL : {2}",
            e, ControllerConfiguration.BEEHIVE_REST_ROOT_URL, beehiveBase, e.getMessage()
        );
      }

      catch (URISyntaxException e)
      {
        throw new ConfigurationException(
            "Invalid URI : {0}", e, e.getMessage()
        );
      }

      catch (ProtocolException e)
      {
        throw new ConnectionException(
            "Failed to create HTTP request : {0}", e, e.getMessage()
        );
      }

      catch (IOException e)
      {
        throw new ConnectionException(
            "Downloading account configuration failed : {0}", e, e.getMessage()
        );
      }
    }


    // TODO
    //
    //
    private String encode(String username, String password)
    {
      Md5PasswordEncoder encoder = new Md5PasswordEncoder();

      String encodedPwd = encoder.encodePassword(new String(password), username);

      if (username == null || encodedPwd == null)
      {
        return null;
      }

      return new String(Base64.encodeBase64((username + ":" + encodedPwd).getBytes()));
    }
  }


  /**
   * Handles the announcement of the controller via it's MAC address to Beehive.<br>
   * Once a user has linked this controller to an account and the returned ControllerDTO contains<br>
   * a AccountDTO with users, this thread is ending, but then the backend command agent is started<br>
   * to perform a regular check on beehive if a command is there for this controller
   * 
   *
   */
  private class ControllerAnnouncement extends Thread {
    ControllerAnnouncement() {
       super("ConntrollerAnnouncement");
    }

    public void run() {
       //As long as we are not linked to an account we periodically try to receive account info 
       while (true) {
          ClientResource cr = null;
          try {
             log.trace("Controller will announce " + NetworkUtil.getMACAddresses() + " as MAC address to beehive");
             cr = new ClientResource( controllerConfig.getBeehiveAccountServiceRESTRootUrl() + "controller/announce/"+ NetworkUtil.getMACAddresses());
             Representation r = cr.post(null);
             String str;
             str = r.getText();
             log.trace("Controller announcement received response >" + str + "<");
             GenericResourceResultWithErrorMessage res =new JSONDeserializer<GenericResourceResultWithErrorMessage>().use(null, GenericResourceResultWithErrorMessage.class).use("result", ControllerDTO.class).deserialize(str); 
             controllerDTO = (ControllerDTO)res.getResult();
             if ((controllerDTO != null) && (controllerDTO.getAccount() != null)) {
                break;
             }
          } catch (Exception e) {
             log.error("!!! Unable to announce controller MAC address to Beehive", e);
          } finally {
             if (cr != null) {
                cr.release();
             }
          }
          try { Thread.sleep(1000 * 30); } catch (InterruptedException e) {} //Let's wait 30 seconds
       }
       beehiveCommandCheckService.start(controllerDTO);
    }

  }

  
  /**
   * Handles the announcement of discovered devices.<br>
   * When discovered devices are available and the controller is linked to an account,<br>
   * those devices are sent to Beehive.
   */
  private class DiscoveredDevicesAnnouncement extends Thread {
     DiscoveredDevicesAnnouncement() {
       super("DiscoveredDevicesAnnouncement");
    }

    public void run() {
       while (true) {
          if (!discoveredDevicesToAnnounce.isEmpty()) {
             synchronized (discoveredDevicesToAnnounce) {
                ClientResource cr = new ClientResource(controllerConfig.getBeehiveDeviceDiscoveryServiceRESTRootUrl() + "discoveredDevices");
                if ((controllerDTO != null) && (controllerDTO.getAccount() != null)) {
                   UserDTO user = controllerDTO.getAccount().getUsers().get(0);
                   cr.setChallengeResponse(ChallengeScheme.HTTP_BASIC, user.getUsername(), user.getPassword());
                   try {   
                      Representation rep = new JsonRepresentation(new JSONSerializer().exclude("*.class").deepSerialize(discoveredDevicesToAnnounce));
                      Representation result = cr.post(rep);
                      GenericResourceResultWithErrorMessage res = new JSONDeserializer<GenericResourceResultWithErrorMessage>().use(null, GenericResourceResultWithErrorMessage.class).use("result", ArrayList.class).use("result.values", Long.class).deserialize(result.getText());
                      if (res.getErrorMessage() != null) {
                         throw new RuntimeException(res.getErrorMessage());
                      }
                      discoveredDevicesToAnnounce.clear();
                   } catch (Exception e) {
                     log.error("Could not announce discovered devices", e);
                   } finally {
                      if (cr != null) {
                         cr.release();
                      }
                   }
                }
             }
          }
          try { Thread.sleep(1000 * 60); } catch (InterruptedException e) {} //Let's wait one minute
       }
     }
  }


  public void unlinkController() {
     this.controllerDTO = null;
     this.controllerAnnouncement.start();
  }


}

