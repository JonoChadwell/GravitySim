package fancysim;

import java.util.Stack;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class MatrixStack {

   private Stack<Matrix4f> mstack;
   private Matrix4f scratch = new Matrix4f();

   public MatrixStack() {
      mstack = new Stack<>();
      mstack.push(Matrix4f.setIdentity(new Matrix4f()));
   }

   public void pushMatrix() {
      Matrix4f top = mstack.peek();
      mstack.push(top);
   }

   public void popMatrix() {
      mstack.pop();
   }

   public void loadIdentity() {
      Matrix4f top = mstack.peek();
      Matrix4f.setIdentity(top);
   }

   public void translate(Vector3f trans) {
      Matrix4f top = mstack.peek();
      top.translate(trans);
   }

   public void scale(Vector3f scale) {
      Matrix4f top = mstack.peek();
      top.scale(scale);
   }

   public void scale(float s) {
      Matrix4f top = mstack.peek();
      Vector3f scale = new Vector3f();
      scale.set(s, s, s);
      top.scale(scale);
   }

   public void rotate(float angle, Vector3f axis) {
      Matrix4f top = mstack.peek();
      top.rotate(angle, axis);
   }

   public void multMatrix(Matrix4f matrix) {
      Matrix4f top = mstack.pop();
      top = Matrix4f.mul(top, matrix, top);
   }

   public void ortho(float left, float right, float bottom, float top, float zNear, float zFar) {
      // Sets the top of the stack
      Matrix4f M = mstack.peek();
      Matrix4f.setZero(M);
      M.m00 = 2.0f / (right - left);
      M.m11 = 2.0f / (top - bottom);
      M.m22 = -2.0f / (zFar - zNear);
      M.m33 = -(right + left) / (right - left);
      M.m13 = -(top + bottom) / (top - bottom);
      M.m23 = -(zFar + zNear) / (zFar - zNear);
      M.m33 = 1.0f;
   }

   public void ortho2D(float left, float right, float bottom, float top) {
      ortho(left, right, bottom, top, -1.0f, 1.0f);
   }

   public void perspective(float fovy, float aspect, float zNear, float zFar) {
      // Sets the top of the stack
      Matrix4f M = mstack.peek();
      Matrix4f.setZero(M);
      float tanHalfFovy = (float) Math.tan(0.5f * fovy * Math.PI / 180.0f);
      M.m00 = 1.0f / (aspect * tanHalfFovy);
      M.m11 = 1.0f / (tanHalfFovy);
      M.m22 = -(zFar + zNear) / (zFar - zNear);
      M.m23 = -(2.0f * zFar * zNear) / (zFar - zNear);
      M.m32 = -1.0f;
   }

   public void frustum(float left, float right, float bottom, float top, float nearval, float farval) {
      // http://cgit.freedesktop.org/mesa/mesa/tree/src/mesa/math/m_matrix.c
      float x, y, a, b, c, d;
      x = (2.0f * nearval) / (right - left);
      y = (2.0f * nearval) / (top - bottom);
      a = (right + left) / (right - left);
      b = (top + bottom) / (top - bottom);
      c = -(farval + nearval) / (farval - nearval);
      d = -(2.0f * farval * nearval) / (farval - nearval);

      // Sets the top of the stack
      Matrix4f M = mstack.peek();
      M.m00 = x;
      M.m01 = 0.0f;
      M.m02 = a;
      M.m03 = 0.0f;
      M.m10 = 0.0f;
      M.m11 = y;
      M.m12 = b;
      M.m13 = 0.0f;
      M.m20 = 0.0f;
      M.m21 = 0.0f;
      M.m22 = c;
      M.m23 = d;
      M.m30 = 0.0f;
      M.m31 = 0.0f;
      M.m32 = -1.0f;
      M.m33 = 0.0f;
   }

   private Vector3f x = new Vector3f();
   private Vector3f y = new Vector3f();
   private Vector3f z = new Vector3f();

   public void lookAt(Vector3f eye, Vector3f center, Vector3f up) {
      // http://cgit.freedesktop.org/mesa/mesa/tree/src/glu/mesa/glu.c?h=mesa_3_2_dev
      Vector3f.sub(eye, center, z).normalise(z);
      Vector3f.cross(up, z, x);
      Vector3f.cross(z, x, y);
      x.normalise();
      y.normalise();
      Matrix4f.setZero(scratch);
      Matrix4f M = scratch;
      M.m00 = x.x;
      M.m01 = x.y;
      M.m02 = x.z;
      M.m00 = y.x;
      M.m01 = y.y;
      M.m02 = y.z;
      M.m00 = z.x;
      M.m01 = z.y;
      M.m02 = z.z;
      M.m33 = 1.0f;
      multMatrix(M);
      z.set(eye);
      eye.scale(-1);
      translate(z);
   }

   public Matrix4f topMatrix() {
      return mstack.peek();
   }

   private static void printMat(Matrix4f mat) {
      System.out.println("[" + mat.m00 + "," + mat.m01 + "," + mat.m02 + "," + mat.m03 + ",");
      System.out.println(" " + mat.m10 + "," + mat.m11 + "," + mat.m12 + "," + mat.m13 + ",");
      System.out.println(" " + mat.m20 + "," + mat.m21 + "," + mat.m22 + "," + mat.m23 + ",");
      System.out.println(" " + mat.m30 + "," + mat.m31 + "," + mat.m32 + "," + mat.m33 + "]");
   }
}
