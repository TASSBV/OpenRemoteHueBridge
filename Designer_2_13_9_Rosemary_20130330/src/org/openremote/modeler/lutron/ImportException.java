package org.openremote.modeler.lutron;

public class ImportException extends Exception {

  /**
   * Represents an issue occurring during import.
   */
  private static final long serialVersionUID = 1L;

  public ImportException(String arg0, Throwable arg1) {
    super(arg0, arg1);
  }
  
}
