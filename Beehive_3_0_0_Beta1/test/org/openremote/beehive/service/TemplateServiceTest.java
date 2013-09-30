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
package org.openremote.beehive.service;

import java.util.List;

import junit.framework.Assert;

import org.openremote.beehive.SpringTestContext;
import org.openremote.beehive.TemplateTestBase;
import org.openremote.beehive.api.dto.TemplateDTO;
import org.openremote.beehive.api.service.TemplateService;
import org.openremote.beehive.api.service.impl.GenericDAO;
import org.openremote.beehive.domain.Account;
import org.openremote.beehive.domain.Template;

public class TemplateServiceTest extends TemplateTestBase {

   private TemplateService service = (TemplateService) SpringTestContext.getInstance().getBean("templateService");
   
   private GenericDAO genericDAO = (GenericDAO) SpringTestContext.getInstance().getBean("genericDAO");
   
   public void testGetTemplatesByAccountOid() {
      List<TemplateDTO> templates = service.loadAllPrivateTemplatesByAccountOid(1L);
      Assert.assertEquals(2, templates.size());
      Assert.assertEquals("t1", templates.get(0).getName());
      Assert.assertEquals("t2", templates.get(1).getName());
   }
   
   public void testGetAllPublicTemplate() {
      List<TemplateDTO> templates = service.loadAllPublicTemplatesByAccountOid(1L);
      Assert.assertEquals(1, templates.size());
      Assert.assertEquals("t3", templates.get(0).getName());
      Assert.assertEquals("content", templates.get(0).getContent());
   }
   public void testSave() {
      Account a = genericDAO.getByMaxId(Account.class);
      Template t3 = new Template();
      t3.setAccount(a);
      t3.setName("t3");
      t3.setContent("content");
      a.addTemplate(t3);

      long templateOid = service.save(t3);
      Template t = genericDAO.loadById(Template.class, templateOid);
      assertEquals("t3", t.getName());
      assertEquals("content", t.getContent());
      assertEquals(a.getOid(), t.getAccount().getOid());
   }
   
   public void testRemove() {
      assertTrue(service.delete(1L));
   }

   public void testGetTemplatesByKeywordsAndPage() {
      int totalSize = 8;
      Account a = genericDAO.getByMaxId(Account.class);
      String[] keywords = { "one", "two,", "three", "four", "one,two,", "three,four", "two,one,four", "one,three,two" };
      for (int i = 0; i < totalSize; i++) {
         Template template = new Template();
         template.setKeywords(keywords[i]);
         template.setContent("content"+i);
         template.setName("template"+i);
         template.setShared(true);
         a.addTemplate(template);
         template.setAccount(a);
         service.save(template);
      }
      System.out.println(genericDAO.loadAll(Template.class).size());
      assertTrue(genericDAO.loadAll(Template.class).size()>=totalSize);
      List<TemplateDTO> page1Template = service.loadPublicTemplatesByKeywordsAndPage("", 0);
      assertTrue(page1Template.size()==TemplateService.TEMPLATE_SIZE_PER_PAGE);
      
      List<TemplateDTO> templateWithKeywordsOne = service.loadPublicTemplatesByKeywordsAndPage("one", 0);
      assertTrue(templateWithKeywordsOne.size()==4);
      for (TemplateDTO dto : templateWithKeywordsOne) {
         assertTrue(genericDAO.loadById(Template.class, dto.getOid()).getKeywords().contains("one"));
      }
      
      List<TemplateDTO> templateWithKeywordsTwo = service.loadPublicTemplatesByKeywordsAndPage("two", 0);
      assertTrue(templateWithKeywordsTwo.size()==4);
      for (TemplateDTO dto : templateWithKeywordsTwo) {
         assertTrue(genericDAO.loadById(Template.class, dto.getOid()).getKeywords().contains("two"));
      }
      
      List<TemplateDTO> templateWithKeywordsOneTwo = service.loadPublicTemplatesByKeywordsAndPage("one,two", 0);
      assertTrue(templateWithKeywordsOneTwo.size()==3);
      for (TemplateDTO dto : templateWithKeywordsOneTwo) {
         Template tplt = genericDAO.loadById(Template.class, dto.getOid());
         assertTrue(tplt.getKeywords().contains("one") && tplt.getKeywords().contains("two"));
      }
      
      List<TemplateDTO> templateWithKeywordsThreeFour = service.loadPublicTemplatesByKeywordsAndPage("three,four", 0);
      assertTrue(templateWithKeywordsThreeFour.size()==1);
      for (TemplateDTO dto : templateWithKeywordsThreeFour) {
         Template tplt = genericDAO.loadById(Template.class, dto.getOid());
         assertTrue(tplt.getKeywords().contains("three") && tplt.getKeywords().contains("four"));
      }
      
      List<TemplateDTO> templateWithKeywordsFourThree = service.loadPublicTemplatesByKeywordsAndPage("four,three", 0);
      assertTrue(templateWithKeywordsFourThree.size()==1);
      for (TemplateDTO dto : templateWithKeywordsFourThree) {
         Template tplt = genericDAO.loadById(Template.class, dto.getOid());
         assertTrue(tplt.getKeywords().contains("three") && tplt.getKeywords().contains("four"));
      }
   }
}
