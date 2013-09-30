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
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;

/**
 * @author marcus@openremote.org
 */
public class AutoCommitCheckColumnConfig extends CheckColumnConfig {


    public AutoCommitCheckColumnConfig() {
        super();
    }

    public AutoCommitCheckColumnConfig(String id, String name, int width) {
        super(id, name, width);
    }

    @Override
    protected void onMouseDown(GridEvent<ModelData> ge) {
        super.onMouseDown(ge);
        grid.getStore().commitChanges();
    }

}
