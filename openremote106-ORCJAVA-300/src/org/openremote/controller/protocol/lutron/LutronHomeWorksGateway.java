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
package org.openremote.controller.protocol.lutron;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketException;
import java.util.HashMap;

import org.apache.commons.net.telnet.TelnetClient;
import org.openremote.controller.LutronHomeWorksConfig;
import org.openremote.controller.protocol.MessageQueueWithPriorityAndTTL;
import org.openremote.controller.protocol.MessageQueueWithPriorityAndTTL.Coalescable;
import org.openremote.controller.protocol.lutron.model.Dimmer;
import org.openremote.controller.protocol.lutron.model.GrafikEye;
import org.openremote.controller.protocol.lutron.model.HomeWorksDevice;
import org.openremote.controller.protocol.lutron.model.Keypad;
import org.openremote.controller.utils.Logger;

/**
 * 
 * @author <a href="mailto:eric@openremote.org">Eric Bariaux</a>
 *
 */
public class LutronHomeWorksGateway /*implements ApplicationListener */{

  // TODO:
  // what on failed logout also ?
  // have a logout command on system down
  // security and vacation mode commands

  /*
   * State machine describing the connection / string parsing
   * not logged in -> send message -> log in
   * receive log in confirm -> logged in, can send messages
   * receive invalid log in -> close connection, so will retry as if connection not possible (stays not logged in, clear the queue, stops trying, refuse any messages)
   * socket closed -> not logged in, retry process queue
  */

  // Class Members --------------------------------------------------------------------------------

  private static final int TELNET_TIMOUT = 10000;
  private static final int COMMUNICATION_ERROR_RETRY_DELAY = 15000;
  private static final int INVALID_LOGIN_RETRY_DELAY = 60000;

  /**
   * Lutron HomeWorks logger. Uses a common category for all Lutron related
   * logging.
   */
  private final static Logger log = Logger.getLogger(LutronHomeWorksCommandBuilder.LUTRON_LOG_CATEGORY);

  private static HashMap<LutronHomeWorksAddress, HomeWorksDevice> deviceCache = new HashMap<LutronHomeWorksAddress, HomeWorksDevice>();

  // Don't ask this to the config factory when instantiating the bean, this
  // results in infinite recursion
  private LutronHomeWorksConfig lutronConfig;

  private MessageQueueWithPriorityAndTTL<LutronCommand> queue = new MessageQueueWithPriorityAndTTL<LutronCommand>();

  private LoginState loginState = new LoginState();

	private LutronHomeWorksConnectionThread connectionThread;
	
  public synchronized void startGateway() {
      if (lutronConfig == null) {
          lutronConfig = LutronHomeWorksConfig.readXML();
      // Check config, report error if any -> TODO: auto discovery ?
      log.info("Got Lutron config");
      log.info("Address >" + lutronConfig.getAddress() + "<");
      log.info("Port >" + lutronConfig.getPort() + "<");
      log.info("UserName >" + lutronConfig.getUserName() + "<");
      log.info("Password >" + lutronConfig.getPassword() + "<");
    }

    if (connectionThread == null) {
      // Starts some thread that has the responsibility to establish connection and keep it alive
      connectionThread = new LutronHomeWorksConnectionThread();
      connectionThread.start();
    }
  }

  public void sendCommand(String command, LutronHomeWorksAddress address, String parameter) {
    log.info("Asked to send command " + command);

    // Ask to start gateway, if it's already done, this will do nothing
    startGateway();
    queue.add(new LutronCommand(command, address, parameter));
  }

