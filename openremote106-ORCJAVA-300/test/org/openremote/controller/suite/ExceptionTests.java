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
package org.openremote.controller.suite;

import org.openremote.controller.exception.InitializationExceptionTest;
import org.openremote.controller.exception.OpenRemoteExceptionTest;
import org.openremote.controller.exception.ConversionExceptionTest;
import org.openremote.controller.exception.ConfigurationExceptionTest;
import org.openremote.controller.exception.XMLParsingExceptionTest;
import org.junit.runners.Suite;
import org.junit.runner.RunWith;

/**
 * Tests related to customized exception implementations.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
@RunWith(Suite.class)
@Suite.SuiteClasses(
{
  OpenRemoteExceptionTest.class,

  ConfigurationExceptionTest.class,
  ConversionExceptionTest.class,
  InitializationExceptionTest.class,
  XMLParsingExceptionTest.class
}
)
public class ExceptionTests
{

}

