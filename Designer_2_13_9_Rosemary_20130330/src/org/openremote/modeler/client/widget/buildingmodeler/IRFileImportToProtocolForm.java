/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.modeler.client.widget.buildingmodeler;

import java.util.ArrayList;
import java.util.List;

import net.customware.gwt.dispatch.client.DispatchAsync;

import org.openremote.ir.domain.GlobalCache;
import org.openremote.ir.domain.IRTrans;
import org.openremote.modeler.client.ModelerGinjector;
import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.listener.FormResetListener;
import org.openremote.modeler.client.listener.FormSubmitListener;
import org.openremote.modeler.client.widget.FormWindow;
import org.openremote.modeler.irfileparser.IRCommandInfo;
import org.openremote.modeler.irfileparser.IRLed;
import org.openremote.modeler.shared.dto.DeviceDTO;
import org.openremote.modeler.shared.ir.GenerateIRCommandsAction;
import org.openremote.modeler.shared.ir.GenerateIRCommandsResult;
import org.restlet.client.Request;
import org.restlet.client.Response;
import org.restlet.client.Uniform;
import org.restlet.client.resource.ClientResource;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ListBox;

/**
 * The window creates a deviceCommand into server.
 */
public class IRFileImportToProtocolForm extends FormWindow {

   private DeviceDTO device = null;
   
   private String prontoFileHandle;
   
   private VerticalPanel gCPanel;
   private TextField<String> gCip;
   private TextField<String> tcpPort;
   protected boolean hideWindow = true;

   protected LabelField info;

   private TextField<String> connector;

   private VerticalPanel iRTransPanel;

   private TextField<String> udpPort;

   private ComboBox<IRLed> iRLed;

   private TextField<String> iRTransIp;

   private ArrayList<IRCommandInfo> selectedFunctions;
   @SuppressWarnings("unused")
   private ComboBox<IRLed> IRLed;
   private IRFileImportWindow wrapper;
   private Button submitBtn;
   protected static final String INFO_FIELD = "infoField";

   /**
    * Instantiates a new device command window.
    * 
    * @param wrapper
    * 
    * @param device
    *           the device
    */
   public IRFileImportToProtocolForm(IRFileImportWindow wrapper, String irServiceRootRestURL, String prontoFileHandle, DeviceDTO device) {
      super();
      this.prontoFileHandle = prontoFileHandle;
      this.device = device;
      this.wrapper = wrapper;
      setHeading("New command");
      initial(irServiceRootRestURL);
      show();
   }

   /**
    * Initial.
    */
   private void initial(final String irServiceRootRestURL) {
      setWidth(380);
      setAutoHeight(true);
      setLayout(new FlowLayout());
      info = new LabelField();
      info.setStyleName("importErrorMessage");
      form.setWidth(370);

      submitBtn = new Button("Submit");
      submitBtn.setEnabled(false);
      form.addButton(submitBtn);

      submitBtn.addSelectionListener(new FormSubmitListener(form, submitBtn));

      Button resetButton = new Button("Reset");
      resetButton.addSelectionListener(new FormResetListener(form));
      form.addButton(resetButton);

      form.addListener(Events.BeforeSubmit, new Listener<FormEvent>() {

         @Override
         public void handleEvent(FormEvent be) {
            info.setVisible(false);
            form.mask("Please Wait...");
            GlobalCache globalCache = null;
            IRTrans irTrans = null;

            if (gCPanel.isEnabled()) {
               globalCache = new GlobalCache(gCip.getValue(), tcpPort
                     .getValue(), connector.getValue());
            } else {
               irTrans = new IRTrans(iRTransIp.getValue(), udpPort.getValue(),
                     iRLed.getSelection().get(0).getCode());
            }
            
            ModelerGinjector injector = GWT.create(ModelerGinjector.class);
            DispatchAsync dispatcher = injector.getDispatchAsync();

            GenerateIRCommandsAction action = new GenerateIRCommandsAction(device, prontoFileHandle, selectedFunctions, globalCache, irTrans);
            dispatcher.execute(action, new AsyncCallback<GenerateIRCommandsResult>() {

              @Override
              public void onFailure(Throwable caught) {
                reportError(caught.getMessage());
              }

              @Override
              public void onSuccess(GenerateIRCommandsResult result) {
                if (result.getErrorMessage() != null) {
                  reportError(result.getErrorMessage());
                } else {
                  // Clean-up imported Pronto file as we're done importing
                  ClientResource clientResource = new ClientResource(irServiceRootRestURL + "ProntoFile/" + prontoFileHandle);
                  clientResource.setOnResponse(new Uniform() {
                    // Even if empty, the onReponse handler is required or call does not go through
                    public void handle(Request request, Response response) {
                    }
                  });
                  clientResource.delete();
  
                  IRFileImportToProtocolForm.this.hide();                
                  wrapper.fireEvent(SubmitEvent.SUBMIT, new SubmitEvent());
                }
              }
              
              protected void reportError(String message) {
                form.unmask();
                info.setText("Error : " + message);
                info.setVisible(true);
                submitBtn.setEnabled(true);
              }
            });
         }
      });
      createFields();
      form.add(info);
      info.setVisible(false);
      add(form);
   }


