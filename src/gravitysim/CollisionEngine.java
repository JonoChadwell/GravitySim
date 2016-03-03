package gravitysim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import utils.Vector;

public final class CollisionEngine {
   private static final int MAGIC_NUMBER = 100;

   // hide constructor to prevent instanciation
   private CollisionEngine() {
   }

   public static Set<Set<GravObject>> collide(List<GravObject> objects) {
      HashMap<GravObject, Set<GravObject>> collisions = new HashMap<>();
      recursiveCollide(collisions, objects);
      Set<Set<GravObject>> rtn = new HashSet<>();
      rtn.addAll(collisions.values());
      return rtn;
   }
   
   public static Set<Set<GravObject>> dumbCollide(List<GravObject> objects) {
      HashMap<GravObject, Set<GravObject>> collisions = new HashMap<>();
      dumbCollide(collisions, objects);
      Set<Set<GravObject>> rtn = new HashSet<>();
      rtn.addAll(collisions.values());
      return rtn;
   }

   private static void recursiveCollide(HashMap<GravObject, Set<GravObject>> collisions, List<GravObject> objects) {
      if (objects.size() < MAGIC_NUMBER) {
         dumbCollide(collisions, objects);
         return;
      }
      Vector middle = findMiddle(objects);
      List<List<GravObject>> sectors = new ArrayList<>();
      for (int i = 0; i < 8; i++) {
         sectors.add(new ArrayList<>());
      }

      int doubled = 0;
      int numAdded = 0;
      for (GravObject obj : objects) {
         numAdded = 0;
         numAdded += checkAndAddSector(sectors, obj, middle, false, false, false);
         numAdded += checkAndAddSector(sectors, obj, middle, true, false, false);
         numAdded += checkAndAddSector(sectors, obj, middle, false, true, false);
         numAdded += checkAndAddSector(sectors, obj, middle, true, true, false);
         numAdded += checkAndAddSector(sectors, obj, middle, false, false, true);
         numAdded += checkAndAddSector(sectors, obj, middle, true, false, true);
         numAdded += checkAndAddSector(sectors, obj, middle, false, true, true);
         numAdded += checkAndAddSector(sectors, obj, middle, true, true, true);
         if (numAdded > 1) {
            doubled += numAdded - 1;
         }
         if (doubled >= objects.size()) {
            dumbCollide(collisions, objects);
            return;
         }
      }
      //System.out.println("Doubled count: " + doubled);
      for (List<GravObject> set : sectors) {
         if (set.size() == collisions.size()) {
            dumbCollide(collisions, objects);
            return;
         }
      }
      for (List<GravObject> set : sectors) {
         recursiveCollide(collisions, set);
      }
   }

   private static int checkAndAddSector(List<List<GravObject>> sectors, GravObject obj, Vector middle, boolean x, boolean y, boolean z) {
      if (x && obj.location.x + obj.radius < middle.x)
         return 0;
      if (!x && obj.location.x - obj.radius > middle.x)
         return 0;
      if (y && obj.location.y + obj.radius < middle.y)
         return 0;
      if (!y && obj.location.y - obj.radius > middle.y)
         return 0;
      if (z && obj.location.z + obj.radius < middle.z)
         return 0;
      if (!z && obj.location.z - obj.radius > middle.z)
         return 0;
      sectors.get((x ? 4 : 0) + (y ? 2 : 0) + (z ? 1 : 0)).add(obj);
      return 1;
   }

   private static void dumbCollide(HashMap<GravObject, Set<GravObject>> collisions, List<GravObject> objects) {
      for (int i = 0; i < objects.size(); i++) {
         for (int j = i + 1; j < objects.size(); j++) {
            GravObject a = objects.get(i);
            GravObject b = objects.get(j);
            if (distance(a, b) < a.radius + b.radius) {
               Set<GravObject> aGroup = collisions.get(a);
               Set<GravObject> bGroup = collisions.get(b);
               if (aGroup == null && bGroup == null) {
                  Set<GravObject> newGroup = new HashSet<>();
                  newGroup.add(a);
                  newGroup.add(b);
                  collisions.put(a, newGroup);
                  collisions.put(b, newGroup);
               } else if (aGroup == null && bGroup != null) {
                  bGroup.add(a);
                  collisions.put(a, bGroup);
               } else if (aGroup != null && bGroup == null) {
                  aGroup.add(b);
                  collisions.put(b, aGroup);
               } else if (!aGroup.contains(b)) {
                  aGroup.addAll(bGroup);
                  for (GravObject obj : bGroup) {
                     collisions.put(obj, aGroup);
                  }
               }
            }
         }
      }
   }

   private static Vector findMiddle(List<GravObject> objects) {
      double x = 0.0;
      double y = 0.0;
      double z = 0.0;
      for (GravObject obj : objects) {
         x += obj.location.x;
         y += obj.location.y;
         z += obj.location.z;
      }
      double count = objects.size();
      return new Vector(x / count, y / count, z / count);
   }

   public static double distance(GravObject a, GravObject b) {
      return Vector.distance(a.location, b.location);
   }
}