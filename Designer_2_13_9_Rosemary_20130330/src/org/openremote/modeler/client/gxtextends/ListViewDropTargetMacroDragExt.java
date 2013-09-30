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
package org.openremote.modeler.client.gxtextends;

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.shared.dto.DTOHelper;
import org.openremote.modeler.shared.dto.DTOReference;
import org.openremote.modeler.shared.dto.DeviceCommandDTO;
import org.openremote.modeler.shared.dto.MacroDTO;
import org.openremote.modeler.shared.dto.MacroItemDetailsDTO;
import org.openremote.modeler.shared.dto.MacroItemType;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.dnd.DropTarget;
import com.extjs.gxt.ui.client.dnd.Insert;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.util.Rectangle;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.ListView;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;

/**
 * A <code>DropTarget</code> implementation for the ListView component.
 */
public class ListViewDropTargetMacroDragExt extends DropTarget {

   /** The list view. */
   protected ListView<ModelData> listView;
   
   /** The insert index. */
   protected int insertIndex;
   
   /** The active item. */
   protected ModelData activeItem;

   /** The auto select. */
   private boolean autoSelect;
   
   /** The before. */
   private boolean before;

   /**
    * Creates a new list view drop target instance.
    * 
    * @param listView the target list view
    */
   @SuppressWarnings("unchecked")
   public ListViewDropTargetMacroDragExt(ListView listView) {
      super(listView);
      this.listView = listView;
   }

   /**
    * Returns the target's list view component.
    * 
    * @return the list view
    */
   public ListView<ModelData> getListView() {
      return listView;
   }

   /**
    * Returns true if auto select is enabled.
    * 
    * @return the auto select state
    */
   public boolean isAutoSelect() {
      return autoSelect;
   }

   /* (non-Javadoc)
    * @see com.extjs.gxt.ui.client.dnd.DropTarget#onDragDrop(com.extjs.gxt.ui.client.event.DNDEvent)
    */
   @Override
   protected void onDragDrop(DNDEvent e) {
      if (!handleDragFromDeviceCommandAndDeviceMacro(e)) {
         originOnDragDrop(e);
      }
   }

   /**
    * Origin on drag drop.
    * 
    * @param e the e
    */
   private void originOnDragDrop(DNDEvent e) {
      super.onDragDrop(e);
      final Object data = e.getData();
      DeferredCommand.addCommand(new Command() {
         @SuppressWarnings("unchecked")
         public void execute() {
            List temp = new ArrayList();
            if (data instanceof ModelData) {
               temp.add((ModelData) data);
            } else if (data instanceof List) {
               temp = (List) data;
            }
            if (temp.size() > 0) {
               if (feedback == Feedback.APPEND) {
                  listView.getStore().add(temp);
               } else {
                  int idx = listView.getStore().indexOf(activeItem);
                  if (!before) {
                     idx++;
                  }
                  listView.getStore().insert(temp, idx);
               }
               if (autoSelect) {
                  listView.getSelectionModel().select(temp, false);
               }
            }
         }
      });
   }

   /**
    * Handle drag from device command and device macro.
    * 
    * @param e the e
    * 
    * @return true, if successful
    */
   @SuppressWarnings("unchecked")
   private boolean handleDragFromDeviceCommandAndDeviceMacro(DNDEvent e) {
      boolean handle = false;
      int activeIdx = listView.getStore().indexOf(activeItem);
      if (!before) {
         activeIdx++;
      }
      if (e.getData() instanceof List) {
         List<ModelData> models = (List<ModelData>) e.getData();
         for (ModelData modelData : models) {
            if (modelData.get("model") instanceof BeanModel) {
               BeanModel beanModel = modelData.get("model");
               if (beanModel.getBean() instanceof DeviceCommandDTO) {
                 DeviceCommandDTO dto = (DeviceCommandDTO) beanModel.getBean();
                 beanModel = DTOHelper.getBeanModel(new MacroItemDetailsDTO(null, MacroItemType.Command, dto.getDisplayName(), new DTOReference(dto.getOid())));
               } else if (beanModel.getBean() instanceof MacroDTO) {
                 MacroDTO dto = (MacroDTO) beanModel.getBean();
                 beanModel = DTOHelper.getBeanModel(new MacroItemDetailsDTO(null, MacroItemType.Macro, dto.getDisplayName(), new DTOReference(dto.getOid())));
               }
               listView.getStore().insert(beanModel, activeIdx);
               handle = true;
            }
         }
      }
      return handle;
   }

   /* (non-Javadoc)
    * @see com.extjs.gxt.ui.client.dnd.DropTarget#onDragEnter(com.extjs.gxt.ui.client.event.DNDEvent)
    */
   @Override
   protected void onDragEnter(DNDEvent e) {
      super.onDragEnter(e);
      e.setCancelled(false);
      e.getStatus().setStatus(true);
   }

   /* (non-Javadoc)
    * @see com.extjs.gxt.ui.client.dnd.DropTarget#onDragLeave(com.extjs.gxt.ui.client.event.DNDEvent)
    */
   @Override
   protected void onDragLeave(DNDEvent e) {
      super.onDragLeave(e);
      Insert insert = Insert.get();
      insert.setVisible(false);
   }

   /* (non-Javadoc)
    * @see com.extjs.gxt.ui.client.dnd.DropTarget#onDragMove(com.extjs.gxt.ui.client.event.DNDEvent)
    */
   @Override
   protected void onDragMove(DNDEvent event) {
      event.setCancelled(false);
   }

   /**
    * True to automatically select and new items created after a drop (defaults to false).
    * 
    * @param autoSelect true to auto select
    */
   public void setAutoSelect(boolean autoSelect) {
      this.autoSelect = autoSelect;
   }

   /* (non-Javadoc)
    * @see com.extjs.gxt.ui.client.dnd.DropTarget#showFeedback(com.extjs.gxt.ui.client.event.DNDEvent)
    */
   @Override
   protected void showFeedback(DNDEvent event) {
      if (feedback == Feedback.INSERT) {
         event.getStatus().setStatus(true);
         Element row = listView.findElement(event.getTarget()).cast();

         if (row == null && listView.getStore().getCount() > 0) {
            row = listView.getElement(listView.getStore().getCount() - 1).cast();
         }

         if (row != null) {
            int height = row.getOffsetHeight();
            int mid = height / 2;
            mid += row.getAbsoluteTop();
            int y = event.getClientY();
            before = y < mid;
            int idx = listView.indexOf(row);
            insertIndex = before ? idx : idx + 1;
            activeItem = listView.getStore().getAt(idx);
            if (before) {
               showInsert(event, row, true);
            } else {
               showInsert(event, row, false);
            }
         } else {
            insertIndex = 0;
         }
      }
   }

   /**
    * Show insert.
    * 
    * @param event the event
    * @param row the row
    * @param before the before
    */
   private void showInsert(DNDEvent event, Element row, boolean before) {
      Insert insert = Insert.get();
      insert.setVisible(true);
      Rectangle rect = El.fly(row).getBounds();
      int y = !before ? (rect.y + rect.height - 4) : rect.y - 2;
      insert.el().setBounds(rect.x, y, rect.width, 6);
   }

}
