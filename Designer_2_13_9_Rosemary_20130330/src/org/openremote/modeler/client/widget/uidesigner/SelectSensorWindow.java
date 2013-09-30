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
import org.openremote.modeler.client.proxy.SensorBeanModelProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.shared.dto.DTOHelper;
import org.openremote.modeler.shared.dto.SensorWithInfoDTO;

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
 * The window is to select sensor for image or label component.
 */
public class SelectSensorWindow extends Dialog {

   private ListView<BeanModel> sensorList = new ListView<BeanModel>();
   public SelectSensorWindow() {
      setHeading("Select Sensor");
      setMinHeight(320);
      setWidth(240);
      setLayout(new RowLayout(Orientation.VERTICAL));
      setModal(true);
      initSensorList();
      initSensorInfo();
      setButtons(Dialog.OKCANCEL);
      setHideOnButtonClick(true);
      addButtonListener();
      show();
   }

   private void initSensorList() {
      ContentPanel sensorListContainer = new ContentPanel();
      sensorListContainer.setSize(240, 150);
      sensorListContainer.setBorders(false);
      sensorListContainer.setBodyBorder(false);
      sensorListContainer.setHeaderVisible(false);
      // overflow-auto style is for IE hack.
      sensorListContainer.addStyleName("overflow-auto");
      
      final ListStore<BeanModel> store = new ListStore<BeanModel>();
      SensorBeanModelProxy.loadAllSensorWithInfosDTO(new AsyncSuccessCallback<ArrayList<SensorWithInfoDTO>>() {
        @Override
        public void onSuccess(ArrayList<SensorWithInfoDTO> result) {
          store.add(DTOHelper.createModels(result));

        }
      });      
      sensorList.setHeight(150);
      sensorList.setStore(store);
      sensorList.setDisplayProperty("displayName");
      sensorList.setStyleAttribute("overflow", "auto");
      sensorList.setBorders(false);
      sensorListContainer.add(sensorList);
      add(sensorListContainer, new RowData(1, -1, new Margins(4)));
   }
   
   private void initSensorInfo() {
      final Html sensorInfoHtml = new Html("<p><b>Sensor info</b></p>"); 
      sensorList.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<BeanModel>() {
         @Override
         public void selectionChanged(SelectionChangedEvent<BeanModel> se) {
            BeanModel selectedSensorModel = se.getSelectedItem();
            if (selectedSensorModel != null) {
               SensorWithInfoDTO sensor = selectedSensorModel.getBean();
               String sensorInfo = "<p><b>Sensor info</b></p><p>Type: " + sensor.getType() + "</p><p>Command: "
                     + sensor.getCommandName() + "</P>";
               if (sensor.getType() == SensorType.RANGE) {
                  sensorInfo = sensorInfo + "<p>Min: " + sensor.getMinValue() + "</p>";
                  sensorInfo = sensorInfo + "<p>Max: " + sensor.getMaxValue() + "</p>";
               } else if (sensor.getType() == SensorType.CUSTOM) {
                  sensorInfo = sensorInfo + "<p>States: " + sensor.getStatesInfo() + "</p>";
               }
               sensorInfoHtml.setHtml(sensorInfo);
            }
         }
      });
      add(sensorInfoHtml, new RowData(1, -1, new Margins(4)));
   }
   
   private void addButtonListener() {
      addListener(Events.BeforeHide, new Listener<WindowEvent>() {
         public void handleEvent(WindowEvent be) {
            if (be.getButtonClicked() == getButtonById("ok")) {
               BeanModel beanModel = sensorList.getSelectionModel().getSelectedItem();
               if (beanModel == null) {
                  MessageBox.alert("Error", "Please select a sensor.", null);
                  be.cancelBubble();
               } else {
                  if (beanModel.getBean() instanceof SensorWithInfoDTO) {
                     fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(beanModel));
                  } else {
                     MessageBox.alert("Error", "Please select a sensor.", null);
                     be.cancelBubble();
                  }
               }
            }
         }
      }); 
   }

}
