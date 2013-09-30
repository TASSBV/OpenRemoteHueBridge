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
package org.openremote.controller.command;

import org.jdom.Element;

/**
 * Generic interface for all protocol implementations. <p>
 *
 * The command builder interface is responsible for taking the XML definition from the
 * controller.xml file and turning it into Java object instances (which must in turn
 * implement the Command interface).  <p>
 *
 * The generic structure of all command XML definitions is following:
 *
 * <pre>{@code
 * <command protocol = "protocol-id" >
 *   <property name = "argname1" value = "..."/>
 *   <property name = "argname2" value = "..."/>
 *   <property name = "argnameX" value = "..."/>
 * </command>
 * }</pre>
 *
 * Where each command is represented by its own element with an arbitrary number of property
 * elements as its children. Properties are defined as name, value pairs. <p>
 *
 * Note: at the moment this API definition uses JDOM library for XML parsing.
 *
 * @see Command
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 * @author Handy.Wang 2009-10-15
 */
public interface CommandBuilder
{

  // Constants ------------------------------------------------------------------------------------

  /**
   * String constant for the top level command element protocol attribute: ("{@value}")
   */
  public final static String PROTOCOL_ATTRIBUTE_NAME = "protocol";

  /**
   * String constant for the child property list elements: ("{@value}")
   */
  public final String XML_ELEMENT_PROPERTY       = "property";

  /**
   * String constant for the property name attribute.
   */
  public final String XML_ATTRIBUTENAME_NAME     = "name";

  /**
   * String constant for the property value attribute.
   */
  public final String XML_ATTRIBUTENAME_VALUE    = "value";


  // Methods --------------------------------------------------------------------------------------

  /**
   * Implements XML parsing of the command definition using JDOM API.  <p>
   *
   * The element contains the expected XML structure passed by the runtime. The implementations
   * of this interface should parse the associated property list and handle protocol specific
   * configuration.  <p>
   *
   * As a result, return a fully configured, protocol specific command instance.
   *
   * @param element     contains JDOM instances of the command XML snippet to parse
   *
   * @return a fully configured and initialized protocol command instance
   *
   * @throws org.openremote.controller.exception.NoSuchCommandException if the parsing cannot
   *          be completed for any reason
   *
   */
  Command build(Element element);
   
}