  /**
   * Gets the HomeWorks device from the cache, creating it if not already
   * present.
   * 
   * @param address
   * @return
   * @return
   * @throws LutronHomeWorksDeviceException 
   */
  public HomeWorksDevice getHomeWorksDevice(LutronHomeWorksAddress address, Class<? extends HomeWorksDevice> deviceClass) throws LutronHomeWorksDeviceException {
    HomeWorksDevice device = deviceCache.get(address);
    if (device == null) {
      // No device yet in the cache, try to create one
      try {
        Constructor<? extends HomeWorksDevice> constructor = deviceClass.getConstructor(LutronHomeWorksGateway.class, LutronHomeWorksAddress.class);
        device = constructor.newInstance(this, address);
      } catch (SecurityException e) {
        throw new LutronHomeWorksDeviceException("Impossible to create device instance", address, deviceClass, e);
      } catch (NoSuchMethodException e) {
        throw new LutronHomeWorksDeviceException("Impossible to create device instance", address, deviceClass, e);
      } catch (IllegalArgumentException e) {
        throw new LutronHomeWorksDeviceException("Impossible to create device instance", address, deviceClass, e);
      } catch (InstantiationException e) {
        throw new LutronHomeWorksDeviceException("Impossible to create device instance", address, deviceClass, e);
      } catch (IllegalAccessException e) {
        throw new LutronHomeWorksDeviceException("Impossible to create device instance", address, deviceClass, e);
      } catch (InvocationTargetException e) {
        throw new LutronHomeWorksDeviceException("Impossible to create device instance", address, deviceClass, e);
      }
    }
    if (!(deviceClass.isInstance(device))) {
      throw new LutronHomeWorksDeviceException("Invalid device type found at given address", address, deviceClass, null);
    }
    deviceCache.put(address, device);
    return device;
  }

  /*
  @Override
  public void onApplicationEvent(ApplicationEvent applicationEvent) {
  	if (applicationEvent instanceof ContextRefreshedEvent) {
  		if ("Root WebApplicationContext".equals(((ContextRefreshedEvent) applicationEvent).getApplicationContext().getDisplayName())) {
  			startGateway();
  		}
  	}
  }
  */

  // ---

  private class LutronHomeWorksConnectionThread extends Thread {

   private LutronHomeWorksReaderThread readerThread;
   private LutronHomeWorksWriterThread writerThread;

    @Override
    public void run() {
      TelnetClient tc = new TelnetClient();
      tc.setConnectTimeout(TELNET_TIMOUT);
      while (!isInterrupted()) {
        try {
          log.info("Trying to connect to " + lutronConfig.getAddress() + " on port " + lutronConfig.getPort());
          tc.connect(lutronConfig.getAddress(), lutronConfig.getPort());
          log.info("Telnet client connected");
          readerThread = new LutronHomeWorksReaderThread(tc.getInputStream());
          readerThread.start();
          log.info("Reader thread asked to start");
          writerThread = new LutronHomeWorksWriterThread(tc.getOutputStream());
          writerThread.start();
          log.info("Writer thread asked to start");
          // Wait for the read thread to die, this would indicate the connection was dropped
          while (readerThread != null) {
            readerThread.join(1000);
            if (!readerThread.isAlive()) {
              log.info("Reader thread is dead, clean and re-try to connect");
              tc.disconnect();
              readerThread = null;
              writerThread.interrupt();
              writerThread = null;
            }
          }
        } catch (SocketException e) {
           log.error("Connection to Lutron impossible, sleeping and re-trying later", e);
          // We could not connect, sleep for a while before trying again
          try {
            Thread.sleep(COMMUNICATION_ERROR_RETRY_DELAY);
          } catch (InterruptedException e1) {
             log.warn("Interrupted during our sleep", e1);
          }
        } catch (IOException e) {
          log.error("Connection to Lutron impossible, sleeping and re-trying later", e);
          // We could not connect, sleep for a while before trying again
          try {
            Thread.sleep(COMMUNICATION_ERROR_RETRY_DELAY);
          } catch (InterruptedException e1) {
             log.warn("Interrupted during our sleep", e1);
          }
        } catch (InterruptedException e) {
           log.warn("Interrupted during our sleep", e);
        }
      }
      // For now do not support discovery, use information defined in config
    }

  }

