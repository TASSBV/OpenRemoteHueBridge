package org.openremote.modeler.server.lirc;

import java.util.ArrayList;
import java.util.List;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.Protocol;
import org.openremote.modeler.service.DeviceCommandService;
import org.openremote.modeler.service.DeviceService;
import org.openremote.modeler.shared.lirc.ImportLIRCCommandsAction;
import org.openremote.modeler.shared.lirc.ImportLIRCCommandsResult;
import org.openremote.modeler.shared.lirc.LIRCCommand;

public class ImportLIRCCommandsActionHandler implements ActionHandler<ImportLIRCCommandsAction, ImportLIRCCommandsResult> {

  private DeviceService deviceService;
  private DeviceCommandService deviceCommandService;
  
  @Override
  public ImportLIRCCommandsResult execute(ImportLIRCCommandsAction action, ExecutionContext context) throws DispatchException {
    Device device = deviceService.loadById(action.getDevice().getOid());
    List<DeviceCommand> deviceCommands = new ArrayList<DeviceCommand>();
    for (LIRCCommand cmd : action.getCommands()) {
      DeviceCommand deviceCommand = new DeviceCommand();
      deviceCommand.setDevice(device);      
      Protocol protocol = deviceCommand.createProtocol(Constants.INFRARED_TYPE);
      protocol.addProtocolAttribute("name", cmd.getRemoteName());
      protocol.addProtocolAttribute("command", cmd.getName());
      deviceCommand.setName(cmd.getName());
      deviceCommand.setSectionId(action.getSectionId());
      device.getDeviceCommands().add(deviceCommand);
      deviceCommands.add(deviceCommand);
    }
    deviceCommandService.saveAll(deviceCommands);
    return new ImportLIRCCommandsResult();
  }

  @Override
  public Class<ImportLIRCCommandsAction> getActionType() {
    return ImportLIRCCommandsAction.class;
  }

  @Override
  public void rollback(ImportLIRCCommandsAction action, ImportLIRCCommandsResult result, ExecutionContext context) throws DispatchException {
    // TODO Implementation only required for compound action
  }

  public void setDeviceService(DeviceService deviceService) {
    this.deviceService = deviceService;
  }

  public void setDeviceCommandService(DeviceCommandService deviceCommandService) {
    this.deviceCommandService = deviceCommandService;
  }

}
