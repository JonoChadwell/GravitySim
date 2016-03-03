package gravitysim;

import java.awt.Color;

import utils.Vector;

/**
 *
 * @author Jono
 */
public class GravObject {
   public Vector location;
   public Vector velocity;
   public Vector acceleration;
   public Color color = Color.gray;
   public double mass;
   public double radius;
   public String name;
   
   public GravObject(GravObject other) {
      this.location = other.location;
      this.velocity = other.velocity;
      this.acceleration = other.acceleration;
      this.color = other.color;
      this.mass = other.mass;
      this.radius = other.radius;
      this.name = other.name;
   }

   public GravObject(Vector location, double mass) {
      this.location = location;
      this.mass = mass;
      this.velocity = new Vector();
      this.acceleration = new Vector();
      this.radius = Math.pow(mass, 1.0 / 3) / 1000;
   }

   public GravObject(Vector location, Vector velocity, double mass) {
      this.location = location;
      this.mass = mass;
      this.velocity = velocity;
      this.acceleration = new Vector();
      this.radius = Math.pow(mass, 1.0 / 3) / 1000;
   }

   public GravObject(Vector location, Vector velocity, double mass, double radius) {
      this.location = location;
      this.mass = mass;
      this.velocity = velocity;
      this.acceleration = new Vector();
      this.radius = radius;
   }

   public GravObject(Vector location, Vector velocity, double mass, double radius, String name) {
      this.location = location;
      this.mass = mass;
      this.velocity = velocity;
      this.acceleration = new Vector();
      this.radius = radius;
      this.name = name;
   }

   @Override
   public String toString() {
      return "Location: " + location + "\nvelocity: " + velocity + "\nMass: " + mass + "\nRadius: " + radius;
   }
}
