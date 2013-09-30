package org.openremote.modeler.service;

import org.openremote.modeler.SpringTestContext;
import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.domain.Absolute;
import org.openremote.modeler.domain.Cell;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.ScreenPair;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.domain.Template;
import org.openremote.modeler.domain.UICommand;
import org.openremote.modeler.domain.component.Gesture;
import org.openremote.modeler.domain.component.UIButton;
import org.openremote.modeler.domain.component.UIGrid;
import org.openremote.modeler.domain.component.UISwitch;
import org.openremote.modeler.service.impl.UserServiceImpl;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;

public class TemplateServiceTest {
   private TemplateService templateService = null;
   private UserServiceImpl userServiceImpl = null;

   @BeforeClass
   public void setUp() {
      this.templateService = (TemplateService) SpringTestContext.getInstance().getBean("templateService");
      this.userServiceImpl = (UserServiceImpl) SpringTestContext.getInstance().getBean("userService");

      /*
       * initialize user information :
       */
      userServiceImpl.createUserAccount("test", "test", "test");
      SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("test", "test"));
   }

//   @Test
   public void getJson4EmptyScreen() {
      Screen screen = new Screen();
      screen.setOid(IDUtil.nextID());
      ScreenPair screenPair = new ScreenPair();
      screenPair.setOid(IDUtil.nextID());
      screenPair.setPortraitScreen(screen);
      Template template = new Template("emptyScreen", screenPair);
      Template t2 = templateService.saveTemplate(template);
      System.out.println(t2.getContent());
   }

//   @Test
   public void getJosn4ScreenHasOneButton() {

      Screen screen = new Screen();
      screen.setOid(IDUtil.nextID());
      ScreenPair screenPair = new ScreenPair();
      screenPair.setOid(IDUtil.nextID());
      screenPair.setPortraitScreen(screen);
      Template template = new Template("screenHasButton", screenPair);

      UIButton btn = new UIButton();
      btn.setOid(IDUtil.nextID());

      Absolute absolute = new Absolute();
      absolute.setUiComponent(btn);
      screen.addAbsolute(absolute);
      templateService.saveTemplate(template);
      System.out.println(template.getContent());
   }

//   @Test
   public void testGetScreenFromTemplate() {
      Screen screen = new Screen();
      screen.setOid(IDUtil.nextID());
      ScreenPair screenPair = new ScreenPair();
      screenPair.setOid(IDUtil.nextID());
      screenPair.setPortraitScreen(screen);
      Template template = new Template("screenHasButton", screenPair);

      UIButton btn = new UIButton();
      btn.setOid(IDUtil.nextID());
      btn.setUiCommand(new UICommand());
      UISwitch uiSwitch = new UISwitch();
      uiSwitch.setOid(IDUtil.nextID());
      uiSwitch.setSwitchCommand(new Switch());
      Absolute absolute = new Absolute();
      absolute.setUiComponent(btn);

      Cell cell = new Cell();
      cell.setOid(IDUtil.nextID());
      cell.setUiComponent(uiSwitch);
      UIGrid grid = new UIGrid(10, 10, 320, 240, 4, 4);
      grid.setOid(IDUtil.nextID());
      grid.addCell(cell);

      screen.addAbsolute(absolute);
      screen.addGrid(grid);

      Gesture gesture = new Gesture();
      gesture.setOid(IDUtil.nextID());
      gesture.setUiCommand(new UICommand());
      gesture.getCommands().add(new UICommand());
      screen.addGesture(gesture);

      templateService.saveTemplate(template);
      System.out.println(template.getContent());
      ScreenPair screenPair2 = templateService.buildScreen(template);
      Screen screen2 = screenPair2.getPortraitScreen();
      
      Assert.assertTrue(screen2.getGrids().size() == 1);
      Assert.assertTrue(screen.getGrid(0).getCells().get(0).getUiComponent().getClass() == UISwitch.class);
      Assert.assertTrue(screen2.getAbsolutes().size() == 1);
      Assert.assertTrue(screen2.getAbsolutes().get(0).getUiComponent() instanceof UIButton);
   }

//   @Test
   public void testSaveTemplate() {
      Screen screen = new Screen();
      screen.setOid(IDUtil.nextID());
      ScreenPair screenPair = new ScreenPair();
      screenPair.setOid(IDUtil.nextID());
      screenPair.setPortraitScreen(screen);
      Template template = new Template("screenHasButton", screenPair);

      UIButton btn = new UIButton();
      btn.setOid(IDUtil.nextID());
      btn.setUiCommand(new UICommand());
      UISwitch uiSwitch = new UISwitch();
      uiSwitch.setOid(IDUtil.nextID());
      uiSwitch.setSwitchCommand(new Switch());
      Absolute absolute = new Absolute();
      absolute.setUiComponent(btn);

      Cell cell = new Cell();
      cell.setOid(IDUtil.nextID());
      cell.setUiComponent(uiSwitch);
      UIGrid grid = new UIGrid(10, 10, 320, 240, 4, 4);
      grid.setOid(IDUtil.nextID());
      grid.addCell(cell);

      screen.addAbsolute(absolute);
      screen.addGrid(grid);

      Gesture gesture = new Gesture();
      gesture.setOid(IDUtil.nextID());
      gesture.setUiCommand(new UICommand());
      gesture.getCommands().add(new UICommand());
      screen.addGesture(gesture);

      templateService.saveTemplate(template);
      System.out.println(template.getContent());
   }
}
