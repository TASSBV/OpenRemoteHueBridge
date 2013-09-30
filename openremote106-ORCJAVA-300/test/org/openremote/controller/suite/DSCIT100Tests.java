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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.openremote.controller.protocol.dscit100.DSCIT100CommandBuilderTest;
import org.openremote.controller.protocol.dscit100.DSCIT100CommandTest;
import org.openremote.controller.protocol.dscit100.PacketTest;
import org.openremote.controller.protocol.dscit100.PanelStateTest;
import org.openremote.controller.protocol.dscit100.StateDefinitionTest;

/**
 * All OpenRemote DSCIT100 tests aggregated here.
 *
 * @author <a href="mailto:gdrapp@gmail.com">Greg Rapp</a>
 */
@RunWith(Suite.class)
@Suite.SuiteClasses(
   {
     PacketTest.class,
     DSCIT100CommandTest.class,
     DSCIT100CommandBuilderTest.class,
     StateDefinitionTest.class,
     PanelStateTest.class
   }
)

public class DSCIT100Tests
{

}
