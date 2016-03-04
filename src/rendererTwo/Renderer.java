package rendererTwo;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import softwareRenderer.ObjectFile;
import utils.Triangle;
import utils.Vector;

public class Renderer {
   public static void main(String args[]) {
      String file = "/home/jpchadwe/Documents/CPE471/Program1/bunny.obj";
      ObjectFile of = new ObjectFile(file);
      
      Display display = new Display();
      display.points = of.verticies;
      display.faces = of.triangles;
      
//      List<Vector> points = new ArrayList<>();
//      List<Triangle> faces = new ArrayList<>();
//      points.add(new Vector(1,0,0));
//      points.add(new Vector(0,1,0));
//      points.add(new Vector(0,0,1));
//      faces.add(new Triangle(0,1,2));
//      
//      display.points = points;
//      display.faces = faces;
      
      JFrame frame = new JFrame();
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setSize(800, 600);
      frame.setContentPane(display);
      frame.setVisible(true);
   }
}
