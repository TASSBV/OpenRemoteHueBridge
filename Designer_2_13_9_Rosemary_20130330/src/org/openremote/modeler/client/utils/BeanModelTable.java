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

package org.openremote.modeler.client.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.domain.BusinessEntity;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.data.ChangeEventSource;
import com.extjs.gxt.ui.client.data.ChangeListener;
import com.extjs.gxt.ui.client.store.TreeStore;

/**
 * For store BeanModel in Frontend.<br/> Every operation to the table will notify the listener on it.<br/>
 * 
 * <br/>
 * 
 * The notification only support add,update,remove. <br/> Use {@link ChangeListener} to listen the state of this table.
 * Use changeEvent.getItem() to get the beanmodel which had changed.
 * 
 * @author allen.wei
 */
public class BeanModelTable {

   /** The map store BeanModel. */
   protected Map<Long, BeanModel> map = new HashMap<Long, BeanModel>();

   // the same as ChangeEventSource
   /** The Constant ADD. */
   public static final int ADD = ChangeEventSource.Add;

   /** The Constant UPDATE. */
   public static final int UPDATE = ChangeEventSource.Update;

   /** The Constant REMOVE. */
   public static final int REMOVE = ChangeEventSource.Remove;

   /** The change event support. */
   private Map<Long, List<ChangeListener>> changeListeners = new HashMap<Long, List<ChangeListener>>();

   /** The insert listeners. */
   protected Map<Long, ChangeListener> insertListeners = new HashMap<Long, ChangeListener>();
   
   /** The bean model change listener map. */
   protected Map<BeanModel, ChangeListener> beanModelChangeListenerMap = new HashMap<BeanModel, ChangeListener>();
   
   /** The bean model insert listener map. */
   protected Map<BeanModel, ChangeListener> beanModelInsertListenerMap = new HashMap<BeanModel, ChangeListener>();

   /**
    * Instantiates a new bean model table.
    */
   public BeanModelTable() {
   }

   /**
    * Adds the change listener.When a BeanModel of this table add,update,delete. It will create {@link ChangeEvent} send
    * to all the listeners. <br/> User can use changeEvent.getType() to judge the event type. Event type can be
    * {@link #ADD},{@link #UPDATE},{@link #REMOVE}
    * 
    * @param listener the {@link ChangeListener}
    * @param id the id
    */
   public void addChangeListener(Long id, ChangeListener listener) {
      List<ChangeListener> listeners = null;
      if ((listeners = changeListeners.get(id)) == null) {
         listeners = new ArrayList<ChangeListener>();
      }
      listeners.add(listener);

      changeListeners.put(id, listeners);
   }
   
   /**
    * Sets the insert listener for the given id.
    * There can only be one listener at a time for a given id.
    * 
    * @param listener the listener
    * @param parentId the parent id
    */
   public void setInsertListener(Long parentId, ChangeListener listener) {
      if (insertListeners.get(parentId) == null) {
         insertListeners.put(parentId, listener);
      }
   }
   
   /**
    * Adds the insert listener.
    * 
    * @param parentBeanModel the parent bean model
    * @param treeStore the tree store
    */
   public void addInsertListener(BeanModel parentBeanModel, TreeStore<BeanModel> treeStore) {}

   /**
    * Notify all the {@link ChangeListener}.
    * 
    * @param evt the {@link ChangeEvent}
    */
   public void notify(ChangeEvent evt) {
      BeanModel beanModel = (BeanModel) evt.getItem();
      if (changeListeners.get(getIdFromBeanModel(beanModel)) != null) {
         List<ChangeListener> chgListeners = changeListeners.get(getIdFromBeanModel(beanModel));
         if (chgListeners != null && chgListeners.size() > 0) {
            for (int i = chgListeners.size() - 1; i >= 0; i--) {
               chgListeners.get(i).modelChanged(evt);
            }
         }
      }
   }

   /**
    * Excute notify.
    * 
    * @param evt the evt
    */
   protected void excuteNotify(ChangeEvent evt) {}

   /**
    * Removes the {@link ChangeListener}.
    * 
    * @param listener the listener
    * @param id the id
    */
   public void removeChangeListener(Long id, ChangeListener listener) {
      List<ChangeListener> listeners = changeListeners.get(id);
      if(listeners!=null&& listeners.size()>0){
         listeners.remove(listener);
         changeListeners.put(id, listeners);
      }
   }
   
   /**
    * Gets the change listener.
    * 
    * @param beanModel the bean model
    * 
    * @return the change listener
    */
   public ChangeListener getChangeListenerFromMap(BeanModel beanModel) {
      return beanModelChangeListenerMap.get(beanModel);
   }
   
