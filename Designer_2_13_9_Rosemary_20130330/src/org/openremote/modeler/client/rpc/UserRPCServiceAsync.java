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

import org.openremote.modeler.shared.dto.UserDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UserRPCServiceAsync {

   void inviteUser(String email, String role, AsyncCallback<UserDTO> callback);

   void getPendingInviteesByAccount(AsyncCallback<ArrayList<UserDTO>> callback);

   void updateUserRoles(long uid, String roles, AsyncCallback<UserDTO> callback);

   void deleteUser(long uid, AsyncCallback<Void> callback);

   void getUserId(AsyncCallback<Long> callback);

   void getAccountAccessUsersDTO(AsyncCallback<ArrayList<UserDTO>> callback);
}
