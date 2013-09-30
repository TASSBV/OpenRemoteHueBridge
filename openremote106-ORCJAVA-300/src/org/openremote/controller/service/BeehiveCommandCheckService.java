package org.openremote.controller.service;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import org.drools.lang.DRLParser.calendars_key_return;
import org.openremote.controller.Constants;
import org.openremote.controller.ControllerConfiguration;
import org.openremote.controller.proxy.ControllerProxy;
import org.openremote.controller.utils.Logger;
import org.openremote.controllercommand.domain.ControllerCommandDTO;
import org.openremote.rest.GenericResourceResultWithErrorMessage;
import org.openremote.useraccount.domain.ControllerDTO;
import org.openremote.useraccount.domain.UserDTO;
import org.restlet.data.ChallengeScheme;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import flexjson.JSONDeserializer;

public class BeehiveCommandCheckService {

   /**
    * Common log category for startup logging, with a specific sub-category for the BeehiveCommandCheckService
    */
   private final static Logger log = Logger.getLogger(Constants.BEEHIVE_COMMAND_CHECKER_LOG_CATEGORY);

   
   private ControllerConfiguration controllerConfig;
   private ControllerDTO controllerDTO;
   private BeehiveCommandChecker commandCheckerThread;


   private Deployer deployer;

   public BeehiveCommandCheckService(ControllerConfiguration controllerConfig) {
      this.controllerConfig = controllerConfig;
      this.commandCheckerThread = new BeehiveCommandChecker();
   }

   public void setDeployer(Deployer deployer) {
      this.deployer = deployer;
   }
   
   public void start(ControllerDTO controllerDTO) {
      this.controllerDTO = controllerDTO;
      commandCheckerThread.setRunning(true);
      commandCheckerThread.start();
   }
   
   public void stop() {
      commandCheckerThread.setRunning(false);
   }
   
   
   protected void initiateProxy(ControllerCommandDTO command)  {
      Long id = command.getOid();
      String url = command.getCommandParameter().get("url");
      String token = command.getCommandParameter().get("token");
      
      SocketChannel beehiveSocket = null;
      boolean needsAck = true;
      try {
         log.info("Connecting to beehive at "+url+" for proxy");
         beehiveSocket = ControllerProxy.makeClientSocket(url, token, controllerConfig.getProxyTimeout());
         // at this point the command should already have been marked as ack by the listening end at beehive
         log.info("Connected to beehive");
         needsAck = false;
         // try to connect to it, see if it's still valid
         String ip = controllerConfig.getWebappIp();
         int port = controllerConfig.getWebappPort();
         if (ip==null || ip.trim().length()==0) {
            ip = "localhost";
         }
         if (port == 0) {
            port = 8080;
         }
         ControllerProxy proxy = new ControllerProxy(beehiveSocket, ip, port, controllerConfig.getProxyTimeout());
         log.info("Starting proxy");
         proxy.start();
      } catch (IOException e) {
         log.info("Got exception while connecting to beehive", e);
         if(beehiveSocket != null){
            try {
               beehiveSocket.close();
            } catch (IOException e1) {
               // ignore
            }
         }
         // the server should have closed it, but let's help him to make sure
         if(needsAck)
            ackCommand(id);
      }
   }

   private void ackCommand(Long id)  {
      log.info("Acking command "+id);
      ClientResource cr = null;
      try {
         cr = new ClientResource( controllerConfig.getBeehiveControllerCommandServiceRESTRootUrl() + "command/" + id);
         UserDTO user = controllerDTO.getAccount().getUsers().get(0);
         cr.setChallengeResponse(ChallengeScheme.HTTP_BASIC, user.getUsername(), user.getPassword());
         Representation r = cr.delete();
         String str;
         str = r.getText();
         GenericResourceResultWithErrorMessage res = new JSONDeserializer<GenericResourceResultWithErrorMessage>().use(null, GenericResourceResultWithErrorMessage.class).use("result", String.class).deserialize(str);
         if (res.getErrorMessage() != null) {
            throw new RuntimeException(res.getErrorMessage());
         }
      } catch (Exception e) {
         log.error("!!! Unable to ACK controller command with id: " + id, e);
      } finally {
         if (cr != null) {
            cr.release();
         }
      }
   }
   
   
   private void unlinkController(ControllerCommandDTO controllerCommandDTO) {
      this.stop();
      this.controllerDTO = null;
      this.deployer.unlinkController();
   }   
   
   private void executeCommand(ControllerCommandDTO controllerCommandDTO) {
      switch (controllerCommandDTO.getCommandTypeEnum()) {
         case INITIATE_PROXY:
            initiateProxy(controllerCommandDTO);
            break;
         case UNLINK_CONTROLLER:
            unlinkController(controllerCommandDTO);
            break;
         default:
            log.error("ControllerCommand not implemented yet: " + controllerCommandDTO.getCommandType());
      }
      
   }
   
   private class BeehiveCommandChecker extends Thread {
      
      private boolean running;
      
      BeehiveCommandChecker() {
         super("BeehiveCommandChecker");
      }

      @SuppressWarnings("unchecked")
      public void run() {
         //As long as we are not linked to an account we periodically try to receive account info
         int sleepTime = controllerConfig.getBeehiveCommandServiceCheckInterval();
         while (isRunning()) {
            ClientResource cr = null;
            try {
               cr = new ClientResource( controllerConfig.getBeehiveControllerCommandServiceRESTRootUrl() + "commands/" + controllerDTO.getOid());
               UserDTO user = controllerDTO.getAccount().getUsers().get(0);
               cr.setChallengeResponse(ChallengeScheme.HTTP_BASIC, user.getUsername(), user.getPassword());
               Representation r = cr.get();
               String str;
               str = r.getText();
               GenericResourceResultWithErrorMessage res = new JSONDeserializer<GenericResourceResultWithErrorMessage>().use(null, GenericResourceResultWithErrorMessage.class).use("result", ArrayList.class).use("result.values", ControllerCommandDTO.class).deserialize(str);
               if (res.getErrorMessage() != null) {
                  throw new RuntimeException(res.getErrorMessage());
               }
               List<ControllerCommandDTO> commands = (ArrayList<ControllerCommandDTO>)res.getResult();
               if (!commands.isEmpty()) {
                  executeCommand(commands.get(0));
               }
            } catch (Exception e) {
               log.error("!!! Unable to check for new controller command from Beehive", e);
            } finally {
               if (cr != null) {
                  cr.release();
               }
            }
            try { Thread.sleep(sleepTime); } catch (InterruptedException e) {} //Let's wait 30 seconds
         }
      }

      public boolean isRunning() {
         return running;
      }

      public void setRunning(boolean running) {
         this.running = running;
      }
      
      
    }


}
