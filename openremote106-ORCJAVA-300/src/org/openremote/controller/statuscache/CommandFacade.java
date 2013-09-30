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
package org.openremote.controller.statuscache;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import org.openremote.controller.model.Command;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class CommandFacade
{


  Map<String, Command> namedCommands = new HashMap<String, Command>();


  protected CommandFacade(Set<Command> commands)
  {
    for (Command cmd : commands)
    {
      String cmdName = cmd.getProperty(Command.COMMAND_NAME_PROPERTY);

      if (cmdName != null && !cmdName.equals(""))
      {
        namedCommands.put(cmdName, cmd);
      }
    }
  }

  public void command(String name)
  {
    command(name, null);    // null == no param
  }

  public void command(String name, String param)
  {
    if (name == null || name.equals(""))
    {
      // TODO log

      return;
    }

    Command cmd = namedCommands.get(name);

    if (cmd == null)
    {
      // TODO log

      return;
    }

    if (param == null)
    {
      cmd.execute();
    }

    else
    {
      cmd.execute(param);
    }
  }

  public void command(String name, int value)
  {
    command(name, Integer.toString(value));
  }

}

