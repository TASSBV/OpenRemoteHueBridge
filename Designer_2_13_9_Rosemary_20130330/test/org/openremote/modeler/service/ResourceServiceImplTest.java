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
package org.openremote.modeler.service;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.openremote.modeler.SpringTestContext;
import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.domain.Absolute;
import org.openremote.modeler.domain.Cell;
import org.openremote.modeler.domain.CommandDelay;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroItem;
import org.openremote.modeler.domain.DeviceMacroRef;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.GroupRef;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.Protocol;
import org.openremote.modeler.domain.ProtocolAttr;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.ScreenPair;
import org.openremote.modeler.domain.ScreenPairRef;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorCommandRef;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.domain.SwitchCommandOffRef;
import org.openremote.modeler.domain.SwitchCommandOnRef;
import org.openremote.modeler.domain.SwitchSensorRef;
import org.openremote.modeler.domain.component.Gesture;
import org.openremote.modeler.domain.component.ImageSource;
import org.openremote.modeler.domain.component.Navigate;
import org.openremote.modeler.domain.component.UIButton;
import org.openremote.modeler.domain.component.UIGrid;
import org.openremote.modeler.domain.component.UIImage;
import org.openremote.modeler.domain.component.UILabel;
import org.openremote.modeler.domain.component.UISwitch;
import org.openremote.modeler.domain.component.UITabbar;
import org.openremote.modeler.domain.component.UITabbarItem;
import org.openremote.modeler.domain.component.Gesture.GestureType;
import org.openremote.modeler.domain.component.Navigate.ToLogicalType;
import org.openremote.modeler.service.impl.ResourceServiceImpl;
import org.openremote.modeler.utils.XmlParser;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


public class ResourceServiceImplTest {
   
