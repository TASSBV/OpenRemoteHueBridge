package org.openremote.modeler.service;

import java.util.Collection;
import java.util.List;

import org.openremote.modeler.SpringTestContext;
import org.openremote.modeler.client.Constants;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.Protocol;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.domain.SliderCommandRef;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SliderServiceTest {
   private SliderService sliderService = null;
   private DeviceCommandService deviceCommandService = null;
   private UserService userService = null;
   @BeforeClass
   public void setUp() {
      userService = (UserService)SpringTestContext.getInstance().getBean("userService");
      sliderService = (SliderService) SpringTestContext.getInstance().getBean("sliderService");
      
      userService.createUserAccount("test", "test", "test@email.com");
      SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("test", "test"));
      deviceCommandService = (DeviceCommandService) SpringTestContext.getInstance().getBean("deviceCommandService");
   }

   @Test
   public void testSaveSlider() {
      Slider slider = new Slider();
      Protocol protocol = new Protocol();
      protocol.setType(Constants.INFRARED_TYPE);

      DeviceCommand cmd = new DeviceCommand();
      cmd.setProtocol(protocol);
      cmd.setName("testLirc");
      deviceCommandService.save(cmd);

      slider.setName("testName");
      SliderCommandRef scf = new SliderCommandRef();
      scf.setSlider(slider);
      scf.setDeviceCommand(cmd);
      slider.setSetValueCmd(scf);
      slider.setAccount(userService.getAccount());
      
      Slider slider2 = new Slider();
      slider2.setName("testName2");
      slider2.setAccount(userService.getAccount());

      sliderService.save(slider);
      sliderService.save(slider2);

      Assert.assertEquals(slider.getOid(), 1);
      Assert.assertEquals(slider2.getOid(), 2);
      Slider sliderFromTable = sliderService.loadAll().get(0);
      Assert.assertEquals(sliderFromTable.getSetValueCmd().getDeviceCommand().getName(), "testLirc");
   }

   @Test(dependsOnMethods = "testSaveSlider")
   public void testUpdate() {
      List<Slider> sliders = sliderService.loadAll();
      Assert.assertEquals(sliders.size(), 2);
      
      Slider slider = sliders.get(0);
      slider.setName("testUpdate");
      sliderService.update(slider);

      for (Slider s : sliders) {
         if (s.getOid() == 1) {
            Assert.assertEquals(s.getName(), "testUpdate");
            break;
         }
      }
   }

   @Test(dependsOnMethods = "testSaveSlider")
   public void testLoadAll() {
      Collection<Slider> switchs = sliderService.loadAll();
      Assert.assertEquals(switchs.size(), 2);
   }

   @Test(dependsOnMethods = "testUpdate")
   public void testDelte() {
      Slider slider = new Slider();
      slider.setOid(1);
      sliderService.delete(1);
      Collection<Slider> switchs = sliderService.loadAll();
      Assert.assertEquals(switchs.size(), 1);
   }
}
