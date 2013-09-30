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

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.gxtextends.ListViewDropTargetMacroDragExt;
import org.openremote.modeler.client.gxtextends.SelectionServiceExt;
import org.openremote.modeler.client.gxtextends.SourceSelectionChangeListenerExt;
import org.openremote.modeler.client.gxtextends.TreePanelDragSourceMacroDragExt;
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.listener.ConfirmDeleteListener;
import org.openremote.modeler.client.listener.EditDelBtnSelectionListener;
import org.openremote.modeler.client.listener.FormSubmitListener;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.proxy.DeviceMacroBeanModelProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.FormWindow;
import org.openremote.modeler.client.widget.TreePanelBuilder;
import org.openremote.modeler.selenium.DebugId;
import org.openremote.modeler.shared.dto.DTOHelper;
import org.openremote.modeler.shared.dto.DeviceCommandDTO;
import org.openremote.modeler.shared.dto.MacroDTO;
import org.openremote.modeler.shared.dto.MacroDetailsDTO;
import org.openremote.modeler.shared.dto.MacroItemDetailsDTO;
import org.openremote.modeler.shared.dto.MacroItemType;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.dnd.DND.Operation;
import com.extjs.gxt.ui.client.dnd.ListViewDragSource;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;

/**
 * The window to creates or updates a macro.
 */
public class MacroWindow extends FormWindow {

  private MacroDTO macro;
  private MacroDetailsDTO macroDetails;

  private boolean edit;

  /** The Constant MACRO_DND_GROUP. */
  private static final String MACRO_DND_GROUP = "macro";

  /** The icons. */
  private Icons icons = GWT.create(Icons.class);

  /** The macro name field. */
  private TextField<String> macroNameField = null;

  /** The add macro item container. */
  private LayoutContainer addMacroItemContainer;

  /** The device command tree. */
  private TreePanel<BeanModel> deviceCommandTree = null;

  /** The left macro list. */
  private TreePanel<BeanModel> leftMacroList = null;

  /** The right macro item list view. */
  private ListView<BeanModel> rightMacroItemListView = null;

  /** The selection service. */
  private SelectionServiceExt<BeanModel> selectionService;

  /**
   * Instantiates a macro window to create a new macro.
   */
  public MacroWindow() {
    setHeading("New Macro");
    setup();
    macroDetails = new MacroDetailsDTO();
    show();
  }

  /**
   * Instantiates a macro window to edit a macro.
   * 
   * @param deviceMacroModel the device macro
   */
  public MacroWindow(MacroDTO macro) {
    this.macro = macro;
    setHeading("Edit Macro");
    edit = true;
    
    DeviceMacroBeanModelProxy.loadMacroDetails(macro, new AsyncSuccessCallback<BeanModel>() {
      public void onSuccess(BeanModel result) {
        MacroWindow.this.macroDetails = result.getBean();
        setup();
        layout();
      }
    });
  }

