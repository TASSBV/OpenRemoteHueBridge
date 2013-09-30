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
package org.openremote.modeler.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.text.MessageFormat;

import org.apache.log4j.Logger;
import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.client.model.AutoSaveResponse;
import org.openremote.modeler.client.rpc.UtilsRPCService;
import org.openremote.modeler.client.utils.PanelsAndMaxOid;
import org.openremote.modeler.client.utils.ScreenFromTemplate;
import org.openremote.modeler.configuration.PathConfig;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.Template;
import org.openremote.modeler.domain.component.ImageSource;
import org.openremote.modeler.domain.component.UISlider;
import org.openremote.modeler.exception.FileOperationException;
import org.openremote.modeler.exception.ConfigurationException;
import org.openremote.modeler.exception.NetworkException;
import org.openremote.modeler.exception.UIRestoreException;
import org.openremote.modeler.exception.XmlExportException;
import org.openremote.modeler.service.ResourceService;
import org.openremote.modeler.service.TemplateService;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.shared.GraphicalAssetDTO;
import org.openremote.modeler.utils.ImageRotateUtil;
import org.openremote.modeler.utils.XmlParser;
import org.openremote.modeler.cache.LocalFileCache;
import org.openremote.modeler.cache.ResourceCache;
import org.openremote.modeler.cache.CacheOperationException;
import org.openremote.modeler.logging.LogFacade;

/**
 * TODO
 * 
 * The server side implementation of the RPC service <code>DeviceRPCService</code>.
 * 
 * @author handy.wang
 */
@SuppressWarnings("serial")
public class  UtilsController extends BaseGWTSpringController implements UtilsRPCService {
   
   private static final String ROTATED_FLAG = "ROTATE";
   
   private static final Logger LOGGER = Logger.getLogger(UtilsController.class);
   private ResourceService resourceService;
   private TemplateService screenTemplateService;
   private UserService userService;
   
   private Configuration configuration;
   
   private static final String UI_DESIGNER_LAYOUT_PANEL_KEY = "panelList";

   /** The Constant for store group list in session. */
   private static final String UI_DESIGNER_LAYOUT_GROUP_KEY = "groupList";
   
   /** The Constant for store screen list in session. */
   private static final String UI_DESIGNER_LAYOUT_SCREEN_KEY = "screenList";
   
   private static final String UI_DESIGNER_LAYOUT_MAXID = "maxID";




  /**
   * TODO : MODELER-288 -- this should delegate to Beehive, not handled by designer
   */
  @Override public String exportFiles(long maxId, List<Panel> panelList)
  {
    //      return resourceService.downloadZipResource(maxId, this.getThreadLocalRequest().getSession().getId(), panelList);

    LogFacade log = LogFacade.getInstance(LogFacade.Category.EXPORT);

    resourceService.resolveDTOReferences(panelList);

    // TODO : this call should be internalized, see MODELER-287
    resourceService.initResources(panelList, maxId);

    resourceService.saveResourcesToBeehive(panelList);

    // TODO : should be injected
    ResourceCache cache = new LocalFileCache(configuration, userService.getCurrentUser());

    try
    {
      cache.update();
    }

    catch (ConfigurationException e)
    {
      String msg = MessageFormat.format(
          "Cannot complete export operation due to a configuration error : {0}", e.getMessage()
      );

      // TODO : log and throw because unclear if these runtime exceptions to UI are logged anywhere
      log.error(msg, e);

      throw new XmlExportException(msg, e);
    }

    catch (NetworkException e)
    {
      String msg = MessageFormat.format(
          "Network error prevented export operation from completing : {0}", e.getMessage()
      );

      // TODO : log and throw because unclear if these runtime exceptions to UI are logged anywhere
      log.error(msg, e);

      throw new XmlExportException(msg, e);
    }

    catch (CacheOperationException e)
    {
      String msg = MessageFormat.format(
          "Resource export from local cache failed : {0}", e.getMessage()
      );

      // TODO : log and throw because unclear if these runtime exceptions to UI are logged anywhere
      log.error(msg, e);

      throw new XmlExportException(msg, e);
    }

    // TODO : should never come from cache in the first place but from Beehive
    return resourceService.downloadZipResource(maxId, getThreadLocalRequest().getSession().getId(), panelList);
  }


   /**
    * Gets the resource service.
    * 
    * @return the resource service
    */
   public ResourceService getResourceService() {
      return resourceService;
   }

   /**
    * Sets the resource service.
    * 
    * @param resourceService the new resource service
    */
   public void setResourceService(ResourceService resourceService) {
      this.resourceService = resourceService;
   }

   /**
    * Gets the configuration.
    * 
    * @return the configuration
    */
   public Configuration getConfiguration() {
      return configuration;
   }

