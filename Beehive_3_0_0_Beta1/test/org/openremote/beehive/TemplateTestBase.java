package org.openremote.beehive;

import org.jboss.resteasy.mock.MockHttpRequest;
import org.openremote.beehive.api.service.AccountService;
import org.openremote.beehive.api.service.impl.GenericDAO;
import org.openremote.beehive.domain.Account;
import org.openremote.beehive.domain.Icon;
import org.openremote.beehive.domain.Template;
import org.openremote.beehive.domain.User;

import com.sun.syndication.io.impl.Base64;

/**
 * Test base for template-related test
 * 
 * @author Dan Cong
 *
 */
public class TemplateTestBase extends TestBase {
   
   private GenericDAO genericDAO = (GenericDAO) SpringTestContext.getInstance().getBean("genericDAO");
   
   private AccountService accountService = (AccountService) SpringTestContext.getInstance().getBean("accountService");
   
   @Override
   protected void setUp() throws Exception {
      super.setUp();

      User user = new User();
      user.setUsername("dan");
      user.setPassword("cong");
      genericDAO.save(user);
      
      Account a = new Account();
      user.setAccount(a);
      Template t1 = new Template();
      t1.setAccount(a);
      t1.setName("t1");
      t1.setContent("content");
      a.addTemplate(t1);
      Template t2 = new Template();
      t2.setAccount(a);
      t2.setName("t2");
      t2.setContent("content");
      a.addTemplate(t2);
      Template t3 = new Template();
      t3.setAccount(a);
      t3.setName("t3");
      t3.setContent("content");
      t3.setShared(true);
      a.addTemplate(t3);
      accountService.save(a);
      
      Icon i = new Icon();
      i.setFileName("menu.png");
      i.setName("menu");
      genericDAO.save(i);
   }

   

   @Override
   protected void tearDown() throws Exception {
      super.tearDown();
      User u = genericDAO.getByNonIdField(User.class, "username", "dan");
      genericDAO.delete(u);
   }
   
   protected void addCredential(MockHttpRequest mockHttpRequest) {
      mockHttpRequest.header(Constant.HTTP_AUTH_HEADER_NAME, Constant.HTTP_BASIC_AUTH_HEADER_VALUE_PREFIX
            + Base64.encode("dan:cong"));
   }

}
