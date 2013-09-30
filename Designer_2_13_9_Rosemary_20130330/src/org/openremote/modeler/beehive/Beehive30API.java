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
package org.openremote.modeler.beehive;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.HttpURLConnection;
import java.text.DecimalFormat;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.commons.codec.binary.Base64;
import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.client.Constants;
import org.openremote.modeler.domain.User;
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.exception.ConfigurationException;
import org.openremote.modeler.exception.NetworkException;
import org.openremote.modeler.logging.LogFacade;
import org.openremote.modeler.cache.CacheWriteStream;
import org.openremote.modeler.cache.ResourceCache;
import org.openremote.modeler.cache.CacheOperationException;

/**
 * Implements {@link BeehiveService} for Beehive 3.0 REST API. <p>
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class Beehive30API implements BeehiveService
{


  // Class Members --------------------------------------------------------------------------------

  /**
   * Logger for this Beehive client API service.
   */
  private final static LogFacade serviceLog =
      LogFacade.getInstance(LogFacade.Category.BEEHIVE);

  /**
   * Specialized logger for storing account archive download performance stats in their
   * own sub-category.
   */
  private final static LogFacade downloadPerfLog =
      LogFacade.getInstance(LogFacade.Category.BEEHIVE_DOWNLOAD_PERFORMANCE);




  // Instance Fields ------------------------------------------------------------------------------

  /**
   * Designer configuration.
   */
  private Configuration config;



  // Constructors ---------------------------------------------------------------------------------

  /**
   * Initializes a Beehive client API.
   *
   * @param config    Designer configuration.
   */
  public Beehive30API(Configuration config)
  {
    this.config = config;
  }



  // Implements BeehiveService --------------------------------------------------------------------

  /**
   * Downloads a user artifact archive from Beehive server and stores it to a local resource
   * cache. 
   *
   * @param currentUser   the user associated with the request
   * @param cache         the cache to store the user resources to
   *
   * @throws ConfigurationException
   *              If designer configuration error prevents the service from executing
   *              normally. Often a fatal error type that should be logged/notified to
   *              admins, configuration corrected and application re-deployed.
   *
   * @throws NetworkException
   *              If (possibly recoverable) network errors occured during the service
   *              operation. Network errors may be recoverable in which case this operation
   *              could be re-attempted. See {@link NetworkException.Severity} for an
   *              indication of the network error type.
   *
   * @throws CacheOperationException
   *              If there was a failure in writing the downloaded resources to cache.
   *
   *
   */
  @Override public void downloadResources(User currentUser, ResourceCache cache)
      throws ConfigurationException, NetworkException, CacheOperationException
  {

    // TODO :
    //    - Must use HTTPS


    // Construct the request...

    HttpClient httpClient = new DefaultHttpClient();

    URI beehiveArchiveURI;

    try
    {
      beehiveArchiveURI = new URI(
          config.getBeehiveRESTRootUrl() + "user/" +
          currentUser.getUsername() + "/openremote.zip"
      );
    }

    catch (URISyntaxException e)
    {
      throw new ConfigurationException(
          "Incorrect Beehive REST URL defined in config.properties : {0}", e, e.getMessage()
      );
    }

    HttpGet httpGet = new HttpGet(beehiveArchiveURI);


    // Authenticate...

    addHTTPAuthenticationHeader(httpGet, currentUser.getUsername(), currentUser.getPassword());


    // Collect some network statistics...

    long starttime = System.currentTimeMillis();


    // HTTP GET to Beehive...

    HttpResponse response;

    try
    {
      response = httpClient.execute(httpGet);
    }

    catch (IOException e)
    {
      throw new NetworkException(
          "Network error while downloading account (OID = {0}) archive from Beehive " +
          "(URL : {1}) : {2}", e,

          currentUser.getAccount().getOid(), beehiveArchiveURI, e.getMessage()
      );
    }


    // Make sure we got a response and a proper HTTP return code...

    if (response == null)
    {
      throw new NetworkException(
          NetworkException.Severity.SEVERE,
          "Beehive did not respond to HTTP GET request, URL : {0} {1}",
          beehiveArchiveURI, printUser(currentUser)
      );
    }

    StatusLine statusLine = response.getStatusLine();

    if (statusLine == null)
    {
      throw new NetworkException(
          NetworkException.Severity.SEVERE,
          "There was no status from Beehive to HTTP GET request, URL : {0} {1}",
          beehiveArchiveURI, printUser(currentUser)
      );
    }

    int httpResponseCode = statusLine.getStatusCode();


    // Deal with the HTTP OK (200) case.

    if (httpResponseCode == HttpURLConnection.HTTP_OK)
    {
      HttpEntity httpEntity = response.getEntity();

      if (httpEntity == null)
      {
        throw new NetworkException(
            NetworkException.Severity.SEVERE,
            "No content received from Beehive to HTTP GET request, URL : {0} {1}",
            beehiveArchiveURI, printUser(currentUser)
        );
      }


      // Download to cache...

      BufferedInputStream httpInput;

      try
      {
        CacheWriteStream cacheStream = cache.openWriteStream();

        httpInput = new BufferedInputStream(httpEntity.getContent());

        byte[] buffer = new byte[4096];
        int bytecount = 0, len;
        long contentLength = httpEntity.getContentLength();

        try
        {
          while ((len = httpInput.read(buffer)) != -1)
          {
            try
            {
              cacheStream.write(buffer, 0, len);
            }

            catch (IOException e)
            {
              throw new CacheOperationException(
                  "Writing archive to cache failed : {0}", e, e.getMessage()
              );
            }

            bytecount += len;
          }

          // MUST mark complete for cache to accept the incoming archive...

          cacheStream.markCompleted();
        }

        finally
        {
          try
          {
            cacheStream.close();
          }

          catch (Throwable t)
          {
            serviceLog.warn(
                "Unable to close resource archive cache stream : {0}",
                t, t.getMessage()
            );
          }

          if (httpInput != null)
          {
            try
            {
              httpInput.close();
            }

            catch (Throwable t)
            {
              serviceLog.warn(
                  "Unable to close HTTP input stream from Beehive URL ''{0}'' : {1}",
                  t, beehiveArchiveURI, t.getMessage()
              );
            }
          }
        }

        if (contentLength >= 0)
        {
          if (bytecount != contentLength)
          {
            serviceLog.warn(
                "Expected content length was {0} bytes but wrote {1} bytes to cache stream ''{2}''.",
                contentLength, bytecount, cacheStream
            );
          }
        }
        

        // Record network performance stats...

        long endtime = System.currentTimeMillis();

        float kbytes  = ((float)bytecount) / 1000;
        float seconds = ((float)(endtime - starttime)) / 1000;
        float kbpersec = kbytes / seconds;

        String kilobytes  = new DecimalFormat("###########0.00").format(kbytes);
        String nettime    = new DecimalFormat("##########0.000").format(seconds);
        String persectime = new DecimalFormat("##########0.000").format(kbpersec);

        downloadPerfLog.info(
            "Downloaded " + kilobytes + " kilobytes in " + nettime + " seconds (" +
            persectime + "kb/s)"
        );
      }

      catch (IOException e)
      {
        // HTTP request I/O error...

        throw new NetworkException(
            "Download of Beehive archive failed : {0}", e, e.getMessage()
        );
      }
    }


    // Assuming 404 indicates a new user... quietly return, nothing to download...

    // TODO : MODELER-286

    else if (httpResponseCode == HttpURLConnection.HTTP_NOT_FOUND)
    {
      serviceLog.info("No user data found. Return code 404. Assuming new user account...");

      return;
    }

    else
    {
      // TODO :
      //
      //   Currently assumes any other HTTP return code is a standard network error.
      //   This could be improved by handling more specific error codes (some are
      //   fatal, some are recoverable) such as 500 Internal Error (permanent) or
      //   307 Temporary Redirect
      //
      // TODO :
      //
      //   Should handle authentication errors in their own branch, not in this generic block

      throw new NetworkException(
          "Failed to download Beehive archive from URL ''{0}'' {1}, " +
          "HTTP Response code: {2}",

          beehiveArchiveURI, printUser(currentUser), httpResponseCode
      );
    }
  }


  /**
   * Uploads resources from the given input stream to Beehive server. The Beehive REST API
   * used assumes a zip compressed stream including all relevant resources. The input stream
   * parameter must match these expectations. <p>
   *
   * The access to Beehive is authenticated using the given user's credentials.
   *
   * @param archive       zip compressed byte stream containing all the resources to upload
   *                      to Beehive
   * @param currentUser   user to authenticate in Beehive
   *
   *
   * @throws ConfigurationException
   *            If the Beehive REST URL has been incorrectly configured. Will require
   *            reconfiguration and re-deployment of the application.
   *
   * @throws NetworkException
   *            If there's an I/O error on the upload stream or the Beehive server
   *            returns an error status
   *
   */
  @Override public void uploadResources(InputStream archive, User currentUser)
      throws ConfigurationException, NetworkException
  {
    final String ARCHIVE_NAME = "openremote.zip";

    // TODO : must be HTTPS

    Account acct = currentUser.getAccount();

    HttpClient httpClient = new DefaultHttpClient();
    HttpPost httpPost = new HttpPost();

    addHTTPAuthenticationHeader(httpPost, currentUser.getUsername(), currentUser.getPassword());

    String beehiveRootRestURL = config.getBeehiveRESTRootUrl();
    String url = beehiveRootRestURL + "account/" + acct.getOid() + "/" + ARCHIVE_NAME;

    try
    {
      httpPost.setURI(new URI(url));
    }

    catch (URISyntaxException e)
    {
      throw new ConfigurationException(
          "Incorrectly configured Beehive REST URL ''{0}'' : {1}",
          e, beehiveRootRestURL, e.getMessage()
      );
    }

    InputStreamBody resource = new InputStreamBody(archive, ARCHIVE_NAME);

    MultipartEntity entity = new MultipartEntity();
    entity.addPart("resource", resource);
    httpPost.setEntity(entity);

    HttpResponse response;

    try
    {
      response = httpClient.execute(httpPost);
    }

    catch (IOException e)
    {
      throw new NetworkException(
          "Network I/O error while uploading resource artifacts to Beehive : {0}",
          e, e.getMessage()
      );
    }

    if (response.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK)
    {
       throw new NetworkException(
           "Failed to save resources to Beehive, status code: {0}",
           response.getStatusLine().getStatusCode()
       );
    }

    // TODO :
    //   - should probably check other return codes explicitly here too, such as
    //     authentication errors (which is most likely not recoverable whereas
    //     a regular network connection glitch might well be)...
  }



  // Private Instance Methods ---------------------------------------------------------------------



  /**
   * Adds a HTTP 1.1 Authentication header to the given HTTP request. The header 
   * value (username and password) are base64 encoded as required by the HTTP
   * specification.
   *
   * @param request   the HTTP request to add the header to
   * @param username  username string
   * @param password  password string
   */
  private void addHTTPAuthenticationHeader(HttpRequest request, String username, String password)
  {
    request.setHeader(
        Constants.HTTP_BASIC_AUTH_HEADER_NAME,
        Constants.HTTP_BASIC_AUTH_HEADER_VALUE_PREFIX +
        base64EncodeAuthHeaderValue(username + ":" + password)
    );
  }


  /**
   * Base64 encode the value string for a HTTP authentication header, as
   * required by the HTTP specification. The name, password string must follow
   * the required formatting name:password.
   *
   * @param namePassword  name:password string per the HTTP spec
   *
   * @return  base64 encoded authentication header value
   */
  private static String base64EncodeAuthHeaderValue(String namePassword)
  {
    if (namePassword == null)
    {
      return null;
    }

    return new String(Base64.encodeBase64(namePassword.getBytes()));
  }


  /**
   * Utility to print some user account information for logging.
   *
   * @param user    current user
   *
   * @return    (user name - email, account ID)
   */
  private String printUser(User user)
  {
    return "(User: " + user.getUsername() + " - " + user.getEmail() +
           ", Account OID: " + user.getAccount().getOid() + ")";
  }




}

