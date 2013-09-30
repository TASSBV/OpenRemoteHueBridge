package org.openremote.modeler.client.widget.uidesigner;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.icon.IconResources;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.proxy.UtilsProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.IconPreviewWidget;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.component.ImageSource;
import org.openremote.modeler.domain.component.UITabbarItem;
import org.openremote.modeler.shared.GraphicalAssetDTO;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Info;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class ImageAssetPicker extends DialogBox {

  private static ImageAssetPickerUiBinder uiBinder = GWT.create(ImageAssetPickerUiBinder.class);

  interface ImageAssetPickerUiBinder extends UiBinder<Widget, ImageAssetPicker> {
  }
  
  public interface ImageAssetPickerListener {
    void imagePicked(String imageURL);
  }

  @UiFactory
  DialogBox itself() {
    return this;
  }

  private final SingleSelectionModel<GraphicalAssetDTO> selectionModel = new SingleSelectionModel<GraphicalAssetDTO>();
  
  private List<GraphicalAssetDTO> assets;
  
  private ImageAssetPickerListener listener;
  
  public void setListener(ImageAssetPickerListener listener) {
    this.listener = listener;
  }

  public ImageAssetPicker(final String currentImageURL) {
    uiBinder.createAndBindUi(this);
    mainLayout.setSize("50em", "20em");
    okButton.setEnabled(false);
    
    getElement().getStyle().setZIndex(Integer.MAX_VALUE - 1); // TODO: check how we can be sure of the value to use

    
    // TODO:
    
    // Recheck behaviour when moving with keyboard
    // Difference between line focus (yellow) and selection (blue)
    // Line focus should trigger preview
    
    
    Column<GraphicalAssetDTO, String> column = new Column<GraphicalAssetDTO, String>(new ButtonCell() {
      @Override
      public void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
        if (data != null) {
          ImageResource icon = IconResources.INSTANCE.delete();
          SafeHtml html = SafeHtmlUtils.fromTrustedString(AbstractImagePrototype.create(icon).getHTML());
          sb.append(html);
        }
      }
    }) {
      @Override
      public String getValue(GraphicalAssetDTO object) {
        return "";
      }
    };
    column.setFieldUpdater(new FieldUpdater<GraphicalAssetDTO, String>() {
      @Override
      public void update(int index, final GraphicalAssetDTO object, String value) {        
        if (isImageInUse(object.getName())) {
          // TODO : display proper alert
          Info.display("INFO", "Delete not allowed");
        } else {
          
          UtilsProxy.deleteImage(object.getName(), new AsyncCallback<Void>() {

            @Override
            public void onSuccess(Void result) {
              assets.remove(object);
              if (selectionModel.getSelectedObject() != null) {
                selectionModel.setSelected(selectionModel.getSelectedObject(), false);
                imagePreview.setVisible(false);
              }
              table.setRowData(assets);
            }

            @Override
            public void onFailure(Throwable caught) {
              // TODO
            }
            
          });
        }
      }
    });
    table.addColumn(column);
    
    TextColumn<GraphicalAssetDTO> fileNameColumn = new TextColumn<GraphicalAssetDTO>() {
      @Override
      public String getValue(GraphicalAssetDTO asset) {
        return asset.getName();
      }
    };
    table.addColumn(fileNameColumn, "Name");
    table.setSelectionModel(selectionModel);
    
    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {      
      @Override
      public void onSelectionChange(SelectionChangeEvent event) {
        okButton.setEnabled(selectionModel.getSelectedObject() != null);
        imagePreview.setUrl(selectionModel.getSelectedObject().getUrl());
        imagePreview.setVisible(true);
      }
    });
    
    UtilsProxy.getUserImagesURLs(new AsyncSuccessCallback<List<GraphicalAssetDTO>>() {
      @Override
      public void onSuccess(List<GraphicalAssetDTO> result) {
        assets = result;
        sortAssets();
        table.setRowData(result);
        selectImage(currentImageURL, result);
      }

    });
  }

  @UiField
  DockLayoutPanel mainLayout;

  @UiField
  CellTable<GraphicalAssetDTO> table;
  
  @UiField
  Image imagePreview;
  
  @UiField
  Button cancelButton;
  
  @UiField
  Button okButton;
  
  @UiField
  Button addButton;
  
  @UiHandler("addButton")
  void handleAdd(ClickEvent e) {
    ChangeIconWindow selectImageONWindow = new ChangeIconWindow(new IconPreviewWidget(100, 100), 100);
    getElement().getStyle().setZIndex(Integer.parseInt(selectImageONWindow.getElement().getStyle().getZIndex()) - 1);
    setModal(false);
    
    selectImageONWindow.addListener(Events.Hide, new Listener<BaseEvent>() {
      @Override
      public void handleEvent(BaseEvent be) {
        setModal(true);
      }
    });
    
    selectImageONWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
       @Override
       public void afterSubmit(SubmitEvent be) {
         final String imageUrl = be.getData();
         assets.add(new GraphicalAssetDTO(imageUrl.substring(imageUrl.lastIndexOf("/") + 1), imageUrl));
         sortAssets();
         table.setRowData(assets);
         selectImage(imageUrl, assets);
       }
    });

  }

  @UiHandler("cancelButton")
  void handleCancel(ClickEvent e) {
    hide();
  }

  @UiHandler("okButton")
  void handleOK(ClickEvent e) {
    if (listener != null) {
      listener.imagePicked(selectionModel.getSelectedObject().getUrl());
    }
    hide();
  }

  private void selectImage(final String currentImageURL, List<GraphicalAssetDTO> result) {
    for (GraphicalAssetDTO ga : result) {
      if (ga.getUrl().equals(currentImageURL) || ga.getName().equals(currentImageURL)) { // TODO: testing also on image name, not nice but used for states on image widgets
        selectionModel.setSelected(ga, true);
        break;
      }
    }
  }
  
  private boolean isImageInUse(String imageName) {
    for (BeanModel panelBeanModel : BeanModelDataBase.panelTable.loadAll()) {
      Panel p = panelBeanModel.getBean();
      for (Group g : p.getGroups()) {
        for (Screen s : g.getLandscapeScreens()) {
          if (isImageUsedInScreen(imageName, s)) {
            return true;
          }
        }
        for (Screen s : g.getPortraitScreens()) {
          if (isImageUsedInScreen(imageName, s)) {
            return true;
          }
        }
        if (isImageUsedByTabBarItems(imageName, g.getTabbarItems())) {
          return true;
        }
      }
      if (isImageUsedByTabBarItems(imageName, p.getTabbarItems())) {
        return true;
      }
    }
    return false;
  }

  private boolean isImageUsedByTabBarItems(String imageName, Collection<UITabbarItem> items) {
    for (UITabbarItem tbi : items) {
      if (imageName.equals(tbi.getImage().getImageFileName())) {
        return true;
      }
    }
    return false;
  }

  private boolean isImageUsedInScreen(String imageName, Screen s) {
    if (imageName.equals(s.getBackground().getImageSource().getImageFileName())) {
      return true;
    }
    for (ImageSource src : s.getAllImageSources()) {
      if (imageName.equals(src.getImageFileName())) {
        return true;
      }
    }
    return false;
  }
  
  private void sortAssets() {
    Collections.sort(assets, new Comparator<GraphicalAssetDTO>() {
      @Override
      public int compare(GraphicalAssetDTO o1, GraphicalAssetDTO o2) {
        return o1.getName().compareToIgnoreCase(o2.getName());
      }           
     });
  }
}