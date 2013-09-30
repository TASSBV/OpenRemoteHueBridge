package org.openremote.modeler.server.russound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.RangeSensor;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorCommandRef;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.service.DeviceCommandService;
import org.openremote.modeler.service.DeviceService;
import org.openremote.modeler.service.SensorService;
import org.openremote.modeler.service.SliderService;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.shared.dto.DeviceDTO;
import org.openremote.modeler.shared.russound.CreateRussoundDeviceAction;
import org.openremote.modeler.shared.russound.CreateRussoundDeviceResult;

public class CreateRussoundDeviceActionHandler implements ActionHandler<CreateRussoundDeviceAction, CreateRussoundDeviceResult> {

  protected UserService userService;
  protected DeviceService deviceService;
  protected DeviceCommandService deviceCommandService;
  protected SensorService sensorService;
  protected SliderService sliderService;

  
  public void setUserService(UserService userService) {
    this.userService = userService;
  }

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
  public CreateRussoundDeviceResult execute(CreateRussoundDeviceAction action, ExecutionContext context) throws DispatchException {
    CreateRussoundDeviceResult result = new CreateRussoundDeviceResult();
    
    ArrayList<DeviceDTO> dtos = new ArrayList<DeviceDTO>();
    for (Device dev : createRussoundDevices(action.getDeviceName(), action.getModel(), action.getControllerCount())) {
      dtos.add(new DeviceDTO(dev.getOid(), dev.getDisplayName()));
    }
    result.setDevices(dtos);
    return result;
  }

  @Override
  public Class<CreateRussoundDeviceAction> getActionType() {
    return CreateRussoundDeviceAction.class;
  }

  @Override
  public void rollback(CreateRussoundDeviceAction action, CreateRussoundDeviceResult result, ExecutionContext context) throws DispatchException {
    // TODO Implementation only required for compound action
  }

  private ArrayList<Device> createRussoundDevices(String deviceName, String model, int controllerCount) {
    ArrayList<Device> deviceList = new ArrayList<Device>();
    
    String vendor = "Russound";
    int zonesPerController = -1;
    if (model.equals("CA-S44")) {
      zonesPerController = 4;
    } else if (model.equals("MCA-C5")) {
      zonesPerController = 8;
    } else {
      zonesPerController = 6;
    }
    
    Account account = userService.getAccount();
    
    for (int controller = 1; controller <= controllerCount; controller++)
    {
      for (int zone = 1; zone <= zonesPerController; zone++)
      {
        int zoneNumber = (controller - 1) * zonesPerController + zone;
        String name = deviceName + " - Zone" + zoneNumber;
        Device device = new Device();
        device.setAccount(account);
        device.setModel(model);
        device.setName(name);
        device.setVendor(vendor);
        
        addRussoundCommands(device, zone, controller, zoneNumber, zonesPerController);
        addRussoundSensors(device);
        addRussoundSwitches(device);
        addRussoundSliders(device);
        
        deviceList.add(deviceService.saveDevice(device));
      }
    }
    return deviceList;
  }

  private void addRussoundCommands(Device device, int zone, int controller, int zoneNumber, int zonesPerController) {
    HashMap<String, String> cmdNames = new HashMap<String, String>();
    cmdNames.put("All Zones (OFF)", "ALL_OFF");
    cmdNames.put("All Zones (ON)", "ALL_ON");
    cmdNames.put("Power (ON)", "POWER_ON");
    cmdNames.put("Power (OFF)", "POWER_OFF");
    cmdNames.put("Power (STATUS)", "GET_POWER_STATUS");
    cmdNames.put("Volume (UP)", "VOL_UP");
    cmdNames.put("Volume (DOWN)", "VOL_DOWN");
    cmdNames.put("Volume (GET)", "GET_VOLUME");
    cmdNames.put("Volume (SET)", "SET_VOLUME");
    cmdNames.put("TurnOnVolume (SET)", "SET_TURNON_VOLUME");
    cmdNames.put("TurnOnVolume (GET)", "GET_TURNON_VOLUME");
    cmdNames.put("Loudness (ON)", "SET_LOUDNESS_ON");
    cmdNames.put("Loudness (OFF)", "SET_LOUDNESS_OFF");
    cmdNames.put("Loudness (STATUS)", "GET_LOUDNESS_MODE");
    cmdNames.put("Bass Level (SET)", "SET_BASS_LEVEL");
    cmdNames.put("Bass Level (GET)", "GET_BASS_LEVEL");
    cmdNames.put("Treble Level (SET)", "SET_TREBLE_LEVEL");
    cmdNames.put("Treble Level (GET)", "GET_TREBLE_LEVEL");
    cmdNames.put("Balance Level (SET)", "SET_BALANCE_LEVEL");
    cmdNames.put("Balance Level (GET)", "GET_BALANCE_LEVEL");
    cmdNames.put("Source (SET)", "SET_SOURCE");
    cmdNames.put("Source (GET)", "GET_SOURCE");
    
    for (Entry<String, String> cmd : cmdNames.entrySet()) {
      DeviceCommand deviceCommand = new DeviceCommand();
      deviceCommand.setName("Zone" + zoneNumber + " " + cmd.getKey());
      deviceCommand.setDevice(device);
      HashMap<String, String> attrMap = new HashMap<String, String>();
      attrMap.put("controller", Integer.toString(controller));
      attrMap.put("zone", Integer.toString(zone));
      attrMap.put("command", cmd.getValue());
      deviceCommand.createProtocolWithAttributes("Russound RNET Protocol", attrMap);
      device.getDeviceCommands().add(deviceCommand);
    }
  }

