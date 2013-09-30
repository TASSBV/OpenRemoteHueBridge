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

public class IpDisconnectReq extends IpMessage {
  public static final int STI = 0x209;
  private int             ccid;
  private Hpai            hpai;

  public IpDisconnectReq(int ccid, Hpai serverHpai) {
    super(STI, 10);
    this.ccid = ccid;
    this.hpai = serverHpai;
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
    os.write(this.ccid & 0xFF);
    os.write(0);
    this.hpai.write(os);
  }
}
