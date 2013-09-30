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
package org.openremote.modeler.client.rpc;

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.domain.User;
import org.openremote.modeler.exception.UserInvitationException;
import org.openremote.modeler.shared.dto.UserDTO;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The Interface is for managing user and account.
 */
@RemoteServiceRelativePath("user.smvc")
public interface UserRPCService extends RemoteService {

   /**
    * Invite a user for sharing an account, and the account has a specified role.
    * 
    * @param email the email
    * @param role the role
    * 
    * @return the user
    * 
    * @throws UserInvitationException the user invitation exception
    */
   UserDTO inviteUser(String email, String role) throws UserInvitationException;
   
   /**
    * Gets the pending invitees by current account.
    * 
    * @return the pending invitees by account
    */
   ArrayList<UserDTO> getPendingInviteesByAccount();
   
   /**
    * Update the invited user roles.
    * 
    * @param uid the uid
    * @param roles the roles
    * 
    * @return the user
    */
   UserDTO updateUserRoles(long uid, String roles);
   
   /**
    * Delete the invited user by user id.
    * 
    * @param uid the uid
    */
   void deleteUser(long uid);
   
   /**
    * Gets the current user's id.
    * 
    * @return the user id
    */
   Long getUserId();
   
   /**
    * Gets the users who can access the current account.
    * 
    * @return the account access users
    */
   ArrayList<UserDTO> getAccountAccessUsersDTO();
}
