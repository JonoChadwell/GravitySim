package gravitysim;

import java.util.ArrayList;
import java.util.Set;

import utils.Vector;

/**
 *
 * @author Jono
 */
public class Simulation {
    private double GRAVITY = 8.89 * Math.pow(10, -10);
    private ArrayList<GravObject> objects = new ArrayList<>();
    private double ticks = 0;
    
    public void tickSimulation(double part) {
        ticks += part;
        collision();
        for (GravObject go : objects) {
           go.acceleration = new Vector();
        }
        for (int i = 0; i < objects.size() - 1; i++) {
           GravObject go = objects.get(i);
           calcGravitationalAcceleration(go, i + 1);
           go.velocity = Vector.add(go.velocity, Vector.scale(go.acceleration, part));
        }
        for (GravObject go : objects) {
            go.location = Vector.add(go.location, Vector.scale(go.velocity, part));
        }
    }

   private void calcGravitationalAcceleration(GravObject go, int minIndex) {
      for (int i = minIndex; i < objects.size(); i++) {
         GravObject goOther = objects.get(i);
         Vector accel = getGravAccel(go, goOther);
         go.acceleration = Vector.add(go.acceleration, accel);
         accel = getGravAccel(goOther, go);
         goOther.acceleration = Vector.add(goOther.acceleration, accel);
      }
   }

    private Vector getGravAccel(GravObject go, GravObject goOther) {
        Vector distance = Vector.difference(go.location, goOther.location);
        double dist = Vector.abs(distance);
        double acceleration = GRAVITY * goOther.mass / (dist * dist);
        return Vector.scale(Vector.unit(distance), -acceleration);
    }
    
    private GravObject performCollision(GravObject go, GravObject goOther) {
        Vector diff = Vector.difference(go.location, goOther.location);
        double amt = (goOther.mass) / (go.mass + goOther.mass);
        diff = Vector.scale(diff, -amt);
        Vector goMoment = Vector.scale(go.velocity, go.mass);
        Vector goOtherMoment = Vector.scale(goOther.velocity, goOther.mass);
        Vector newMoment = Vector.add(goMoment, goOtherMoment);
        Vector newVelocity = Vector.scale(newMoment, 1 / (go.mass + goOther.mass));
        GravObject rtn;
        rtn = new GravObject(Vector.add(go.location, diff), newVelocity, go.mass + goOther.mass);
        //rtn.drawColor = Color.white; //change color of collided objects
        return rtn;
    }
    
   private GravObject performCollision(Set<GravObject> objs) {
      GravObject temp = null;
      for (GravObject obj : objs) {
         if (temp == null) {
            temp = obj;
         } else {
            temp = performCollision(temp, obj);
         }
      }
      return temp;
   }

   private void collision() {
      Set<Set<GravObject>> allCollisions = CollisionEngine.collide(objects);
      for (Set<GravObject> collision : allCollisions) {
         objects.removeAll(collision);
         objects.add(performCollision(collision));
      }
   }
    
    public void centerMass() {
        Vector massLocCenter = new Vector();
        Vector massVelCenter = new Vector();
        double massSum = 0.0;
        for (GravObject go : objects) {
            massLocCenter = Vector.add(Vector.scale(go.location, go.mass), massLocCenter);
            massVelCenter = Vector.add(Vector.scale(go.velocity, go.mass), massVelCenter);
            massSum += go.mass;
        }
        massLocCenter = Vector.scale(massLocCenter, 1 / massSum);
        massVelCenter = Vector.scale(massVelCenter, 1 / massSum);
        for (GravObject go : objects) {
            go.location = Vector.difference(go.location, massLocCenter);
            go.velocity = Vector.difference(go.velocity, massVelCenter);
        }
    }
    
    public void addObject(GravObject go){
        objects.add(go);
    }
    
    public GravObject[] getObjects(){
        return objects.toArray(new GravObject[0]);
    }
    public double getTicks(){
        return ticks;
    }
}
