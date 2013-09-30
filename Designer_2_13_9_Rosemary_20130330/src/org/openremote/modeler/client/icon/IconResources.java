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
package org.openremote.modeler.client.icon;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface IconResources extends ClientBundle {

  IconResources INSTANCE = GWT.create(IconResources.class); 
  
  @Source("HorizontalLeftAlignIcon.png")
  ImageResource horizontalLeftAlignIcon();

  @Source("HorizontalCenterAlignIcon.png")
  ImageResource horizontalCenterAlignIcon();

  @Source("HorizontalRightAlignIcon.png")
  ImageResource horizontalRightAlignIcon();

  @Source("VerticalTopAlignIcon.png")
  ImageResource verticalTopAlignIcon();

  @Source("VerticalCenterAlignIcon.png")
  ImageResource verticalCenterAlignIcon();

  @Source("VerticalBottomAlignIcon.png")
  ImageResource verticalBottomAlignIcon();

  @Source("SameSizeIcon.png")
  ImageResource sameSizeIcon();

  @Source("HorizontalSpreadIcon.png")
  ImageResource horizontalSpreadIcon();

  @Source("VerticalSpreadIcon.png")
  ImageResource verticalSpreadIcon();
  
  @Source("HorizontalCenterIcon.png")
  ImageResource horizontalCenterIcon();

  @Source("VerticalCenterIcon.png")
  ImageResource verticalCenterIcon();
  
  @Source("delete.png")
  ImageResource delete();
}
