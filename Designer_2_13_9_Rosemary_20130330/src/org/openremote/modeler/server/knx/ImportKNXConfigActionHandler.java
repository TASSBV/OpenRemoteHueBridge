package org.openremote.modeler.server.knx;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.domain.CustomSensor;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.Protocol;
import org.openremote.modeler.domain.RangeSensor;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorCommandRef;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.service.DeviceCommandService;
import org.openremote.modeler.service.DeviceService;
import org.openremote.modeler.service.SensorService;
import org.openremote.modeler.shared.knx.ImportKNXConfigAction;
import org.openremote.modeler.shared.knx.ImportKNXConfigResult;

import com.extjs.gxt.ui.client.data.ModelData;

public class ImportKNXConfigActionHandler implements ActionHandler<ImportKNXConfigAction, ImportKNXConfigResult> {

  protected DeviceService deviceService;
  protected DeviceCommandService deviceCommandService;
  protected SensorService sensorService;
  
  public void setDeviceService(DeviceService deviceService) {
    this.deviceService = deviceService;
  }

  public void setDeviceCommandService(DeviceCommandService deviceCommandService) {
    this.deviceCommandService = deviceCommandService;
  }
  
  public void setSensorService(SensorService sensorService) {
    this.sensorService = sensorService;
  }

  @Override
  public ImportKNXConfigResult execute(ImportKNXConfigAction action, ExecutionContext context) throws DispatchException {
    ImportKNXConfigResult result = new ImportKNXConfigResult();
    Device device = deviceService.loadById(action.getDevice().getOid());
    createKNXObjects(device, action.getConfig());
    return result;
  }

  @Override
  public Class<ImportKNXConfigAction> getActionType() {
    return ImportKNXConfigAction.class;
  }

  @Override
  public void rollback(ImportKNXConfigAction action, ImportKNXConfigResult result, ExecutionContext context) throws DispatchException {
    // TODO Implementation only required for compound action
  }

  private void createKNXObjects(Device device, List<ModelData> importData) {
    List<DeviceCommand> deviceCommands = createDeviceCommandsFromGridData(device, importData);
    createSensorsForStatusCommands(device, deviceCommandService.saveAll(deviceCommands));
  }

  private void createSensorsForStatusCommands(Device device, List<DeviceCommand> deviceCommands) {
    
    List<Sensor> sensorList = createSensorListFromDevices(device, deviceCommands);
    sensorService.saveAllSensors(sensorList, device.getAccount());

/*
         List<BeanModel> allCommandsAndSensors = new ArrayList<BeanModel>();

        allCommandsAndSensors.addAll(deviceCommandModels);
        allCommandsAndSensors.addAll(sensordModels);
        */
  }

  private List<Sensor> createSensorListFromDevices(Device device, List<DeviceCommand> deviceCommands) {
    List<Sensor> result = new ArrayList<Sensor>();
    for (DeviceCommand deviceCommand : deviceCommands) {
      Protocol prot = deviceCommand.getProtocol();
      String cmdName = prot.getAttributeValue("command");
      String dpt = prot.getAttributeValue("DPT");
      if ("status".equalsIgnoreCase(cmdName)) {
        if ("1.001".equals(dpt)) {
          Sensor sensor = createSensor(device, SensorType.SWITCH, deviceCommand);
          result.add(sensor);
        } else if ("5.001".equals(dpt)) {
          Sensor sensor = createSensor(device, SensorType.LEVEL, deviceCommand);
          result.add(sensor);
        } else if ("5.010".equals(dpt)) {
          Sensor sensor = createSensor(device, SensorType.RANGE, deviceCommand);
          result.add(sensor);
        } else if ("9.001".equals(dpt)) {
          Sensor sensor = createSensor(device, SensorType.CUSTOM, deviceCommand);
          result.add(sensor);
        }
      }
    }
    return result;
  }

