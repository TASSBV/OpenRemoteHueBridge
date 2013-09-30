/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.modeler.client;

/**
 * Puts all the fronted constant here.
 */
public class Constants {

   /**
    * Not be instantiated.
    */
   private Constants() {
   }

   /** The Constant INFRARED_TYPE. For assign protocol type. */
   public static final String INFRARED_TYPE = "Infrared";
   
   /** The Constant KNX_TYPE. */
   public static final String KNX_TYPE = "KNX";
   
   /** The Constant X10. */
   public static final String X10_TYPE = "x10";
   
   /** The Constant BUTTON_DND_GROUP. For assign UIDesigner to DND button. */
   public static final String BUTTON_DND_GROUP = "buttonDNDGroup";
   
   /** The Constant CONTROL_DND_GROUP. */
   public static final String CONTROL_DND_GROUP = "controlDNDGroup";
   
   /** The Constant REORDER_PANEL_GROUP. */
   public static final String REORDER_PANEL_GROUP = "reorderPanelGroup";
   
   /** The Constant DEVICES. */
   public static final String DEVICES = "devices";
   
   /** The Constant MACROS. */
   public static final String MACROS = "macros";
   
   /** The Constant DEVICES_OID. */
   public static final long DEVICES_OID = -100;
   
   /** The Constant MACROS_OID. */
   public static final long MACROS_OID = -200;

   /** The Constant NULL_PARENT_OID. */
   public static final long NULL_PARENT_OID = -300;
   
   /** The Constant SCREEN_TABLE_OID. */
   public static final long SCREEN_TABLE_OID = -400;
   
   public static final long GROUP_TABLE_OID = -600;
   
   public static final long PREVIEW_SCREENBUTTON_OID = -500;
   
   /** The Constant PANEL_DESC_FILE_EXT. */
   public static final String PANEL_DESC_FILE = "import";
   
   /** Make resizable in east and south direction. */
   public static final String RESIZABLE_HANDLES = "e s";
   
   public static final String CUSTOM_PANEL = "Custom";
   public static final String DEFAULT_GROUP = "Default Group";
   public static final String DEFAULT_SCREEN = "Starting Screen";
   
   public static final String REG_POSITIVEINT = "^[1-9][0-9]*$";
   public static final String REG_NONNEGATIVEINT = "^\\d+$";
   public static final String REG_INTEGER = "^-?\\d+$";
   public static final String REG_EMAIL = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
   
   public final static String OPENREMOTE_WEBSITE= "http://www.openremote.org";
   
   /** The Constant OPENREMOTE_NAMESPACE. */
   public final static String OPENREMOTE_NAMESPACE= "or";
   
   /** The Constant SCHEMA_LANGUAGE. */
   public final static String SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
   
   /** The Constant XML_SCHEMA. */
   public final static String XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
   
   /** The Constant SCHEMA_SOURCE. */
   public final static String SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
   
   public static final String HTTP_BASIC_AUTH_HEADER_NAME= "Authorization";
   
   public static final String HTTP_BASIC_AUTH_HEADER_VALUE_PREFIX= "Basic ";
   
   public static final String REGISTRATION_ACTIVATION_EMAIL_VM_NAME= "registration-activation-email.vm";
   
   public static final String REGISTRATION_INVITATION_EMAIL_VM_NAME= "registration-invitation-email.vm";
   
   public static final String FORGET_PASSWORD_EMAIL_VM_NAME= "forget-password-email.vm";
   
   public static final String DEFAULT_FONT_FAMILY = "tahoma,arial,verdana,sans-serif";
   
   public static final String PORTRAIT = "portrait";
   
   public static final String LANDSCAPE = "landscape";
   
   public static final String CURRETN_ROLE = "currentRole";
   
   public static final String CURRETN_RESOURCE_PATH = "currentResourcePath";
   
   public static final String IPHONE_TYPE = "iPhone";
   
   public static final String IPAD_TYPE = "iPad";
   
   public static final String ROLE_MODELER_DISPLAYNAME = "Building Modeler";
   public static final String ROLE_DESIGNER_DISPLAYNAME = "UI Designer";
   public static final String ROLE_MODELER_DESIGNER_DISPLAYNAME = "Building Modeler & UI Designer";
   public static final String ROLE_ADMIN_DISPLAYNAME = "Admin";
   
   public static final String IRFILE_UPLOAD_ERROR = "File Upload Error :";
}
