/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2011, OpenRemote Inc.
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
package org.openremote.controller;

import org.openremote.controller.service.ServiceContext;

/**
 * DenonAVRSerial Configuration File.
 * 
 * The Denon AVR may be connected to the ORB using either direct connection using a com
 * port or a Serial over Ethernet adapter.
 * 
 * For connection over a com port configure the following properties. connectionType=RS232
 * comPort=/dev/ttyUSB0
 * 
 * For connection over a Serial over Ethernet converter configure the following properties.
 * connectionType=UDP udpIp=192.168.100.95 udpPort=4008
 * 
 * The converter has to be set up for UDP communication.
 * 
 * @author <a href="mailto:toesterdahl@ultra-marine.org>Torbjörn Österdahl</a>
 */
public class DenonAVRSerialConfiguration extends Configuration
{

  // Constants ------------------------------------------------------------------------------------

  public final static String DENONAVRSERIAL_CONNECTION_TYPE = "connectionType"; // RS232, UDP
  public final static String DENONAVRSERIAL_COM_PORT = "comPort"; // /dev/ttyUSB0, COM1
  public final static String DENONAVRSERIAL_UDP_IP = "udpIp";
  public final static String DENONAVRSERIAL_UDP_PORT = "udpPort";

  // Class Members --------------------------------------------------------------------------------

  public static DenonAVRSerialConfiguration readXML()
  {
    DenonAVRSerialConfiguration configuration = ServiceContext.getDenonAVRSerialConfiguration();

    return (DenonAVRSerialConfiguration) Configuration
        .updateWithControllerXMLConfiguration(configuration);
  }

  // Instance Fields ------------------------------------------------------------------------------

  private String connectionType;
  private String comPort;
  private String udpIp;
  private int udpPort;

  // Public Instance Methods ----------------------------------------------------------------------

  public String getConnectionType()
  {
    return preferAttrCustomValue(DENONAVRSERIAL_CONNECTION_TYPE, connectionType);
  }

  public void setConnectionType(String connectionType)
  {
    this.connectionType = connectionType;
  }

  public String getComPort()
  {
    return preferAttrCustomValue(DENONAVRSERIAL_COM_PORT, comPort);
  }

  public void setComPort(String comPort)
  {
    this.comPort = comPort;
  }

  public String getUdpIp()
  {
    return preferAttrCustomValue(DENONAVRSERIAL_UDP_IP, udpIp);
  }

  public void setUdpIp(String udpIp)
  {
    this.udpIp = udpIp;
  }

  public int getUdpPort()
  {
    return preferAttrCustomValue(DENONAVRSERIAL_UDP_PORT, udpPort);
  }

  public void setUdpPort(int udpPort)
  {
    this.udpPort = udpPort;
  }

}
