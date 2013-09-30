/* OpenRemote, the Home of the Digital Home.
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
package org.openremote.controller.utils;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.apache.log4j.Logger;
import org.openremote.controller.ControllerConfiguration;

import sun.java2d.loops.MaskBlit;

/**
 * This class is used to provide utility method about network. 
 * @author Javen
 *
 */
public class NetworkUtil {
   public static final Logger logger = Logger.getLogger(NetworkUtil.class);
   private static ControllerConfiguration configuration = null;   
   private NetworkUtil(){}
   
   /**
    * This method is used to get the local host ip 
    * if it is failed to get the local ip , "localhost" will be returned.
    * @return localhost ip 
    */
   public static String getLocalhostIP() {
      String ip = "localhost";
      if (System.getProperty("os.name").toUpperCase().contains("WINDOWS")) {
         ip = getLocalHostFromWindows();
      } else {
         ip = getLocalHostFromLinux();
      }
      return ip;
   }
   
   private static String getLocalHostFromWindows(){
      String ip = getConfiguration().getWebappIp();
      if ((ip != null) && (!ip.isEmpty())) {
          return ip;
      }
      InetAddress addr = null;
      try {
         addr = InetAddress.getLocalHost();
      } catch (UnknownHostException e) {
         logger.error("Can't get Network Interfaces", e);
      }
      byte[] ipAddr = addr.getAddress();
      StringBuffer ipAddrStr = new StringBuffer();
      for (int i = 0; i < ipAddr.length; i++) {
         if (i > 0) {
            ipAddrStr.append(".");
         }
         ipAddrStr.append(ipAddr[i] & 0xFF);
      }
      return ipAddrStr.toString();
   }
   
   private synchronized static ControllerConfiguration getConfiguration() {
      if (configuration == null) {
         configuration = ControllerConfiguration.readXML();
      }
      return configuration;
   }

   private static String getLocalHostFromLinux(){
      String ip = getConfiguration().getWebappIp();
      if ((ip != null) && (!ip.isEmpty())) {
          return ip;
      }
      try {
         Enumeration<?> interfaces = (Enumeration<?>) NetworkInterface.getNetworkInterfaces();
         while (interfaces.hasMoreElements()) {
            NetworkInterface ni = (NetworkInterface) interfaces.nextElement();
            if (ni.isUp() && ni.supportsMulticast() && !ni.isLoopback()) {
               Enumeration<?> addresses = ni.getInetAddresses();
               while (addresses.hasMoreElements()) {
                  InetAddress ia = (InetAddress) addresses.nextElement();
                  if (ia instanceof Inet6Address) {
                     continue;
                  }
                  ip = ia.getHostAddress();
               }
               if(!ip.isEmpty()){
                  break;
               }
            }
         }
      } catch (SocketException e) {
         logger.error("Can't get Network Interfaces", e);
      }
      return ip; 
   }
   
   public static String getMACAddresses() throws Exception {
      StringBuffer macs = new StringBuffer();
      Enumeration<NetworkInterface> enum1 = NetworkInterface.getNetworkInterfaces();
      while (enum1.hasMoreElements()) {
         NetworkInterface networkInterface = (NetworkInterface) enum1.nextElement();
         if (!networkInterface.isLoopback()) {
            boolean onlyLinkLocal = true;
            for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
               if (!interfaceAddress.getAddress().isLinkLocalAddress()) {
                  onlyLinkLocal = false;
               }
            }
            if (onlyLinkLocal) continue;
            byte[] mac = networkInterface.getHardwareAddress();
            if (mac != null) {
               macs.append(getMACString(networkInterface.getHardwareAddress()));
               macs.append(",");
            }
         }
      }
      if (macs.length()==0) {
         return "no-mac-address-found";
      }
      macs.deleteCharAt(macs.length()-1);
      return macs.toString();
   }

   public static String getMACString(byte[] mac) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < mac.length; i++) {
         sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));     
      }
      return sb.toString();
   }
   
}
