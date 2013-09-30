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
import org.openremote.controller.protocol.knx.GroupAddressTest;
import org.openremote.controller.protocol.knx.KNXCommandBuilderTest;
import org.openremote.controller.protocol.knx.DimIncreaseDecreaseTest;
import org.openremote.controller.protocol.knx.ScalingTest;
import org.openremote.controller.protocol.knx.KNXIpConnectionManagerTest;
import org.openremote.controller.protocol.knx.RangeTest;
import org.openremote.controller.protocol.knx.datatype.TwoOctetFloatTest;

/**
 * All KNX tests aggregated here.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
@RunWith(Suite.class)
@Suite.SuiteClasses(
   {
       KNXCommandBuilderTest.class,
       DimIncreaseDecreaseTest.class,
       ScalingTest.class,
       RangeTest.class,
       GroupAddressTest.class,
       KNXIpConnectionManagerTest.class,
       TwoOctetFloatTest.class
   }
)
public class KNXTests
{

}

