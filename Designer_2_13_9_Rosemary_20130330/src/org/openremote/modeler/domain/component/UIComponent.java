/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2012, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.openremote.modeler.domain.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Transient;

import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.domain.BusinessEntity;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.BeanModelTag;

import flexjson.JSON;

/**
 * parent for UIButton,UISwich,UIGrid...
 * 
 * @author Javen
 * 
 */

// TODO EBR : remove BeanModelTag when appropriate

public abstract class UIComponent extends BusinessEntity implements BeanModelTag {

  private static final long serialVersionUID = -2311643498267814551L;
  
  private transient boolean removed = false;

   public UIComponent() {
   }

   public UIComponent(long id) {
      super(id);
   }

   public String getName() {
      return "UIComponent";
   }

   public boolean isRemoved() {
      return removed;
   }

   public void setRemoved(boolean removed) {
      this.removed = removed;
   }

   /**
    * Generate the xml content which used in panel.xml
    */
   @Transient
   @JSON(include=false)
   public abstract String getPanelXml();
   
   public int getPreferredWidth() {
      return 50;
   }

   public int getPreferredHeight() {
      return 50;
   }

   /**
    * create a new UIComponet with the same type of <b>uiComponent</b>
    * 
    * @param uiComponent
    * @return a new UIComponet with the same type of <b>uiComponent</b>
    */
   public static UIComponent createNew(UIComponent uiComponent) {
      UIComponent result = null;
      if (uiComponent != null) {
         if (uiComponent instanceof UIButton) {
            result = new UIButton();
         } else if (uiComponent instanceof UISwitch) {
            result = new UISwitch();
         } else if (uiComponent instanceof UISlider) {
            result = new UISlider();
         } else if (uiComponent instanceof UILabel) {
            result = new UILabel();
         } else if (uiComponent instanceof UIImage) {
            result = new UIImage();
         } else if (uiComponent instanceof UITabbar) {
            return new UITabbar();
         }
      }
      result.setOid(IDUtil.nextID());
      return result;
   }

   /**
    * create a new UIComponet with the same type and the same attribute of <b>uiComponent</b>
    * 
    * @param uiComponent
    * @return a new UIComponet with the same type and the same attribute of <b>uiComponent</b>
    */
   public static UIComponent copy(UIComponent uiComponent) {
      if (uiComponent != null) {
         if (uiComponent instanceof UIButton) {
            return new UIButton((UIButton) uiComponent);
         } else if (uiComponent instanceof UISwitch) {
            return new UISwitch((UISwitch) uiComponent);
         } else if (uiComponent instanceof UISlider) {
            return new UISlider((UISlider) uiComponent);
         } else if (uiComponent instanceof UILabel) {
            return new UILabel((UILabel) uiComponent);
         } else if (uiComponent instanceof UIImage) {
            return new UIImage((UIImage) uiComponent);
         }  else if (uiComponent instanceof UITabbar) {
            return new UITabbar((UITabbar)uiComponent);
         }
      }
      return null;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      UIComponent other = (UIComponent) obj;
      return other.getPanelXml().equals(getPanelXml());
   }

   @Override
   public int hashCode() {
      return (int) getOid();
   }
   
   
   
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
   public static List<BeanModel> createModels(Collection<? extends UIComponent> list) {
      List<BeanModel> models = new ArrayList<BeanModel>();
      for (UIComponent b : list) {
         models.add(b.getBeanModel());
      }
      return models;


   }
   


}
