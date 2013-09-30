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
package org.openremote.controller;

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.openremote.controller.utils.Logger;

/**
 * TODO :
 *
 *   This is a place holder for JVM wide services in the controller. Should only have one
 *   instance per VM. Handles system level resources such as thread pooling.
 *
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class OpenRemoteRuntime
{

  /**
   * Direct logging to INIT log category.
   */
  private final static Logger log = Logger.getLogger(Constants.INIT_LOG_CATEGORY);


  static
  {
    try
    {
      // ----- BEGIN PRIVILEGED CODE --------------------------------------------------------------

      AccessController.doPrivilegedWithCombiner(new PrivilegedAction<Void>()
      {
        @Override public Void run()
        {
          openremoteThreadGroup = new ThreadGroup("OpenRemote Controller");

          return null;
        }
      });

      // ----- END PRIVILEGED CODE ----------------------------------------------------------------
    }

    catch (SecurityException e)
    {
      log.warn(
          "Unable to create OpenRemote thread group. Will default to security manager's " +
          "thread group : {0}", e, e.getMessage()
      );
    }
    catch (Throwable t)
    {
      log.warn(
          "Unable to create thread group. Will default to callers thread group : {0}",
          t, t.getMessage()
      );
    }
  }

  private static ThreadGroup openremoteThreadGroup = null;


  /**
   * TODO :
   *
   *   At the moment just a  centralized API for creating threads. Eventually all thread
   *   creation should be delegated here and this implementation should be backed by system
   *   wide thread pool.
   */
  public static Thread createThread(String name, Runnable runnable)
  {
    Thread thread = new Thread(openremoteThreadGroup, runnable, name);

    return thread;
  }

}

