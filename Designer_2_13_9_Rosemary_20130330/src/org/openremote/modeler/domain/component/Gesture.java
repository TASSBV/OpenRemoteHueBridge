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
package org.openremote.modeler.domain.component;

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.domain.UICommand;
import org.openremote.modeler.shared.dto.UICommandDTO;

import flexjson.JSON;

/**
 * Defined a gesture on a screen.
 * It supports to navigate to and to send command.
 */
public class Gesture extends UIControl {

   private static final long serialVersionUID = -6553735205979965418L;
   
   private GestureType type;
   private Navigate navigate = new Navigate();
   private UICommand uiCommand;
   private UICommandDTO uiCommandDTO;

   public Gesture() {
   }
   public Gesture(GestureType type) {
      this.type = type;
   }
   public GestureType getType() {
      return type;
   }

   public Navigate getNavigate() {
      return navigate;
   }

   public UICommand getUiCommand() {
      return uiCommand;
   }
   
  public UICommandDTO getUiCommandDTO() {
    return uiCommandDTO;
  }
  
  public void setUiCommandDTO(UICommandDTO uiCommandDTO) {
    this.uiCommandDTO = uiCommandDTO;
  }
  
  public void setType(GestureType type) {
      this.type = type;
   }

   public void setNavigate(Navigate navigate) {
      this.navigate = navigate;
   }

   public void setUiCommand(UICommand uiCommand) {
      this.uiCommand = uiCommand;
   }
   
   @JSON(include=false)
   @Override
   public String getPanelXml() {
      StringBuilder XMLContent = new StringBuilder();
      XMLContent.append("<gesture id=\""+getOid()+"\" ");
      if (uiCommand != null) {
         XMLContent.append("hasControlCommand=\"true\" ");
      }
      XMLContent.append("type=\""+type.toString()+"\">");
      if (navigate != null && navigate.isSet()) {
         XMLContent.append("<navigate ");
          
         if(navigate.getToGroup()!=-1){
            XMLContent.append("toGroup=\""+navigate.getToGroup()+"\" ");
            if(navigate.getToScreen()!=-1){
               XMLContent.append("toScreen=\""+navigate.getToScreen()+"\" ");
            }
         }else{
            XMLContent.append("to=\""+navigate.getToLogical().toString()+"\"");
         }
         XMLContent.append("/>");
      }
      XMLContent.append("</gesture>");
      return XMLContent.toString();
   }
   @SuppressWarnings("serial")
   @JSON(include=false)
   @Override
   public List<UICommand> getCommands() {
      if (uiCommand != null) {
         return new ArrayList<UICommand>() {
            {
               add(uiCommand);
            }
         };
      }
      return new ArrayList<UICommand>();
   }
   
   /**
    * The Enum GestureType defines four types of gesture.
    */
   public static enum GestureType {
      swipe_bottom_to_top, swipe_top_to_bottom, swipe_left_to_right, swipe_right_to_left;
      
      @Override
      public String toString() {
         return super.toString().replaceAll("_", "-");
      }
      
   }
}
