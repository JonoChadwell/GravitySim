package rendererTwo;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import utils.Quaternion;
import utils.Triangle;
import utils.Vector;

public class Camera {
   public Vector location = new Vector(0,0,-1);
   public double perspectiveDistance = 1;
   public double zoom = 1000;

   public Vector light = new Vector(0,-1,0);
   
   private Vector forwards = new Vector(0,0,1);
   private double rotation = 0.0;
   
   private Vector getLeft() {
      Vector base = Vector.cross(forwards, new Vector(0,1,0));
      return Quaternion.rotate(base, forwards, rotation);
   }
   
   private Vector getUp() {
      Vector base = Vector.cross(forwards, new Vector(0,1,0));
      return Quaternion.rotate(base, forwards, rotation + Math.PI / 2);
   }
   
   public void look(double x, double y, double r) {
      Quaternion left = new Quaternion(getUp(), x);
      Quaternion up = new Quaternion(getLeft(), y);
      Quaternion product = Quaternion.cross(left, up);
      this.forwards = Quaternion.rotate(this.forwards, product).vector();
      rotation += r;
   }
   
   public void move(Vector amt) {
//      location = Vector.add(location, amt);
      location = Vector.add(
            location,
            Vector.scale(getLeft(), amt.x),
            Vector.scale(getUp(), amt.y),
            Vector.scale(forwards, amt.z));
   }
   
   public void render(List<Vector> worldPoints, List<Triangle> faces, Graphics g, int width, int height) {
      rotation = rotation + 0.001;
      List<Vector> renderPoints = new ArrayList<>();
      double hRot = Math.atan2(forwards.z, forwards.x);
      double vRot = Math.atan2(Math.sqrt(forwards.z * forwards.z + forwards.x * forwards.x), forwards.y);
      Quaternion flatten = new Quaternion(forwards, rotation);
      Quaternion vertical = new Quaternion(Vector.cross(forwards, new Vector(0,1,0)), vRot);
      Quaternion horizontal = new Quaternion(new Vector(0,1,0), hRot);
      Quaternion finalRotation = Quaternion.cross(Quaternion.cross(flatten, vertical), horizontal);
      List<Color> colors = calculateLighting(worldPoints, faces);
      
      for (Vector v : worldPoints) {
         Vector rotated = Quaternion.rotate(Vector.difference(v, location), finalRotation).vector();
         rotated.x = rotated.x * zoom / (rotated.z / perspectiveDistance) + width / 2;
         rotated.y = rotated.y * zoom / (rotated.z / perspectiveDistance) + height / 2;
         renderPoints.add(rotated);
      }
      
      faces.sort((a,b) -> Double.compare(renderPoints.get(b.a).z, renderPoints.get(a.a).z));
      for (Triangle t : faces) {
         Vector A = renderPoints.get(t.a);
         Vector B = renderPoints.get(t.b);
         Vector C = renderPoints.get(t.c);
         
         Color Ac = colors.get(t.a);
         Color Bc = colors.get(t.b);
         Color Cc = colors.get(t.c);
         
         if (A.z > 0.001 && B.z > 0.001 && C.z > 0.001) {
            double red = Cc.getRed() * 0.333 + Ac.getRed() * 0.333 + Bc.getRed() * 0.333;
            double green = Cc.getGreen() * 0.333 + Ac.getGreen() * 0.333 + Bc.getGreen() * 0.333;
            double blue = Cc.getBlue() * 0.333 + Ac.getBlue() * 0.333 + Bc.getBlue() * 0.333;
            g.setColor(new Color((int) red, (int) green, (int) blue));
            //g.setColor(Color.cyan);
            int x[] = {(int) A.x, (int) B.x, (int) C.x};
            int y[] = {(int) A.y, (int) B.y, (int) C.y};
            g.fillPolygon(x, y, 3);
         }
      }
   }
   
   private static class LightingData {
      public double val;
      public int count;
   }
   
   private static final double AMBIENT = 0.2;
   private static final double REFLECTIVE = 0.4;
   private static final double REFRACTIVE = 0.4;
   
   public List<Color> calculateLighting(List<Vector> points, List<Triangle> faces) {
      List<LightingData> data = new ArrayList<>();
      for (Vector v : points) {
         data.add(new LightingData());
      }
      for (Triangle t : faces) {
         Vector AC = Vector.difference(points.get(t.a), points.get(t.c));
         Vector BC = Vector.difference(points.get(t.b), points.get(t.c));
         Vector normal = Vector.unit(Vector.cross(AC, BC));
         double refractivePortion = clamp(Vector.dot(normal, light)) * REFRACTIVE;
         Vector reflectedLight = Vector.difference(Vector.scale(normal, 2 * Vector.dot(Vector.scale(light, -1), normal)), light);
         double reflectivePortion = clamp(Vector.dot(this.forwards, Vector.unit(reflectedLight))) * REFLECTIVE;
         double lighting = refractivePortion + reflectivePortion + AMBIENT;
         addData(lighting, data.get(t.a));
         addData(lighting, data.get(t.b));
         addData(lighting, data.get(t.c));
      }
      List<Color> rtn = new ArrayList<>();
      for (LightingData ld : data) {
         ld.val = clamp(ld.val);
         rtn.add(new Color(0, (int) (255 * ld.val), (int) (255 * ld.val)));
      }
      return rtn;
   }
   
   public double clamp(double input) {
      if (input < 0) {
         return 0;
      }
      if (input > 1) {
         return 1;
      }
      return input;
   }
   
   public void addData(double val, LightingData data) {
      data.val = (data.val * data.count + val) / (data.count + 1);
      data.count++;
   }
   
   private double getTwiceArea(Vector a, Vector b, Vector c) {
      return getTwiceArea(a.x, a.y, b.x, b.y, c.x, c.y);
   }
   
   private double getTwiceArea(double ax, double ay, double bx, double by, double cx, double cy) {
      return (bx - ax) * (cy - ay) - (cx - ax) * (by - ay);
   }
}
