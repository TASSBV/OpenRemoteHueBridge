/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.controller.rest;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.openremote.controller.Constants;
import org.openremote.controller.rest.support.json.JSONTranslator;
import org.apache.log4j.Logger;

/**
 * This superclass contains the common implementation elements for the OpenRemote Controller
 * HTTP/REST API.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public abstract class RESTAPI extends HttpServlet
{

  public static enum ResponseType { APPLICATION_XML, APPLICATION_JSON, TEXT_JAVASCRIPT }


  // Class Members --------------------------------------------------------------------------------

  /**
   * Common log category for HTTP REST API.
   */
  private final static Logger logger = Logger.getLogger(Constants.REST_ALL_PANELS_LOG_CATEGORY);


  // TODO :
  //   once all REST API servlets have been migrated to RESTAPI subclasses, this can become
  //   an instance method
  //                                                                    [JPL]
  //
  public static String composeXMLErrorDocument(int errorCode, String errorMessage)
  {
     StringBuffer sb = new StringBuffer();
     sb.append(Constants.STATUS_XML_HEADER);
     sb.append("\n<error>\n");
     sb.append("  <code>");
     sb.append(errorCode);
     sb.append("</code>\n");

     sb.append("  <message>");
     sb.append(errorMessage);
     sb.append("</message>\n");
     sb.append("</error>\n");
     sb.append(Constants.STATUS_XML_TAIL);
    
     return sb.toString();
  }

  // Servlet Implementation -----------------------------------------------------------------------
  @Override protected void doOptions(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
  {
     /*
      *  This is the HTTP Method that CORS uses for it's pre-flight check just need to
      *  return headers and no content.
      */
     String acceptHeader = request.getHeader(Constants.HTTP_ACCEPT_HEADER);
     
     // Add HTML5 CORS headers
     response.addHeader("Access-Control-Allow-Origin", "*");
     response.addHeader("Access-Control-Allow-Methods", "GET, POST");
     response.addHeader("Access-Control-Allow-Headers", "origin, authorization, accept");
     response.addHeader("Access-Control-Max-Age", "99999");
     
     if (Constants.MIME_APPLICATION_JSON.equalsIgnoreCase(acceptHeader))
     {
       response.setContentType(Constants.MIME_APPLICATION_JSON);
     }
     else if (Constants.MIME_TEXT_JAVASCRIPT.equalsIgnoreCase(acceptHeader))
     {
        response.setContentType(Constants.MIME_TEXT_JAVASCRIPT);
     }
     else
     {
       response.setContentType(Constants.MIME_APPLICATION_XML);
     }
     
     response.setStatus(200);
     response.getWriter().flush();
  }
  
  @Override protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
  {
    doPost(request, response);
  }

  @Override protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
  {
    // Get the 'accept' header from client -- this will indicate whether we will send
    // application/xml or application/json response...     
    String acceptHeader = request.getHeader(Constants.HTTP_ACCEPT_HEADER);
    ResponseType responseType;
    
    // Set character encoding...

    response.setCharacterEncoding(Constants.CHARACTER_ENCODING_UTF8);

    // Add HTML5 CORS header
    response.addHeader("Access-Control-Allow-Origin", "*");
    
    // Set the response content type to either 'application/xml' or 'application/json'
    // according to client's 'accept' header...

    if (Constants.MIME_APPLICATION_JSON.equalsIgnoreCase(acceptHeader))
    {
      response.setContentType(Constants.MIME_APPLICATION_JSON);
      responseType = ResponseType.APPLICATION_JSON;
    }
    
    else if (Constants.MIME_TEXT_JAVASCRIPT.equalsIgnoreCase(acceptHeader))
    {
       response.setContentType(Constants.MIME_TEXT_JAVASCRIPT);
       responseType = ResponseType.TEXT_JAVASCRIPT;       
    }

    else
    {
      // Currently if we don't recognize accept type, default to 'application/xml'...

      response.setContentType(Constants.MIME_APPLICATION_XML);

      responseType = ResponseType.APPLICATION_XML;
    }
    
    // Store response type in request object
    request.setAttribute("responseType", responseType);

    try
    {
      handleRequest(request, response);
    }
    catch (Throwable t)
    {
      logger.error("Error in handling REST API response: " + t.getMessage(), t);

      response.setStatus(500);
    }
    finally
    {
      response.getWriter().flush();
    }
  }



  protected abstract void handleRequest(HttpServletRequest request, HttpServletResponse response);



  protected void sendResponse(HttpServletRequest request, HttpServletResponse response, String xml)
  {
    ResponseType responseType = ResponseType.APPLICATION_XML;
    Object obj = request.getAttribute("responseType");
    if (obj != null) {
       responseType = (ResponseType)obj;
    }
    try
    {
      switch (responseType)
      {
        case APPLICATION_JSON:
          response.getWriter().print(JSONTranslator.translateXMLToJSON(request, response, xml));
          break;
        case TEXT_JAVASCRIPT:
           // Additional JSON Formatter implemented to ensure JSONArray output where required
           String output = JSONTranslator.translateXMLToJSONP(request, response, xml);
           response.getWriter().print(output);
           break;           
        case APPLICATION_XML:     // fall through to default...
        default:

          response.getWriter().print(xml);

          break;
      }
    }
    catch (IOException e)
    {
      logger.error("Unable to write response: " + e.getMessage(), e);
    }
  }


  protected void sendResponse(HttpServletRequest request, HttpServletResponse response, int errorCode, String message)
  {
     ResponseType responseType = ResponseType.APPLICATION_XML;
     Object obj = request.getAttribute("responseType");
     if (obj != null) {
        responseType = (ResponseType)obj;
     }
    switch (responseType)
    {
      case APPLICATION_XML:

        response.setStatus(errorCode);

        break;

      case APPLICATION_JSON:

        // TODO :
        //
        //    The JSON implementation is inconsistent with regards to how to handle
        //    error responses -- on one hand HTTP return code field should be used but
        //    on other parts of the implementation HTTP OK is preferred as return
        //    code for cases where the error is returned as an document response.
        //
        //    Whatever is the correct behavior needs to be tested. No proper tests
        //    were added by the amateurs who wrote the original code (as usual).
        //    Assuming HTTP OK here for JSON responses (XML responses set the HTTP
        //    return code to match the error code as expected).
        //                                                                        [JPL]
      default:

        break;

    }

    sendResponse(request, response, composeXMLErrorDocument(errorCode, message));
  }



}

