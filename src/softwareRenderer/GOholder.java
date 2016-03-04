package softwareRenderer;

import gravitysim.GravObject;
import gravitysim.Sun;

import java.awt.Color;

import utils.Vector;

/**
 *
 * @author Jono
 */
public class GOholder {

   public GOholder(GravObject go) {
      this.go = go;
      if (go.name != null) {
         name = go.name;
      }
      location = go.location;
      radius = go.radius;
      drawColor = go.color;
      isSun = go instanceof Sun;
   }

   public GravObject go;
   public String name;
   public boolean isSun;
   public Vector location;
   public double radius;
   public Color drawColor;
   public double screenRadius;
   public Vector screenLocation;
}
