package org.openremote.beehive.rest.service;

import javax.ws.rs.Path;

import org.openremote.beehive.SpringTestContext;
import org.openremote.beehive.rest.LIRCConfigFileRESTService;
import org.openremote.beehive.spring.ISpringContext;

@Path("/lirc.conf")
public class LircdConfRESTTestService extends LIRCConfigFileRESTService {

   @Override
   protected Class<? extends ISpringContext> getSpringContextClass() {
      return SpringTestContext.class;
   }
}
