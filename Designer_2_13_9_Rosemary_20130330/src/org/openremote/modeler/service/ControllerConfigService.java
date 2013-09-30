package org.openremote.modeler.service;

import java.util.HashSet;
import java.util.Set;

import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.ConfigCategory;
import org.openremote.modeler.domain.ControllerConfig;
import org.openremote.modeler.shared.dto.ControllerConfigDTO;

public interface ControllerConfigService {
   
   public static final String CONTROLLER_CONFIG_XML_FILE = "controller-config-2.0-M7.xml";

   
   /**
    * Get all the configuration item under a category for a account. 
    * @param categoryName The name of category 
    * @param account The current account. 
    * @return all configuration item under a category. 
    */
   Set<ControllerConfig> listAllConfigsByCategoryNameForAccount(String categoryName,Account account);
   
   /**
    * Update a configuration . 
    * @param config The configuration you want to update
    * @return A configuration after being updated. 
    */
   ControllerConfig update(ControllerConfig config);
   
   /**
    * Save all the configuration 
    * We can use this method to create a controller configuration for a user.  
    * @param configs The configurations you want to save. 
    * @return The configuration s you have saved. 
    */
   Set<ControllerConfig> saveAll(Set<ControllerConfig> configs);
   
   /**
    * This method is used to get all missing configurations under a 
    * category. 
    * Sometime there are some new configurations may be added, 
    * but always all the configurations will be saved to user's 
    * account as soon as this user is created. Therefore there some 
    * configurations which are not saved to user if the default 
    * configurations is changed. 
    * @param categoryName
    * @return
    */
   Set<ControllerConfig> listMissedConfigsByCategoryName(String categoryName);
   
   /**
    * This method is used to get all missing controller configurations 
    * without caring about configuration's category.  
    * 
    * Sometime there are some new configurations may be added, 
    * but always all the configurations will be saved to user's 
    * account as soon as this user is created. Therefore there some 
    * configurations which are not saved to user if the default 
    * configurations is changed. 
    * @return
    */
   Set<ControllerConfig> listAllMissingConfigs();
   
   Set<ControllerConfig> listAllexpiredConfigs();
   
   Set<ControllerConfig> listAllConfigsByCategory(String categoryName);
   
   Set<ControllerConfig> listAllConfigs();
   
   Set<ControllerConfig> listAllByAccount(Account account);
   
   Set<ConfigCategory> listAllCategory();
   

   Set<ControllerConfig> saveAllDTOs(HashSet<ControllerConfigDTO> configDTOs);

}
