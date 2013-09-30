/* 
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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
package org.openremote.controller;

import java.util.HashMap;
import java.util.Map;

import org.openremote.controller.service.ServiceContext;

/**
 * Configuration class acts as a common superclass for various configuration segments. <p>
 *
 * Controller configuration can be set either by modifying local property files or by setting
 * configuration properties in the controller.xml file. If a configuration value is present in both
 * the controller.xml file and in a local configuration (text) file, then the controller.xml
 * configuration takes precedence. <p>
 *
 * If the controller.xml is created with OpenRemote Designer tool then the configuration values
 * are stored in the user's online account and distributed to the Controller when it is
 * synchronized with the online account. The online mode allows support/service personnel to modify
 * Controller configuration remotely.  <p>
 *
 * Note that the functionality described above requires that the subclasses use the configuration
 * value methods of this superclass.
 *
 * TODO:
 *  - this implementation still needs further re-structuring, see
 *    ORCJAVA-183 (http://jira.openremote.org/browse/ORCJAVA-183)
 *    ORCJAVA-193 (http://jira.openremote.org/browse/ORCJAVA-193)
 *
 *
 * @author <a href="mailto:juha@openremote.org>Juha Lindfors</a>
 *
 * @see ControllerConfiguration
 * @see RoundRobinConfiguration
 */
public abstract class Configuration
{

  // Class Members --------------------------------------------------------------------------------


  protected static Configuration updateWithControllerXMLConfiguration(Configuration config)
  {
    // TODO : remove dependency to deprecated API, see ORCJAVA-193
    Map<String, String> properties = ServiceContext.getDeployer().getConfigurationProperties();
    config.setConfigurationProperties(properties);

    return config;
  }


  // Instance Fields ------------------------------------------------------------------------------


  private Map<String, String> configurationProperties = new HashMap<String, String>();




  // Protected Methods ----------------------------------------------------------------------------


  public /* TODO */  void setConfigurationProperties(Map<String, String> configurationProperties)
  {
    if (configurationProperties == null)
    {
       return;
    }

    this.configurationProperties = new HashMap<String, String>(configurationProperties);
  }


  protected String preferAttrCustomValue(String attrName, String defaultValue)
  {
    return configurationProperties.containsKey(attrName) ?
        configurationProperties.get(attrName) : defaultValue;
  }

  protected boolean preferAttrCustomValue(String attrName, boolean defaultValue)
  {
    return configurationProperties.containsKey(attrName) ?
        Boolean.valueOf(configurationProperties.get(attrName)) : defaultValue;
  }

  protected int preferAttrCustomValue(String attrName, int defaultValue)
  {
    return configurationProperties.containsKey(attrName) ?
        Integer.valueOf(configurationProperties.get(attrName)) : defaultValue;
  }

  protected long preferAttrCustomValue(String attrName, long defaultValue)
  {
    return configurationProperties.containsKey(attrName) ?
        Long.valueOf(configurationProperties.get(attrName)) : defaultValue;
  }

  protected String[] preferAttrCustomValue(String attrName, String[] defaultValue)
  {
    return configurationProperties.containsKey(attrName) ?
        configurationProperties.get(attrName).split(",") : defaultValue;
  }



}
