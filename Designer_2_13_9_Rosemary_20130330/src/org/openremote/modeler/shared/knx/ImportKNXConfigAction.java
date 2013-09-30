package org.openremote.modeler.shared.knx;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import net.customware.gwt.dispatch.shared.Action;

import org.openremote.modeler.shared.dto.DeviceDTO;

import com.extjs.gxt.ui.client.data.ModelData;

public class ImportKNXConfigAction implements Action<ImportKNXConfigResult> {

  public static final Map<String, String> COMMAND_DPT_MAP;

  static {
      COMMAND_DPT_MAP = new TreeMap<String, String>();
      COMMAND_DPT_MAP.put("Switch", "1.001");
      COMMAND_DPT_MAP.put("Switch Status", "1.001");
      COMMAND_DPT_MAP.put("Dim/Scale 0-100%", "5.001");
      COMMAND_DPT_MAP.put("Dim/Scale Status", "5.001");
      COMMAND_DPT_MAP.put("Dimmer/Blind Step", "3.007");
      COMMAND_DPT_MAP.put("Range 0-255", "5.010");
      COMMAND_DPT_MAP.put("Range Status", "5.010");
      COMMAND_DPT_MAP.put("Temperature", "9.001");
      COMMAND_DPT_MAP.put("Temperature Status", "9.001");
      COMMAND_DPT_MAP.put("Play Scene", "17.001");
      COMMAND_DPT_MAP.put("Store Scene", "18.001");
      COMMAND_DPT_MAP.put("N/A", "N/A");
  }
  
  private DeviceDTO device;
  private ArrayList<ModelData> config;
  
  public ImportKNXConfigAction() {
    super();
  }

  public ImportKNXConfigAction(DeviceDTO device, ArrayList<ModelData> config) {
    super();
    this.device = device;
    this.config = config;
  }

  public DeviceDTO getDevice() {
    return device;
  }

  public void setDevice(DeviceDTO device) {
    this.device = device;
  }

  public ArrayList<ModelData> getConfig() {
    return config;
  }

  public void setConfig(ArrayList<ModelData> config) {
    this.config = config;
  }
}