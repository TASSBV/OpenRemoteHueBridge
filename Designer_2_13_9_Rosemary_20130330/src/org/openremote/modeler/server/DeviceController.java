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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openremote.modeler.client.rpc.DeviceRPCService;
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.CustomSensor;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.Protocol;
import org.openremote.modeler.domain.RangeSensor;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorCommandRef;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.domain.State;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.service.DeviceService;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.shared.dto.DeviceCommandDTO;
import org.openremote.modeler.shared.dto.DeviceCommandDetailsDTO;
import org.openremote.modeler.shared.dto.DeviceDTO;
import org.openremote.modeler.shared.dto.DeviceDetailsDTO;
import org.openremote.modeler.shared.dto.DeviceWithChildrenDTO;
import org.openremote.modeler.shared.dto.SensorDTO;
import org.openremote.modeler.shared.dto.SensorDetailsDTO;
import org.openremote.modeler.shared.dto.SliderDTO;
import org.openremote.modeler.shared.dto.SliderDetailsDTO;
import org.openremote.modeler.shared.dto.SwitchDTO;
import org.openremote.modeler.shared.dto.SwitchDetailsDTO;

/**
 * The server side implementation of the RPC service <code>DeviceRPCService</code>.
 */
public class DeviceController extends BaseGWTSpringController implements DeviceRPCService {

   /** The Constant serialVersionUID. */
   private static final long serialVersionUID = -6698924847005128888L;
   
   /** The device service. */
   private DeviceService deviceService;
   
   /** The user service. */
   private UserService userService;

