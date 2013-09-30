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
import java.io.InputStream;

public class IpDiscoverResp extends IpMessage {
  public static final int STI = 0x202;
  private Hpai            controlEndpoint;

  public IpDiscoverResp(InputStream is, int vl) throws IOException {
    super(STI, vl);
    this.controlEndpoint = new Hpai(is);

    // TODO Read device hardware DIB
    int l = is.read();
    is.skip(l - 1);

    // TODO Read supported service families DIB
    l = is.read();
    is.skip(l - 1);
  }

  @Override
  public Primitive getPrimitive() {
    return Primitive.RESP;
  }

  public Hpai getControlEndpoint() {
    return this.controlEndpoint;
  }
}
