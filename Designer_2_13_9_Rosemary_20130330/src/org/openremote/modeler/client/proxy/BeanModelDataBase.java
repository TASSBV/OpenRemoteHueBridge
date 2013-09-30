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
package org.openremote.modeler.client.proxy;

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.model.TreeFolderBean;
import org.openremote.modeler.client.utils.BeanModelTable;
import org.openremote.modeler.client.utils.GroupTable;
import org.openremote.modeler.client.utils.ScreenTable;
import org.openremote.modeler.domain.BusinessEntity;
import org.openremote.modeler.domain.CommandRefItem;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroItem;
import org.openremote.modeler.domain.DeviceMacroRef;
import org.openremote.modeler.domain.GroupRef;
import org.openremote.modeler.domain.ScreenPairRef;

import com.extjs.gxt.ui.client.data.BeanModel;

/**
 * Stores all the UI models here like a database. <br/>
 * 
 * 1.All the operations on the model must use {@link BeanModelTable}. So all the related UI Element can update themselves.<br/>
 * 2.If the UI Element which want to observe the model can add change listener on every {@link BeanModelTable}.
 */
public class BeanModelDataBase {

   /**
    * Not be instantiated.
    */
   private BeanModelDataBase() {
   }
   
   /** The Constant groupTable. */
   public static final BeanModelTable groupTable = new GroupTable();

   /** The Constant screenTable. */
   public static final ScreenTable screenTable = new ScreenTable();
   
   public static final BeanModelTable panelTable = new BeanModelTable();
   
   /**
    * Gets the original device macro item bean model id,if not find return 0.
    * 
    * @param deviceMacroItemBeanModel the device macro item bean model
    * 
    * @return the original device macro item bean model id,If not find return 0.
    */
   public static long getOriginalDeviceMacroItemBeanModelId(BeanModel deviceMacroItemBeanModel) {
      if (deviceMacroItemBeanModel.getBean() instanceof DeviceMacroItem) {
         DeviceMacroItem deviceMacroItem = (DeviceMacroItem) deviceMacroItemBeanModel.getBean();
         if (deviceMacroItem instanceof DeviceMacroRef) {
            DeviceMacroRef deviceMacroRef = (DeviceMacroRef) deviceMacroItem;
            return deviceMacroRef.getTargetDeviceMacro().getOid();
         } else if (deviceMacroItem instanceof DeviceCommandRef) {
            DeviceCommandRef deviceCommandRef = (DeviceCommandRef) deviceMacroItem;
            return deviceCommandRef.getDeviceCommand().getOid();
         }
      }
      return 0;
   }
   
   public static long getOriginalCommandRefItemBeanModelId(BeanModel commandRefItemBeanModel) {
      if (commandRefItemBeanModel.getBean() instanceof CommandRefItem) {
         CommandRefItem deviceMacroItem = (CommandRefItem) commandRefItemBeanModel.getBean();
         return deviceMacroItem.getDeviceCommand().getOid();
      }
      return 0;
   }

   
   /**
    * Gets the source bean model id.
    * 
    * @param beanModel the bean model
    * 
    * @return the source bean model id
    */
   public static Long getSourceBeanModelId(BeanModel beanModel) {
      if (beanModel == null) {
         return Constants.NULL_PARENT_OID;
      } else if (beanModel.getBean() instanceof TreeFolderBean) {
         TreeFolderBean treeFolderBean = (TreeFolderBean) beanModel.getBean();
         if (Constants.DEVICES.equals(treeFolderBean.getType())) {
            return Constants.DEVICES_OID;
         } else if (Constants.MACROS.equals(treeFolderBean.getType())) {
            return Constants.MACROS_OID;
         }
      } else if (beanModel.getBean() instanceof Device) {
         return ((Device) beanModel.getBean()).getOid();
      } else if (beanModel.getBean() instanceof DeviceCommand) {
         return ((DeviceCommand) beanModel.getBean()).getOid();
      } else if (beanModel.getBean() instanceof DeviceMacro) {
         return ((DeviceMacro) beanModel.getBean()).getOid();
      } else if (beanModel.getBean() instanceof DeviceCommandRef) {
         DeviceCommandRef deviceCommandRef = (DeviceCommandRef) beanModel.getBean();
         return deviceCommandRef.getDeviceCommand().getDevice().getOid();
      } else if (beanModel.getBean() instanceof CommandRefItem) {
         CommandRefItem commandRefItem = (CommandRefItem) beanModel.getBean();
         return commandRefItem.getDeviceCommand().getDevice().getOid();
      }
      return 0L;
   }
   
   /**
    * Gets the bean model id,if not find return 0.
    * 
    * @param beanModel the bean model
    * 
    * @return the bean model id,if not find return 0.
    */
   public static long getBeanModelId(BeanModel beanModel) {
      if (beanModel == null) {
         return 0;
      }
      if (beanModel.getBean() instanceof BusinessEntity) {
         BusinessEntity entity = (BusinessEntity) beanModel.getBean();
         return entity.getOid();
      }
      return 0;
   }

   /**
    * Gets the bean models according to domains.
    * 
    * @param businessEntities domain which superclass is {@link BusinessEntity}
    * @param beanModelTable table which stores this model.
    * 
    * @return the all the matched bean models
    */
   public static List<BeanModel> getBeanModelsByBeans(List<? extends BusinessEntity> businessEntities,
         BeanModelTable beanModelTable) {
      List<BeanModel> list = new ArrayList<BeanModel>();
      for (BusinessEntity businessEntity : businessEntities) {
         if (beanModelTable.get(businessEntity.getOid()) != null) {
            list.add(beanModelTable.get(businessEntity.getOid()));
         }
      }
      return list;
   }

   /**
    * Gets the original screen ref bean model id.
    * 
    * @param beanModel the bean model
    * 
    * @return the original screen ref bean model id
    */
   public static long getOriginalDesignerRefBeanModelId(BeanModel beanModel) {
      if (beanModel == null) {
         return 0;
      }
      if (beanModel.getBean() instanceof ScreenPairRef) {
         ScreenPairRef screenRef = (ScreenPairRef) beanModel.getBean();
         return screenRef.getScreenId();
      } else if (beanModel.getBean() instanceof GroupRef) {
         GroupRef groupRef = (GroupRef) beanModel.getBean();
         return groupRef.getGroupId();
      }
      return 0;
   }
  
}