  // ---

  private class LutronHomeWorksWriterThread extends Thread {

    private OutputStream os;

    public LutronHomeWorksWriterThread(OutputStream os) {
      super();
      this.os = os;
    }

    @Override
    public void run() {

      log.info("Writer thread starting");
      // Directly send login/password to Lutron. If not sending anything, Lutron sends nothing and this just sits waiting forever.
      // Sending empty CR/LF causes invalid login, so we kill are threads and start over again -> infinite loop
      PrintWriter pr = new PrintWriter(new OutputStreamWriter(os));
      pr.println(lutronConfig.getUserName() + "," + lutronConfig.getPassword());
      pr.flush();

      while (!isInterrupted()) {
        synchronized (loginState) {
          while (!loginState.loggedIn) {
            try {
              while (!loginState.needsLogin && !loginState.loggedIn) {
                log.info("Not logged in, waiting to be woken up");
                // We're not logged in, wait until the reader thread ask to login and confirms we're logged in
                loginState.wait();
                log.info("Woken up on loggedIn, loggedIn: " + loginState.loggedIn + " - needsLogin: " + loginState.needsLogin);
              }
              if (!loginState.loggedIn) {
                log.info("Sending login info: " + lutronConfig.getUserName() + "," + lutronConfig.getPassword());
                // We've been awakened and we're not yet logged in. It means we need to send login info
                pr.println(lutronConfig.getUserName() + "," + lutronConfig.getPassword());
                pr.flush();
                loginState.needsLogin = false;
                log.info("Sent log in info");
              }
              // We've been awakened and we're logged in, we'll just go out of the loop and proceed with normal execution
            } catch (InterruptedException e) {
              // We'll loop and test again for login
            }
          }
        }
        LutronCommand cmd = null;
        try {
          cmd = queue.blockingPoll();
        } catch (InterruptedException e) {
          break;
        }
        if (cmd != null) {
          log.info("Sending >" + cmd.toString() + "< on print writer " + pr);
          pr.println(cmd.toString() + "\n");
          pr.flush();
        }
      }
    }
  }

  // ---

  private class LutronHomeWorksReaderThread extends Thread {

   private InputStream is;

    public LutronHomeWorksReaderThread(InputStream is) {
      super();
      this.is = is;
    }

