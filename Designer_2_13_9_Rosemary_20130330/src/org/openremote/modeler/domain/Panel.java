/*
 * OpenRemote, the Home of the Digital Home.
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
package org.openremote.modeler.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Transient;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.domain.component.ImageSource;
import org.openremote.modeler.domain.component.UITabbar;
import org.openremote.modeler.domain.component.UITabbarItem;
import org.openremote.modeler.touchpanel.TouchPanelCanvasDefinition;
import org.openremote.modeler.touchpanel.TouchPanelDefinition;
import org.openremote.modeler.touchpanel.TouchPanelTabbarDefinition;

import com.extjs.gxt.ui.client.data.BeanModelTag;

import flexjson.JSON;

/**
 * TODO
 *
 * The Panel define the different device touch panel, such as iPhone panel, wall panel etc.
 * It includes name, groupRefs, global tabbarItems and touchPanelDefinition.
 */
public class Panel extends BusinessEntity implements BeanModelTag
{

  // Serialization --------------------------------------------------------------------------------

  private static final long serialVersionUID = 6122936524433692761L;



  // Class Members --------------------------------------------------------------------------------

// TODO
//   - Commenting out logging for now -- domain object is compiled to javascript and executed
//     on client browser with its own logging environment. The log facade needs to be massaged
//     into something that can be compiled to JS and remote logged via GWT RPC on server side.
  
//
//  /**
//   * Log category for panel domain objects.
//   */
//  private final static LogFacade domainLog =
//      LogFacade.getInstance(LogFacade.Category.PANEL_MODEL);
//
//  /**
//   * Specialized log category for errors serious enough that administrators/developers should
//   * be notified if they occur.
//   */
//  private final static AdministratorAlert admin =
//      AdministratorAlert.getInstance(LogFacade.Category.PANEL_ADMIN_ALERT);


  private static int defaultNameIndex = 1;

  public static void increaseDefaultNameIndex()
  {
     defaultNameIndex++;
  }

  public static String getNewDefaultName()
  {
     return "panel" + defaultNameIndex;
  }


  /**
   * TODO :
   *
   *   This implementation has been moved back to the domain package hierarchy from
   *   ResourceServiceImpl.java (where it didn't belong). This should help constraining
   *   excessive API dependencies and API details spreading to other classes that was
   *   completely unnecessary.
   *
   *   This should still not be a static class method but an instance implementation.
   *   However, not introducing it yet at instance level due to the very fragile Java
   *   serialization based state caching format -- to reduce potential disruptions will
   *   leave further refactorings unless and until a more robust state serialization
   *   is in place (e.g. XML stream) or the serialization binary format has been
   *   sufficiently covered in test suite.
   *
   *   Further, the additional processing of groups and screen pairs should be pushed
   *   down to their respective domain classes.
   *
   * TODO :
   *
   *   Ideally all images could be handled with ImageSource domain object, rather than
   *   as a set of strings which is what's returned now. Need to review the domain object
   *   designer to ensure ImageSources are used universally.
   *
   *
   * @param panel   the panel which images are returned (including all component images)
   *
   * @return    a set of filenames (not including their account specific file paths in
   *            local cache) that identify the store images on the design
   */
  public static Set<String> getAllImageNames(Panel panel)
  {
    Set<String> imageNames = new HashSet<String>();

    // All images of a panel-wide tab bar items...

    if (panel.getTabbar() != null)
    {
      imageNames.addAll(getTabbarImageNames(panel.getTabbar()));
    }

    // All images related to a custom panel definition...

    if (Constants.CUSTOM_PANEL.equals(panel.getType()))
    {
      imageNames.addAll(getCustomPanelImages(panel));
    }


    List<GroupRef> groupRefs = panel.getGroupRefs();

    if (groupRefs == null)
    {
//      domainLog.warn("Panel ''{0}'' has no groups.", panel.getDisplayName());

      return imageNames;
    }


    // Iterate through all the groups in a panel...

    for (GroupRef groupRef : groupRefs)
    {
      Group group = groupRef.getGroup();


      // If has a group specific tab bar definition, get all tab bar item images...

      if (group.getTabbar() != null)
      {
        imageNames.addAll(getTabbarImageNames(group.getTabbar()));
      }

      List<ScreenPairRef> screenPairRefs = group.getScreenRefs();

      if (screenPairRefs == null)
      {
//        domainLog.debug("Panel ''{0}'' has no screens defined.", panel.getDisplayName());

        continue;
      }


      // Iterate through all screens within a group...

      for (ScreenPairRef screenPairRef : screenPairRefs)
      {
        ScreenPair screenPair = screenPairRef.getScreen();

        if (ScreenPair.OrientationType.PORTRAIT.equals(screenPair.getOrientation()))
        {
          Collection<ImageSource> sources = screenPair.getPortraitScreen().getAllImageSources();

          for (ImageSource source : sources)
          {
            imageNames.add(source.getImageFileName());
          }
        }

        else if (ScreenPair.OrientationType.LANDSCAPE.equals(screenPair.getOrientation()))
        {
          Collection<ImageSource> sources = screenPair.getLandscapeScreen().getAllImageSources();

          for (ImageSource source : sources)
          {
            imageNames.add(source.getImageFileName());
          }
        }

        else if (ScreenPair.OrientationType.BOTH.equals(screenPair.getOrientation()))
        {
          Collection<ImageSource> sources = screenPair.getPortraitScreen().getAllImageSources();

          for (ImageSource source : sources)
          {
            imageNames.add(source.getImageFileName());
          }

          sources = screenPair.getLandscapeScreen().getAllImageSources();

          for (ImageSource source : sources)
          {
            imageNames.add(source.getImageFileName());
          }
        }
      }
    }

    return imageNames;
  }


