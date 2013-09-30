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
package org.openremote.modeler.client.rpc;


import java.util.Collection;
import java.util.List;

import org.openremote.modeler.client.model.AutoSaveResponse;
import org.openremote.modeler.client.utils.PanelsAndMaxOid;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.component.UISlider;
import org.openremote.modeler.exception.BeehiveNotAvailableException;
import org.openremote.modeler.exception.FileOperationException;
import org.openremote.modeler.exception.IllegalRestUrlException;
import org.openremote.modeler.exception.ResourceFileLostException;
import org.openremote.modeler.exception.UIRestoreException;
import org.openremote.modeler.exception.XmlExportException;
import org.openremote.modeler.shared.GraphicalAssetDTO;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


/**
 * The Interface supports some utility methods.
 * 
 * @author handy.wang
 */
@RemoteServiceRelativePath("utils.smvc")
public interface UtilsRPCService extends RemoteService {


   /**
    * Export files into a compressed file, include panel.xml, controller.xml, panels.obj and some images.
    * 
    * @param maxId the max id
    * @param activityList the activity list
    * 
    * @return the string
    */
   String exportFiles(long maxId, List<Panel> panelList) throws XmlExportException,FileOperationException,ResourceFileLostException;
   
   /**
    * Get beehive rest icon url.
    * 
    * @return the string
    */
   String beehiveRestIconUrl();

   /**
    * Auto save ui designer layout into server and beehive.
    * It is saved as a zip file.
    * 
    * @param panels the panels
    * @param maxID the max id
    * 
    * @return the auto save response
    * 
    * @throws BeehiveNotAvailableException the beehive not available exception
    * @throws FileOperationException the file operation exception
    * @throws IllegalRestUrlException the illegal rest url exception
    * @throws ResourceFileLostException the resource file lost exception
    */
   AutoSaveResponse autoSaveUiDesignerLayout(Collection<Panel> panels, long maxID) throws BeehiveNotAvailableException,FileOperationException,IllegalRestUrlException,ResourceFileLostException;
   
   /**
    * Manually save ui designer layout into server and beehive.
    * It is saved as a zip file.
    * 
    * @param panels the panels
    * @param maxID the max id
    * 
    * @return the auto save response
    * 
    * @throws BeehiveNotAvailableException the beehive not available exception
    * @throws FileOperationException the file operation exception
    * @throws IllegalRestUrlException the illegal rest url exception
    * @throws ResourceFileLostException the resource file lost exception
    */
   AutoSaveResponse saveUiDesignerLayout(Collection<Panel> panels, long maxID) throws BeehiveNotAvailableException,FileOperationException,IllegalRestUrlException,ResourceFileLostException;
   
   /**
    * Restore panels and max oid from server or beehive.
    * They are stored as a zip file.
    * 
    * @return the panels and max oid
    * 
    * @throws UIRestoreException the UI restore exception
    * @throws BeehiveNotAvailableException the beehive not available exception
    */
   PanelsAndMaxOid restore() throws UIRestoreException,BeehiveNotAvailableException;
   
   /**
    * Check the panels if can restore from a stored zip file.
    * 
    * @return true, if successful
    */
   boolean canRestore();
   
   Collection<Panel> loadPanelsFromSession() throws UIRestoreException,BeehiveNotAvailableException;
   
   List<Group> loadGroupsFromSession();
   
   List<Screen> loadScreensFromSession();
   
   /**
    * Load layout component's max id from session.
    * 
    */
   Long loadMaxID();

   String downLoadImage(String url);
   
   UISlider rotateImage(UISlider uiSlider);
   
   /**
    * Gets the account path that under the resource path.
    * 
    * @return the account path
    */
   String getAccountPath();
   
   /**
    * Gets current user's on line test url.
    * It used for testing WYSISWYG in all kinds of panel(iPhone, Android, Webconsole, etc.).
    * 
    * @return the on line test url
    */
   String getOnLineTestURL ();
   
   List<GraphicalAssetDTO>getUserImagesURLs();
   
   void deleteImage(String imageName);
   
   String getIrServiceRestRootURL();
}
