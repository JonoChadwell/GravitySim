package renderer;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jono
 */
public class Camera {

   private static enum Mode {
      FACES,
      LINES,
      POINTS;
   }
   
   private Mode mode = Mode.FACES;
   public Vector location;
   public double hRot;
   public double vRot;
   public double zoom = 1000;
   public double perspectiveDistance = 1;
   private double screenX;
   private double screenY;

   public Vector light = new Vector(1,0,0);
   
   private Vector normal = new Vector();
   private Vector lateral = new Vector();
   private Vector horizontal = new Vector();
   private double normalSquared;
   private double lateralSquared;
   private double horizontalSquared;

   public Camera(Vector location) {
      this.location = location;
   }
   
   private double getTwiceArea(double ax, double ay, double bx, double by, double cx, double cy) {
      return (bx - ax) * (cy - ay) - (cx - ax) * (by - ay);
   }
   
   private double getTwiceArea(Vector a, Vector b, double cx, double cy) {
      return getTwiceArea(a.x, a.y, b.x, b.y, cx, cy);
   }

   public void draw(List<Vector> originalPoints, List<Triangle> faces, Graphics g, int width, int height) {
      this.screenX = width / 2;
      this.screenY = height / 2;
      
      List<Vector> points = convert(originalPoints);
      List<Color> colors = calculateLighting(originalPoints, faces);
      
      double[][] zbuff = new double[width][height];
      for (int i = 0; i < zbuff.length; i++) {
         for (int j = 0; j < zbuff[0].length; j++) {
            zbuff[i][j] = 1000;
         }
      }
      g.setColor(Color.CYAN);
      switch (mode) {
      case FACES:
         for (Triangle t : faces) {
            BoundingBox bb = new BoundingBox(points, t);
            
            Vector A = points.get(t.a);
            Vector B = points.get(t.b);
            Vector C = points.get(t.c);
            
            Color Ac = colors.get(t.a);
            Color Bc = colors.get(t.b);
            Color Cc = colors.get(t.c);
            
            double Area = getTwiceArea(A, B, C.x, C.y);
            
            if (A.z > 0 && B.z > 0 && C.z > 0) {
               for (int x = Math.max(bb.minX, 0); x <= Math.min(bb.maxX, width - 1); x++) {
                  for (int y = Math.max(bb.minY, 0); y <= Math.min(bb.maxY, height - 1); y++) {
                     
                     double a = getTwiceArea(A, B, x, y) / Area;
                     double b = getTwiceArea(B, C, x, y) / Area;
                     double c = getTwiceArea(C, A, x, y) / Area;
                     double z = C.z * a + A.z * b + B.z * c;
                     
                     if (a >= 0 && b >= 0 && c >= 0 && Math.abs(a + b + c - 1) < 0.10) {
                        if (z > 0 && z < zbuff[x][y]) {
                           zbuff[x][y] = z;
                           double red = Cc.getRed() * a + Ac.getRed() * b + Bc.getRed() * c;
                           double green = Cc.getGreen() * a + Ac.getGreen() * b + Bc.getGreen() * c;
                           double blue = Cc.getBlue() * a + Ac.getBlue() * b + Bc.getBlue() * c;
                           g.setColor(new Color((int) red, (int) green, (int) blue));
                           g.drawLine(x, y, x, y - 1);
                        }
                     }
                  }
               }
            }
         }
         break;
         
      case LINES:
         for (Triangle t : faces) {
            if (points.get(t.a).z > 0.001 && points.get(t.b).z > 0.001 && points.get(t.c).z > 0.001) {
               g.drawLine((int) points.get(t.a).x, (int) points.get(t.a).y, (int) points.get(t.b).x, (int) points.get(t.b).y);
               g.drawLine((int) points.get(t.b).x, (int) points.get(t.b).y, (int) points.get(t.c).x, (int) points.get(t.c).y);
               g.drawLine((int) points.get(t.c).x, (int) points.get(t.c).y, (int) points.get(t.a).x, (int) points.get(t.a).y);
            }
         }
         break;
         
      case POINTS:
         for (Vector v : points) {
            if (v.z > 0.001) {
               g.drawOval((int) v.x - 2, (int) v.y - 2, 4, 4);
            }
         }
      }
   }
   
   private static class LightingData {
      public double val;
      public int count;
   }
   
   public List<Color> calculateLighting(List<Vector> points, List<Triangle> faces) {
      List<LightingData> data = new ArrayList<>();
      for (Vector v : points) {
         data.add(new LightingData());
      }
      for (Triangle t : faces) {
         Vector AC = Vector.difference(points.get(t.a), points.get(t.c));
         Vector BC = Vector.difference(points.get(t.b), points.get(t.c));
         Vector normal = Vector.cross(AC, BC);
         normal = Vector.scale(normal, 1 / Vector.abs(normal));
         double lighting = Vector.dot(normal, light);
         addData(lighting, data.get(t.a));
         addData(lighting, data.get(t.b));
         addData(lighting, data.get(t.c));
      }
      List<Color> rtn = new ArrayList<>();
      for (LightingData ld : data) {
         rtn.add(new Color(0, (int) (255 * (ld.val + 1) / 2), (int) (255 * (ld.val + 1) / 2)));
      }
      return rtn;
   }
   
   public void addData(double val, LightingData data) {
      data.val = (data.val * data.count + val) / (data.count + 1);
      data.count++;
   }

   public List<Vector> convert(List<Vector> vectors) {
      precalcDrawing();
      List<Vector> rtn = new ArrayList<Vector>();
      for (Vector v : vectors) {
         v = Vector.add(v, location);
         v = calcBasicCoords(v);
         v = scaleForDistance(v);
         rtn.add(v);
      }
      return rtn;
   }

   private void precalcDrawing() {
      normal = new Vector(-Math.cos(hRot) * Math.cos(vRot), Math.sin(hRot) * Math.cos(vRot), -Math.sin(vRot));
      lateral = new Vector(Math.sin(hRot), Math.cos(hRot), 0);
      horizontal = Vector.cross(normal, lateral);
      normalSquared = Vector.dot(normal, normal);
      lateralSquared = Vector.dot(lateral, lateral);
      horizontalSquared = Vector.dot(horizontal, horizontal);
   }

   private Vector calcBasicCoords(Vector v) {
      return new Vector(
            Vector.dot(v, lateral) / lateralSquared * zoom + screenX,
            Vector.dot(v, horizontal)/ horizontalSquared * zoom + screenY,
            Vector.dot(v, normal) / normalSquared);
   }

   private Vector scaleForDistance(Vector v) {
      return new Vector(
            (v.x - screenX) / (v.z / perspectiveDistance) + screenX,
            (v.y - screenY) / (v.z / perspectiveDistance) + screenY,
            v.z);
   }
   
   public void moveCamera(Vector amt) {
      location = Vector.add(Vector.scale(lateral, amt.x), location);
      location = Vector.add(Vector.scale(normal, amt.y), location);
      location = Vector.add(Vector.scale(horizontal, amt.z), location);
  }
}
