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
package org.openremote.controller.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openremote.controller.service.ServiceContext;
import org.openremote.controller.utils.PathUtil;
import org.openremote.controller.ControllerConfiguration;
import org.springframework.util.FileCopyUtils;

/**
 * TODO : The Servlet to get the files in resource folder.
 *
 * Relevant tasks:
 *   ORCJAVA-175  (http://jira.openremote.org/browse/ORCJAVA-175)
 *   ORCJAVA-176  (http://jira.openremote.org/browse/ORCJAVA-176)
 *   ORCJAVA-177  (http://jira.openremote.org/browse/ORCJAVA-177)
 * 
 * @author Dan 2009-6-9
 */
public class ResourceServlet extends HttpServlet
{

  // HttpServlet Implementation -------------------------------------------------------------------

  @Override protected void doGet(HttpServletRequest request, HttpServletResponse response)
                                                            throws ServletException, IOException
  {

    // TODO : this part of the API needs proper integration tests (see ORCJAVA-175)

    String relativePath = request.getPathInfo();
    InputStream is = findResource(relativePath);

    if (is != null)
    {
      // TODO : inline stream copy (see ORCJAVA-177)
      
      FileCopyUtils.copy(is, response.getOutputStream());
    }

    else
    {
      response.sendError(HttpServletResponse.SC_NOT_FOUND, relativePath);
    }
  }


  // Private Instance Methods ---------------------------------------------------------------------

  private InputStream findResource(String relativePath)
  {
    ControllerConfiguration config = ServiceContext.getControllerConfiguration();

    // TODO : review error handling, at minimum log the underlying IO errors (see ORCJAVA-176)

    File file = new File(PathUtil.removeSlashSuffix(config.getResourcePath()) + relativePath);

    if (file.exists() && file.isFile())
    {
      try
      {
         return new FileInputStream(file);
      }

      catch (FileNotFoundException e)
      {
         return null;
      }
    }

    return null;
  }

}
