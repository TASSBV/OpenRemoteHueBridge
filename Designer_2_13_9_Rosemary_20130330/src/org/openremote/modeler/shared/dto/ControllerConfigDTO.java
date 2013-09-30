package org.openremote.modeler.shared.dto;


public class ControllerConfigDTO implements DTO {

  private static final long serialVersionUID = 1L;

  private static final String OPTION_SPLIT_SEPARATOR = ",";

  private Long oid;
  private String category;
  private String name;
  private String value;
  private String hint;
  private String validation = ".+";
  private String options;

  public ControllerConfigDTO() {
    super();
  }

  public ControllerConfigDTO(Long oid, String category, String name, String value, String hint, String validation, String options) {
    super();
    this.oid = oid;
    this.category = category;
    this.name = name;
    this.value = value;
    this.hint = hint;
    this.validation = validation;
    this.options = options;
  }

  public Long getOid() {
    return oid;
  }

  public void setOid(Long oid) {
    this.oid = oid;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getHint() {
    return hint;
  }

  public void setHint(String hint) {
    this.hint = hint;
  }

  public String getValidation() {
    return validation;
  }

  public void setValidation(String validation) {
    this.validation = validation;
  }

  public String getOptions() {
    return options;
  }

  public void setOptions(String options) {
    this.options = options;
  }

  public String[] optionsArray(){
    return options.split(OPTION_SPLIT_SEPARATOR);
 }

}
