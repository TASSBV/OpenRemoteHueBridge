package org.openremote.controller.protocol.bus;

import java.io.IOException;

import org.openremote.controller.protocol.knx.ip.KnxIpException;

/**
 * The abstraction of a physical bus.
 * <p>
 * This interface describes the entry points of all the physical buses OpenRemote controller uses.
 * </p>
 */
public interface PhysicalBus {
   /**
    * Start the physical bus.
    * 
    * @param srcAddr
    * @return An object that may be used by the caller.
    * @throws KnxIpException
    * @throws IOException
    * @throws InterruptedException
    */
   void start(Object inChannel, Object outChannel);

   /**
    * Stop the physical bus.
    * 
    * @throws InterruptedException
    */
   void stop();

   /**
    * Send a message to device(s) through the physical bus.
    * 
    * @param message
    *           The message to send.
    * @throws IOException
    */
   void send(Message message) throws IOException;

   /**
    * Receive a message from the physical bus. This method blocks until a message is received.
    * 
    * @return Expected message.
    * @throws IOException
    *            Message could not be received.
    */
   Message receive() throws IOException;
}
