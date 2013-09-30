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
package org.openremote.modeler.client.gxtextends;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionProvider;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

/**
 * A <code>SourceSelectionChangeListenerExt</code> that ignores its own selection
 * events. Useful when registering the provider with the
 * <code>SelectionServiceExt</code>.
 */
public class SourceSelectionChangeListenerExt extends SelectionChangedListener<BeanModel> {
  
  /** The provider. */
  private SelectionProvider<BeanModel> provider;

  /**
   * The Class extend <code>SelectionChangedListener</code> to support <code>BeanModel</code> type argument.
   * 
   * @param provider the provider
   */
  public SourceSelectionChangeListenerExt(SelectionProvider<BeanModel> provider) {
    this.provider = provider;
  }

  /**
   * {@inheritDoc}
   */
  public void selectionChanged(final SelectionChangedEvent<BeanModel> event) {
    SelectionProvider<BeanModel> eventProvider = event.getSelectionProvider();
    // add "size > 0" check, if "size < 0", there would be a exception "Array index out of range: 0".
    if (eventProvider != provider && provider.getSelection().size() > 0) {
      if (provider.getSelection().get(0) != eventProvider.getSelection().get(0)) {
        DeferredCommand.addCommand(new Command() {
          public void execute() {
            provider.setSelection(event.getSelection());
          }
        });

      }
    }
  }
}