   private static final Logger log = Logger.getLogger(ResourceServiceImplTest.class);
   private Configuration configuration;
   private ResourceServiceImpl resourceServiceImpl = null;
   private DeviceCommandService deviceCommandService;
   private DeviceMacroService deviceMacroService;
   private UserService userService;
   @BeforeClass
   public void setUp() {
      resourceServiceImpl = (ResourceServiceImpl) SpringTestContext.getInstance().getBean("resourceService");
      deviceCommandService = (DeviceCommandService) SpringTestContext.getInstance().getBean("deviceCommandService");
      deviceMacroService = (DeviceMacroService) SpringTestContext.getInstance().getBean("deviceMacroService");
      userService = (UserService) SpringTestContext.getInstance().getBean("userService");
      userService.createUserAccount("test", "test", "test");
      SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("test", "test"));
      /*------------xml validation-------------*/
      configuration = (Configuration) SpringTestContext.getInstance().getBean("configuration");
   }
   @Test
   public void testNopanel() {
      Collection<Panel> emptyPanel = new ArrayList<Panel>();
     outputPanelXML(emptyPanel);
   }
   @Test
   public void testPanelHasGroupScreenControl()throws Exception {
      List<ScreenPairRef> screenRefs = new ArrayList<ScreenPairRef>();
      List<GroupRef> groupRefs = new ArrayList<GroupRef>();
      List<Panel> panels = new ArrayList<Panel>();
      
      /*---------------widget-------------------*/
      UIButton absBtn = new UIButton();            //UIButton
      absBtn.setOid(IDUtil.nextID());
      absBtn.setName("abs_btn1");
      ImageSource defaultImage = new ImageSource("default.jpg");
      ImageSource pressedImage = new ImageSource("pressed.jpg");
      absBtn.setImage(defaultImage);
      absBtn.setPressImage(pressedImage);
      
      UILabel label = new UILabel(IDUtil.nextID());    // UILabel
      label.setText("testLabel");
      label.setColor("000fff000");
      
      UIButton gridBtn = new UIButton();
      gridBtn.setOid(IDUtil.nextID());
      gridBtn.setName("grid_btn1");
      
      Switch switchToggle = new Switch();
      switchToggle.setOid(IDUtil.nextID());
      Sensor sensor = new Sensor();
      sensor.setType(SensorType.SWITCH);
      sensor.setOid(IDUtil.nextID());
      sensor.setName("testSensro");
      SwitchSensorRef sensorRef = new SwitchSensorRef(switchToggle);
      sensorRef.setOid(IDUtil.nextID());
      sensorRef.setSensor(sensor);
      switchToggle.setSwitchSensorRef(sensorRef);
      label.setSensor(sensor);
      
      UISwitch absSwitch = new UISwitch();      //UISwitch
      absSwitch.setOid(IDUtil.nextID());
      ImageSource onImage = new ImageSource("on.jpg");
      ImageSource offImage = new ImageSource("off.jpg");
      absSwitch.setOnImage(onImage);
      absSwitch.setOffImage(offImage);
      absSwitch.setSwitchCommand(switchToggle);
      
      UISwitch gridSwitch = new UISwitch();
      gridSwitch.setOid(IDUtil.nextID());
      gridSwitch.setSwitchCommand(switchToggle); 
      
      UIImage uiImage = new UIImage(IDUtil.nextID()); //UIImage
      uiImage.setSensor(sensor);
      uiImage.setLabel(label);
         
      /*---------------widget-------------------*/
      
      
      /*---------------screen-------------------*/
      Screen screen1 = new Screen();
      screen1.setOid(IDUtil.nextID());
      screen1.setName("screen1");
      
      Screen screen2 = new Screen();
      screen2.setOid(IDUtil.nextID());
      screen2.setName("screen1");
      
      Absolute abs1 = new Absolute();
      abs1.setUiComponent(absBtn);
      Absolute abs2 = new Absolute();
      abs2.setUiComponent(absSwitch);
      
      UIGrid grid1 = new UIGrid(10,10,20,20,4,4);
      Cell c1 = new Cell();
      c1.setUiComponent(gridBtn);
      grid1.addCell(c1);
      UIGrid grid2 = new UIGrid(10,10,34,20,5,4);
      Cell c2 = new Cell();
      c2.setUiComponent(gridSwitch);
      grid2.addCell(c2);
      Cell uiImageCell = new Cell();
      uiImageCell.setUiComponent(uiImage);
      grid2.addCell(uiImageCell);
      
      Cell labelCell = new Cell();
      labelCell.setUiComponent(label);
      grid2.addCell(labelCell);
      
      screen1.addAbsolute(abs1);
      screen2.addAbsolute(abs2);
      
      screen1.addGrid(grid1);
      screen2.addGrid(grid2);
      
      ScreenPair screenPair1 = new ScreenPair();
      screenPair1.setOid(IDUtil.nextID());
      screenPair1.setPortraitScreen(screen1);
      
      ScreenPair screenPair2 = new ScreenPair();
      screenPair2.setOid(IDUtil.nextID());
      screenPair2.setPortraitScreen(screen2);
      
      screenRefs.add(new ScreenPairRef(screenPair1));
      screenRefs.add(new ScreenPairRef(screenPair2));
      /*---------------group-------------------*/
      Group group1 = new Group();
      group1.setOid(IDUtil.nextID());
      group1.setName("group1");
      group1.setScreenRefs(screenRefs);
      
      Group group2 = new Group();
      group2.setOid(IDUtil.nextID());
      group2.setName("group1");
      group2.setScreenRefs(screenRefs);
      
      groupRefs.add(new GroupRef(group1));
      groupRefs.add(new GroupRef(group2));
      /*---------------panel------------------*/
      Panel panel1 = new Panel();
      panel1.setOid(IDUtil.nextID());
      panel1.setGroupRefs(groupRefs);
      panel1.setGroupRefs(groupRefs);
      panel1.setName("panel1");
      
      Panel panel2 = new Panel();
      panel2.setOid(IDUtil.nextID());
      panel2.setGroupRefs(groupRefs);
      panel2.setGroupRefs(groupRefs);
      panel2.setName("panel2");
      
      panels.add(panel1);
      panels.add(panel2);
      outputPanelXML(panels);
   }
@Test
   public void testPanelTabbarWithNavigateToGroupAndScreen() {
      Collection<Panel> panelWithJustOneNavigate = new ArrayList<Panel>();
      Navigate nav = new Navigate();
      nav.setOid(IDUtil.nextID());
      nav.setToGroup(1L);
      nav.setToScreen(2L);
      UITabbarItem item = new UITabbarItem();
      item.setNavigate(nav);
      item.setName("navigate name");
      Panel p = new Panel();
      p.setName("panel has a navigate");
      List<UITabbarItem> items = new ArrayList<UITabbarItem>();
      items.add(item);
      UITabbar tabbar = new UITabbar();
      tabbar.setTabbarItems(items);
      p.setTabbar(tabbar);
      panelWithJustOneNavigate.add(p);
      outputPanelXML(panelWithJustOneNavigate);
   }
