/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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
package org.openremote.beehive.rest;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.openremote.beehive.api.dto.RemoteOptionDTO;

/**
 * In order to let rest service to serialize list of RemoteOptions User: allenwei Date: 2009-2-10 Time: 13:57:29
 */
@XmlRootElement(name = "options")
public class RemoteOptionListing {

   private List<RemoteOptionDTO> remoteOptions = new ArrayList<RemoteOptionDTO>();

   public RemoteOptionListing() {
   }

   public RemoteOptionListing(List<RemoteOptionDTO> remoteOptions) {
      this.remoteOptions = remoteOptions;
   }

   @XmlElement(name = "option")
   public List<RemoteOptionDTO> getRemoteOptions() {
      return remoteOptions;
   }

   public void setRemoteOptions(List<RemoteOptionDTO> remoteOptions) {
      this.remoteOptions = remoteOptions;
   }
}