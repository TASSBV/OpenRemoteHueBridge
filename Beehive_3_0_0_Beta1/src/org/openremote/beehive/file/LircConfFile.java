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
package org.openremote.beehive.file;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openremote.beehive.domain.Code;
import org.openremote.beehive.domain.Model;
import org.openremote.beehive.domain.RemoteOption;
import org.openremote.beehive.domain.RemoteSection;
import org.openremote.beehive.utils.FileUtil;
import org.openremote.beehive.utils.StringUtil;

/**
 * Allows to parse the content in LIRC configuration file.
 * 
 * @author Dan 2009-2-6
 * 
 */
public class LircConfFile {

   private File file;

   public LircConfFile(File file) {
      this.file = file;
   }

   public String getAbsolutePath() {
      return file.getAbsolutePath();
   }

   public void discoverOptions() {
      boolean beginRemote = false;
      boolean beginCodes = false;
      List<String> list = FileUtil.getContentList(getAbsolutePath());
      for (String str : list) {
         if (isComment(str) || StringUtils.isBlank(str)) {
            continue;
         } else {
            if (str.indexOf(RemoteSection.BEGIN_REMOTE) != -1) {
               beginRemote = true;
               continue;
            } else if (str.indexOf(RemoteSection.BEGIN_CODES) != -1 || str.indexOf(RemoteSection.BEGIN_RAW_CODES) != -1) {
               beginCodes = true;
               continue;
            } else if (str.indexOf(RemoteSection.END_REMOTE) != -1) {
               beginRemote = false;
               continue;
            } else if (str.indexOf(RemoteSection.END_CODES) != -1 || str.indexOf(RemoteSection.END_RAW_CODES) != -1) {
               beginCodes = false;
               continue;
            }
         }
         if (beginCodes || !beginRemote) {
            continue;
         }
         if (beginRemote && !beginCodes) {
            String[] arr = str.trim().split("\\s+");
            int orginal_num = RemoteOption.options.get(arr[0]) == null ? 0 : RemoteOption.options.get(arr[0]);
            RemoteOption.options.put(arr[0], orginal_num + 1);
         }
      }
   }

   public static List<RemoteSection> getRemoteSectionList(FileInputStream fis) {

      boolean beginRemote = false;
      boolean beginCodes = false;
      boolean isRaw = false;
      Code rawCode = null;
      String comment = "";
      String sectionComment = "";
      String wholeComment = "";
      List<RemoteSection> remoteSectionList = new ArrayList<RemoteSection>();
      List<String> list = FileUtil.getContentList(fis);
      RemoteSection section = null;
      for (String str : list) {
         if (StringUtils.isBlank(str)) {
            comment += StringUtil.lineSeparator();
            sectionComment += StringUtil.lineSeparator();
         } else if (isComment(str)) {
            String line = str + StringUtil.lineSeparator();
            comment += line;
            sectionComment += line;
            wholeComment += line;
         } else if (isTag(str)) {
            if (str.indexOf(RemoteSection.BEGIN_REMOTE) != -1) {
               beginRemote = true;
               section = new RemoteSection();
               section.setComment(sectionComment);
               sectionComment = "";
               comment = "";
            } else if (str.indexOf(RemoteSection.BEGIN_CODES) != -1) {
               beginCodes = true;
               isRaw = false;
               comment = "";
            } else if (str.indexOf(RemoteSection.BEGIN_RAW_CODES) != -1) {
               beginCodes = true;
               isRaw = true;
               comment = "";
            } else if (str.indexOf(RemoteSection.END_REMOTE) != -1) {
               section.setRaw(isRaw);
               remoteSectionList.add(section);
               beginRemote = false;
               comment = "";
               sectionComment = "";
            } else if (str.indexOf(RemoteSection.END_CODES) != -1 || str.indexOf(RemoteSection.END_RAW_CODES) != -1) {
               beginCodes = false;
               comment = "";
            }
         } else {
            if (beginCodes && isRaw == false) {
               Code code = new Code();
               String[] arr = str.trim().split("\\s+");
               code.setName(arr[0]);
               code.setValue(arr[1]);
               code.setComment(comment);
               comment = "";
               code.setRemoteSection(section);
               section.getCodes().add(code);
            }
            if (beginCodes && isRaw == true) {
               if (isRawOptionName(str)) {
                  rawCode = new Code();
                  rawCode.setRemoteSection(section);
                  rawCode.setName(str.trim().split("\\s+")[1]);
                  rawCode.setComment(comment);
                  comment = "";
                  section.getCodes().add(rawCode);
               } else {
                  rawCode.addValueLine(str);
               }
            }
            if (beginRemote && !beginCodes) {
               RemoteOption option = new RemoteOption();
               String[] arr = str.trim().split("\\s+");
               option.setName(arr[0]);
               option.setValue(arr.length > 2 ? arr[1] + " " + arr[2] : arr[1]);
               option.setComment(comment);
               comment = "";
               if ("name".equals(arr[0])) {
                  section.setName(arr[1]);
               }
               option.setRemoteSection(section);
               section.getRemoteOptions().add(option);
            }
         }
      }
      if (remoteSectionList.size() > 0) {
         Model model = new Model();
         model.setComment(wholeComment);
         remoteSectionList.get(0).setModel(model);
      }
      return remoteSectionList;

   }

   private static boolean isTag(String str) {
      return str.trim().matches("begin remote|begin codes|end codes|end remote|begin raw_codes|end raw_codes");
   }

   private static boolean isComment(String str) {
      return str.trim().startsWith("#");
   }

   private static boolean isRawOptionName(String str) {
      return str.trim().startsWith("name");
   }

}
