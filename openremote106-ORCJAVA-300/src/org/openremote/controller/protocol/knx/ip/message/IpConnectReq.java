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
package org.openremote.controller.protocol.knx.ip.message;

import java.io.IOException;
import java.io.OutputStream;

public class IpConnectReq extends IpMessage {
   public static final int STI = 0x205;
   // TODO check if 3rd byte should not be changed
   private byte[] cri;
   private Hpai controlEndpoint, dataEndpoint;

   public IpConnectReq(Hpai controlEndpoint, Hpai dataEndpoint) {
      super(STI, 0x14);
      this.controlEndpoint = controlEndpoint;
      this.dataEndpoint = dataEndpoint;
      this.cri = new byte[] { 0x04, 0x04, 0x02, 0x00 };
   }

   @Override
   public Primitive getPrimitive() {
      return Primitive.REQ;
   }

   @Override
   public int getSyncSendTimeout() {
      return 10000;
   }

   @Override
   public void write(OutputStream os) throws IOException {
      super.write(os);
      this.controlEndpoint.write(os);
      this.dataEndpoint.write(os);
      os.write(this.cri);
   }
}
