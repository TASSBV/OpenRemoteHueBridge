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
package org.openremote.controller.protocol.infrared;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.openremote.controller.ControllerConfiguration;
import org.openremote.controller.command.ExecutableCommand;

/**
 * The Infrared Event.
 * 
 * @author Dan 2009-4-20
 */
public class IRCommand implements ExecutableCommand {
   
   /** The logger. */
   private static Logger logger = Logger.getLogger(IRCommand.class.getName());
   
   /** The remote device name. This name MUST be the name defined in lircd.conf */
   private String name;
   
   /** The button command. Such as menu, play etc. */
   private String command;
   
   /** The configuration. */
   private ControllerConfiguration configuration = ControllerConfiguration.readXML();
   
   /**
    * {@inheritDoc}
    */
   @Override
   public void send() {
      irsend("SEND_ONCE");   
   }


   /* (non-Javadoc)
    * @see org.openremote.controller.event.Event#start()
    
   @Override
   public void start() {
      irsend("SEND_START");
   }

    (non-Javadoc)
    * @see org.openremote.controller.event.Event#stop()
    
   @Override
   public void stop() {
      irsend("SEND_STOP");
   }*/
   

   /**
    * Irsend.
    * 
    * @param sendType the send type
    */
   private void irsend(String sendType) {
      String cmd = configuration.getIrsendPath() + " " 
          + sendType + " " + getName() + " " + getCommand();
      try {
         Process pro = Runtime.getRuntime().exec(cmd);
         logger.info(cmd);
         pro.waitFor();
      } catch (InterruptedException e) {
         logger.error(cmd + " was interrupted.", e);
      } catch (IOException e) {
         logger.error(cmd + " failed.", e);
      }
   }

   /**
    * Sets the configuration.
    * 
    * @param configuration the new configuration
    */
   public void setConfiguration(ControllerConfiguration configuration) {
      this.configuration = configuration;
   }
   
   /**
    * Gets the command.
    * 
    * @return the command
    */
   public String getCommand() {
      return command;
   }

   /**
    * Sets the command.
    * 
    * @param command the new command
    */
   public void setCommand(String command) {
      this.command = command;
   }

   /**
    * Gets the name.
    * 
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * Sets the name.
    * 
    * @param name the new name
    */
   public void setName(String name) {
      this.name = name;
   }

   
}
