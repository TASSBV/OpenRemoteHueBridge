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
package org.openremote.controller;

import org.openremote.controller.service.ServiceContext;

/**
 * TODO : Lutron Homeworks Configuration File.
 *
 * @author Eric Bariaux
 * @author <a href="mailto:juha@openremote.org>Juha Lindfors</a>
 */
public class LutronHomeWorksConfig extends Configuration
{

  // Constants ------------------------------------------------------------------------------------

	public final static String LUTRON_HOMEWORKS_USERNAME = "lutron_homeworks.username";
	public final static String LUTRON_HOMEWORKS_PASSWORD = "lutron_homeworks.password";
	public final static String LUTRON_HOMEWORKS_ADDRESS = "lutron_homeworks.address";
	public final static String LUTRON_HOMEWORKS_PORT = "lutron_homeworks.port";


  // Class Members --------------------------------------------------------------------------------

  public static LutronHomeWorksConfig readXML()
  {
    LutronHomeWorksConfig config = ServiceContext.getLutronHomeWorksConfiguration();

    return (LutronHomeWorksConfig)Configuration.updateWithControllerXMLConfiguration(config);
  }


  // Instance Fields ------------------------------------------------------------------------------

	private String userName;
	private String password;
	private String address;
	private int port;


  // Public Instance Methods ----------------------------------------------------------------------
  
	public String getPassword()
  {
		return preferAttrCustomValue(LUTRON_HOMEWORKS_PASSWORD, password);
	}

	public void setPassword(String password)
  {
		this.password = password;
	}

	public String getAddress()
  {
		return preferAttrCustomValue(LUTRON_HOMEWORKS_ADDRESS, address);
	}

	public void setAddress(String address)
  {
		this.address = address;
	}

	public int getPort()
  {
		return preferAttrCustomValue(LUTRON_HOMEWORKS_PORT, port);
	}

	public void setPort(int port)
  {
		this.port = port;
	}

	public String getUserName()
  {
		return preferAttrCustomValue(LUTRON_HOMEWORKS_USERNAME, userName);
	}

	public void setUserName(String userName)
  {
		this.userName = userName;
	}

}
