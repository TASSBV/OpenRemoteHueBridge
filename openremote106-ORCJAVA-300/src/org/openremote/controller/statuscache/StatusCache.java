/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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
package org.openremote.controller.statuscache;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.openremote.controller.Constants;
import org.openremote.controller.exception.NoSuchComponentException;
import org.openremote.controller.exception.ResourceNotFoundException;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.Command;
import org.openremote.controller.protocol.Event;
import org.openremote.controller.utils.Logger;

/**
 * TODO:
 *
 *   ORCJAVA-208 -- internalize ChangedStatusTable and ChangedStatusRecord into this implementation
 *
 *
 * @author @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 * @author Javen Zhang
 */
public class StatusCache
{

  // Class Members --------------------------------------------------------------------------------

  /**
   * Common status cache logging category on operations that occur during runtime (not part
   * of lifecycle start/stop operations).
   */
  private final static Logger log = Logger.getLogger(Constants.RUNTIME_STATECACHE_LOG_CATEGORY);


  // Private Instance Fields ----------------------------------------------------------------------


  /**
   * Maintains a map of sensor ids to their values in cache.
   */
  private SensorMap sensorMap;


  /**
   * A chain of event processors that incoming events (values) are forced through before
   * their values are stored in the cache. <p>
   *
   * Event processors may modify the existing values, discard events entirely or spawn
   * multiple other events that are included in the state cache.
   */
  private EventProcessorChain eventProcessorChain;

  /**
   * TODO : Map of sensor IDs to actual sensor instances.
   */
  private Map<Integer, Sensor> sensors = new ConcurrentHashMap<Integer, Sensor>();

  /**
   * Used to indicate if the state cache is in the middle of a shut down process -- this
   * flag can be used by methods to fail-fast in such cases.
   */
  private volatile Boolean isShutdownInProcess = false;


  //private EventContext.CommandFacade commandFacade;



  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a new status cache instance.
   */
  public StatusCache()
  {
    this(new ChangedStatusTable(), new EventProcessorChain());
  }

  /**
   * TODO : ORCJAVA-208
   *
   */
  public StatusCache(ChangedStatusTable cst, EventProcessorChain epc)
  {
    this.eventProcessorChain = epc;
    this.sensorMap = new SensorMap(cst);
  }




  // Public Instance Methods ----------------------------------------------------------------------


  public void start()
  {
    eventProcessorChain.start();
  }


  /**
   * Register a sensor with this cache instance. The registered sensor will participate in
   * cache's lifecycle.
   *
   * @param sensor    sensor to register
   */
  public synchronized void registerSensor(Sensor sensor)
  {
    // TODO :
    //   push thread synchronization down to sensorMap.init() once
    //   Sensor references are handled there
    //                                                    [JPL]

    if (isShutdownInProcess)
    {
      return;
    }

    Sensor previous = sensors.put(sensor.getSensorID(), sensor);

    // Use a specific log category just to log the creation of sensor objects
    // in this method (happens at startup or soft restart)...

    Logger initLog = Logger.getLogger(Constants.SENSOR_INIT_LOG_CATEGORY);

    if (previous != null)
    {
      initLog.error(
          "Duplicate registration of sensor ID {0}. Sensor ''{1}'' has replaced ''{2}''.",
          sensor.getSensorID(), sensor.getName(), previous.getName()
      );
    }

    sensorMap.init(sensor);

    initLog.info("Registered sensor : {0}", sensor.toString());
  }



  /**
   * Returns a sensor instance associated with the given ID.
   *
   * @param id    sensor ID
   *
   * @return      sensor instance
   */
  public Sensor getSensor(int id)
  {
    // TODO :
    //   Should eventually return a sensor interface, see ORCJAVA-123

    // TODO :
    //   This method is currently only consumed by Deployer implementation. The deployer in turn
    //   is only using it to host a temporary method that may still move due to refactoring.
    //   At that point the whole delegation through Deployer may not be very meaningful.
    
    return sensors.get(id);
  }


  /**
   * Performs a state cache cleanup at shut down. This method is synchronized, preventing
   * concurrent thread access to shutdown steps. <p>
   *
   * Part of the shutdown of state cache:
   * <ul>
   * <li>registered sensors are stopped</li>
   * <li>the in-memory states are cleared</li>
   * <li>registered sensors are unregistered</li>
   * </ul>
   *
   * This allows more orderly cleanup of the resources associated with this state cache --
   * namely the sensor threads are allowed to cleanup and stop properly. <p>
   *
   * Once the shutdown is completed, this cache instance can be discarded. There's no corresponding
   * start operation to allow reuse of this object.
   */
  public synchronized void shutdown()
  {
    {
      try
      {
        isShutdownInProcess = true;

        eventProcessorChain.stop();

        stopSensors();

        sensorMap.clear();

        sensors.clear();        // TODO
      }

      finally
      {
        isShutdownInProcess = false;
      }
    }
  }


  
  /**
   * Updates an incoming event value into cache. <p>
   *
   * <b>TODO:</b>
   *
   * This method is currently synchronized to restrict concurrency -- events are processed
   * and updated one-by-one. The implications of concurrent event processing through the processors
   * and concurrent updates must be evaluated. See ORCJAVA-205.
   *
   * @param event   the event to process -- the actual value stored in this cache will depend
   *                on the modifications made by event processors associated with this cache
   */
  public synchronized void update(Event event)
  {

    // fail fast on incoming sensor updates, if we want to shut things down already...

    if (isShutdownInProcess)
    {
      log.debug(
          "Device state cache is shutting down. Ignoring update from ''{0}'' (ID = ''{1}'').",
          event.getSource(), event.getSourceID()
      );

      return;
    }


    // push incoming event through processing chain -- keep the last returned instance including
    // modifications if any...

    EventContext ctx = new EventContext(this, event, eventProcessorChain.getCommandFacade());

    eventProcessorChain.push(ctx);

    // Update the final value...

    if (!ctx.hasTerminated())
    {
      sensorMap.update(ctx.getEvent());
    }

    else
    {
      log.debug(
          "Event {0} was terminated by event processors. No update was made to device state cache.",
          ctx.getEvent()
      );
    }
  }


