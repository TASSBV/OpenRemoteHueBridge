/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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
package org.openremote.beehive.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.openremote.beehive.api.dto.TemplateDTO;

/**
 * Defines UI templating functionality in the UI designer.
 * one screen design can be set as a template and base other screens on this template design.
 * e.g. only create Apple Remote screen once and store and reuse it for all panel designs.
 * 
 * @author Dan 2010-1-29
 */
@Entity
@SuppressWarnings("serial")
@Table(name = "template")
public class Template extends BusinessEntity {
   public static final long PUBLIC_ACCOUNT_OID = 0L;
   
   private String name;
   private String content;
   private Account account;
   private String keywords = "";
   private boolean shared = false;

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }
   
//   @Column(nullable = false, columnDefinition = Constant.TEXT_COLUMN_DEFINITION)
   @Column(nullable = false )
   @Lob
   public String getContent() {
      return content;
   }

   public void setContent(String content) {
      this.content = content;
   }

   @ManyToOne
   public Account getAccount() {
      return account;
   }

   public void setAccount(Account account) {
      this.account = account;
   }

   public String getKeywords() {
      return keywords;
   }

   public void setKeywords(String keywords) {
      this.keywords = keywords;
   }

   public boolean isShared() {
      return shared;
   }

   public void setShared(boolean shared) {
      this.shared = shared;
   }
   
   public TemplateDTO toDTO() {
      TemplateDTO dto = new TemplateDTO();
      dto.setContent(content);
      dto.setKeywords(keywords);
      dto.setName(name);
      dto.setOid(getOid());
      dto.setShared(shared);
      return dto;
   }
}
