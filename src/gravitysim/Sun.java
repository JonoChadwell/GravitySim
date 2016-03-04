package gravitysim;

import utils.Vector;

/**
 *
 * @author Jono
 */
public class Sun extends GravObject {
   public Sun(Vector location, Vector velocity, double mass) {
      super(location, velocity, mass);
   }

   public Sun(Vector location, Vector velocity, double mass, double radius, String name) {
      super(location, velocity, mass, radius, name);
   }
}
