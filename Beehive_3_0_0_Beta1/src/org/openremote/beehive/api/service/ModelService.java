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
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.openremote.beehive.api.dto.ModelDTO;
import org.openremote.beehive.domain.Model;

/**
 * Business service for <code>ModelDTO</code>.
 * 
 * @author allen.wei 2009-2-17
 */
public interface ModelService {
   
   /**
    * Gets all <code>ModelDTOs</code> belongs to certain <code>VendorDTO</code> according to it's id.
    * 
    * @param vendorName vendor name
    * 
    * @return list of ModelDTOs
    */
   List<ModelDTO> findModelsByVendorName(String vendorName);

   /**
    * Gets all <code>ModelDTOs</code> belongs to certain <code>VendorDTO</code> according to it's name.
    * 
    * @param vendorId vendor id
    * 
    * @return list of ModelDTOs
    */
   List<ModelDTO> findModelsByVendorId(long vendorId);

   /**
    * loads <code>ModelDTO</code> by <code>VendorDTO</code> name and <code>ModelDTO</code> name.
    * 
    * @param vendorName name of VendorDTO
    * @param modelName name of ModelDTO
    * 
    * @return ModelDTO
    */
   ModelDTO loadByVendorNameAndModelName(String vendorName, String modelName);

   /**
    * loads <code>ModelDTO</code> by id.
    * 
    * @param modelId the model id
    * 
    * @return the model dto
    */
   ModelDTO loadModelById(long modelId);

   /**
    * Allows to import a LIRC Configuration file.
    * 
    * @param fis FileInputStream of the LIRC Configuration file
    * @param vendorName its vendor name
    * @param modelName its model name
    */
   void add(FileInputStream fis, String vendorName, String modelName);

   /**
    * Allows to export the content text of a LIRC Configuration. This will NOT lead to disk writes.
    * 
    * @param id the target LIRC Configuration id
    * 
    * @return the content text
    */
   String exportText(long id);

   /**
    * Allows to export the <code>File</code> of a LIRC Configuration. This will lead to disk writes.
    * 
    * @param id the target LIRC Configuration id
    * 
    * @return the file
    */
   File exportFile(long id);

   /**
    * Allows to export the file of a LIRC Configuration. This will lead to disk writes.
    * 
    * @param id the target LIRC Configuration id
    * 
    * @return the file URL to be downloaded
    */
   String downloadFile(long id);

   /**
    * Allows to export the file of a LIRC Configuration. This will NOT lead to disk writes.
    * 
    * @param id the target LIRC Configuration id
    * 
    * @return the file OutputStream to be downloaded
    */
   InputStream exportStream(long id);
   
   /**
    * Count.
    * 
    * @return the models amount
    */
   int count();
   

   /**
    * Delete by name.
    * 
    * @param modelName the model name
    */
   void deleteByName(String modelName);
   
   /**
    * Merge.
    * 
    * @param fis the fis
    * @param id the id
    */
   void merge(FileInputStream fis, long id);
   
   /**
    * Checks if is file.
    * 
    * @param path the path
    * 
    * @return true, if is file
    */
   boolean isFile(String path);
   
   /**
    * Find by file name.
    * 
    * @param fileName the file name
    * 
    * @return the model
    */
   Model findByFileName(String fileName);
   
   /**
    * Sync with.
    * 
    * @param file the file
    */
   void syncWith(File file);
   
}