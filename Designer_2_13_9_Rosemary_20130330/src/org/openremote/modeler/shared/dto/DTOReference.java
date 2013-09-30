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

import java.io.Serializable;

/**
 * Used so that one DTO can reference another one (e.g. sensor referencing its command).
 * Required so that reference can both exist for objects already persisted (that have an id)
 * and newly created instances (that don't have an id).
 * 
 * @author Eric Bariaux (eric@openremote.org)
 */
public class DTOReference implements Serializable {

  private static final long serialVersionUID = 1L;
  
  private DTO dto;
  private Long id;

  public DTOReference() {
    super();
  }

  public DTOReference(DTO dto) {
    super();
    this.dto = dto;
  }

  public DTOReference(Long id) {
    super();
    this.id = id;
  }

  public DTOReference(DTO dto, Long id) {
    super();
    this.dto = dto;
    this.id = id;
  }

  public DTO getDto() {
    return dto;
  }

  public void setDto(DTO dto) {
    this.dto = dto;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
  
}
