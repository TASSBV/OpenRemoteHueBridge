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
package org.openremote.modeler.client.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.ScreenPair;
import org.openremote.modeler.domain.ScreenPairRef;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.data.ChangeEventSupport;
import com.extjs.gxt.ui.client.data.ChangeListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.TreeStoreEvent;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;

/**
 * The class is for add change listener to panel tree. 
 */
public class PanelTreeStoreChangeListener {

   private TreePanel<BeanModel> panelTree;
   private Map<BeanModel, ChangeListener> changeListenerMap = null;
   
   public PanelTreeStoreChangeListener(TreePanel<BeanModel> panelTree) {
      this.panelTree = panelTree;
      addTreeStoreEventListener();
   }
   
   /**
    * Adds the tree store event listener.
    */
   private void addTreeStoreEventListener() {
      panelTree.getStore().addListener(Store.Add, new Listener<TreeStoreEvent<BeanModel>>() {
         public void handleEvent(TreeStoreEvent<BeanModel> be) {
            addChangeListener(be.getChildren());
         }
      });
      panelTree.getStore().addListener(Store.DataChanged, new Listener<TreeStoreEvent<BeanModel>>() {
         public void handleEvent(TreeStoreEvent<BeanModel> be) {
            addChangeListener(be.getChildren());
         }
      });
      panelTree.getStore().addListener(Store.Clear, new Listener<TreeStoreEvent<BeanModel>>() {
         public void handleEvent(TreeStoreEvent<BeanModel> be) {
            removeChangeListener(be.getChildren());
         }
      });
      panelTree.getStore().addListener(Store.Remove, new Listener<TreeStoreEvent<BeanModel>>() {
         public void handleEvent(TreeStoreEvent<BeanModel> be) {
            removeChangeListener(be.getChildren());
         }
      });
   }
   
   /**
    * Adds the change listener.
    * 
    * @param models the models
    */
   private void addChangeListener(List<BeanModel> models) {
      if (models == null) {
         return;
      }
      for (BeanModel beanModel : models) {
         if (beanModel.getBean() instanceof ScreenPairRef) {
            BeanModelDataBase.screenTable.addChangeListener(BeanModelDataBase
                  .getOriginalDesignerRefBeanModelId(beanModel), getSourceBeanModelChangeListener(beanModel));
         }
      }
   }

   /**
    * Removes the change listener.
    * 
    * @param models the models
    */
   private void removeChangeListener(List<BeanModel> models) {
      if (models == null) {
         return;
      }
      for (BeanModel beanModel : models) {
         if (beanModel.getBean() instanceof ScreenPairRef) {
            BeanModelDataBase.screenTable.removeChangeListener(BeanModelDataBase
                  .getOriginalDesignerRefBeanModelId(beanModel), getSourceBeanModelChangeListener(beanModel));
         }
         if(changeListenerMap!=null){
            changeListenerMap.remove(beanModel);
         }
      }
   }

   /**
    * Gets the source bean model change listener.
    * 
    * @param target the target
    * 
    * @return the source bean model change listener
    */
   private ChangeListener getSourceBeanModelChangeListener(final BeanModel target) {
      if (changeListenerMap == null) {
         changeListenerMap = new HashMap<BeanModel, ChangeListener>();
      }
      ChangeListener changeListener = changeListenerMap.get(target);
      if (changeListener == null) {
         changeListener = new ChangeListener() {
            public void modelChanged(ChangeEvent changeEvent) {
               if (changeEvent.getType() == ChangeEventSupport.Remove) {
                  if (target.getBean() instanceof ScreenPairRef) {
                     ScreenPairRef screenRef = (ScreenPairRef) target.getBean();
                     Group group = screenRef.getGroup();
                     group.removeScreenRef(screenRef);
                  }
                  panelTree.getStore().remove(target);
               }
               if (changeEvent.getType() == ChangeEventSupport.Update) {
                  BeanModel source = (BeanModel) changeEvent.getItem();
                  if (source.getBean() instanceof ScreenPair) {
                     ScreenPair screen = (ScreenPair) source.getBean();
                     ScreenPairRef screenRef = (ScreenPairRef) target.getBean();
                     screenRef.setScreen(screen);
                  }
                  panelTree.getStore().update(target);
               }
            }
         };
         changeListenerMap.put(target, changeListener);
      }
      return changeListener;
   }
}
