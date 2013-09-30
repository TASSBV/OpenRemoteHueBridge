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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.openremote.modeler.domain.CommandRefItem;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.Protocol;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.DeviceCommandService;
import org.openremote.modeler.service.DeviceMacroItemService;
import org.openremote.modeler.shared.dto.DeviceCommandDTO;
import org.openremote.modeler.shared.dto.DeviceCommandDetailsDTO;
import org.springframework.transaction.annotation.Transactional;

/**
 * The implementation for DeviceCommandService interface.
 * 
 * @author Allen
 */
public class DeviceCommandServiceImpl extends BaseAbstractService<DeviceCommand> implements DeviceCommandService {

 
   /** The device macro item service. */
   private DeviceMacroItemService deviceMacroItemService;

   /**
    * Sets the device macro item service.
    * 
    * @param deviceMacroItemService the new device macro item service
    */
   public void setDeviceMacroItemService(DeviceMacroItemService deviceMacroItemService) {
      this.deviceMacroItemService = deviceMacroItemService;
   }

   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.service.DeviceCommandService#saveAll(java.util.List)
    */
   @Transactional
   public List<DeviceCommand> saveAll(List<DeviceCommand> deviceCommands) {
      for (DeviceCommand command : deviceCommands) {
        
        System.out.println("Saving command : " + command);
        
//         genericDAO.save(command);
        save(command);
      }
      return deviceCommands;
   }

   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.service.DeviceCommandService#save(org.openremote.modeler.domain.DeviceCommand)
    */
   @Transactional
   public DeviceCommand save(DeviceCommand deviceCommand) {
      genericDAO.save(deviceCommand);
//      Hibernate.initialize(deviceCommand.getProtocol().getAttributes());
      return deviceCommand;
   }

   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.service.DeviceCommandService#deleteCommand(long)
    */
   @Transactional
   public Boolean deleteCommand(long id) {
      DeviceCommand deviceCommand = loadById(id);
      DetachedCriteria criteria = DetachedCriteria.forClass(CommandRefItem.class);
      List<CommandRefItem> commandRefItems = genericDAO.findByDetachedCriteria(criteria.add(Restrictions.eq("deviceCommand", deviceCommand)));
      if (commandRefItems.size() > 0) {
         return false;
      } else {
         deviceMacroItemService.deleteByDeviceCommand(deviceCommand);
         genericDAO.delete(deviceCommand);
      }
      return true;
   }

   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.service.DeviceCommandService#updateDeviceCommandWithDTO(DeviceCommandDetailsDTO)
    */
   @Transactional
   public void updateDeviceCommandWithDTO(DeviceCommandDetailsDTO dto) {
     DeviceCommand dc = loadById(dto.getOid());
     genericDAO.delete(dc.getProtocol());
     dc.setName(dto.getName());
     Protocol protocol = new Protocol();
     protocol.setDeviceCommand(dc);
     dc.setProtocol(protocol);
     protocol.setType(dto.getProtocolType());
     for (Map.Entry<String, String> e : dto.getProtocolAttributes().entrySet()) {
       protocol.addProtocolAttribute(e.getKey(), e.getValue());
     }
     genericDAO.saveOrUpdate(dc);
   }

   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.service.BaseAbstractService#loadById(long)
    */
   public DeviceCommand loadById(long id) {
     return loadById(id, false);
   }
   
   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.service.BaseAbstractService#loadById(long, boolean)
    */
   public DeviceCommand loadById(long id, boolean loadDeviceEagerly) {
      DeviceCommand deviceCommand = super.loadById(id);
      Hibernate.initialize(deviceCommand.getProtocol().getAttributes());
      
      if (loadDeviceEagerly) {
        Hibernate.initialize(deviceCommand.getDevice());
        Hibernate.initialize(deviceCommand.getDevice().getDeviceAttrs());
        Hibernate.initialize(deviceCommand.getDevice().getSensors());
        Hibernate.initialize(deviceCommand.getDevice().getSwitchs());
        Hibernate.initialize(deviceCommand.getDevice().getSliders());
      }
      
      return deviceCommand;
   }

   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.service.DeviceCommandService#loadByDevice(long)
    */
   public List<DeviceCommand> loadByDevice(long id) {
      Device device = genericDAO.loadById(Device.class, id);
      List<DeviceCommand> deviceCommandList = device.getDeviceCommands();
      for (DeviceCommand deviceCommand : deviceCommandList) {
         Hibernate.initialize(deviceCommand.getProtocol().getAttributes());
      }
      return deviceCommandList;
   }

   public List<DeviceCommand> loadSameCommands(DeviceCommand deviceCommand) {
      List<DeviceCommand> tmpResult = new ArrayList<DeviceCommand>();
      DetachedCriteria critera = DetachedCriteria.forClass(DeviceCommand.class);
      critera.add(Restrictions.eq("device.oid", deviceCommand.getDevice().getOid()));
      critera.add(Restrictions.eq("name", deviceCommand.getName()));
      if (deviceCommand.getSectionId() != null) {
         critera.add(Restrictions.eq("sectionId", deviceCommand.getSectionId()));
      }
      tmpResult = genericDAO.findByDetachedCriteria(critera);
      if (tmpResult != null) {
         for(Iterator<DeviceCommand> iterator= tmpResult.iterator();iterator.hasNext();) {
            DeviceCommand cmd = iterator.next();
            if (! cmd.equalsWithoutCompareOid(deviceCommand)) {
               iterator.remove();
            }
         }
      }
      return tmpResult;
   }
   
   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.service.DeviceCommandService#loadComandsDTOByDevice(long)
    */
   public ArrayList<DeviceCommandDTO> loadCommandsDTOByDevice(long id) {
      Device device = genericDAO.loadById(Device.class, id);
      ArrayList<DeviceCommandDTO> dtos = new ArrayList<DeviceCommandDTO>();
      List<DeviceCommand> dcs = device.getDeviceCommands();
      for (DeviceCommand deviceCommand : dcs) {
        dtos.add(new DeviceCommandDTO(deviceCommand.getOid(), deviceCommand.getDisplayName(), deviceCommand.getProtocol().getType()));
      }
      return dtos;
   }
}
