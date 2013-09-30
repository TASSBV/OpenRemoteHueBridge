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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.openremote.beehive.utils.StringUtil;

/**
 * This is the second level hierarchy, for example what is shown in http://lirc.sourceforge.net/remotes/sony.
 * 
 * @author Dan 2009-2-6
 */
@Entity
@SuppressWarnings("serial")
@Table(name = "model")
public class Model extends BusinessEntity {

   private String name;
   private Vendor vendor;
   private String comment;
   private String fileName;
   private List<RemoteSection> remoteSections;

   public Model() {
      name = "";
      comment = "";
      fileName = "";
      remoteSections = new ArrayList<RemoteSection>();
   }

   @Column(nullable = false)
   public String getName() {
      parseModelNameInFileName();
      if (StringUtils.isBlank(name) && StringUtils.isNotBlank(comment)) {
         name = StringUtil.parseModelNameInComment(comment);
      }
      if (name.equals("?") || StringUtils.isBlank(name)) {
         if (fileName.indexOf(".") == -1) {
            name = fileName;
         } else if (getRemoteSections().size() > 0) {
            name = getRemoteSections().get(0).getRemoteOptions().get(0).getValue();
         }
      }
      return name;
   }

   private void parseModelNameInFileName() {
      if (StringUtils.isBlank(name) && fileName.matches("[^lircd\\.conf\\.].*?\\.irman")) {
         name = fileName.substring(0, fileName.indexOf(".irman"));
      }
      if (StringUtils.isBlank(name) && fileName.matches("lircd\\.conf\\..*")) {
         name = fileName.replace("lircd.conf.", "");
      }
      if (StringUtils.isBlank(name) && fileName.endsWith(".conf")) {
         name = fileName.replace(".conf", "");
      }
      if (StringUtils.isBlank(name) && fileName.endsWith(".tira")) {
         name = fileName.replace(".tira", "");
      }
      if (StringUtils.isBlank(name) && fileName.endsWith(".raw")) {
         name = fileName.replace(".raw", "");
      }
   }

   public String allSectionText() {
      String text = "";
      for (RemoteSection remoteSection : getRemoteSections()) {
         text += remoteSection.allText();
      }
      return text;
   }

   public void setName(String name) {
      this.name = name;
   }

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(nullable = false)
   public Vendor getVendor() {
      return vendor;
   }

   public void setVendor(Vendor vendor) {
      this.vendor = vendor;
   }

   @OneToMany(mappedBy = "model", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
   public List<RemoteSection> getRemoteSections() {
      return remoteSections;
   }

   public void setRemoteSections(List<RemoteSection> remoteSections) {
      this.remoteSections = remoteSections;
   }

   @Lob
   public String getComment() {
      return comment;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   @Column(name = "file_name")
   public String getFileName() {
      return fileName;
   }

   public void setFileName(String fileName) {
      this.fileName = fileName;
   }

   /**
    * Gets a relative file path, e.g. 3m\MP8640 in WINDOWS , 3m/MP8640 in Linux
    * 
    * @return a relative file path
    */
   public String filePath() {
      return getVendor().getName() + File.separator + getFileName();
   }

}
