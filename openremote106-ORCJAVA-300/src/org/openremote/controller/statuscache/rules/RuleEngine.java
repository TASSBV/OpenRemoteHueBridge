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
package org.openremote.controller.statuscache.rules;

import java.net.URI;
import java.net.URISyntaxException;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collection;

import org.openremote.controller.utils.Logger;
import org.openremote.controller.Constants;
import org.openremote.controller.ControllerConfiguration;
import org.openremote.controller.service.ServiceContext;
import org.openremote.controller.exception.InitializationException;
import org.openremote.controller.protocol.Event;
import org.openremote.controller.statuscache.EventContext;
import org.openremote.controller.statuscache.EventProcessor;
import org.openremote.controller.statuscache.LifeCycleEvent;
import org.openremote.controller.statuscache.SwitchFacade;
import org.openremote.controller.statuscache.LevelFacade;
import org.openremote.controller.statuscache.RangeFacade;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.conf.AssertBehaviorOption;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.Globals;
import org.drools.runtime.rule.FactHandle;
import org.drools.definition.KnowledgePackage;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.DecisionTableInputType;
import org.drools.builder.ResourceType;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class RuleEngine extends EventProcessor
{

  // TODO : integrate with statuscache/deployer lifecycle




  // Enums ----------------------------------------------------------------------------------------

  public enum ResourceFileType
  {
    DROOLS_RULE_LANGUAGE(".drl"),

    CSV_DECISION_TABLE(".csv");


    // Enum Implementation ------------------------------------------------------------------------

    private String fileExtension;

    private ResourceFileType(String fileExtension)
    {
      this.fileExtension = fileExtension;
    }

    public String getFileExtension()
    {
      return fileExtension;
    }
  }



  // Class Members --------------------------------------------------------------------------------

  private final static Logger log = Logger.getLogger(
      Constants.RUNTIME_EVENTPROCESSOR_LOG_CATEGORY + ".drools"
  );

  private final static Logger initLog = Logger.getLogger(
      Constants.EVENT_PROCESSOR_INIT_LOG_CATEGORY
  );


  // Private Instance Fields ----------------------------------------------------------------------

  private KnowledgeBase kb;
  private StatefulKnowledgeSession knowledgeSession;
  private Map<Integer, FactHandle> eventSources = new HashMap<Integer, FactHandle>();

  private SwitchFacade switchFacade;
  private LevelFacade levelFacade;
  private RangeFacade rangeFacade;



  // Implements EventProcessor --------------------------------------------------------------------



  /**
   * TODO
   */
  @Override public void push(EventContext ctx)
  {
    // if we got no rules, just push event back to next processor...

    if (kb == null)
    {
      return;
    }


    // TODO : add listener for logging

    Event evt = ctx.getEvent();

    switchFacade.pushEventContext(ctx);
    switchFacade.pushLogger(log);
    levelFacade.pushEventContext(ctx);
    levelFacade.pushLogger(log);
    rangeFacade.pushEventContext(ctx);
    rangeFacade.pushLogger(log);

//    SwitchFacade switchFacade = new SwitchFacade();
//
//    knowledgeSession.setGlobal("switch", switchFacade);
//
//    knowledgeSession.setGlobal("event", eventFacade);

    try
    {
      if (!knowledgeSession.getObjects().contains(evt))
      {
        if (eventSources.keySet().contains(evt.getSourceID()))
        {
          knowledgeSession.retract(eventSources.get(evt.getSourceID()));

          eventSources.remove(evt.getSourceID());
        }

        FactHandle handle = knowledgeSession.insert(evt);

        eventSources.put(evt.getSourceID(), handle);
      }

      log.trace("Inserted event {0}", evt);
      log.trace("Fact count: " + knowledgeSession.getFactCount());
      
      knowledgeSession.fireAllRules();
    }

    catch (Throwable t)
    {
      log.error(
          "Error in executing rule : {0} -- Event {1} not processed!",
          t, t.getMessage(), ctx.getEvent()
      );

      if (t.getCause() != null)
      {
        log.error("Root Cause: \n", t.getCause());
      }
    }
  }


  /**
   * TODO
   *
   */
  @Override public void start(LifeCycleEvent event) throws InitializationException
  {

    ControllerConfiguration config = ServiceContext.getControllerConfiguration();

    URI resourceURI;

    try
    {
      resourceURI = new URI(config.getResourcePath());

      if (!resourceURI.isAbsolute())
      {
        resourceURI = new File(config.getResourcePath()).toURI();
      }
    }

    catch (URISyntaxException e)
    {
      throw new InitializationException(
          "Property 'resource.path' value ''{0}'' cannot be parsed. " +
          "It must contain a valid URI : {1}",
          e, config.getResourcePath(), e.getMessage()
      );
    }

    URI rulesURI = resourceURI.resolve("rules");

    if (!hasDirectoryReadAccess(rulesURI))
    {
      throw new InitializationException(
          "Directory ''{0}'' does not exist or cannot be read.", rulesURI
      );
    }

    Map<Resource, File> ruleDefinitions = getRuleDefinitions(rulesURI, ResourceFileType.DROOLS_RULE_LANGUAGE);
    Map<Resource, File> csvDecisionTables = getRuleDefinitions(rulesURI, ResourceFileType.CSV_DECISION_TABLE);

    if (ruleDefinitions.isEmpty() && csvDecisionTables.isEmpty())
    {
      initLog.info("No rule definitions found in ''{0}''.", new File(rulesURI).getAbsolutePath());

      return;
    }


    // Note, knowledgebuilder is not thread-safe...


    KnowledgeBaseConfiguration kbConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
    kbConfiguration.setOption(AssertBehaviorOption.EQUALITY);

    kb = KnowledgeBaseFactory.newKnowledgeBase(kbConfiguration);

    Set<KnowledgePackage> packages1 = getValidKnowledgePackages(ruleDefinitions, ResourceFileType.DROOLS_RULE_LANGUAGE);
    Set<KnowledgePackage> packages2 = getValidKnowledgePackages(csvDecisionTables, ResourceFileType.CSV_DECISION_TABLE);

    // TODO :
    //    Leaving XLS out for now -- not sure they're really necessary (you can always
    //    save as CSV) and don't want to introduce additional library dependencies (POI et al)
    //                                                                                          [JPL]


    kb.addKnowledgePackages(packages1);
    kb.addKnowledgePackages(packages2);


    knowledgeSession = kb.newStatefulKnowledgeSession();

    switchFacade = new SwitchFacade();
    rangeFacade = new RangeFacade();
    levelFacade = new LevelFacade();

    
    try
    {
      knowledgeSession.setGlobal("execute", event.getCommandFacade());
    }

    catch (Throwable t)
    {}

    try
    {
      knowledgeSession.setGlobal("switches", switchFacade);
    }

    catch (Throwable t)
    {}

    try
    {
      knowledgeSession.setGlobal("ranges", rangeFacade);
    }

    catch (Throwable t)
    {}

    try
    {
      knowledgeSession.setGlobal("levels", levelFacade);
    }

    catch (Throwable t)
    {}

    knowledgeSession.fireAllRules();
  }


  /**
   * TODO
   */
  @Override public void stop()
  {
    if (knowledgeSession != null)
    {
      knowledgeSession.dispose();
    }
    
    kb = null;
  }


  /**
   * Returns the name of this event processor.
   *
   * @return    rule engine name
   */
  @Override public String getName()
  {
    return "Drools Rule Engine";
  }


  // Private Instance Methods ---------------------------------------------------------------------


  /**
   * Checks that the rule directory exists and we can access it.
   *
   * @param uri   file URI pointing to the rule definition directory
   *
   * @return      true if we can read the dir, false otherwise
   *
   * @throws      InitializationException   if URI is null or security manager was installed
   *                                        but read access was not granted to directory pointed
   *                                        by the given file URI
   */
  private boolean hasDirectoryReadAccess(URI uri) throws InitializationException
  {

    if (uri == null)
    {
      throw new InitializationException("Rule resource directory was resolved to 'null'");
    }

    File dir = new File(uri);

    try
    {
      return dir.exists() && dir.canRead();
    }

    catch (SecurityException e)
    {
      throw new InitializationException(
          "Security Manager has denied read access to directory ''{0}''. " +
          "In order to deploy rule definitions, file read access must be explicitly " +
          "granted to this directory. ({1})",
          e, uri, e.getMessage()
      );
    }
  }


  /**
   * Loads the rule definitions of a given type from a pre-defined file URI.  <p>
   *
   * Note that if security manager is enabled, it must explicitly grant read access to the
   * directory referenced by the file URI.
   *
   * @param uri               File URI pointing to a <b>directory</b> that contains rule definitions
   * @param resourceFileType  The file type of the rule definitions to be loaded.
   *
   * @return  a map containing a resource handle (that can be used by Drools) as key, and the file
   *          reference to the physical file that was used to create the resource reference
   */
  private Map<Resource, File> getRuleDefinitions(URI uri, final ResourceFileType resourceFileType)
  {
    final File dir = new File(uri);

    File[] files;

    try
    {
      files = dir.listFiles(new FilenameFilter()
      {
        @Override public boolean accept(File path, String name)
        {
          return (path.equals(dir) && name.endsWith(resourceFileType.getFileExtension()));
        }
      });
    }

    catch (SecurityException e)
    {
      initLog.error(
          "Unable to list files in directory ''{0}'' due to security restrictions. " +
          "Security manager must grant read access to this directory. No rules were loaded. " +
          "(Exception: {1})", e, dir.getAbsolutePath(), e.getMessage()
      );

      return new HashMap<Resource, File>(0);
    }

    if (files == null)
    {
      initLog.error(
          "File location ''{0}'' is not a directory, or an I/O error occured trying to list " +
          "files in this directory. No rules were loaded.", dir.getAbsolutePath()
      );

      return new HashMap<Resource, File>(0);
    }

    Map<Resource, File> ruleDefinitions = new HashMap<Resource, File>();

    for (File file : files)
    {
      try
      {
        if (file.length() >0) {
          Resource resource = ResourceFactory.newFileResource(file);
          ruleDefinitions.put(resource, file);
          initLog.debug("Adding Rule ''{0}''...", file.getName());
        }
      }

      catch (Throwable t)
      {
        initLog.warn(
            "Unable to add rule definition ''{0}'' : {1}",
            t, file.getAbsoluteFile(), t.getMessage()
        );
      }
    }

    return ruleDefinitions;
  }


  /**
   * This is a bit of an ugly hack -- was in a hurry and couldn't find if there's a better
   * way in Drools API to handle this... Drools responds to errors in rule definitions by
   * throwing runtime exceptions. This is a bit of an issue when you are potentially deploying
   * multiple separate files, as it is not clear if the correct ones will be deployed or if
   * everything gets discarded if even one doesn't parse.  <p>
   *
   * So the ugly hack part is that we try to add rule definitions individually to temporary
   * knowledge bases. If they get parsed successfully then we keep them. Otherwise we don't
   * keep them on the list, just tell user about the error and continue to deploy the ones
   * that do work. For each rule definition we discard the temporary knowledge base and then
   * later deploy all successful ones in the real knowledge base that we'll use at runtime. <p>
   *
   * Doing this feels wrong so if there's a more natural way to accomplish this (deploying
   * different types of rule definitions -- DRL, CSV, etc -- and discarding the faulty ones
   * only rather than all of them) then this should be fixed.
   *
   * @param definitions     potential rule definition candidates -- these will be attempted to
   *                        add to temporary knowledge base just to see if their definitions
   *                        cause any exceptions to be thrown
   *
   * @param resourceType    Resource file type -- DRL or CSV file
   *
   * @return  set of knowledge packages that seemed to deploy correctly -- to be deployed in
   *          the actual knowledge base we use at runtime
   */
  private Set<KnowledgePackage> getValidKnowledgePackages(Map<Resource, File> definitions,
                                                          ResourceFileType resourceType)
  {
    Set<KnowledgePackage> packages = new HashSet<KnowledgePackage>();

    for (Resource resource : definitions.keySet())
    {
      KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();

      try
      {
        if (resourceType == ResourceFileType.CSV_DECISION_TABLE)
        {
          DecisionTableConfiguration conf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
          conf.setInputType(DecisionTableInputType.CSV);

          builder.add(resource, ResourceType.DTABLE, conf);
        }

        else if (resourceType == ResourceFileType.DROOLS_RULE_LANGUAGE)
        {
          builder.add(resource, ResourceType.DRL);
        }

        else
        {
          initLog.warn("Unrecognized rule definition file format : {0}", resourceType);
        }

        if (builder.hasErrors())
        {
          Collection<KnowledgeBuilderError> errors = builder.getErrors();

          initLog.error(
              "Rule definition ''{0}'' could not be deployed. See errors below.",
              definitions.get(resource).getName()
          );

          for (KnowledgeBuilderError error : errors)
          {
            initLog.error(error.getMessage());
          }
        }
      }

      catch (Throwable t)
      {
        initLog.error(
            "Error in rule definition ''{0}'' : {1}",
            t, definitions.get(resource).getName(), t.getMessage()
        );
      }

      try
      {
        KnowledgeBase kb = builder.newKnowledgeBase();

        kb.addKnowledgePackages(builder.getKnowledgePackages());

        packages.addAll(builder.getKnowledgePackages());

        initLog.debug("Adding rule definitions from ''{0}''...", definitions.get(resource).getName());
      }

      catch (IllegalArgumentException e)
      {
        initLog.error(
            "There was an error parsing the rule definition ''{0}'' : {1}",
            e, definitions.get(resource).getName(), e.getMessage()
        );
      }
    }

    return packages;
  }

}

