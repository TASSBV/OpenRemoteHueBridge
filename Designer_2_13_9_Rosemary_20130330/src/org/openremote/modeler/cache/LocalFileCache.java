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
package org.openremote.modeler.cache;

import java.io.File;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import java.text.DecimalFormat;
import java.text.MessageFormat;

import org.openremote.modeler.domain.User;
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.configuration.PathConfig;
import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.logging.LogFacade;
import org.openremote.modeler.logging.AdministratorAlert;
import org.openremote.modeler.exception.ConfigurationException;
import org.openremote.modeler.exception.NetworkException;
import org.openremote.modeler.beehive.BeehiveService;
import org.openremote.modeler.beehive.Beehive30API;
import org.openremote.modeler.beehive.BeehiveServiceException;
import org.openremote.modeler.utils.FileUtilsExt;

/**
 * Resource cache based on local file system access. This class provides an API for handling
 * and caching account's file resources.
 *
 * @see ResourceCache
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class LocalFileCache implements ResourceCache<File>
{

  // TODO Tasks:
  //
  //    - http://jira.openremote.org/browse/MODELER-284 -- log performance stats
  //    - http://jira.openremote.org/browse/MODELER-285 -- push resource data integrity and
  //                                                       durability aspects to Beehive
  //    - http://jira.openremote.org/browse/MODELER-286 -- Beehive API returns 404 on new users
  //    - http://jira.openremote.org/browse/MODELER-287 -- internalize ResourceService.initResources
  //                                                       to this implementation
  //    

  // Constants ------------------------------------------------------------------------------------

  /**
   * The archive file name used in the cache directory to store the downloaded state from Beehive.
   */
  private final static String BEEHIVE_ARCHIVE_NAME = "openremote.zip";

  /**
   * File name prefix used for daily backups of cached Beehive archive.
   */
  private final static String DAILY_BACKUP_PREFIX = BEEHIVE_ARCHIVE_NAME + ".daily";

  /**
   * Convenience constant to indicate a hour granularity on system time (which is in milliseconds)
   */
  private final static int HOUR = 1000 * 60 * 60;

  /**
   * Convenience constant to indicate day granularity on system time (which is in milliseconds)
   */
  private final static int DAY  = 24 * HOUR;



  // Class Members --------------------------------------------------------------------------------

  /**
   * Log category for this cache implementation.
   */
  private final static LogFacade cacheLog =
      LogFacade.getInstance(LogFacade.Category.CACHE);

  /**
   * Admin alert notifications for critical errors in this implementation.
   */
  private final static AdministratorAlert admin =
      AdministratorAlert.getInstance(AdministratorAlert.Type.RESOURCE_CACHE);

  /**
   * Class-wide safety valve on backup file generation. If any errors are detected, halt backups
   * based on the concern that a potential corrupt data might propagate itself into backup cycles. <p>
   *
   * This set contains account IDs that have been flagged and should be prevented from creating
   * more backup copies.  <p>
   *
   * Multiple thread-access is synchronized via copy-on-write implementation. Assumption is that
   * writes are rare (only occur in case of systematic errors) and mostly access is read-only to
   * check existence of account IDs.
   */
  private final static Set<Long> haltAccountBackups = new CopyOnWriteArraySet<Long>();



  // Instance Fields ------------------------------------------------------------------------------

  /**
   * Designer configuration.
   */
  private Configuration configuration;

  /**
   * The account with which this cache is associated with.
   */
  private Account account;

  /**
   * The current user associated with the calling thread that is accessing the account which
   * this cache belongs to.
   */
  private User currentUser;

  /**
   * The path to an account's cache folder in the local filesystem.
   */
  private File cacheFolder;



  // Constructors ---------------------------------------------------------------------------------


  /**
   * Constructs a new instance to manage operations on the given user account's local file cache.
   *
   * @param config    Designer configuration
   * @param user      The current user whose associated account and it's cache in local file
   *                  system will be manipulated.
   */
  public LocalFileCache(Configuration config, User user)
  {
    this.configuration = config;

    this.account = user.getAccount();

    this.currentUser = user;

    this.cacheFolder = new File(PathConfig.getInstance(config).userFolder(account));
  }



  // Implements ResourceCache ---------------------------------------------------------------------

  /**
   * Opens a stream for writing a zip compressed archive with user resources to this cache.
   * Note the API requirements on {@link CacheWriteStream} use : the stream must be marked
   * as complete by the calling client before this implementation accepts the resources.
   *
   * @see CacheWriteStream
   *
   * @return  An open stream that can be used to store a zip compressed resource archive
   *          to this cache. The returned stream object includes an API that differs from
   *          standard Java I/O streaming interfaces with a
   *          {@link org.openremote.modeler.cache.CacheWriteStream#markCompleted()} which
   *          the caller of this method must use in order for this cache implementation to
   *          consider the stream as completed and its contents usable for storing in cache.
   *
   * @throws CacheOperationException
   *            If an error occurs in creating or opening the required files in the local file
   *            system to store the incoming resource archive stream contents.
   *
   * @throws ConfigurationException
   *            If security constraints prevent access to required files in the local filesystem
   */
  @Override public CacheWriteStream openWriteStream()
      throws CacheOperationException, ConfigurationException
  {
    File tempDownloadTarget = new File(
        getCachedArchive().getPath() + "." + UUID.randomUUID().toString() + ".download"
    );

    cacheLog.debug("Downloading to ''{0}''", tempDownloadTarget.getAbsolutePath());

    try
    {
      return new FileCacheWriteStream(tempDownloadTarget);
    }

    catch (FileNotFoundException e)
    {
      throw new CacheOperationException(
          "Cannot open or create file ''{0}'' : {1}", e, tempDownloadTarget, e.getMessage()
      );
    }

    catch (SecurityException e)
    {
      throw new ConfigurationException(
          "Write access to ''{0}'' has been denied : {1}", e, tempDownloadTarget, e.getMessage()
      );
    }
  }


  /**
   * Creates a zip compressed file on the local file system (in this account's cache directory)
   * containing all user resources and returns a readable input stream from it. <p>
   *
   * This input stream can be used where the designer resources are expected as a zipped
   * archive bundle (Beehive API calls, configuration export functions, etc.)
   *
   * @return  an input stream from a zip archive in the local filesystem cache containing
   *          all account artifacts
   *
   * @throws CacheOperationException
   *            if any of the local file system operations fail
   *
   * @throws ConfigurationException
   *            if there are security restrictions on any of the file access
   */
  @Override public InputStream openReadStream() throws CacheOperationException, ConfigurationException
  {
    File exportArchiveFile = createExportArchive();

    try
    {
      return new BufferedInputStream(new FileInputStream(exportArchiveFile));
    }

    catch (Throwable t)
    {
      throw new CacheOperationException(
          "Failed to create input stream to export archive ''{0}'' : {1}",
          t, exportArchiveFile, t.getMessage()
      );
    }

    // TODO : MODELER-284 -- log execution performance
  }


  /**
   * Synchronizes the local cached Beehive archive with the Beehive server. If there are
   * existing previous cached copies of the Beehive archive on the local system, those are backed
   * up first. After the Beehive archive has been downloaded, it is extracted in the given
   * account's cache folder.
   *
   * @throws NetworkException
   *            If any errors occur with the network connection to Beehive server -- the basic
   *            assumption here is that network exceptions are recoverable (within a certain
   *            time period) and the method call can optionally be re-attempted at later time.
   *            Do note that the exception class provides a severity level which can be used
   *            to indicate the likelyhood that the network error can be recovered from.
   *
   * @throws ConfigurationException
   *            If any of the cache operations cannot be performed due to security restrictions
   *            on the local file system.
   *
   * @throws CacheOperationException
   *            If any runtime I/O errors occur during the sync.
   */
  @Override public void update() throws NetworkException, ConfigurationException, CacheOperationException
  {

    // Backup existing cached archives.
    //
    // TODO: MODELER-285
    //
    //   - We are over-cautious with cached copies here (which should be throw-away copies under
    //     normal circumstances) because of the issues with state synchronization between
    //     Designer and Beehive that currently exists -- these issues are commented on the
    //     DesignerState class in more detail. Once the implementations have been reviewed on
    //     both sides, the backup functionality can be made less aggressive (sparser) or disabled
    //     altogeher.

    try
    {
      backup();
    }

    // Handle errors from local cache operations (File I/O) explicitly here, and do not propagate
    // them higher up in the call stack. Errors in backups do not prevent normal operation but
    // does mean we've lost the usual recovery mechanisms so admins should be notified and act
    // to correct the problem as soon as possible.

    catch (CacheOperationException e)
    {
      haltAccountBackups.add(account.getOid());

      admin.alert("Local cache operation error : {0}", e, e.getMessage());
    }


    cacheLog.info("Updating account cache for {0}.", printUserAccountLog(currentUser));


    // Make sure cache folder is present, if not then create it...

    if (!hasCacheFolder())
    {
      createCacheFolder();      
    }


    // Invoke Beehive REST API to download the currently saved account resources and cache
    // them in Designer's local cache.
    //
    // At this point if there were previously cached archives, they've been backed up
    // (assuming backup operations worked correctly) so we can overwrite it. However,
    // the download is still done to a temp file first to ensure there were no I/O errors
    // and we don't overwrite with a partially downloaded or corrupted archive.

    BeehiveService beehive = new Beehive30API(configuration);


    try
    {
      beehive.downloadResources(currentUser, this);
    }

    catch (BeehiveServiceException e)
    {
      // Generic, unanticipated exception type...

      throw new CacheOperationException("Download of resources failed : {0}", e, e.getMessage());
    }


    // If we got through download without exceptions and still have nothing in cache...

    if (!hasState())
    {
      cacheLog.info(
          "No user resources were downloaded from Beehive. Assuming new user account ''{0}''",
          currentUser.getUsername()
      );

      return;
    }


    // If we made through all the error checking, we're ready to go. Unzip the archive and finish.

    extract(getCachedArchive(), cacheFolder);

    cacheLog.info(
        "Extracted ''{0}'' to ''{1}''.",
        getCachedArchive().getAbsolutePath(), cacheFolder.getAbsolutePath()
    );
  }


  /**
   * Indicates if we've found any resource artifacts in the cache that would imply an existing,
   * previous cache state. This includes the presence of any backup copies. <p>
   *
   * @return    true if account's cache folder holds any previously cache artifacts, false
   *            otherwise
   *
   * @throws    ConfigurationException
   *                If any of the cache operations cannot be performed due to security restrictions
   *                on the local file system.
   */
  @Override public boolean hasState() throws ConfigurationException
  {
    if (hasCachedArchive())
    {
      return true;
    }

    else if (hasNewestDailyBackup())
    {
      return true;
    }

    return false;
  }
  

  /**
   * TODO : See Javadoc on interface definition. This exists to support earlier API patterns.
   */
  @Override public void markInUseImages(Set<File> markedImageFiles)
  {
    for (File file : markedImageFiles)
    {
      try
      {
        // TODO :
        //
        //   There are still bugs in the implementation and/or previous designer serialized state
        //   that cause the domain model to reference images that are not present in the cache.
        //
        //   This is a workaround to prevent later errors occuring due to missing cache
        //   resources -- if the image being marked as 'in-use' does not exist, do not include it,
        //   instead log an error.
        //                                                                            [JPL]

        File cacheResource = new File(cacheFolder, file.getName());

        if (!cacheResource.exists())
        {
          cacheLog.error(
              "BUG: domain model references image {0} ({1}) which was not found in cache folder.",
              file, cacheResource
          );
        }

        else
        {
          imageFiles.add(file);
        }
      }

      catch (SecurityException e)
      {
        cacheLog.error(
            "Security manager denied read access to ''{0}'' : {1}",
            new File(cacheFolder, file.getName()).getAbsolutePath(), e.getMessage()
        );
      }
    }
  }

  private Set<File> imageFiles = new HashSet<File>();



  // Public Instance Methods ----------------------------------------------------------------------


  /**
   * TODO : MODELER-287
   *
   *   - Note that this still has a functional dependency to the existing legacy
   *     resource service implementation that exports the appropriate XML files
   *     into the correct file cache directory first (initResource() call).
   *
   * TODO : MODELER-289
   *
   *   - This method should not be public. Once the faulty export implementation in
   *     Designer is corrected (see Modeler-288), should become private
   *
   *
   *
   * Creates an exportable zip file archive on the local file system in the configured
   * account's cache directory. <p>
   *
   * This archive is used to send user design and configuration changes, added resource
   * files, etc. as a single HTTP POST payload to Beehive server. <p>
   *
   * This implementation is based on the current object model used in Designer which is
   * not (yet) versioned. The list of artifacts included in the export archive therefore
   * include : <p>
   *
   * <ul>
   *   <li>panel.xml</li>
   *   <li>controller.xml</li>
   *   <li>panels.obj</li>
   *   <li>lircd.conf</li>
   *   <li>image resources</li>
   * </ul>
   *
   *
   * @return  reference to the export archive file in the account's cache directory
   *
   * @throws  CacheOperationException
   *              if any of the file operations fail
   *
   * @throws  ConfigurationException
   *              if there are any security restrictions on file access
   *
   */
  public File createExportArchive() throws CacheOperationException, ConfigurationException
  {

    // File paths to add to export/upload archive...
    //   - panel.xml
    //   - controller.xml
    //   - panels.obj
    //   - lircd.conf
    //   - image resources
    //   - rules

    File panelXMLFile = new File("panel.xml");
    File controllerXMLFile = new File("controller.xml");
    File panelsObjFile = new File("panels.obj");
    File lircdFile = new File("lircd.conf");
    File rulesFile = new File("rules", "modeler_rules.drl");


    // Collect all the files going into the archive...

    Set<File> exportFiles = new HashSet<File>();
    exportFiles.addAll(this.imageFiles);
    exportFiles.add(panelXMLFile);
    exportFiles.add(controllerXMLFile);

    try
    {
      if (new File(cacheFolder, panelsObjFile.getPath()).exists())
      {
        exportFiles.add(panelsObjFile);
      }
    }

    catch (SecurityException e)
    {
      throw new ConfigurationException(
          "Security manager denied read access to file ''{0}'' (Account : {1}) : {2}",
          e, panelsObjFile.getAbsolutePath(), account.getOid(), e.getMessage()
      );
    }

    try
    {
      if (new File(cacheFolder, rulesFile.getPath()).exists())
      {
        exportFiles.add(rulesFile);
      }
    }

    catch (SecurityException e)
    {
      throw new ConfigurationException(
          "Security manager denied read access to file ''{0}'' (Account : {1}) : {2}",
          e, rulesFile.getAbsolutePath(), account.getOid(), e.getMessage()
      );
    }

    try
    {
      if (new File(cacheFolder, lircdFile.getPath()).exists())
      {
        exportFiles.add(lircdFile);
      }
    }

    catch (SecurityException e)
    {
      throw new ConfigurationException(
          "Security manager denied read access to file ''{0}'' (Account : {1}) : {2}",
          e, lircdFile, account.getOid(), e.getMessage()
      );
    }


    // Create export archive file (do not overwrite the existing beehive archive)...

    File exportDir = new File(cacheFolder, "export");
    File targetFile = new File(exportDir, BEEHIVE_ARCHIVE_NAME);

    try
    {
      if (!exportDir.exists())
      {
        boolean success = exportDir.mkdirs();

        if (!success)
        {
          throw new CacheOperationException(
              "Cannot complete export archive operation. Unable to create required " +
              "file directory ''{0}'' for cache (Account ID = {1}).",
              exportDir.getAbsolutePath(), account.getOid()
          );
        }
      }

      if (targetFile.exists())
      {
        boolean success = targetFile.delete();

        if (!success)
        {
          throw new CacheOperationException(
              "Cannot complete export archive operation. Unable to delete pre-existing " +
              "file ''{0}'' (Account ID = {1})", targetFile.getAbsolutePath(), account.getOid()
          );
        }
      }
    }

    catch (SecurityException e)
    {
      throw new ConfigurationException(
          "Security manager denied access to temporary export archive file ''{0}'' for " +
          "account ID = {1} : {2}", e, targetFile.getAbsolutePath(), account, e.getMessage()
      );
    }


    // Zip it up...

    compress(targetFile, exportFiles);


    // Done.

    return targetFile;
  }


  // Private Instance Methods ---------------------------------------------------------------------



  private void validateArchive(File tempArchive)
  {
    // TODO
  }


  /**
   * Constructs the local filesystem directory structure to store a cached
   * Beehive archive associated with a given account. <p>
   *
   * Both the local cache directory and common subdirectories are created.
   *
   * @see #hasCacheFolder
   *
   * @throws ConfigurationException
   *            if the creation of the directories fail for any reason
   */
  private void createCacheFolder() throws ConfigurationException
  {
    try
    {
      boolean success = cacheFolder.mkdirs();

      if (!success)
      {
        throw new ConfigurationException(
            "Unable to create required directories for ''{0}''.", cacheFolder.getAbsolutePath()
        );
      }

      else
      {
        cacheLog.info(
            "Created account {0} cache folder (Users: {1}).", account.getOid(), account.getUsers()
        );
      }
    }

    catch (SecurityException e)
    {
      throw new ConfigurationException(
          "Security manager has denied read/write access to local user cache in ''{0}'' : {1}",
          e, cacheFolder.getAbsolutePath(), e.getMessage()
      );
    }

    if (!hasBackupFolder())      // TODO : See MODELER-285
    {
      createBackupFolder();
    }
  }


  /**
   * Checks for the existence of local cache folder this cache implementation uses for its
   * operations.
   *
   * @return true if cache directory already exists, false otherwise
   *
   * @throws ConfigurationException
   *            if read access to file system has been denied
   */
  private boolean hasCacheFolder() throws ConfigurationException
  {
    try
    {
      return cacheFolder.exists();
    }

    catch (SecurityException e)
    {
      throw new ConfigurationException(
          "Security manager has denied read access to local user cache in ''{0}'' : {1}",
          e, cacheFolder.getAbsolutePath(), e.getMessage()
      );
    }
  }


  /**
   * Returns the local filesystem path to a Beehive archive in current user's account.
   *
   * @return    file path where the Beehive archive is stored in cache folder for
   *            this account
   */
  private File getCachedArchive()
  {
    return new File(cacheFolder, BEEHIVE_ARCHIVE_NAME);
  }

  /**
   * Tests for existence of a Beehive archive in the user's cache directory (as specified
   * by {@link #BEEHIVE_ARCHIVE_NAME} constant}.
   *
   * @see #getCachedArchive
   *
   * @return    true if Beehive archive is present in user account's cache folder, false
   *            otherwise
   *
   * @throws ConfigurationException
   *            if file read access is denied by security manager
   */
  private boolean hasCachedArchive() throws ConfigurationException
  {
    File f = getCachedArchive();

    try
    {
      return f.exists();
    }

    catch (SecurityException e)
    {
      throw new ConfigurationException(
        "Security manager has denied read access to ''{0}'' : {1}",
        e, f.getAbsolutePath(), e.getMessage()
      );
    }
  }




  /**
   * TODO : MODELER-285 -- this implementation should migrate to Beehive side.
   *
   * Creates a backup of the downloaded Beehive archive in the given account's cache folder.
   * Backups are created at most once per day. Number of daily backups can be limited. Daily
   * backups may optionally be rolled to weekly or monthly backups.
   *
   * @throws ConfigurationException
   *            if read/write access to cache folder or cache backup folder has not been granted,
   *            or creation of the required files fails for any other reason
   *
   * @throws CacheOperationException
   *            if any runtime or I/O errors occur during the normal file operations required
   *            to create backups, or if there's any indication that backup operation is not
   *            working as expected and admin should be notified
   */
  private void backup() throws ConfigurationException, CacheOperationException
  {

    // See if we should run...

    if (haltAccountBackups.contains(account.getOid()))
    {
      cacheLog.warn("Backups for account {0} have been stopped!", account.getOid());

      return;
    }


    // Sanity check...

    if (!hasCachedArchive() && hasNewestDailyBackup())
    {
      // It looks like the originally cached BEEHIVE_ARCHIVE_NAME has disappeared, but some
      // cache backup is still in place.
      //
      // This is odd, so bail out and notify.

      throw new CacheOperationException(
          "The Beehive archive was not found in cache but some backups were created earlier. " +
          "Should investigate the issue."
      );
    }


    // If no BEEHIVE_ARCHIVE_NAME file is found in cache directory, (and there are no backups)
    // it means this is the first time the Beehive archive will be downloaded (hopefully,
    // otherwise we got some deeper issues). There's nothing to back up yet.

    if (!hasCachedArchive())
    {
      cacheLog.info("No existing Beehive archive found. Backup skipped.");

      return;
    }


    // If it looks like this is the first backup being made (hopefully not due to cache
    // backups getting wiped due to some other errors)...

    if (!hasBackupFolder())
    {
      createBackupFolder();
    }

    try
    {
      // If there are no existing backups, just make a blanket copy...

      if (!hasNewestDailyBackup())
      {
        makeDailyBackup();
      }

      else
      {

        // If previous backups exist, check the timestamps and create a new one if the
        // newest is older than 24 hours...

        long timestamp = getCachedArchive().lastModified();
        long backuptime = getNewestDailyBackupFile().lastModified();

        if (timestamp - backuptime > DAY)
        {
          cacheLog.info(
              "Current daily backup is {0} days old. Creating a new backup...",
              new DecimalFormat("######0.00").format((float)(timestamp - backuptime) / DAY)
          );

          makeDailyBackup();
        }

        else
        {
          cacheLog.info(
              "Current archive was created only {0} hours ago. Skipping daily backup...",
              new DecimalFormat("######0.00").format(((float)(timestamp - backuptime)) / HOUR)
          );
        }
      }
    }

    catch (SecurityException e)
    {
      throw new ConfigurationException(
          "Security manager has denied read access to ''{0}'' : {1}",
          e, getCachedArchive().getAbsolutePath(), e.getMessage()
      );
    }
  }


  /**
   * TODO : MODELER-285 -- migrate this implementation to Beehive side
   *
   * Creates a daily backup copy of the downloaded (and cached) Beehive archive for the associated
   * account. The backup is only created if the existing (if any) backup is older than one day
   * (based on file timestamps). The copy is created via a temp file in case I/O errors occur.
   *
   * @see #hasCachedArchive
   * @see #getCachedArchive
   * @see #hasBackupFolder
   * @see #getBackupFolder
   *
   * @throws CacheOperationException
   *            if any runtime or I/O errors occur while making backups
   *
   * @throws ConfigurationException
   *            if read/write access to cache backup directory has been denied
   */
  private void makeDailyBackup() throws CacheOperationException, ConfigurationException
  {

    /**
     * The Maximum number of daily backups we'll create.
     */
    final int MAX_DAILY_BACKUPS = 5;

    File cacheFolder = getBackupFolder();

    cacheLog.debug("Making a daily backup of current Beehive archive...");

    try
    {
      File oldestDaily = new File(DAILY_BACKUP_PREFIX + "." + MAX_DAILY_BACKUPS);

      // If we've reached the maximum number of copies already, see if any of them
      // are more than a week old (and could be stored to weekly backups).

      if (oldestDaily.exists())
      {
        moveToWeeklyBackup(oldestDaily);
      }


      // Shuffle older backup copies out of the way (down by one index) to make space
      // for the latest to be saved in DAILY_BACKUP_PREFIX.1

      for (int index = MAX_DAILY_BACKUPS - 1; index > 0; index--)
      {
        File daily = new File(cacheFolder, DAILY_BACKUP_PREFIX + "." + index);
        File target = new File(cacheFolder, DAILY_BACKUP_PREFIX + "." + (index + 1));

        if (!daily.exists())
        {
          cacheLog.debug(
              "Daily backup file ''{0}'' was not present. Skipping...", daily.getAbsolutePath()
          );

          continue;
        }

        if (!daily.renameTo(target))
        {
          sortBackups();

          throw new CacheOperationException(
              "There was an error moving ''{0}'' to ''{1}''.",
              daily.getAbsolutePath(), target.getAbsolutePath()
          );
        }

        else
        {
          cacheLog.debug("Moved " + daily.getAbsolutePath() + " to " + target.getAbsolutePath());
        }
      }
    }

    catch (SecurityException e)
    {
      throw new ConfigurationException(
          "Security Manager has denied read/write access to daily backup files in ''{0}'' : {1}" +
          e, cacheFolder.getAbsolutePath(), e.getMessage()
      );
    }


    // Now make the actual copy of existing Beehive archive into cache backups. Copy to a
    // temp file first in case of errors (such as out-of-disk-space) might occur.

    File beehiveArchive = getCachedArchive();
    File tempBackupArchive = new File(cacheFolder, BEEHIVE_ARCHIVE_NAME + ".tmp");

    BufferedInputStream archiveReader = null;
    BufferedOutputStream tempBackupWriter = null;

    try
    {
      archiveReader = new BufferedInputStream(new FileInputStream(beehiveArchive));
      tempBackupWriter = new BufferedOutputStream(new FileOutputStream(tempBackupArchive));

      int len, bytecount = 0;
      final int BUFFER_SIZE = 4096;
      byte[] buffer = new byte[BUFFER_SIZE];

      while ((len = archiveReader.read(buffer, 0, BUFFER_SIZE )) != -1)
      {
        tempBackupWriter.write(buffer, 0, len);

        bytecount += len;
      }

      tempBackupWriter.flush();

      long originalFileSize = beehiveArchive.length();


      // Just a sanity check -- we should get the same amount of bytes on both sides on copy...

      if (originalFileSize != bytecount)
      {
        throw new CacheOperationException(
            "Original archive size was {0} bytes but only {1} were copied.",
            originalFileSize, bytecount
        );
      }

      cacheLog.debug(
          "Finished copying ''{0}'' to ''{1}''.",
          beehiveArchive.getAbsolutePath(), tempBackupArchive.getAbsolutePath()
      );
    }

    catch (FileNotFoundException e)
    {
      throw new CacheOperationException(
          "Files required for copying a backup of Beehive archive could not be found, opened " +
          "or created : {1}", e, e.getMessage()
      );
    }

    catch (IOException e)
    {
      throw new CacheOperationException(
          "Error while making a copy of the Beehive archive : {0}", e, e.getMessage()
      );
    }

    finally
    {
      if (archiveReader != null)
      {
        try
        {
          archiveReader.close();
        }

        catch (Throwable t)
        {
          cacheLog.warn(
              "Failed to close stream to ''{0}'' : {1}",
              t, beehiveArchive.getAbsolutePath(), t.getMessage()
          );
        }
      }

      if (tempBackupWriter != null)
      {
        try
        {
          tempBackupWriter.close();
        }

        catch (Throwable t)
        {
          cacheLog.warn(
              "Failed to close stream to ''{0}'' : {1}",
              t, tempBackupArchive.getAbsolutePath(), t.getMessage()
          );
        }
      }
    }


    validateArchive(tempBackupArchive);


    // Copy was ok, now move from temp to actual location...

    File newestDaily = getNewestDailyBackupFile();

    try
    {
      if (!tempBackupArchive.renameTo(newestDaily))
      {
        throw new CacheOperationException(
            "Error moving ''{0}'' to ''{1}''.",
            tempBackupArchive.getAbsolutePath(), newestDaily.getAbsolutePath()
        );
      }

      else
      {
        cacheLog.info("Backup complete. Saved in ''{0}''", newestDaily.getAbsolutePath());
      }
    }

    catch (SecurityException e)
    {
      throw new ConfigurationException(
          "Security Manager has denied write access to ''{0}'' : {1}",
          e, newestDaily.getAbsolutePath(), e.getMessage()
      );
    }
  }


  private static void moveToWeeklyBackup(File dailyBackupToMove)
  {
    // TODO

    cacheLog.error("Cannot make weekly backup. Weekly backups not implemented yet.");
  }

  private static void sortBackups()
  {
    // TODO
  }


  /**
   * TODO : http://jira.openremote.org/browse/MODELER-285
   *
   * Constructs the local filesystem backup directory for Beehive archives.
   *
   * @throws ConfigurationException
   *            if the creation of the directory fail for any reason
   */
  private void createBackupFolder() throws ConfigurationException
  {
    File backupFolder = getBackupFolder();

    try
    {
      boolean success = backupFolder.mkdirs();

      if (!success)
      {
        throw new ConfigurationException(
            "Unable to create required directories for ''{0}''.", backupFolder.getAbsolutePath()
        );
      }

      else
      {
        cacheLog.debug(
            "Created cache backup folder for account {0} (Users: {1}).",
            account.getOid(), account.getUsers()
        );
      }
    }

    catch (SecurityException e)
    {
      throw new ConfigurationException(
          "Security manager has denied read/write access to local user cache in ''{0}'' : {1}",
          e, backupFolder.getAbsolutePath(), e.getMessage()
      );
    }
  }


  /**
   * TODO : http://jira.openremote.org/browse/MODELER-285
   *
   * Checks for the existence of cache *backup* folder.
   *
   * @return true if cache backup directory already exists, false otherwise
   *
   * @throws ConfigurationException
   *            if read access to file system has been denied
   */
  private boolean hasBackupFolder() throws ConfigurationException
  {
    File backupFolder = getBackupFolder();

    try
    {
      return backupFolder.exists();
    }

    catch (SecurityException e)
    {
      throw new ConfigurationException(
          "Security manager has denied read access to local user cache in ''{0}'' : {1}",
          e, backupFolder.getAbsolutePath(), e.getMessage()
      );
    }
  }


  /**
   * TODO : http://jira.openremote.org/browse/MODELER-285
   *
   * Returns a file path to archive backup folder of this account's local file cache.
   *
   * @return  path to a directory that can be used to store backups of downloaded
   *          Beehive archives for the associated account
   */
  private File getBackupFolder()
  {
    return new File(cacheFolder, "cache-backup");
  }


  /**
   * TODO : http://jira.openremote.org/browse/MODELER-285
   *
   * Checks for the existence of a latest daily backup copy of this account's Beehive
   * archive.
   *
   * @see #getNewestDailyBackupFile
   *
   * @return    true if what is marked as the most recent copy of the account's Beehive archive
   *            exists in the backup directory, false otherwise
   *
   * @throws ConfigurationException
   *            if read access to the latest daily backup copy has been denied
   */
  private boolean hasNewestDailyBackup() throws ConfigurationException
  {
    File cacheFolder = getBackupFolder();
    File newestDaily = getNewestDailyBackupFile();

    try
    {
      return newestDaily.exists();
    }

    catch (SecurityException e)
    {
      throw new ConfigurationException(
          "Security manager has denied read access to ''{0}'' in ''{1}'' : {2}",
          e, newestDaily, cacheFolder.getAbsolutePath(), e.getMessage()
      );
    }
  }


  /**
   * TODO : http://jira.openremote.org/browse/MODELER-285
   *
   * Returns a file path to the latest daily backup of this account's Beehive archive.
   *
   * @see #hasNewestDailyBackup
   *
   * @return  path to a file where the latest daily backup of this account's Beehive archive
   *          is stored
   */
  private File getNewestDailyBackupFile()
  {
    return new File(getBackupFolder(), DAILY_BACKUP_PREFIX + ".1");
  }


  /**
   * Extracts a source resource archive to target path in local filesystem. Necessary
   * subdirectories will be created according to archive structure if necessary. Existing
   * files and directories matching the archive structure <b>will be deleted!</b>
   *
   * @param sourceArchive     file path to source archive in the local filesystem.
   * @param targetDirectory   file path to target directory where to extract the archive
   *
   * @throws CacheOperationException
   *            if any file I/O errors occured during the extract operation
   *
   * @throws ConfigurationException
   *            if security manager has imposed access restrictions to the required files or
   *            directories
   */
  private void extract(File sourceArchive, File targetDirectory) throws CacheOperationException,
                                                                        ConfigurationException
  {
    ZipInputStream archiveInput = null;
    ZipEntry zipEntry;

    try
    {
      archiveInput = new ZipInputStream(new BufferedInputStream(new FileInputStream(sourceArchive)));

      while ((zipEntry = archiveInput.getNextEntry()) != null)
      {
        if (zipEntry.isDirectory())
        {
          // Do nothing -- relevant subdirectories will be created when handling the node files...

          continue;
        }

        File extractFile = new File(targetDirectory, zipEntry.getName());
        BufferedOutputStream extractOutput = null;

        try
        {
          FileUtilsExt.deleteQuietly(extractFile); // TODO : don't be quiet

          // create parent directories if necessary...

          if (!extractFile.getParentFile().exists())
          {
            boolean success = extractFile.getParentFile().mkdirs();

            if (!success)
            {
              throw new CacheOperationException(
                  "Unable to create cache folder directories ''{0}''. Reason unknown.",
                  extractFile.getParent()
              );
            }
          }

          extractOutput = new BufferedOutputStream(new FileOutputStream(extractFile));

          int len, bytecount = 0;
          byte[] buffer = new byte[4096];

          while ((len = archiveInput.read(buffer)) != -1)
          {
            try
            {
              extractOutput.write(buffer, 0, len);

              bytecount += len;
            }

            catch (IOException e)
            {
              throw new CacheOperationException(
                  "Error writing to ''{0}'' : {1}",
                  e, extractFile.getAbsolutePath(), e.getMessage()
              );
            }
          }

          cacheLog.debug(
              "Wrote {0} bytes to ''{1}''...", bytecount, extractFile.getAbsolutePath()
          );
        }

        catch (SecurityException e)
        {
          throw new ConfigurationException(
              "Security manager has denied access to ''{0}'' : {1}",
              e, extractFile.getAbsolutePath(), e.getMessage()
          );
        }

        catch (FileNotFoundException e)
        {
          throw new CacheOperationException(
              "Could not create file ''{0}'' : {1}",
              e, extractFile.getAbsolutePath(), e.getMessage()
          );
        }

        catch (IOException e)
        {
          throw new CacheOperationException(
              "Error reading zip entry ''{0}'' from ''{1}'' : {2}",
              e, zipEntry.getName(), sourceArchive.getAbsolutePath(), e.getMessage()
          );
        }

        finally
        {
          if (extractOutput != null)
          {
            try
            {
              extractOutput.close();
            }

            catch (Throwable t)
            {
              cacheLog.error(
                  "Could not close extracted file ''{0}'' : {1}",
                  t, extractFile.getAbsolutePath(), t.getMessage()
              );
            }
          }

          if (archiveInput != null)
          {
            try
            {
              archiveInput.closeEntry();
            }

            catch (Throwable t)
            {
              cacheLog.warn(
                  "Could not close zip entry ''{0}'' in archive ''{1}'' : {2}",
                  t, zipEntry.getName(), t.getMessage()
              );
            }
          }
        }
      }
    }

    catch (SecurityException e)
    {
      throw new ConfigurationException(
          "Security Manager has denied access to ''{0}'' : {1}",
          e, sourceArchive.getAbsolutePath(), e.getMessage()
      );
    }

    catch (FileNotFoundException e)
    {
      throw new CacheOperationException(
          "Archive ''{0}'' cannot be opened for reading : {1}",
          e, sourceArchive.getAbsolutePath(), e.getMessage()
      );
    }

    catch (IOException e)
    {
      throw new CacheOperationException(
          "Error reading archive ''{0}'' : {1}",
          e, sourceArchive.getAbsolutePath(), e.getMessage()
      );
    }

    finally
    {
      try
      {
        if (archiveInput != null)
        {
          archiveInput.close();
        }
      }

      catch (Throwable t)
      {
        cacheLog.error(
            "Error closing input stream to archive ''{0}'' : {1}",
            t, sourceArchive.getAbsolutePath(), t.getMessage()
        );
      }
    }
  }


  /**
   * Compresses a set of files into a target zip archive. The file instances should be relative
   * paths used to structure the archive into directories. The relative paths will be resolved
   * to actual file paths in the current account's file cache.
   *
   * @param target    Target file path where the zip archive will be stored.
   * @param files     Set of <b>relative</b> file paths to include in the zip archive. The file
   *                  paths should be set to match the expected directory structure in the final
   *                  archive (therefore should not reflect the absolute file paths expected to
   *                  be included in the archive).
   *
   * @throws CacheOperationException
   *            if any of the zip file operations fail
   *
   * @throws ConfigurationException
   *            if there are any security restrictions about reading the set of included files
   *            or writing the target zip archive file
   */
  private void compress(File target, Set<File> files) throws CacheOperationException,
                                                             ConfigurationException
  {
    ZipOutputStream zipOutput = null;

    try
    {
      zipOutput = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(target)));

      for (File file : files)
      {
        BufferedInputStream fileInput = null;

        // translate the relative zip archive directory path to existing user cache absolute path...

        File cachePathName = new File(cacheFolder, file.getPath());

        try
        {
          if (!cachePathName.exists())
          {
            throw new CacheOperationException(
                "Expected to add file ''{0}'' to export archive ''{1}'' (Account : {2}) but it " +
                "has gone missing (cause unknown). This can indicate implementation or deployment " +
                "error. Aborting export operation as a safety precaution.",
                cachePathName.getPath(), target.getAbsolutePath(), account.getOid()
            );
          }

          fileInput = new BufferedInputStream(new FileInputStream(cachePathName));

          ZipEntry entry = new ZipEntry(file.getPath());

          entry.setSize(cachePathName.length());
          entry.setTime(cachePathName.lastModified());

          zipOutput.putNextEntry(entry);

          cacheLog.debug("Added new export zip entry ''{0}''.", file.getPath());

          int count, total = 0;
          final int BUFFER_SIZE = 2048;
          byte[] data = new byte[BUFFER_SIZE];

          while ((count = fileInput.read(data, 0, BUFFER_SIZE)) != -1)
          {
            zipOutput.write(data, 0, count);

            total += count;
          }

          zipOutput.flush();

          // Sanity check...

          if (total != cachePathName.length())
          {
            throw new CacheOperationException(
                "Only wrote {0} out of {1} bytes when archiving file ''{2}'' (Account : {3}). " +
                "This could have occured either due implementation error or file I/O error. " +
                "Aborting archive operation to prevent a potentially corrupt export archive to " +
                "be created.", total, cachePathName.length(), cachePathName.getPath(), account.getOid()
            );
          }

          else
          {
            cacheLog.debug(
                "Wrote {0} out of {1} bytes to zip entry ''{2}''",
                total, cachePathName.length(), file.getPath()
            );
          }
        }

        catch (SecurityException e)
        {
          // we've messed up deployment... quite likely unrecoverable...

          throw new ConfigurationException(
              "Security manager has denied r/w access when attempting to read file ''{0}'' and " +
              "write it to archive ''{1}'' (Account : {2}) : {3}",
              e, cachePathName.getPath(), target, account.getOid(), e.getMessage()
          );
        }

        catch (IllegalArgumentException e)
        {
          // This may occur if we overrun some fixed size limits in ZIP format...

          throw new CacheOperationException(
              "Error creating ZIP archive for account ID = {0} : {1}",
              e, account.getOid(), e.getMessage()
          );
        }

        catch (FileNotFoundException e)
        {
          throw new CacheOperationException(
              "Attempted to include file ''{0}'' in export archive but it has gone missing " +
              "(Account : {1}). Possible implementation error in local file cache. Aborting  " +
              "export operation as a precaution ({2})",
              e, cachePathName.getPath(), account.getOid(), e.getMessage()
          );
        }

        catch (ZipException e)
        {
          throw new CacheOperationException(
              "Error writing export archive for account ID = {0} : {1}",
              e, account.getOid(), e.getMessage()
          );
        }

        catch (IOException e)
        {
          throw new CacheOperationException(
              "I/O error while creating export archive for account ID = {0}. " +
              "Operation aborted ({1})", e, account.getOid(), e.getMessage()
          );
        }

        finally
        {
          if (zipOutput != null)
          {
            try
            {
              zipOutput.closeEntry();
            }

            catch (Throwable t)
            {
              cacheLog.warn(
                  "Unable to close zip entry for file ''{0}'' in export archive ''{1}'' " +
                  "(Account : {2}) : {3}.",
                  t, file.getPath(), target.getAbsolutePath(), account.getOid(), t.getMessage()
              );
            }
          }

          if (fileInput != null)
          {
            try
            {
              fileInput.close();
            }

            catch (Throwable t)
            {
              cacheLog.warn(
                  "Failed to close input stream from file ''{0}'' being added " +
                  "to export archive (Account : {1}) : {2}", t, cachePathName.getPath(), account.getOid(), t.getMessage()
              );
            }
          }
        }
      }
    }

    catch (FileNotFoundException e)
    {
      throw new CacheOperationException(
          "Unable to create target export archive ''{0}'' for account {1) : {2}",
          e, target, account.getOid(), e.getMessage()
      );
    }

    finally
    {
      try
      {
        if (zipOutput != null)
        {
          zipOutput.close();
        }
      }

      catch (Throwable t)
      {
        cacheLog.warn(
            "Failed to close the stream to export archive ''{0}'' : {1}.", 
            t, target, t.getMessage()
        );
      }
    }
  }


  /**
   * Helper for logging user information.
   *
   * TODO : should be reused via User domain object
   *
   * @param currentUser   current logged in user (as per the http session associated with this
   *                      thread)
   *
   * @return    string with user name, email, role and account id information
   */
  private String printUserAccountLog(User currentUser)
  {
    return "(User: " + currentUser.getUsername() +
           ", Email: " + currentUser.getEmail() +
           ", Roles: " + currentUser.getRole() +
           ", Account ID: " + currentUser.getAccount().getOid() +
           ")";
  }



  // Inner Classes -------------------------------------------------------------------------------


  /**
   * Implements a file-based write stream into the cache. The after processing is used to move
   * the downloaded archive (once marked complete and validated) from the temporary download file
   * location to the final location in the filesystem. This should ensure we don't deal with
   * partial downloads.
   */
  private class FileCacheWriteStream extends CacheWriteStream
  {

    /**
     * Path to the temporary download location.
     */
    private File temp;

    /**
     * Constructs a new file based cache write stream.
     *
     * @param tempTarget    initial location where the downloaded bytes are stored
     *
     * @throws FileNotFoundException
     *              if the temporary file target cannot be created or opened
     *
     * @throws SecurityException
     *              if security manager denied access to creating or opening the temporary file
     */
    private FileCacheWriteStream(File tempTarget) throws FileNotFoundException, SecurityException
    {
      super(new BufferedOutputStream(new FileOutputStream(tempTarget)));

      this.temp = tempTarget;
    }

    /**
     * Invoked for streams that have been marked completed upon stream close. <p>
     *
     * Validate the download archive and move it to its final path location before continuing.
     *
     * @throws IOException
     */
    @Override protected void afterClose() throws IOException
    {
      // Can check that we have space on the filesystem to extract the archive, that
      // archive is not corrupt, and specific files are included in the archive.

      validateArchive(temp);


      // Got complete download, archive has been validated. Now make it 'final'.
      // Move is often much faster than copy.

      File finalTarget = getCachedArchive();

      try
      {
        boolean success = temp.renameTo(finalTarget);

        if (!success)
        {
          throw new IOException(MessageFormat.format(
              "Failed to replace existing Beehive archive ''{0}'' with ''{1}''",
              finalTarget.getAbsolutePath(), temp.getAbsolutePath())
          );
        }

        cacheLog.info(
            "Moved ''{0}'' to ''{1}''", temp.getAbsolutePath(), finalTarget.getAbsolutePath()
        );
      }

      catch (SecurityException e)
      {
        throw new IOException(MessageFormat.format(
            "Security manager has denied write access to ''{0}'' : {1}",
            e, finalTarget.getAbsolutePath(), e.getMessage())
        );
      }
    }

    @Override public void close()
    {
      try
      {
        super.close();
      }

      catch (Throwable t)
      {
        cacheLog.warn(
            "Unable to close resource archive cache stream : {0}",
            t, t.getMessage()
        );

      }
    }

    @Override public String toString()
    {
      return "Stream Target : " + temp.getAbsolutePath();
    }
  }

}

