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
package org.openremote.modeler.client.widget.propertyform;

import org.openremote.modeler.client.event.AbsoluteBoundsEvent;
import org.openremote.modeler.client.event.WidgetDeleteEvent;
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.listener.AbsoluteBoundsListener;
import org.openremote.modeler.client.model.ORBounds;
import org.openremote.modeler.client.utils.AbsoluteBoundsListenerManager;
import org.openremote.modeler.client.utils.PropertyEditable;
import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.client.widget.component.ScreenTabbar;
import org.openremote.modeler.client.widget.component.ScreenTabbarItem;
import org.openremote.modeler.client.widget.uidesigner.AbsoluteLayoutContainer;
import org.openremote.modeler.client.widget.uidesigner.ComponentContainer;
import org.openremote.modeler.client.widget.uidesigner.GridLayoutContainerHandle;
import org.openremote.modeler.domain.Absolute;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;

/**
 * The PropertyForm initialize the property form display style.
 */
public class PropertyForm extends FormPanel {
   private ComponentContainer componentContainer;
   protected WidgetSelectionUtil widgetSelectionUtil;

   public PropertyForm(ComponentContainer componentContainer, WidgetSelectionUtil widgetSelectionUtil) {
      this.componentContainer = componentContainer;
      this.widgetSelectionUtil = widgetSelectionUtil;
      setFrame(true);
      setHeaderVisible(false);
      setBorders(false);
      setBodyBorder(false);
      setPadding(2);
      setLabelWidth(90);
      setFieldWidth(150);
      setScrollMode(Scroll.AUTO);
      LayoutContainer layoutContainer = (LayoutContainer)componentContainer.getParent();
      if (layoutContainer instanceof AbsoluteLayoutContainer) {
         addAbsolutePositionAndSizeProperties((ComponentContainer)layoutContainer);
      }
   }

   /**
    * Manage the absolute layoutcontainer's position and size accurately.
    */
   private void addAbsolutePositionAndSizeProperties(ComponentContainer layoutContainer) {
      final AbsoluteLayoutContainer currentContainer = (AbsoluteLayoutContainer)layoutContainer;
      final Absolute absolute = currentContainer.getAbsolute();
      final TextField<String> posLeftField = new TextField<String>();
      posLeftField.setName("posLeft");
      posLeftField.setFieldLabel("Left");
      posLeftField.setAllowBlank(false);
      posLeftField.setRegex("^\\d+$");
      posLeftField.getMessages().setRegexText("The left must be a nonnegative integer");
      posLeftField.setValue(absolute.getLeft() + "");
      posLeftField.addListener(Events.Blur, new Listener<BaseEvent>() {
         @Override
         public void handleEvent(BaseEvent be) {
            currentContainer.setPosition(Integer.parseInt(posLeftField.getValue()), absolute.getTop());
            currentContainer.layout();
         }
      });
      final TextField<String> posTopField = new TextField<String>();
      posTopField.setName("posTop");
      posTopField.setFieldLabel("Top");
      posTopField.setAllowBlank(false);
      posTopField.setRegex("^\\d+$");
      posTopField.getMessages().setRegexText("The top must be a nonnegative integer");
      posTopField.setValue(absolute.getTop() + "");
      posTopField.addListener(Events.Blur, new Listener<BaseEvent>() {
         @Override
         public void handleEvent(BaseEvent be) {
            currentContainer.setPosition(absolute.getLeft(), Integer.parseInt(posTopField.getValue()));
            currentContainer.layout();
         }
      });
      final TextField<String> widthField = new TextField<String>();
      widthField.setName("width");
      widthField.setFieldLabel("Width");
      widthField.setAllowBlank(false);
      widthField.setRegex("^[1-9][0-9]*$");
      widthField.getMessages().setRegexText("The width must be a positive integer");
      widthField.setValue(absolute.getWidth() + "");
      widthField.addListener(Events.Blur, new Listener<BaseEvent>() {
         @Override
         public void handleEvent(BaseEvent be) {
            currentContainer.setSize(Integer.parseInt(widthField.getValue()), absolute.getHeight());
            currentContainer.layout();
         }
      });

      final TextField<String> heightField = new TextField<String>();
      heightField.setName("height");
      heightField.setFieldLabel("Height");
      heightField.setAllowBlank(false);
      heightField.setRegex("^[1-9][0-9]*$");
      heightField.getMessages().setRegexText("The height must be a positive integer");
      heightField.setValue(absolute.getHeight() + "");
      heightField.addListener(Events.Blur, new Listener<BaseEvent>() {
         @Override
         public void handleEvent(BaseEvent be) {
            currentContainer.setSize(absolute.getWidth(), Integer.parseInt(heightField.getValue()));
            currentContainer.layout();
         }
      });
      add(posLeftField);
      add(posTopField);
      add(widthField);
      add(heightField);
      AbsoluteBoundsListenerManager.getInstance().addAbsoluteBoundsListener(currentContainer, new AbsoluteBoundsListener() {
         public void handleEvent(AbsoluteBoundsEvent event) {
            ORBounds bounds = event.getBounds();
            posLeftField.setValue(bounds.getLeft() + "");
            posTopField.setValue(bounds.getTop() + "");
            widthField.setValue(bounds.getWidth() + "");
            heightField.setValue(bounds.getHeight() + "");
         }
      });
   }

   /**
    * Adds the delete button to delete select component.
    */
   protected void addDeleteButton() {
      if (componentContainer instanceof ComponentContainer) {
         final ComponentContainer componentContainer = (ComponentContainer) this.componentContainer;
         Button deleteButton = new Button("Delete From Screen");
         deleteButton.setIcon(((Icons) GWT.create(Icons.class)).delete());
         deleteButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
               MessageBox.confirm("Delete", "Are you sure you want to delete?", new Listener<MessageBoxEvent>() {
                  public void handleEvent(MessageBoxEvent be) {
                     if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                        if (componentContainer instanceof GridLayoutContainerHandle
                              || componentContainer instanceof ScreenTabbarItem
                              || componentContainer instanceof ScreenTabbar) {
                           componentContainer.fireEvent(WidgetDeleteEvent.WIDGETDELETE, new WidgetDeleteEvent());
                        } else {
                           ((ComponentContainer) componentContainer.getParent()).fireEvent(
                                 WidgetDeleteEvent.WIDGETDELETE, new WidgetDeleteEvent());
                        }
                        widgetSelectionUtil.resetSelection();
                     }
                  }
               });
            }

         });
         add(deleteButton);
      }
   }
   
   public PropertyForm(PropertyEditable componentContainer) {
      setFrame(true);
      setHeaderVisible(false);
      setBorders(false);
      setBodyBorder(false);
      setPadding(2);
      setLabelWidth(60);
      setFieldWidth(100);
      setScrollMode(Scroll.AUTO);
   }
}
