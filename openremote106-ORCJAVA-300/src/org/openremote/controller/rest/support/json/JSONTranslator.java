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
package org.openremote.controller.rest.support.json;

import java.net.HttpURLConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.openremote.controller.Constants;

/**
 * TODO : This is responsible for translating xml data to json data.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 * @author handy 2010-06-28
 * @author <a href="mailto:richard@openremote.org">Richard Turner 2011-10-12</a>
 */
public class JSONTranslator
{

  private static final Logger logger = Logger.getLogger(Constants.XML_PARSER_LOG_CATEGORY);
  
  public static String translateXMLToJSONP(HttpServletRequest request, HttpServletResponse response, String xml)
  {
     return translateXMLToJSON(Constants.MIME_TEXT_JAVASCRIPT, response, xml, request);
  }
  
  public static String translateXMLToJSON(HttpServletRequest request, HttpServletResponse response, String xml)
  {
    return translateXMLToJSON(Constants.MIME_APPLICATION_JSON, response, xml, request);
  }

  public static String translateXMLToJSON(String acceptHeader, HttpServletResponse response, String xml)
  {
     return translateXMLToJSON(acceptHeader, response, xml, null);
  }
  
  public static String translateXMLToJSON(String acceptHeader, HttpServletResponse response, String xml, HttpServletRequest request)
  {
    if (Constants.MIME_APPLICATION_JSON.equalsIgnoreCase(acceptHeader))
    {
      if (response != null) {
         response.setContentType(Constants.MIME_APPLICATION_JSON);
      }
      return translate(request, response, xml);
    }
    
    else if (Constants.MIME_TEXT_JAVASCRIPT.equalsIgnoreCase(acceptHeader))
    {
      if (response != null) {
         response.setContentType(Constants.MIME_TEXT_JAVASCRIPT);
      }

      return translate(request, response, xml);
    }

    else
    {
      if (response != null) {
         response.setContentType(Constants.MIME_APPLICATION_XML);
      }
      return xml;
    }
  }


  public static String translateXMLToJSON(String acceptHeader, HttpServletResponse response, int errorCode, String xml)
  {
    if (Constants.MIME_APPLICATION_JSON.equalsIgnoreCase(acceptHeader))
    {
      if (response != null) {
         response.setContentType(Constants.MIME_APPLICATION_JSON);
      }
      
      return translate(null, response, xml);
    }

    else
    {
      if (response != null) {
         response.setContentType(Constants.MIME_APPLICATION_XML);
      }

      return translate(null, response, xml);
    }
  }


  private static String translate(HttpServletRequest request, HttpServletResponse response, String xml)
  {
     if (response != null) {
        response.setStatus(HttpURLConnection.HTTP_OK);
     }
     xml = xml.replaceAll("<openremote.*>", "").replace("</openremote>", "");
     String json = "";     
     try {
        JSONObject jsonObj = XML.toJSONObject(xml);
        JSONObject errorObj = jsonObj.optJSONObject("error");
        if (errorObj == null) {
           // Format this JSON Object
           jsonObj = JSONFormatter.format(request, jsonObj);
        }
        json = jsonObj.toString();
     } catch (JSONException e) {
        logger.error("Can't convert XML to json.", e);
        json = "{\"error\":{\"message\":\"XML to JSON Parsing error\",\"code\":520}}";
     }
     return json;
  }

}
