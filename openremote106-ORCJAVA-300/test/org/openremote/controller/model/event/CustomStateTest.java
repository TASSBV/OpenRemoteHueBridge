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
package org.openremote.controller.model.event;

import org.junit.Test;
import org.junit.Assert;

/**
 * Basic tests for {@link org.openremote.controller.model.event.CustomState}
 *
 * (ORCJAVA-82 http://jira.openremote.org/browse/ORCJAVA-82)
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class CustomStateTest
{


  /**
   * Basic constructor and validation.
   */
  @Test public void basicConstruction()
  {
    CustomState cs = new CustomState(0, "name", "foo");

    Assert.assertTrue(cs.getValue().equals("foo"));
    Assert.assertTrue(cs.getSource().equals("name"));
    Assert.assertTrue(cs.getSourceID() == 0);

    Assert.assertTrue(cs.serialize().equals("foo"));

    Assert.assertTrue(cs.toString() != null);
    Assert.assertFalse(cs.toString().equals(""));
  }

  
}

