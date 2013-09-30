package org.openremote.modeler.server.lutron.importmodel;

public class Button {

  private String name;
  private int number;
  
  public Button(String name, int number) {
    super();
    this.name = name;
    this.number = number;
  }

  public String getName() {
    return name;
  }

  public int getNumber() {
    return number;
  }

}
