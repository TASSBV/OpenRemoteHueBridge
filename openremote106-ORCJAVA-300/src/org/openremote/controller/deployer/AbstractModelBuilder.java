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

import java.util.List;

import org.openremote.controller.service.Deployer;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.Constants;
import org.openremote.controller.exception.XMLParsingException;
import org.openremote.controller.exception.InitializationException;
import org.jdom.Element;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 * A common superclass implementation for different model builder implementations to share/reuse
 * code.  <p>
 *
 * Subclasses should provide implementations of {@link #build} and
 * {@link #readControllerXMLDocument()} methods. The build() method will be invoked by this
 * implementation after the default implementation of
 * {@link org.openremote.controller.deployer.ModelBuilder#buildModel()} has been executed.
 * The readControllerXMLDocument() should be implemented to read the XML definition of the
 * controller from file system.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public abstract class AbstractModelBuilder implements ModelBuilder
{

  /*
   *  TODO:
   *
   *    ORCJAVA-183 (http://jira.openremote.org/browse/ORCJAVA-183) : Refactor configuration
   *    service as part of the model
   *
   */



  // Class Members --------------------------------------------------------------------------------


  /**
   * Common log category for startup logging, with a specific sub-category for deployer.
   */
  protected final static Logger log = Logger.getLogger(Constants.DEPLOYER_LOG_CATEGORY);




  /**
   * Isolated this one method call to suppress warnings (JDOM API does not use generics).
   *
   * @param rootElement  the root element which child elements are retrieved
   *
   * @return  the child elements of the root element
   */
  @SuppressWarnings("unchecked")
  public static List<Element> getChildElements(Element rootElement)
  {
    return rootElement.getChildren();
  }


  // Instance Fields ------------------------------------------------------------------------------


  /**
   * Stores a JDOM Document reference to an in-memory node-tree of the controller's XML definition.
   */
  protected Document controllerXMLDefinition;
  
  /**
   * Reference to the deployer can be used by subclasses, if needed
   */
  protected Deployer deployer;




  // Public Instance Methods ----------------------------------------------------------------------


  /**
   * Returns the XML element from controller's XML definition matching the given "id" attribute
   * value. Id attribute values must be unique and only one element can be returned from the query
   * (multiple elements with same id will cause an exception to be thrown).
   *
   * @param  id      value of element's "id" attribute
   *
   * @return JDOM Element matching the given element id attribute
   *
   * @throws InitializationException
   *            if controller's XML definition cannot be read
   *
   * @throws XMLParsingException
   *            if element with given id was not found, if there were more than one element
   *            with the same id, or if for any other reason the execution of the XPath query
   *            fails
   */
  public Element queryElementById(int id) throws InitializationException
  {

    Element element = queryElementFromXML(controllerXMLDefinition, "*[@id='" + id + "']");

    if (element == null)
    {
      throw new XMLParsingException("No component found with id ''{0}''.", id);
    }

    return element;
  }



  // Implements ModelBuilder ----------------------------------------------------------------------


  /**
   * Provides a default implementation which reads the controller's XML definition into memory
   * (via {@link #readControllerXMLDocument()} method) and stores the XML document node-tree
   * in {@link #controllerXMLDefinition} field.  <p>
   *
   * Subclasses can override this method altogether or provide additional implementation via
   * {@link #build} method which this implementation will execute.
   */
  @Override public void buildModel()
  {
    try
    {
      controllerXMLDefinition = readControllerXMLDocument();

      build();
    }

    catch (InitializationException e)
    {
      log.error("Reading controller''s definition failed : {0}", e, e.getMessage());
    }
  }



  // Protected Instance Methods -------------------------------------------------------------------


  /**
   * Executes an XPath expression on the controller's XML definition using the
   * {@link ModelBuilder.SchemaVersion#OPENREMOTE_NAMESPACE} namespace.
   * This implementation is limited to XPath expressions that return a single
   * XML elements only.
   *
   * @param   doc          controller's XML definition
   *
   * @param   xPathQuery   XPath expression to return a single XML element. The given string
   *                       is prefixed with '//or:' for OpenRemote namespace.
   *
   * @return  One XML element or <tt>null</tt> if nothing was found
   *
   * @throws  XMLParsingException
   *            if there were errors creating the XPath expression or executing it
   */
  protected static Element queryElementFromXML(Document doc, String xPathQuery)
      throws XMLParsingException
  {
    if (xPathQuery == null || xPathQuery.equals(""))
    {
      throw new XMLParsingException(
          "Null or empty XPath expression for document {0}", doc
      );
    }

    xPathQuery = "//" + SchemaVersion.OPENREMOTE_NAMESPACE.getPrefix() + ":" + xPathQuery;

    try
    {
      XPath xpath = XPath.newInstance(xPathQuery);
      xpath.addNamespace(SchemaVersion.OPENREMOTE_NAMESPACE);

      List elements = xpath.selectNodes(doc);

      if (!elements.isEmpty())
      {
        if (elements.size() > 1)
        {
          throw new XMLParsingException(
              "Expression ''{0}'' matches more than one element : {1}",
              xPathQuery, elements.size()
          );
        }

        Object o = elements.get(0);

        if (o instanceof Element)
        {
          return (Element)o;
        }

        else
        {
          throw new XMLParsingException(
              "XPath query is expected to only return Element types, got ''{0}''", o.getClass()
          );
        }
      }

      else
      {
        return null;
      }
    }

    catch (JDOMException e)
    {
      throw new XMLParsingException(
          "XPath evaluation ''{0}'' failed : {1}", e, xPathQuery, e.getMessage()
      );
    }
  }

  
  /**
   * When the ModelBuilder is updating the commandFactory based on new config properties, the
   * deployer is needed in case the commandBuilder is using it.
   * @param deployer
   */
  public void setDeployer(Deployer deployer) {
    this.deployer = deployer;
  }



/**
   * Subclasses should implement this to include additional logic in addition to the default
   * implementation in {@link #buildModel()} method.
   */
  protected abstract void build();

  
  /**
   * Subclasses should implement to return an XML document instance that contains the controller's
   * XML definition. 
   *
   * @return    reference to XML document instance that contains controller's definition
   *
   *
   * @throws    InitializationException   if the XML document instance cannot be read for
   *                                      any reason
   */
  protected abstract Document readControllerXMLDocument() throws InitializationException;

}

