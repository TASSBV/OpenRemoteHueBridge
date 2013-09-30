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
package org.openremote.modeler.irfileparser;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * Adds BeanModelTag capability to IRLed for compatibility with GXT stores.
 * 
 * @author Eric Bariaux (eric@openremote.org)
 *
 */
public class IRLed extends BaseModelData {

   private static final long serialVersionUID = 1L;

   public IRLed() {
   }

   public IRLed(String value, String code) {
      setValue(value);
      setCode(code);
   }

   public String getCode() {
      return get("code");
   }

   public void setCode(String code) {
      set("code", code);
   }

   protected String getValue() {
      return get("value");
   }

   protected void setValue(String value) {
      set("value", value);
   }
}