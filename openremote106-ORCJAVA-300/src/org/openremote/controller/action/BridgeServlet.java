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

import org.json.JSONException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

/**

 * This Servlet gets used by Bridge.jsp.
 * When a bridge is selected on bridge.jsp it will post to this servlet
 * which then calls {@link BridgeFinder#setChosenBridge(String, String, String)}
 * @author TASS Technology Solutions - www.tass.nl
 */


public class BridgeServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        BridgeFinder finder = new BridgeFinder();
        javax.servlet.http.HttpSession session = request.getSession();
        session.setAttribute("Bridgeusername",null);
        session.setAttribute("Bridgeip",null);

        //get parameters

        String id  =  request.getParameter("id");

        String ip  =  request.getParameter("ip");

        String mac  = request.getParameter("macaddress");

        String username = null;
       // set the bridge as chosen.
        try {
            username = finder.setChosenBridge(id,ip,mac);
            if(username == ""){
                session.setAttribute("Bridgeusername","failed");
                System.out.println(" username  null");
            }       else{
                session.setAttribute("Bridgeusername",username);
            }

            session.setAttribute("Bridgeip",ip);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }



        //response.sendRedirect("bridge.jsp");
        // return to index     r

        request.getRequestDispatcher("bridge.jsp").forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
