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
package org.openremote.controller.protocol.knx.ip;

public class KnxIpException extends Exception {
   private static final long serialVersionUID = 1L;
   
   public static enum Code {
      alreadyConnected,
      notConnected,
      unknownHost,
      noResponseFromInterface,
      responseError,
      wrongSequenceCounterValue,
      wrongChannelId,
      wrongResponseType,
      invalidHeader,
      unexpectedServiceType;
   }

   private Code code;

   public KnxIpException(Code code, String msg) {
      super(msg);
      this.code = code;
   }

   public Code getCode() {
      return code;
   }

   @Override
   public String getMessage() {
      return super.getMessage();
   }
}
