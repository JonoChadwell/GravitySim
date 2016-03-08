package gravitysim;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.Timer;

import utils.Vector;

/**
 *
 * @author Jono
 */
public class SimulationController {

    public Simulation sim = new Simulation();
    private static Random rn = new Random();
    public boolean paused = false;
    public double tickAmount = 1.0;

    public SimulationController() {
       
//       sim.addObjects(SolarObjects.objs);
//       sim.convertAxis();
//       tickAmount = 0.02;
       addTorusRange();
    }

    public void addRandomRange(int count) {
        double startVelocity = .01;
        double avgMass = 200;
        double range = 1;
        for (int i = 0; i < count; i++) {
            sim.addObject(
                  new GravObject(
                        new Vector(
                              getRandom(2 * range) - range,
                              getRandom(2 * range) - range,
                              getRandom(2 * range) - range),
                        new Vector(
                              getRandom(startVelocity) - startVelocity / 2,
                              getRandom(startVelocity) - startVelocity / 2,
                              getRandom(startVelocity) - startVelocity / 2),
                        getRandom(avgMass * 2)));
        }
        sim.centerMass();
    }
    
    public void addTorusRange() {
       int count = 5000;
       
       double sunMass = 332948;
       double velocityDeviation = 0.002;
       double massDeviation = 0.8;
       double baseMass = 1;
       double torusRadius = 1;
       double spread = 0.5;
       double verticalSpreadFactor = 0.2;
       double baseVelocity = .0172;
       double spreadPower = 0.5;
       
       sim.addObject(new GravObject(
             new Vector(0,0,0),
             new Vector(0,0,0),
             sunMass));
       
       for (int i = 0; i < count - 1; i++) {
           double angle = getRandom(Math.PI * 2);
           double phi = getRandom(Math.PI * 2);
           double dist = Math.pow(getRandom(1), spreadPower) * spread;
           double mass = getRandom(massDeviation * 2) - massDeviation + baseMass;
           
           double targetCircularVelocity = baseVelocity * Math.sqrt(torusRadius + -Math.cos(phi) * dist);
           
           Vector circularVelocity = Vector.scale(new Vector(Math.cos(angle), 0, -Math.sin(angle)), targetCircularVelocity);
           Vector deviantVelocity = new Vector(
                 getRandom(velocityDeviation * 2) - velocityDeviation,
                 getRandom(velocityDeviation * 2) - velocityDeviation,
                 getRandom(velocityDeviation * 2) - velocityDeviation);
           Vector finalVelocity = Vector.add(circularVelocity, deviantVelocity);
           
           Vector basePosition = new Vector(Math.sin(angle), 0, Math.cos(angle));
           Vector angularPosition = Vector.scale(basePosition, torusRadius);
           Vector phiPosition = Vector.add(Vector.scale(basePosition, Math.cos(phi) * dist), Vector.scale(new Vector(0,verticalSpreadFactor,0), Math.sin(phi) * dist));
           Vector finalPosition = Vector.add(angularPosition, phiPosition);
           
           sim.addObject(new GravObject(
                 finalPosition,
                 finalVelocity,
                 mass));
       }
       sim.centerMass();
   }

    private double getRandom(double max) {
        return rn.nextDouble() * max;
    }

    public void tick() {
        sim.tickSimulation(tickAmount);
    }
}
