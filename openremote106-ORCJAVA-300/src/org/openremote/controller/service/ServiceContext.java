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
package org.openremote.controller.service;

import org.openremote.controller.*;
import org.openremote.controller.net.IPAutoDiscoveryServer;
import org.openremote.controller.net.RoundRobinTCPServer;
import org.openremote.controller.net.RoundRobinUDPServer;
import org.openremote.controller.statuscache.StatusCache;
import org.openremote.controller.utils.Logger;

/**
 * This class defines an abstract service context without compile time links to any particular
 * service container implementation. <p>
 *
 * Service context can be instantiated/registered only once per VM/classloader. The subclasses
 * are expected to register themselves directly from their constructor via
 * {@link #registerServiceContext(ServiceContext)} method. Only one instance can be registered. <p>
 *
 * The actual service provider is bound at runtime and can therefore vary according to deployment
 * environment.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public abstract class ServiceContext
{


  // Tasks TODO :
  //
  //        the various configuration related services (e.g. lutron) need a better API (higher
  //        degree of de-typing) -- these should be part of the configuration refactoring as
  //        described in ORCJAVA-183 : http://jira.openremote.org/browse/ORCJAVA-183
  //
  //        Reduce dependencies to deprecated getDeployer() API -- ORCJAVA-193, ORCJAVA-173
  //
  //        Remove direct use of subclass SpringContext API -- ORCJAVA-195



  // Constants ------------------------------------------------------------------------------------

  /**
   * Mutex for service context singleton to protect against multi-thread race conditions.
   */
  private final static Object SINGLETON_MUTEX = new Object();


  // Enums ----------------------------------------------------------------------------------------


  /**
   * TODO : This is a temporary refactoring construct.
   *
   *        This enum encapsulates the spring configuration specific bean names and replaces
   *        them with typed enum identifiers to reduce sprinkling bean names all over the codebase.
   *
   *        These names are used where a direct lookup to spring context has been made. Most of
   *        them should be ultimately unnecessary and to be replaced with injecting dependent
   *        references instead (see various to-dos and JIRA references below). Some others may
   *        still need to be (temporarily) added to reduce direct API calls to SpringContext
   *        (see ORCJAVA-195)
   */
  public static enum ServiceName
  {

    DEPLOYER("deployer"),                                     // TODO : Deprecated, see ORCJAVA-173 ORCJACA-193
    CONTROLLER_CONFIGURATION("configuration"),                // TODO : To be removed, see ORCJAVA-183
    ROUND_ROBIN_CONFIGURATION("roundRobinConfig"),            // TODO : To be removed, see ORCJAVA-183
    LUTRON_HOMEWORKS_CONFIGURATION("lutronHomeWorksConfig"),  // TODO : To be removed, see ORCJAVA-183
    AMX_NI_CONFIGURATION("AMXNIConfig"),                      // TODO : To be removed, see ORCJAVA-183
    DEVICE_STATE_CACHE("statusCache"),                        // TODO : Deprecated, see ORCJAVA-197
    COMPONENT_CONTROL_SERVICE("controlCommandService"),       // TODO : should be retrieved through deployer interface
    DENONAVRSERIAL_CONFIGURATION("denonAVRSerialConfiguration"), // TODO : To be removed, see ORCJAVA-183
    HUEBRIDGE_CONFIGURATION("HuebridgeConfig");

    private String springBeanName;

    private ServiceName(String springBeanName)
    {
      this.springBeanName = springBeanName;
    }


    public String getSpringBeanName()
    {
      return springBeanName;
    }
  }


  // Class Members --------------------------------------------------------------------------------

  /**
   * Direct logging to INIT log category.
   */
  private final static Logger log = Logger.getLogger(Constants.INIT_LOG_CATEGORY);

  /**
   * Service context singleton instance.
   */
  private static ServiceContext singletonInstance;


  /**
   * TODO :
   *   This is temporary and should go away with configuration refactoring as part of the
   *   deployment unit, see ORCJAVA-183 : http://jira.openremote.org/browse/ORCJAVA-183
   */
  public static ControllerConfiguration getControllerConfiguration()
  {
    try
    {
      return (ControllerConfiguration)getInstance().getService(ServiceName.CONTROLLER_CONFIGURATION);
    }

    catch (ClassCastException e)
    {
      throw new Error(
          "Controller Configuration service implementation has had an incompatible change.", e
      );
    }
  }

  /**
   * TODO :
   *   This is temporary and should go away with configuration refactoring as part of the
   *   deployment unit, see ORCJAVA-183 : http://jira.openremote.org/browse/ORCJAVA-183
   */
  public static RoundRobinConfiguration getRoundRobinConfiguration()
  {
    try
    {
      return (RoundRobinConfiguration)getInstance().getService(ServiceName.ROUND_ROBIN_CONFIGURATION);
    }

    catch (ClassCastException e)
    {
      throw new Error(
          "Roundrobin Configuration service implementation has had an incompatible change.", e
      );
    }
  }

  /**
   * TODO :
   *   This is temporary and should go away with configuration refactoring as part of the
   *   deployment unit, see ORCJAVA-183 : http://jira.openremote.org/browse/ORCJAVA-183
   */
  public static LutronHomeWorksConfig getLutronHomeWorksConfiguration()
  {
    try
    {
      return (LutronHomeWorksConfig)getInstance().getService(ServiceName.LUTRON_HOMEWORKS_CONFIGURATION);
    }

    catch (ClassCastException e)
    {
      throw new Error(
          "Lutron HomeWorks Configuration service has had an incompatible change.", e
      );
    }
  }

  /**
   * TODO :
   *   This is temporary and should go away with configuration refactoring as part of the
   *   deployment unit, see ORCJAVA-183 : http://jira.openremote.org/browse/ORCJAVA-183
   */
  public static AMXNIConfig getAMXNiConfiguration()
  {
    try
    {
      return (AMXNIConfig)getInstance().getService(ServiceName.AMX_NI_CONFIGURATION);
    }

    catch (ClassCastException e)
    {
      throw new Error(
          "AMX Ni Configuration service has had an incompatible change.", e
      );
    }
  }


    public static HuebridgeConfig getHuebridgeConfiguration()
    {
        try
        {
            return (HuebridgeConfig)getInstance().getService(ServiceName.HUEBRIDGE_CONFIGURATION);
        }

        catch (ClassCastException e)
        {
            throw new Error(
                    "Huebridge Configuration service has had an incompatible change.", e
            );
        }
    }
  /**
   * TODO :
   *   See ORCJAVA-193 (http://jira.openremote.org/browse/ORCJAVA-193)
   *   See ORCJAVA-173 (http://jira.openremote.org/browse/ORCJAVA-173)
   *
   *
   * @deprecated  This is deprecated and should go away with refactoring of the configuration
   *              to become part of deployment unit (see ORCJAVA-183) and creating a REST admin
   *              interface for the controller (see ORCJAVA-173).
   */
  @Deprecated public static Deployer getDeployer()
  {
    try
    {
      return (Deployer)getInstance().getService(ServiceName.DEPLOYER);
    }

    catch (ClassCastException e)
    {
      throw new Error(
          "Deployer service has had an incompatible change.", e
      );
    }
  }

  public static DenonAVRSerialConfiguration getDenonAVRSerialConfiguration()
  {
    try
    {
      return (DenonAVRSerialConfiguration)getInstance().getService(ServiceName.DENONAVRSERIAL_CONFIGURATION);
    }

    catch (ClassCastException e)
    {
      throw new Error(
          "Denon AVR Serial Configuration service has had an incompatible change.", e
      );
    }
  }


  /**
   * TODO : See ORCJAVA-197 -- http://jira.openremote.org/browse/ORCJAVA-197
   *
   * @deprecated See ORCJAVA-197
   */
  @Deprecated public static StatusCache getDeviceStateCache()
  {
    try
    {
      return (StatusCache)getInstance().getService(ServiceName.DEVICE_STATE_CACHE);
    }

    catch (ClassCastException e)
    {
      throw new Error(
          "Device state cache service implementation has had an incompatible change.", e
      );
    }
  }

  /**
   * TODO :
   *   currently used to access from REST servlet implementations -- should expose a single
   *   (deployer?) reference in WAR application context that enables reference access to
   *   other services as necessary
   */
  public static ControlCommandService getComponentControlService()
  {
    try
    {
      return (ControlCommandService)getInstance().getService(ServiceName.COMPONENT_CONTROL_SERVICE);
    }

    catch (ClassCastException e)
    {
      throw new Error(
          "Component control service implementation has had an incompatible change.", e
      );
    }
  }


  /**
   * TODO
   */
  protected static ServiceContext getInstance()   
  {
    synchronized (SINGLETON_MUTEX)
    {
      if (singletonInstance == null)
      {
        throw new IllegalStateException(
            "An attempt was made to access service context before it was initialized."
        );
      }

      else
      {
        return singletonInstance;
      }
    }
  }


  /**
   * TODO
   */
  protected static void registerServiceContext(ServiceContext ctx) throws InstantiationException
  {
    synchronized (SINGLETON_MUTEX)
    {
      if (singletonInstance != null)
      {
        throw new InstantiationException(
            "A service context has already been initialized and registered. " +
            "This registration can only be done once."
        );
      }

      else
      {
        singletonInstance = ctx;
      }
    }
  }


  // Constructors ---------------------------------------------------------------------------------

  /**
   * TODO
   */
  protected ServiceContext() { }




  // Public Instance Methods ----------------------------------------------------------------------


  public void init()
  {
    // TODO : this could be called from the constructor, rather than requiring explicit init() API call

    try
    {
      Thread t = OpenRemoteRuntime.createThread("Controller Auto-Discovery", new IPAutoDiscoveryServer());
      t.start();

      Thread.sleep(10);   // TODO : makes no sense
    }

    catch (InterruptedException e)
    {
      log.error("Autodiscovery server failed to start : {0}", e.getMessage());
    }

    try
    {
      Thread udp = OpenRemoteRuntime.createThread("Cluster UDP", new RoundRobinUDPServer());
      udp.start();

      Thread.sleep(10);   // TODO : makes no sense

      Thread tcp = OpenRemoteRuntime.createThread("Cluster TCP", new RoundRobinTCPServer());
      tcp.start();

      Thread.sleep(10);   // TODO : makes no sense
    }

    catch (InterruptedException e)
    {
      log.error("Cluster failed to start : {0}", e.getMessage());
    }

    // allow for service container specific initialization...
    
    initializeController();
  }



  // Abstract Methods -----------------------------------------------------------------------------


  /**
   * Returns a service implementation by the given service name. This is customizable per
   * different runtimes (Java SE, Android, etc.). This also abstracts away compile-time
   * dependencies to any particular bean-binding or service frameworks. The concrete
   * implementations are free to use whichever framework or API mechanisms to retrieve and
   * return the requested service implementations.
   *
   * @param   name    service name
   * @return  service implementation
   */
  protected abstract Object getService(ServiceName name);


  /**
   * An explicit call made by the service context to the concrete implementations allowing
   * them to implement DI framework specific initialization, if necessary.  <p>
   *
   * This in general is application (JVM) wide initialization, and is therefore called once
   * per controller's lifecycle -- should not be confused with the deployment lifecycle which
   * may occur multiple times within controller process lifetime.
   */
  protected abstract void initializeController();
  
}
