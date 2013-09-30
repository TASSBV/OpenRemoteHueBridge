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
package org.openremote.modeler.client.model;

import java.util.List;

import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.utils.BeanModelTable;

import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.TreeLoadEvent;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.store.TreeStoreEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The tree store can auto update the deviceMacroTree.
 * 
 */
@SuppressWarnings("unchecked")
public class AutoListenableTreeStore<T extends ModelData> extends TreeStore {

   /**
    * Instantiates a new magic tree store.
    */
   public AutoListenableTreeStore() {
      super();
      this.loader = initLoader();
      loader.addLoadListener(new LoadListener() {
         public void loaderBeforeLoad(LoadEvent le) {
            onBeforeLoad(le);
         }

         public void loaderLoad(LoadEvent le) {
            onLoad((TreeLoadEvent) le);
         }

         public void loaderLoadException(LoadEvent le) {
            onLoadException(le);
         }
      });
   }

   /**
    * Inits the loader.
    * 
    * @return the tree loader< bean model>
    */
   private TreeLoader<BeanModel> initLoader() {
      final AutoListenableTreeStore currentTreeStore = this;
      RpcProxy<List<BeanModel>> rpcProxy = new RpcProxy<List<BeanModel>>() {
         @Override
         protected void load(final Object o, final AsyncCallback<List<BeanModel>> listAsyncCallback) {
            final BeanModelTable beanModelTable = getBeanModelTable((BeanModel) o);
            final BeanModel targetParentBeanModel = (BeanModel) o;
            if (beanModelTable == null) {
               return;
            }
            beanModelTable.loadFromTable((BeanModel) o, new AsyncSuccessCallback<List<BeanModel>>() {
               public void onSuccess(List<BeanModel> result) {
                  listAsyncCallback.onSuccess(result);
               }
            });
            
            currentTreeStore.addListener(DataChanged, new Listener<TreeStoreEvent<BeanModel>>() {
               public void handleEvent(TreeStoreEvent<BeanModel> treeStoreEvent) {
                  addListenersToBeanModelTable(currentTreeStore, beanModelTable, targetParentBeanModel, treeStoreEvent);
               }               
            });
            currentTreeStore.addListener(Add, new Listener<TreeStoreEvent<BeanModel>>() {
               public void handleEvent(TreeStoreEvent<BeanModel> treeStoreEvent) {
                  addListenersToBeanModelTable(currentTreeStore, beanModelTable, targetParentBeanModel, treeStoreEvent);
               }               
            });
            currentTreeStore.addListener(Clear, new Listener<TreeStoreEvent<BeanModel>>() {
               public void handleEvent(TreeStoreEvent<BeanModel> treeStoreEvent) {
                  removeListenersFromBeanModelTable(beanModelTable, treeStoreEvent);
               }
            });
            currentTreeStore.addListener(Remove, new Listener<TreeStoreEvent<BeanModel>>() {
               public void handleEvent(TreeStoreEvent<BeanModel> treeStoreEvent) {
                  removeListenersFromBeanModelTable(beanModelTable, treeStoreEvent);
               }
            });
         }
      };
      
      TreeLoader<BeanModel> loader = new BaseTreeLoader<BeanModel>(rpcProxy) {
         @Override
         public boolean hasChildren(BeanModel beanModel) {
            return isReallyHasChildren(beanModel);
         }
      };
      return loader;
   }
   
   /**
    * Adds the listener to bean model table.
    * 
    * @param currentTreeStore the that
    * @param beanModelTable the bean model table
    * @param targetParentBeanModel the target parent bean model
    * @param treeStoreEvent the tree store event
    */
   private void addListenersToBeanModelTable(final AutoListenableTreeStore currentTreeStore,
         final BeanModelTable beanModelTable, final BeanModel targetParentBeanModel,
         TreeStoreEvent<BeanModel> treeStoreEvent) {
      beanModelTable.addInsertListener(targetParentBeanModel, currentTreeStore);
      beanModelTable.addChangeListener(currentTreeStore, treeStoreEvent);
   }
   
   /**
    * Removes the listeners from bean model table.
    * 
    * @param beanModelTable the bean model table
    * @param treeStoreEvent the tree store event
    */
   private void removeListenersFromBeanModelTable(final BeanModelTable beanModelTable, TreeStoreEvent<BeanModel> treeStoreEvent) {
      for (BeanModel beanModel : treeStoreEvent.getChildren()) {
         beanModelTable.removeChangeListener(BeanModelDataBase.getBeanModelId(beanModel), beanModelTable.getChangeListenerFromMap(beanModel));
      }
   }

   /**
    * Gets the bean model table.
    * 
    * @param beanModel the bean model
    * 
    * @return the bean model table
    */
   protected BeanModelTable getBeanModelTable(BeanModel beanModel) {
      return new BeanModelTable();
   }

   /**
    * Checks if is really has children.
    * 
    * @param beanModel the bean model
    * 
    * @return true, if is really has children
    */
   protected boolean isReallyHasChildren(BeanModel beanModel) {
      return false;
   }
}
