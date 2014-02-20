package gravitysim;

import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author Jono
 */
public class Camera {

    public Vector location;
    public double hRot;
    public double vRot;
    public double zoom = 1;
    public double objectScale;
    private Vector side;
    private Vector up;
    private Vector point;
    private Sun sun;

    public Camera(Vector _location) {
        location = _location;
    }

    public void draw(GravObject[] objectList, Graphics g, int width, int height) {
        precalcDrawing();
        g.setColor(Color.WHITE);
        g.fillRect(0,0,width,height);
        
        g.setColor(Color.BLACK);
        
        double scale = 2.0 / Math.max(width,height);
        
        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++) {
                Vector lookingAt = Vector.add(point, Vector.scale(up, (h - height /2) * scale),Vector.scale(side, (w - width / 2) * scale));
                Vector lookingDir = Vector.difference(lookingAt,location);
                if (hitsAny(location, lookingDir,objectList)) {
                    g.drawLine(w, h, w, h - 1);
                }
            }
        }
    }
    
    public boolean hitsAny(Vector start, Vector dir, GravObject[] list) {
        for (GravObject go : list) {
            if (hits(start,dir,go) != null) {
                return true;
            }
        }
        return false;
    }
    
    public Vector hits(Vector start, Vector dir, GravObject go) {
        double a = Vector.dot(dir,dir);
        double b = Vector.dot(Vector.scale(Vector.difference(start,go.location), 2),dir);
        double c = Vector.dot(
                Vector.difference(start,go.location),
                Vector.difference(start,go.location))
                - go.radius * go.radius * objectScale * objectScale;
        double discrim = b*b-4*a*c;
        if (discrim == 0) {
            double t = (-b+Math.sqrt(discrim)) / (2 * a);
            if (t > 0) {
                return Vector.add(start,Vector.scale(dir,t));
            }
        }
        else if (discrim > 0) {
            double t1 = (-b + Math.sqrt(discrim)) / (2 * a);
            double t2 = (-b - Math.sqrt(discrim)) / (2 * a);
            if (t1 > 0 && t2 > 0) {
                return Vector.add(start,Vector.scale(dir,Math.min(t1,t2)));
            }
        }
        return null;
    }
            

    private void precalcDrawing() {
        Vector looking = new Vector(
                -Math.cos(hRot) * Math.cos(vRot), 
                Math.sin(hRot) * Math.cos(vRot), 
                -Math.sin(vRot));
        side = new Vector(Math.sin(hRot), Math.cos(hRot), 0);
        up = Vector.unit(Vector.cross(looking, side));
        looking = Vector.scale(Vector.unit(looking),zoom);
        point = Vector.add(location,looking);
    }

    public void moveCamera(Vector amt) {
        location = Vector.add(location,amt);
    }
}

