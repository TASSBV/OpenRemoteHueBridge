package org.openremote.modeler.client.model;

public class ORBounds {

   private int left;
   private int top;
   private int width;
   private int height;
   
   public ORBounds(int left, int top, int width, int height) {
      this.left = left;
      this.top = top;
      this.width = width;
      this.height = height;
   }
   
   public int getLeft() {
      return left;
   }
   public int getTop() {
      return top;
   }
   public int getWidth() {
      return width;
   }
   public int getHeight() {
      return height;
   }
   
   
}
