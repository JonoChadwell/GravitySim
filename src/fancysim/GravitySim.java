/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fancysim;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import utils.ObjectFile;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.opengl.GL41.*;

/**
 *
 * @author Jono
 */
public class GravitySim {

   private Shape bunny;
   private Program prog;

   private static String readFile(String path) {
      try {
         return new String(Files.readAllBytes(Paths.get(path)));
      } catch (IOException ex) {
         throw new RuntimeException(ex);
      }
   }

   private void init() {
      glClearColor(0.22f, 0.22f, 0.22f, 1.0f);
      glEnable(GL_DEPTH_TEST);

      String vertex = readFile("res/simple_vert.glsl");
      String fragment = readFile("res/simple_frag.glsl");

      String file = "res/bunny.obj";
      bunny = new Shape(file);
      bunny.rescale();
      bunny.computeNormals();
      bunny.init();

      prog = new Program();
      prog.setShaderNames("res/simple_vert.glsl", "res/simple_frag.glsl");
      prog.init();
      prog.addUniform("P");
      prog.addUniform("MV");
      prog.addUniform("V");
      prog.addUniform("MatAmb");
      prog.addUniform("MatDif");
      prog.addUniform("MatShn");
      prog.addUniform("MatSpc");
      prog.addUniform("LightPos");
      prog.addUniform("eye");
      prog.addUniform("LightColor");
      prog.addAttribute("vertPos");
      prog.addAttribute("vertNor");
      prog.addAttribute("vertTex");
   }

   private Vector3f scratch = new Vector3f();

   private Vector3f vector(float x, float y, float z) {
      scratch.set(x, y, z);
      return scratch;
   }

   private static final int BLUE = 0;
   private static final int GREY = 1;
   private static final int BRASS = 2;
   private static final int COPPER = 3;

   private void setMaterial(int i) {

      switch (i) {
      case 0: // shiny blue plastic
         glUniform3f(prog.getUniform("MatAmb"), 0.02f, 0.04f, 0.2f);
         glUniform3f(prog.getUniform("MatDif"), 0.0f, 0.16f, 0.9f);
         glUniform3f(prog.getUniform("MatSpc"), 0.14f, 0.2f, 0.8f);
         glUniform1f(prog.getUniform("MatShn"), 120.0f);
         break;
      case 1: // flat grey
         glUniform3f(prog.getUniform("MatAmb"), 0.13f, 0.13f, 0.14f);
         glUniform3f(prog.getUniform("MatDif"), 0.3f, 0.3f, 0.4f);
         glUniform3f(prog.getUniform("MatSpc"), 0.3f, 0.3f, 0.4f);
         glUniform1f(prog.getUniform("MatShn"), 4.0f);
         break;
      case 2: // brass
         glUniform3f(prog.getUniform("MatAmb"), 0.3294f, 0.2235f, 0.02745f);
         glUniform3f(prog.getUniform("MatDif"), 0.7804f, 0.5686f, 0.11373f);
         glUniform3f(prog.getUniform("MatSpc"), 0.9922f, 0.941176f, 0.80784f);
         glUniform1f(prog.getUniform("MatShn"), 27.9f);
         break;
      case 3: // copper
         glUniform3f(prog.getUniform("MatAmb"), 0.1913f, 0.0735f, 0.0225f);
         glUniform3f(prog.getUniform("MatDif"), 0.7038f, 0.27048f, 0.0828f);
         glUniform3f(prog.getUniform("MatSpc"), 0.257f, 0.1376f, 0.08601f);
         glUniform1f(prog.getUniform("MatShn"), 12.8f);
         break;
      }
   }

   private void render() {
      // Get current frame buffer size.
      int width = Display.getWidth();
      int height = Display.getHeight();

      glViewport(0, 0, width, height);

      // Clear framebuffer.
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

      // Use the matrix stack for Lab 6
      float aspect = width / (float) height;

      // Create the matrix stacks - please leave these alone for now
      MatrixStack P = new MatrixStack();
      MatrixStack MV = new MatrixStack();
      MatrixStack V = new MatrixStack();

      // Apply perspective projection.
      P.pushMatrix();
      //P.ortho(-5, 5, -5, 5, -100, 100);
      P.perspective(45.0f, aspect, 0.01f, 100.0f);

      // Apply camera projection
      V.pushMatrix();
      V.loadIdentity();

      prog.bind();
      
      setMatrix("P", P);
      setMatrix("V", V);

      MV.pushMatrix();
      MV.loadIdentity();

      glUniform3f(prog.getUniform("LightPos"), 2, 2, -2);
      glUniform3f(prog.getUniform("LightColor"), 1, 1, 1);
      glUniform3f(prog.getUniform("eye"), 0.0f, 0.0f, 100.0f);

      MV.translate(vector(0, 0, 10));
      setMaterial(BLUE);
      setMatrix("MV", MV);
      bunny.draw(prog);
      
      MV.popMatrix();
      prog.unbind();

      // Pop matrix stacks
      P.popMatrix();
      V.popMatrix();
   }
   
   FloatBuffer MatBuffer = BufferUtils.createFloatBuffer(16);
   private void setMatrix(String name, MatrixStack mat) {
      MatBuffer.clear();
      mat.topMatrix().store(MatBuffer);
      MatBuffer.flip();
      glUniformMatrix4(prog.getUniform(name), true, MatBuffer);
   }

   public void run() {
      try {
         Display.setDisplayMode(new DisplayMode(800, 600));
         Display.setResizable(true);
         Display.create();
      } catch (LWJGLException e) {
         e.printStackTrace();
         System.exit(0);
      }

      // init OpenGL here
      System.out.println("OpenGL version: " + glGetString(GL_VERSION));
      System.out.println("GLSL version: " + glGetString(GL_SHADING_LANGUAGE_VERSION));
      init();

      while (!Display.isCloseRequested()) {

         // render OpenGL here
         render();
         

         Display.sync(60);
         Display.update();
      }

      Display.destroy();
   }

   public static void main(String[] args) {
      new GravitySim().run();
   }
   
   private static void printMat(Matrix4f mat) {
      System.out.println("[" + mat.m00 + "," + mat.m01 + "," + mat.m02 + "," + mat.m03 + ",");
      System.out.println(" " + mat.m10 + "," + mat.m11 + "," + mat.m12 + "," + mat.m13 + ",");
      System.out.println(" " + mat.m20 + "," + mat.m21 + "," + mat.m22 + "," + mat.m23 + ",");
      System.out.println(" " + mat.m30 + "," + mat.m31 + "," + mat.m32 + "," + mat.m33 + "]");
   }
}