  private void addRussoundSensors(Device device) {
    List<DeviceCommand> commands = device.getDeviceCommands();
    for (DeviceCommand deviceCommand : commands) {
      if (deviceCommand.getName().indexOf("STATUS") != -1) {
        Sensor sensor = createSensor(SensorType.SWITCH, deviceCommand, device, 0, 0);
        device.getSensors().add(sensor);
      } else if (deviceCommand.getName().indexOf("GET") != -1) {
        if (deviceCommand.getName().indexOf("Volume") != -1) {
          Sensor sensor = createSensor(SensorType.RANGE, deviceCommand, device, 0, 100);
          device.getSensors().add(sensor);
        } else {
          Sensor sensor = createSensor(SensorType.RANGE, deviceCommand, device, -10, 10);
          device.getSensors().add(sensor);
        }
      }
    }
  }

  private Sensor createSensor(SensorType type, DeviceCommand command, Device device, int min, int max) {
    Sensor sensor;
    if (type == SensorType.RANGE) {
        sensor = new RangeSensor();
       ((RangeSensor) sensor).setMin(min);
       ((RangeSensor) sensor).setMax(max);
    } else {
        sensor = new Sensor();
    }
    sensor.setType(type);
    sensor.setName(command.getName().substring(0,command.getName().indexOf('(')-1));
  
    SensorCommandRef sensorCommandRef = new SensorCommandRef();
    sensorCommandRef.setDeviceCommand(command);
    sensorCommandRef.setSensor(sensor);
    sensor.setSensorCommandRef(sensorCommandRef);
    sensor.setDevice(device);
    sensor.setAccount(device.getAccount());
    return sensor;
  }
  
  private void addRussoundSliders(Device device) {
    HashMap<String, DeviceCommand> sliderCommands = new HashMap<String, DeviceCommand>();
    HashMap<DeviceCommand, Sensor> sliderCommandsAndSensors = new HashMap<DeviceCommand, Sensor>();
    for (DeviceCommand deviceCommand : device.getDeviceCommands()) {
      if (deviceCommand.getName().indexOf("(SET)") != -1) {
        sliderCommands.put(deviceCommand.getName().replace(" (SET)", ""), deviceCommand);
      }
    }
    for (Sensor sensor : device.getSensors()) {
      if (sliderCommands.get(sensor.getName()) != null) {
        sliderCommandsAndSensors.put(sliderCommands.get(sensor.getName()), sensor);
      }
    }
    for (Entry<DeviceCommand, Sensor> entry : sliderCommandsAndSensors.entrySet()) {
      Slider slider = new Slider(entry.getValue().getName() + " Slider", entry.getKey(), entry.getValue());
      slider.setAccount(device.getAccount());
      device.getSliders().add(slider);
    }
  }

  private void addRussoundSwitches(Device device) {
    DeviceCommand powerOn = null;
    DeviceCommand powerOff = null;
    DeviceCommand loudnessOn = null;
    DeviceCommand loudnessOff = null;
    Sensor powerSensor = null;
    Sensor loudnessSensor = null;
    for (DeviceCommand deviceCommand : device.getDeviceCommands()) {
      if (deviceCommand.getName().indexOf("Power (ON)") != -1) {
        powerOn = deviceCommand;
      }
      else if (deviceCommand.getName().indexOf("Power (OFF)") != -1) {
        powerOff = deviceCommand;
      }
      else if (deviceCommand.getName().indexOf("Loudness (ON)") != -1) {
        loudnessOn = deviceCommand;
      }
      else if (deviceCommand.getName().indexOf("Loudness (OFF)") != -1) {
        loudnessOff = deviceCommand;
      }
    }
    for (Sensor sensor : device.getSensors()) {
      if (sensor.getName().indexOf("Power") != -1) {
        powerSensor = sensor;
      }
      else if (sensor.getName().indexOf("Loudness") != -1) {
        loudnessSensor = sensor;
      }      
    }
    Switch powerSwitch = new Switch(powerOn, powerOff, powerSensor);
    powerSwitch.setAccount(device.getAccount());
    device.getSwitchs().add(powerSwitch);
    Switch loudnessSwitch = new Switch(loudnessOn, loudnessOff, loudnessSensor);
    loudnessSwitch.setAccount(device.getAccount());
    device.getSwitchs().add(loudnessSwitch);
  }
  
}