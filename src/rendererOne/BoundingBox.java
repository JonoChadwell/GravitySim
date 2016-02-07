package rendererOne;

import java.util.List;

import utils.Triangle;
import utils.Vector;

public class BoundingBox {
   public final int minX;
   public final int maxX;
   public final int minY;
   public final int maxY;
   
   public BoundingBox(int minX, int maxX, int minY, int maxY) {
      this.minX = minX;
      this.maxX = maxX;
      this.minY = minY;
      this.maxY = maxY;
   }
   
   public BoundingBox(List<Vector> vertices, Triangle t) {
      this.minX = (int) Math.floor(min(vertices.get(t.a).x, vertices.get(t.b).x, vertices.get(t.c).x));
      this.maxX = (int) Math.ceil(max(vertices.get(t.a).x, vertices.get(t.b).x, vertices.get(t.c).x));
      this.minY = (int) Math.floor(min(vertices.get(t.a).y, vertices.get(t.b).y, vertices.get(t.c).y));
      this.maxY = (int) Math.ceil(max(vertices.get(t.a).y, vertices.get(t.b).y, vertices.get(t.c).y));
   }
   
   private double min(double a, double b, double c) {
      return Math.min(a, Math.min(b, c));
   }
   
   private double max(double a, double b, double c) {
      return Math.max(a, Math.max(b, c));
   }
}
