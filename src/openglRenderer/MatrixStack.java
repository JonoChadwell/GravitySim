package openglRenderer;

import java.util.Stack;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class MatrixStack {
   private Stack<Matrix4f> mstack;

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

   public void loadMatrix(Matrix4f mat) {
      Matrix4f top = mstack.peek();
      top.load(mat);
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
      M.m31 = -(top + bottom) / (top - bottom);
      M.m32 = -(zFar + zNear) / (zFar - zNear);
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
      M.m32 = -(2.0f * zFar * zNear) / (zFar - zNear);
      M.m23 = -1.0f;
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
      M.m10 = 0.0f;
      M.m20 = a;
      M.m30 = 0.0f;
      M.m01 = 0.0f;
      M.m11 = y;
      M.m21 = b;
      M.m31 = 0.0f;
      M.m02 = 0.0f;
      M.m12 = 0.0f;
      M.m22 = c;
      M.m32 = d;
      M.m03 = 0.0f;
      M.m13 = 0.0f;
      M.m23 = -1.0f;
      M.m33 = 0.0f;
   }

   public Matrix4f topMatrix() {
      return mstack.peek();
   }

   public static void printMat(Matrix4f mat) {
      System.out.println("[" + mat.m00 + "," + mat.m10 + "," + mat.m20 + "," + mat.m30 + ",");
      System.out.println(" " + mat.m01 + "," + mat.m11 + "," + mat.m21 + "," + mat.m31 + ",");
      System.out.println(" " + mat.m02 + "," + mat.m12 + "," + mat.m22 + "," + mat.m32 + ",");
      System.out.println(" " + mat.m03 + "," + mat.m13 + "," + mat.m23 + "," + mat.m33 + "]");
   }
}
