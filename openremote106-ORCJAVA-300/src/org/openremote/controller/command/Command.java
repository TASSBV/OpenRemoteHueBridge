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

/**
 * TODO  :
 *   - This is only a tagging interface but the name abstraction 'Command' no longer makes sense
 *     as it also includes "EventListener" implementations (which has no command to execute).
 *     Something like 'ProtocolHandler' would be more appropriate. Rename will however affect
 *     everyone (and documentation) so leaving it for next major version (or if the existing one
 *     is very stable)
 *                                                                                           [JPL]
 *
 * 
 * @author Handy.Wang 2009-10-15
 * @author Dan Cong
 */
public interface Command
{
        
    public final static String STATUS_COMMAND = "status";
    
    /** 
     * Attribute name of dynamic command value for slider, colorpicker.<br />
     * This attribute is temporary for holding dynamic control command value from REST API. <br />
     * Take slider for example: <br />
     * REST API: http://localhost:8080/controller/rest/control/{slider_id}/10 <br />
     * <b>10</b> means control command value of slider, which will be stored into the attribute named <b>DYNAMIC_VALUE_ATTR_NAME</b> of Command DOM element.
     */
    public static final String DYNAMIC_VALUE_ATTR_NAME = "dynamicValue";
    
    
    /**
     * Dynamic parameter place holder regular expression.
     * When a command contains a dynamic value taken from a slider or color picker etc., 
     * this could be as simple as allowing '${param}' literal somewhere in the command value, 
     * any command builder should replace '${param}' with the command param value got from REST call.  
     */
    public static final String DYNAMIC_PARAM_PLACEHOLDER_REGEXP = "\\$\\{param\\}";
}
