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
package org.openremote.beehive.api.service;

import org.openremote.beehive.domain.Account;
import org.openremote.beehive.domain.User;

/**
 * Account service.
 * 
 * @author Dan Cong
 *
 */
public interface AccountService {
   
   void save(Account a);
   
   User loadByUsername(String username);
   
   boolean isHTTPBasicAuthorized(long accountId, String credentials);
   
   boolean isHTTPBasicAuthorized(String credentials);
   
   boolean isHTTPBasicAuthorized(long accountId, String credentials, boolean isPasswordEncoded);
   
   boolean isHTTPBasicAuthorized(String username, String credentials, boolean isPasswordEncoded);
   
   long queryAccountIdByUsername(String username);

}
