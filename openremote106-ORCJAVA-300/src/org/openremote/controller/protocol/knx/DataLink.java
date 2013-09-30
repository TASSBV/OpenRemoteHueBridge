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
package org.openremote.controller.protocol.knx;

import org.apache.log4j.Logger;
import org.openremote.controller.utils.Strings;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Represents Data Link layer in KNX communication stack.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
class DataLink
{


  // Constants -----------------------------------------------------------------------------------

  private final static MessageCode DATA_REQUEST =
      new MessageCode(MessageCode.DATA_REQUEST_BYTE, "L_Data.req");

  private final static MessageCode DATA_INDICATE =
      new MessageCode(MessageCode.DATA_INDICATE_BYTE, "L_Data.ind");

  private final static MessageCode DATA_CONFIRM =
      new MessageCode(MessageCode.DATA_CONFIRM_BYTE, "L_Data.con");


  private final static MessageCode POLL_REQUEST =
      new MessageCode(MessageCode.POLL_REQUEST_BYTE, "L_Poll_Data.req");

  private final static MessageCode POLL_CONFIRM =
      new MessageCode(MessageCode.POLL_CONFIRM_BYTE, "L_Poll_Data.con");


  private final static MessageCode RAW_REQUEST =
      new MessageCode(MessageCode.RAW_REQUEST_BYTE, "L_Raw.req");

  private final static MessageCode RAW_INDICATE =
      new MessageCode(MessageCode.RAW_INDICATE_BYTE, "L_Raw.ind");

  private final static MessageCode RAW_CONFIRM =
      new MessageCode(MessageCode.RAW_CONFIRM_BYTE, "L_Raw.con");


  // Class Members --------------------------------------------------------------------------------

  /**
   * KNX logger. Uses a common category for all KNX related logging.
   */
  private final static Logger log = Logger.getLogger(KNXCommandBuilder.KNX_LOG_CATEGORY);


//  static boolean isDataIndicateFrame(byte msgCode)
//  {
//    return msgCode == MessageCode.DATA_INDICATE_BYTE;
//  }


  static String findServicePrimitiveByMessageCode(byte msgCode)
  {
    MessageCode mc = MessageCode.lookup(msgCode);

    if (mc == null)
    {
      log.warn(
          "Looked up an unknown data link layer frame message code: " +
          Strings.byteToUnsignedHexString(msgCode)
      );

      return "Unknown";
    }

    else
    {
      return mc.getPrimitiveName();
    }
  }


  static enum Service
  {
    DATA(DATA_REQUEST, DATA_INDICATE, DATA_CONFIRM),
    POLL(POLL_REQUEST, POLL_CONFIRM),
    RAW (RAW_REQUEST,  RAW_INDICATE,  RAW_CONFIRM);



    MessageCode requestCode;
    MessageCode indicateCode;
    MessageCode confirmCode;

    Service(MessageCode req, MessageCode ind, MessageCode con)
    {
      this(req, con);
      this.indicateCode = ind;
    }

    Service(MessageCode req, MessageCode con)
    {
      this.requestCode = req;
      this.confirmCode = con;
    }

    byte getRequestMessageCode()
    {
      return requestCode.getByteValue();
    }
  }

  public static class MessageCode
  {
    public final static int DATA_REQUEST_BYTE  = 0x11;
    public final static int DATA_INDICATE_BYTE = 0x29;
    public final static int DATA_CONFIRM_BYTE  = 0x2E;

    public final static int POLL_REQUEST_BYTE  = 0x13;
    public final static int POLL_CONFIRM_BYTE  = 0x25;

    public final static int RAW_REQUEST_BYTE   = 0x10;
    public final static int RAW_INDICATE_BYTE  = 0x2D;
    public final static int RAW_CONFIRM_BYTE   = 0x2F;


    private static Map<Byte, MessageCode> lookup = new ConcurrentHashMap<Byte, MessageCode>(25);

    private static MessageCode lookup(byte b)
    {
      return lookup.get(b);
    }


    private int messageCode;
    private String primitive;

    private MessageCode(int messageCode, String primitive)
    {
      this.messageCode = messageCode;
      this.primitive = primitive;

      lookup.put((byte)messageCode, this);
    }

    private byte getByteValue()
    {
      return (byte)messageCode;
    }

    private String getPrimitiveName()
    {
      return primitive;
    }
  }
}
