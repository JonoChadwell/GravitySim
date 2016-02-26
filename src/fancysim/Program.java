package fancysim;

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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Program {
   private int pid = 0;
   private String vShaderName = "";
   private String fShaderName = "";
   
   private static String readFile(String path) {
      try {
         return new String(Files.readAllBytes(Paths.get(path)));
      } catch (IOException ex) {
         throw new RuntimeException(ex);
      }
   }

   public void setShaderNames(String v, String f) {
      vShaderName = v;
      fShaderName = f;
   }

   public boolean init() {
      int rc;
      
      // Create shader handles
      int VS = glCreateShader(GL_VERTEX_SHADER);
      int FS = glCreateShader(GL_FRAGMENT_SHADER);
      
      // Read shader sources
      String vshader = readFile(vShaderName);
      String fshader = readFile(fShaderName);
      glShaderSource(VS, vshader);
      glShaderSource(FS, fshader);
      
      // Compile vertex shader
      glCompileShader(VS);
      
      // Compile fragment shader
      glCompileShader(FS);
      
      // Create the program and link
      pid = glCreateProgram();
      glAttachShader(pid, VS);
      glAttachShader(pid, FS);
      glLinkProgram(pid);
      
      assert(glGetError() == GL_NO_ERROR);
      return true;
   }
   
/*
public void bind()
{
   glUseProgram(pid);
}

public void unbind()
{
   glUseProgram(0);
}

public void addAttribute(const string &name)
{
   attributes[name] = GLSL::getAttribLocation(pid, name.c_str(), isVerbose());
}

public void addUniform(const string &name)
{
   uniforms[name] = GLSL::getUniformLocation(pid, name.c_str(), isVerbose());
}

int getAttribute(const string &name) const
{
   map<string,GLint>::const_iterator attribute = attributes.find(name.c_str());
   if(attribute == attributes.end()) {
      if(isVerbose()) {
         cout << name << " is not an attribute variable" << endl;
      }
      return 0;
   }
   return attribute->second;
}

GLint Program::getUniform(const string &name) const
{
   map<string,GLint>::const_iterator uniform = uniforms.find(name.c_str());
   if(uniform == uniforms.end()) {
      if(isVerbose()) {
         cout << name << " is not a uniform variable" << endl;
      }
      return 0;
   }
   return uniform->second;
}*/
}
