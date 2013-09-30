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
package org.openremote.controller.service.impl;

import java.util.List;

import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.component.Component;
import org.openremote.controller.component.ComponentFactory;
import org.openremote.controller.component.control.Control;
import org.openremote.controller.exception.ConfigurationException;
import org.openremote.controller.exception.InitializationException;
import org.openremote.controller.exception.XMLParsingException;
import org.openremote.controller.service.ControlCommandService;
import org.openremote.controller.service.Deployer;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.utils.MacrosIrDelayUtil;


/**
 * TODO
 * 
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 * @author Handy.Wang
 */
public class ControlCommandServiceImpl implements ControlCommandService
{

  // Class Members --------------------------------------------------------------------------------

  /**
   * Logging category for runtime execution of commands.
   */
  private final static Logger log = Logger.getLogger(Constants.RUNTIME_COMMAND_EXECUTION_LOG_CATEGORY);


  // Private Instance Fields ----------------------------------------------------------------------

  private Deployer deployer;
  private ComponentFactory componentFactory;



  // Constructors ---------------------------------------------------------------------------------

  public ControlCommandServiceImpl(Deployer deployer, ComponentFactory cf)
  {
    this.deployer = deployer;
    this.componentFactory = cf;
  }


  // Public Instance Methods ----------------------------------------------------------------------

  public void trigger(String controlID, String commandParam)
  {
    try
    {
      Control control = getControl(controlID, commandParam);
      List<ExecutableCommand> executableCommands = control.getExecutableCommands();
      MacrosIrDelayUtil.ensureDelayForIrCommand(executableCommands);

      for (ExecutableCommand executableCommand : executableCommands)
      {
        if (executableCommand == null)
        {
          log.warn(
              "Got a null entry in write command list. This may indicate an implementation error. " +
              "Skipping and will continue processing..."
          );
          
          continue;
        }

        executableCommand.send();
      }
    }

    // Am currently catching all (checked) exceptions here and not propagating them further.
    // This leaves the caller with no status on whether their executed write command was
    // succesful.
    //
    // If necessary, the API here can be modified to return structured information about
    // errors (in all likelyhood, some error information does pass through due to the extensive
    // use of unchecked exceptions in the legacy code).
    //                                                                                    [JPL]

    catch (XMLParsingException exception)
    {
      // Errors related to controller.xml protocol mappings...

      log.warn(
          "The required components to execute remote control (Component ID = {0}) could not be " +
          "parsed from {1} : {2}", exception, controlID, Constants.CONTROLLER_XML, exception.getMessage()
      );
    }

    catch (ConfigurationException exception)
    {
      // Errors related to applicationContext.xml and other controller configuration...

      log.warn(
          "Write command execution of component ID = {0} could not be completed due to " +
          "controller configuration error : {1}", exception, controlID, exception.getMessage()
      );  
    }

    catch (InitializationException exception)
    {
      // Other types of init errors...

      log.error(
          "CONTROLLER ERROR: Unable to execute write command of component ID = {0}. " +
          "Error message : {1}", exception, controlID, exception.getMessage()
      );
    }
  }




  // Private Instance Methods ---------------------------------------------------------------------

  private Control getControl(String controlID, String commandParam) throws InitializationException
  {
    Element controlElement = deployer.queryElementById(Integer.parseInt(controlID));

    if (controlElement == null)
    {
       throw new XMLParsingException(
           "Component with ID = {0} not found in {1}.", controlID, Constants.CONTROLLER_XML
       );
    }

    Component component = componentFactory.getComponent(controlElement, commandParam);

    if (component instanceof Control)
    {
      return (Control)component;
    }

    else
    {
      throw new ConfigurationException(
          "Component with ID = {0} is not a control component type : {1}",
          controlID, component.getClass().getName()
      );
    }
  }

}