@Test
public void testScreenHasGesture() {
   Collection<Panel> panelWithJustOneNavigate = new ArrayList<Panel>();
   List<ScreenPairRef> screenRefs = new ArrayList<ScreenPairRef>();
   List<GroupRef> groupRefs = new ArrayList<GroupRef>();
   
   List<Gesture> gestures = new ArrayList<Gesture>();
   
   Navigate nav = new Navigate();
   nav.setOid(IDUtil.nextID());
   nav.setToGroup(1L);
   nav.setToScreen(2L);
   Gesture gesture = new Gesture();
   gesture.setNavigate(nav);
   gesture.setOid(IDUtil.nextID());
   gesture.setType(GestureType.swipe_bottom_to_top);
   
   gestures.add(gesture);
   
   Panel p = new Panel();
   p.setName("panel has a navigate");
   
   final Screen screen1 = new Screen();
   screen1.setOid(IDUtil.nextID());
   screen1.setName("screen1");
   screen1.setGestures(gestures);
   ScreenPair screenPair = new ScreenPair();
   screenPair.setOid(IDUtil.nextID());
   screenPair.setPortraitScreen(screen1);
   screenRefs.add(new ScreenPairRef(screenPair));
   
   Group group1 = new Group();
   group1.setOid(IDUtil.nextID());
   group1.setName("group1");
   group1.setScreenRefs(screenRefs);
   
   groupRefs.add(new GroupRef(group1));
   p.setGroupRefs(groupRefs);
   
   panelWithJustOneNavigate.add(p);
   outputPanelXML(panelWithJustOneNavigate);
   
   
}
   
@Test
   public void testPanelTabbarWithNavigateToLogical() {
      Collection<Panel> panelWithJustOneNavigate = new ArrayList<Panel>();
      Navigate nav = new Navigate();
      nav.setOid(IDUtil.nextID());
      nav.setToLogical(ToLogicalType.back);
      UITabbarItem item = new UITabbarItem();
      item.setNavigate(nav);
      item.setName("navigate name");
      Panel p = new Panel();
      p.setName("panel has a navigate");
      List<UITabbarItem> items = new ArrayList<UITabbarItem>();
      items.add(item);
      UITabbar tabbar = new UITabbar();
      tabbar.setTabbarItems(items);
      p.setTabbar(tabbar);
      panelWithJustOneNavigate.add(p);
      outputPanelXML(panelWithJustOneNavigate);
   }
   
@Test
   public void testPanelNavigateHasImage() {
      Collection<Panel> panelWithJustOneNavigate = new ArrayList<Panel>();
      Navigate nav = new Navigate();
      nav.setOid(IDUtil.nextID());
      nav.setToLogical(ToLogicalType.back);
      
      ImageSource image = new ImageSource();
      image.setSrc("http://finalist.cn/logo.ico");
      
      UITabbarItem item = new UITabbarItem();
      item.setImage(image);
      
      item.setNavigate(nav);
      item.setName("navigate name");
      Panel p = new Panel();
      p.setName("panel has a navigate");
      List<UITabbarItem> items = new ArrayList<UITabbarItem>();
      items.add(item);
      p.setTabbarItems(items);
      panelWithJustOneNavigate.add(p);
      outputPanelXML(panelWithJustOneNavigate);
   }
   
