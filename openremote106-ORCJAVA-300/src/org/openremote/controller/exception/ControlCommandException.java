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
package org.openremote.controller.exception;

/**
 * TODO
 * 
 * @author Dan 2009-6-2
 */
@SuppressWarnings("serial")
public class ControlCommandException extends ControllerException
{
   
  public final static int COMMAND_BUILDER_ERROR = 418;

  public final static int NO_SUCH_COMPONENT = 419;

  public final static int NO_SUCH_COMMAND_BUILDER = 420;

  public final static int INVALID_COMMAND_TYPE = 421;

  public final static int CONTROLLER_XML_NOT_FOUND = 422;

  public final static int NO_SUCH_COMMAND = 423;

  public final static int INVALID_POLLING_URL = 425;

  public final static int INVALID_PANEL_XML = 427;

  public final static int INVALID_ELEMENT = 429;

  public final static int INVALID_REFERENCE = 430;


  public ControlCommandException()
  {
    super();
  }

  public ControlCommandException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public ControlCommandException(String message)
  {
    super(message);
  }

  public ControlCommandException(Throwable cause)
  {
    super(cause);
  }

}
