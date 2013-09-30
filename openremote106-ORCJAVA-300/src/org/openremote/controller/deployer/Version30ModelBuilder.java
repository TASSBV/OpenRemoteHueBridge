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
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.jdom.Document;
import org.openremote.controller.ControllerConfiguration;
import org.openremote.controller.exception.InitializationException;


/**
 * TODO :
 *
 *   placeholder for the next major schema version that is currently in planning stages.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class Version30ModelBuilder extends AbstractModelBuilder
{


  // Class Members --------------------------------------------------------------------------------


  /**
   * Utility method to isolate the privileged code block for file read access check (exists)
   *
   * @param   config    controller's user configuration
   *
   * @return  true if file exists; false if file does not exists or was denied by
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
   * controller runtime object model definition.
   *
   * @param   config    controller's user configuration
   *
   * @return  file representing an object model definition for a controller
   */
  private static File getControllerDefinitionFile(ControllerConfiguration config)
  {
    String uri = new File(config.getResourcePath()).toURI().resolve("openremote.xml").getPath();

    return new File(uri);
  }



  // Implements ModelBuilder ----------------------------------------------------------------------


  @Override public boolean hasControllerDefinitionChanged()
  {
    return false;
  }


  // Implements AbstractModelBuilder --------------------------------------------------------------

  @Override protected void build()
  {
    // nothing here yet...
  }

  @Override protected Document readControllerXMLDocument()
  {
    return null;
  }


}

