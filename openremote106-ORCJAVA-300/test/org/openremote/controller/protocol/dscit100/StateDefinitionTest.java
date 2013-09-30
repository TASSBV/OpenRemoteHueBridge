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
package org.openremote.controller.protocol.dscit100;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.protocol.dscit100.PanelState.StateType;

public class StateDefinitionTest
{

  @Before
  public void setUp() throws Exception
  {
  }

  @Test
  public void testDSCIT100StateDefinition()
  {
    StateDefinition sd = new StateDefinition(StateType.PARTITION, "1");

    assertTrue(sd instanceof StateDefinition);
    assertEquals(StateType.PARTITION, sd.getType());
    assertEquals("1", sd.getTarget());
  }
}
