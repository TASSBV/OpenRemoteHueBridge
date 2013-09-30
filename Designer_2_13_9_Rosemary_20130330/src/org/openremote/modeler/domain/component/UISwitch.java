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
import java.util.Collection;
import java.util.List;

import javax.persistence.Transient;

import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.domain.UICommand;
import org.openremote.modeler.shared.dto.SensorWithInfoDTO;
import org.openremote.modeler.shared.dto.SwitchWithInfoDTO;

import flexjson.JSON;

/**
 * UISwitch defines a switch command, on image and off image.
 * It can sends on/off command, changes self state.
 */
public class UISwitch extends UIControl implements SensorOwner ,ImageSourceOwner {
   
   private static final long serialVersionUID = 8617346306578381074L;
   
   /** The UISwitch's on image. */
   private ImageSource onImage;
   
   /** The UISwitch's off image. */
   private ImageSource offImage;
   
   /** The switch command include sensor, on command and off command. */
   private Switch switchCommand;
   
   private SwitchWithInfoDTO switchDTO;

   public UISwitch() {
      super();
   }

   public UISwitch(UISwitch swh) {
      this.setOid(swh.getOid());
      this.onImage = swh.onImage;
      this.offImage = swh.offImage;
      this.switchCommand = swh.switchCommand;
   }

   public UISwitch(long id) {
      super(id);
   }

   public ImageSource getOnImage() {
      return onImage;
   }

   public ImageSource getOffImage() {
      return offImage;
   }

   public void setOnImage(ImageSource onImage) {
      this.onImage = onImage;
   }

   public void setOffImage(ImageSource offImage) {
      this.offImage = offImage;
   }

   public Switch getSwitchCommand() {
      return switchCommand;
   }

   public void setSwitchCommand(Switch switchCommand) {
      this.switchCommand = switchCommand;
   }
   
   public SwitchWithInfoDTO getSwitchDTO() {
    return switchDTO;
  }

  public void setSwitchDTO(SwitchWithInfoDTO switchDTO) {
    this.switchDTO = switchDTO;
  }

  @Override
   public String getName() {
      return "Switch";
   }

   /**
    * If the switchCommand has been set, return the on and off commands.
    * 
    * @see org.openremote.modeler.domain.component.UIControl#getCommands()
    */
   @JSON(include=false)
   @Override
   public List<UICommand> getCommands() {
      List<UICommand> commands = new ArrayList<UICommand>();
      if (switchCommand != null) {
         if (switchCommand.getSwitchCommandOnRef() != null) {
            commands.add(switchCommand.getSwitchCommandOnRef());
         }
         if (switchCommand.getSwitchCommandOffRef() != null) {
            commands.add(switchCommand.getSwitchCommandOffRef());
         }
      }
      return commands;
   }

   @Transient
   @Override
   @JSON(include=false)
   public String getPanelXml() {
      StringBuffer xmlContent = new StringBuffer();
      xmlContent.append("        <switch id=\"" + getOid() + "\">\n");
      if (getSensor() != null) {
         xmlContent.append("<link type=\"sensor\" ref=\"" + getSensor().getOid() + "\">");
         if (onImage != null && onImage.getSrc() != null) {
            xmlContent.append("          <state name=\"on\" value=\"" + onImage.getImageFileName() + "\"/>\n");
         }
         if (offImage != null && offImage.getSrc() != null) {
            xmlContent.append("          <state name=\"off\" value=\"" + offImage.getImageFileName() + "\"/>\n");
         }
         xmlContent.append("</link>");
      }
      xmlContent.append("        </switch>\n");
      return xmlContent.toString();
   }

   /**
    * Gets the sensor from switch command.
    * 
    */
   @Override
   @JSON(include=false)
   public Sensor getSensor() {
      if (switchCommand != null && switchCommand.getSwitchSensorRef() != null) {
         return switchCommand.getSwitchSensorRef().getSensor();
      }
      return null;
   }

   @Override
   public void setSensor(Sensor sensor) {
      if (switchCommand != null && switchCommand.getSwitchSensorRef() != null) {
         switchCommand.getSwitchSensorRef().setSensor(sensor);
      }
   }
   
   @Override
   public void setSensorDTO(SensorWithInfoDTO sensorDTO) {
     // TODO EBR : Just to comply to interface, but this class should not implement this interface, never has direct access to sensor, only through Switch
   }
   
   @Override
   @JSON(include=false)
   public SensorWithInfoDTO getSensorDTO() {
     return null;
     // TODO EBR : As above
   }

   public boolean canUseImage(){
      return onImage != null && offImage != null;
   }

   @Override
   @JSON(include = false)
   public Collection<ImageSource> getImageSources() {
      Collection<ImageSource> imageSources = new ArrayList<ImageSource>(2);
      if (this.onImage != null && !this.onImage.isEmpty()) {
         imageSources.add(onImage);
      }
      
      if (this.offImage != null && ! this.offImage.isEmpty()) {
         imageSources.add(offImage);
      }
      return imageSources;
   }
}
