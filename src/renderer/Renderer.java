package renderer;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

public class Renderer {
   public static void main(String args[]) {
      String file = "/home/jpchadwe/Documents/CPE471/Program1/bunny.obj";
      ObjectFile of = new ObjectFile(file);
      
      List<Vector> points = new ArrayList<>();
      List<Triangle> faces = new ArrayList<>();
      points.add(new Vector(0.1,0,0));
      points.add(new Vector(0,0.1,0));
      points.add(new Vector(0,0,0.1));
      faces.add(new Triangle(0,1,2));
      
      Display display = new Display();
      display.points = of.verticies;
      display.faces = of.triangles;
      
      JFrame frame = new JFrame();
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setSize(800, 600);
      frame.setContentPane(display);
      frame.setVisible(true);
   }
}
