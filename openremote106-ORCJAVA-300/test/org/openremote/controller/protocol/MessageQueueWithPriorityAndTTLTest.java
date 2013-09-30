package org.openremote.controller.protocol;

import junit.framework.Assert;

import org.junit.Test;
import org.openremote.controller.protocol.MessageQueueWithPriorityAndTTL;
import org.openremote.controller.protocol.MessageQueueWithPriorityAndTTL.Coalescable;

/**
 * 
 * @author <a href="mailto:eric@openremote.org">Eric Bariaux</a>
 *
 */
public class MessageQueueWithPriorityAndTTLTest {

  @Test public void testSimpleOperations() {
    MessageQueueWithPriorityAndTTL<String> queue = new MessageQueueWithPriorityAndTTL<String>();
    queue.add("Message 1");
    queue.add("Message 2");
    queue.priorityAdd("Message 3");
    queue.priorityAdd("Message 4");
    Assert.assertEquals("Message 3", queue.poll());
    Assert.assertEquals("Message 4", queue.poll());
    Assert.assertEquals("Message 1", queue.poll());
    Assert.assertEquals("Message 2", queue.poll());
  }
  
  @Test public void testQueueTTLFeature() throws InterruptedException {
    MessageQueueWithPriorityAndTTL<String> queue = new MessageQueueWithPriorityAndTTL<String>(1000); // 1 sec TTL
    queue.add("Message 1");
    queue.add("Message 2");
    queue.priorityAdd("Message 3");
    Assert.assertEquals("Message 3", queue.poll());
    Assert.assertEquals("Message 1", queue.poll());
    // Wait for 1.5 sec before the poll, message with 1 sec TTL should not be present in queue anymore
    Thread.sleep(1500);
    Assert.assertNull(queue.poll());
  }

  @Test public void testMessageTTLFeature() throws InterruptedException {
    MessageQueueWithPriorityAndTTL<String> queue = new MessageQueueWithPriorityAndTTL<String>();
    queue.add("Message 1", 1000);
    queue.add("Message 2", 5000);
    queue.priorityAdd("Message 3", 1000);
    queue.priorityAdd("Message 4", 1000);
    Assert.assertEquals("Message 3", queue.poll());
    // Wait for 1.5 sec before the poll
    Thread.sleep(1500);
    Assert.assertEquals("Message 2", queue.poll());
    Assert.assertNull(queue.poll());
  }

  @Test public void testCoalescable() {
    MessageQueueWithPriorityAndTTL<TestEntry> queue = new MessageQueueWithPriorityAndTTL<TestEntry>();
    queue.add(new TestEntry("Entry 1"));
    queue.add(new TestEntry("Entry 2"));
    queue.add(new TestEntry("Entry 1"));
    queue.add(new TestEntry("Entry 3"));
    Assert.assertEquals("Entry 2", queue.poll().getValue());
    Assert.assertEquals("Entry 1", queue.poll().getValue());
    Assert.assertEquals("Entry 3", queue.poll().getValue());
  }

  private class TestEntry implements Coalescable {

    private String value;
   
    public TestEntry(String value) {
      super();
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public boolean isCoalesable(Coalescable other) {
      if (!(other instanceof TestEntry)) {
        return false;
      }
      return (((TestEntry)other).getValue().equals(getValue()));
    }
    
  }
}