    @Override
    public void run() {
      log.info("Reader thread starting");
      log.info("TC input stream " + is);
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      log.info("Buffered reader " + br);

      String line = null;
      try {
        line = br.readLine();
      } catch (IOException e1) {
         log.warn("Could not read from Lutron", e1);
      }
      do {
        try {
          log.info("Reader thread got line >" + line + "<");
          if (line.startsWith("LOGIN: login successful")) {
            synchronized (loginState) {
              loginState.loggedIn = true;
              loginState.invalidLogin = false;
              // We're logged in, notify writer thread to start sending messages
              loginState.notify();
            }
            // Configure Lutron "protocol" as desired
            queue.priorityAdd(new LutronCommand("PROMPTOFF", null, null));
            queue.priorityAdd(new LutronCommand("DLMON", null, null));
            queue.priorityAdd(new LutronCommand("GSMON", null, null));
            queue.priorityAdd(new LutronCommand("KLMON", null, null));
          } else if (line.startsWith("LOGIN:")) {
            log.info("Asked to login, wakening writer thread");
            synchronized (loginState) {
              // If we thought we were already logged in, reset that
              loginState.loggedIn = false;

              loginState.needsLogin = true;
              // System asks for login, notify writer thread to send it
              loginState.notify();
            }
          } else if (line.startsWith("login incorrect")) {
            synchronized (loginState) {
              loginState.loggedIn = false;
              loginState.invalidLogin = true;
            }
            // Wait a moment and get out of our read loop, this will terminate the thread, close the connection and re-try
            try {
               Thread.sleep(INVALID_LOGIN_RETRY_DELAY);
             } catch (InterruptedException e1) {
                log.warn("Interrupted during our sleep", e1);
             }
             break;
          } else if (line.startsWith("closing connection")) {
            synchronized (loginState) {
              loginState.loggedIn = false;
            }
            // Get out of our read loop, this will terminate the thread
            break;
          } else {
            // Try parsing the line as a feedback / response from the system
            LutronResponse response = parseResponse(line);
            if (response != null) {
              if ("GSS".equals(response.response)) {
                try {
                  // GrafikEye scene feedback: GSS, [01:05:01], 1
                  GrafikEye ge = (GrafikEye) getHomeWorksDevice(response.address, GrafikEye.class);
                  if (ge != null) {
                    ge.processUpdate(response.parameter);
                  }
                } catch (LutronHomeWorksDeviceException e) {
                  log.error("Impossible to get device", e);
                }
              } else if ("KLS".equals(response.response)) {
                try {
                  // Keypad LED feedback: KLS, [01:06:01], 110000001000010000000000
                  Keypad keypad = (Keypad) getHomeWorksDevice(response.address, Keypad.class);
                  if (keypad != null) {
                    keypad.processUpdate(response.parameter);
                  }
                } catch (LutronHomeWorksDeviceException e) {
                  log.error("Impossible to get device", e);
                }
              } else if ("DL".equals(response.response)) {
                try {
                  Dimmer dim = (Dimmer) getHomeWorksDevice(response.address, Dimmer.class);
                  if (dim != null) {
                    dim.processUpdate(response.parameter);
                  }
                } catch (LutronHomeWorksDeviceException e) {
                  log.error("Impossible to get device", e);
                }
              }
            } else {
               log.info("Received unknown information from Lutron >" + line + "<");
            }
          }
          line = br.readLine();
        } catch (IOException e) {
           log.warn("Could not read from Lutron", e);
        }
      } while (line != null && !isInterrupted());
    }

  }

  private LutronResponse parseResponse(String responseText) {
    LutronResponse response = null;

    String[] parts = responseText.split(",");
    // All the responses we currently understand have 3 components
    if (parts.length == 3) {
      try {
        response = new LutronResponse();
        response.response = parts[0].trim();
        response.address = new LutronHomeWorksAddress(parts[1].trim());
        response.parameter = parts[2].trim();
        log.info("Response is (" + response.response + "," + response.address + "," + response.parameter + ")");
      } catch (InvalidLutronHomeWorksAddressException e) {
        // Invalid address, consider we got invalid response from Lutron
        response = null;
      }
    }
    return response;
  }

  public class LutronCommand implements Coalescable {

    private String command;
    private LutronHomeWorksAddress address;
    private String parameter;

    public LutronCommand(String command, LutronHomeWorksAddress address, String parameter) {
      this.command = command;
      this.address = address;
      this.parameter = parameter;
    }

    public String toString() {
      StringBuffer buf = new StringBuffer(command);
      if (address != null) {
        buf.append(", ");
        buf.append(address);
      }
      if (parameter != null) {
        buf.append(", ");
        buf.append(parameter);
      }
      return buf.toString();
    }
    
    @Override
    public boolean isCoalesable(Coalescable other) {
      if (!(other instanceof LutronCommand)) {
        return false;
      }
      LutronCommand otherCommand = (LutronCommand)other;
      return (otherCommand.command.equals(this.command) && (otherCommand.address.equals(this.address)));
    }

  }

  private class LutronResponse {

    public String response;
    public LutronHomeWorksAddress address;
    public String parameter;

  }

  private class LoginState {

    /**
     * Indicates that we must send the login information.
     */
    public boolean needsLogin;

    /**
     * Indicates if we're logged into the system, if not commands must be queued.
     */
    public boolean loggedIn;

    /**
     * Indicates if we tried logging in and been refused the login, if so do not try again.
     * TODO: there must be a way to reset this.
     */
    public boolean invalidLogin;
  }

}
