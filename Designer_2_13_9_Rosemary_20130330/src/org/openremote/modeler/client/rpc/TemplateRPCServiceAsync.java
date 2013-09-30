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

import java.util.List;

import org.openremote.modeler.client.utils.ScreenFromTemplate;
import org.openremote.modeler.domain.ScreenPair;
import org.openremote.modeler.domain.Template;

import com.google.gwt.user.client.rpc.AsyncCallback;
/**
 * 
 * @author javen
 *
 */
public interface TemplateRPCServiceAsync {
   void getTemplates(final boolean isFromPrivate,AsyncCallback<List<Template>> callback) /*throws TestException*/;
   void saveTemplate(final Template template,AsyncCallback<Template> callback);
   void buildScreeFromTemplate(final Template template,AsyncCallback<ScreenFromTemplate> callback);
   void deleteTemplate(final long templateId,AsyncCallback<Boolean> callback);
   void searchTemplates(final String keywords,final int page, AsyncCallback<List<Template>> callback);
   void buildScreen(final Template template,AsyncCallback<ScreenPair> callback);
   void updateTemplate(final Template template,AsyncCallback<Template> callback);
}
