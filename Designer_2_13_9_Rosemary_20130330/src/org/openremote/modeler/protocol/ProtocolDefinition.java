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

package org.openremote.modeler.protocol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class defines a protocol with its properties. A protocol has display name, tag name and some attributes.
 * 
 * The protocol xml segment structure is similar following:</br>
 * 
 * &lt;protocol displayName="Infrared" tagName="ir"&gt;</br>
      &lt;attr name="name" label="Name"&gt;</br>
         &lt;validations&gt;</br>
            &lt;allowBlank&gt;false&lt;/allowBlank&gt;</br>
         &lt;/validations&gt;</br>
      &lt;/attr>
      &lt;attr name="command" label="IR Command"&gt;</br>
         &lt;validations&gt;</br>
            &lt;allowBlank&gt;false&lt;/allowBlank&gt;</br>
            &lt;maxLength&gt;10&lt;/maxLength&gt;</br>
            &lt;regex message="Command is necessary"&gt;\w+&lt;/regex&gt;</br>
         &lt;/validations&gt;</br>
      &lt;/attr&gt;</br>
   &lt;/protocol&gt;</br>
 * 
 * @author <a href="mailto:allen.wei@finalist.cn">allen.wei</a>
 */
public class ProtocolDefinition implements Serializable {
   
   /** The Constant serialVersionUID. */
   private static final long serialVersionUID = -726881807822688804L;
   
   /** The name. */
   private String displayName;
   
   /** The tag name. */
   private String tagName;
   
   /** The attrs. */
   private List<ProtocolAttrDefinition> attrs = new ArrayList<ProtocolAttrDefinition>();

   /**
    * Gets the display name.
    * 
    * @return the display name
    */
   public String getDisplayName() {
      return displayName;
   }

   /**
    * Sets the display name.
    * 
    * @param displayName the new display name
    */
   public void setDisplayName(String displayName) {
      this.displayName = displayName;
   }

   /**
    * Gets the tag name.
    * 
    * @return the tag name
    */
   public String getTagName() {
      return tagName;
   }

   /**
    * Sets the tag name.
    * 
    * @param tagName the new tag name
    */
   public void setTagName(String tagName) {
      this.tagName = tagName;
   }

   /**
    * Gets the attrs.
    * 
    * @return the attrs
    */
   public List<ProtocolAttrDefinition> getAttrs() {
      return attrs;
   }

   /**
    * Sets the attrs.
    * 
    * @param attrs the new attrs
    */
   public void setAttrs(List<ProtocolAttrDefinition> attrs) {
      this.attrs = attrs;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }

      ProtocolDefinition that = (ProtocolDefinition) o;

      if (displayName != null ? !displayName.equals(that.displayName) : that.displayName != null) {
         return false;
      }
      if (tagName != null ? !tagName.equals(that.tagName) : that.tagName != null) {
         return false;
      }
      if (attrs == null && that.attrs == null) {
         return true;
      }
      if (attrs == null || that.attrs == null) {
         return false;
      }
      if (attrs.size() == that.attrs.size()) {
         for (int i = 0; i < attrs.size(); i++) {
            if (!attrs.get(i).equals(that.attrs.get(i))) {
               return false;
            }
         }
      } else {
         return false;
      }
      return true;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      int result = (displayName != null) ? displayName.hashCode() : 0;
      result += (tagName != null) ? tagName.hashCode() : 0;
      return 31 * result + (attrs != null ? attrs.hashCode() : 0);
   }
}
