package org.openremote.modeler.domain;

import com.extjs.gxt.ui.client.data.BeanModelTag;

public class Template extends BusinessEntity implements BeanModelTag {
   private static final long serialVersionUID = -4719734393235222900L;
   
   public static final long PRIVATE = -1L;
   public static final long PUBLIC = 0L;
   
   private long shareTo = PRIVATE ;
   
   private String name = "";
   private String content = "";
//   private String model = "";
//   private String type = "";
//   private String vendor = "";
   private String keywords = "";
   private boolean shared = false;
   
   private ScreenPair screen;
   
   public Template(){}
   
   public Template(String name,ScreenPair screen){
      this.name = name;
      this.screen = screen;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getContent() {
      return content;
   }

   public void setContent(String content) {
      this.content = content;
   }

   
   /*public String getModel() {
      return model;
   }

   public void setModel(String model) {
      this.model = model;
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public String getVendor() {
      return vendor;
   }

   public void setVendor(String vendor) {
      this.vendor = vendor;
   }*/

   public String getKeywords() {
      return keywords;
   }

   public void setKeywords(String keywords) {
      this.keywords = keywords;
   }

   public boolean isShared() {
      return shared;
   }

   public void setShared(boolean shared) {
      this.shared = shared;
   }

   public ScreenPair getScreen() {
      return screen;
   }

   public void setScreen(ScreenPair screen) {
      this.screen = screen;
   }

   public long getShareTo() {
      return shareTo;
   }

   public void setShareTo(long shareTo) {
      this.shareTo = shareTo;
   }
   
   public String getDisplayName() {
      return name + " ( " + keywords +" )";
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      Template other = (Template) obj;
      if (content == null) {
         if (other.content != null) return false;
      } else if (!content.equals(other.content)) return false;
      if (keywords == null) {
         if (other.keywords != null) return false;
      } else if (!keywords.equals(other.keywords)) return false;
      if (name == null) {
         if (other.name != null) return false;
      } else if (!name.equals(other.name)) return false;
      if (shared != other.shared) return false;
      return true;
   }
   
   
}
