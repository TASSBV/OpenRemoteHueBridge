/*
 * OpenRemote, the Home of the Digital Home.
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
import java.text.MessageFormat;

import org.hibernate.Hibernate;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.CustomSensor;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.DeviceMacroItemService;
import org.openremote.modeler.service.DeviceService;
import org.openremote.modeler.logging.LogFacade;
import org.openremote.modeler.logging.AdministratorAlert;
import org.openremote.modeler.exception.PersistenceException;
import org.springframework.transaction.annotation.Transactional;


/**
 * TODO
 */
public class DeviceServiceImpl extends BaseAbstractService<Device> implements DeviceService {

  private static LogFacade persistenceLog = LogFacade.getInstance(LogFacade.Category.PERSISTENCE);

  private static AdministratorAlert admin =
      AdministratorAlert.getInstance(AdministratorAlert.Type.DATABASE);

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
    */
   @Transactional public Device saveDevice(Device device) {
      genericDAO.save(device);
      /*
      Hibernate.initialize(device.getSensors());
      Hibernate.initialize(device.getSwitchs());
      List<DeviceCommand> deviceCommands = device.getDeviceCommands();
      for(DeviceCommand cmd : deviceCommands ) {
         Hibernate.initialize(cmd.getProtocol().getAttributes());
      }
      Hibernate.initialize(device.getSliders());
      Hibernate.initialize(device.getDeviceAttrs());
      */
      return device;
   }

  @Transactional public void deleteDevice(long id) 
  {
    try
    {
      Device device;

      try
      {
        device = loadById(id);
      }

      catch (ObjectNotFoundException e)
      {
        persistenceLog.warn(
            "Attempted to delete non-existent device with id '{0}' -- Delete Ignored.", id
        );

        return;
      }

      for (DeviceCommand deviceCommand : device.getDeviceCommands())
      {
        deviceMacroItemService.deleteByDeviceCommand(deviceCommand);
      }

      genericDAO.delete(device);
    }

    catch (Throwable t)
    {
      persistenceLog.error(
          "Delete device operation (ID: {0}) failed : {1}", t, id, t.getMessage()
      );
    }
  }

   /**
    * {@inheritDoc}
    */
   public List<Device> loadAll(Account account) {
      List<Device> devices = account.getDevices();
      return devices;
   }

   /**
    * {@inheritDoc}
    */
   @Transactional public void updateDevice(Device device) {
      genericDAO.saveOrUpdate(device);
   }

   /**
    * {@inheritDoc}
    */
   @Transactional public Device loadById(long id) {
      Device device = super.loadById(id);
      if (device.getAccount() != null) {
         Hibernate.initialize(device.getAccount().getConfigs());
      }
      Hibernate.initialize(device.getDeviceCommands());
      Hibernate.initialize(device.getSensors());
      for (Sensor sensor : device.getSensors()) {
         if (SensorType.CUSTOM == sensor.getType()) {
            Hibernate.initialize(((CustomSensor)sensor).getStates());
         }
      }
      Hibernate.initialize(device.getSliders());


      try
      {
        Hibernate.initialize(device.getSwitchs());
      }

      catch (ObjectNotFoundException e)
      {
        Account acct = device.getAccount();

        String errorMessage = MessageFormat.format(
            "DATA INTEGRITY ERROR: Device ''{0}'' (ID : {1}) for " +
            "account (users : ''{2}'', ID: {3}) references a non-existent switch component: {4}",
            device.getName(), id, acct.getUsers(), acct.getOid(), e.getMessage()
        );

        admin.alert(errorMessage);

        PersistenceException.throwAsGWTClientException(
            "There was an error loading your device ''{0}''. Please contact an administrator " +
            "to solve this issue. Error message : {1}",
            device.getName(), errorMessage
        );
      }

      return device;
   }

   public List<Device> loadSameDevices(Device device) {
      DetachedCriteria critera = DetachedCriteria.forClass(Device.class);
      critera.add(Restrictions.eq("name", device.getName()));
      critera.add(Restrictions.eq("model", device.getModel()));
      critera.add(Restrictions.eq("vendor", device.getVendor()));
      critera.add(Restrictions.eq("account.oid", device.getAccount().getOid()));
      return genericDAO.findPagedDateByDetachedCriteria(critera, 1, 0);
   }




}
