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
package org.openremote.controller.model;

import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class XMLMapping
{

  // Constants ------------------------------------------------------------------------------------

  public final static String XML_DECLARATION_UTF8 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

  public final static String XML_DOCUMENT_INDENT = "  ";



  // Include Element XML Constants ----------------------------------------------------------------

  /**
   * Include element name in controller.xml file, i.e.
   *
   * <pre>
   * {@code
   *       <include type = "xxx" ref = "nnn" />
   * }</pre>
   */
  public final static String XML_INCLUDE_ELEMENT_NAME = "include";

  /**
   * Include element 'type' attribute name in controller.xml file, i.e.
   *
   * <pre>
   * {@code
   *       <include type = "xxx" ref = "nnn"/>
   * }</pre>
   */
  public final static String XML_INCLUDE_ELEMENT_TYPE_ATTR = "type";

  /**
   * Include element 'type' attribute value 'sensor' in controller.xml file, i.e.
   *
   * <pre>
   * {@code
   *       <include type = "sensor" ref = "nnn"/>
   * }</pre>
   */
  public final static String XML_INCLUDE_ELEMENT_TYPE_SENSOR = "sensor";

  /**
   * Include element 'type' attribute value 'command' in controller.xml file, i.e.
   *
   * <pre>
   * {@code
   *       <include type = "command" ref = "nnn"/>
   * }</pre>
   */
  public final static String XML_INCLUDE_ELEMENT_TYPE_COMMAND = "command";

  /**
   * Include element 'ref' attribute in controller.xml file, i.e.
   *
   * <pre>
   * {@code
   *       <include type = "sensor" ref = "nnn"/>
   * }</pre>
   */
  public final static String XML_INCLUDE_ELEMENT_REF_ATTR = "ref";



  



  // Class Members --------------------------------------------------------------------------------

  
  protected static String getMandatoryAttributeValue(Node element, String attributeName)
      throws XMLSchemaException
  {
    NamedNodeMap attrs = element.getAttributes();

    if (attrs == null)
    {
      throw new XMLSchemaException(
          "Missing mandatory attribute '" + attributeName +
          "' in <" + element.getNodeName() + "> element."
      );
    }


    Node attribute = attrs.getNamedItem(attributeName);

    if (attribute == null)
    {
      throw new XMLSchemaException(
          "Missing mandatory attribute '" + attributeName +
          "' in <" + element.getNodeName() + "> element."
      );
    }

    return attribute.getNodeValue();
  }

}

