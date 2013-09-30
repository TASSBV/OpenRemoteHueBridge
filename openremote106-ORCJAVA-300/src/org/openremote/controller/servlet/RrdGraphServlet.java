/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2012, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.openremote.controller.servlet;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openremote.controller.spring.SpringContext;
import org.openremote.controller.statuscache.rrd4j.Rrd4jDataLogger;
import org.openremote.controller.utils.Strings;
import org.rrd4j.graph.RrdGraph;
import org.rrd4j.graph.RrdGraphDef;
import org.rrd4j.graph.RrdGraphDefTemplate;
import org.springframework.util.FileCopyUtils;

/**
 * 
 * http://localhost:8080/controller/graph?name=graph1&start=20120209-22-00&end=20120210-01-00&width=1024&height=768
 * @author marcus
 *
 */
public class RrdGraphServlet extends HttpServlet {

   /**
    * 
    */
   private static final long serialVersionUID = 458348209402500938L;
   private SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HH-mm");

   // HttpServlet Implementation -------------------------------------------------------------------

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

      String graphName = request.getParameter("name");
      String start = request.getParameter("start");
      String end = request.getParameter("end");
      String width = request.getParameter("width");
      String height = request.getParameter("height");

      File a = File.createTempFile("OR_RrdGraph_", null);
      a.deleteOnExit();

      SpringContext sc = SpringContext.getInstance();

      Rrd4jDataLogger rrd4j = (Rrd4jDataLogger) sc.getBean("rrd4jLogger");
      String graphDef = rrd4j.getGraphDef(graphName);
      RrdGraphDefTemplate temp = new RrdGraphDefTemplate(graphDef);

      try {
         RrdGraphDef gdef = temp.getRrdGraphDef();
         gdef.setFilename(a.getAbsolutePath());
         Date startDate = null;
         Date endDate = null;
         Date now = new Date();
         if (start != null) {
            if (start.startsWith("+") || start.startsWith(" ")) {
               startDate = new Date(now.getTime()+Strings.convertPollingIntervalString(start.substring(1)));
            } else if (start.startsWith("-")) {
               startDate = new Date(now.getTime()-Strings.convertPollingIntervalString(start.substring(1)));
            } else {
               startDate = df.parse(start);
            }
            gdef.setStartTime(startDate.getTime()/1000);
         }
         if (end != null) {
            if (end.startsWith("+") || end.startsWith(" ")) {
               endDate = new Date(now.getTime()+Strings.convertPollingIntervalString(end.substring(1)));
            } else if (end.startsWith("-")) {
               endDate = new Date(now.getTime()-Strings.convertPollingIntervalString(end.substring(1)));
            } else {
               endDate = df.parse(end);
            }
            if ((startDate != null) && (endDate.compareTo(startDate) <= 0)) {
               response.getWriter().print("End-Date has to be after Start-Date");
               return;
            }
            gdef.setEndTime(endDate.getTime()/1000);
         }
         gdef.setWidth((width==null)?800:(Integer.parseInt(width)));
         gdef.setHeight((height==null)?400:(Integer.parseInt(height)));
         gdef.setImageFormat("PNG");
         RrdGraph g = new RrdGraph(gdef);
         BufferedImage bi = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
         g.render(bi.getGraphics());

         String relativePath = request.getPathInfo();
         InputStream is = new FileInputStream(a);
         if (is != null) {
            FileCopyUtils.copy(is, response.getOutputStream());
         } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, relativePath);
         }
      } catch (Exception e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
         throw new ServletException(e);
      }
   }

}