@Test
   public void testGroupNavigateHasImage() {
      Collection<Panel> panelWithJustOneNavigate = new ArrayList<Panel>();
      Navigate nav = new Navigate();
      nav.setOid(IDUtil.nextID());
      nav.setToLogical(ToLogicalType.back);
      
      ImageSource image = new ImageSource();
      image.setSrc("http://finalist.cn/logo.ico");
      
      UITabbarItem item = new UITabbarItem();
      item.setImage(image);
      
      item.setNavigate(nav);
      item.setName("navigate name");
      Panel p = new Panel();
      p.setName("panel has a navigate");
      List<UITabbarItem> items = new ArrayList<UITabbarItem>();
      items.add(item);
      
      Group group = new Group();
      group.setName("groupName");
      group.setOid(IDUtil.nextID());
      group.setTabbarItems(items);
      
      p.addGroupRef(new GroupRef(group));
      panelWithJustOneNavigate.add(p);
      outputPanelXML(panelWithJustOneNavigate);
   }
   
 @Test
   public void testScreenHasBackgrouond() {
      Collection<Panel> panel = new ArrayList<Panel>();
      Screen screen = new Screen();
      screen.setOid(IDUtil.nextID());
      
      screen.getBackground().setImageSource(new ImageSource("http://finalist.cn/logo.jpg"));
      
      Panel p = new Panel();
      p.setName("panel has a navigate");
      
      Group group = new Group();
      group.setName("groupName");
      group.setOid(IDUtil.nextID());
      ScreenPair screenPair = new ScreenPair();
      screenPair.setOid(IDUtil.nextID());
      screenPair.setPortraitScreen(screen);
      
      group.addScreenRef(new ScreenPairRef(screenPair));
      
      p.addGroupRef(new GroupRef(group));
      panel.add(p);
      outputPanelXML(panel);
   }
@Test
   public void testgetControllXMWithEmptyScreen() {
      List<Screen> screens = new ArrayList<Screen>();
      Screen screen = new Screen();
      screen.setOid(IDUtil.nextID());
      screen.setName("EmptyScreen");
      
      screens.add(screen);
      outputControllerXML(screens);
   }
   
@Test
   public void testGetControllerXMLWithButtonAndSwitchButNoCmd() {
      List<Screen> screens = new ArrayList<Screen>();
      Screen screen = new Screen();
      screen.setOid(IDUtil.nextID());
      screen.setName("screenWithButtonAndSwitch");
      
      UIButton absBtn = new UIButton();
      absBtn.setOid(IDUtil.nextID());
      absBtn.setName("abs_btn1");
      
      UIButton gridBtn = new UIButton();
      gridBtn.setOid(IDUtil.nextID());
      gridBtn.setName("grid_btn1");
      
      
      Switch switchToggle = new Switch();
      switchToggle.setOid(IDUtil.nextID());
      Sensor sensor = new Sensor();
      sensor.setType(SensorType.SWITCH);
      sensor.setOid(IDUtil.nextID());
      sensor.setName("testSensro");
      SwitchSensorRef sensorRef = new SwitchSensorRef(switchToggle);
      sensorRef.setOid(IDUtil.nextID());
      sensorRef.setSensor(sensor);
      switchToggle.setSwitchSensorRef(sensorRef);
      
      UISwitch absSwitch = new UISwitch();
      absSwitch.setOid(IDUtil.nextID());
      absSwitch.setSwitchCommand(switchToggle);
      
      UISwitch gridSwitch = new UISwitch();
      gridSwitch.setOid(IDUtil.nextID());
      gridSwitch.setSwitchCommand(switchToggle);
      Absolute abs1 = new Absolute();
      abs1.setOid(IDUtil.nextID());
      abs1.setUiComponent(absBtn);
      Absolute abs2 = new Absolute();
      abs2.setOid(IDUtil.nextID());
      abs2.setUiComponent(absSwitch);
      
      UIGrid grid1 = new UIGrid(10,10,20,20,4,4);
      grid1.setOid(IDUtil.nextID());
      Cell c1 = new Cell();
      c1.setUiComponent(gridBtn);
      grid1.addCell(c1);
      UIGrid grid2 = new UIGrid(10,10,34,20,5,4);
      grid2.setOid(IDUtil.nextID());
      Cell c2 = new Cell();
      c2.setUiComponent(gridSwitch);
      grid2.addCell(c2);
      
      screen.addAbsolute(abs1);
      screen.addAbsolute(abs2);
      screen.addGrid(grid1);
      screen.addGrid(grid2);
      
      screens.add(screen);
      outputControllerXML(screens);
   }
   
