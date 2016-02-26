package fancysim;

import java.util.ArrayList;

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
            calcGravitationalAcceleration(go);
            go.velocity = Vector.add(go.velocity, Vector.scale(go.acceleration, part));
        }
        for (GravObject go : objects) {
            go.location = Vector.add(go.location, Vector.scale(go.velocity, part));
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
        double acceleration = GRAVITY * goOther.mass / (dist * dist);
        return Vector.scale(Vector.unit(distance), -acceleration);
    }
    
    private boolean checkIfColliding(GravObject go, GravObject goOther) {
        Vector distance = Vector.difference(go.location, goOther.location);
        double dist = Vector.abs(distance);
        return dist < (go.radius + goOther.radius);
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
    
    private void collision() {
        for (int i = 0; i < objects.size() - 1; i++) {
            for (int j = i + 1; j < objects.size(); j++) {
                if (checkIfColliding(objects.get(i), objects.get(j))) {
                    GravObject go = objects.get(i);
                    GravObject goOther = objects.get(j);
                    objects.remove(go);
                    objects.remove(goOther);
                    objects.add(performCollision(go, goOther));
                    
                    collision();
                    return;
                }
            }
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