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

import org.openremote.beehive.utils.StringUtil;

/**
 * A configuration section in a LIRC configuration file linked with a model. It is possible to have more than one remote
 * configuration in a configuration file. Each remote configuration in a file should go to the database as a separate
 * remote record. User can later combine multiple sections into a single configuration file which can support multiple
 * remote devices.
 * 
 * @author Dan 2009-2-6
 */
@Entity
@Table(name = "remote_section")
@SuppressWarnings("serial")
public class RemoteSection extends BusinessEntity {

   private String name;

   private boolean raw;

   private String comment;

   private Model model;

   private List<RemoteOption> remoteOptions;

   private List<Code> codes;

   public final static String BEGIN_REMOTE = "begin remote";
   public final static String BEGIN_CODES = "begin codes";
   public final static String END_CODES = "end codes";
   public final static String END_REMOTE = "end remote";
   public final static String BEGIN_RAW_CODES = "begin raw_codes";
   public final static String END_RAW_CODES = "end raw_codes";

   public RemoteSection() {
      remoteOptions = new ArrayList<RemoteOption>();
      codes = new ArrayList<Code>();
      comment = "";
   }

   public String beginRemoteTag() {
      return StringUtil.lineSeparator() + BEGIN_REMOTE + StringUtil.lineSeparator();
   }

   @Column(nullable = false)
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Column(nullable = false)
   public boolean isRaw() {
      return raw;
   }

   public void setRaw(boolean raw) {
      this.raw = raw;
   }

   @Lob
   public String getComment() {
      return comment;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(nullable = false)
   public Model getModel() {
      return model;
   }

   public void setModel(Model model) {
      this.model = model;
   }

   @OneToMany(mappedBy = "remoteSection", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   public List<RemoteOption> getRemoteOptions() {
      return remoteOptions;
   }

   public void setRemoteOptions(List<RemoteOption> remoteOptions) {
      this.remoteOptions = remoteOptions;
   }

   @OneToMany(mappedBy = "remoteSection", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   public List<Code> getCodes() {
      return codes;
   }

   public void setCodes(List<Code> codes) {
      this.codes = codes;
   }

   private String allOptionText() {
      String text = "";
      for (RemoteOption remoteOption : getRemoteOptions()) {
         text += remoteOption.textLine();
      }
      return text + StringUtil.lineSeparator();
   }

   /**
    * Gets all the text including tags, options and codes.
    * 
    * @return text string
    */
   public String allText() {
      String text = comment;
      text += beginRemoteTag();
      text += allOptionText();
      text += allCodeText();
      text += endRemoteTag();
      return text + StringUtil.lineSeparator();
   }

   private String allCodeText() {
      String text = beginCodeTag();
      for (Code code : getCodes()) {
         text += code.textLine();
      }
      text += StringUtil.lineSeparator();
      text += endCodeTag();
      return text + StringUtil.lineSeparator();
   }

   private String endCodeTag() {
      String tag = END_CODES;
      if (raw) {
         tag = END_RAW_CODES;
      }
      return "      " + tag + StringUtil.doubleLineSeparator();
   }

   private String beginCodeTag() {
      String tag = BEGIN_CODES;
      if (raw) {
         tag = BEGIN_RAW_CODES;
      }
      return "      " + tag + StringUtil.lineSeparator();
   }

   private String endRemoteTag() {
      return END_REMOTE + StringUtil.doubleLineSeparator();
   }

   /**
    * Gets a relative file path, e.g. sigma_designs\realmagic\REALMagic_SIR in WINDOWS ,
    * sigma_designs/realmagic/REALMagic_SIR in Linux
    * 
    * @return a relative file path
    */
   public String filePath() {
      return model.getVendor().getName() + File.separator + model.getName() + File.separator + name;
   }

}
