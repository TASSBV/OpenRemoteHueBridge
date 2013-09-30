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
package org.openremote.beehive.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.openremote.beehive.Configuration;
import org.openremote.beehive.exception.LIRCrawlerException;
import org.openremote.beehive.file.LIRCElement;
import org.openremote.beehive.spring.SpringContext;

/**
 * The Class LIRCrawler.
 * 
 * @author Tomsky
 */
public class LIRCrawler {
   
   /** The configuration. */
   public static Configuration configuration = (Configuration) SpringContext.getInstance().getBean("configuration");

   /** The http client. */
   private static HttpClient httpClient = createHttpClient();

   /** The Constant LOGGER. */
   private static final Logger LOGGER = Logger.getLogger(LIRCrawler.class.getName());

   /**
    * Creates the http client.
    * 
    * @return the http client
    */
   private static HttpClient createHttpClient() {
      HttpClient httpClient = new HttpClient();
      httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(30000);
      return httpClient;
   }

   /**
    * List.
    * 
    * @param lircUrl
    *           the lirc url
    * 
    * @return the list< lirc element>
    */
   public static List<LIRCElement> list(String lircUrl) {
      List<LIRCElement> lircs = new ArrayList<LIRCElement>();
      Pattern pattern = Pattern.compile(configuration.getLircCrawRegex());
      Matcher matcher = pattern.matcher(getPageContent(lircUrl));
      while (matcher.find()) {
         LIRCElement lirc = new LIRCElement();
         String path = StringEscapeUtils.unescapeHtml(matcher.group(2));
         if (!FileUtil.isImage(path)) {
            if (matcher.group(1).equals("text") || matcher.group(1).equals("script")) {
               lirc.setModel(true);
            }
            lirc.setPath(StringUtil.appendFileSeparator(lircUrl) + path);
            lirc.setUploadDate(matcher.group(3));
            lircs.add(lirc);
         }
      }
      return lircs;
   }

   /**
    * Write model.
    * 
    * @param lirc
    *           the lirc
    */
   public static void writeModel(LIRCElement lirc) {
      String modelContent = getPageContent(lirc.getPath());
      if (!modelContent.equals("")) {
         FileUtil.writeStringToFile(modelContent, StringUtil.appendFileSeparator(FileUtil.configuration
               .getWorkCopyDir())
               + lirc.getRelativePath());
      }
   }

   /**
    * Gets the page content. When the network is bad, retry 100 times.
    * 
    * @param url
    *           the url
    * 
    * @return the page content
    */
   private static String getPageContent(String url) {
      String content = null;
      int retryCount = -1;
      while (content == null) {
         content = getHtmlBody(url);
         retryCount++;
         if (retryCount > 10) {
            content = "";
            LIRCrawlerException ee = new LIRCrawlerException("Occur the network exception, maybe the url [" + url
                  + "] is unreachable.");
            ee.setErrorCode(LIRCrawlerException.CRAWLER_NETWORK_ERROR);
            throw ee;
         } else if (retryCount != 0) {
            LOGGER.error("try " + url + " " + retryCount + " times.");
            try {
               Thread.sleep(1000 * 5);
            } catch (InterruptedException e) {
               LOGGER.error("Thread sleep 5s occur error!", e);
            }
         }
      }
      return content;
   }

   /**
    * Gets the html body.
    * 
    * @param url
    *           the url
    * 
    * @return the html body
    */
   private static String getHtmlBody(String url) {
      String responseBody = "";
      GetMethod getMethod = new GetMethod(url);
      getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 30000);
      getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
      try {
         int statusCode = httpClient.executeMethod(getMethod);
         if (statusCode != HttpStatus.SC_OK) {
            LOGGER.error("Method failed: " + getMethod.getStatusLine());
         }
         BufferedReader bufferIn = new BufferedReader(new InputStreamReader(getMethod.getResponseBodyAsStream()));
         StringBuffer sb = new StringBuffer();
         char[] buf = new char[1024 * 1024];
         int len;
         while ((len = bufferIn.read(buf)) > 0) {
            sb.append(buf, 0, len);
         }
         responseBody = sb.toString();
      } catch (HttpException e) {
         LOGGER.error("Please check your provided http address " + url, e);
         return null;
      } catch (IOException e) {
         LOGGER.error("Occur the network exception, maybe the url [" + url + "] is unreachable.", e);
         return null;
      } finally {
         getMethod.releaseConnection();
      }
      return responseBody;
   }
   
}
