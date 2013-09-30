package org.openremote.modeler.server.lutron.importmodel;

import java.util.HashSet;
import java.util.Set;

public class Area {

  private String name;
  private Set<Room> rooms;
  
  public Area(String name) {
    super();
    this.name = name;
    this.rooms = new HashSet<Room>();
  }

  public String getName() {
    return name;
  }
  
  public Set<Room> getRooms() {
    return rooms;
  }

  public void addRoom(Room room) {
    rooms.add(room);
  }
  
}
