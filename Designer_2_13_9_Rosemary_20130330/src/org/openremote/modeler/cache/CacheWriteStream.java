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

import java.io.FilterOutputStream;
import java.io.OutputStream;
import java.io.IOException;

import org.openremote.modeler.logging.LogFacade;

/**
 * Specialized output stream to write data into a {@link ResourceCache}. <p>
 *
 * This class adds an operation to mark the incoming stream data as 'complete' by
 * the caller (client). Resource cache implementations may discard data from the
 * stream if the stream has been closed but was not marked as complete (to avoid
 * storing/processing incomplete data). <p>
 *
 * Therefore, when writing data into the resource cache, invoke {@link #markCompleted()}
 * <b>before</b> closing the stream (otherwise cache implementation may choose to ignore
 * the incoming byte stream). <p>
 *
 * This class also provides a callback {@link #afterClose()} which is invoked <b>if</b>
 * the stream has been marked completed by caller/client and the stream is then closed.
 * This allows cache implementations to do after processing of the sent data, such as moving
 * the bytes from temporary work buffers into actual live cache entries.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public abstract class CacheWriteStream extends FilterOutputStream
{

  // Instance Fields ------------------------------------------------------------------------------

  /**
   * Indicates if the written data stream is considered complete.
   */
  private boolean complete = false;


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a new output stream with a given underlying (actual) output stream.
   *
   * @param out   nested output stream where write operations are delegated to
   */
  protected CacheWriteStream(OutputStream out)
  {
    super(out);
  }


  // FilterOutputStream Overrides -----------------------------------------------------------------


  /**
   * Will close the underlying output stream. If the byte stream has been marked as 'complete'
   * by the caller, will invoke an {@link #afterClose()} operation that can be used by cache
   * implementations for post-processing of the data.
   *
   * @throws IOException    in case of any I/O errors from underlying stream
   */
  @Override public void close() throws IOException
  {
    super.out.close();

    if (complete)
    {
      this.afterClose();
    }
  }


  // Public Instance Methods ----------------------------------------------------------------------


  /**
   * Marks this stream as complete. Calling this method also closes the underlying stream,
   * therefore it is not possible to write additional data into the output stream once it has
   * been marked as complete.
   */
  public void markCompleted() 
  {
    try
    {
      super.out.flush();
      super.out.close();

      complete = true;
    }

    catch (IOException e)
    {
      LogFacade.getInstance(LogFacade.Category.CACHE).error(
          "Failed to close underlying stream : {0}", e, e.getMessage()
      );
    }
  }



  // Protected Instance Methods -------------------------------------------------------------------

  /**
   * Callback implementation for concrete cache write stream implementations. Cache implementations
   * should provide their own concrete stream implementations with necessary data post-processing.
   * This method is invoked if 1) incoming write stream has been marked as complete by the caller
   * and 2) the stream has been explicitly closed.
   *
   * @throws IOException    in case of any I/O errors
   */
  protected abstract void afterClose() throws IOException;

}

