package gravitysim;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

/**
 *
 * @author Jono
 */
public class Camera {

    public Vector location;
    public double hRot;
    public double vRot;
    public double zoom = 1000;
    public double perspectiveDistance = 1.0;
    public double objectScale = 51;
    public boolean isOrtho = false;
    public boolean drawLabels = true;
    private double screenX;
    private double screenY;
    private Vector normal = new Vector();
    private GOholder[] drawList;
    private Vector lateral = new Vector();
    private Vector horizontal = new Vector();
    private ArrayList<GOholder> suns;
    private Vector mouse = new Vector(-100,-100,0);
    private GOholder nearestToMouse;
    private GravObject following;

    public Camera(Vector _location) {
        location = _location;
    }

    public void draw(GravObject[] objectList, Graphics g, int x, int y) {
        if (following != null)
            location = Vector.difference(new Vector(), following.location);
        precalcDrawing();
        drawList = new GOholder[objectList.length];
        suns = new ArrayList<>();
        for (int i = 0; i < objectList.length; i++) {
            GOholder h = new GOholder(objectList[i]);
            h.location = Vector.add(h.location, location);
            h.screenLocation = new Vector();
            h.screenLocation.z = distance(h.location);
            drawList[i] = h;
            if (h.isSun) {
                suns.add(h);
                calcBasicCoords(h);
                scaleForDistance(h);
            }
        }
        quickSort(drawList, 0, drawList.length - 1);
        for (GOholder gh : drawList) {
            if (gh.screenLocation.z > 0.001) { //.001 to avoid glitches with following objects and rounding errors
                calcBasicCoords(gh);
                if (!isOrtho) {
                    scaleForDistance(gh);
                }
                //fill object
                g.setColor(Color.black);
                g.fillOval(
                        (int) (gh.screenLocation.x - gh.screenRadius) + x,
                        (int) (gh.screenLocation.y - gh.screenRadius) + y,
                        (int) (gh.screenRadius * 2),
                        (int) (gh.screenRadius * 2));
                //draw circle around object
                g.setColor(gh.drawColor);
                g.drawOval(
                        (int) (gh.screenLocation.x - gh.screenRadius) + x,
                        (int) (gh.screenLocation.y - gh.screenRadius) + y,
                        (int) (gh.screenRadius * 2),
                        (int) (gh.screenRadius * 2));
                //draw labal
                if (drawLabels && gh.name != null){
                    g.setColor(Color.red);
                    g.drawString(gh.name, (int) (gh.screenLocation.x + gh.screenRadius) + 5 + x, (int) gh.screenLocation.y + y);
                }
            }
            if (gh.isSun) {
                g.setColor(gh.drawColor);
                g.fillOval(
                        (int) (gh.screenLocation.x - gh.screenRadius) + x,
                        (int) (gh.screenLocation.y - gh.screenRadius) + y,
                        (int) (gh.screenRadius * 2),
                        (int) (gh.screenRadius * 2));
            } else {
                for (GOholder s : suns) {
                    //drawSunArc(gh, s, g, x, y);
                }
            }
        }
        //calculate neaest to mouse
        boolean found = false;
        double smallestDifference = 100;
        for (GOholder gh : drawList)
        {
            double thisDifference = 
                    (gh.screenLocation.x + x - mouse.x) * (gh.screenLocation.x + x - mouse.x) + 
                    (gh.screenLocation.y + y - mouse.y) * (gh.screenLocation.y + y - mouse.y);
            if (thisDifference < smallestDifference){
                smallestDifference = thisDifference;
                found = true;
                nearestToMouse = gh;
            }
        }
        if (!found)
            nearestToMouse = null;
    }

    private void calcBasicCoords(GOholder gh) {
        gh.screenRadius = gh.radius * zoom * objectScale;
        gh.screenLocation.x = -Vector.dot(gh.location, lateral) / Vector.dot(lateral, lateral) * zoom + screenX;
        gh.screenLocation.y = -Vector.dot(gh.location, horizontal) / Vector.dot(horizontal, horizontal) * zoom + screenY;
    }