@Test
   public void testGetControllerXMLWithButtonAndSwitchJustHaveDeviceCommand() {
      
      Protocol protocol = new Protocol();
      protocol.setType(Constants.INFRARED_TYPE);
      
      DeviceCommand cmd = new DeviceCommand();
      cmd.setProtocol(protocol);
      cmd.setName("testLirc");
      //cmd.setOid(IDUtil.nextID());
      deviceCommandService.save(cmd);
      DeviceCommandRef cmdRef = new DeviceCommandRef(cmd);
      List<Screen> screens = new ArrayList<Screen>();
      Screen screen = new Screen();
      screen.setOid(IDUtil.nextID());
      screen.setName("screenWithButtonAndSwitch");
      
      UIButton absBtn = new UIButton();
      absBtn.setOid(IDUtil.nextID());
      absBtn.setName("abs_btn1");
      absBtn.setUiCommand(cmdRef);
      
      UIButton gridBtn = new UIButton();
      gridBtn.setOid(IDUtil.nextID());
      gridBtn.setName("grid_btn1");
      gridBtn.setUiCommand(cmdRef);
      
      UISwitch absSwitch = new UISwitch();
      absSwitch.setOid(IDUtil.nextID());
      
      Switch switchToggle = new Switch();
      
      SwitchCommandOnRef onCommand = new SwitchCommandOnRef();
      onCommand.setOnSwitch(switchToggle);
      onCommand.setDeviceCommand(cmd);
      SwitchCommandOffRef offCommand = new SwitchCommandOffRef();
      offCommand.setOffSwitch(switchToggle);
      offCommand.setDeviceCommand(cmd);
      switchToggle.setSwitchCommandOffRef(offCommand);
      switchToggle.setSwitchCommandOnRef(onCommand);
      
      absSwitch.setSwitchCommand(switchToggle);
      
      UISwitch gridSwitch = new UISwitch();
      gridSwitch.setOid(IDUtil.nextID());
      gridSwitch.setSwitchCommand(switchToggle);
      
      Absolute abs1 = new Absolute();
      abs1.setUiComponent(absBtn);
      Absolute abs2 = new Absolute();
      abs2.setUiComponent(absSwitch);
      
      UIGrid grid1 = new UIGrid(10,10,20,20,4,4);
      Cell c1 = new Cell();
      c1.setUiComponent(gridBtn);
      grid1.addCell(c1);
      UIGrid grid2 = new UIGrid(10,10,34,20,5,4);
      Cell c2 = new Cell();
      c2.setUiComponent(gridSwitch);
      grid2.addCell(c2);
      
      screen.addAbsolute(abs1);
      screen.addAbsolute(abs2);
      screen.addGrid(grid1);
      screen.addGrid(grid2);
      
      screens.add(screen);
      outputControllerXML(screens);
   }
