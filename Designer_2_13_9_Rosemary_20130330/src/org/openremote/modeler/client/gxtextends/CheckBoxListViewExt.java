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
package org.openremote.modeler.client.gxtextends;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.ListView;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.Element;

/**
 * Rewrite the CheckBoxListView, support to click the label and check the input, just modified the OnRender method.
 */
public class CheckBoxListViewExt<M extends ModelData> extends ListView<M> {
   private String checkBoxSelector = ".x-view-item-checkbox";
   protected List<M> checkedPreRender;

   public String getCheckBoxSelector() {
     return checkBoxSelector;
   }

   public List<M> getChecked() {
     List<M> l = new ArrayList<M>();
     NodeList<Element> nodes = el().select(checkBoxSelector);
     for (int i = 0; i < nodes.getLength(); i++) {
       InputElement e = nodes.getItem(i).cast();
       if (e.isChecked()) {
         l.add(getStore().getAt(i));
       }
     }
     return l;
   }

   public void setCheckBoxSelector(String checkBoxSelector) {
     this.checkBoxSelector = checkBoxSelector;
   }

   /**
    * Selects a specific item in the view
    * 
    * @param m the modeldata that should be checked
    * @param checked true to check
    */
   public void setChecked(M m, boolean checked) {
     if (rendered) {
       NodeList<Element> nodes = el().select(checkBoxSelector);
       int index = store.indexOf(m);
       if (index != -1) {
         Element e = nodes.getItem(index);
         if (e != null) {
           ((InputElement) e.cast()).setChecked(checked);
         }
       }
     } else {
       if (checkedPreRender == null) {
         checkedPreRender = new ArrayList<M>();
       }
       if (checked) {
         if (!checkedPreRender.contains(m)) {
           checkedPreRender.add(m);
         }
       } else {
         checkedPreRender.remove(m);
       }
     }
   }

   protected void onRender(Element target, int index) {
     if (getTemplate() == null) {
       String spacing = GXT.isIE ? "0" : "3";
       String displayProperty = getDisplayProperty();
       setTemplate(XTemplate.create("<tpl for=\".\"><div class='x-view-item x-view-item-check'><table cellspacing='"
           + spacing + "' cellpadding=0><tr><td><input class=\"x-view-item-checkbox\" type=\"checkbox\" id=\"{"
           + displayProperty + "}\"/></td><td><td><label for=\"{" + displayProperty + "}\">{"
           + displayProperty + "}</label></td></tr></table></div></tpl>"));
     }
     super.onRender(target, index);
     if (checkedPreRender != null) {
       for (M m : checkedPreRender) {
         setChecked(m, true);
       }
       checkedPreRender = null;
     }
   }
}
