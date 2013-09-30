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
package org.openremote.modeler.client.widget.buildingmodeler;

import java.util.ArrayList;
import java.util.List;

import net.customware.gwt.dispatch.client.DispatchAsync;

import org.openremote.modeler.client.ModelerGinjector;
import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.knx.KNXImportResultOverlay;
import org.openremote.modeler.client.utils.AutoCommitCheckColumnConfig;
import org.openremote.modeler.client.utils.NoButtonsRowEditor;
import org.openremote.modeler.client.widget.FormWindow;
import org.openremote.modeler.selenium.DebugId;
import org.openremote.modeler.shared.dto.DeviceDTO;
import org.openremote.modeler.shared.knx.ImportKNXConfigAction;
import org.openremote.modeler.shared.knx.ImportKNXConfigResult;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.JsonLoadResultReader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.MemoryProxy;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author marcus@openremote.org
 */
public class KNXImportWindow extends FormWindow {

    private DeviceDTO device;
    private Button importBtn;
    private final KNXImportWindow importWindow;
    private MemoryProxy<String> proxy;
    private BaseListLoader<ListLoadResult<ModelData>> loader;
    private FileUploadField fileUploadField;
    private ListStore<ModelData> store;

    /**
     * Instantiates a new import window.
     */
    public KNXImportWindow(BeanModel deviceBeanModel) {
        super();
        importWindow = this;
        setSize(800, 600);
        initial("Import ETS4 project or ETS3 group export CSV file");
        this.ensureDebugId(DebugId.IMPORT_WINDOW);
        this.device = (DeviceDTO) deviceBeanModel.getBean();
        show();
    }

    /**
     * Initial.
     * 
     * @param heading
     *            the heading
     */
    private void initial(String heading) {
        setHeading(heading);
        form.setAction(GWT.getModuleBaseURL() + "fileUploadController.htm?method=importETS4");
        form.setEncoding(Encoding.MULTIPART);
        form.setMethod(Method.POST);

        createFileUploadField();
        createLoadResetButton();
        createResultGrid();
        createWindowButtons();

        addListenersToForm();
        add(form);
    }

    /**
     * Creates the fields.
     */
    private void createResultGrid() {
        // defines the xml structure
        ModelType type = new ModelType();
        type.setRoot("records");
        type.addField("GroupAddress", "groupAddress");
        type.addField("Name", "name");
        type.addField("DPT", "dpt");
        type.addField("commandType", "command");
        type.addField("import", "importGA");
        type.addField("SceneNumber", "SceneNumber");

        // need a loader, proxy, and reader
        proxy = new MemoryProxy<String>(null);
        JsonLoadResultReader<ListLoadResult<ModelData>> reader = new JsonLoadResultReader<ListLoadResult<ModelData>>(
                type);
        loader = new BaseListLoader<ListLoadResult<ModelData>>(proxy, reader);
        store = new ListStore<ModelData>(loader);
        store.addStoreListener(new StoreListener<ModelData>() {

            @SuppressWarnings("unchecked")
            @Override
            public void storeUpdate(StoreEvent<ModelData> se) {
                List<ModelData> data = (List<ModelData>) se.getStore().getModels();
                boolean enable = false;
                if (data != null) {
                    for (ModelData modelData : data) {
                        if (modelData.get("import")) {
                            enable = true;
                            break;
                        }
                    }
                }
                importBtn.setEnabled(enable);
            }

        });

        final NoButtonsRowEditor<ModelData> re = new NoButtonsRowEditor<ModelData>(store);

        // create the column model
        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

        // Col1
        columns.add(new ColumnConfig("GroupAddress", "GroupAddress", 100));

        // Col2
        columns.add(new ColumnConfig("Name", "Name", 165));

        // Col3
        columns.add(new ColumnConfig("DPT", "DPT", 100));

        // Col4
        final SimpleComboBox<String> combo = new SimpleComboBox<String>();
        combo.setForceSelection(true);
        combo.setEditable(false);
        combo.setTriggerAction(TriggerAction.ALL);
        combo.add(new ArrayList<String>(ImportKNXConfigAction.COMMAND_DPT_MAP.keySet()));

        final CellEditor editor = new CellEditor(combo) {
            @Override
            public Object preProcessValue(Object value) {
                if (value == null) {
                    return value;
                }
                return combo.findModel(value.toString());
            }

            @Override
            public Object postProcessValue(Object value) {
                if (value == null) {
                    return value;
                }
                return ((ModelData) value).get("value");
            }
        };
        combo.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {
            @Override
            public void selectionChanged(final SelectionChangedEvent<SimpleComboValue<String>> se) {
                if (combo.isRendered()) {
                    if ((se.getSelectedItem() != null) && (se.getSelectedItem().getValue().indexOf("Scene") != -1)) {
                        final MessageBox box = MessageBox.prompt("Scene Selection", "Please enter scene number (1-64):");  
                        box.addCallback(new Listener<MessageBoxEvent>() {  
                          @SuppressWarnings("unchecked")
                        public void handleEvent(MessageBoxEvent be) {
                              if (be.getButtonClicked().getItemId().equals(Dialog.OK)) {
                                  NoButtonsRowEditor<ModelData> a = (NoButtonsRowEditor<ModelData>) ((SimpleComboBox<String>)se.getSource()).getParent();
                                  a.getStore().getAt(a.getRowIndex()).set("SceneNumber", be.getValue());
                              } else {
                                  se.setCancelled(true);
                              }
                          }  
                        }); 
                    }
                    re.stopEditing(true);
                }
            }
        });

