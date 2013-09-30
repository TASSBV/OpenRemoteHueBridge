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
package org.openremote.beehive.api.service.impl;

import org.openremote.beehive.Constant;
import org.openremote.beehive.api.service.AccountService;
import org.openremote.beehive.domain.Account;
import org.openremote.beehive.domain.Code;
import org.openremote.beehive.domain.User;
import org.springframework.security.providers.encoding.Md5PasswordEncoder;

import com.sun.syndication.io.impl.Base64;

/**
 * Account service implementation.
 * 
 * @author Dan Cong
 */
public class AccountServiceImpl extends BaseAbstractService<Code> implements AccountService {

   @Override
   public void save(Account a) {
      genericDAO.save(a);
   }

   @Override
   public User loadByUsername(String username) {
      return genericDAO.getByNonIdField(User.class, "username", username);
   }
   
   public long queryAccountIdByUsername(String username) {
      User u = genericDAO.getByNonIdField(User.class, "username", username);
      return u == null ? 0L : u.getAccount().getOid();
   }

   @Override
   public boolean isHTTPBasicAuthorized(long accountId, String credentials, boolean isPasswordEncoded) {
      if (credentials != null && credentials.startsWith(Constant.HTTP_BASIC_AUTH_HEADER_VALUE_PREFIX)) {
         credentials = credentials.replaceAll(Constant.HTTP_BASIC_AUTH_HEADER_VALUE_PREFIX, "");
         credentials = Base64.decode(credentials);
         String[] arr = credentials.split(":");
         if (arr.length == 2) {
            String username = arr[0];
            String password = arr[1];
            long accId = queryAccountIdByUsername(username);
            if (accId == 0L || accId != accountId) {
               return false;
            }
            User user = loadByUsername(username);
            if (!isPasswordEncoded) {
               password = new Md5PasswordEncoder().encodePassword(password, username);
            }
            if (user != null && user.getPassword().equals(password)) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public boolean isHTTPBasicAuthorized(long accountId, String credentials) {
      return isHTTPBasicAuthorized(accountId, credentials, true);
   }

   @Override
   public boolean isHTTPBasicAuthorized(String username, String credentials, boolean isPasswordEncoded) {
      return isHTTPBasicAuthorized(queryAccountIdByUsername(username), credentials, isPasswordEncoded);
   }

   @Override
   public boolean isHTTPBasicAuthorized(String credentials) {
      if (credentials != null && credentials.startsWith(Constant.HTTP_BASIC_AUTH_HEADER_VALUE_PREFIX)) {
         credentials = credentials.replaceAll(Constant.HTTP_BASIC_AUTH_HEADER_VALUE_PREFIX, "");
         credentials = Base64.decode(credentials);
         String[] arr = credentials.split(":");
         if (arr.length == 2) {
            String username = arr[0];
            String password = arr[1];
            User user = loadByUsername(username);
            if (user != null && user.getPassword().equals(password)) {
               return true;
            }
         }
      }

      return false;
   }
   

}
