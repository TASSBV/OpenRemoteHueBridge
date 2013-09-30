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

import java.util.ArrayList;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.proxy.SliderBeanModelProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.shared.dto.DTOHelper;
import org.openremote.modeler.shared.dto.SliderWithInfoDTO;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

/**
 * The window to select a slider command for the slider component.
 */
public class SelectSliderWindow extends Dialog {

   private ListView<BeanModel> sliderList = new ListView<BeanModel>();
   public SelectSliderWindow() {
      setHeading("Select Slider");
      setMinHeight(320);
      setWidth(240);
      setLayout(new RowLayout(Orientation.VERTICAL));
      setModal(true);
      initSliderList();
      initSliderInfo();
      setButtons(Dialog.OKCANCEL);
      setHideOnButtonClick(true);
      addButtonListener();
      show();
   }

   private void initSliderList() {
      ContentPanel sliderListContainer = new ContentPanel();
      sliderListContainer.setSize(240, 150);
      sliderListContainer.setBorders(false);
      sliderListContainer.setBodyBorder(false);
      sliderListContainer.setHeaderVisible(false);
      // overflow-auto style is for IE hack.
      sliderListContainer.addStyleName("overflow-auto");
      
      final ListStore<BeanModel> store = new ListStore<BeanModel>();
      SliderBeanModelProxy.loadAllSliderWithInfosDTO(new AsyncSuccessCallback<ArrayList<SliderWithInfoDTO>>() {
        @Override
        public void onSuccess(ArrayList<SliderWithInfoDTO> result) {
          store.add(DTOHelper.createModels(result));
        }
      });
      sliderList.setStore(store);
      sliderList.setDisplayProperty("displayName");
      sliderList.setStyleAttribute("overflow", "auto");
      sliderList.setBorders(false);
      sliderList.setHeight(150);
      sliderListContainer.add(sliderList);
      add(sliderListContainer, new RowData(1, -1, new Margins(4)));
   }
   
   private void initSliderInfo() {
      final Html sliderInfoHtml = new Html("<p><b>Slider info</b></p>"); 
      sliderList.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<BeanModel>() {
         @Override
         public void selectionChanged(SelectionChangedEvent<BeanModel> se) {
            BeanModel selectedSliderModel = se.getSelectedItem();
            if (selectedSliderModel != null) {
               SliderWithInfoDTO slider = selectedSliderModel.getBean();
               String sliderInfo = "<p><b>Slider info</b></p>";
               if (slider.getCommandName() != null){
                  sliderInfo = sliderInfo + "<p>Command: " + slider.getCommandName() + "</p>";
               }
               if (slider.getSensorName() != null) {
                  sliderInfo = sliderInfo + "<p>Sensor: " + slider.getSensorName() + "</p>";
               }
               sliderInfo = sliderInfo + "<p>Device: " + slider.getDeviceName() + "</p>";
               sliderInfoHtml.setHtml(sliderInfo);
            }
         }
      });
      add(sliderInfoHtml, new RowData(1, -1, new Margins(4)));
   }
   
   private void addButtonListener() {
      addListener(Events.BeforeHide, new Listener<WindowEvent>() {
         public void handleEvent(WindowEvent be) {
            if (be.getButtonClicked() == getButtonById("ok")) {
               BeanModel beanModel = sliderList.getSelectionModel().getSelectedItem();
               if (beanModel == null) {
                  MessageBox.alert("Error", "Please select a slider.", null);
                  be.cancelBubble();
               } else {
                  if (beanModel.getBean() instanceof SliderWithInfoDTO) {
                     fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(beanModel));
                  } else {
                     MessageBox.alert("Error", "Please select a slider.", null);
                     be.cancelBubble();
                  }
               }
            }
         }
      }); 
   }
   
}
