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

package org.openremote.modeler.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;

import flexjson.JSON;


/**
 * Business entity class for all JPA entities with the common property oid.
 * 
 * @author Dan 2009-2-6
 */
@MappedSuperclass
public abstract class BusinessEntity implements Serializable {

   /** The Constant serialVersionUID. */
   private static final long serialVersionUID = -4133577592315343274L;
   
   /** The oid. */
   private long oid;

   /**
    * Instantiates a new business entity.
    */
   public BusinessEntity() {
      super();
   }

   /**
    * Instantiates a new business entity.
    * 
    * @param oid the oid
    */
   public BusinessEntity(long oid) {
      super();
      this.oid = oid;
   }

   /**
    * Gets the oid.
    * 
    * @return the oid
    */
   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   public long getOid() {
      return oid;
   }

   /**
    * Sets the oid.
    * 
    * @param oid the new oid
    */
   public void setOid(long oid) {
      this.oid = oid;
   }

   
   // TODO EBR : following 2 methods must go
   
   
   /**
    * Gets the bean model.
    * 
    * @return the bean model
    */
   @Transient
   @JSON(include = false)
   public BeanModel getBeanModel() {
      BeanModelFactory beanModelFactory = BeanModelLookup.get().getFactory(getClass());
      return beanModelFactory.createModel(this);
   }

   /**
    * Creates the models.
    * 
    * @param list the list
    * 
    * @return the list< bean model>
    */
   public static List<BeanModel> createModels(Collection<? extends BusinessEntity> list) {
      List<BeanModel> models = new ArrayList<BeanModel>();
      for (BusinessEntity b : list) {
         models.add(b.getBeanModel());
      }
      return models;


   }
   
   /**
    * Gets the display name.
    * 
    * @return the display name
    */
   @Transient
   public String getDisplayName() {
      return "unKnown";
   }
}
