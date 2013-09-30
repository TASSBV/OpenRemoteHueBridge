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
import org.openremote.controller.command.Command;

/**
 * @author Greg Rapp
 * 
 */
public class DSCIT100CommandTest
{

  private DSCIT100ConnectionManager connManager = null;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception
  {
    connManager = new DSCIT100ConnectionManager();
  }

  /**
   * Test method for
   * {@link org.openremote.controller.protocol.dscit100.DSCIT100Command#createCommand(java.lang.String, java.lang.String, org.openremote.controller.protocol.dscit100.DSCIT100ConnectionManager)}
   * .
   */
  @Test
  public void testCreateCommand()
  {
    Command command = DSCIT100Command.createCommand("ARM", "1.1.1.1:50",
        "1234", "1", connManager);

    assertTrue(command instanceof DSCIT100Command);
  }

}
