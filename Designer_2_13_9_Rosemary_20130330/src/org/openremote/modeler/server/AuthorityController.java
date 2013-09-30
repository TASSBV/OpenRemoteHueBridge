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

import org.openremote.modeler.auth.Authority;
import org.openremote.modeler.client.rpc.AuthorityRPCService;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.context.SecurityContextHolder;

/**
 * The server side implementation of the RPC service <code>AuthorityRPCService</code>.
 */
public class AuthorityController extends BaseGWTSpringController implements AuthorityRPCService {
   
   /** The Constant serialVersionUID. */
   private static final long serialVersionUID = 9014403953508227827L;

   /**
    * {@inheritDoc}
    * 
    * @see org.openremote.modeler.client.rpc.AuthorityRPCService#getAuthoritication()
    */
   public Authority getAuthority() {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      Authority authority = new Authority();
      if (auth != null) {
         authority.setUsername(auth.getName());
         GrantedAuthority[] authorities = auth.getAuthorities();
         for (int i = 0; i < authorities.length; i++) {
            authority.addRole(authorities[i].getAuthority());
         }
      } else {
         authority = null;
      }
      return authority;
   }
}