  /**
   * TODO :
   *   Not sure we need this, could just loop on caller side to do multiple queryStatus().
   *   May be more consistent, especially because of the unoptimal exception handling here, etc.
   *
   *   See ORCJAVA-206.
   *
   * @throws NoSuchComponentException when the component id is not cached.
   */
  public Map<Integer, String> queryStatus(Set<Integer> sensorIDs)
  {
    if (sensorIDs == null || sensorIDs.size() == 0)
    {
       return null;     // TODO : return an empty collection instead
    }

    log.trace("Query status for sensor IDs : {0}", sensorIDs);

    Map<Integer, String> statuses = new HashMap<Integer, String>();

    for (Integer sensorId : sensorIDs) 
    {
       statuses.put(sensorId, queryStatus(sensorId));
    }

    log.trace("Returning sensor status map (ID, Value) : {0}", statuses);

    return statuses;
  }


  /**
   * Returns the current in-memory state of the given sensor ID.
   *
   * TODO : ORCJAVA-203 -- migrate to Event API
   *
   * @param sensorID    requested sensor ID
   *
   * @return  current cache-stored value for the given sensor ID
   */
  public String queryStatus(Integer sensorID)
  {
    if (!sensorMap.hasExistingState(sensorID))
    {

      log.error(
          "Requested sensor id ''{0}'' was not found. Defaulting to ''{1}''.",
          sensorID, Sensor.UNKNOWN_STATUS);

      return Sensor.UNKNOWN_STATUS;
    }

    return sensorMap.getCurrentState(sensorID).serialize();
  }


  /**
   * TODO
   *
   * @param name
   * @return
   * @throws ResourceNotFoundException
   */
  public Event queryStatus(String name) throws ResourceNotFoundException
  {
    return sensorMap.get(name);
  }

 
  public Iterator<Event> getStateSnapshot()
  {
    return sensorMap.getSnapshot();
  }


  public void initializeEventContext(Set<Command> commands)
  {
    eventProcessorChain.createCommandFacade(commands);
  }



  // Private Instance Methods ---------------------------------------------------------------------


  /**
   * TODO
   */
  private void stopSensors()
  {
    for (Sensor sensor : sensors.values())
    {
      log.info(
          "Stopping sensor ''{0}'' (ID = ''{1}'')...",
          sensor.getName(), sensor.getSensorID()
      );

      try
      {
        sensor.stop();
      }

      catch (Throwable t)
      {
        log.error(
            "Failed to stop sensor ''{0}'' (ID = ''{1}'') : {2}",
            t, sensor.getName(), sensor.getSensorID(), t.getMessage()
        );
      }
    }
  }



  // Inner Classes --------------------------------------------------------------------------------


  /**
   * TODO
   */
  private class SensorMap
  {

    private Map<String, Integer> nameIdIndex = new ConcurrentHashMap<String, Integer>();
    private Map<Integer, Event> currentState = new ConcurrentHashMap<Integer, Event>();
    private ChangedStatusTable deviceStatusChanges;

    private SensorMap(ChangedStatusTable cst)
    {
      this.deviceStatusChanges = cst;
    }

    private void init(Sensor sensor)
    {
      nameIdIndex.put(sensor.getName(), sensor.getSensorID());
      currentState.put(sensor.getSensorID(), new Sensor.UnknownEvent(sensor));
    }

    private Iterator<Event> getSnapshot()
    {
      return currentState.values().iterator();  
    }

    private void clear()
    {
      clearDeviceStatusChanges();

      nameIdIndex.clear();
      currentState.clear();
    }

    private void clearDeviceStatusChanges()
    {
      for (Sensor sensor : sensors.values())
      {
        // Just wake up all the records, acturelly, the status didn't change.
        deviceStatusChanges.updateStatusChangedIDs(sensor.getSensorID());
      }

      deviceStatusChanges.clearAllRecords();

    }


    private boolean hasExistingState(int id)
    {
      return currentState.get(id) != null;
    }

    private Event getCurrentState(int id)
    {
      return currentState.get(id);
    }

    private Event get(String name) throws ResourceNotFoundException
    {
      if (!nameIdIndex.keySet().contains(name))
      {
        throw new ResourceNotFoundException(
            "No sensors with name ''{0}'' found.", name
        );
      }

      int id = nameIdIndex.get(name);

      return currentState.get(id);
    }


    private void update(Event event)
    {
      int id = event.getSourceID();

      if (currentState.get(id) == null)
      {
        currentState.put(id, event);
      }

      else
      {
        Event previousState = currentState.get(id);

        if (previousState.isEqual(event))
        {
          return;
        }

        currentState.put(id, event);
      }


      deviceStatusChanges.updateStatusChangedIDs(event.getSourceID());

      log.trace(
          "Marked Sensor ID = {0} (''{1}'') changed.", event.getSourceID(), event.getSource()
      );

    }

  }
}
