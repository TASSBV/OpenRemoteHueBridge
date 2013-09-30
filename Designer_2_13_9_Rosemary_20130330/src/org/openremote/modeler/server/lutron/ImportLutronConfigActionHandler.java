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
package org.openremote.modeler.server.lutron;

import java.util.ArrayList;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.Protocol;
import org.openremote.modeler.domain.RangeSensor;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorCommandRef;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.service.DeviceCommandService;
import org.openremote.modeler.service.DeviceService;
import org.openremote.modeler.service.SensorService;
import org.openremote.modeler.service.SliderService;
import org.openremote.modeler.shared.lutron.ImportConfig;
import org.openremote.modeler.shared.lutron.ImportLutronConfigAction;
import org.openremote.modeler.shared.lutron.ImportLutronConfigResult;
import org.openremote.modeler.shared.lutron.OutputImportConfig;
import org.openremote.modeler.shared.lutron.OutputType;
import org.springframework.transaction.annotation.Transactional;

public class ImportLutronConfigActionHandler implements ActionHandler<ImportLutronConfigAction, ImportLutronConfigResult> {

  protected DeviceService deviceService;
  protected DeviceCommandService deviceCommandService;
  protected SensorService sensorService;
  protected SliderService sliderService;

  public void setDeviceService(DeviceService deviceService) {
    this.deviceService = deviceService;
  }

  public void setDeviceCommandService(DeviceCommandService deviceCommandService) {
    this.deviceCommandService = deviceCommandService;
  }

  public void setSensorService(SensorService sensorService) {
    this.sensorService = sensorService;
  }
  
  public void setSliderService(SliderService sliderService) {
    this.sliderService = sliderService;
  }

  @Override
  public ImportLutronConfigResult execute(ImportLutronConfigAction action, ExecutionContext context) throws DispatchException {
    ImportLutronConfigResult result = new ImportLutronConfigResult();
    Device device = deviceService.loadById(action.getDevice().getOid());
    result.setDeviceCommands(createDeviceElements(device, action.getConfig()));
    
    
    // TODO: handle potential errors and return error message
    // TODO: return created elements / updated device ? See also with RequestProxy mechanism
    

    return result;
    
  }

  @Override
  public Class<ImportLutronConfigAction> getActionType() {
    return ImportLutronConfigAction.class;
  }

  @Override
  public void rollback(ImportLutronConfigAction action, ImportLutronConfigResult result, ExecutionContext context) throws DispatchException {
    // TODO Implementation only required for compound action
  }

