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
package org.openremote.modeler.selenium;

/**
 * Used to collect all the debug identifiers of Selenium test on widget, so that
 * if one is changed, its test can detect it. This can also keep unique for all
 * identifiers. Selenium use Javascript to test your app, GWT allows you to set
 * debug id for a widget, the debug id is the id of a html element.
 * 
 * @author Dan 2009-8-6
 */
public class DebugId {

    /**
     * Not be instantiated.
     */
    private DebugId() {
    }

    /* DevicePanel */
    /** The Constant DEVICE_TREE_CONTAINER. */
    public static final String DEVICE_TREE_CONTAINER = "deviceTreeContainer";

    /** The Constant DEVICE_NEW_BTN. */
    public static final String DEVICE_NEW_BTN = "DeviceNewBtn";

    /** The Constant NEW_DEVICE_MENU_ITEM. */
    public static final String NEW_DEVICE_MENU_ITEM = "newDeviceMenuItem";

    /** The Constant DEVICE_EDIT_BTN. */
    public static final String DEVICE_EDIT_BTN = "DeviceEditBtn";

    /** The Constant DELETE_DEVICE_BUTTON. */
    public static final String DELETE_DEVICE_BUTTON = "deleteDeviceButton";

    /** The Constant NEW_COMMAND_ITEM. */
    public static final String NEW_COMMAND_ITEM = "newCommandItem";

    /* DeviceWindow */
    /** The Constant NEW_DEVICE_WINDOW. */
    public static final String NEW_DEVICE_WINDOW = "newDeviceWindow";

    /** The Constant DEVICE_SUBMIT_BTN. */
    public static final String DEVICE_SUBMIT_BTN = "deviceSubmitBtn";
    public static final String DEVICE_FINISH_BTN = "deviceFinishBtn";

    /** The Constant DEVICE_NAME_FIELD. */
    public static final String DEVICE_NAME_FIELD = "deviceNameField";

    /** The Constant DEVICE_VENDOR_FIELD. */
    public static final String DEVICE_VENDOR_FIELD = "deviceVendorField";

    /** The Constant DEVICE_MODEL_FIELD. */
    public static final String DEVICE_MODEL_FIELD = "deviceModelField";

    /* DeviceCommand Window */

    /** The Constant DEVICE_COMMAND_NAME_FIELD. */
    public static final String DEVICE_COMMAND_NAME_FIELD = "deviceCommandNameField";

    /** The Constant DEVICE_COMMAND_PROTOCOL_FIELD. */
    public static final String DEVICE_COMMAND_PROTOCOL_FIELD = "deviceCommandProtocolField";

    /* DeviceMacro Panel */
    /** The Constant DEVICE_MACRO_PANEL. */
    public static final String DEVICE_MACRO_PANEL_HEADER = "DeviceMacroPanelHeader";

    /** The Constant NEW_MACRO_BTN. */
    public static final String NEW_MACRO_BTN = "newMacroBtn";

    /** The Constant DEVICE_MACRO_NAME_FIELD. */
    public static final String DEVICE_MACRO_NAME_FIELD = "deviceMacroNameField";

    /** Activity */

    /** The Constant NEW_ACTIVITY_MENU_ITEM. */
    public static final String NEW_ACTIVITY_MENU_ITEM = "newActivityMenuItem";

    /** The Constant NEW_ACTIVITY_WINDOW. */
    public static final String NEW_ACTIVITY_WINDOW = "newActivityWindow";

    /** The Constant EDIT_ACTIVITY_WINDOW. */
    public static final String EDIT_ACTIVITY_WINDOW = "editActivityWindow";

    /** The Constant NEW_ACTIVITY_WINDOW_SUBMIT_BTN. */
    public static final String NEW_ACTIVITY_WINDOW_SUBMIT_BTN = "newActivityWindowSubmitBtn";

    /** The Constant NEW_ACTIVITY_WINDOW_RESET_BTN. */
    public static final String NEW_ACTIVITY_WINDOW_RESET_BTN = "newActivityWindowResetBtn";

