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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.FutureTask;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import utils.ObjectFile;
import utils.Vector;
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

   private Shape tail;
   private Shape sphere;
   private Program prog;
   private Vector eye = new Vector(-2,20,0);
   private Vector up = new Vector(0,-1,0);
   private double phi = -1.3962;
   private double theta = 0.0;
   
   private double spinSpeed = 0.001;
   private double moveSpeed = 0.05;
   private double phiBound = 1.39626;
   private double objectScale = 1.0;
   private double timePassed = 0.0;
   
   private static class Tail {
      public Vector forwards;
      public Vector toCam;
      public GravObject source;
      public double z;
   }

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
      
      glEnable(GL_BLEND);
      glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

      String vertex = readFile("res/simple_vert.glsl");
      String fragment = readFile("res/simple_frag.glsl");

      String file = "res/tail.obj";
      tail = new Shape(file);
      tail.rescale();
      tail.computeNormals();
      tail.init();
      
      file = "res/sphere.obj";
      sphere = new Shape(file);
      sphere.rescale();
      sphere.computeNormals();
      sphere.init();

      prog = new Program();
      prog.setShaderNames("res/simple_vert.glsl", "res/simple_frag.glsl");
      prog.init();
      prog.addUniform("P");
      prog.addUniform("MV");
      prog.addUniform("V");
      prog.addUniform("MatAmb");
      prog.addUniform("MatDif");
      prog.addUniform("MatShnOrScale");
      prog.addUniform("MatSpc");
      prog.addUniform("LightPos");
      prog.addUniform("EyePosOrTailPos");
      prog.addUniform("LightColor");
      prog.addUniform("Opacity");
      prog.addUniform("Mode");
      prog.addAttribute("vertPos");
      prog.addAttribute("vertNor");
      prog.addAttribute("vertTex");
   }

   private Vector3f scratch = new Vector3f();

   private Vector3f conv(Vector v) {
      scratch.set((float) v.x, (float) v.y, (float) v.z);
      return scratch;
   }
   
   private Vector3f conv(double x, double y, double z) {
      scratch.set((float) x, (float) y, (float) z);
      return scratch;
   }

   
   
   private void setOpacity(float f) {
      glUniform1f(prog.getUniform("Opacity"), f);
   }
   
   private static final int NORMAL = 0;
   private static final int TAILS = 1;
   private void setMode(int i) {
      glUniform1i(prog.getUniform("Mode"), i);
   }

   private static final int BLUE = 0;
   private static final int GREY = 1;
   private static final int BRASS = 2;
   private static final int COPPER = 3;
   private static final int SUN = 4;

   private void setMaterial(int i) {

      switch (i) {
      case 0: // shiny blue plastic
         glUniform3f(prog.getUniform("MatAmb"), 0.02f, 0.04f, 0.2f);
         glUniform3f(prog.getUniform("MatDif"), 0.0f, 0.16f, 0.9f);
         glUniform3f(prog.getUniform("MatSpc"), 0.14f, 0.2f, 0.8f);
         glUniform1f(prog.getUniform("MatShnOrScale"), 120.0f);
         break;
      case 1: // flat grey
         glUniform3f(prog.getUniform("MatAmb"), 0,0,0);
         glUniform3f(prog.getUniform("MatDif"), 0,0,0);
         glUniform3f(prog.getUniform("MatSpc"), 0,0,0);
         glUniform1f(prog.getUniform("MatShnOrScale"), 4.0f);
         break;
      case 2: // brass
         glUniform3f(prog.getUniform("MatAmb"), 0.3294f, 0.2235f, 0.02745f);
         glUniform3f(prog.getUniform("MatDif"), 0.7804f, 0.5686f, 0.11373f);
         glUniform3f(prog.getUniform("MatSpc"), 0.9922f, 0.941176f, 0.80784f);
         glUniform1f(prog.getUniform("MatShnOrScale"), 27.9f);
         break;
      case 3: // copper
         glUniform3f(prog.getUniform("MatAmb"), 0.1913f, 0.0735f, 0.0225f);
         glUniform3f(prog.getUniform("MatDif"), 0.7038f, 0.27048f, 0.0828f);
         glUniform3f(prog.getUniform("MatSpc"), 0.257f, 0.1376f, 0.08601f);
         glUniform1f(prog.getUniform("MatShnOrScale"), 12.8f);
         break;
      case 4: // Sun
         glUniform3f(prog.getUniform("MatAmb"), 2.0f, 1.8f, 1.5f);
         glUniform3f(prog.getUniform("MatDif"), 0, 0, 0);
         glUniform3f(prog.getUniform("MatSpc"), 0, 0, 0);
         glUniform1f(prog.getUniform("MatShnOrScale"), 12.8f);
      }
   }
  
   private void lookDirection(MatrixStack ms, Vector zaxis) {
      zaxis = Vector.scale(zaxis, -1);
      Vector xaxis = Vector.unit(Vector.cross(up, zaxis));
      Vector yaxis = Vector.unit(Vector.cross(xaxis, zaxis));
      
      Matrix4f mat = new Matrix4f();
      mat.m00 = (float) xaxis.x;
      mat.m10 = (float) xaxis.y;
      mat.m20 = (float) xaxis.z;
      mat.m30 = (float) (-Vector.dot(xaxis, eye));
      
      mat.m01 = (float) yaxis.x;
      mat.m11 = (float) yaxis.y;
      mat.m21 = (float) yaxis.z;
      mat.m31 = (float) (-Vector.dot(yaxis, eye));
      
      mat.m02 = (float) zaxis.x;
      mat.m12 = (float) zaxis.y;
      mat.m22 = (float) zaxis.z;
      mat.m32 = (float) (-Vector.dot(zaxis, eye));
      
      mat.m03 = 0;
      mat.m13 = 0;
      mat.m23 = 0;
      mat.m33 = 1;
      
      ms.loadMatrix(mat);
   }
   
   private void updateCameraPosition() {
      
      if (Display.isActive() && Mouse.isButtonDown(0)) {
         theta -= Mouse.getDX() * spinSpeed;
         phi += Mouse.getDY() * spinSpeed;
         if (phi > phiBound) {
            phi = phiBound;
         } else if (phi < -phiBound) {
            phi = -phiBound;
         }
         Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
         Mouse.setGrabbed(true);
         
      } else {
         Mouse.setGrabbed(false);
      }
      
      double x = Math.cos(phi) * Math.cos(theta);
      double y = Math.sin(phi);
      double z = Math.cos(phi) * Math.cos(Math.PI / 2 - theta);
      
      Vector zaxis = new Vector(x,y,z);
      Vector xaxis = Vector.unit(Vector.cross(up, zaxis));
      Vector yaxis = Vector.unit(Vector.cross(xaxis, zaxis));
      
      if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
         eye = Vector.add(eye, Vector.scale(zaxis, moveSpeed));
      }
      if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
         eye = Vector.add(eye, Vector.scale(zaxis, -moveSpeed));
      }
      if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
         eye = Vector.add(eye, Vector.scale(xaxis, moveSpeed));
      }
      if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
         eye = Vector.add(eye, Vector.scale(xaxis, -moveSpeed));
      }
      if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
         eye = Vector.add(eye, Vector.scale(yaxis, moveSpeed));
      }
      if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
         eye = Vector.add(eye, Vector.scale(yaxis, -moveSpeed));
      }
   }

   private void render(List<GravObject> objs) {
      // Get current frame buffer size.
      int width = Display.getWidth();
      int height = Display.getHeight();
      GravObject sun = objs.stream().max((a, b) -> Double.compare(a.radius, b.radius)).get();
      
//      double dist = 0;
//      for (GravObject obj : objs) {
//         if (obj != sun) {
//            dist += Vector.distance(sun.location, obj.location);
//         }
//      }
//      System.out.println("Average Distance: " + dist / objs.size());

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
      P.perspective(50.0f, aspect, 0.01f, 200.0f);

      // Apply camera projection
      V.pushMatrix();
      V.loadIdentity();
      
      double x = Math.cos(phi) * Math.cos(theta);
      double y = Math.sin(phi);
      double z = Math.cos(phi) * Math.cos(Math.PI / 2 - theta);
      Vector direction = new Vector(x,y,z);
      lookDirection(V, direction);

      prog.bind();
      setMatrix("P", P);
      setMatrix("V", V);
      
      glUniform3f(prog.getUniform("LightPos"), (float) sun.location.x, (float) sun.location.y, (float) sun.location.z);
      glUniform3f(prog.getUniform("LightColor"), 1, 1, 1);
      glUniform3f(prog.getUniform("EyePosOrTailPos"), (float) eye.x, (float) eye.y, (float) eye.z);

      MV.pushMatrix();
      
      //draw sun
      setOpacity(1.0f);
      setMode(NORMAL);
      MV.loadIdentity();
      MV.translate(conv(sun.location));
      MV.scale((float) (sun.radius * objectScale));
      setMaterial(SUN);
      setMatrix("MV", MV);
      sphere.draw(prog);
      //Vector sunPos = mult(MV, sun.location);
      
      //draw objects
      setMaterial(COPPER);
      List<Tail> tails = new ArrayList<>();
      for (GravObject obj : objs) {
         if (obj != sun) {
            MV.loadIdentity();
            MV.translate(conv(obj.location));
            MV.scale((float) (obj.radius * objectScale));
            setMatrix("MV", MV);
            sphere.draw(prog);
            
            Vector worldPos = mult(MV, obj.location);
            Tail t = new Tail();
            t.forwards = Vector.unit(Vector.difference(sun.location, worldPos));
            t.toCam = Vector.unit(Vector.difference(new Vector(eye.x, eye.y, eye.z), worldPos));
            t.source = obj;
            t.z = Vector.distance(obj.location, eye);
            tails.add(t);
         }
      }
      
      
      tails.sort((a,b) -> -Double.compare(a.z, b.z));
      setMaterial(GREY);
      
      setMode(TAILS);
      Vector modelUp = new Vector(0,1,0);
      for (Tail t : tails) {
         MV.loadIdentity();
         MV.translate(conv(t.source.location));
         MV.scale((float) (t.source.radius * objectScale));
         double hAngle = Math.atan2(t.forwards.x, t.forwards.z) + Math.PI / 2;
         double vAngle = -Math.atan2(t.forwards.y, Vector.abs(new Vector(t.forwards.x,t.forwards.z, 0)));
         Vector desiredUp = Vector.unit(Vector.cross(t.forwards, t.toCam));
         double LorR = Vector.dot(t.toCam, Vector.cross(Vector.cross(t.forwards, modelUp), t.forwards));
         double rAngle = Math.acos(Vector.dot(modelUp, desiredUp));
         if (LorR > 0) {
            rAngle = -rAngle;
         }
            
         MV.rotate((float) vAngle, conv(Math.sin(hAngle),0,Math.cos(hAngle)));
         MV.rotate((float) hAngle, conv(0,1,0));
         MV.rotate((float) rAngle, conv(1,0,0));
         
         
         MV.scale(conv(200, 8, 1));
         MV.translate(conv(1, 0, 0));
         
         setMatrix("MV", MV);
         glUniform3f(prog.getUniform("EyePosOrTailPos"), (float) t.source.location.x, (float) t.source.location.y, (float) t.source.location.z);
         glUniform1f(prog.getUniform("MatShnOrScale"), (float) t.source.radius * 100f);
         setOpacity((float) Math.sqrt(t.source.radius) / 2f);
         tail.draw(prog);
      }
      
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
      glUniformMatrix4(prog.getUniform(name), false, MatBuffer);
   }
   
   private Scanner s = new Scanner(System.in);
   private void handleInput() throws Exception {
      while (System.in.available() > 0) {
         Scanner line = new Scanner(s.nextLine());
         switch (line.next()) {
         case "sim":
            sim.tickAmount = line.nextDouble();
         }
         line.close();
      }
   }
   
   private SimulationController sim;
   public void run() throws Exception {
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
      
      sim = new SimulationController();
      sim.tickAmount = 1;
      
      List<GravObject> objects = new ArrayList<>();
      for (GravObject obj : sim.sim.getObjects()) {
         objects.add(new GravObject(obj));
      }
      
      FutureTask<List<GravObject>> task = new FutureTask<>(() -> {
         sim.tick();
         return sim.sim.getObjects();
      });
      Thread simulationThread = new Thread(task);
      simulationThread.start();
      while (!Display.isCloseRequested()) {
         if (task.isDone()) {
            objects.clear();
            for (GravObject obj : task.get()) {
               objects.add(new GravObject(obj));
            }
            task = new FutureTask<>(() -> {
               sim.tick();
               //sim.sim.centerMass();
               return sim.sim.getObjects();
            });
            simulationThread = new Thread(task);
            simulationThread.start();
         }
         updateCameraPosition();
         handleInput();
         timePassed = sim.sim.getTicks();
         render(objects);
         Display.sync(60);
         Display.update();
      }

      Display.destroy();
   }
   
   public Vector mult(MatrixStack ms, Vector a) {
      Vector4f annoying = new Vector4f();
      annoying.set((float) a.x, (float) a.y, (float) a.z, 1);
      Matrix4f.transform(ms.topMatrix(), annoying, annoying);
      return new Vector(annoying.x, annoying.y, annoying.z);
   }
   
   public Vector mult(MatrixStack ms, Vector a, float w) {
      Vector4f annoying = new Vector4f();
      annoying.set((float) a.x, (float) a.y, (float) a.z, w);
      Matrix4f.transform(ms.topMatrix(), annoying, annoying);
      return new Vector(annoying.x, annoying.y, annoying.z);
   }

   public static void main(String[] args) throws Exception  {
      new GravitySim().run();
   }
}