   /**
    * create the fields for globalcaché and IR Trans
    */
   private void createFields() {
      String hostRegex = "(?:(?:1\\d{0,2}|[3-9]\\d?|2(?:[0-5]{1,2}|\\d)?|0)\\.){3}(?:1\\d{0,2}|[3-9]\\d?|2(?:[0-5]{1,2}|\\d)?|0)|(^(([a-zA-Z]|[a-zA-Z][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z]|[A-Za-z][A-Za-z0-9\\-]*[A-Za-z0-9])$)|(^([0-9a-f]{1,4}:){1,1}(:[0-9a-f]{1,4}){1,6}$)|(^([0-9a-f]{1,4}:){1,2}(:[0-9a-f]{1,4}){1,5}$)|(^([0-9a-f]{1,4}:){1,3}(:[0-9a-f]{1,4}){1,4}$)|(^([0-9a-f]{1,4}:){1,4}(:[0-9a-f]{1,4}){1,3}$)|(^([0-9a-f]{1,4}:){1,5}(:[0-9a-f]{1,4}){1,2}$)|(^([0-9a-f]{1,4}:){1,6}(:[0-9a-f]{1,4}){1,1}$)|(^(([0-9a-f]{1,4}:){1,7}|:):$)|(^:(:[0-9a-f]{1,4}){1,7}$)|(^((([0-9a-f]{1,4}:){6})(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3})$)|(^(([0-9a-f]{1,4}:){5}[0-9a-f]{1,4}:(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3})$)|(^([0-9a-f]{1,4}:){5}:[0-9a-f]{1,4}:(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$)|(^([0-9a-f]{1,4}:){1,1}(:[0-9a-f]{1,4}){1,4}:(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$)|(^([0-9a-f]{1,4}:){1,2}(:[0-9a-f]{1,4}){1,3}:(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$)|(^([0-9a-f]{1,4}:){1,3}(:[0-9a-f]{1,4}){1,2}:(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$)|(^([0-9a-f]{1,4}:){1,4}(:[0-9a-f]{1,4}){1,1}:(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$)|(^(([0-9a-f]{1,4}:){1,5}|:):(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$)|(^:(:[0-9a-f]{1,4}){1,5}:(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$)";
      String hostRegexText = "Must be a valid IPv4 or IPv6 address or a fully qualified domain name";
      final ListBox product = new ListBox();
      product.addItem("Please choose a product");
      product.addItem("GlobalCaché	");
      product.addItem("IRTrans");
      form.add(product);

      // globalCaché panel
      gCPanel = new VerticalPanel();
      gCPanel.setSpacing(10);

      FieldSet gCFieldSet = new FieldSet();
      FormLayout gCLayout = new FormLayout();
      gCLayout.setLabelWidth(80);
      gCFieldSet.setLayout(gCLayout);

      gCip = new TextField<String>();
      gCip.setFieldLabel("IP address / HostName");
      gCip.setAllowBlank(false);
      gCip.setRegex(hostRegex );
      gCip.getMessages().setRegexText(hostRegexText);
      gCFieldSet.add(gCip);
      
      tcpPort = new TextField<String>();
      tcpPort.setValue("4998");
      tcpPort.setAllowBlank(false);
      tcpPort.setMaxLength(5);

      tcpPort.setRegex("\\d+");
      tcpPort.getMessages().setRegexText("Port must be an integer number");

      tcpPort.setFieldLabel("TCP Port");
      gCFieldSet.add(tcpPort);
      connector = new TextField<String>();
      connector.setValue("4:1");
      connector.setRegex("[1-7]:[1-3]");
      connector.getMessages().setRegexText(
            "format must be digit (1-> 7):digit (1-> 3)");
      connector.setFieldLabel("Connector");
      gCFieldSet.add(connector);

      gCPanel.add(gCFieldSet);
      gCPanel.setVisible(false);
      form.add(gCPanel);

      // IRTrans Panel
      iRTransPanel = new VerticalPanel();
      iRTransPanel.setSpacing(10);

      FieldSet iRTransFieldSet = new FieldSet();
      FormLayout iRTransLayout = new FormLayout();
      iRTransLayout.setLabelWidth(80);
      iRTransFieldSet.setLayout(iRTransLayout);
      iRTransIp = new TextField<String>();
      iRTransIp.setFieldLabel(new String("Ip address Host name"));
      iRTransIp.setAllowBlank(false);
      iRTransIp.setRegex(hostRegex );
      iRTransIp.getMessages().setRegexText(hostRegexText);
      iRTransFieldSet.add(iRTransIp);
      udpPort = new TextField<String>();
      udpPort.setValue("21000");
      udpPort.setAllowBlank(false);
      udpPort.setMaxLength(5);
      udpPort.setRegex("\\d+");
      udpPort.getMessages().setRegexText("Port must be an integer number");
      udpPort.setFieldLabel(new String("UDP Port"));
      iRTransFieldSet.add(udpPort);
      iRLed = new ComboBox<IRLed>();
      ListStore<IRLed> irStore = new ListStore<IRLed>();
      irStore.add(new IRLed("Internal", "i"));
      irStore.add(new IRLed("External", "e"));
      irStore.add(new IRLed("Both", "b"));
      iRLed.setStore(irStore);
      iRLed.setFieldLabel("IR Led");
      iRLed.setAllowBlank(false);
      iRLed.setDisplayField("value");
      iRLed.setLazyRender(false);
      iRLed.setValue(new IRLed("Internal", "i"));
      iRLed.setTriggerAction(TriggerAction.ALL);

      iRTransFieldSet.add(iRLed);

      iRTransPanel.add(iRTransFieldSet);
      iRTransPanel.setVisible(false);
      form.add(iRTransPanel);

      product.addChangeHandler(new ChangeHandler() {

         @Override
         public void onChange(ChangeEvent event) {
            info.setVisible(false);
            switch (product.getSelectedIndex()) {
            case 1:
               gCPanel.setEnabled(true);
               gCPanel.setVisible(true);
               iRTransPanel.setEnabled(false);
               iRTransPanel.setVisible(false);
               submitBtn.setEnabled(true);
               break;
            case 2:
               gCPanel.setEnabled(false);
               gCPanel.setVisible(false);
               iRTransPanel.setEnabled(true);
               iRTransPanel.setVisible(true);
               submitBtn.setEnabled(true);
               break;
            default:
               gCPanel.setEnabled(false);
               gCPanel.setVisible(false);
               iRTransPanel.setVisible(false);
               iRTransPanel.setVisible(false);
               submitBtn.setEnabled(false);
               break;
            }
         }
      });
      form.layout();
   }

   /** stores the grid selected items
    * @param selectedItems
    */
   public void setSelectedFunctions(List<BeanModel> selectedItems) {
      this.selectedFunctions = new ArrayList<IRCommandInfo>();
      for (BeanModel bm : selectedItems) {
        selectedFunctions.add((IRCommandInfo) bm.getBean());
      }
   }
}
