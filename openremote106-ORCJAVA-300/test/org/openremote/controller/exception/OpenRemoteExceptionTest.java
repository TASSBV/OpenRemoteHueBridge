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
package org.openremote.controller.exception;

import org.junit.Test;
import org.junit.Assert;

/**
 * Basic tests to make sure nothing major gets messed up in the
 * {@link org.openremote.controller.exception.OpenRemoteException} implementation.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class OpenRemoteExceptionTest
{

  /**
   * Format message with no parameterization in it.
   */
  @Test public void testFormatting()
  {
    String msg = "some string";

    String formattedMsg = OpenRemoteException.format(msg);

    Assert.assertTrue(formattedMsg.equals(msg));
  }


  /**
   * Test basic string parameter replacement.
   */
  @Test public void testParameterizedFormatting()
  {
    String msg = "some {0}";

    String formattedMsg = OpenRemoteException.format(msg, "content");

    Assert.assertTrue(formattedMsg.equals("some content"));
  }

  /**
   * Test behavior when formatting fails.
   */
  @Test public void testFunkyFormatting()
  {
    String msg1 = "Test {0, date}, {1, integer, currency}";

    String formattedMsg1 = OpenRemoteException.format(msg1, "foo", "bar");

    Assert.assertTrue(formattedMsg1.startsWith(msg1 + "  [EXCEPTION MESSAGE FORMATTING ERROR:"));


    String msg2 = "Test {0, foo}, {1, bar}";

    String formattedMsg2 = OpenRemoteException.format(msg2, null, 1);

    Assert.assertTrue(formattedMsg2.startsWith(msg2 + "  [EXCEPTION MESSAGE FORMATTING ERROR:"));



    String msg3 = "Test {0, number}, {1, number, percentage}";

    String formattedMsg3 = OpenRemoteException.format(msg3, "foo", "bar");

    Assert.assertTrue(formattedMsg3.startsWith(msg3 + "  [EXCEPTION MESSAGE FORMATTING ERROR:"));
  }



}

