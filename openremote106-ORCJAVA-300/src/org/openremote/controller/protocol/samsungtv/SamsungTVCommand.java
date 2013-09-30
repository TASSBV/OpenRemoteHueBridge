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
package org.openremote.controller.protocol.samsungtv;

import org.apache.log4j.Logger;
import org.openremote.controller.command.ExecutableCommand;

/**
 * 
 * @author marcus
 *
 */
public class SamsungTVCommand implements ExecutableCommand {

   /** The logger. */
   private static Logger logger = Logger.getLogger(SamsungTVRemoteCommandBuilder.SAMSUNG_TV_PROTOCOL_LOG_CATEGORY);

   /** The key which is sent */
   private Key key;
   
   /** The samsungTVSession which is used to sent the command */
   private SamsungTVSession session;


   /**
    * 
    * @param session
    * @param keyCode
    */
   public SamsungTVCommand(SamsungTVSession session, Key key) {
      this.session = session;
      this.key= key;
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public void send() {
      logger.debug("Will send keyCode: ' " + key.getValue() + "' to Samsung TV");
      try {
         session.sendKey(key);
      } catch (Exception e) {
         logger.error("Could not send key.", e);
      }
   }



}
