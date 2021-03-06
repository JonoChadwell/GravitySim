package utils;

/**
 *
 * @author Jono
 */
public class Vector {
    public double x,y,z;
    
    public Vector(){
        x = 0;
        y = 0;
        z = 0;
    }
    
    public Vector(double _x, double _y, double _z){
        x = _x;
        y = _y;
        z = _z;
    }
    
    public static double abs(Vector v){
        return Math.sqrt(v.x*v.x + v.y*v.y + v.z*v.z);
    }
    
    public static double dot(Vector i, Vector j){
        return i.x*j.x + i.y*j.y + i.z*j.z;
    }
    
    public static Vector cross(Vector i, Vector j){
        return new Vector(i.y*j.z-i.z*j.y, i.z*j.x-i.x*j.z,i.x*j.y-i.y*j.x);
    }
    
    public static Vector scale(Vector v, double scalar){
        return new Vector(v.x * scalar, v.y * scalar, v.z * scalar);
    }
    
    public static Vector difference(Vector i, Vector j){
        return new Vector(i.x-j.x, i.y-j.y, i.z-j.z);
    }
    
    public static double distance(Vector i, Vector j) {
       return abs(Vector.difference(i,j));
    }
    
    public static Vector add(Vector ... vals) {
       Vector rtn = new Vector();
       for (Vector v : vals) {
          rtn.x += v.x;
          rtn.y += v.y;
          rtn.z += v.z;
       }
       return rtn;
    }
    
    public static Vector unit(Vector v) {
        double abs = abs(v);
        return new Vector(v.x/abs,v.y/abs,v.z/abs);
    }
    
    public static Vector negate(Vector v) {
       return new Vector(-v.x, -v.y, -v.z);
    }
    
    @Override
    public String toString(){
        return "{"+x+", "+y+", "+z+"}";
    }
}