    private void scaleForDistance(GOholder gh) {
        gh.screenRadius = gh.screenRadius / (gh.screenLocation.z / perspectiveDistance);
        gh.screenLocation.x = gh.screenLocation.x / (gh.screenLocation.z / perspectiveDistance);
        gh.screenLocation.y = gh.screenLocation.y / (gh.screenLocation.z / perspectiveDistance);
    }

    private double distance(Vector Point) {
        return Vector.dot(Point, normal);
    }

    private void precalcDrawing() {
        normal = new Vector(-Math.cos(hRot) * Math.cos(vRot), Math.sin(hRot) * Math.cos(vRot), -Math.sin(vRot));
        lateral = new Vector(Math.sin(hRot), Math.cos(hRot), 0);
        horizontal = Vector.cross(normal, lateral);
    }

    private static void quickSort(GOholder arr[], int left, int right) {
        //modified from code found online at stackOverflow.com
        int i = left, j = right;
        GOholder tmp;
        double pivot = arr[(left + right) / 2].screenLocation.z;

        /* partition */
        while (i <= j) {
            while (arr[i].screenLocation.z > pivot) {
                i++;
            }
            while (arr[j].screenLocation.z < pivot) {
                j--;
            }
            if (i <= j) {
                tmp = arr[i];
                arr[i] = arr[j];
                arr[j] = tmp;
                i++;
                j--;
            }
        }
        /* recursion */
        if (left < j) {
            quickSort(arr, left, j);
        }
        if (i < right) {
            quickSort(arr, i, right);
        }
    }

    public void moveCamera(Vector amt) {
        location = Vector.add(Vector.scale(lateral, amt.x), location);
        location = Vector.add(Vector.scale(normal, amt.y), location);
        location = Vector.add(Vector.scale(horizontal, amt.z), location);
    }

    public void drawSunArc(GOholder gh, GOholder s, Graphics g, int x, int y) {
        //bugged
        double cosScreenAngle = Math.sin(Math.atan2(gh.screenLocation.x - s.screenLocation.x, gh.screenLocation.y - s.screenLocation.y));
        double sinScreenAngle = Math.cos(Math.atan2(gh.screenLocation.x - s.screenLocation.x, gh.screenLocation.y - s.screenLocation.y));
        double cosRealAngle = Vector.dot(Vector.difference(gh.location, s.location), normal) / Vector.abs(Vector.difference(gh.location, s.location)) / Vector.abs(normal);
        int points = 5;
        double[] xPoints1 = new double[points * 2];
        double[] yPoints1 = new double[points * 2];
        for (int i = 0; i < points; i++) {
            int j = i;
            double ang = Math.PI / points * j;
            xPoints1[i] = -Math.sin(ang) * gh.screenRadius;
            yPoints1[i] = Math.cos(ang) * gh.screenRadius;
        }
        for (int i = 2 * points - 1; i > points - 1; i--) {
            int j = i - points;
            double ang = Math.PI / points * j;
            xPoints1[i] = -Math.sin(ang) * gh.screenRadius * cosRealAngle;
            yPoints1[i] = -Math.cos(ang) * gh.screenRadius;
        }
        int[] xPoints2 = new int[points * 2];
        int[] yPoints2 = new int[points * 2];
        for (int i = 0; i < points * 2; i++) {
            xPoints2[i] = (int) (xPoints1[i] * cosScreenAngle + yPoints1[i] * -sinScreenAngle + gh.screenLocation.x + x);
            yPoints2[i] = (int) (xPoints1[i] * sinScreenAngle + yPoints1[i] * cosScreenAngle + gh.screenLocation.y + y);
        }
        g.setColor(new Color(s.drawColor.getRed() * 2 / 3, s.drawColor.getBlue() * 2 / 3,0));// s.drawColor.getGreen() * 2 / 3));
        g.fillPolygon(xPoints2, yPoints2, points * 2);
    }
    
    public void setMouseLocation(Vector v){
        mouse = v;
    }
    
    public GOholder getNearestToMouse()
    {
        return nearestToMouse;
    }
    
    public void setFollowing(GravObject go){
        following = go;
    }
    
    public GravObject getFollowing(){
        return following;
    }
}

