package org.openremote.modeler.shared.ir;

import net.customware.gwt.dispatch.shared.Result;

public class GenerateIRCommandsResult implements Result {
  
  private String errorMessage;
  
  public GenerateIRCommandsResult() {
    super();
  }

  public GenerateIRCommandsResult(String errorMessage) {
    super();
    this.errorMessage = errorMessage;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

}
