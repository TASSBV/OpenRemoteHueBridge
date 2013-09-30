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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openremote.controller.command.DelayCommand;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.protocol.infrared.IRCommand;
import org.openremote.controller.ControllerConfiguration;

/**
 * This class provide some method for operating macros. 
 * @author Javen
 *
 */
public class MacrosIrDelayUtil {
   private static Logger logger = Logger.getLogger(MacrosIrDelayUtil.class);
   /**
    * The infrared command can be send very quickly but it is executed very slowly,If the infrared commands are
    * send very frequently, some commands may not be executed. as a result, we need to make sure at least there be
    * several seconds delay before the next infrared  commands is executed.
    * 
    * @param commands
    */
   public static void ensureDelayForIrCommand(List<ExecutableCommand> commands){
      
      List<Integer> irCmdIndex = getIrCommandIndexList(commands);
      if (irCmdIndex == null) {
         return;
      }

     // TODO - Fix this: this is completely stupid use of the configuration, the XML is parsed every time [JPL]
     
      long minDelaySeconds = ControllerConfiguration.readXML().getMacroIRExecutionDelay();
      Map<Integer, DelayCommand> delays = getDelayForIrCommand(commands, irCmdIndex, minDelaySeconds);

      int addTimes = 0;
      Set<Integer> insertIndex = delays.keySet();
      for (Integer index : insertIndex) {
         commands.add(index + addTimes, delays.get(index));
         addTimes++;
         logger.info("add " + minDelaySeconds + " seconds before " + index + " ircommand");
      }
   }

   private static Map<Integer, DelayCommand> getDelayForIrCommand(List<ExecutableCommand> commands,
         List<Integer> irCmdIndex, long minDelaySeconds) {
      Map<Integer, DelayCommand> delays = new HashMap<Integer, DelayCommand>();

      for (int i = 0; i < irCmdIndex.size() - 1; i++) {
         int currIndex = irCmdIndex.get(i);
         int nextIndex = irCmdIndex.get(i + 1);
         Long delaySeconds = 0L;
         if (nextIndex - currIndex == 1) {
            DelayCommand delayCmd = new DelayCommand(String.valueOf(minDelaySeconds));
            delays.put(nextIndex, delayCmd);
         } else {
            for (int j = currIndex + 1; j < nextIndex; j++) {
               delaySeconds += ((DelayCommand) commands.get(j)).getDelayMillisecond();
            }
            if (delaySeconds < minDelaySeconds) {
               DelayCommand delayCmd = new DelayCommand(String.valueOf(minDelaySeconds - delaySeconds));
               delays.put(nextIndex, delayCmd);
            }
         }
      }
      return delays;
   }

   private static List<Integer> getIrCommandIndexList(List<ExecutableCommand> commands) {
      List<Integer> irCmdIndex = new ArrayList<Integer>(5);
      for (int i = 0; i < commands.size(); i++) {
         ExecutableCommand cmd = commands.get(i);
         if (!(cmd instanceof IRCommand) && !(cmd instanceof DelayCommand)) {
            return null;
         } else if (cmd instanceof IRCommand) {
            irCmdIndex.add(i);
         }
      }
      return irCmdIndex;
   }
   
   
}
