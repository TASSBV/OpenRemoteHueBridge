/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.modeler.servlet;

import static nl.captcha.Captcha.NAME;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nl.captcha.Captcha;
import nl.captcha.servlet.CaptchaServletUtil;
import nl.captcha.servlet.SimpleCaptchaServlet;

/**
 * Generate the captcha image and write into response.
 */
public class CaptchaImageServlet extends SimpleCaptchaServlet {

   private static final long serialVersionUID = -8036055236431739173L;

   @Override
   public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      HttpSession session = req.getSession();
      Captcha captcha;
      captcha = new Captcha.Builder(_width, _height).addText().gimp().addBorder().addNoise().addBackground().build();

      session.setAttribute(NAME, captcha);
      CaptchaServletUtil.writeImage(resp, captcha.getImage());
   }

}
