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
package org.openremote.modeler.client.widget;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;

/**
 * Wizard Window for a series of {@link CommonForm}.
 * 
 * @author Dan 2009-8-21
 */
public class WizardWindow extends CommonWindow {

   /** The form series. */
   protected CommonForm[] forms;

   /** The current step. */
   protected int currentStep;

   /** The device bean model. */
   protected BeanModel beanModel;

   /** The back btn. */
   protected Button backBtn;
   
   /** The next btn. */
   protected Button nextBtn;
   
   /** The finish btn. */
   protected Button finishBtn;

   /**
    * Instantiates a new wizard window.
    * 
    * @param deviceBeanModel
    *           the device bean model
    */
   public WizardWindow(BeanModel deviceBeanModel) {
      super();
      this.beanModel = deviceBeanModel;
      init();
   }

   /**
    * Inits the forms.
    */
   protected void initForms() {
      forms = new CommonForm[] {};
   }

   /**
    * Inits the window.
    */
   private void init() {
      setAutoHeight(true);
      setLayout(new FlowLayout());
      showButtons();
      initForms();
      showFirstForm();
   }

   /**
    * Show buttons.
    */
   private void showButtons() {

      backBtn = new Button("< Back");
      backBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            postProcess(currentStep, forms[currentStep]);
            showStepForm(currentStep - 1);
            forms[currentStep--].hide();
            layout();
            center();
         }

      });
      addButton(backBtn);
      nextBtn = new Button("Next >");
      nextBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            if (forms[currentStep].isValid()) {
               postProcess(currentStep, forms[currentStep]);
               showStepForm(currentStep + 1);
               forms[currentStep++].hide();
               layout();
               center();
            }
         }

      });
      addButton(nextBtn);

      finishBtn = new Button("Finish");
//      finishBtn.ensureDebugId(DebugId.DEVICE_FINISH_BTN);
      finishBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            if (forms[currentStep].isValid()) {
               finishBtn.disable();
               finish(currentStep, forms[currentStep]);
            }
         }

      });
      addButton(finishBtn);
   }


   /**
    * Show first form.
    */
   private void showFirstForm() {
      for (int i = 0; i < forms.length; i++) {
         add(forms[i]);
         if (i == 0) {
            showStepForm(i);
         } else {
            forms[i].hide();
         }
      }
   }

   /**
    * Show step form.
    * 
    * @param step the step
    */
   private void showStepForm(int step) {
      forms[step].show();
      setFocusWidget(forms[step].getFields().get(0));
      if (step == forms.length - 1) {
         nextBtn.disable();
      } else {
         nextBtn.enable();
      }
      if (step == 0) {
         backBtn.disable();
      } else {
         backBtn.enable();
      }
   }

   /**
    * Post process of every step. 
    * This function does nothing by default, you'd better override it to your own implementation.
    * 
    * @param step
    *           the step
    * @param currentForm
    *           the current form
    */
   protected void postProcess(int step, FormPanel currentForm) {
      // nothing
   }

   /**
    * Finish this wizard. This function just submit the current form. 
    * You'd better override it to your own implementation.
    * 
    * @param step
    *           the step
    * @param currentForm
    *           the current form
    */
   protected void finish(int step, FormPanel currentForm) {
      currentForm.submit();
   }
   

}
