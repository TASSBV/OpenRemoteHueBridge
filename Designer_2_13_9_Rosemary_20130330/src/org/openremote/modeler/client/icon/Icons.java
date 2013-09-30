/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2012, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.openremote.modeler.client.icon;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ImageBundle;

/**
 * The Interface defines the application's image bundle.
 */
public interface Icons extends ImageBundle {

   @Resource("folder.gif")
   AbstractImagePrototype folder();

   @Resource("cmd.png")
   AbstractImagePrototype deviceCmd();

   @Resource("delete.png")
   AbstractImagePrototype delete();

   @Resource("tv.png")
   AbstractImagePrototype device();

   @Resource("edit.png")
   AbstractImagePrototype edit();

   @Resource("add.png")
   AbstractImagePrototype add();

   @Resource("database_go.png")
   AbstractImagePrototype importFromDB();

   @Resource("brick_add.png")
   AbstractImagePrototype macroAddIcon();

   @Resource("brick_delete.png")
   AbstractImagePrototype macroDeleteIcon();

   @Resource("brick_edit.png")
   AbstractImagePrototype macroEditIcon();

   @Resource("brick.png")
   AbstractImagePrototype macroIcon();

   @Resource("add_delay.png")
   AbstractImagePrototype addDelayIcon();

   @Resource("edit_delay.png")
   AbstractImagePrototype editDelayIcon();

   @Resource("delay.png")
   AbstractImagePrototype delayIcon();

   @Resource("group.png")
   AbstractImagePrototype groupIcon();

   @Resource("screen.png")
   AbstractImagePrototype screenIcon();

   @Resource("devices.png")
   AbstractImagePrototype devicesRoot();

   @Resource("macros.png")
   AbstractImagePrototype macrosRoot();

   @Resource("log_out.png")
   AbstractImagePrototype logout();

   @Resource("save.gif")
   AbstractImagePrototype saveIcon();

   @Resource("button.png")
   AbstractImagePrototype buttonIcon();

   @Resource("switch.png")
   AbstractImagePrototype switchIcon();

   @Resource("panel.png")
   AbstractImagePrototype panelIcon();

   @Resource("grid.png")
   AbstractImagePrototype gridIcon();

   @Resource("tabbar_config.png")
   AbstractImagePrototype tabbarConfigIcon();
   
   @Resource("tabbar_item.png")
   AbstractImagePrototype tabbarItemIcon();

   @Resource("slider.png")
   AbstractImagePrototype sliderIcon();

   @Resource("label.gif")
   AbstractImagePrototype labelIcon();

   @Resource("image.png")
   AbstractImagePrototype imageIcon();

   @Resource("sensor.png")
   AbstractImagePrototype sensorIcon();

   @Resource("config.png")
   AbstractImagePrototype configIcon();

   @Resource("template.gif")
   AbstractImagePrototype templateIcon();

   @Resource("bm.png")
   AbstractImagePrototype bmIcon();

   @Resource("ud.png")
   AbstractImagePrototype udIcon();

   @Resource("export_zip.png")
   AbstractImagePrototype exportAsZipIcon();
   
   @Resource("screen_link.png")
   AbstractImagePrototype screenLinkIcon();
   
   @Resource("url.png")
   AbstractImagePrototype onLineTestIcon();
   
   @Resource("user.png")
   AbstractImagePrototype userIcon();

}
