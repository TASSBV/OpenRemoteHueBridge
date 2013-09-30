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
package org.openremote.modeler.domain;


/**
 * @author marcus@openremote.org
 *
 * TODO: See MODELER-266 -- should share a common base implementation with controller
 */
public class KnxGroupAddress {

    private String name;
    private String groupAddress;
    private String dpt;
    private String command = "N/A";
    private Boolean importGA = Boolean.FALSE;
    
    public KnxGroupAddress(String dpt, String groupAddress, String name) {
        super();
        this.dpt = dpt;
        this.groupAddress = groupAddress;
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getGroupAddress() {
        return groupAddress;
    }
    
    public void setGroupAddress(String groupAddress) {
        this.groupAddress = groupAddress;
    }
    
    public String getDpt() {
        return dpt;
    }
    
    public void setDpt(String dpt) {
        this.dpt = dpt;
    }

    public Boolean getImportGA() {
        return importGA;
    }

    public void setImportGA(Boolean importGA) {
        this.importGA = importGA;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }


    
    
}
