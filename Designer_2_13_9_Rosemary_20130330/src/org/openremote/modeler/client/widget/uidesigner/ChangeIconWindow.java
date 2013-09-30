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

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.gxtextends.NestedJsonLoadResultReader;
import org.openremote.modeler.client.proxy.UtilsProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.utils.ImageSourceValidator;
import org.openremote.modeler.client.widget.IconPreviewWidget;
import org.openremote.modeler.client.widget.ImageUploadField;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.DataField;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.data.ScriptTagProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.BoxLayout.BoxLayoutPack;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout.VBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayoutData;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The window is for user to change the component's icon(button, switch, slider, image, etc).
 */
public class ChangeIconWindow extends Dialog {

   public static final String UPLOAD_IMAGE = "uploadImage";
   /** The beehive rest icon url. */
   private static String beehiveRestIconUrl = null;
   
   /** The Constant FROM_BEEHIVE. */
   private static final String FROM_BEEHIVE = "fromBeehive";
   
   /** The Constant FROM_URL. */
   private static final String FROM_URL = "fromURL";
   
   /** The Constant FROM_LOCAL. */
   private static final String FROM_LOCAL = "fromLocal";
   
   /** The radio group. */
   private RadioGroup radioGroup = new RadioGroup();
   
   /** The beehive icons view. */
   private ListView<ModelData> beehiveIconsView;
   
   /** The url panel. */
   private FormPanel urlPanel = new FormPanel();
   
   /** The upload panel. */
   private FormPanel uploadPanel = new FormPanel();
   
   /** The preview icon container. */
   private LayoutContainer previewIconContainer = new LayoutContainer();
   
   /** The image url. */
   private String imageURL;
   
   /** The preview widget. */
   private IconPreviewWidget previewWidget;
   
   /** The window. */
   private ChangeIconWindow window;

   /**
    * Instantiates a new change icon window.
    *
    * @param  previewWidget  the icon preview container.
    * 
    * @param  previewWidth   the preview container width.
    */
   public ChangeIconWindow(IconPreviewWidget previewWidget, int previewWidth) {
      this.previewWidget = previewWidget;
      
      // Make sure we "set image mode, not text", prevents display of weird image if none is set
      previewWidget.setIcon("");      
      window = this;
      if (previewWidth > 90) {
         setMinWidth(400 + previewWidth + 16);
      } else {
         setMinWidth(500);
      }

      initial();
      show();
   }
   