  /**
   * Sets the window style and initializes the form.
   */
  private void setup() {
    selectionService = new SelectionServiceExt<BeanModel>();
    setPlain(true);
    setBlinkModal(true);
    setWidth(530);
    setHeight(460);
    setResizable(false);
    createFormElement();

    form.setLabelAlign(LabelAlign.TOP);
    form.setHeight(400);
    form.addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
      public void handleEvent(FormEvent be) {
        beforeFormSubmit();
      }

    });
    add(form);
  }

  /**
   * Creates the form element.
   */
  private void createFormElement() {
    macroNameField = new TextField<String>();
    
    if (edit) {
      macroNameField.setValue(macroDetails.getName());
    }
    macroNameField.setAllowBlank(false);
    macroNameField.setFieldLabel("Macro Name");
    macroNameField.setName("macroName");
    macroNameField.setStyleAttribute("marginBottom", "10px");
    macroNameField.ensureDebugId(DebugId.DEVICE_MACRO_NAME_FIELD);
    form.add(macroNameField);

    createSelectCommandContainer();

    Button submitBtn = new Button("OK");
    submitBtn.addSelectionListener(new FormSubmitListener(form, submitBtn));

    form.addButton(submitBtn);
  }

  /**
   * Creates the select command container.
   */
  private void createSelectCommandContainer() {
    addMacroItemContainer = new LayoutContainer();
    FieldSet fieldSet = new FieldSet();

    AdapterField adapterField = new AdapterField(addMacroItemContainer);
    adapterField.setAutoWidth(true);
    fieldSet.add(adapterField);
    fieldSet.setHeading("Add Macro Item(drag from left to right)");

    form.add(fieldSet);

    HBoxLayout layout = new HBoxLayout();
    layout.setHBoxLayoutAlign(HBoxLayoutAlign.TOP);
    addMacroItemContainer.setLayout(layout);
    addMacroItemContainer.setHeight(280);

    createLeftCommandMacroTab();
    createRightMacroList();
  }

  /**
   * Creates the left command macro tab.
   */
  private void createLeftCommandMacroTab() {
    TabPanel leftCommandMacroTabPanel = new TabPanel();
    leftCommandMacroTabPanel.setWidth(220);
    leftCommandMacroTabPanel.setPlain(true);
    leftCommandMacroTabPanel.setHeight(275);

    TabItem deviceCommandTab = new TabItem("Device Command");
    deviceCommandTab.setLayout(new FitLayout());

    deviceCommandTab.add(createDeviceCommandTree());
    leftCommandMacroTabPanel.add(deviceCommandTab);
    deviceCommandTab.scrollIntoView(leftCommandMacroTabPanel);

    TabItem macroTab = new TabItem("Macro");
    macroTab.setLayout(new FitLayout());
    macroTab.add(createLeftMacroTree());
    leftCommandMacroTabPanel.add(macroTab);

    addMacroItemContainer.add(leftCommandMacroTabPanel);
  }

  /**
   * Creates the device command tree.
   * 
   * @return the layout container
   */
  private LayoutContainer createDeviceCommandTree() {
    LayoutContainer treeContainer = new LayoutContainer();
    // overflow-auto style is for IE hack.
    treeContainer.addStyleName("overflow-auto");
    treeContainer.setStyleAttribute("backgroundColor", "white");
    treeContainer.setBorders(false);

    deviceCommandTree = TreePanelBuilder.buildDeviceCommandTree();
    deviceCommandTree.setHeight("100%");

    TreePanelDragSourceMacroDragExt dragSource = new TreePanelDragSourceMacroDragExt(deviceCommandTree);
    dragSource.addDNDListener(new DNDListener() {
      @SuppressWarnings("unchecked")
      @Override
      public void dragStart(DNDEvent e) {
        TreePanel<BeanModel> tree = (TreePanel<BeanModel>) e.getComponent();
        BeanModel beanModel = tree.getSelectionModel().getSelectedItem();
        if (!(beanModel.getBean() instanceof DeviceCommandDTO)) {
          e.setCancelled(true);
          e.getStatus().setStatus(false);
        }
        super.dragStart(e);
      }

    });
    dragSource.setGroup(MACRO_DND_GROUP);

    treeContainer.add(deviceCommandTree);

    return treeContainer;
  }

  /**
   * Creates the left macro list.
   * 
   * @return the layout container
   */
  private LayoutContainer createLeftMacroTree() {
    LayoutContainer leftMacroListContainer = new LayoutContainer();
    // overflow-auto style is for IE hack.
    leftMacroListContainer.addStyleName("overflow-auto");
    leftMacroListContainer.setStyleAttribute("backgroundColor", "white");
    leftMacroListContainer.setBorders(false);

    leftMacroList = TreePanelBuilder.buildMacroTree();
    leftMacroListContainer.setHeight("100%");
    leftMacroListContainer.add(leftMacroList);

    TreePanelDragSourceMacroDragExt dragSource = new TreePanelDragSourceMacroDragExt(leftMacroList);
    dragSource.addDNDListener(new DNDListener() {

      @SuppressWarnings("unchecked")
      @Override
      public void dragStart(DNDEvent e) {
        TreePanel<BeanModel> tree = ((TreePanel<BeanModel>) e.getComponent());
        BeanModel beanModel = tree.getSelectionModel().getSelectedItem();
        if (!(beanModel.getBean() instanceof MacroDTO)) {
          e.setCancelled(true);
          e.getStatus().setStatus(false);
        } else if (((MacroDTO)beanModel.getBean()).getOid() == macroDetails.getOid()) { // when edit macro, can not dnd oneself.
          e.setCancelled(true);
          e.getStatus().setStatus(false);
        }
        super.dragStart(e);
      }

    });
    dragSource.setGroup(MACRO_DND_GROUP);

    return leftMacroListContainer;
  }

  /**
   * Creates the right macro list.
   */
  private void createRightMacroList() {
    ContentPanel rightListContainer = new ContentPanel();
    rightListContainer.setHeaderVisible(false);
    rightListContainer.setWidth(230);
    rightListContainer.setHeight(275);
    rightListContainer.setLayout(new FitLayout());

    ToolBar toolBar = createRightMacroItemListToolbar();

    rightListContainer.setTopComponent(toolBar);

    rightMacroItemListView = createRightMacroItemListView();

    selectionService.addListener(new SourceSelectionChangeListenerExt(rightMacroItemListView.getSelectionModel()));
    selectionService.register(rightMacroItemListView.getSelectionModel());
    setupRightMacroItemDND();

    rightListContainer.add(rightMacroItemListView);
    HBoxLayoutData flex = new HBoxLayoutData(new Margins(0, 5, 0, 0));
    flex.setFlex(1);
    addMacroItemContainer.add(new Text(), flex);
    addMacroItemContainer.add(rightListContainer);
  }

  /**
   * Creates the right macro item list toolbar.
   * 
   * @return the tool bar
   */
  private ToolBar createRightMacroItemListToolbar() {
    ToolBar toolBar = new ToolBar();
    List<Button> editDelBtns = new ArrayList<Button>();

    Button addDelayBtn = new Button();
    addDelayBtn.setToolTip("Add Delay");
    addDelayBtn.setIcon(icons.addDelayIcon());
    addDelayBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
      @Override
      public void componentSelected(ButtonEvent ce) {
        addDelay();
      }

    });
    toolBar.add(addDelayBtn);

    Button editDelayBtn = new Button();
    editDelayBtn.setEnabled(false);
    editDelayBtn.setToolTip("Edit Delay");
    editDelayBtn.setIcon(icons.editDelayIcon());
    editDelayBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
      @Override
      public void componentSelected(ButtonEvent ce) {
        editDelay();
      }
    });
    toolBar.add(editDelayBtn);
    editDelBtns.add(editDelayBtn);

    Button deleteBtn = new Button();
    deleteBtn.setEnabled(false);
    deleteBtn.setToolTip("Delete Macro Item");
    deleteBtn.setIcon(icons.delete());
    deleteBtn.addSelectionListener(new ConfirmDeleteListener<ButtonEvent>() {

      @Override
      public void onDelete(ButtonEvent ce) {
        onDeleteMacroItemBtnClicked();
      }

    });
    toolBar.add(deleteBtn);
    editDelBtns.add(deleteBtn);
    selectionService.addListener(new EditDelBtnSelectionListener(editDelBtns));
    return toolBar;
  }

  /**
   * Setup right macro item dnd.
   */
  private void setupRightMacroItemDND() {
    ListViewDropTargetMacroDragExt dropTarget = new ListViewDropTargetMacroDragExt(rightMacroItemListView);
    dropTarget.setAllowSelfAsSource(true);
    dropTarget.setGroup(MACRO_DND_GROUP);
    dropTarget.setFeedback(Feedback.INSERT);
    dropTarget.setOperation(Operation.MOVE);

    ListViewDragSource dragSource = new ListViewDragSource(rightMacroItemListView);
    dragSource.setGroup(MACRO_DND_GROUP);
  }

  /**
   * Setup right macro item list view.
   * 
   * @return the list view< bean model>
   */
  private ListView<BeanModel> createRightMacroItemListView() {
    rightMacroItemListView = new ListView<BeanModel>();
    rightMacroItemListView.setDisplayProperty("displayName");

    ListStore<BeanModel> store = new ListStore<BeanModel>();

    rightMacroItemListView.setStore(store);
    rightMacroItemListView.setHeight(203);
    if (macroDetails != null) {
      for (MacroItemDetailsDTO item : macroDetails.getItems()) {
        rightMacroItemListView.getStore().add(DTOHelper.getBeanModel(item));
      }
    }
    return rightMacroItemListView;
  }

  /**
   * Before form submit.
   */
  private void beforeFormSubmit() {
    AsyncSuccessCallback<MacroDTO> callback = new AsyncSuccessCallback<MacroDTO>() {
      @Override
      public void onSuccess(MacroDTO result) {
        if (macro == null) {
          fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(result));
        } else {
          macro.setDisplayName(result.getDisplayName());
          macro.setItems(result.getItems());
          fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(macro));
        }
      }
    };

    macroDetails.setName(macroNameField.getValue());
    ArrayList<MacroItemDetailsDTO> items = new ArrayList<MacroItemDetailsDTO>();
    for (BeanModel bm : rightMacroItemListView.getStore().getModels()) {
      items.add((MacroItemDetailsDTO) bm.getBean());
    }
    macroDetails.setItems(items);
    if (edit) {
      DeviceMacroBeanModelProxy.updateMacroWithDTO(macroDetails, callback);
    } else {
      DeviceMacroBeanModelProxy.saveNewMacro(macroDetails, callback);
    }

  }

  /**
   * On delete macro item btn clicked.
   */
  private void onDeleteMacroItemBtnClicked() {
    for (BeanModel data : rightMacroItemListView.getSelectionModel().getSelectedItems()) {
      int index = rightMacroItemListView.getStore().indexOf(data);
      rightMacroItemListView.getStore().remove(data);
      if (rightMacroItemListView.getStore().getCount() > 0) {
        rightMacroItemListView.getSelectionModel().select(index, false);
      }
    }
  }

  /**
   * Adds the delay.
   */
  private void addDelay() {
    final DelayWindow delayWindow = new DelayWindow();
    delayWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
      @Override
      public void afterSubmit(SubmitEvent be) {
        delayWindow.hide();
        BeanModel delayModel = be.getData();
        rightMacroItemListView.getStore().add(delayModel);
        rightMacroItemListView.getSelectionModel().select(delayModel, false);
      }
    });
    delayWindow.show();
  }

  /**
   * Edits the delay.
   */
  private void editDelay() {
    BeanModel data = rightMacroItemListView.getSelectionModel().getSelectedItem();
    if (data.getBean() instanceof MacroItemDetailsDTO && ((MacroItemDetailsDTO) data.getBean()).getType() == MacroItemType.Delay) {
      final DelayWindow editDelayWindow = new DelayWindow((MacroItemDetailsDTO) data.getBean());
      editDelayWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
        @Override
        public void afterSubmit(SubmitEvent be) {
          editDelayWindow.hide();
          BeanModel delayModel = be.getData();
          rightMacroItemListView.getStore().update(delayModel);
        }
      });
      editDelayWindow.show();
    } else {
      MessageBox.info("Warn", "Please select a delay", null);
    }
  }
}