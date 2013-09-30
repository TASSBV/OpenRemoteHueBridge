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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.openremote.beehive.api.dto.VendorDTO;
import org.openremote.beehive.api.service.VendorService;
import org.openremote.beehive.domain.Vendor;
import org.openremote.beehive.utils.FileUtil;

/**
 * {@inheritDoc}.
 * 
 * @author allen 2009-2-17
 */
public class VendorServiceImpl extends BaseAbstractService<Vendor> implements VendorService {

   /** The logger. */
   private static Logger logger = Logger.getLogger(VendorServiceImpl.class.getName());

   /**
    * {@inheritDoc }.
    * 
    * @return the list<VendorDTO>
    */
   public List<VendorDTO> loadAllVendors() {
      List<VendorDTO> vendorDTOs = new ArrayList<VendorDTO>();
      for (Vendor vendor : loadAll()) {
         VendorDTO vendorDTO = new VendorDTO();
         try {
            BeanUtils.copyProperties(vendorDTO, vendor);
         } catch (IllegalAccessException e) {
            logger.error("error occurs while BeanUtils.copyProperties(vendorDTO, vendor);");
         } catch (InvocationTargetException e) {
            logger.error("error occurs while BeanUtils.copyProperties(vendorDTO, vendor);");
         }
         vendorDTOs.add(vendorDTO);
      }
      return vendorDTOs;
   }

   /**
    * {@inheritDoc }.
    * 
    * @param vendorName
    *           the vendor name
    */
   public void deleteByName(String vendorName) {
      Vendor vendor = loadByName(vendorName);
      if (vendor != null) {
         genericDAO.delete(vendor);
      }
   }

   /**
    * {@inheritDoc }.
    * 
    * @param vendorName
    *           the vendor name
    */
   public void syncWith(File file) {
      if (file.isFile()) {
         return;
      }
      boolean isDeleted = !file.exists();
      String[] arr = FileUtil.splitPath(file);
      String vendorName = arr[arr.length - 1];
      if (vendorName.equals("ovara")) {
         return;
      }
      if (isDeleted) {
         return;
      } else {
         Vendor vendor = loadByName(vendorName);
         if (vendor == null) {
            Vendor newVendor = new Vendor();
            newVendor.setName(vendorName);
            genericDAO.save(newVendor);
         } else if (!vendorName.equals(vendor.getName())) {
            vendor.setName(vendorName);
            genericDAO.merge(vendor);
         }
      }

   }

   /**
    * {@inheritDoc }.
    * 
    * @param vendorName
    *           the vendor name
    */
   public Vendor loadByName(String vendorName) {
      return genericDAO.getByNonIdField(Vendor.class, "name", vendorName);
   }
}
