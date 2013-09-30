/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2011, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.openremote.controller.protocol.huebridge;

import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.HuebridgeConfig;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.utils.CommandUtil;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.utils.Strings;

import java.util.List;

import static java.lang.Integer.parseInt;

/**
 * Builds huebridge command from XML element. example:
 * <p/>
 * <pre>
 * {@code
 * <command id="xxx" protocol="huebridge">
 *      <property name="bridgeip" value="10.10.4.168" />
 * </command>
 * }
 * </pre>
 *
 * Used properties are bridgeip,  key, sensor, lightid, power, color, brightness, saturation and pollingInterval
 *
 * @author TASS Technology Solutions - www.tass.nl
 */
public class HueBridgeCommandBuilder implements CommandBuilder {

    // Constants
    // ------------------------------------------------------------------------------------

    /**
     * Common log category name for all HTTP protocol related logging.
     */
    public final static String HUEBRIDGE_PROTOCOL_LOG_CATEGORY = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "huebridge";

    private final static String STR_ATTRIBUTE_NAME_BRIDGEIP = "bridgeip";
    private final static String STR_ATTRIBUTE_NAME_KEY = "key";
    private final static String STR_ATTRIBUTE_NAME_SENSOR = "sensor";
    private final static String STR_ATTRIBUTE_NAME_LIGHTID = "lightid";
    private final static String STR_ATTRIBUTE_NAME_POWER = "power";
    private final static String STR_ATTRIBUTE_NAME_COLOR = "color";
    private final static String STR_ATTRIBUTE_NAME_BRIGHTNESS = "brightness";
    private final static String STR_ATTRIBUTE_NAME_SATURATION = "saturation";
    private final static String STR_ATTRIBUTE_NAME_POLLINGINTERVAL = "pollingInterval";

    // Class Members
    // --------------------------------------------------------------------------------

    /**
     * Logger for this HTTP protocol implementation.
     */
    private final static Logger logger = Logger.getLogger(HUEBRIDGE_PROTOCOL_LOG_CATEGORY);

    // Implements CommandBuilder
    // --------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public Command build(Element element) {
        logger.debug("Building HueBridgeCommand");
        List<Element> propertyEles = element.getChildren("property", element.getNamespace());
        String bridgeip = null;
        String key = null;
        String lightid = null;
        String sensor = null;
        String power = null;
        Boolean powerboolean = null;
        String color = null;
        Integer colorvalue = null;
        String brightness = null;
        Integer brightnessvalue = null;
        String saturation = null;
        Integer saturationvalue = null;
        String interval = null;
        Integer intervalInMillis = null;




        // read values from config xml

