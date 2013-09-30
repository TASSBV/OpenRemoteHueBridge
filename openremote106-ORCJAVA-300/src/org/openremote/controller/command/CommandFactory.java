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
package org.openremote.controller.command;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.openremote.controller.Constants;
import org.openremote.controller.exception.ConfigurationException;
import org.openremote.controller.service.Deployer;
import org.openremote.controller.utils.Logger;


/**
 * TODO:
 *
 *   - ORCJAVA-209 : merge command models -- expecting this implementation to be further
 *                   reduced or removed altogether as part of 209 refactoring
 *
 *
 * @author <a href="mailto:juha@openremote.org>Juha Lindfors</a>
 * @author Handy.Wang 2009-10-13
 */
public class CommandFactory
{

  public static String COMMANDBUILDER_CONFIG_KEY_PREFIX = "protocol.";
  public static String COMMANDBUILDER_CONFIG_KEY_SUFFIX = ".classname";
  
  /**
   * Common log category for startup logging, with a specific sub-category for deployer.
   */
  protected final static Logger log = Logger.getLogger(Constants.DEPLOYER_LOG_CATEGORY);

  
  private Map<String, CommandBuilder> commandBuilders = new HashMap<String, CommandBuilder>();

  public CommandFactory(Map<String, CommandBuilder> commandBuilders)
  {
   if (commandBuilders == null)
   {
     return;
   }

   this.commandBuilders = commandBuilders;
  }

  
  /**
   * This method is called from the ModelBuilder before the model is builded.<br>
   * Based on the given properties map, the command factory will update it's commandBuilder map<br>
   * and created any new commandBuilders that were configured inside controller.xml file.<p>
   * If the commandBuilder has a ctor which takes a deployer, the deployer reference is passed<br>
   * into the commandBuilder.<p>
   * The config in controller.xml must look like this:
   * <pre>
   * {@code
   *  <property name="protocol.zwave.classname" value="org.openremote.controller.protocol.zwave.ZWaveCommandBuilder" />
   * }</pre>
   * The property name has to begin with "protocol." and end with ".classname". Text text between is taken as protocol name.
   * <p>
   * @param map - the maps contains properties coming from controller.xml
   * @param deployer - a reference to the deployer 
   */
  @SuppressWarnings("unchecked")
  public void updateCommandBuilders(Map<String, String> map, Deployer deployer) {
     for (Entry<String, String> configEntry : map.entrySet()) {
        if (configEntry.getKey().startsWith(COMMANDBUILDER_CONFIG_KEY_PREFIX) && configEntry.getKey().endsWith(COMMANDBUILDER_CONFIG_KEY_SUFFIX)) {
           String protocolName = configEntry.getKey().substring(COMMANDBUILDER_CONFIG_KEY_PREFIX.length(), configEntry.getKey().length()-COMMANDBUILDER_CONFIG_KEY_SUFFIX.length());
           String protocolCommandBuilderClass = configEntry.getValue();
           if (!commandBuilders.containsKey(protocolName)) {
               Class<CommandBuilder> cmdBuilderClass = null;
               try {
                  cmdBuilderClass = (Class<CommandBuilder>) Class.forName(protocolCommandBuilderClass);
               } catch (ClassNotFoundException e) {
                  log.error("Could not load commandBuilder class: " + protocolCommandBuilderClass, e);
                  continue;
               }
               Constructor<CommandBuilder> ctor = null; 
               try {
                  ctor = cmdBuilderClass.getDeclaredConstructor(Deployer.class);
                  commandBuilders.put(protocolName, ctor.newInstance(deployer));
                  continue;
               } catch (Exception e) {
                  log.info("No constructor(deployer) found for class: " + protocolCommandBuilderClass);
               }

               try {
                  commandBuilders.put(protocolName, cmdBuilderClass.newInstance());
                } catch (Exception e) {
                   log.error("Could not instantiate commandBuilder: " + protocolCommandBuilderClass, e);
                }
           }
        }
     }
  }
  
  /**
   *
   * @deprecated    Additional use of this method should be avoided -- implementations should
   *                delegate to in-memory object model for commands instead (see
   *                {@link org.openremote.controller.model.Command} and
   *                {@link org.openremote.controller.deployer.Version20CommandBuilder}
   *                implementations). Expecting this method to be refactored/removed as part
   *                of ORCJAVA-209.
   */
  @Deprecated public Command getCommand(Element element) throws ConfigurationException
  {
    if (element == null)
    {
       throw new ConfigurationException("Null reference trying to create a protocol command.");
    }

    String protocolType = element.getAttributeValue(CommandBuilder.PROTOCOL_ATTRIBUTE_NAME);

    if (protocolType == null || protocolType.equals(""))
    {
       throw new ConfigurationException(
           "Protocol attribute is missing in {0}",
           new XMLOutputter().outputString(element)
       );
    }

    CommandBuilder builder = commandBuilders.get(protocolType);

    if (builder == null)
    {
      throw new ConfigurationException(
          "No device protocol builders registered with protocol type ''{0}''.", protocolType
      );
    }

    return builder.build(element);
  }
}
