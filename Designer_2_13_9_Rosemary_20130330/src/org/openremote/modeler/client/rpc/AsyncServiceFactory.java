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

package org.openremote.modeler.client.rpc;

import com.google.gwt.core.client.GWT;

/**
 * Factory of AsyncService. Lazy load all the Async RPC Service.
 * 
 * @author allen.wei
 */
public class AsyncServiceFactory {
   
   /**
    * Not be instantiated.
    */
   private AsyncServiceFactory() {
   }

   /** The device macro service async. */
   private static DeviceMacroRPCServiceAsync deviceMacroServiceAsync = null;
   
   /** The device service async. */
   private static DeviceRPCServiceAsync deviceServiceAsync = null;
   
   /** The device command service async. */
   private static DeviceCommandRPCServiceAsync deviceCommandServiceAsync = null;
   
   /** The utils rpc service async. */
   private static UtilsRPCServiceAsync utilsRPCServiceAsync = null;

   private static SwitchRPCServiceAsync switchRPCServiceAsync = null;
   
   private static SensorRPCServiceAsync sensorRPCServiceAsync = null;
   
   private static SliderRPCServiceAsync sliderRPCServiceAsync = null;
   
   private static ControllerConfigRPCServiceAsync controllerConfigPRCServiceAsync = null;
   
   private static ConfigCategoryRPCServiceAsync configCategoryRPCServiceAsync = null;
   
   private static ConfigurationRPCServiceAsync configurationRPCServiceAsync = null;
   
   private static TemplateRPCServiceAsync templateRPCServiceAsync = null;
   
   private static UserRPCServiceAsync userRPCServiceAsync = null;
      
   /**
    * Gets the device macro service async.
    * 
    * @return the device macro service async
    */
   public static DeviceMacroRPCServiceAsync getDeviceMacroServiceAsync() {
      if (deviceMacroServiceAsync == null) {
         deviceMacroServiceAsync = GWT.create(DeviceMacroRPCService.class);
      }
      return deviceMacroServiceAsync;
   }
   
   /**
    * Gets the device service async.
    * 
    * @return the device service async
    */
   public static DeviceRPCServiceAsync getDeviceServiceAsync() {
      if (deviceServiceAsync == null) {
         deviceServiceAsync = GWT.create(DeviceRPCService.class);
      }
      return deviceServiceAsync;
   }
   
   /**
    * Gets the device command service async.
    * 
    * @return the device command service async
    */
   public static DeviceCommandRPCServiceAsync getDeviceCommandServiceAsync() {
      if (deviceCommandServiceAsync == null) {
         deviceCommandServiceAsync = GWT.create(DeviceCommandRPCService.class);
      }
      return deviceCommandServiceAsync;
   }

   /**
    * Gets the utils rpc service async.
    * 
    * @return the utils rpc service async
    */
   public static UtilsRPCServiceAsync getUtilsRPCServiceAsync() {
      if (utilsRPCServiceAsync == null) {
         utilsRPCServiceAsync = GWT.create(UtilsRPCService.class);
      }
      return utilsRPCServiceAsync;
   }
   
   public static SwitchRPCServiceAsync getSwitchRPCServiceAsync() {
      if (switchRPCServiceAsync == null) {
         switchRPCServiceAsync = GWT.create(SwitchRPCService.class);
      }

      return switchRPCServiceAsync;
   }
   
   public static SensorRPCServiceAsync getSensorRPCServiceAsync() {
      if (sensorRPCServiceAsync == null) {
         sensorRPCServiceAsync = GWT.create(SensorRPCService.class);
      }
      return sensorRPCServiceAsync;
   }

   public static SliderRPCServiceAsync getSliderRPCServiceAsync() {
      if (sliderRPCServiceAsync == null) {
         sliderRPCServiceAsync = GWT.create(SliderRPCService.class);
      }
      return sliderRPCServiceAsync;
   }
   
   public static ControllerConfigRPCServiceAsync getControllerConfigPRCServiceAsync(){
      if(controllerConfigPRCServiceAsync == null){
         controllerConfigPRCServiceAsync = GWT.create(ControllerConfigRPCService.class);
      }
      
      return controllerConfigPRCServiceAsync;
   }
   
   public static ConfigCategoryRPCServiceAsync getConfigCategoryRPCServiceAsync(){
      if(configCategoryRPCServiceAsync == null){
         configCategoryRPCServiceAsync = GWT.create(ConfigCategoryRPCService.class);
      }
      
      return configCategoryRPCServiceAsync;
   }
   
   public static ConfigurationRPCServiceAsync getConfigurationRPCServiceAsync(){
      if(configurationRPCServiceAsync == null){
         configurationRPCServiceAsync = GWT.create(ConfigurationRPCService.class);
      }
      
      return configurationRPCServiceAsync;
   }
   
   public static TemplateRPCServiceAsync getTemplateRPCServiceAsync(){
      if(templateRPCServiceAsync == null){
         templateRPCServiceAsync = GWT.create(TemplateRPCService.class);
      }
      
      return templateRPCServiceAsync;
   }
   
   public static UserRPCServiceAsync getUserRPCServiceAsync(){
      if(userRPCServiceAsync == null){
         userRPCServiceAsync = GWT.create(UserRPCService.class);
      }
      
      return userRPCServiceAsync;
   }

}
