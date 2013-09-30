/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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
package org.openremote.beehive.api.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.openremote.beehive.Configuration;
import org.openremote.beehive.api.dto.IconDTO;
import org.openremote.beehive.api.service.IconService;
import org.openremote.beehive.domain.Icon;
import org.openremote.beehive.utils.StringUtil;
import org.springframework.beans.BeanUtils;

/**
 * @author Tomsky
 *
 */
public class IconServiceImpl extends BaseAbstractService<Icon> implements IconService {
   
   private Configuration configuration;

   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }

   /**
    * {@inheritDoc}
    * 
    */
   @Override
   public List<IconDTO> findIconsByName(String name) {
      DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Icon.class);
      detachedCriteria.add(Restrictions.eq("name", name.toLowerCase()));
      List<Icon> icons = genericDAO.findByDetachedCriteria(detachedCriteria);
      List<IconDTO> iconDTOList = new ArrayList<IconDTO>();
      String iconDir = configuration.getIconsDir();
      for (Icon icon : icons) {
         IconDTO iconDTO = new IconDTO();
         BeanUtils.copyProperties(icon, iconDTO);
         iconDTO.setFileName(iconDir + icon.getFileName());
         iconDTOList.add(iconDTO);
      }
      return iconDTOList;
   }

   /**
    * {@inheritDoc}
    * 
    */
   @Override
   public List<IconDTO> loadAllIcons() {
      List<Icon> icons = genericDAO.loadAll(Icon.class);
      List<IconDTO> iconDTOList = new ArrayList<IconDTO>();
      String iconDir = configuration.getIconsDir();
      for (Icon icon : icons) {
         IconDTO iconDTO = new IconDTO();
         BeanUtils.copyProperties(icon, iconDTO);
         iconDTO.setFileName(StringUtil.appendFileSeparator(iconDir) + icon.getFileName());
         iconDTOList.add(iconDTO);
      }
      return iconDTOList;
   }

}
