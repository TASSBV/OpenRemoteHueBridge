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
 * Just test the constructors of {@link org.openremote.controller.exception.ConfigurationException}
 * class to make sure everything is alright.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class ConfigurationExceptionTest
{


  @Test public void testConstructors()
  {
    new ConfigurationException("msg");

    ConfigurationException e = new ConfigurationException("Test message {0}", "foo");
    Assert.assertTrue(e.getMessage().equals("Test message foo"));

    e = new ConfigurationException("Test {0} {1}", new Error("error"), "foo", 8);

    Assert.assertTrue(e.getMessage().equals("Test foo 8"));
    Assert.assertTrue(e.getCause().getMessage().equals("error"));
  }

}

