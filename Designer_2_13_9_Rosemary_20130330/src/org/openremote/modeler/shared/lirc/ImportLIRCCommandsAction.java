package org.openremote.modeler.shared.lirc;

import java.util.ArrayList;

import net.customware.gwt.dispatch.shared.Action;

import org.openremote.modeler.shared.dto.DeviceDTO;

public class ImportLIRCCommandsAction implements Action<ImportLIRCCommandsResult> {

  private DeviceDTO device;
  private String sectionId;
  private ArrayList<LIRCCommand> commands;
  
  public ImportLIRCCommandsAction() {
    super();
  }

  public ImportLIRCCommandsAction(DeviceDTO device, String sectionId, ArrayList<LIRCCommand> commands) {
    super();
    this.device = device;
    this.sectionId = sectionId;
    this.commands = commands;
  }

  public DeviceDTO getDevice() {
    return device;
  }

  public void setDevice(DeviceDTO device) {
    this.device = device;
  }

  public String getSectionId() {
    return sectionId;
  }

  public void setSectionId(String sectionId) {
    this.sectionId = sectionId;
  }

  public ArrayList<LIRCCommand> getCommands() {
    return commands;
  }

  public void setCommands(ArrayList<LIRCCommand> commands) {
    this.commands = commands;
  }
  
}