   private void initial() {
      setMinHeight(350);
      setHeading("Add image");
      setModal(true);
      setLayout(new BorderLayout());
      setButtons(Dialog.OKCANCEL);  
      setHideOnButtonClick(true);
      setBodyBorder(false);
      
      addListener(Events.BeforeHide, new Listener<WindowEvent>() {
         public void handleEvent(WindowEvent be) {

            if (be.getButtonClicked() == getButtonById("ok")) {
               setImageURL();

               if (imageURL != null) {
                  if (imageURL.startsWith("http")) {
                     UtilsProxy.downLoadImage(imageURL, new AsyncCallback<String> () {
                        @Override
                        public void onFailure(Throwable caught) {
                          Info.display("Error","Failed to download image from: " + imageURL);
                        }

                        @Override
                        public void onSuccess(String result) {
                           fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(result));
                        }                        
                     });
                  } else {
                    fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(imageURL));
                  }
               } else {
                 hide();
               }
            }
         }
      });

      createRadioContainer();
      
      if (beehiveRestIconUrl == null) {

         UtilsProxy.getBeehiveRestIconUrl(new AsyncSuccessCallback<String>() {

            @Override
            public void onSuccess(String result) {
               beehiveRestIconUrl = result;
               createContentCotainer();
               layout();
            }
         });
      } else {
         createContentCotainer();
      }
      
      BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);  
      centerData.setSplit(true);
      add(previewIconContainer, centerData);
      
   }
   
   /**
    * Creates the radio container.
    */
   private void createRadioContainer() {
      LayoutContainer radioContainer = new LayoutContainer();
      Radio beehiveRadio = new Radio() {

         @Override
         protected void onClick(ComponentEvent be) {
            super.onClick(be);

            if (FROM_BEEHIVE.equals(group.getValue().getValueAttribute())) {
               imageURL = null;
               beehiveIconsView.show();
               beehiveIconsView.getSelectionModel().select(0, false);
               urlPanel.hide();
               uploadPanel.hide();
               previewIconContainer.hide();
               layout();
            }
         }
      };

      beehiveRadio.setBoxLabel("Select from beehive");
      beehiveRadio.setValueAttribute(FROM_BEEHIVE);
      beehiveRadio.setValue(true);
      
      Radio fromURL = new Radio() {

         @Override
         protected void onClick(ComponentEvent be) {
            super.onClick(be);

            if (FROM_URL.equals(group.getValue().getValueAttribute())) {
               imageURL = null;
               urlPanel.show();
               beehiveIconsView.hide();
               uploadPanel.hide();

               if (previewIconContainer.getItemCount() == 0) {
                  initPreviewContainer();
               }

               previewIconContainer.show();
               layout();
            }
         }
      };

      fromURL.setBoxLabel("From a URL");
      fromURL.setValueAttribute(FROM_URL);
      
      Radio uploadIcon = new Radio() {

         @Override
         protected void onClick(ComponentEvent be) {
            super.onClick(be);

            if (FROM_LOCAL.equals(group.getValue().getValueAttribute())) {
               imageURL = null;
               uploadPanel.show();

               if (previewIconContainer.getItemCount() == 0) {
                  initPreviewContainer();
               }

               previewIconContainer.show();
               beehiveIconsView.hide();
               urlPanel.hide();
               layout();
            }
         }
         
      };

      uploadIcon.setValueAttribute(FROM_LOCAL);
      uploadIcon.setBoxLabel("Upload an Image");

      radioGroup.add(beehiveRadio);
      radioGroup.add(fromURL);
      radioGroup.add(uploadIcon);
      radioContainer.add(radioGroup);
      
      BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 25);
      northData.setMargins(new Margins(5, 5, 0, 5));
      
      add(radioContainer, northData);
   }
   
   /**
    * Creates the content cotainer.
    */
   private void createContentCotainer() {
      
      ContentPanel iconContainer = new ContentPanel();
      iconContainer.setLayout(new FitLayout());
      iconContainer.setBorders(true);
      iconContainer.setBodyBorder(false);
      iconContainer.setHeaderVisible(false);
      
      iconContainer.add(createBeehiveIconsView());
      urlPanel.setHeaderVisible(false);
      urlPanel.setBorders(false);
      urlPanel.setBodyBorder(false);
      urlPanel.setHeight(80);
      urlPanel.setButtonAlign(HorizontalAlignment.RIGHT);
      TextField<String> urlField = new TextField<String>();
      urlField.setFieldLabel("URL");
      Button preview = new Button("Preview");
      preview.addSelectionListener(new PreviewListener());
      urlPanel.add(urlField);
      urlPanel.addButton(preview);
      iconContainer.add(urlPanel);
      
      ImageUploadField imageUpload = new ImageUploadField(null) {

         @Override
         protected void onChange(ComponentEvent ce) {
            super.onChange(ce);

            if (!uploadPanel.isValid()) {
               return;
            }

            uploadPanel.submit();
            window.mask("Uploading image...");
         }
      };

      imageUpload.setActionToForm(uploadPanel);
      uploadPanel.setSize(320, 80);
      uploadPanel.setLabelWidth(45);
      uploadPanel.setHeaderVisible(false);
      uploadPanel.setBorders(false);
      uploadPanel.setBodyBorder(false);
      uploadPanel.add(imageUpload);
      uploadPanel.addListener(Events.Submit, new Listener<FormEvent>() {
         public void handleEvent(FormEvent be) {
            imageURL = ImageSourceValidator.validate(be.getResultHtml());
            window.unmask();
            if (imageURL != null) {
               previewWidget.setIcon(imageURL);
               layout();
            } else {
               MessageBox.alert("Error", "Please upload an image.", null);
            }
         }
      });

      iconContainer.add(uploadPanel);
      beehiveIconsView.show();
      beehiveIconsView.getSelectionModel().select(0, false);
      urlPanel.hide();
      uploadPanel.hide();
      
      BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 380, 320, 400);
      westData.setMargins(new Margins(0, 5, 0, 0));
      add(iconContainer, westData);
   }

   /**
    * Inits the preview container.
    */
   private void initPreviewContainer() {
      VBoxLayout layout = new VBoxLayout();
      layout.setPadding(new Padding(5));
      layout.setVBoxLayoutAlign(VBoxLayoutAlign.CENTER);
      layout.setPack(BoxLayoutPack.CENTER);
      previewIconContainer.setLayout(layout);
      previewIconContainer.setBorders(true);
      previewIconContainer.setStyleAttribute("backgroundColor", "white");
      previewIconContainer.add(previewWidget, new VBoxLayoutData(new Margins(0, 0, 5, 0)));
   }
   
   /**
    * Creates the beehive icons view.
    * 
    * @return the list view< model data>
    */
   private ListView<ModelData> createBeehiveIconsView() {
      ModelType iconType = new ModelType();
      iconType.setRoot("icons.icon");
      DataField idField = new DataField("id");
      idField.setType(Long.class);
      iconType.addField(idField);
      iconType.addField("fileName");
      iconType.addField("name");
      
      ScriptTagProxy<ListLoadResult<ModelData>> scriptTagProxy = new ScriptTagProxy<ListLoadResult<ModelData>>(beehiveRestIconUrl);
      NestedJsonLoadResultReader<ListLoadResult<ModelData>> reader = new NestedJsonLoadResultReader<ListLoadResult<ModelData>>(
            iconType);
      final BaseListLoader<ListLoadResult<ModelData>> loader = new BaseListLoader<ListLoadResult<ModelData>>(scriptTagProxy, reader);

      ListStore<ModelData> store = new ListStore<ModelData>(loader);
      loader.load();
     
      beehiveIconsView = new ListView<ModelData>();
      beehiveIconsView.setId("img-chooser-view");
      beehiveIconsView.setTemplate(getTemplate());
      beehiveIconsView.setStore(store);
      beehiveIconsView.setBorders(false);
      beehiveIconsView.setLoadingText("Loading icons...");
      beehiveIconsView.setItemSelector("div.thumb-wrap");
      return beehiveIconsView;
   }
   
   /**
    * Gets the template.
    * 
    * @return the template
    */
   private native String getTemplate() /*-{ 
        return ['<tpl for=".">', 
        '<div class="thumb-wrap" id="{name}" style="border: 1px solid white">', 
        '<div class="thumb"><img src="{fileName}" title="{name}"></div></div>', 
        '</tpl>', 
        '<div class="x-clear"></div>'].join(""); 
         
        }-*/; 
   
   /**
    * Sets the image url.
    */
   @SuppressWarnings("unchecked")
   private void setImageURL() {
      String radioValue = radioGroup.getValue().getValueAttribute();
      if (FROM_BEEHIVE.equals(radioValue)) {
         imageURL = beehiveIconsView.getSelectionModel().getSelectedItem().get("fileName").toString();
      } else if (FROM_URL.equals(radioValue)) {
         imageURL = ((TextField<String>) urlPanel.getItem(0)).getValue();
      }
   }
   
   private final class PreviewListener extends SelectionListener<ButtonEvent> {
      @SuppressWarnings("unchecked")
      @Override
      public void componentSelected(ButtonEvent ce) {
         imageURL = ((TextField<String>) urlPanel.getItem(0)).getValue();
         if (imageURL != null) {
            previewWidget.setIcon(imageURL);
            layout();
         } else {
            MessageBox.alert("Error", "Please input an image URL.", null);
         }
      }
   }
}
