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
package org.openremote.beehive.exception;

/**
 * The exception class when svn operation.
 * 
 * @author Tomsky
 */
public class SVNException extends RuntimeException {

   /** The Constant serialVersionUID. */
   private static final long serialVersionUID = 6673589293618355474L;

   /** The Constant SVN_COMMIT_ERROR. */
   public static final int SVN_COMMIT_ERROR = 430;
   
   /** The Constant SVN_GETINFO_ERROR. */
   public static final int SVN_GETINFO_ERROR = 431;
   
   /** The Constant SVN_DIFF_ERROR. */
   public static final int SVN_DIFF_ERROR = 432;
   
   /** The Constant SVN_GETLIST_ERROR. */
   public static final int SVN_GETLIST_ERROR = 433;
   
   /** The Constant SVN_GETLOG_ERROR. */
   public static final int SVN_GETLOG_ERROR = 434;
   
   /** The Constant SVN_REVERT_ERROR. */
   public static final int SVN_REVERT_ERROR = 435;
   
   /** The Constant SVN_ROLLBACK_ERROR. */
   public static final int SVN_ROLLBACK_ERROR = 436;
   
   /** The Constant SVN_GETSTATUS_ERROR. */
   public static final int SVN_GETSTATUS_ERROR = 437;
   
   /** The Constant SVN_GETCONTENT_ERROR. */
   public static final int SVN_GETCONTENT_ERROR = 438;
   
   /** The Constant SVN_EXPORT_ERROR. */
   public static final int SVN_EXPORT_ERROR = 439;
   
   /** The Constant SVN_URL_ERROR. */
   public static final int SVN_URL_ERROR = 440;
   
   /** The Constant SVN_IO_ERROR. */
   public static final int SVN_IO_ERROR = 441;
   
   /** The error code. */
   private int errorCode;
   
   
   /**
    * Gets the error code.
    * 
    * @return the error code
    */
   public int getErrorCode() {
      return errorCode;
   }

   /**
    * Sets the error code.
    * 
    * @param errorCode the new error code
    */
   public void setErrorCode(int errorCode) {
      this.errorCode = errorCode;
   }

   /**
    * Instantiates a new sVN exception.
    */
   public SVNException() {
      super();
   }

   /**
    * The Constructor.
    * 
    * @param message the message
    */
   public SVNException(String message) {
      super(message);
   }

   /**
    * The Constructor.
    * 
    * @param cause the cause
    */
   public SVNException(Throwable cause) {
      super(cause);
   }

   /**
    * The Constructor.
    * 
    * @param message the message
    * @param cause the cause
    */
   public SVNException(String message, Throwable cause) {
      super(message, cause);
   }

}
