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

import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.SwitchService;
import org.openremote.modeler.service.UserService;
import org.springframework.transaction.annotation.Transactional;

public class SwitchServiceImpl extends BaseAbstractService<Switch> implements SwitchService {
   private UserService userService = null;

   @Override
   public Switch loadById(long id) {
     return genericDAO.getById(Switch.class, id);
   }
   
   @Override
   @Transactional
   public void delete(long id) {
      Switch switchToggle = genericDAO.loadById(Switch.class, id);
      genericDAO.delete(switchToggle);
   }

   @Override
   public List<Switch> loadAll() {
      List<Switch> result = userService.getAccount().getSwitches();
      if (result == null || result.size() == 0) {
         return new ArrayList<Switch> ();
      }
      Hibernate.initialize(result);
      return result;
   }


   @Override
   @Transactional
   public Switch save(Switch switchToggle) {
      genericDAO.save(switchToggle);
      if (switchToggle.getSwitchSensorRef() != null) {
         Hibernate.initialize(switchToggle.getSwitchSensorRef().getSensor());
      }
      return switchToggle;
   }

   @Override
   @Transactional
   public Switch update(Switch switchToggle) {
     genericDAO.saveOrUpdate(switchToggle);
     return switchToggle;
     /*
      Switch old = genericDAO.loadById(Switch.class, switchToggle.getOid());
      old.setName(switchToggle.getName());
      if (switchToggle.getSwitchCommandOffRef() != null
            && old.getSwitchCommandOffRef().getOid() != switchToggle.getSwitchCommandOffRef().getOid()) {
         genericDAO.delete(old.getSwitchCommandOffRef());
         old.setSwitchCommandOffRef(switchToggle.getSwitchCommandOffRef());
         switchToggle.getSwitchCommandOffRef().setOffSwitch(old);
      }
      if (switchToggle.getSwitchCommandOnRef() != null
            && old.getSwitchCommandOnRef().getOid() != switchToggle.getSwitchCommandOnRef().getOid()) {
         genericDAO.delete(old.getSwitchCommandOnRef());
         old.setSwitchCommandOnRef(switchToggle.getSwitchCommandOnRef());
         switchToggle.getSwitchCommandOnRef().setOnSwitch(old);
      }
      if (old.getSwitchSensorRef() != null
            && old.getSwitchSensorRef().getOid() != switchToggle.getSwitchSensorRef().getOid()) {
         genericDAO.delete(old.getSwitchSensorRef());
         old.setSwitchSensorRef(switchToggle.getSwitchSensorRef());
         switchToggle.getSwitchSensorRef().setSwitchToggle(old);
      }
      return old;
      */
   }
   
   public UserService getUserService() {
      return userService;
   }

   public void setUserService(UserService userService) {
      this.userService = userService;
   }
   
   public List<Switch> loadSameSwitchs(Switch swh) {
      List<Switch> result = null;
      DetachedCriteria critera = DetachedCriteria.forClass(Switch.class);
      critera.add(Restrictions.eq("device.oid", swh.getDevice().getOid()));
      critera.add(Restrictions.eq("name", swh.getName()));
      result = genericDAO.findByDetachedCriteria(critera);
      if (result != null) {
         for(Iterator<Switch> iterator = result.iterator();iterator.hasNext();) {
            Switch tmp = iterator.next();
            if (! tmp.equalsWithoutCompareOid(swh)) {
               iterator.remove();
            }
         }
      }
      return result;
   }
}
