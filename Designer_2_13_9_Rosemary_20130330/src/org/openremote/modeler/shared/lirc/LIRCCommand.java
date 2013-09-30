package org.openremote.modeler.shared.lirc;

import java.io.Serializable;

public class LIRCCommand implements Serializable {

  private static final long serialVersionUID = 1L;
  
  private String name;
  private String remoteName;
  
  public LIRCCommand() {
    super();
  }

  public LIRCCommand(String name, String remoteName) {
    super();
    this.name = name;
    this.remoteName = remoteName;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getRemoteName() {
    return remoteName;
  }

  public void setRemoteName(String remoteName) {
    this.remoteName = remoteName;
  }
  
}
