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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.openremote.beehive.utils.StringUtil;

/**
 * <p>
 * Infrared Options (prefixed name-value pairs)
 * </p>
 * <p>
 * There are various option fields in the configuration files. These are interpreted by the specific IR transmitter LIRC
 * device drivers to generate the proper bit sequences for the low level device API.
 * </p>
 * <p>
 * Some options are obvious and used pretty much by all LIRC config files:
 * </p>
 * <ul>
 * <li>name</li>
 * <li>flags</li>
 * <li>header</li>
 * <li>one</li>
 * <li>zero</li>
 * <li>bits</li>
 * <li>eps</li>
 * <li>aeps</li>
 * <li>gap</li>
 * </ul>
 * 
 * @author Dan 2009-2-6
 * 
 */
@Entity
@Table(name = "remote_option")
@SuppressWarnings("serial")
public class RemoteOption extends BusinessEntity {

   public final static String AEPS = "aeps";

   public final static String TOGGLE_BIT = "toggle_bit";

   private String name;

   private String value;

   private String comment;

   private RemoteSection remoteSection;

   public static HashMap<String, Integer> options = new HashMap<String, Integer>();

   public RemoteOption() {
      value = "";
      comment = "";
   }

   @Column(nullable = false)
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Column(nullable = false)
   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   @Lob
   public String getComment() {
      return comment;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(nullable = false, name = "remote_section_oid")
   public RemoteSection getRemoteSection() {
      return remoteSection;
   }

   public void setRemoteSection(RemoteSection remoteSection) {
      this.remoteSection = remoteSection;
   }

   public static void print() {
      for (String key : options.keySet()) {
         System.out.println(key + "=" + options.get(key));
      }
      System.out.println("sum=" + options.size());
   }

   public static void reset() {
      options.clear();
   }

   public static List<String> orderedOptionNames() {
      List<OptionElement> optionElements = new ArrayList<OptionElement>();
      for (String name : options.keySet()) {
         optionElements.add(new RemoteOption().new OptionElement(name, options.get(name)));
      }
      Collections.sort(optionElements, new Comparator<OptionElement>() {

         public int compare(OptionElement o1, OptionElement o2) {
            if (o1.getFrequency() > o2.getFrequency()) {
               return -1;
            } else if (o1.getFrequency() < o2.getFrequency()) {
               return 1;
            }
            return 0;
         }

      });
      List<String> names = new ArrayList<String>();
      for (OptionElement optionElement : optionElements) {
         names.add(optionElement.getName());
      }
      return names;
   }

   public class OptionElement {

      public OptionElement(String name, int frequency) {
         this.frequency = frequency;
         this.name = name;
      }

      private String name;

      private int frequency;

      public String getName() {
         return name;
      }

      public void setName(String name) {
         this.name = name;
      }

      public int getFrequency() {
         return frequency;
      }

      public void setFrequency(int frequency) {
         this.frequency = frequency;
      }

   }

   public String textLine() {
      String text = comment;
      text += "  " + name + StringUtil.remainedTabSpace(name) + value + StringUtil.lineSeparator();
      // if (isBorderline()) {
      // text += StringUtil.lineSeparator();
      // }
      return text;
   }

}
