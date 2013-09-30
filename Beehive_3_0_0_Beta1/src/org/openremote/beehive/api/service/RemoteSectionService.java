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
package org.openremote.beehive.api.service;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.openremote.beehive.api.dto.ModelDTO;
import org.openremote.beehive.api.dto.RemoteSectionDTO;

/**
 * Business service for <code>RemoteSectionDTO</code>
 * 
 * @author allen.wei 2009-2-18
 */
public interface RemoteSectionService {
   /**
    * Exports a specified remote configuration section in a LIRC configuration file,not the whole file.
    * 
    * @param id
    *           the identifier of the persistent instance
    * @return the <code>File</code> to export
    */
   File exportFile(long id);

   /**
    * Exports a specified remote configuration section in a LIRC configuration file,not the whole file.
    * 
    * @param id
    *           the identifier of the persistent instance
    * @return text as String
    */
   String exportText(long id);

   /**
    * Exports a specified remote configuration section as text in a LIRC configuration file,not the whole file, this
    * will not lead to disk writes.
    * 
    * @param id
    *           the identifier of the persistent instance
    * @return InputStream
    */
   InputStream exportStream(long id);

   /**
    * Finds RemoteSectionDTOs according to <code>ModelDTO</code> id.
    * 
    * @param modelId
    *           ModelDTO id
    * @return a list of RemoteSectionDTOs
    */
   List<RemoteSectionDTO> findByModelId(long modelId);

   /**
    * Loads first <code>RemoteSectionDTO</code> in a <code>ModelDTO</code>.
    * 
    * @param modelId
    *           ModelDTO id
    * @return RemoteSectionDTO
    */
   RemoteSectionDTO loadFisrtRemoteSectionByModelId(long modelId);

   /**
    * load <code>RemoteSectionDTO</code> by id.
    * 
    * @param sectionId
    *           RemoteSectionDTO id
    * @return RemoteSectionDTO
    */
   RemoteSectionDTO loadSectionById(long sectionId);

   /**
    * Gets <code>ModelDTO</code> <code>RemoteSectionDTO</code> belongs to.
    * 
    * @param sectionId
    *           RemoteSectionDTO id
    * @return ModelDTO
    */
   ModelDTO loadModelById(long sectionId);
}
