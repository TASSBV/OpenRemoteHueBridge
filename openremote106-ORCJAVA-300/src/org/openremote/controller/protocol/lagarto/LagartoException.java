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

package org.openremote.controller.protocol.lagarto;

import org.openremote.controller.utils.Logger;

/**
 * Main exception class
 */
public class LagartoException extends Exception
{
  /**
   * Logger
   */
  private final static Logger logger = Logger.getLogger(LagartoCommandBuilder.LAGARTO_PROTOCOL_LOG_CATEGORY);

  /**
  * Description of the exception
  */
  private String description;

  /**
  * Class constructor
  */
  public LagartoException(String message)
  {
    super(message);
    description = message;
  }

  /**
  * Log exception as an error
  */
  public void logError()
  {
    logger.error("LagartoException: " + description);
  }

  /**
  * Log exception as an info
  */
  public void logInfo()
  {
    logger.info("LagartoException: " + description);
  }

  /**
  * out
  *
  * Display exception on the stdout output
  */
  public void out()
  {
    System.out.println("Exception: " + description);
    if (this.getCause() != null)
      System.out.println("Origin: " + this.getCause().toString());
  }
}
