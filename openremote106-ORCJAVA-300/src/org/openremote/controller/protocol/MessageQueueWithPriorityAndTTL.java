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
package org.openremote.controller.protocol;

import java.util.Date;
import java.util.LinkedList;

/**
 * A queue that implements several specific requirements:
 * - queue has an overall TTL, entries are dropped from the queue (on read operations) if they have been in the queue longer than the queue TTL
 * - inserted entries have a TTL, they're dropped from the queue (on read operations) if they have been in the queue longer than their TTL
 * - there is the possibility of priority entries, they are inserted at the head of the queue (after the existing priority entries)
 * - inserted entries are coalesced (if the entry class implements the Coalescable interface), newer entry is kept
 * - it is thread safe
 * 
 * @author <a href="mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class MessageQueueWithPriorityAndTTL<E> {

  // Queue to store entries, they are encapsulated inside a QueueEntry object
  private LinkedList<QueueEntry> queue;
  
  // Indicates position in the queue of first entry than is not a priority entry
  private int firstNonPriorityMessage;

  // Overall queue TTL. 0 means no TTL
  private long ttl = 0;

  /**
   * Creates an empty queue with no overall queue TTL.
   */
  public MessageQueueWithPriorityAndTTL() {
    super();
    queue = new LinkedList<QueueEntry>();
    firstNonPriorityMessage = 0;
  }

  /**
   * Creates an empty queue with the given overall queue TTL.
   * 
   * @param ttl overall queue TTL, expressed in milliseconds, 0 means no TTL
   */
  public MessageQueueWithPriorityAndTTL(long ttl) {
    this();
    this.ttl = ttl;
  }

  /**
   * Adds an entry to the queue as a priority entry. That is in front of all non priority entries but after all existing priority entries already in the queue.
   * The entry is assigned the given TTL.
   * 
   * @param e entry to add to the queue
   * @param ttl ttl for the inserted entry, expressed in milliseconds, 0 means no TTL
   */
  public void priorityAdd(E e, long ttl) {
    synchronized (queue) {
      queue.add(firstNonPriorityMessage, new QueueEntry(e, ttl));
      firstNonPriorityMessage++;
      queue.notify();
    }
  }

  /**
   * Adds an entry to the queue as a priority entry. That is in front of all non priority entries but after all existing priority entries already in the queue.
   * 
   * @param e entry to add to the queue
   */
  public void priorityAdd(E e) {
    priorityAdd(e, 0);
  }

  /**
   * Adds an entry to the queue, at the end of the queue. The entry is assigned the given TTL.
   * 
   * @param e entry to add to the queue
   * @param ttl ttl for the inserted entry, expressed in milliseconds, 0 means no TTL
   */
  public void add(E e, long ttl) {
    synchronized (queue) {
      // Message support coalescing
      if (e instanceof Coalescable) {
        // Iterate the queue from top to bottom, if an entry is Coalescable, remove it
        for (QueueEntry qe : queue) {
          if (qe.getEntry() instanceof Coalescable && ((Coalescable)e).isCoalesable((Coalescable) qe.getEntry())) {
            queue.remove(qe);
            break;
          }
        }
      }
      queue.add(new QueueEntry(e, ttl));
      queue.notify();
    }
  }

  /**
   * Adds an entry to the queue, at the end of the queue.
   * 
   * @param e entry to add to the queue
   */
  public void add(E e) {
    add(e, 0);
  }

  /**
   * Returns and removes the entry at the head of the queue.
   * This method never waits and returns null if the queue is empty.
   * 
   * @return Entry at the head of the queue or null if queue is empty
   */
  public E poll() {
    QueueEntry e = null;
    long currentTime = new Date().getTime();
    synchronized (queue) {
      while (e == null && !queue.isEmpty()) {
        if (firstNonPriorityMessage > 0) {
          firstNonPriorityMessage--;
        }
        e = queue.poll();
        // First check general queue TTL
        if (e != null && ttl != 0 && e.getTimestamp() + ttl < currentTime) {
          e = null;
        }
        // Then message specific one
        if (e != null && e.getTtl() != 0 && e.getTimestamp() + e.getTtl() < currentTime) {
          e = null;
        }
      }
    }
    return (e != null) ? e.getEntry() : null;
  }

  /**
   * Returns and removes the entry at the head of the queue.
   * If the queue is currently empty, this methods waits until there is an entry added before returning.
   * 
   * @return Entry at the head of the queue
   */
  public E blockingPoll() throws InterruptedException {
    synchronized (queue) {
      while (queue.isEmpty()) {
        // Queue is empty, wait
       queue.wait();
      }
    }
    return poll();
  }

  /**
   * Encapsulates queue entry, adding entry ttl and timestamp to implement TTL.
   * 
   * @author <a href="mailto:eric@openremote.org">Eric Bariaux</a>
   */
  private class QueueEntry {

    private long timestamp;
    private long ttl;
    private E entry;

    public QueueEntry(E entry, long ttl) {
      super();
      this.timestamp = new Date().getTime();
      this.entry = entry;
      this.ttl = ttl;
    }

    public long getTimestamp() {
      return timestamp;
    }
    
    public long getTtl() {
      return ttl;
    }

    public E getEntry() {
      return entry;
    }

    public String toString() {
      return new Date(timestamp) + ": " + entry;
    }
  }

  public interface Coalescable {
    
    boolean isCoalesable(Coalescable other);
    
  }
}
