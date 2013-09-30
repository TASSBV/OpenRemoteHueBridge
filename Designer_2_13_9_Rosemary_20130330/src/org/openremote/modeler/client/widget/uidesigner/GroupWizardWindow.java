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
package org.openremote.modeler.client.widget.uidesigner;

import org.openremote.modeler.client.widget.CommonForm;
import org.openremote.modeler.client.widget.WizardWindow;
import org.openremote.modeler.domain.GroupRef;
import org.openremote.modeler.domain.Panel;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.widget.form.FormPanel;

/**
 * The wizard window to create a new group, if click next button, can select screens for the group.
 */
public class GroupWizardWindow extends WizardWindow {

   public static final int SELECT_PANEL_STEP = 0;
   public static final int SELECT_SCREEN_STEP = 1;
   public GroupWizardWindow(BeanModel groupRefBeanModel) {
      super(groupRefBeanModel);
      setHeading("New Group");
      show();
   }

   @Override
   protected void initForms() {
      forms = new CommonForm[]{
            new SelectPanelForm(this, beanModel),
            new SelectScreenForm(this, beanModel)
      };
   }

   @Override
   protected void postProcess(int step, FormPanel currentForm) {
      switch (step) {
      case SELECT_PANEL_STEP:
         SelectPanelForm selectPanelForm = (SelectPanelForm) currentForm;
         SelectScreenForm selectScreenForm = (SelectScreenForm) forms[SELECT_SCREEN_STEP];
         selectScreenForm.update((Panel) selectPanelForm.getSelectedItem().getBean());
         break;
      case SELECT_SCREEN_STEP:
         
         break;
      default:
         break;
      }
   }

   @Override
   protected void finish(int step, FormPanel currentForm) {
      SelectPanelForm selectPanelForm = (SelectPanelForm) forms[SELECT_PANEL_STEP];
      switch (step) {
      case SELECT_PANEL_STEP:
         break;
      case SELECT_SCREEN_STEP:
         GroupRef groupRef = (GroupRef) beanModel.getBean();
         groupRef.getGroup().setName(selectPanelForm.getFields().get(0).getValue().toString());
         groupRef.setPanel((Panel) selectPanelForm.getSelectedItem().getBean());
         ((Panel) selectPanelForm.getSelectedItem().getBean()).addGroupRef(groupRef);
         break;
      default:
         break;
      }
      super.finish(step, currentForm);
   }

   
   
}
