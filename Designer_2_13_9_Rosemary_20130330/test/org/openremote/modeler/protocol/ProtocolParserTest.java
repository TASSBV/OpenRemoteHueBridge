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

package org.openremote.modeler.protocol;

import java.util.HashMap;
import java.util.Map;

import org.openremote.modeler.exception.ParseProtocolException;
import org.openremote.modeler.service.ProtocolParser;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:allen.wei@finalist.cn">allen.wei</a>
 */
public class ProtocolParserTest {

   /**
    * Test protocol schema validate is fail.
    */
   @Test(expectedExceptions = { ParseProtocolException.class })
   public void testSchemaValidateFail() {
      ProtocolParser protocolParser = new ProtocolParser();
      protocolParser.setPath(getClass().getResource("fixture").getPath() + "/fail");
      protocolParser.parseXmls();
   }

   /**
    * Test parse protocol xml.
    */
   @Test
   public void testParseXmls() {
      ProtocolParser protocolParser = new ProtocolParser();
      protocolParser.setPath(getClass().getResource("fixture").getPath());
      Map<String, ProtocolDefinition> pros = new HashMap<String, ProtocolDefinition>();
      ProtocolDefinition definition = getCorrectProtocolDefinition();
      pros.put(definition.getDisplayName(), definition);

      Assert.assertEquals(pros, protocolParser.parseXmls());
   }

   /**
    * Gets the correct protocol definition.
    * 
    * @return the correct protocol definition
    */
   private ProtocolDefinition getCorrectProtocolDefinition() {
      ProtocolDefinition definition = new ProtocolDefinition();
      definition.setDisplayName("KNX");
      definition.setTagName("knx");

      ProtocolAttrDefinition groupAddressAttr = new ProtocolAttrDefinition();
      groupAddressAttr.setName("groupAddress");
      groupAddressAttr.setLabel("Group Address");
      ProtocolValidator allowBlank = new ProtocolValidator(ProtocolValidator.ALLOW_BLANK_TYPE, "false", null);
      ProtocolValidator regex = new ProtocolValidator(ProtocolValidator.REGEX_TYPE, "(\\d\\.){3}\\d",
            "group address should be 1.4.1.4");
      groupAddressAttr.getValidators().add(allowBlank);
      groupAddressAttr.getValidators().add(regex);
      definition.getAttrs().add(groupAddressAttr);

      ProtocolAttrDefinition commandAttr = new ProtocolAttrDefinition();
      commandAttr.setName("command");
      commandAttr.setLabel("KNX Command");
      commandAttr.setTooltipMessage("KNX Group Address");
      ProtocolValidator allowBlank2 = new ProtocolValidator(ProtocolValidator.ALLOW_BLANK_TYPE, "false", null);
      ProtocolValidator maxLength2 = new ProtocolValidator(ProtocolValidator.MAX_LENGTH_TYPE, "10", null);
      ProtocolValidator regex2 = new ProtocolValidator(ProtocolValidator.REGEX_TYPE, "\\w*", null);
      commandAttr.getValidators().add(allowBlank2);
      commandAttr.getValidators().add(maxLength2);
      commandAttr.getValidators().add(regex2);
      definition.getAttrs().add(commandAttr);

      return definition;
   }
}
