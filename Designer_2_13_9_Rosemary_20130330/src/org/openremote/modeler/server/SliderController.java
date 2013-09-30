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
package org.openremote.modeler.server;

import java.util.ArrayList;

import org.openremote.modeler.client.rpc.SliderRPCService;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.service.DeviceCommandService;
import org.openremote.modeler.service.SensorService;
import org.openremote.modeler.service.SliderService;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.shared.dto.DTOReference;
import org.openremote.modeler.shared.dto.DeviceCommandDTO;
import org.openremote.modeler.shared.dto.SliderDTO;
import org.openremote.modeler.shared.dto.SliderDetailsDTO;
import org.openremote.modeler.shared.dto.SliderWithInfoDTO;

/**
 * The server side implementation of the RPC service <code>SliderRPCService</code>.
 */
@SuppressWarnings("serial")
public class SliderController extends BaseGWTSpringController implements SliderRPCService {

   private SliderService sliderService;
   private SensorService sensorService;
   private DeviceCommandService deviceCommandService;
   
   private UserService userService;
   
   @Override
   public void delete(long id) {
      sliderService.delete(id);
   }

   public void setSliderService(SliderService switchService) {
      this.sliderService = switchService;
   }

   public void setUserService(UserService userService) {
      this.userService = userService;
   }

   public void setSensorService(SensorService sensorService) {
    this.sensorService = sensorService;
  }

  public void setDeviceCommandService(DeviceCommandService deviceCommandService) {
    this.deviceCommandService = deviceCommandService;
  }

  @Override
  public SliderDetailsDTO loadSliderDetails(long id) {
     Slider slider = sliderService.loadById(id);
     DeviceCommand command = slider.getSetValueCmd().getDeviceCommand();
     return new SliderDetailsDTO(slider.getOid(), slider.getName(), new DTOReference(slider.getSliderSensorRef().getSensor().getOid()), new DTOReference(command.getOid()), command.getDisplayName());
   }

  @Override
  public ArrayList<SliderWithInfoDTO> loadAllSliderWithInfosDTO() {
    ArrayList<SliderWithInfoDTO> dtos = new ArrayList<SliderWithInfoDTO>();
    for (Slider slider : sliderService.loadAll()) {
      dtos.add(createSliderWithInfoDTO(slider));
    }
    return dtos;    
  }

  public static SliderWithInfoDTO createSliderWithInfoDTO(Slider slider) {
    return new SliderWithInfoDTO(slider.getOid(), slider.getDisplayName(),
            (slider.getSetValueCmd() != null)?slider.getSetValueCmd().getDisplayName():null,
            (slider.getSliderSensorRef() != null)?slider.getSliderSensorRef().getDisplayName():null,
            slider.getDevice().getDisplayName());
  }

  public static SliderDTO createSliderDTO(Slider slider) {
    SliderDTO sliderDTO = new SliderDTO(slider.getOid(), slider.getDisplayName());
    DeviceCommand dc = slider.getSetValueCmd().getDeviceCommand();
    sliderDTO.setCommand(new DeviceCommandDTO(dc.getOid(), dc.getDisplayName(), dc.getProtocol().getType()));
    return sliderDTO;
  }
  
  @Override
   public void updateSliderWithDTO(SliderDetailsDTO sliderDTO) {
     Slider slider = sliderService.loadById(sliderDTO.getOid());
     slider.setName(sliderDTO.getName());

     if (slider.getSliderSensorRef().getSensor().getOid() != sliderDTO.getSensor().getId()) {
       Sensor sensor = sensorService.loadById(sliderDTO.getSensor().getId());
       slider.getSliderSensorRef().setSensor(sensor);
     }
     
     if (slider.getSetValueCmd().getDeviceCommand().getOid() != sliderDTO.getCommand().getId()) {
       DeviceCommand dc = deviceCommandService.loadById(sliderDTO.getCommand().getId());
       slider.getSetValueCmd().setDeviceCommand(dc);
     }
     
     sliderService.update(slider);
   }

  public void saveNewSlider(SliderDetailsDTO sliderDTO, long deviceId) {
    Sensor sensor = sensorService.loadById(sliderDTO.getSensor().getId());
    DeviceCommand command = deviceCommandService.loadById(sliderDTO.getCommand().getId());

    Slider slider = new Slider(sliderDTO.getName(), command, sensor);
    slider.setAccount(userService.getAccount());
    
    sliderService.save(slider);
  }

}
