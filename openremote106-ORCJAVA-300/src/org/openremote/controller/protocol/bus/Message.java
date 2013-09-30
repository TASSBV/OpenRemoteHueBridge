package org.openremote.controller.protocol.bus;

/**
 * A message to send or receive over a physical bus.
 */
public class Message {

   private byte[] content;

   /**
    * Constructor.
    * 
    * @param content
    *           Message content.
    */
   public Message(byte[] content) {
      this.content = content;
   }

   /**
    * Get message content.
    * 
    * @return An array of byte representing message content.
    */
   public byte[] getContent() {
      return this.content;
   }
}
