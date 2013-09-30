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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.openremote.beehive.api.dto.CodeDTO;
import org.openremote.beehive.api.service.CodeService;
import org.openremote.beehive.domain.Code;
import org.openremote.beehive.domain.RemoteSection;

/**
 * @author allen.wei 2009-2-18
 */
public class CodeServiceImpl extends BaseAbstractService<Code> implements CodeService {
   public List<CodeDTO> findByRemoteSectionId(long remoteSectionId) {
      List<CodeDTO> codeDTOs = new ArrayList<CodeDTO>();
      RemoteSection remoteSection = genericDAO.loadById(RemoteSection.class, remoteSectionId);
      for (Code code : remoteSection.getCodes()) {
         CodeDTO codeDTO = new CodeDTO();
         try {
            BeanUtils.copyProperties(codeDTO, code);
            codeDTO.setRemoteName(code.getRemoteSection().getRemoteOptions().get(0).getValue());
         } catch (IllegalAccessException e) {
            // TODO handle exception
            e.printStackTrace();
         } catch (InvocationTargetException e) {
            // TODO handle exception
            e.printStackTrace();
         }
         codeDTOs.add(codeDTO);
      }
      return codeDTOs;
   }
}