        for (Element ele : propertyEles) {
            String elementName = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_NAME);
            String elementValue = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_VALUE);

            if (STR_ATTRIBUTE_NAME_BRIDGEIP.equals(elementName)) {
                bridgeip = CommandUtil.parseStringWithParam(element, elementValue);
                logger.debug("HueBridgeCommand: bridgeip = " + bridgeip);
            } else if (STR_ATTRIBUTE_NAME_KEY.equals(elementName)) {
                key = CommandUtil.parseStringWithParam(element, elementValue);

                logger.debug("HueBridgeCommand: key = " + key);
            } else if (STR_ATTRIBUTE_NAME_SENSOR.equals(elementName)) {
                sensor = CommandUtil.parseStringWithParam(element, elementValue);
                logger.debug("HueBridgeCommand: sensor = " + sensor);
            } else if (STR_ATTRIBUTE_NAME_LIGHTID.equals(elementName)) {
                lightid = CommandUtil.parseStringWithParam(element, elementValue);
                logger.debug("HueBridgeCommand: lightid = " + lightid);
            } else if (STR_ATTRIBUTE_NAME_POWER.equals(elementName)) {
                power = CommandUtil.parseStringWithParam(element, elementValue);
                logger.debug("HueBridgeCommand: power = " + power);
            } else if (STR_ATTRIBUTE_NAME_COLOR.equals(elementName)) {
                color = CommandUtil.parseStringWithParam(element, elementValue);
                logger.debug("HueBridgeCommand:  color = " + color);
            } else if (STR_ATTRIBUTE_NAME_BRIGHTNESS.equals(elementName)) {
                brightness = CommandUtil.parseStringWithParam(element, elementValue);
                logger.debug("HueBridgeCommand: brightness = " + brightness);
            } else if (STR_ATTRIBUTE_NAME_SATURATION.equals(elementName)) {
                saturation = CommandUtil.parseStringWithParam(element, elementValue);
                logger.debug("HueBridgeCommand: saturation = " + saturation);
            } else if (STR_ATTRIBUTE_NAME_POLLINGINTERVAL.equals(elementName)) {
                interval = CommandUtil.parseStringWithParam(element, elementValue);
                logger.debug("HueBridgeCommand: pollingInterval = " + interval);
            }
        }

        //if bridgeip or key is empty. try to get it from the config files.
        try {
            if (bridgeip == null || bridgeip.isEmpty() || key == null || key.isEmpty()) {
                HuebridgeConfig hueConfig = HuebridgeConfig.readXML();
                if (bridgeip == null || bridgeip.isEmpty()) {
                    bridgeip = hueConfig.getAddress();
                }
                if (key == null || key.isEmpty()) {
                    key = hueConfig.getKey();
                }
            }
        } catch (Exception e1) {
            throw new NoSuchCommandException("Unable to create HueBridge command, could not get data from controller config");
        }

        //check for critical fields to not be empty
       if (bridgeip == null || bridgeip.isEmpty()){
           throw new NoSuchCommandException("Unable to create HueBridge command, bridgeip is not entered correctly");
        }
        if (key == null || key.isEmpty()){
            throw new NoSuchCommandException("Unable to create HueBridge command, key is not entered correctly");
        }
        if (lightid == null || lightid.isEmpty()){
            throw new NoSuchCommandException("Unable to create HueBridge command, lightid is not entered correctly");
        }

        // check fields for the right input.
        // when fields cannot be read properly throw the NoSuchCommandException
        try {
            if (null != power) {
                if (power.equalsIgnoreCase("on")) {
                    powerboolean = true;
                } else if (power.equalsIgnoreCase("off")) {
                    powerboolean = false;
                } else {
                    throw new NoSuchCommandException("Unable to create HueBridge command, Lightstatus on or off could not read");
                }
            }
        } catch (Exception e1) {
            throw new NoSuchCommandException("Unable to create HueBridge command, Lightstatus on or off could not read");
        }

        try {
            if (null != color) {
                colorvalue = parseInt(color);
                if (colorvalue > 65535) colorvalue = 65535;
                if (colorvalue < 0) colorvalue = 0;
            }
        } catch (Exception e1) {
            throw new NoSuchCommandException("Unable to create HueBridge command, Color could not be converted into integer");
        }

        try {
            if (null != brightness) {
                brightnessvalue = parseInt(brightness);
                if (brightnessvalue > 255) brightnessvalue = 255;
                if (brightnessvalue < 0) brightnessvalue = 0;
            }
        } catch (Exception e1) {
            throw new NoSuchCommandException("Unable to create HueBridge command, Brightness could not be converted into integer");
        }

        try {
            if (null != saturation) {
                saturationvalue = parseInt(saturation);
                if (saturationvalue > 255) saturationvalue = 255;
                if (saturationvalue < 0) saturationvalue = 0;
            }
        } catch (Exception e1) {
            throw new NoSuchCommandException("Unable to create HueBridge command, Saturation could not be converted into integer");
        }

        try {
            if (null != interval) {
                intervalInMillis = Integer.valueOf(Strings.convertPollingIntervalString(interval));
            }
        } catch (Exception e1) {
            throw new NoSuchCommandException("Unable to create HttpGet command, pollingInterval could not be converted into milliseconds");
        }

        return new HueBridgeCommand(bridgeip, key, sensor, lightid, powerboolean, colorvalue, brightnessvalue, saturationvalue, intervalInMillis);

    }
}