        ColumnConfig column = new ColumnConfig();
        column = new ColumnConfig();
        column.setId("commandType");
        column.setHeader("Command type");
        column.setWidth(130);
        column.setEditor(editor);
        columns.add(column);

        // Col5
        AutoCommitCheckColumnConfig checkColumn = new AutoCommitCheckColumnConfig("import", "Import?", 55);
        CheckBox cb = new CheckBox();
        CellEditor checkBoxEditor = new CellEditor(cb);
        checkColumn.setEditor(checkBoxEditor);
        columns.add(checkColumn);

        ColumnModel cm = new ColumnModel(columns);

        // Create the grid
        final Grid<ModelData> grid = new Grid<ModelData>(store, cm);
        grid.addPlugin(checkColumn);
        grid.addPlugin(re);
        grid.setBorders(true);
        grid.setLoadMask(true);
        grid.getView().setEmptyText("Please hit the load button.");
        grid.setAutoExpandColumn("Name");

        ContentPanel panel = new ContentPanel();
        panel.setFrame(true);
        panel.setCollapsible(false);
        panel.setButtonAlign(HorizontalAlignment.CENTER);
        panel.setHeading("Available group addresses");
        panel.setLayout(new FitLayout());
        panel.add(grid);
        panel.setSize(750, 480);
        form.add(panel);
    }

    /**
     * Creates the fields.
     */
    private void createFileUploadField() {
        fileUploadField = new FileUploadField();
        fileUploadField.setName("file");
        fileUploadField.setAllowBlank(false);
        fileUploadField.setFieldLabel("File");
        fileUploadField.setStyleAttribute("overflow", "hidden");
        form.add(fileUploadField);
    }

    /**
     * Creates the load button
     */
    private void createLoadResetButton() {
        Button loadBtn = new Button("Load");
        loadBtn.ensureDebugId(DebugId.KNX_IMPORT_WINDOW_LOAD_BTN);

        loadBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                if (form.isValid()) {
                    form.submit();
                }
            }
        });
        form.addButton(loadBtn);

        Button resetBtn = new Button("Clear");
        resetBtn.ensureDebugId(DebugId.KNX_IMPORT_WINDOW_CLEAR_BTN);
        resetBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                proxy.setData("{\"records\":[]}");
                loader.load();
                fileUploadField.clear();
            }
        });
        form.addButton(resetBtn);
    }

    /**
     * Creates the buttons.
     */
    private void createWindowButtons() {
        importBtn = new Button("OK");
        importBtn.ensureDebugId(DebugId.KNX_IMPORT_WINDOW_OK_BTN);
        importBtn.setEnabled(false);
        importBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                importSelectedGridData();
            }
        });

        Button cancelBtn = new Button("Cancel");
        cancelBtn.ensureDebugId(DebugId.KNX_IMPORT_WINDOW_CANCEL_BTN);
        cancelBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                importWindow.hide();
            }
        });
        form.addButton(importBtn);
        form.addButton(cancelBtn);
    }

    private void importSelectedGridData() {
        List<ModelData> data = (List<ModelData>) store.getModels();
        ArrayList<ModelData> importData = new ArrayList<ModelData>();

        for (ModelData modelData : data) {
            if (modelData.get("import")) {
                if ("N/A".equals(modelData.get("commandType"))) {
                    MessageBox.alert("Alert", "Each GroupAddress which should be imported needs a command type.", null);
                    return;
                } else {
                    importData.add(modelData);
                }
            }
        }
        
        ModelerGinjector injector = GWT.create(ModelerGinjector.class);
        DispatchAsync dispatcher = injector.getDispatchAsync();
        ImportKNXConfigAction action = new ImportKNXConfigAction(device, importData);
        dispatcher.execute(action, new AsyncCallback<ImportKNXConfigResult>() {

          @Override
          public void onFailure(Throwable caught) {
            Info.display("ERROR", caught.getMessage());
            
            // TODO: better error reporting
          }

          @Override
          public void onSuccess(ImportKNXConfigResult result) {
            fireEvent(SubmitEvent.SUBMIT, new SubmitEvent());
          }
        });
    }
    
    /**
     * Adds the listeners to form.
     */
    private void addListenersToForm() {
        form.addListener(Events.Submit, new Listener<FormEvent>() {
            public void handleEvent(FormEvent be) {
              KNXImportResultOverlay importResult = KNXImportResultOverlay.fromJSONString(be.getResultHtml());
              if (importResult.getException() != null) {
                MessageBox.alert("Import Error", importResult.getException(), null);
              } else {
                proxy.setData(be.getResultHtml());
                loader.load();
              }
            }
        });
    }

}
