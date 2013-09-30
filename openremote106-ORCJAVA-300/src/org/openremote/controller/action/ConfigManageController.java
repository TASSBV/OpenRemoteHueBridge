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
package org.openremote.controller.action;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openremote.controller.Constants;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.service.ServiceContext;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * TODO
 * 
 *   See relevant tasks in:
 *
 *    - ORCJAVA-173 (http://jira.openremote.org/browse/ORCJAVA-173)
 *    - ORCJAVA-171 (http://jira.openremote.org/browse/ORCJAVA-171)
 * 
 */
public class ConfigManageController extends MultiActionController
{


  // Constants ------------------------------------------------------------------------------------

  private final static String ZIP_FILE_INPUT_PARAMETER_NAME = "zip_file";

  private final static String MIME_TEXT_PLAIN = "text/plain";

  // Class Members --------------------------------------------------------------------------------


  private final static Logger log = Logger.getLogger(Constants.DEPLOYER_LOG_CATEGORY);



  // Public Instance Methods ----------------------------------------------------------------------

  public ModelAndView uploadZip(HttpServletRequest request, HttpServletResponse response) throws
      IOException, ServletRequestBindingException
  {
    response.setContentType(MIME_TEXT_PLAIN);
    PrintWriter writer = response.getWriter();

    InputStream zipInput = null;

    try
    {
      MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
      zipInput = new BufferedInputStream(
          multipartRequest.getFile(ZIP_FILE_INPUT_PARAMETER_NAME).getInputStream());

      // TODO :
      //  -- decouple from direct Java reference (and therefore from controller runtime),
      //     and go over HTTP/REST instead. See ORCJAVA-173

      ServiceContext.getDeployer().deployFromZip(zipInput);

      response.setStatus(HttpURLConnection.HTTP_OK);
      writer.println("Upload Complete.");
    }

    catch (Throwable t)
    {
      log.error("Manual configuration upload failed : {0}", t, t.getMessage());

      response.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
      writer.print(t.getMessage());
    }

    finally
    {
      if (zipInput != null)
      {
        try
        {
          zipInput.close();
        }

        catch (IOException e)
        {
          log.debug("Could not close the incoming zip upload stream : {0}", e, e.getMessage());
        }
      }

      writer.close();
      response.flushBuffer();
    }
    
    return null;
  }


  public ModelAndView syncOnline(HttpServletRequest request, HttpServletResponse response)
     throws IOException, ServletRequestBindingException
  {
    String username = request.getParameter("username");
    String password = request.getParameter("password");

    response.setContentType(MIME_TEXT_PLAIN);
    PrintWriter writer = response.getWriter();

    try
    {
      // TODO :
      //  -- decouple from direct Java reference (and therefore from controller runtime),
      //     and go over HTTP/REST instead. See ORCJAVA-173

      ServiceContext.getDeployer().deployFromOnline(username, password);

      response.setStatus(HttpURLConnection.HTTP_OK);
    }

    catch (Throwable t)
    {
      log.error("Synchronizing controller with online account failed : {0}", t, t.getMessage());

      response.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
      writer.print(t.getMessage());
    }

    finally
    {
      writer.close();
      response.flushBuffer();
    }

    return null;
  }



  public ModelAndView refreshController(HttpServletRequest request, HttpServletResponse response)
     throws IOException, ServletRequestBindingException
  {
    response.setContentType(MIME_TEXT_PLAIN);
    PrintWriter writer = response.getWriter();

    try
    {
      // TODO :
      //  -- decouple from direct Java reference (and therefore from controller runtime),
      //     and go over HTTP/REST instead. See ORCJAVA-173

      ServiceContext.getDeployer().softRestart();

      response.setStatus(HttpURLConnection.HTTP_OK);
    }

    catch (Throwable t)
    {
      log.error("Redeploy failed : {0}", t, t.getMessage());

      response.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
      writer.print(t.getMessage());
    }

    finally
    {
      writer.close();
      response.flushBuffer();
    }

    return null;

   }

  public ModelAndView getControllerLinkedStatus(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletRequestBindingException
  {
    response.setContentType(MIME_TEXT_PLAIN);
    PrintWriter writer = response.getWriter();
    try {
      String accountID = ServiceContext.getDeployer().getLinkedAccountId();
      response.setStatus(HttpURLConnection.HTTP_OK);
      writer.print(accountID); 
    } catch (Throwable t) {
      log.error("Error when checking controller link status", t);
      response.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
      writer.print(t.getMessage());
    } finally {
      writer.close();
      response.flushBuffer();
    }
    return null;
  }
}