   /**
    * Sets the configuration.
    * 
    * @param configuration the new configuration
    */
   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }
   
   
   public void setScreenTemplateService(TemplateService screenTemplateService) {
      this.screenTemplateService = screenTemplateService;
   }

   /**
    * {@inheritDoc}
    */
   public String beehiveRestIconUrl() {
      return configuration.getBeehiveRestIconUrl();
   }

   
   public UserService getUserService() {
      return userService;
   }

   public void setUserService(UserService userService) {
      this.userService = userService;
   }

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   public AutoSaveResponse autoSaveUiDesignerLayout(Collection<Panel> panels, long maxID) {
      AutoSaveResponse autoSaveResponse = new AutoSaveResponse();
      
      List<Panel> oldPanels = new ArrayList<Panel>();
      if (getThreadLocalRequest().getSession().getAttribute(UI_DESIGNER_LAYOUT_PANEL_KEY) != null) {
         oldPanels = (List<Panel>) getThreadLocalRequest().getSession().getAttribute(UI_DESIGNER_LAYOUT_PANEL_KEY);
      }
      if (panels != null && panels.size() > 0) {
        
        resourceService.resolveDTOReferences(panels);
        
         if (!resourceService.getPanelsJson(panels).equals(resourceService.getPanelsJson(oldPanels))) {
            synchronized (getThreadLocalRequest().getSession()) {
               getThreadLocalRequest().getSession().setAttribute(UI_DESIGNER_LAYOUT_PANEL_KEY, panels);
               getThreadLocalRequest().getSession().setAttribute(UI_DESIGNER_LAYOUT_MAXID, maxID);
               autoSaveResponse.setUpdated(true);
               resourceService.initResources(panels, maxID);
               resourceService.saveResourcesToBeehive(panels);
            }
            LOGGER.info("Auto save UI designerLayout sucessfully");
         }
      }
      return autoSaveResponse;
   }

   @Override
   public AutoSaveResponse saveUiDesignerLayout(Collection<Panel> panels, long maxID) {
      AutoSaveResponse autoSaveResponse = new AutoSaveResponse();

      if (panels != null) {
         synchronized (getThreadLocalRequest().getSession()) {
            getThreadLocalRequest().getSession().setAttribute(UI_DESIGNER_LAYOUT_PANEL_KEY, panels);
            getThreadLocalRequest().getSession().setAttribute(UI_DESIGNER_LAYOUT_MAXID, maxID);
            autoSaveResponse.setUpdated(true);
            
            resourceService.resolveDTOReferences(panels);
            
            resourceService.initResources(panels, maxID);
            resourceService.saveResourcesToBeehive(panels);
         }
         LOGGER.info("manual save UI DesingerLayout successfully");
      }
      autoSaveResponse.setUpdated(true);
      return autoSaveResponse;
   }

   @Override
   public Collection<Panel> loadPanelsFromSession() {
      synchronized (getThreadLocalRequest().getSession()) {
//      Object obj = getThreadLocalRequest().getSession().getAttribute(UI_DESIGNER_LAYOUT_PANEL_KEY);
//      if(obj == null){
         PanelsAndMaxOid panelsAndMaxOid = restore();
//         obj = panelsAndMaxOid !=null ? panelsAndMaxOid.getPanels(): null; 
//      }
         return panelsAndMaxOid.getPanels();
      }
   }
   
   @SuppressWarnings("unchecked")
   @Override
   public List<Group> loadGroupsFromSession() {
      Object obj = getThreadLocalRequest().getSession().getAttribute(UI_DESIGNER_LAYOUT_GROUP_KEY);
      return (obj == null) ? new ArrayList<Group>() : (List<Group>)obj;
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<Screen> loadScreensFromSession() {
      Object obj = getThreadLocalRequest().getSession().getAttribute(UI_DESIGNER_LAYOUT_SCREEN_KEY);
      return (obj == null) ? new ArrayList<Screen>() : (List<Screen>)obj;
   }

   @Override
   public Long loadMaxID() {
      Object obj = getThreadLocalRequest().getSession().getAttribute(UI_DESIGNER_LAYOUT_MAXID);
      return (obj == null) ? 0 : (Long)obj;
   }

   @Override
   public String downLoadImage(String url) {
      PathConfig pathConfig = PathConfig.getInstance(configuration);
      File userFolder = new File(pathConfig.userFolder(userService.getAccount()));
      if (!userFolder.exists()) {
         if (! userFolder.mkdirs()) {
            throw new FileOperationException("Can't create user folder for user: "+userService.getCurrentUser().getUsername());
         }
      }
      File imageFile = new File(userFolder, url.substring(url.lastIndexOf("/") + 1));
      try {
         XmlParser.downloadFile(url, imageFile);
      } catch (IOException e) {
         LOGGER.error("Download image " + url + " occur IOException.", e);
      }
      return resourceService.getRelativeResourcePathByCurrentAccount(imageFile.getName());
   }

   @Override
   public PanelsAndMaxOid restore() {
      PanelsAndMaxOid result = resourceService.restore();
      if(result!=null){
         Collection<Panel> panels = result.getPanels();
         long maxOid = result.getMaxOid();
         getThreadLocalRequest().getSession().setAttribute(UI_DESIGNER_LAYOUT_PANEL_KEY, panels);
         getThreadLocalRequest().getSession().setAttribute(UI_DESIGNER_LAYOUT_MAXID, maxOid);
      }
      return result;
   }

   @Override
   public boolean canRestore() {
      return resourceService.canRestore();
   }
   
   public ScreenFromTemplate buildScreenFromTemplate(Template template){
      return screenTemplateService.buildFromTemplate(template);
   }

   @Override
   public UISlider rotateImage(UISlider uiSlider) {
      PathConfig pathConfig = PathConfig.getInstance(configuration);
      String userFolderPath = pathConfig.userFolder(userService.getAccount());
      File userFolder = new File(userFolderPath);
      File minImageFile = null;
      File minTrackImageFile = null;
      File thumbImageFile = null;
      File maxTrackImageFile = null;
      File maxImageFile = null;
      if (uiSlider.getMinImage() != null && !uiSlider.getMinImage().isEmpty()) {
         minImageFile = new File(userFolder,uiSlider.getMinImage().getImageFileName());
      }
      if (uiSlider.getMinTrackImage() != null && !uiSlider.getMinTrackImage().isEmpty()) {
         minTrackImageFile = new File(userFolder, uiSlider.getMinTrackImage().getImageFileName());
      }
      if (uiSlider.getThumbImage() != null && !uiSlider.getThumbImage().isEmpty()) {
         thumbImageFile = new File(userFolder, uiSlider.getThumbImage().getImageFileName());
      }
      if (uiSlider.getMaxTrackImage() != null && !uiSlider.getMaxTrackImage().isEmpty()) {
         maxTrackImageFile = new File(userFolder, uiSlider.getMaxTrackImage().getImageFileName());
      }
      if (uiSlider.getMaxImage() != null && !uiSlider.getMaxImage().isEmpty()) {
         maxImageFile = new File(userFolder,uiSlider.getMaxImage().getImageFileName());
      }
      double degree = uiSlider.isVertical()?90:-90;
      for (File f : userFolder.listFiles()) {
         if (f.equals(minImageFile)) {
            uiSlider.setMinImage(new ImageSource(getImageNameAfterRotate(minImageFile,degree)));
         } else if (f.equals(minTrackImageFile)) {
            uiSlider.setMinTrackImage(new ImageSource(getImageNameAfterRotate(minTrackImageFile,degree)));
         } else if (f.equals(thumbImageFile)) {
            uiSlider.setThumbImage(new ImageSource(getImageNameAfterRotate(thumbImageFile,degree)));
         } else if (f.equals(maxTrackImageFile)) {
            uiSlider.setMaxTrackImage(new ImageSource(getImageNameAfterRotate(maxTrackImageFile,degree)));
         } else if (f.equals(maxImageFile)) {
            uiSlider.setMaxImage(new ImageSource(getImageNameAfterRotate(maxImageFile,degree)));
         }
      }
      return uiSlider;
   }
   
   private String getImageNameAfterRotate(File imageFileInUserFolder,double degree) {
      if (imageFileInUserFolder.getName().contains(ROTATED_FLAG)) {
         File beforeRotatedFile = new File(imageFileInUserFolder.getParent()+File.separator+(imageFileInUserFolder.getName().replace(ROTATED_FLAG, "")));
         if (beforeRotatedFile.exists()) {
            return getAccountPath()+beforeRotatedFile.getName();
         }
      }
      File fileAfterRotated = ImageRotateUtil.rotate(imageFileInUserFolder, imageFileInUserFolder.getParent() + File.separator + ROTATED_FLAG
            +imageFileInUserFolder.getName(), degree);
      return fileAfterRotated==null?getAccountPath()+imageFileInUserFolder.getName():getAccountPath()+fileAfterRotated.getName();
   }

   public String getAccountPath() {
      String accountPath = resourceService.getRelativeResourcePathByCurrentAccount("account");
      return accountPath.substring(0, accountPath.lastIndexOf("/") + 1);
   }
   
   public String getOnLineTestURL () {
      return configuration.getBeehiveRESTRootUrl()+"user/"+userService.getCurrentUser().getUsername();
   }
   
   public List<GraphicalAssetDTO>getUserImagesURLs() {
     return resourceService.getUserImagesURLs();
   }
   
   public void deleteImage(String imageName) {
     resourceService.deleteImage(imageName);
   }
   
   public String getIrServiceRestRootURL() {
     return configuration.getIrServiceRESTRootUrl();
   }
}
