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

import org.openremote.controller.Constants;

/**
 * The exception class when panel.xml Not Found.
 * 
 * @author Dan 2009-5-22
 */
@SuppressWarnings("serial")
public class PanelXMLNotFoundException extends ControlCommandException
{

  /**
   * Instantiates a new controller xml not found exception.
   */
  public PanelXMLNotFoundException()
  {
    super("*" + Constants.PANEL_XML + "* not found.");
    setErrorCode(Constants.HTTP_RESPONSE_PANEL_XML_NOT_DEPLOYED);
  }

  /**
   * Instantiates a new controller xml not found exception.
   *
   * @param message the message
   * @param cause the cause
   */
  public PanelXMLNotFoundException(String message, Throwable cause)
  {
    super("*" + Constants.PANEL_XML + "* not found." + message, cause);
    setErrorCode(Constants.HTTP_RESPONSE_PANEL_XML_NOT_DEPLOYED);
  }

  /**
   * Instantiates a new controller xml not found exception.
   *
   * @param message the message
   */
  public PanelXMLNotFoundException(String message)
  {
    super("*" + Constants.PANEL_XML + "* not found." + message);
    setErrorCode(Constants.HTTP_RESPONSE_PANEL_XML_NOT_DEPLOYED);
  }


}
