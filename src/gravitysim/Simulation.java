package gravitysim;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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
      applyGravity(part);

      for (GravObject go : objects) {
         go.changeLocation(Vector.add(go.location, Vector.scale(go.velocity, part)));
      }
   }

   private long time(Runnable r) {
      long start = System.nanoTime();
      r.run();
      return System.nanoTime() - start;
   }

   private static final int THREADS = 8;

   @SuppressWarnings("unused")
   private void applyGravity(double part) {
      if (THREADS <= 1) {
         for (GravObject go : objects) {
            calcGravitationalAcceleration(go);
            go.velocity = Vector.add(go.velocity, Vector.scale(go.acceleration, part));
         }
      } else {
         List<Thread> gravityThreads = new ArrayList<>();
         for (int i = 0; i < THREADS; i++) {
            final int section = i;
            Thread t = new Thread(
                  () -> {
                     for (int pos = objects.size() * section / THREADS; pos < objects.size() * (section + 1) / THREADS; pos++) {
                        GravObject go = objects.get(pos);
                        calcGravitationalAcceleration(go);
                        go.velocity = Vector.add(go.velocity, Vector.scale(go.acceleration, part));
                     }
                  });
            t.start();
            gravityThreads.add(t);
         }
         for (Thread t : gravityThreads) {
            try {
               t.join();
            } catch (InterruptedException e) {
               throw new RuntimeException(e);
            }
         }
      }
   }

   private void calcGravitationalAcceleration(GravObject go) {
      go.acceleration = new Vector();
      for (GravObject goOther : objects) {
         if (go != goOther) {
            Vector accel = getGravAccel(go, goOther);
            go.acceleration = Vector.add(accel, go.acceleration);
         }
      }
   }

   private Vector getGravAccel(GravObject go, GravObject goOther) {
      Vector distance = Vector.difference(go.location, goOther.location);
      double dist = Vector.abs(distance);
      if (dist < go.radius + goOther.radius) {
         return new Vector();
      }
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
      // rtn.drawColor = Color.white; //change color of collided objects
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
      LinkedList<Vector> before = objs.stream().max((a,b) -> Double.compare(a.mass, b.mass)).get().previous;
      if (before != null) {
         temp.previous = before;
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

   public void addObject(GravObject go) {
      objects.add(go);
   }
   
   public void addObjects(GravObject[] gos) {
      for (GravObject obj : gos) {
         addObject(obj);
      }
   }
   
   public void convertAxis() {
      for (GravObject obj : objects) {
         double y;
         y = obj.location.y;
         obj.location.y = obj.location.z;
         obj.location.z = y;
         
         y = obj.velocity.y;
         obj.velocity.y = obj.velocity.z;
         obj.velocity.z = y;
         
         y = obj.acceleration.y;
         obj.acceleration.y = obj.acceleration.z;
         obj.acceleration.z = y;
      }
   }

   public List<GravObject> getObjects() {
      return objects;
   }

   public double getTicks() {
      return ticks;
   }
}
