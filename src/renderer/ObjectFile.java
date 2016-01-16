package renderer;

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
            } else if (s.startsWith("f")) {
               Scanner line = new Scanner(s);
               line.next();
               triangles.add(new Triangle(
                     line.nextInt() - 1, 
                     line.nextInt() - 1, 
                     line.nextInt() - 1));
            }
         }
         scanner.close();
      } catch (Exception e) {
         throw new RuntimeException("Input File Issue", e);
      }
   }
}
