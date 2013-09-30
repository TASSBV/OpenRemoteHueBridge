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

public abstract class IpMessage {
  public static final int     OK     = 0;
  private static final byte[] HEADER = { 0x06, 0x10 };
  private int                 sti;
  private int                 variableLength;

  public static enum Primitive {
    REQ, RESP
  };

  public abstract Primitive getPrimitive();

  public IpMessage(int sti, int variableLength) {
    this.sti = sti;
    this.variableLength = variableLength;
  }

  public void write(OutputStream os) throws IOException {
    os.write(HEADER);
    int d = this.getServiceTypeIdentifier();
    os.write((d >> 8) & 0xFF);
    os.write(d & 0xFF);
    d = this.getVariableLength() + 6;
    os.write((d >> 8) & 0xFF);
    os.write(d & 0xFF);
  }

  public int getSyncSendTimeout() {
    return 0;
  }

  public int getServiceTypeIdentifier() {
    return this.sti;
  }

  public int getVariableLength() {
    return this.variableLength;
  }
}
