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
package org.openremote.modeler.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openremote.modeler.client.rpc.UserRPCService;
import org.openremote.modeler.domain.User;
import org.openremote.modeler.exception.UserInvitationException;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.shared.dto.UserDTO;

/**
 * The Class is for inviting user and managing invited user.
 */
public class UserController extends BaseGWTSpringController implements UserRPCService {

   private static final long serialVersionUID = -3486307399647834562L;

   private UserService userService;
   
   public UserDTO inviteUser(String email, String role) throws UserInvitationException{
      if (StringUtils.isEmpty(email) || StringUtils.isEmpty(role)) {
         throw new UserInvitationException("Failed to send invitation");
      }
      User u = userService.inviteUser(email, role, userService.getCurrentUser());
      return new UserDTO(u.getOid(), u.getUsername(), u.getEmail(), u.getRole());
   }

   public void setUserService(UserService userService) {
      this.userService = userService;
   }

   public ArrayList<UserDTO> getPendingInviteesByAccount() {
     return createUserDTOsFromUsers(userService.getPendingInviteesByAccount(userService.getCurrentUser()));
   }

   public UserDTO updateUserRoles(long uid, String roles) {
      User u = userService.updateUserRoles(uid, roles);
      return new UserDTO(u.getOid(), u.getUsername(), u.getEmail(), u.getRole());
   }

   public void deleteUser(long uid) {
      userService.deleteUser(uid);
   }

   public Long getUserId() {
      return userService.getCurrentUser().getOid();
   }
   
   public ArrayList<UserDTO> getAccountAccessUsersDTO() {
     return createUserDTOsFromUsers(userService.getAccountAccessUsers(userService.getCurrentUser()));
   }
   
   private ArrayList<UserDTO> createUserDTOsFromUsers(List<User> users) {
     ArrayList<UserDTO> dtos = new ArrayList<UserDTO>();
     for (User u : users) {
       dtos.add(new UserDTO(u.getOid(), u.getUsername(), u.getEmail(), u.getRole()));
     }
     return dtos;
   }

}