    /** The Constant ACTIVITY_NAME_FIELD. */
    public static final String ACTIVITY_NAME_FIELD = "activityNameField";

    /** The Constant ACTIVITY_TREE_CONTAINER. */
    public static final String ACTIVITY_TREE_CONTAINER = "activityTreeContainer";

    /** The Constant ACTIVITY_NEW_BTN. */
    public static final String ACTIVITY_NEW_BTN = "activityNewButton";

    /** The Constant ACTIVITY_EDIT_BTN. */
    public static final String ACTIVITY_EDIT_BTN = "activityEditButton";

    /** The Constant ACTIVITY_DELETE_BTN. */
    public static final String ACTIVITY_DELETE_BTN = "activityDeleteButton";

    /** The Constant ACTIVITY_EXPORT_BTN. */
    public static final String ACTIVITY_EXPORT_BTN = "activityExportButton";

    /** The Constant ACTIVITY_IMPORT_BTN. */
    public static final String ACTIVITY_IMPORT_BTN = "activityImportButton";

    /* Screen Panel */
    public static final String SCREEN_NEW_BTN = "screenNewBtn";
    public static final String SCREEN_EDIT_BTN = "screenEditBtn";
    public static final String SCREEN_DELETE_BTN = "screenDeleteBtn";

    /* Screen Window */
    public static final String SCREEN_NAME_FIELD = "screenNameField";
    public static final String SCREEN_PANEL_FIELD = "screenPanelField";
    public static final String SCREEN_BG_FIELD = "screenBgField";
    public static final String SCREEN_ABSOLUTE_RADIO = "screenAbsoluteRadio";
    public static final String SCREEN_GRID_RADIO = "screenGridRadio";
    public static final String SCREEN_GRID_ROW_FIELD = "screenGridRowField";
    public static final String SCREEN_GRID_COLUMN_FIELD = "screenGridColumnField";
    public static final String SCREEN_SUBMIT_BTN = "screenSubmitBtn";

    /* Group Panel */
    public static final String GROUP_PANEL_HEADER = "groupPanelHeader";

    /* Profile Panel */
    public static final String PROFILE_PANEL_HEADER = "profilePanelHeader";

    /** The Constant APPLICATION_FILE_BTN. */
    public static final String APPLICATION_FILE_BTN = "applicationFileBtn";

    /** The Constant IMPORT. */
    public static final String IMPORT = "import";

    /** The Constant EXPORT. */
    public static final String EXPORT = "export";

    /** The Constant LOGOUT. */
    public static final String LOGOUT = "logout";

    /** The Constant APPLICATION_HELP_BTN. */
    public static final String APPLICATION_HELP_BTN = "applicationHelpBtn";

    /** The Constant IMPORT_WINDOW. */
    public static final String IMPORT_WINDOW = "importWindow";

    /** The Constant IMPORT_WINDOW_UPLOAD_BTN. */
    public static final String IMPORT_WINDOW_UPLOAD_BTN = "importWindowUploadBtn";

    /** The Constant IMPORT_WINDOW_CANCEL_BTN. */
    public static final String IMPORT_WINDOW_CANCEL_BTN = "importWindowCancelBtn";

    /** The Constant Save. */
    public static final String SAVE = "save";

    /* Common window */
    public static final String COMMON_SUBMIT_BTN = "commonSubmitBtn";

    public static final String KNX_IMPORT_WINDOW_LOAD_BTN = "knxImportWindowLoadBtn";

    public static final String KNX_IMPORT_WINDOW_OK_BTN = "knxImportWindowOkBtn";

    public static final String KNX_IMPORT_WINDOW_CANCEL_BTN = "knxImportWindowCancelBtn";

    public static final String KNX_IMPORT_WINDOW_CLEAR_BTN = "knxImportWindowClearBtn";
    
    public static final String IRFILE_IMPORT_WINDOW_LOAD_BTN = "irFileImportWindowOkBtn";

   public static final String IRFILE_IMPORT_WINDOW_CLEAR_BTN = "irFileImportWindowClearBtn";
}
