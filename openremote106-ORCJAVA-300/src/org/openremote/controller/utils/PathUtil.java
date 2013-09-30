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
package org.openremote.controller.utils;

/**
 * The Utility for file system Path.
 * 
 * @author Dan 2009-5-14
 */
@Deprecated public class PathUtil
{
   
  /**
   * Append file separator.
   */
  @Deprecated public static String addSlashSuffix(String src)
  {
    if (src == null)
    {
      return "/";
    }

    return src.endsWith("/") ? src : src + "/";
  }

  /**
   * Removes the slash suffix.
   */
  @Deprecated public static String removeSlashSuffix(String src)
  {
    return src.endsWith("/") ? src.substring(0, src.lastIndexOf("/")) : src ;
  }
   
}
