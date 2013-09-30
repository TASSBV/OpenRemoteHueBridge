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

import org.openremote.controller.model.Command;
import org.openremote.controller.exception.InitializationException;
import org.openremote.controller.exception.XMLParsingException;
import org.jdom.Element;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public interface DeviceProtocolBuilder
{
  /**
   * TODO
   *
   * The actual builder implementation. Currently with JDOM dependency.
   *
   * @param segmentChildElements  root element to parse Java object from
   *
   * @return  Java instance
   *
   * @throws org.openremote.controller.exception.InitializationException
   *              if the build process encounters an irrecovable error
   */
  Command build(Element segmentChildElements) throws XMLParsingException;


}
