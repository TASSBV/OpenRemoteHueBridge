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
package org.openremote.modeler.client.utils;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.RowEditor;

/**
 * @author marcus@openremote.org
 *
 * @param <M>  TODO
 */
public class NoButtonsRowEditor<M extends ModelData> extends RowEditor<M> {

    private ListStore<ModelData> store;

    public NoButtonsRowEditor(ListStore<ModelData> store) {
        super();
        super.renderButtons = false;
        this.store = store;
    }

    @Override
    public void stopEditing(boolean saveChanges) {
        super.stopEditing(saveChanges);
        if (isDirty()) {
            store.commitChanges();
        }
    }

    public ListStore<ModelData> getStore() {
        return store;
    }

    public void setStore(ListStore<ModelData> store) {
        this.store = store;
    }

    public int getRowIndex() {
        return super.rowIndex;
    }
    
}
