/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.openremote.modeler.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import flexjson.JSON;

/**
 * A domain class which represent a configuration. 
 * The name and value property is necessary.
 * The property validation is used to validate the value.
 * The property options represent the options for the value.
 * @author javen
 *
 */
@Entity
@Table(name = "controller_config")
public class ControllerConfig extends BusinessEntity{
   private static final long serialVersionUID = -6443368320902438959L;
   
   public static final String NAME_XML_ATTRIBUTE_NAME = "name";
   public static final String VALUE_XML_ATTRIBUTE_NAME = "value";
   public static final String VALIDATION_XML_ATTRIBUTE_NAME = "validation";
   public static final String OPTION_XML_ATTRIBUTE_NAME = "options";
   
   public static final String HINT_XML_NODE_NAME = "hint";
   public static final String XML_NODE_NAME = "config";
   
   private String category = "";
   private String name = "";
   private String value = "";
   private String hint = "";
   private String validation = ".+";
   private String options = "";
   
   private Account account = null;
   
   public ControllerConfig(){}
   
   
   public ControllerConfig(String category, String name, String value, String hint, Account account) {
      this.category = category;
      this.name = name;
      this.value = value;
      this.hint = hint;
      this.account = account;
   }


   @Column(nullable = false)
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }
   @Transient
   @JSON(include=true)
   public String getHint() {
      return hint;
   }

   public void setHint(String hint) {
      this.hint = hint;
   }
   @Lob @Column(nullable = false)
   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   @ManyToOne
   @JSON(include = false)
   public Account getAccount() {
      return account;
   }

   public void setAccount(Account account) {
      this.account = account;
   }
   
   public String getCategory() {
      return category;
   }
   
   public void setCategory(String category) {
      this.category = category;
   }
   @Transient
   public String getValidation() {
      return validation;
   }


   public void setValidation(String validation) {
      this.validation = validation;
   }

   @Transient
   public String getOptions() {
      return options;
   }


   public void setOptions(String options) {
      this.options = options;
   }
   
   @Transient
   public String toString(){
      StringBuilder sb = new StringBuilder();
      sb.append("<"+XML_NODE_NAME+" "+NAME_XML_ATTRIBUTE_NAME+"=\""+name+ "\""+ VALUE_XML_ATTRIBUTE_NAME +"=\""+value+"\" " +VALIDATION_XML_ATTRIBUTE_NAME+"=\""+validation+"\" "+OPTION_XML_ATTRIBUTE_NAME+"=\""+options+"\">\n");
      sb.append("\t<"+HINT_XML_NODE_NAME+">\n");
      sb.append(hint+"\n");
      sb.append("\t</"+HINT_XML_NODE_NAME+">\n");
      sb.append("</"+XML_NODE_NAME+">");
      return sb.toString();
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((category == null) ? 0 : category.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      return result;
   }


   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      ControllerConfig other = (ControllerConfig) obj;
      if (category == null) {
         if (other.category != null) return false;
      } else if (!category.equals(other.category)) return false;
      if (name == null) {
         if (other.name != null) return false;
      } else if (!name.equals(other.name)) return false;
      return true;
   }
   
   
}