  @Transactional
  private ArrayList<DeviceCommand> createDeviceElements(Device device, ImportConfig config) {
    final String NoScene = null;
    final String NoLevel = null;
    final String NoKey = null;

    DeviceCommand deviceCommand;
    ArrayList<DeviceCommand> deviceCommands = new ArrayList<DeviceCommand>();
    ArrayList<Sensor> sensors = new ArrayList<Sensor>();
    ArrayList<Slider> sliders = new ArrayList<Slider>();
    
    for (OutputImportConfig outputConfig : config.getOutputs()) {
      if (OutputType.Dimmer == outputConfig.getType() || OutputType.QEDShade == outputConfig.getType()) {
        deviceCommands.add(addDeviceCommand(device, outputConfig.getOutputName() + "_Raise", outputConfig.getAddress(), "RAISE", NoScene, NoLevel, NoKey));
        deviceCommands.add(addDeviceCommand(device, outputConfig.getOutputName() + "_Lower", outputConfig.getAddress(), "LOWER", NoScene, NoLevel, NoKey));
        deviceCommands.add(addDeviceCommand(device, outputConfig.getOutputName() + "_Stop", outputConfig.getAddress(), "STOP", NoScene, NoLevel, NoKey));
        deviceCommand = addDeviceCommand(device, outputConfig.getOutputName() + "_LevelRead", outputConfig.getAddress(), "STATUS_DIMMER", NoScene, NoLevel, NoKey);
        deviceCommands.add(deviceCommand);        
        Sensor sensor = createDeviceSensor(device, SensorType.LEVEL, deviceCommand, outputConfig.getOutputName() + "_Level");
        sensors.add(sensor);
        deviceCommand = addDeviceCommand(device, outputConfig.getOutputName() + "_Fade", outputConfig.getAddress(), "FADE", NoScene, NoLevel, NoKey);
        deviceCommands.add(deviceCommand);
        sliders.add(createDeviceSlider(device, deviceCommand, sensor, outputConfig.getOutputName() + "_Slider"));        
      } else if (OutputType.GrafikEyeMainUnit == outputConfig.getType()) {
        deviceCommands.add(addDeviceCommand(device, outputConfig.getOutputName() + "_SceneOff", outputConfig.getAddress(), "SCENE", "0", NoLevel, NoKey));
        deviceCommand = addDeviceCommand(device, outputConfig.getOutputName() + "_OffRead", outputConfig.getAddress(), "STATUS_SCENE", "0", NoLevel, NoKey);
        deviceCommands.add(deviceCommand);
        sensors.add(createDeviceSensor(device, SensorType.SWITCH, deviceCommand, outputConfig.getOutputName() + "_OffSelected"));
        for (int sceneNumber = 1; sceneNumber <= 8; sceneNumber++) {
          deviceCommands.add(addDeviceCommand(device, outputConfig.getOutputName() + "_Scene" + Integer.toString(sceneNumber), outputConfig.getAddress(), "SCENE", Integer.toString(sceneNumber), NoLevel, NoKey));
          deviceCommand = addDeviceCommand(device, outputConfig.getOutputName() + "_Scene" + Integer.toString(sceneNumber) + "Read", outputConfig.getAddress(), "STATUS_SCENE", Integer.toString(sceneNumber), NoLevel, NoKey);
          deviceCommands.add(deviceCommand);
          sensors.add(createDeviceSensor(device, SensorType.SWITCH, deviceCommand, outputConfig.getOutputName() + "_Scene" + Integer.toString(sceneNumber) + "Selected"));
        }
        deviceCommand = addDeviceCommand(device, outputConfig.getOutputName() + "_SceneRead", outputConfig.getAddress(), "STATUS_SCENE", NoScene, NoLevel, NoKey);
        deviceCommands.add(deviceCommand);
        Sensor sensor = createDeviceSensor(device, SensorType.RANGE, deviceCommand, outputConfig.getOutputName() + "_SelectedScene");
        ((RangeSensor) sensor).setMin(0);
        ((RangeSensor) sensor).setMax(8);
        sensors.add(sensor);
      } else if (OutputType.Fan == outputConfig.getType()) {
        deviceCommands.add(addDeviceCommand(device, outputConfig.getOutputName() + "_Off", outputConfig.getAddress(), "FADE", NoScene, "0", NoKey));
        deviceCommands.add(addDeviceCommand(device, outputConfig.getOutputName() + "_Low", outputConfig.getAddress(), "FADE", NoScene, "25", NoKey));
        deviceCommands.add(addDeviceCommand(device, outputConfig.getOutputName() + "_Medium", outputConfig.getAddress(), "FADE", NoScene, "50", NoKey));
        deviceCommands.add(addDeviceCommand(device, outputConfig.getOutputName() + "_MediumHigh", outputConfig.getAddress(), "FADE", NoScene, "75", NoKey));
        deviceCommands.add(addDeviceCommand(device, outputConfig.getOutputName() + "_Full", outputConfig.getAddress(), "FADE", NoScene, "100", NoKey));
      }
      // TODO: handle other output types
    }

    /*
    for (ControlStation controlStation : room.getInputs()) {
      for (org.openremote.modeler.server.lutron.importmodel.Device roomDevice : controlStation.getDevices()) {
        for (org.openremote.modeler.server.lutron.importmodel.Button button : roomDevice.getButtons()) {
          if (org.openremote.modeler.server.lutron.importmodel.Device.DeviceType.Keypad.equals(roomDevice.getType())) {
            /*
            addDeviceCommand(device, controlStation.getName() + "_" + button.getName() + "_Press", roomDevice.getAddress(), "PRESS", NoScene, NoLevel, Integer.toString(button.getNumber()));
            addDeviceCommand(device, controlStation.getName() + "_" + button.getName() + "_Release", roomDevice.getAddress(), "RELEASE", NoScene, NoLevel, Integer.toString(button.getNumber()));
            addDeviceCommand(device, controlStation.getName() + "_" + button.getName() + "_Hold", roomDevice.getAddress(), "HOLD", NoScene, NoLevel, Integer.toString(button.getNumber()));
            *//*
            // TODO: if defined as web keypad, generate UI
          }
        }
      }
      // TODO: handle other input types
    }*/
    
    deviceCommandService.saveAll(deviceCommands);
    sensorService.saveAllSensors(sensors, device.getAccount());
    sliderService.saveAllSliders(sliders, device.getAccount());
    
    
    // TODO: test just re-saving the device
    
    // TODO : Nothing returned for now, DisplatchHandler does not handle Hibernate objects over the wire
    return null;
  }

  private DeviceCommand addDeviceCommand(Device aDevice, String name, String address, String command, String scene, String level, String key) {
    DeviceCommand dc = new DeviceCommand();
    
    Protocol protocol = dc.createProtocol("Lutron HomeWorks");
    protocol.addProtocolAttribute("address", address);
    protocol.addProtocolAttribute("command", command);
    if (scene != null) {
      protocol.addProtocolAttribute("scene", scene);
    }
    if (level != null) {
      protocol.addProtocolAttribute("level", level);
    }
    if (key != null) {
      protocol.addProtocolAttribute("key", key);
    }
    dc.setName(name);
    dc.setDevice(aDevice);
    aDevice.getDeviceCommands().add(dc);
    return dc;
  }

  private Sensor createDeviceSensor(Device aDevice, SensorType sensorType, DeviceCommand readCommand, String name) {
    Sensor sensor = null;
    if (SensorType.RANGE == sensorType) {
      sensor = new RangeSensor();
    } else {
      sensor = new Sensor();
    }
    sensor.setName(name);
    sensor.setType(sensorType);
    SensorCommandRef sensorCommandRef = new SensorCommandRef();
    sensorCommandRef.setDeviceCommand(readCommand);
    sensorCommandRef.setSensor(sensor);
    sensor.setSensorCommandRef(sensorCommandRef);
    sensor.setDevice(aDevice);
    return sensor;
  }

  private Slider createDeviceSlider(Device aDevice, DeviceCommand sliderCommand, Sensor readSensor, String name) {
    Slider slider = new Slider(name, sliderCommand, readSensor);
    slider.setDevice(aDevice);
    return slider;
  }

}