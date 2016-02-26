/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fancysim;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;

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
   /*
   private static String readFile(String path) {
      try {
         return new String(Files.readAllBytes(Paths.get(path)));
      } catch (IOException ex) {
         throw new RuntimeException(ex);
      }
   }
   
   private void init() {
      glClearColor(0.12f, 0.34f, 0.56f, 1.0f);
      glEnable(GL_DEPTH_TEST);
      
      String vertex = readFile("res/simple_vert.glsl");
      String fragment = readFile("res/simple_frag.glsl");
      
      int prog = glCreateProgram();
      int fragShader = loadAndCompileShader(vertex, GL_VERTEX_SHADER);
      int 
   }
   
   private int loadAndCompileShader(String file, int shaderType)
   {
      int handle = glCreateShader(shaderType);
      glShaderSource(handle, file);
      glCompileShader(handle);
      int shaderStatus = glGetShader(handle, GL_COMPILE_STATUS);
 
      // check whether compilation was successful
      if( shaderStatus == GL11.GL_FALSE)
      {
         throw new IllegalStateException("compilation error for shader ["+filename+"]. Reason: " + glGetShaderInfoLog(handle, 1000));
      }
 
      return handle;
   }
   
   public void run() {
      try {
         Display.setDisplayMode(new DisplayMode(800,600));
         Display.create();
     } catch (LWJGLException e) {
         e.printStackTrace();
         System.exit(0);
     }
      
     // init OpenGL here
      
     while (!Display.isCloseRequested()) {
          
         // render OpenGL here
          
         Display.update();
     }
      
     Display.destroy();
   }

   public static void main(String[] args) {
      new GravitySim().run();
   }*/
}
