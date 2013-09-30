package org.openremote.controller.utils;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.dbunit.util.Base64;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;

public class SecurityUtil {
   
   public static WebRequest getSecuredRequest(WebConversation wc, String requestURL) {
      WebRequest pollingGetMethodRequest = new GetMethodWebRequest(requestURL);
      String usernameAndPassword = "dan:dan";
      String encodedUsernameAndPassword = Base64.encodeString(usernameAndPassword);
      pollingGetMethodRequest.setHeaderField("Authorization", HttpServletRequest.BASIC_AUTH + " " + encodedUsernameAndPassword);
      return pollingGetMethodRequest;
   }

   public static HttpUriRequest getSecuredHttpRequest(String requestURL) {
      HttpUriRequest httpRequest = new HttpPost(requestURL);
      String usernameAndPassword = "dan:dan";
      String encodedUsernameAndPassword = Base64.encodeString(usernameAndPassword);
      httpRequest.setHeader("Authorization", HttpServletRequest.BASIC_AUTH + " " + encodedUsernameAndPassword);
      return httpRequest;
   }
}
