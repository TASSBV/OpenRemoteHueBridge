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
package org.openremote.modeler.shared.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;

public class DTOHelper {

  public static BeanModel getBeanModel(DTO dto) {
    BeanModelFactory beanModelFactory = BeanModelLookup.get().getFactory(dto.getClass());
    BeanModel bm = beanModelFactory.createModel(dto);
    return bm;
 }

 public static List<BeanModel> createModels(Collection<? extends DTO> list) {
    List<BeanModel> models = new ArrayList<BeanModel>();
    for (DTO b : list) {
       models.add(getBeanModel(b));
    }
    return models;
 }

}