  /**
   * TODO :
   *
   *   should be part of TabBar API -- see comments above why not refactored the move yet
   */
  private static Collection<String> getTabbarImageNames(UITabbar tabbar)
  {
    Collection<String> imageNames = new HashSet<String>(5);

    if (tabbar == null)
    {
//      domainLog.debug("Got null tab bar, no additional images added.");

      return imageNames;
    }

    List<UITabbarItem> items = tabbar.getTabbarItems();

    if (items == null)
    {
//      domainLog.warn("TabBar.getItems() should return an empty list, not null");

      return imageNames;
    }

    for (UITabbarItem item : items)
    {
      ImageSource image = item.getImage();

      if (image != null && !image.isEmpty())
      {
        imageNames.add(image.getImageFileName());

//        domainLog.debug("Added tab bar image ''{0}'' to image collection.", image.getImageFileName());
      }
    }

    return imageNames;
  }


  /**
   * TODO :
   *
   *   should be part of TouchPanel API -- see comments why not refactored the move yet
   */
  private static Collection<String> getCustomPanelImages(Panel panel)
  {
    Collection<String> imageNames = new HashSet<String>(2);

    if (panel == null)
    {
      return imageNames;
    }

    // TODO :
    //   Wrapping this into try-catch block because the domain model looks so poorly written
    //   with regards to uninitialized variables and potential NPE's and it's too much trouble
    //   to guard against them all -- the domain model needs a thorough rewrite and testing.
    
//    try
//    {
      String vBgImage = panel.getTouchPanelDefinition().getBgImage();
      String hBgImage = panel.getTouchPanelDefinition().getHorizontalDefinition().getBgImage();
      String tbImage  = panel.getTouchPanelDefinition().getTabbarDefinition().getBackground()
                             .getImageFileName();

      if (vBgImage != null && !vBgImage.isEmpty())
      {
        imageNames.add(vBgImage);
      }

      if (hBgImage != null && !hBgImage.isEmpty())
      {
        imageNames.add(hBgImage);
      }

      if (tbImage != null && !tbImage.isEmpty() &&
          !TouchPanelTabbarDefinition.IPHONE_TABBAR_BACKGROUND.endsWith(tbImage))
      {
        imageNames.add(tbImage);
      }
//    }
//
//    catch (Throwable t)
//    {
//      admin.alert("Error in Designer domain API usage : {0}", t, t.getMessage());
//    }

    return imageNames;
  }



  // Instance Fields ------------------------------------------------------------------------------

  private String name;
  private List<GroupRef> groupRefs = new ArrayList<GroupRef>();
  private List<UITabbarItem> tabbarItems = new ArrayList<UITabbarItem>();
  private TouchPanelDefinition touchPanelDefinition;
  private UITabbar tabbar = null;


  // Instance Methods -----------------------------------------------------------------------------

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }


  public List<GroupRef> getGroupRefs()
  {
    return groupRefs;
  }

  public void setGroupRefs(List<GroupRef> groupRefs)
  {
    this.groupRefs = groupRefs;
  }

  public void addGroupRef(GroupRef groupRef)
  {
    groupRefs.add(groupRef);
  }

  public void insertGroupRef(GroupRef before, GroupRef target)
  {
    int index = groupRefs.indexOf(before);
    groupRefs.add(index, target);
  }

  public void removeGroupRef(GroupRef groupRef)
  {
    groupRefs.remove(groupRef);
  }

  public void clearGroupRefs()
  {
      groupRefs.clear();
  }


  public TouchPanelDefinition getTouchPanelDefinition()
  {
    return touchPanelDefinition;
  }

  public void setTouchPanelDefinition(TouchPanelDefinition touchPanelDefinition)
  {
    this.touchPanelDefinition = touchPanelDefinition;
  }



  public UITabbar getTabbar()
  {
    return tabbar;
  }

  public void setTabbar(UITabbar tabbar)
  {
    this.tabbar = tabbar;
  }

  public List<UITabbarItem> getTabbarItems()
  {
    return tabbarItems;
  }

  public void setTabbarItems(List<UITabbarItem> tabbarItems)
  {
    this.tabbarItems = tabbarItems;
  }


  /* (non-Javadoc)
   * @see org.openremote.modeler.domain.BusinessEntity#getDisplayName()
   */
  @Transient public String getDisplayName()
  {
    TouchPanelCanvasDefinition canvas = touchPanelDefinition.getCanvas();

    return name + "(" + touchPanelDefinition.getName() + "," +
           canvas.getWidth() + "X" + canvas.getHeight() + ")";
  }


  public String getType()
  {
    if (touchPanelDefinition != null)
    {
      return touchPanelDefinition.getType();
    }

    return Constants.CUSTOM_PANEL;
  }

  /**
   * Gets the groups from groupRefs, a groupRef contain a group.
   *
   * @return the groups
   */
  @Transient @JSON(include = false)
  public List<Group> getGroups()
  {
    List<Group> groups = new ArrayList<Group>();

    for (GroupRef groupRef : groupRefs)
    {
       groups.add(groupRef.getGroup());
    }

    return groups;
  }

}