    /**
     * Sets the device service .
     * 
     * @param deviceService the device service
     */
   public void setDeviceService(DeviceService deviceService) {
      this.deviceService = deviceService;
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.openremote.modeler.client.rpc.DeviceRPCService#deleteDevice
    */
   public void deleteDevice(long id) {
      deviceService.deleteDevice(id);
   }
   /**
    * Sets the user service.
    * 
    * @param userService the new user service
    */
   public void setUserService(UserService userService) {
      this.userService = userService;
   }

   public ArrayList<DeviceDTO> loadAllDTOs() {
     List<Device> devices = deviceService.loadAll(userService.getAccount());
     ArrayList<DeviceDTO> dtos = new ArrayList<DeviceDTO>();
     for (Device d : devices) {
       dtos.add(new DeviceDTO(d.getOid(), d.getDisplayName()));
     }
     return dtos;
  }

   public DeviceWithChildrenDTO loadDeviceWithChildrenDTOById(long oid) {
     Device device = deviceService.loadById(oid);
     DeviceWithChildrenDTO deviceDTO = new DeviceWithChildrenDTO(device.getOid(), device.getDisplayName());
     deviceDTO.setDeviceCommands(createDeviceCommandDTOs(device.getDeviceCommands()));
     ArrayList<SensorDTO> sensorDTOs = new ArrayList<SensorDTO>();
     for (Sensor sensor : device.getSensors()) {
       sensorDTOs.add(SensorController.createSensorDTO(sensor));
     }
     deviceDTO.setSensors(sensorDTOs);
     ArrayList<SwitchDTO> switchDTOs = new ArrayList<SwitchDTO>();
     for (Switch s : device.getSwitchs()) {
       switchDTOs.add(SwitchController.createSwitchDTO(s));
     }
     deviceDTO.setSwitches(switchDTOs);
     ArrayList<SliderDTO> sliderDTOs = new ArrayList<SliderDTO>();
     for (Slider s : device.getSliders()) {
       sliderDTOs.add(SliderController.createSliderDTO(s));
     }
     deviceDTO.setSliders(sliderDTOs);
     return deviceDTO;
   }

   public DeviceWithChildrenDTO loadDeviceWithCommandChildrenDTOById(long oid) {
     Device device = deviceService.loadById(oid);
     DeviceWithChildrenDTO deviceDTO = new DeviceWithChildrenDTO(device.getOid(), device.getDisplayName());
     deviceDTO.setDeviceCommands(createDeviceCommandDTOs(device.getDeviceCommands()));
     return deviceDTO;
   }

  protected ArrayList<DeviceCommandDTO> createDeviceCommandDTOs(List<DeviceCommand> deviceCommands) {
    ArrayList<DeviceCommandDTO> dcDTOs = new ArrayList<DeviceCommandDTO>();
     for (DeviceCommand dc : deviceCommands) {
       dcDTOs.add(new DeviceCommandDTO(dc.getOid(), dc.getDisplayName(), dc.getProtocol().getType()));
     }
    return dcDTOs;
  }
   
   public DeviceDetailsDTO loadDeviceDetailsDTO(long oid) {
     Device device = deviceService.loadById(oid);
     return new DeviceDetailsDTO(device.getOid(), device.getName(), device.getVendor(), device.getModel());
   }
   
   @Override
   public DeviceDTO saveNewDevice(DeviceDetailsDTO device) {
     Device deviceBean = new Device(device.getName(), device.getVendor(), device.getModel());
     deviceBean.setAccount(userService.getAccount());
     deviceService.saveDevice(deviceBean);
     return new DeviceDTO(deviceBean.getOid(), deviceBean.getDisplayName());
   }
   
   @Override
   public DeviceDTO saveNewDeviceWithChildren(DeviceDetailsDTO device, ArrayList<DeviceCommandDetailsDTO> commands, ArrayList<SensorDetailsDTO> sensors,
                                         ArrayList<SwitchDetailsDTO> switches, ArrayList<SliderDetailsDTO> sliders) {
     Account account = userService.getAccount();
     
     Device deviceBean = new Device(device.getName(), device.getVendor(), device.getModel());
     deviceBean.setAccount(account);

     Map<DeviceCommandDetailsDTO, DeviceCommand> commandBeans = new HashMap<DeviceCommandDetailsDTO, DeviceCommand>();
     for (DeviceCommandDetailsDTO command : commands) {
       DeviceCommand dc = new DeviceCommand();
       dc.setDevice(deviceBean);
       
       dc.setName(command.getName());
       Protocol protocol = new Protocol();
       protocol.setDeviceCommand(dc);
       dc.setProtocol(protocol);
       protocol.setType(command.getProtocolType());
       for (Map.Entry<String, String> e : command.getProtocolAttributes().entrySet()) {
         protocol.addProtocolAttribute(e.getKey(), e.getValue());
       }
       commandBeans.put(command, dc);
     }
     deviceBean.setDeviceCommands(new ArrayList<DeviceCommand>(commandBeans.values()));

     Map<SensorDetailsDTO, Sensor> sensorBeans = new HashMap<SensorDetailsDTO, Sensor>();
     for (SensorDetailsDTO sensorDTO : sensors) {
       Sensor sensor = null;
       if (sensorDTO.getType() == SensorType.RANGE) {
         sensor = new RangeSensor(sensorDTO.getMinValue(), sensorDTO.getMaxValue());
       } else if (sensorDTO.getType() == SensorType.CUSTOM) {
         CustomSensor customSensor = new CustomSensor();
         for (Map.Entry<String,String> e : sensorDTO.getStates().entrySet()) {
           customSensor.addState(new State(e.getKey(), e.getValue()));
         }
         sensor = customSensor;
       } else {
         sensor = new Sensor(sensorDTO.getType());
       }
       
       sensor.setDevice(deviceBean);
       sensor.setName(sensorDTO.getName());
       sensor.setAccount(account);
       
       DeviceCommand deviceCommand = commandBeans.get(sensorDTO.getCommand().getDto());
       SensorCommandRef commandRef = new SensorCommandRef();
       commandRef.setSensor(sensor);
       commandRef.setDeviceCommand(deviceCommand);
       sensor.setSensorCommandRef(commandRef);
       sensorBeans.put(sensorDTO, sensor);
     }
     deviceBean.setSensors(new ArrayList<Sensor>(sensorBeans.values()));
     
     List<Switch> switchBeans = new ArrayList<Switch>();
     for (SwitchDetailsDTO switchDTO : switches) {
       Sensor sensor = sensorBeans.get(switchDTO.getSensor().getDto());
       DeviceCommand onCommand = commandBeans.get(switchDTO.getOnCommand().getDto());
       DeviceCommand offCommand = commandBeans.get(switchDTO.getOffCommand().getDto());
       
       Switch sw = new Switch(onCommand, offCommand, sensor);
       sw.setName(switchDTO.getName());
       sw.setAccount(account);
       switchBeans.add(sw);
     }
     deviceBean.setSwitchs(switchBeans);
     
     List<Slider> sliderBeans = new ArrayList<Slider>();
     for (SliderDetailsDTO sliderDTO : sliders) {
       Sensor sensor = sensorBeans.get(sliderDTO.getSensor().getDto());
       DeviceCommand command = commandBeans.get(sliderDTO.getCommand().getDto());

       Slider slider = new Slider(sliderDTO.getName(), command, sensor);
       slider.setAccount(account);
       sliderBeans.add(slider);
     }
     deviceBean.setSliders(sliderBeans);
     
     deviceService.saveDevice(deviceBean);
     return new DeviceDTO(deviceBean.getOid(), deviceBean.getDisplayName());
   }

   public void updateDeviceWithDTO(DeviceDetailsDTO device) {
     Device deviceBean = deviceService.loadById(device.getOid());
     deviceBean.setName(device.getName());
     deviceBean.setVendor(device.getVendor());
     deviceBean.setModel(device.getModel());
     deviceService.updateDevice(deviceBean);
   }
}
