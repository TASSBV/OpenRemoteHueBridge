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
package org.openremote.controller.command;

import java.math.BigDecimal;

import org.openremote.controller.exception.ConversionException;

/**
 * Encapsulates command parameters passed through REST interface to protocol command builders. <p>
 *
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class CommandParameter
{

  // Private Instance Fields ----------------------------------------------------------------------

  /**
   * Command parameter value.
   */
  private BigDecimal value;


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a new command parameter with a given integer value.
   *
   * @param number    value of the parameter
   *
   * @throws ConversionException  if the conversion from the string representation of command
   *                              parameter cannot be parsed and converted to integer for any
   *                              reason
   */
  public CommandParameter(String number) throws ConversionException
  {
    try
    {
      value = new BigDecimal(number);
    }
    catch (NumberFormatException exception)
    {
      throw new ConversionException(
          "Failed to parse " + number + " to integer : " + exception.getMessage(), exception
      );
    }
  }


  // Public Instance Methods ----------------------------------------------------------------------

  /**
   * Returns the parameter value.
   *
   * @return  value of integer command parameter
   */
  public BigDecimal getValue()
  {
    return value;
  }

}


