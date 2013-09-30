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
package org.openremote.modeler.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.openremote.modeler.domain.KnxGroupAddress;

/**
 * The class takes a ets4 export file as inputstream and creates a list of
 * KnxGroupAddress objects
 * 
 * @author marcus@openremote.org
 */
public class KnxImporter
{

  private static final Logger LOGGER = Logger.getLogger(KnxImporter.class);

  @SuppressWarnings("unchecked")
  public List<KnxGroupAddress> importETS4Configuration(InputStream inputStream) throws Exception
  {

    List<KnxGroupAddress> result = new ArrayList<KnxGroupAddress>();
    String xmlData = null;
    DecimalFormat df = new DecimalFormat("000");
    SAXBuilder builder = new SAXBuilder();
    Document document = null;

    ZipInputStream zin = new ZipInputStream(inputStream);
    ZipEntry zipEntry = zin.getNextEntry();
    while (zipEntry != null)
    {
      if (zipEntry.getName().endsWith("/0.xml"))
      {
        xmlData = convertStreamToString(zin);
        break;
      }
      zipEntry = zin.getNextEntry();
    }

    if (xmlData != null)
    {
      //Remove UTF-8 Byte-order mark from the beginning of the data
      xmlData = xmlData.trim().replaceFirst("^([\\W]+)<","<");
      
      // parse the XML as a W3C Document
      StringReader in = new StringReader(xmlData);
      document = builder.build(in);

      // Query all GroupAddress elements
      XPath xpath = XPath.newInstance("//knx:GroupAddress");
      xpath.addNamespace("knx", document.getRootElement().getNamespaceURI());
      List<Element> xresult = xpath.selectNodes(document);
      for (Element element : xresult)
      {

        String id = element.getAttributeValue("Id");
        String name = element.getAttributeValue("Name");
        String address = element.getAttributeValue("Address");
        String dpt = null;
        // Query referenced ComObjectInstanceRef element which holds DPT
        xpath = XPath.newInstance("//knx:Send[@GroupAddressRefId='" + id + "']/../..");
        xpath.addNamespace("knx", "http://knx.org/xml/project/10");
        List<Element> result2 = xpath.selectNodes(document);
        if (result2.size() > 0)
        {
          dpt = result2.get(0).getAttributeValue("DatapointType");
          if (dpt != null && StringUtils.isNotEmpty(dpt))
          {
            StringTokenizer st = new StringTokenizer(dpt, "-");
            st.nextElement();
            try
            {
              dpt = st.nextToken() + "." + df.format(Integer.parseInt(st.nextToken()));
            } catch (Exception e)
            {
              dpt = null;
            }
          } else
          {
            dpt = null;
          }
        }
        String levelAddress = getAddressFromInt(Integer.parseInt(address));
        result.add(new KnxGroupAddress(dpt, levelAddress, name));
        LOGGER.debug("Created GroupAddress: " + levelAddress + " - " + name + " - " + dpt);
      }
    }

    return result;
  }

  public List<KnxGroupAddress> importETS3GroupAddressCsvExport(InputStream inputStream) throws Exception
  {
    List<KnxGroupAddress> result = new ArrayList<KnxGroupAddress>();

    String str;
    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "ISO-8859-1"));
    while ((str = in.readLine()) != null)
    {
      StringTokenizer st = new StringTokenizer(str, ";");
      String name = st.nextToken();
      String ga = st.nextToken();
      if (ga.indexOf("-") == -1)
      {
        result.add(new KnxGroupAddress(null, ga, name));
      }
    }

    return result;
  }

  public static String convertStreamToString(InputStream is) throws IOException
  {
    if (is != null)
    {
      Writer writer = new StringWriter();

      char[] buffer = new char[1024];
      try
      {
        Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        int n;
        while ((n = reader.read(buffer)) != -1)
        {
          writer.write(buffer, 0, n);
        }
      } finally
      {
        is.close();
      }
      return writer.toString();
    } else
    {
      return "";
    }
  }

  public static String getAddressFromInt(int knxaddress)
  {
    int maingroup, subgroup, group;
    // extract values
    maingroup = (knxaddress >> 11) & 0x0f;
    subgroup = (knxaddress >> 8) & 0x07;
    group = knxaddress & 0xff;
    String erg = "" + maingroup + "/" + subgroup + "/" + group;
    return erg;
  }

}