   /**
    * Put change listener into map.
    * 
    * @param beanModel the bean model
    * @param changeListener the change listener
    */
   public void putChangeListenerIntoMap(BeanModel beanModel, ChangeListener changeListener) {
      beanModelChangeListenerMap.put(beanModel, changeListener);
   }

   /**
    * Insert a BeanModel into table. <br/>
    * 
    * If beanModel equal null, it will throw {@link IllegalArgumentException}.<br/> If beanModel's bean isn't inherit
    * from {@link BusinessEntity}, it will throw {@link IllegalArgumentException}.<br/>
    * 
    * @param beanModel the bean model
    */
   public void insert(BeanModel beanModel) {
      if (beanModel.getBean() == null) {
         throw new IllegalArgumentException("Gets Bean from BeanModel is null");

      }
      if (!(beanModel.getBean() instanceof BusinessEntity)) {
         throw new IllegalArgumentException("Original model must extends from BusinessEntity");

      }
      if (beanModel.getBean() instanceof BusinessEntity) {
         if (map.get(getIdFromBeanModel(beanModel)) != null) {
            return;
         }
         map.put(getIdFromBeanModel(beanModel), beanModel);
         notifyTableAddData(beanModel);
      }
   }

   /**
    * Insert all BeanModel. It will call {@link #insert(BeanModel)} recursively
    * 
    * @param beanModels the bean models
    */
   public void insertAll(Collection<BeanModel> beanModels) {
      for (BeanModel beanModel : beanModels) {
         insert(beanModel);
      }
   }

   /**
    * Gets the BeanModel by id.
    * 
    * @param oid the id of model
    * 
    * @return the bean model
    */
   public BeanModel get(long oid) {
      return (BeanModel) map.get(oid);
   }

   /**
    * Delete a BeanModel from table. <br/>
    * 
    * It will send {@link #REMOVE} {@link ChangeEvent} to change listeners.
    * 
    * @param id the id
    */
   public void delete(long id) {
      BeanModel beanModel = get(id);
      if (beanModel != null) {
         map.remove(id);
         notifyBeanModel(REMOVE, beanModel);
      }

   }

   /**
    * Delete BeanModel from table according BeanModel. <br/> Actually it call {@link #delete(long)} method.
    * 
    * @param beanModel the bean model
    */
   public void delete(BeanModel beanModel) {
      if(beanModel != null){
         delete(getIdFromBeanModel(beanModel));
      }
   }

   /**
    * Notify bean model.
    * 
    * @param type the type
    * @param beanModel the bean model
    */
   public void notifyBeanModel(int type, BeanModel beanModel) {
      ChangeEvent changeEvent = new ChangeEvent(type, null, beanModel);
      notify(changeEvent);
   }

   /**
    * Notify table add data.
    * 
    * @param beanModel the bean model
    */
   public void notifyTableAddData(BeanModel beanModel) {
      ChangeEvent changeEvent = new ChangeEvent(ADD, null, beanModel);
      excuteNotify(changeEvent);
   }

   /**
    * Update BeanModel. It use id to judge whether two object equals.
    * 
    * It will send {@link #UPDATE} {@link ChangeEvent} to change listeners.
    * 
    * @param newBeanModel the new bean model
    */
   public void update(BeanModel newBeanModel) {
      map.put(getIdFromBeanModel(newBeanModel), newBeanModel);
      notifyBeanModel(UPDATE, newBeanModel);
   }

   /**
    * Update with bean.
    * 
    * @param oldBeanModel the old bean model
    * @param bean the bean
    */
   public static <T extends BusinessEntity> void updateWithBean(BeanModel oldBeanModel, T bean) {
      for (@SuppressWarnings("unused") String property : oldBeanModel.getPropertyNames()) {

      }
   }

   /**
    * Gets the id from bean model.
    * 
    * @param beanModel the bean model
    * 
    * @return the id from bean model
    */
   private long getIdFromBeanModel(BeanModel beanModel) {
      BusinessEntity businessEntity = beanModel.getBean();
      return businessEntity.getOid();
   }


   /**
    * Load from table.
    * 
    * @param parent the parent
    * @param asyncSuccessCallback the async success callback
    */
   public void loadFromTable(BeanModel parent, final AsyncSuccessCallback<List<BeanModel>> asyncSuccessCallback) {}
   
   /**
    * Load all.
    * 
    * @return the list< bean model>
    */
   public List<BeanModel> loadAll() {
      List<BeanModel> beanModelList = new ArrayList<BeanModel>();
      for (Long key : map.keySet()) {
         beanModelList.add(map.get(key));
      }
      return beanModelList;
   }
   
   /**
    * Clear table.
    */
   public void clear() {
      for (long key : map.keySet()) {
         delete(map.get(key));
      }
      changeListeners.clear();
      insertListeners.clear();
      beanModelChangeListenerMap.clear();
      beanModelInsertListenerMap.clear();
   }
}
