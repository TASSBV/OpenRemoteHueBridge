package org.openremote.controller.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.openremote.controller.Constants;

public abstract class Proxy extends Thread {

   private static Logger logger = Logger.getLogger(Constants.PROXY_LOG_CATEGORY);
   protected SocketChannel srcSocket;
   protected boolean halted;
   protected Selector selector;
   protected ByteBuffer srcBuffer;
   private int timeout;

   public Proxy(SocketChannel clientSocket, int timeout) throws IOException {
      this.srcSocket = clientSocket;
      srcSocket.configureBlocking(false);
      selector = Selector.open();
      this.timeout = timeout;
      // this one comes from src and goes to dst
      srcBuffer = ByteBuffer.allocate(4096);
   }

   @Override
   public void run() {
      try{
         logger.info("Client running");
         // we first need to connect to the endpoint
         SocketChannel dstSocket;
         try {
            dstSocket = openDestinationSocket();
         } catch (IOException e) {
            logger.error("Failed to connect to the destination", e);
            return;
         }
         try {
            logger.info("We got connection to the destination");
            dstSocket.configureBlocking(false);
            // did we already read something for dst?
            if(srcBuffer.position() > 0){
               srcBuffer.flip();
               dstSocket.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
               // we're not reading anymore from srcSocket until we've written it to dstSocket
               srcSocket.register(selector, 0);
            }else{
               // initially we only read from both sides
               dstSocket.register(selector, SelectionKey.OP_READ);
               srcSocket.register(selector, SelectionKey.OP_READ);
            }
            // make a second buffer
            // this one comes from dst and goes to src
            ByteBuffer dstBuffer = ByteBuffer.allocate(4096);
            // and loop until we're done
            while(hasValidKeys(selector)){
               logger.info("Selecting");
               if(selector.select(timeout) == 0){
                  logger.info("Timed out or halted");
                  break;
               }
               if(halted){
                  logger.info("Halted");
                  break;
               }
               logger.info("Select done");
               Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
               while(keys.hasNext()){
                  SelectionKey key = keys.next();
                  keys.remove();
                  if(!key.isValid())
                     continue;
                  // first deal with operations on the src socket
                  if(key.channel() == srcSocket){
                     // are we reading from it for dst?
                     if(key.isReadable()){
                        read(srcSocket, srcBuffer, selector, key, dstSocket);
                        // or are we writing to it from dst?
                     }else if(key.isWritable()) {
                        write(srcSocket, dstBuffer, selector, key, dstSocket);
                     }
                     // then deal with operations on the dst socket
                  }else if(key.channel() == dstSocket) {
                     // are we reading from it for src?
                     if(key.isReadable()){
                        read(dstSocket, dstBuffer, selector, key, srcSocket);
                        // or are we writing on it from src?
                     }else if(key.isWritable()) {
                        write(dstSocket, srcBuffer, selector, key, srcSocket);
                     }
                  }
                  // remove empty keys
                  if(key.interestOps() == 0){
                     logger.info("No more ops for "+key.channel());
                     key.cancel();
                  }
               }
            }
            logger.info("Done with proxying");
         } catch (Exception e) {
            logger.error("Proxy dead", e);
         }finally{
            // close the dst socket
            try {
               dstSocket.close();
            } catch (IOException x) {
            }
         }
      }finally{
         // close the src socket
         try {
            srcSocket.close();
         } catch (IOException x) {
         }
         onProxyExit();
      }
   }

   protected void onProxyExit() {}

   protected abstract SocketChannel openDestinationSocket() throws IOException;

   private boolean hasValidKeys(Selector selector) {
      for(SelectionKey key : selector.keys()){
         if(key.isValid() && key.interestOps() != 0)
            return true;
      }
      return false;
   }

   private void write(SocketChannel dstSocket, ByteBuffer buffer,
         Selector selector, SelectionKey dstKey, SocketChannel srcSocket) throws IOException {
      logger.info("Writing to "+dstSocket);
      dstSocket.write(buffer);
      // did we write everything?
      if(!buffer.hasRemaining()){
         logger.info("Wrote everything, now start reading, input shutdown: "+srcSocket.socket().isInputShutdown()
               +", buffer open: "+srcSocket.isOpen()
               +", socket closed: "+srcSocket.socket().isClosed());
         // stop writing to src and start reading from dst
         buffer.clear();
         // reset they WRITE registration on dstSocket
         dstKey.interestOps(dstKey.interestOps() ^ SelectionKey.OP_WRITE);
         // add the READ registration on srcSocket
         SelectionKey srcKey = srcSocket.keyFor(selector);
         if(srcKey != null)
            srcKey.interestOps(srcKey.interestOps() | SelectionKey.OP_READ);
         else
            srcSocket.register(selector, SelectionKey.OP_READ);
      }else{
         // else keep writing
         logger.info("Wrote some, but "+buffer.remaining()+" left to write, keep on writing");
      }
   }

   private void read(SocketChannel srcSocket, ByteBuffer buffer,
         Selector selector, SelectionKey srcKey, SocketChannel dstSocket) throws IOException {
      logger.info("Reading from "+srcSocket);
      int read = srcSocket.read(buffer);
      if(read == -1){
         logger.info("Read EOF, stop reading");
         // stop reading from this socket
         stopReadingFromSocket(srcSocket, srcKey, dstSocket);
         return;
      }
      // did we read anything?
      if(read > 0){
         logger.info("Read "+read+" bytes, start writing");
         // then stop reading from src and start writing to dst
         buffer.flip();
         // reset the READ registration on srcSocket
         srcKey.interestOps(srcKey.interestOps() ^ SelectionKey.OP_READ);
         // add the WRITE registration on dstSocket
         SelectionKey dstKey = dstSocket.keyFor(selector);
         if(dstKey != null)
            dstKey.interestOps(dstKey.interestOps() | SelectionKey.OP_WRITE);
         else
            dstSocket.register(selector, SelectionKey.OP_WRITE);
      }else{
         // else keep reading
         logger.info("Did not read any bytes, keep reading");
      }
   }

   private void stopReadingFromSocket(SocketChannel srcSocket,
         SelectionKey srcKey, SocketChannel dstSocket) throws IOException {
      // reset the READ registration on srcSocket
      srcKey.interestOps(srcKey.interestOps() ^ SelectionKey.OP_READ);
      try{
         srcSocket.socket().shutdownInput();
      }catch(IOException x){
         logger.info("Failed to shutdown input after EOF, no problem");
      }
      // and also shutdown the writing side on dstSocket to forward the EOF
      dstSocket.socket().shutdownOutput();
   }

   public void halt() {
      halted = true;
      selector.wakeup();
   }

}
