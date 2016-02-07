package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ObjectFile {
   
   public List<Vector> verticies = new ArrayList<>();
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
}
