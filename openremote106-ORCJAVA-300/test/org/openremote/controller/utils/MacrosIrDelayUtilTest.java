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
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.openremote.controller.command.DelayCommand;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.protocol.infrared.IRCommand;
import org.openremote.controller.protocol.x10.X10Command;
import org.openremote.controller.protocol.http.HttpGetCommand;
import org.openremote.controller.protocol.virtual.VirtualCommand;


/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 * @author Javen
 */
public class MacrosIrDelayUtilTest {

   private List<ExecutableCommand> haveNotIrCmd = new ArrayList<ExecutableCommand>(5) {
      {
         add(new IRCommand());
         add(new IRCommand());
         add(new DelayCommand());
         add(new IRCommand());
         add(new VirtualCommand("address", "command"));
      }
   };

   private List<ExecutableCommand> containOneOrNoDelayCmdBetweenTwoIrCmd = new ArrayList<ExecutableCommand>(5) {
      {
         add(new IRCommand());
         add(new DelayCommand("200"));
         add(new IRCommand());
         add(new IRCommand());
         add(new DelayCommand("800"));
      }
   };

   private List<ExecutableCommand> containMultiDelayCmdBetweenTwoIrCmd = new ArrayList<ExecutableCommand>(5) {
      {
         add(new IRCommand());
         add(new DelayCommand("200"));
         add(new DelayCommand("200"));
         add(new IRCommand());
         add(new DelayCommand("200"));
         add(new DelayCommand("200"));
         add(new DelayCommand("200"));
         add(new IRCommand());
         add(new DelayCommand("800"));
         add(new IRCommand());
      }
   };
   /**
    * used to test a list not only has IrCommand and DelayCommand. 
    * If it is so, the list will not be changed.
    */
   @Test
   public void testHaveNotOnlyIrcCmdAndDelayCmd() {
      MacrosIrDelayUtil.ensureDelayForIrCommand(haveNotIrCmd);
      Assert.assertTrue(haveNotIrCmd.size() == 5);
   }
   /**
    * used to test this case:  
    * There is one DelayCommand between two IrCommand or there is no DelayCommand between the nearest two IrCommand
    * result: a DelayCommand will be added between theses two IrCommand.
    */
   @Test
   public void testContainOneOrNoDelayCmdBetweenTwoIrCmd() {
      MacrosIrDelayUtil.ensureDelayForIrCommand(containOneOrNoDelayCmdBetweenTwoIrCmd);
      Assert.assertTrue(containOneOrNoDelayCmdBetweenTwoIrCmd.size() == 7);
      Assert.assertTrue(containOneOrNoDelayCmdBetweenTwoIrCmd.get(0) instanceof IRCommand);
      Assert.assertTrue(((DelayCommand) containOneOrNoDelayCmdBetweenTwoIrCmd.get(1)).getDelayMillisecond() == 200);
      Assert.assertTrue(((DelayCommand) containOneOrNoDelayCmdBetweenTwoIrCmd.get(2)).getDelayMillisecond() == 300);
      Assert.assertTrue(containOneOrNoDelayCmdBetweenTwoIrCmd.get(3) instanceof IRCommand);
      Assert.assertTrue(((DelayCommand) containOneOrNoDelayCmdBetweenTwoIrCmd.get(4)).getDelayMillisecond() == 500);
      Assert.assertTrue(containOneOrNoDelayCmdBetweenTwoIrCmd.get(5) instanceof IRCommand);
      Assert.assertTrue(((DelayCommand) containOneOrNoDelayCmdBetweenTwoIrCmd.get(6)).getDelayMillisecond() == 800);
   }
   /**
    * used to test this case: 
    * There are more than one DelayCommand between the nearest two IrCommands.
    * result: If the total delay second is small than minimum delay seconds, a new DelayCommand will be added to make sure there be
    * at least minimum seconds between two IrCommand. else the delay will not be changed. 
    */
   @Test
   public void testContainMultiDelayCmdBetweenTwoIrCmd() {
      MacrosIrDelayUtil.ensureDelayForIrCommand(containMultiDelayCmdBetweenTwoIrCmd);
      Assert.assertTrue(containMultiDelayCmdBetweenTwoIrCmd.size() == 11);
      Assert.assertTrue(containMultiDelayCmdBetweenTwoIrCmd.get(0) instanceof IRCommand);
      Assert.assertTrue(((DelayCommand) containMultiDelayCmdBetweenTwoIrCmd.get(1)).getDelayMillisecond() == 200);
      Assert.assertTrue(((DelayCommand) containMultiDelayCmdBetweenTwoIrCmd.get(2)).getDelayMillisecond() == 200);
      Assert.assertTrue(((DelayCommand) containMultiDelayCmdBetweenTwoIrCmd.get(3)).getDelayMillisecond() == 100);
      Assert.assertTrue(containMultiDelayCmdBetweenTwoIrCmd.get(4) instanceof IRCommand);
      Assert.assertTrue(((DelayCommand) containMultiDelayCmdBetweenTwoIrCmd.get(5)).getDelayMillisecond() == 200);
      Assert.assertTrue(((DelayCommand) containMultiDelayCmdBetweenTwoIrCmd.get(6)).getDelayMillisecond() == 200);
      Assert.assertTrue(((DelayCommand) containMultiDelayCmdBetweenTwoIrCmd.get(7)).getDelayMillisecond() == 200);
      Assert.assertTrue(containMultiDelayCmdBetweenTwoIrCmd.get(8) instanceof IRCommand);
      Assert.assertTrue(((DelayCommand) containMultiDelayCmdBetweenTwoIrCmd.get(9)).getDelayMillisecond() == 800);
      Assert.assertTrue(containMultiDelayCmdBetweenTwoIrCmd.get(10) instanceof IRCommand);
   }
}
