package utils;

public class Quaternion {
   public final double r, i, j, k;
   
   public Quaternion(double r, double i, double j, double k) {
      this.r = r;
      this.i = i;
      this.j = j;
      this.k = k;
   }
   
   public Quaternion(Vector v) {
      this.r = 0;
      this.i = v.x;
      this.j = v.y;
      this.k = v.z;
   }
   
   public Quaternion(Vector v, double theta) {
      double halfSin = Math.sin(theta / 2);
      r = Math.cos(theta / 2);
      i = halfSin * v.x;
      j = halfSin * v.y;
      k = halfSin * v.z;
   }
   
   public Vector vector() {
      return new Vector(i,j,k);
   }
   
   public Quaternion invert() {
      return new Quaternion(
            r,
            -i,
            -j,
            -k);
   }
   
   public double abs() {
      return Math.sqrt(r * r + i * i + j * j + k * k);
   }
   
   public static Quaternion cross(Quaternion a, Quaternion b) {
      return new Quaternion(
            a.r*b.r - a.i*b.i - a.j*b.j - a.k*b.k,
            a.r*b.i + a.i*b.r + a.j*b.k - a.k*b.j,
            a.r*b.j - a.i*b.k + a.j*b.r + a.k*b.i,
            a.r*b.k + a.i*b.j - a.j*b.i + a.k*b.r);
   }
   
   public static Quaternion sum(Quaternion a, Quaternion b) {
      return new Quaternion(
            a.r + b.r,
            a.i + b.i,
            a.j + b.j,
            a.k + b.k);
   }
   
   public static Quaternion difference(Quaternion a, Quaternion b) {
      return new Quaternion(
            a.r - b.r,
            a.i - b.i,
            a.j - b.j,
            a.k - b.k);
   }
   
   public static Quaternion scale(Quaternion a, double b) {
      return new Quaternion(
            a.r * b,
            a.i * b,
            a.j * b,
            a.k * b);
   }
   
   public static double abs(Quaternion a) {
      return Math.sqrt(a.r * a.r + a.i * a.i + a.j * a.j + a.k * a.k);
   }
   
   public static double dot(Quaternion a, Quaternion b) {
      return a.r * b.r + a.i * b.i + a.j * b.j + a.k * b.k;
   }
   
   public static Quaternion unit(Quaternion a) {
      return scale(a, 1 / abs(a));
   }
   
   public static Quaternion invert(Quaternion a) {
      return new Quaternion(
            a.r,
            -a.i,
            -a.j,
            -a.k);
   }
   
   public static Quaternion rotate(Vector a, Quaternion b) {
      Quaternion q = new Quaternion(a);
      return cross(cross(b, q), invert(b));
   }
   
   public static Vector rotate(Vector a, Vector b, double theta) {
      Quaternion rotation = new Quaternion(b, theta);
      return rotate(a, rotation).vector();
   }
   
   public static void main(String[] args) {
      Vector a = new Vector(2,0,0);
      Vector b = Vector.unit(new Vector(1,1,1));
      Vector c = rotate(a, b, Math.PI);
      System.out.println(c);
   }
   
   @Override
   public String toString() {
      return r + " + " + i + "i + " + j + "j + " + k + "k";
   }
}