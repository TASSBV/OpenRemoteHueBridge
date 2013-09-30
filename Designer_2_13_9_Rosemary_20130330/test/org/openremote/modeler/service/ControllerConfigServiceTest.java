package org.openremote.modeler.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.openremote.modeler.SpringTestContext;
import org.openremote.modeler.domain.ConfigCategory;
import org.openremote.modeler.domain.ControllerConfig;
import org.openremote.modeler.utils.XmlParser;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ControllerConfigServiceTest {
   
   private ControllerConfigService configService = (ControllerConfigService) SpringTestContext.getInstance().getBean("controllerConfigService");
   private UserService userService = (UserService) SpringTestContext.getInstance().getBean("userService");
   private Set<ConfigCategory> categories = new HashSet<ConfigCategory>();
   private Set<ControllerConfig> configs = new HashSet<ControllerConfig>();
   
   @BeforeClass
   public void saveFromDefault(){
            
      userService.createUserAccount("test", "test", UserServiceTest.TEST_EMAIL);
      SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("test", "test"));
      XmlParser.initControllerConfig(categories, configs);
      configService.saveAll(configs);
      
   }
   @Test
   public void getAllConfigs(){
      Set<String> categoryNames= new HashSet<String>();
      for(ConfigCategory category : categories){
         categoryNames.add(category.getName());
      }
      Assert.assertTrue(categoryNames.size()>0);
      for(String categoryName : categoryNames){
         Collection<ControllerConfig> cfgs = configService.listAllConfigsByCategoryNameForAccount(categoryName, userService.getAccount());
         Assert.assertTrue(cfgs.size()>=1);
         for(ControllerConfig cfg : cfgs){
            Assert.assertNotNull(cfg.getName());
            Assert.assertNotNull(cfg.getValue());
            Assert.assertNotNull(cfg.getHint());
            Assert.assertNotNull(cfg.getValidation());
            Assert.assertNotNull(cfg.getOptions());
//            System.out.println(cfg);
         }
      }
   }
   @Test
   public void update(){
      String addStr = "...updated";
      Set<ControllerConfig> cfgs = configService.listAllConfigsByCategory("advance");
      Assert.assertTrue(cfgs.size()>0);
      for(ControllerConfig cfg : cfgs){
         if(cfg.getOptions().equals("")&&(addStr+cfg.getValue()).matches(cfg.getValidation())){
            cfg.setValue(cfg.getValue()+addStr);
         }
      }
      
      configService.saveAll(cfgs);
      
      Collection<ControllerConfig> cfgs2 = configService.listAllConfigsByCategory("advance");
      Assert.assertTrue(cfgs2.size()>0);
      for(ControllerConfig cfg : cfgs2){
         if(cfg.getValue().endsWith(addStr)){
            Assert.assertTrue(cfg.getValue().substring(0,cfg.getValue().indexOf(addStr)-1).matches(cfg.getValidation()));
         }
      }
   }
}
