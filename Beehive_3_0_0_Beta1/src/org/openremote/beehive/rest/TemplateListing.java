package org.openremote.beehive.rest;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.openremote.beehive.api.dto.TemplateDTO;

@XmlRootElement(name = "templates")
public class TemplateListing {
   
   private List<TemplateDTO> templates = new ArrayList<TemplateDTO>();

   public TemplateListing() {
   }

   public TemplateListing(List<TemplateDTO> templates) {
      this.templates.addAll(templates);
   }

   @XmlElement(name = "template")
   public List<TemplateDTO> getTemplates() {
      return templates;
   }

   public void setTemplates(List<TemplateDTO> templates) {
      this.templates = templates;
   }

}