@Test
public void testGetControllerXMLWithGestureHaveDeviceCommand() {
   
   Protocol protocol = new Protocol();
   protocol.setType(Constants.INFRARED_TYPE);
   
   DeviceCommand cmd = new DeviceCommand();
   cmd.setProtocol(protocol);
   cmd.setName("testLirc");
   deviceCommandService.save(cmd);
   DeviceCommandRef cmdRef = new DeviceCommandRef(cmd);
   List<Screen> screens = new ArrayList<Screen>();
   Screen screen = new Screen();
   screen.setOid(IDUtil.nextID());
   screen.setName("screenWithButtonAndSwitch");
   List<Gesture> gestures = new ArrayList<Gesture>();
   Gesture gesture = new Gesture();
   gesture.setOid(IDUtil.nextID());
   gesture.setUiCommand(cmdRef);
   gestures.add(gesture);
   screen.setGestures(gestures);
   screens.add(screen);
   outputControllerXML(screens);
}
   @Test
   public void testGetControllerXMLWithButtonAndSwitchHaveSensor() {
      
      Protocol protocol = new Protocol();
      protocol.setType(Constants.INFRARED_TYPE);
      List<ProtocolAttr> attrs = new ArrayList<ProtocolAttr>();
      ProtocolAttr attr1 = new ProtocolAttr();
      attr1.setName("command");
      attr1.setValue("\"nameValue");
      attr1.setProtocol(protocol);
      attrs.add(attr1);
     
      protocol.setAttributes(attrs);
      
      DeviceCommand cmd = new DeviceCommand();
      cmd.setProtocol(protocol);
      cmd.setName("testLirc");
      deviceCommandService.save(cmd);
      DeviceCommandRef cmdRef = new DeviceCommandRef(cmd);
      List<Screen> screens = new ArrayList<Screen>();
      Screen screen = new Screen();
      screen.setOid(IDUtil.nextID());
      screen.setName("screenWithButtonAndSwitch");
      
      UIButton absBtn = new UIButton();
      absBtn.setOid(IDUtil.nextID());
      absBtn.setName("abs_btn1");
      absBtn.setUiCommand(cmdRef);
      
      UIButton gridBtn = new UIButton();
      gridBtn.setOid(IDUtil.nextID());
      gridBtn.setName("grid_btn1");
      gridBtn.setUiCommand(cmdRef);
      
      UISwitch absSwitch = new UISwitch();
      absSwitch.setOid(IDUtil.nextID());
      
      Switch switchToggle = new Switch();
      SwitchCommandOnRef onCommand = new SwitchCommandOnRef();
      onCommand.setOnSwitch(switchToggle);
      onCommand.setDeviceCommand(cmd);
      SwitchCommandOffRef offCommand = new SwitchCommandOffRef();
      offCommand.setOffSwitch(switchToggle);
      offCommand.setDeviceCommand(cmd);
      switchToggle.setSwitchCommandOffRef(offCommand);
      switchToggle.setSwitchCommandOnRef(onCommand);
      
      Sensor sensor = new Sensor();
      sensor.setOid(IDUtil.nextID());
      sensor.setType(SensorType.SWITCH);
      sensor.setName("testSensor");
      SensorCommandRef sensorCmdRef = new SensorCommandRef();
      sensorCmdRef.setDeviceCommand(cmd);
      sensorCmdRef.setSensor(sensor);
      sensor.setSensorCommandRef(sensorCmdRef);
      SwitchSensorRef switchSensorRef = new SwitchSensorRef(switchToggle);
      switchSensorRef.setSensor(sensor);
      switchToggle.setSwitchSensorRef(switchSensorRef);
      
      absSwitch.setSwitchCommand(switchToggle);
      
      UISwitch gridSwitch = new UISwitch();
      gridSwitch.setOid(IDUtil.nextID());
      gridSwitch.setSwitchCommand(switchToggle);
      
      Absolute abs1 = new Absolute();
      abs1.setUiComponent(absBtn);
      Absolute abs2 = new Absolute();
      abs2.setUiComponent(absSwitch);
      
      UIGrid grid1 = new UIGrid(10,10,20,20,4,4);
      Cell c1 = new Cell();
      c1.setUiComponent(gridBtn);
      grid1.addCell(c1);
      UIGrid grid2 = new UIGrid(10,10,34,20,5,4);
      Cell c2 = new Cell();
      c2.setUiComponent(gridSwitch);
      grid2.addCell(c2);
      
      screen.addAbsolute(abs1);
      screen.addAbsolute(abs2);
      screen.addGrid(grid1);
      screen.addGrid(grid2);
      
      screens.add(screen);
      outputControllerXML(screens);
   }
   
   /*
    * The case has some problem because of LazyInitializationException 
    */
