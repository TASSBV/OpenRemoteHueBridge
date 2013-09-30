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
package org.openremote.controller.action;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Domain class which represents a Hue Bridge.
 * Contains 4 fields that can be requested: id, internalip, macAddress and username (also known as key)
 * @author TASS Technology Solutions - www.tass.nl
 */
public class Bridge{

    private String id;
    private String internalip;
    private String macAddress;
    private String username;

    public Bridge(String id, String internalip, String macAddress) {
        this.id = id;
        this.internalip = internalip;
        this.macAddress = macAddress;
    }

    public String getId() {
        return id;
    }

    public String getInternalip() {
        return internalip;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public JSONObject bridgeToJson() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("id", getId());
        obj.put("internalip", getInternalip());
        obj.put("macaddres", getMacAddress());
        obj.put("username", getUsername());

        return obj;
    }
}

