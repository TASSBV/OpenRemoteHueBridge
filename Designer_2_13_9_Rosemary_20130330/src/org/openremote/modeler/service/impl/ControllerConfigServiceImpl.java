/*
 * OpenRemote, the Home of the Digital Home.
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
package org.openremote.modeler.service.impl;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.ConfigCategory;
import org.openremote.modeler.domain.ControllerConfig;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.ControllerConfigService;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.shared.dto.ControllerConfigDTO;
import org.openremote.modeler.utils.XmlParser;
import org.springframework.transaction.annotation.Transactional;

public class ControllerConfigServiceImpl extends BaseAbstractService<ControllerConfig>
    implements ControllerConfigService
{

   private UserService userService = null;
   
   @SuppressWarnings("unchecked")
   @Override
   public Set<ControllerConfig>listAllConfigsByCategoryNameForAccount(String categoryName,Account account) {
      String hql = "select cfg from ControllerConfig cfg where cfg.category like ? and cfg.account.oid=?";
      Object[] args = new Object[]{categoryName,account.getOid()};
      List<ControllerConfig> configs = genericDAO.getHibernateTemplate().find(hql, args);
      Set<ControllerConfig> configSet = new LinkedHashSet<ControllerConfig>();
      configSet.addAll(configs);
      configSet.removeAll(listAllexpiredConfigs());
      initializeConfigs(configSet);
      return configSet;
   }

   @Override
   @Transactional
   public ControllerConfig update(ControllerConfig config) {
      ControllerConfig cfg = genericDAO.loadById(ControllerConfig.class, config.getOid());
      cfg.setCategory(config.getCategory());
      cfg.setHint(config.getHint());
      cfg.setName(config.getName());
      cfg.setValue(config.getValue());
      cfg.setValidation(config.getValidation());
      cfg.setOptions(config.getOptions());
      return config;
   }
   
   @Transactional
   public Set<ControllerConfig> saveAll(Set<ControllerConfig> configs) {
      Set<ControllerConfig> cfgs = new LinkedHashSet<ControllerConfig>();
      for (ControllerConfig cfg : configs) {
         if (cfg.getValue() == null) {
           cfg.setValue("");
         }
         if (cfg.getAccount() == null) {
            cfg.setAccount(userService.getAccount());
            genericDAO.save(cfg);
         } else {
            genericDAO.update(cfg);
         }
         cfgs.add(cfg);
      }
      initializeConfigs(configs);
      return configs;
   }

   @Override
   public Set<ControllerConfig> listAllConfigsByCategory(String categoryName) {
      Account account = userService.getAccount();
      return this.listAllConfigsByCategoryNameForAccount(categoryName, account);
   }

   @SuppressWarnings("unchecked")
   @Override
   public Set<ControllerConfig> listAllByAccount(Account account) {
      String hql = "select cfg from ControllerConfig cfg where cfg.account.oid=?";
      List<ControllerConfig> configs = genericDAO.getHibernateTemplate().find(hql, account.getOid());
      Set<ControllerConfig> configSet = new LinkedHashSet<ControllerConfig>();
      configSet.addAll(configs);
      initializeConfigs(configSet);
      return configSet;
   }

   @Override
   public Set<ControllerConfig> listAllConfigs() {
     Account account = userService.getAccount();
     return listAllByAccount(account);
   }

   @Override
   public Set<ConfigCategory> listAllCategory() {
      Set<ConfigCategory> categories = new LinkedHashSet<ConfigCategory>();
      Set<ControllerConfig> allDefaultConfigs = new LinkedHashSet<ControllerConfig>();
      XmlParser.initControllerConfig(categories, allDefaultConfigs);
      return categories;
   }
   @Override
   public Set<ControllerConfig> listMissedConfigsByCategoryName(String categoryName) {
      Set<ConfigCategory> categories = new HashSet<ConfigCategory>();
      Set<ControllerConfig> allDefaultConfigs = new HashSet<ControllerConfig>();
      XmlParser.initControllerConfig(categories, allDefaultConfigs);
      
      Set<ControllerConfig> unMissedConfigs = this.listAllConfigsByCategory(categoryName);
      Set<ControllerConfig> missedConfigs = new HashSet<ControllerConfig> ();
      for (ControllerConfig cfg : allDefaultConfigs) {
         if (cfg.getCategory().equals(categoryName) && !unMissedConfigs.contains(cfg)) {
            missedConfigs.add(cfg);
         }
      }
      return missedConfigs;
   }
   @Override
   public Set<ControllerConfig> listAllMissingConfigs() {
      Set<ConfigCategory> categories = new HashSet<ConfigCategory>();
      Set<ControllerConfig> allDefaultConfigs = new HashSet<ControllerConfig>();
      XmlParser.initControllerConfig(categories, allDefaultConfigs);
      
      Set<ControllerConfig> unMissedConfigs = this.listAllConfigs();
      Set<ControllerConfig> missedConfigs = new HashSet<ControllerConfig> ();
      for (ControllerConfig cfg : allDefaultConfigs) {
         if (!unMissedConfigs.contains(cfg)) {
            missedConfigs.add(cfg);
         }
      }
      return missedConfigs;
   }
   
   public Set<ControllerConfig> listAllexpiredConfigs() {
      Set<ControllerConfig> allDefaultConfigs = new HashSet<ControllerConfig>();
      XmlParser.initControllerConfig(new HashSet<ConfigCategory>(), allDefaultConfigs);
      Set<ControllerConfig> allSavedConfigs = this.listAllConfigs();
      Set<ControllerConfig> expiredConfigs = new HashSet<ControllerConfig>();
      for (ControllerConfig config: allSavedConfigs) {
         if (!allDefaultConfigs.contains(config)) {
            expiredConfigs.add(config);
         }
      }
      
      return expiredConfigs;
   }
   public void setUserService(UserService userService) {
      this.userService = userService;
   }
   private static void initializeConfigs(Set<ControllerConfig> configs){
      Set<ConfigCategory> categories = new HashSet<ConfigCategory>();
      Set<ControllerConfig> allDefaultConfigs = new HashSet<ControllerConfig>();
      XmlParser.initControllerConfig(categories, allDefaultConfigs);
      for(ControllerConfig cfg : configs){
         ControllerConfig oldCfg = cfg;
         for(ControllerConfig tmp: allDefaultConfigs){
            if(tmp.getName().equals(cfg.getName())&& tmp.getCategory().equals(cfg.getCategory())){
               oldCfg = tmp;
               break;
            }
         }
         cfg.setHint(oldCfg.getHint());
         cfg.setOptions(oldCfg.getOptions());
         cfg.setValidation(oldCfg.getValidation());
      }
   }
   
   
   @Transactional
   public Set<ControllerConfig> saveAllDTOs(HashSet<ControllerConfigDTO> configDTOs) {
     Set<ControllerConfig> configs = new HashSet<ControllerConfig>();
     for (ControllerConfigDTO dto : configDTOs) {
       ControllerConfig config;
       if (dto.getOid() != null && dto.getOid() != 0) {
         config = loadById(dto.getOid());
         updateControllerConfigWithDTO(config, dto);
         genericDAO.update(config);
       } else {
         config = new ControllerConfig();
         config.setAccount(userService.getAccount());
         updateControllerConfigWithDTO(config, dto);
         genericDAO.save(config); 
       }
       configs.add(config);
     }
     initializeConfigs(configs);
     return configs;
   }

  private void updateControllerConfigWithDTO(ControllerConfig config, ControllerConfigDTO dto) {
    config.setCategory(dto.getCategory());
    config.setName(dto.getName());
    config.setValue(dto.getValue());
    config.setHint(dto.getHint());
    config.setValidation(dto.getValidation());
    config.setOptions(dto.getOptions());
  }
   
}