// @Test
   public void testGetControllerXMLWithButtonAndSwitchHaveMacro() {
/*      
      Account account = new Account();
      account.setOid(5);
      
      User u = new User();
      u.setAccount(account);
      u.setPassword("");
      u.setUsername("sa");
      List<Role> roles = new ArrayList<Role>();
      roles.add(Role.ROLE_DESIGNER);
      roles.add(Role.ROLE_MODELER);
      u.setRoles(Role.ROLE_MODELER);
//      u.setOid(4);
      userService.saveUser(u);*/
      
      userService.createUserAccount("testMacro", "testMacro", "test");
      
      DeviceMacro deviceMacro = new DeviceMacro();
      deviceMacro.setName("testMacro");
//      deviceMacro.setOid(6);
//      deviceMacro.setAccount(account);
      
      DeviceMacroItem item1 = new CommandDelay("1000");
//      item1.setOid(7);
      item1.setParentDeviceMacro(deviceMacro);
      
      Protocol protocol = new Protocol();
      protocol.setType(Constants.INFRARED_TYPE);
      
      DeviceCommand cmd = new DeviceCommand();
      cmd.setProtocol(protocol);
      cmd.setName("testLirc");
//      cmd.setOid(4);
      deviceCommandService.save(cmd);
      
      DeviceMacroItem item2 = new DeviceCommandRef(cmd);
//      item2.setOid(8);
      item2.setParentDeviceMacro(deviceMacro);
      
      List<DeviceMacroItem> items = new ArrayList<DeviceMacroItem>();
      items.add(item1);
      items.add(item2);
      
      deviceMacro.setDeviceMacroItems(items);
      
      DeviceMacroRef macroRef = new DeviceMacroRef(deviceMacro);
//      macroRef.setOid(9);
      
      SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("testMacro", "testMacro"));
      deviceMacroService.saveDeviceMacro(deviceMacro);
      
     /* Protocol protocol = new Protocol();
      protocol.setType(Constants.INFRARED_TYPE);
      
      DeviceCommand cmd1 = new DeviceCommand();
      cmd1.setProtocol(protocol);
      cmd1.setName("testLirc");
      cmd1.setOid(4);
      
      DeviceCommand cmd2 = new DeviceCommand();
      cmd2.setProtocol(protocol);
      cmd2.setName("testLirc");
      cmd2.setOid(5);
      
      deviceCommandService.save(cmd1);
      deviceCommandService.save(cmd2);*/
      
      
      
//      DeviceCommandRef cmdRef = new DeviceCommandRef(cmd1);
      
      List<Screen> screens = new ArrayList<Screen>();
      Screen screen = new Screen();
      screen.setOid(IDUtil.nextID());
      screen.setName("screenWithButtonAndSwitch");
      
      UIButton absBtn = new UIButton();
      absBtn.setOid(IDUtil.nextID());
      absBtn.setName("abs_btn1");
      absBtn.setUiCommand(macroRef);
      
      UIButton gridBtn = new UIButton();
      gridBtn.setOid(IDUtil.nextID());
      gridBtn.setName("grid_btn1");
      gridBtn.setUiCommand(macroRef);
      
      UISwitch absSwitch = new UISwitch();
      absSwitch.setOid(IDUtil.nextID());
//      absSwitch.setOnCommand(macroRef);
//      absSwitch.setOffCommand(macroRef);
//      absSwitch.setStatusCommand(macroRef);
      
      UISwitch gridSwitch = new UISwitch();
      gridSwitch.setOid(IDUtil.nextID());
//      gridSwitch.setOnCommand(macroRef);
//      gridSwitch.setOffCommand(macroRef);
//      gridSwitch.setStatusCommand(macroRef);
      
      Absolute abs1 = new Absolute();
      abs1.setUiComponent(absBtn);
      Absolute abs2 = new Absolute();
      abs2.setUiComponent(absSwitch);
      
      UIGrid grid1 = new UIGrid(10,10,20,20,4,4);
      Cell c1 = new Cell();
      c1.setUiComponent(gridBtn);
      grid1.addCell(c1);
      UIGrid grid2 = new UIGrid(10,10,34,20,5,4);
      Cell c2 = new Cell();
      c2.setUiComponent(gridSwitch);
      grid2.addCell(c2);
      
      screen.addAbsolute(abs1);
      screen.addAbsolute(abs2);
      screen.addGrid(grid1);
      screen.addGrid(grid2);
      
      screens.add(screen);
      outputControllerXML(screens);
   }
   private void outputPanelXML(Collection<Panel> panels) {
      try {
         System.out.println(XmlParser.validateAndOutputXML(new File(getClass().getResource(
               configuration.getPanelXsdPath()).getPath()), resourceServiceImpl.getPanelXML(panels)));
      } catch (Exception e) {
         log.error("Can not output panel xml", e);
         fail();
      }
   }
   
   private void outputControllerXML(Collection<Screen> screens) {
      try {
         System.out.println(XmlParser.validateAndOutputXML(new File(getClass().getResource(
               configuration.getControllerXsdPath()).getPath()), resourceServiceImpl.getControllerXML(screens,IDUtil.nextID())));
      } catch (Exception e) {
         log.error("Can not output controller xml", e);
         fail();
      }
   }
}
