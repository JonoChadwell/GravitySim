package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ObjectFile {
   
   public List<Vector> verticies = new ArrayList<>();
   public List<Vector> normals = new ArrayList<>();
   public List<Triangle> triangles = new ArrayList<>();
   
   public ObjectFile(String filename) {
      File file = new File(filename);
      try {
         Scanner scanner = new Scanner(file);
         while (scanner.hasNextLine()) {
            String s = scanner.nextLine();
            if (s.startsWith("v")) {
               Scanner line = new Scanner(s);
               line.next();
               double y = line.nextDouble();
               double z = line.nextDouble();
               double x = line.nextDouble();
               verticies.add(new Vector(x,y,z));
               line.close();
            } else if (s.startsWith("f")) {
               Scanner line = new Scanner(s);
               line.next();
               triangles.add(new Triangle(
                     line.nextInt() - 1, 
                     line.nextInt() - 1, 
                     line.nextInt() - 1));
               line.close();
            }
         }
         scanner.close();
      } catch (Exception e) {
         throw new RuntimeException("Input File Issue", e);
      }
   }
   
   public void rescale() {
      double max = Double.MIN_VALUE;
      double min = Double.MAX_VALUE;
      for (Vector v : verticies) {
         if (v.x > max)
            max = v.x;
         if (v.y > max)
            max = v.y;
         if (v.z > max)
            max = v.z;
         
         if (v.x < min)
            min = v.x;
         if (v.y < min)
            min = v.y;
         if (v.z < min)
            min = v.z;
      }
      List<Vector> newVerticies = new ArrayList<>(verticies.size());
      for (Vector v : verticies) {
         newVerticies.add(new Vector(
               (v.x - min) / (max - min) * 2 - 1,
               (v.y - min) / (max - min) * 2 - 1,
               (v.z - min) / (max - min) * 2 - 1));
      }
      verticies = newVerticies;
   }
   
   public void computeNormals() {
      normals = new ArrayList<>();
      float[] norBuf = new float[verticies.size() * 3];
      for (int i = 0; i < norBuf.length; i++) {
         norBuf[i] = 0.0f;
      }
      for (Triangle t : triangles) {
         Vector a = verticies.get(t.a);
         Vector b = verticies.get(t.b);
         Vector c = verticies.get(t.c);
         
         Vector normal = Vector.unit(Vector.cross(Vector.difference(a, b), Vector.difference(a, c)));
         norBuf[t.a + 0] += normal.x;
         norBuf[t.a + 1] += normal.y;
         norBuf[t.a + 2] += normal.z;
         norBuf[t.b + 0] += normal.x;
         norBuf[t.b + 1] += normal.y;
         norBuf[t.b + 2] += normal.z;
         norBuf[t.c + 0] += normal.x;
         norBuf[t.c + 1] += normal.y;
         norBuf[t.c + 2] += normal.z;
      }
      for (int i = 0; i < norBuf.length; i += 3) {
         normals.add(Vector.unit(new Vector(norBuf[i + 0], norBuf[i + 1], norBuf[i + 2])));
      }
   }
}
