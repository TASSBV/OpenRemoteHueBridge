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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;

/**
 * The Class DateUtil.
 * 
 * @author Tomsky
 */
public class DateUtil {
   
   /** The Constant LOGGER. */
   private static final Logger LOGGER = Logger.getLogger(DateUtil.class.getName());

   /**
    * Instantiates a new date util.
    */
   private DateUtil() {
   }

   /**
    * Gets the time format.
    * 
    * @param strFormat
    *           the str format
    * @param date
    *           the date
    * 
    * @return the string
    */
   public static String getTimeFormat(Date date, String strFormat) {
      DateFormat sdf = new SimpleDateFormat(strFormat);
      String sDate = sdf.format(date);
      return sDate;
   }

   /**
    * Gets the default format.
    * 
    * @param date
    *           the date
    * 
    * @return the default format
    */
   public static String getDefaultFormat(Date date) {
      return getTimeFormat(date, "yyyy-MM-dd HH:mm:ss");
   }

   /**
    * String2 date.
    * 
    * @param strDate
    *           the str date
    * @param format
    *           the format
    * @param locale
    *           the locale
    * 
    * @return the date
    */
   public static Date String2Date(String strDate, String format, Locale locale) {
      DateFormat fmt = new SimpleDateFormat(format, Locale.ENGLISH);
      Date date = new Date();
      try {
         date = fmt.parse(strDate);
      } catch (ParseException e) {
         LOGGER.error("Parse String " + strDate + " to date occur error.", e);
      }
      return date;
   }

   /**
    * Adds the timestamp to filename.
    * 
    * @param date
    *           the date
    * @param fileName
    *           the file name
    * 
    * @return the string
    */
   public static String addTimestampToFilename(Date date, String fileName) {
      String[] time = getTimeFormat(date, "yyyy-MM-dd.HH-mm").split("\\.");
      return time[0] + "/" + time[1] + fileName;
   }
}
