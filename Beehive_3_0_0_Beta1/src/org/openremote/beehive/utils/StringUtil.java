/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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
package org.openremote.beehive.utils;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;


/**
 * Utility class for String
 * 
 * @author Dan 2009-2-16
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 * 
 */
public class StringUtil {

   private static final Logger LOGGER = Logger.getLogger(StringUtil.class.getName());

   private StringUtil() {
   }

   /**
    * Escapes a SQL string
    * 
    * @param src
    *           the string to escape
    * @return the escaped string
    */
   public static String escapeSql(String src) {
      if (src.indexOf('\\') != -1) {
         src = src.replace("\\", "\\\\");
      }
      return StringEscapeUtils.escapeSql(src);
   }

   /**
    * Parses the <code>Model</code> name in a comment
    * 
    * @param comment
    *           the comment to parse
    * @return <code>Model</code> name
    */
   public static String parseModelNameInComment(String comment) {
      String regexpLine = "^\\s*#\\s*(model|model\\s*no\\.\\s*of\\s*remote\\s*control)\\s*:.*?$";
      Pattern patLine = Pattern.compile(regexpLine, Pattern.MULTILINE);
      Matcher m = patLine.matcher(comment);
      String targetLine = "";
      while (m.find()) {
         targetLine = m.group(0);
         break;
      }
      String name = targetLine.substring(targetLine.indexOf(":") + 1).trim();
      int braceIndex = name.indexOf('(');
      if (braceIndex != -1) {
         name = name.substring(0, name.indexOf('(')).trim();
      }
      return name.replace(" ", "_");
   }

   /**
    * Line.Separator of current OS
    * 
    * @return Line.Separator
    */
   public static String lineSeparator() {
      return System.getProperty("line.separator");
   }

   /**
    * Two adjacent Line.Separator of current OS
    * 
    * @return two adjacent Line.Separator
    */
   public static String doubleLineSeparator() {
      String sep = System.getProperty("line.separator");
      return sep + sep;
   }

   /**
    * Calculate the rest of the space between a key and a value
    * 
    * @param key
    *           the key
    * @return the rest of the space
    */
   public static String remainedTabSpace(String key) {
      String space = "";
      if (key.length() <= 24) {
         for (int i = 0; i < 24 - key.length(); i++) {
            space += " ";
         }
      } else {
         space = "\t";
      }
      return space;
   }

   /**
    * Converts '\\' to '/' in URL
    * 
    * @param url
    *           the URL to convert
    * @return the target URL
    */
   public static String toUrl(String url) {
      return url.replace("\\", "/");
   }

   /**
    * Appends a File.Separator to a string
    * 
    * @param src
    *           a string to append
    * @return the target string
    */
   public static String appendFileSeparator(String src) {
      return src.endsWith("/") ? src : src + "/";
   }

   public static StringBuffer readInputStreamToStringBuffer(InputStream is) {
      StringBuffer strBuffer = new StringBuffer();
      byte[] buffer = null;
      int count = 0;
      try {
         do {
            buffer = new byte[1024];
            count = is.read(buffer, 0, buffer.length);
            strBuffer.append(new String(buffer));
         } while (count != -1);
      } catch (IOException e) {
         LOGGER.error("Read inputStream to stringBuffer occur error.", e);
      } finally {
         try {
            is.close();
         } catch (IOException e) {
            LOGGER.error("Close the inputStream occur error.", e);
         }
      }
      return strBuffer;
   }

   /**
    * Parse a String contrain some long to array. If some of long parse fail, this method will
    * ignore it can continue.
    * 
    * @param str
    *           String
    * @param seperator
    *           seperator
    * @return ArrayList<Long>
    */
   public static ArrayList<Long> parseStringIds(String str, String seperator) {
      ArrayList<Long> result = new ArrayList<Long>();
      String[] ids = str.split(seperator);
      for (String id : ids) {
         long l = 0;
         try {
            l = Long.parseLong(id);
         } catch (NumberFormatException e) {
            LOGGER.warn("Error in parsing string '" + id + "' to long (" + e.getMessage() + ")", e);
            continue;
         }
         result.add(l);
      }
      return result;
   }

   /**
    * Get system time
    * 
    * @return time string "yyyy-MM-dd HH:mm:ss"
    */
   public static String systemTime() {
      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      return df.format(new Date());
   }

   /**
    * Escape regexp.
    * 
    * @param string
    *           the string
    * 
    * @return the string
    */
   public static String escapeRegexp(String string) {
      return string.replace("\\", "\\u005C").replace("+", "\\u002B").replace(".", "\\u002E").replace("|", "\\u007C")
            .replace("$", "\\u0024").replace("^", "\\u005E").replace("*", "\\u002A").replace("?", "\\u003F").replace(
                  "{", "\\u007B").replace("[", "\\u005B").replace("(", "\\u0028").replace(")", " \\u0029");
   }
}
