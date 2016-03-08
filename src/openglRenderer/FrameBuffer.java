/**
 * Derivation of:
 * 
 * Copyright (c) 2012, Matt DesLauriers All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer. 
 *
 * * Redistributions in binary
 *   form must reproduce the above copyright notice, this list of conditions and
 *   the following disclaimer in the documentation and/or other materials provided
 *   with the distribution. 
 *
 * * Neither the name of the Matt DesLauriers nor the names
 *   of his contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package openglRenderer;

import static org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glBindFramebufferEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glCheckFramebufferStatusEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glFramebufferTexture2DEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glGenFramebuffersEXT;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL30.glDeleteFramebuffers;
import mdesl.graphics.ITexture;
import mdesl.graphics.Texture;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

public class FrameBuffer implements ITexture {
   private Texture texture;
   private int id;

   public FrameBuffer(int width, int height) throws LWJGLException {
      texture = new Texture(width, height, Texture.DEFAULT_FILTER, Texture.DEFAULT_WRAP);
      texture.bind();
      id = glGenFramebuffersEXT();
      glBindFramebufferEXT(GL_FRAMEBUFFER, id);
      glFramebufferTexture2DEXT(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, texture.getTarget(), texture.getID(), 0);
      int result = glCheckFramebufferStatusEXT(GL_FRAMEBUFFER);
      if (result!=GL_FRAMEBUFFER_COMPLETE) {
         glBindFramebufferEXT(GL_FRAMEBUFFER, 0);
         glDeleteFramebuffers(id);
         throw new LWJGLException("exception "+result+" when checking FBO status");
      }
      glBindFramebufferEXT(GL_FRAMEBUFFER, 0);
   }

   public void begin() {
      if (id == 0)
         throw new IllegalStateException("can't use FBO as it has been destroyed..");
      glViewport(0, 0, getWidth(), getHeight());
      glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, id);
   }

   public void end() {
      if (id==0)
         return;
      glViewport(0, 0, Display.getWidth(), Display.getHeight());
      glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
   }

   @Override
   public float getU() {
      return 0;
   }

   @Override
   public float getV() {
      return 1f;
   }

   @Override
   public float getU2() {
      return 1f;
   }

   @Override
   public float getV2() {
      return 0;
   }

   @Override
   public int getWidth() {
      return texture.getWidth();
   }

   @Override
   public int getHeight() {
      return texture.getHeight();
   }

   @Override
   public Texture getTexture() {
      return texture;
   }
}