  private Sensor createSensor(Device device, SensorType type, DeviceCommand command) {
    Sensor sensor;
    if (type == SensorType.RANGE) {
      sensor = new RangeSensor();
      ((RangeSensor) sensor).setMin(0);
      ((RangeSensor) sensor).setMax(255);
    } else if (type == SensorType.CUSTOM) {
      sensor = new CustomSensor();
    } else {
      sensor = new Sensor();
    }
    sensor.setType(type);
    sensor.setName(command.getName());

    SensorCommandRef sensorCommandRef = new SensorCommandRef();
    sensorCommandRef.setDeviceCommand(command);
    sensorCommandRef.setSensor(sensor);
    sensor.setSensorCommandRef(sensorCommandRef);
    sensor.setDevice(device);
    return sensor;
  }

  private List<DeviceCommand> createDeviceCommandsFromGridData(Device device, List<ModelData> importDataList) {
    List<DeviceCommand> result = new ArrayList<DeviceCommand>();
    for (ModelData importData : importDataList) {
      DeviceCommand deviceCommand = null;
      String name = importData.get("Name");
      String groupAddress = importData.get("GroupAddress");
      String commandType = importData.get("commandType");
      String dpt = ImportKNXConfigAction.COMMAND_DPT_MAP.get(commandType);
      if (commandType.indexOf("Status") != -1) {
        deviceCommand = createKNXDeviceCommand(device, name, groupAddress, "status", dpt);
        result.add(deviceCommand);
      } else if ("Switch".equals(commandType)) {
        deviceCommand = createKNXDeviceCommand(device, name + " (ON)", groupAddress, "on", dpt);
        result.add(deviceCommand);
        deviceCommand = createKNXDeviceCommand(device, name + " (OFF)", groupAddress, "off", dpt);
        result.add(deviceCommand);
      } else if ("Dim/Scale 0-100%".equals(commandType)) {
        deviceCommand = createKNXDeviceCommand(device, name, groupAddress, "scale", dpt);
        result.add(deviceCommand);
      } else if ("Dimmer/Blind Step".equals(commandType)) {
        deviceCommand = createKNXDeviceCommand(device, name + " (UP)", groupAddress, "dim_increase", dpt);
        result.add(deviceCommand);
        deviceCommand = createKNXDeviceCommand(device, name + " (DOWN)", groupAddress, "dim_decrease", dpt);
        result.add(deviceCommand);
      } else if ("Range 0-255".equals(commandType)) {
        deviceCommand = createKNXDeviceCommand(device, name, groupAddress, "range", dpt);
        result.add(deviceCommand);
      } else if ("Play Scene".equals(commandType)) {
        String sceneNo = importData.get("SceneNumber");
        deviceCommand = createKNXDeviceCommand(device, name, groupAddress, "scene " + sceneNo, dpt);
        result.add(deviceCommand);
      } else if ("Store Scene".equals(commandType)) {
        String sceneNo = importData.get("SceneNumber");
        deviceCommand = createKNXDeviceCommand(device, name, groupAddress, "learn_scene " + sceneNo, dpt);
        result.add(deviceCommand);
      } else if ("Temperature".equals(commandType)) {
        deviceCommand = createKNXDeviceCommand(device, name, groupAddress, "temp", dpt);
        result.add(deviceCommand);
      }
    }
    return result;
  }
  
  public DeviceCommand createKNXDeviceCommand(Device device, String name, String groupAddress, String command, String dpt) {
    DeviceCommand deviceCommand = new DeviceCommand();
    deviceCommand.setDevice(device);
    deviceCommand.setName(name);
    
    Protocol protocol = deviceCommand.createProtocol(Constants.KNX_TYPE);
    protocol.addProtocolAttribute("command", command);
    protocol.addProtocolAttribute("DPT", dpt);
    protocol.addProtocolAttribute("groupAddress", groupAddress);

    device.getDeviceCommands().add(deviceCommand);
    return deviceCommand;
  }

}