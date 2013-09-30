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

import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import org.openremote.controller.utils.Logger;
import org.openremote.controller.Constants;
import org.openremote.controller.model.Command;


/**
 * Event processor chain is a container of {@link EventProcessor event processors} for the
 * {@link org.openremote.controller.statuscache.StatusCache device state cache} which
 * can push incoming events from the sensors (and potentially other sources) through a processing
 * chain before they get added to the in-memory cache state.  <p>
 *
 * Event processors can modify event properties, discard events, or create new events as part
 * of the processing. Events are pushed into each processor in a order determined by the processor
 * configuration. The event which will be stored in the in-memory state cache is the event instance
 * returned by the last processor in the stack. <p>
 *
 * Typical use of event processors is scripting of incoming events, executing rules, logging, etc.
 *
 * @see org.openremote.controller.statuscache.EventProcessor
 * @see org.openremote.controller.statuscache.StatusCache
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class EventProcessorChain
{

  // Class Members --------------------------------------------------------------------------------

  /**
   * <b>Runtime</b> log category for event processing.
   */
  private final static Logger log = Logger.getLogger(Constants.RUNTIME_STATECACHE_LOG_CATEGORY);


  // Private Instance Fields ----------------------------------------------------------------------

  /**
   * Contains the ordered list of configured event processors for this chain.
   */
  private List<EventProcessor> processors = new ArrayList<EventProcessor>(5);

//  /**
//   * Flag for lazy initialization.
//   */
//  private boolean hasInit = false;
//
//  private InitializationContext eventProcessorInitContext;
    private CommandFacade commandFacade;



  // Service Dependencies -------------------------------------------------------------------------


  /**
   * Configures this event processor chain with a given, ordered, list of event processors.
   *
   * @param processors    event processor list -- the order of the list is significant
   */
  public void setEventProcessors(List<EventProcessor> processors)
  {
    this.processors = processors;
  }


  public void createCommandFacade(Set<Command> commands)
  {
    this.commandFacade = new CommandFacade(commands);
  }


  public void start()
  {
    // Log for initialization -- separate from runtime logging

    Logger initLog = Logger.getLogger(Constants.EVENT_PROCESSOR_INIT_LOG_CATEGORY);

    LifeCycleEvent ctx = new LifeCycleEvent(commandFacade);

    for (EventProcessor ep : processors)
    {
      try
      {
        initLog.debug("Initializing event processor: {0}", ep.getName());

        ep.start(ctx);

        initLog.info("Initialized event processor : {0}", ep.getName());
      }

      catch (Throwable t)
      {
        initLog.error(
            "Cannot start event processor ''{0}'' : {1}",
            t, ep.getName(), t.getMessage()
        );
      }
    }
  }

  public void stop()
  {
    // Log for initialization -- separate from runtime logging

    Logger initLog = Logger.getLogger(Constants.EVENT_PROCESSOR_INIT_LOG_CATEGORY);

    for (EventProcessor ep : processors)
    {
      try
      {
        initLog.debug("Stopping event processor: {0}", ep.getName());

        ep.stop();

        initLog.info("Stopped event processor : {0}", ep.getName());
      }

      catch (Throwable t)
      {
        initLog.error(
            "Cannot stop event processor ''{0}'' : {1}",
            t, ep.getName(), t.getMessage()
        );
      }
    }
  }
    



  // Protected Instance Methods -------------------------------------------------------------------

  /**
   * Pushes an event through a stack of event processors. The returned event is the result of
   * modifications of all configured event processors (specifically as returned by the last
   * event processor in the stack).
   *
   * @param event   event to process
   *
   * @return  event to store in cache -- this is the event returned by the last event processor
   *          in the stack.
   */
  protected void push(EventContext ctx)
  {
//    // This was a lazy initialization of processors due to some earlier bugs that have been
//    // resolved now. Can eventually be placed into constructor or other more appropriate
//    // initialization sequence -- although lazy init is not bad in itself
//    //                                                                          [JPL]
//
//    if (!hasInit)
//    {
//      initProcessors(ctx);
//
//      hasInit = true;
//    }


    for (EventProcessor processor : processors)
    {
      log.trace("Processing {0}...", ctx.getEvent());

      processor.push(ctx);

      if (ctx.hasTerminated())
      {
 System.out.println("\n\n===== STOPPED EVENT CHAIN AT " + processor);       

        return;
      }

      else
      {
        log.trace("Event {0} processed by ''{1}''...", ctx.getEvent(), processor.getName());
      }
    }

    log.trace("Processing of {0} complete...", ctx.getEvent());
  }


  protected CommandFacade getCommandFacade()
  {
    return commandFacade;
  }


  // Private Methods ------------------------------------------------------------------------------

  /**
   * Initializes each event processor in the stack by calling its
   * {@link EventProcessor#start()} method.
   */
  private void initProcessors(EventContext ctx)
  {
//    // Log for initialization -- separate from runtime logging
//
//    Logger initLog = Logger.getLogger(Constants.EVENT_PROCESSOR_INIT_LOG_CATEGORY);
//
//    for (EventProcessor ep : processors)
//    {
//      try
//      {
//        initLog.debug("Initializing event processor: {0}", ep.getName());
//
//        ep.start(ctx);
//
//        initLog.info("Initialized event processor : {0}", ep.getName());
//      }
//
//      catch (Throwable t)
//      {
//        initLog.error(
//            "Cannot start event processor ''{0}'' : {1}",
//            t, ep.getName(), t.getMessage()
//        );
//      }
//    }
  }





}

