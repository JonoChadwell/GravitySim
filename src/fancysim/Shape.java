package fancysim;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.lwjgl.BufferUtils;

import utils.Triangle;
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

public class Shape {

   private int eleBufID = 0;
   private int posBufID = 0;
   private int norBufID = 0;
   private int vaoID = 0;

   public List<Vector> verticies = new ArrayList<>();
   public List<Vector> normals = new ArrayList<>();
   public List<Triangle> triangles = new ArrayList<>();

   public Shape(String filename) {
      File file = new File(filename);
      try {
         Scanner scanner = new Scanner(file);
         while (scanner.hasNextLine()) {
            String s = scanner.nextLine();
            if (s.startsWith("v")) {
               Scanner line = new Scanner(s);
               line.next();
               double y = line.nextDouble();
               double z = line.nextDouble();
               double x = line.nextDouble();
               verticies.add(new Vector(x, y, z));
               line.close();
            } else if (s.startsWith("f")) {
               Scanner line = new Scanner(s);
               line.next();
               triangles.add(new Triangle(line.nextInt() - 1,
                     line.nextInt() - 1, line.nextInt() - 1));
               line.close();
            }
         }
         scanner.close();
      } catch (Exception e) {
         throw new RuntimeException("Input File Issue", e);
      }
   }

   public void rescale() {
      double max = Double.MIN_VALUE;
      double min = Double.MAX_VALUE;
      for (Vector v : verticies) {
         if (v.x > max)
            max = v.x;
         if (v.y > max)
            max = v.y;
         if (v.z > max)
            max = v.z;

         if (v.x < min)
            min = v.x;
         if (v.y < min)
            min = v.y;
         if (v.z < min)
            min = v.z;
      }
      List<Vector> newVerticies = new ArrayList<>(verticies.size());
      for (Vector v : verticies) {
         newVerticies.add(new Vector((v.x - min) / (max - min) * 2 - 1,
               (v.y - min) / (max - min) * 2 - 1, (v.z - min) / (max - min) * 2
                     - 1));
      }
      verticies = newVerticies;
   }

   public void computeNormals() {
      normals = new ArrayList<>();
      float[] norBuf = new float[verticies.size() * 3];
      for (int i = 0; i < norBuf.length; i++) {
         norBuf[i] = 0.0f;
      }
      for (Triangle t : triangles) {
         Vector a = verticies.get(t.a);
         Vector b = verticies.get(t.b);
         Vector c = verticies.get(t.c);

         Vector normal = Vector.unit(Vector.cross(Vector.difference(a, b),
               Vector.difference(a, c)));
         norBuf[t.a + 0] += normal.x;
         norBuf[t.a + 1] += normal.y;
         norBuf[t.a + 2] += normal.z;
         norBuf[t.b + 0] += normal.x;
         norBuf[t.b + 1] += normal.y;
         norBuf[t.b + 2] += normal.z;
         norBuf[t.c + 0] += normal.x;
         norBuf[t.c + 1] += normal.y;
         norBuf[t.c + 2] += normal.z;
      }
      for (int i = 0; i < norBuf.length; i += 3) {
         normals.add(Vector.unit(new Vector(norBuf[i + 0], norBuf[i + 1],
               norBuf[i + 2])));
      }
   }

   public void init() {
      // Initialize the vertex array object
      vaoID = glGenVertexArrays();
      glBindVertexArray(vaoID);

      // Send the position array to the GPU
      posBufID = glGenBuffers();
      glBindBuffer(GL_ARRAY_BUFFER, posBufID);
      FloatBuffer positions = BufferUtils
            .createFloatBuffer(verticies.size() * 3);
      for (Vector v : verticies) {
         positions.put((float) v.x);
         positions.put((float) v.y);
         positions.put((float) v.z);
      }
      positions.flip();
      glBufferData(GL_ARRAY_BUFFER, positions, GL_STATIC_DRAW);

      // Send the normal array to the GPU
      if (normals.isEmpty()) {
         norBufID = 0;
      } else {
         norBufID = glGenBuffers();
         glBindBuffer(GL_ARRAY_BUFFER, norBufID);
         FloatBuffer normBuffer = BufferUtils.createFloatBuffer(normals.size() * 3);
         for (Vector v : normals) {
            normBuffer.put((float) v.x);
            normBuffer.put((float) v.y);
            normBuffer.put((float) v.z);
         }
         normBuffer.flip();
         glBufferData(GL_ARRAY_BUFFER, normBuffer, GL_STATIC_DRAW);
      }

      // Send the element array to the GPU
      eleBufID = glGenBuffers();
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eleBufID);
      IntBuffer eleBuffer = BufferUtils.createIntBuffer(triangles.size() * 3);
      for (Triangle t : triangles) {
         eleBuffer.put(t.a);
         eleBuffer.put(t.b);
         eleBuffer.put(t.c);
      }
      eleBuffer.flip();
      glBufferData(GL_ELEMENT_ARRAY_BUFFER, eleBuffer, GL_STATIC_DRAW);

      // Unbind the arrays
      glBindBuffer(GL_ARRAY_BUFFER, 0);
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
   }

   public void draw(Program prog) {
      int h_pos, h_nor, h_tex;
      h_pos = h_nor = h_tex = -1;

      glBindVertexArray(vaoID);
      // Bind position buffer
      h_pos = prog.getAttribute("vertPos");
      glEnableVertexAttribArray(h_pos);
      glBindBuffer(GL_ARRAY_BUFFER, posBufID);
      glVertexAttribPointer(h_pos, 3, GL_FLOAT, false, 0, 0);
      

      // Bind normal buffer
      h_nor = prog.getAttribute("vertNor");
      if (h_nor != -1 && norBufID != 0) {
         glEnableVertexAttribArray(h_nor);
         glBindBuffer(GL_ARRAY_BUFFER, norBufID);
         glVertexAttribPointer(h_nor, 3, GL_FLOAT, false, 0, 0);
      }

      // Bind element buffer
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eleBufID);
      
      // Draw
      glDrawElements(GL_TRIANGLES, triangles.size() * 3, GL_UNSIGNED_INT, 0);

      // Disable and unbind
      if (h_tex != -1) {
         glDisableVertexAttribArray(h_tex);
      }
      if (h_nor != -1) {
         glDisableVertexAttribArray(h_nor);
      }
      glDisableVertexAttribArray(h_pos);
      glBindBuffer(GL_ARRAY_BUFFER, 0);
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
   }
   
   private void printBuffer(FloatBuffer buff) {
      buff.mark();
      while (buff.hasRemaining()) {
         System.out.print(buff.get() + ", ");
      }
      buff.reset();
      System.out.println();
   }
   
   private void printBuffer(IntBuffer buff) {
      buff.mark();
      while (buff.hasRemaining()) {
         System.out.print(buff.get() + ", ");
      }
      buff.reset();
      System.out.println();
   }
}
