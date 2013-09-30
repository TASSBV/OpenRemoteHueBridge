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
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.CustomSensor;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.RangeSensor;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorRefItem;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.domain.State;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.DeviceCommandService;
import org.openremote.modeler.service.SensorService;
import org.openremote.modeler.shared.dto.SensorDetailsDTO;
import org.springframework.transaction.annotation.Transactional;

public class SensorServiceImpl extends BaseAbstractService<Sensor> implements SensorService {

  private DeviceCommandService deviceCommandService;

  public void setDeviceCommandService(DeviceCommandService deviceCommandService) {
    this.deviceCommandService = deviceCommandService;
  }

  @Transactional public Boolean deleteSensor(long id) {
      Sensor sensor = super.loadById(id);
      DetachedCriteria criteria = DetachedCriteria.forClass(SensorRefItem.class);
      List<SensorRefItem> sensorRefItems = genericDAO.findByDetachedCriteria(criteria.add(Restrictions.eq("sensor", sensor)));

      if (sensorRefItems.size() > 0) {
         return false;
      } else {
         genericDAO.delete(sensor);
      }
      return true;
   }

   public List<Sensor> loadAll(Account account) {
      List<Sensor> sensors = account.getSensors();
      for (Sensor sensor : sensors) {
         if (sensor.getType() == SensorType.CUSTOM) {
            Hibernate.initialize(((CustomSensor) sensor).getStates());
         }
      }
      return sensors;
   }

   @Transactional public Sensor saveSensor(Sensor sensor) {
      genericDAO.save(sensor);
      return sensor;
   }

   @Transactional public Sensor updateSensor(Sensor sensor) {
     genericDAO.saveOrUpdate(sensor);
     return sensor;
   }

   public Sensor loadById(long id) {
      Sensor sensor = genericDAO.getById(Sensor.class, id);
      if (sensor instanceof CustomSensor) {
         Hibernate.initialize(((CustomSensor) sensor).getStates());
      }
      return sensor;
   }

   public List<Sensor> loadByDeviceId(long deviceId) {
      Device device = genericDAO.loadById(Device.class, deviceId);
      return device.getSensors();
   }
   
   public List<Sensor> loadSameSensors(Sensor sensor) {
      List<Sensor> result = null;
      DetachedCriteria critera = DetachedCriteria.forClass(Sensor.class);
      critera.add(Restrictions.eq("name", sensor.getName()));
      critera.add(Restrictions.eq("type", sensor.getType()));
      critera.add(Restrictions.eq("device.oid", sensor.getDevice().getOid()));
      result = genericDAO.findByDetachedCriteria(critera);
      
      if (result != null) {
         for(Iterator<Sensor> iterator=result.iterator();iterator.hasNext();) {
            Sensor s = iterator.next();
            if(!s.equalsWithoutCompareOid(sensor)){
               iterator.remove();
            }
         }
      }
      return result;
   }

    @Transactional
    public List<Sensor> saveAllSensors(List<Sensor> sensorList, Account account) {
        for (Sensor sensor : sensorList) {
          
          System.out.println("Saving sensor " + sensor);
          
            sensor.setAccount(account);
            genericDAO.save(sensor);
        }
        return sensorList;
    }
    
    @Transactional
    public void updateSensorWithDTO(SensorDetailsDTO sensor) {
      Sensor sensorBean = loadById(sensor.getOid());
      
      if (sensor.getType() != sensorBean.getType()) {
        throw new IllegalStateException("Sensor type cannot be changed on edit");
      }

      sensorBean.setName(sensor.getName());
      
      DeviceCommand deviceCommand = deviceCommandService.loadById(sensor.getCommand().getId());
      sensorBean.getSensorCommandRef().setDeviceCommand(deviceCommand);
      
      if (sensor.getType() == SensorType.RANGE) {
        ((RangeSensor)sensorBean).setMin(sensor.getMinValue());
        ((RangeSensor)sensorBean).setMax(sensor.getMaxValue());
     } else if (sensor.getType() == SensorType.CUSTOM) {
        CustomSensor customSensor = (CustomSensor)sensorBean;
        
        // MODELER-321: removing children from relationship does not delete them in JPA
        genericDAO.deleteAll(customSensor.getStates());
        
        List<State> states = new ArrayList<State>();
        for (Map.Entry<String,String> e : sensor.getStates().entrySet()) {
          State state = new State();
          state.setName(e.getKey());
          state.setValue(e.getValue());
          state.setSensor(customSensor);
          states.add(state);
        }
        customSensor.setStates(states);
     }
      updateSensor(sensorBean);
    }
}
