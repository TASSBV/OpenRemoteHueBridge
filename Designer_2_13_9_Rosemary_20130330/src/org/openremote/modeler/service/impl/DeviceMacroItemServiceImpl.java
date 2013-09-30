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
package org.openremote.modeler.service.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroItem;
import org.openremote.modeler.domain.DeviceMacroRef;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.DeviceMacroItemService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implement of DeviceMacroItemService.
 * 
 * @author allen.wei
 */
public class DeviceMacroItemServiceImpl extends BaseAbstractService<DeviceMacroItem> implements DeviceMacroItemService {

   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.service.DeviceMacroItemService#deleteByDeviceCommand(org.openremote.modeler.domain.DeviceCommand)
    */
   @Transactional public void deleteByDeviceCommand(DeviceCommand deviceCommand) {
      DetachedCriteria criteria = DetachedCriteria.forClass(DeviceCommandRef.class);
      List<DeviceCommandRef> deviceCommandRefs = genericDAO.findByDetachedCriteria(criteria.add(Restrictions.eq("deviceCommand", deviceCommand)));
      genericDAO.deleteAll(deviceCommandRefs);
   }


   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.service.DeviceMacroItemService#deleteByDeviceMacro(org.openremote.modeler.domain.DeviceMacro)
    */
   @Transactional public void deleteByDeviceMacro(DeviceMacro targetDeviceMacro) {
      DetachedCriteria criteria = DetachedCriteria.forClass(DeviceMacroRef.class);
      List<DeviceMacroRef> deviceMacroRefs = genericDAO.findByDetachedCriteria(criteria.add(Restrictions.eq("targetDeviceMacro", targetDeviceMacro)));
      genericDAO.deleteAll(deviceMacroRefs);
   }

   
   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.service.DeviceMacroItemService#loadByDeviceCommandId(long)
    */
   public List<DeviceCommandRef> loadByDeviceCommandId(long id) {
      DeviceCommand deviceCommand = genericDAO.loadById(DeviceCommand.class, id);
      DetachedCriteria criteria = DetachedCriteria.forClass(DeviceCommandRef.class);
      return genericDAO.findByDetachedCriteria(criteria.add(Restrictions.eq("deviceCommand", deviceCommand)));
   }

   
   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.service.DeviceMacroItemService#loadByDeviceMacroId(long)
    */
   public List<DeviceMacroRef> loadByDeviceMacroId(long id) {
      DeviceMacro targetDeviceMacro = genericDAO.loadById(DeviceMacro.class, id);
      DetachedCriteria criteria = DetachedCriteria.forClass(DeviceMacroRef.class);
      return genericDAO.findByDetachedCriteria(criteria.add(Restrictions.eq("targetDeviceMacro", targetDeviceMacro)));
   }

   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.service.DeviceMacroItemService#loadByDeviceMacroId(long)
    */
   public DeviceMacroItem loadByDeviceMacroItemOid(long deviceMacroItemOid) {
      return loadById(deviceMacroItemOid);
   }
}
