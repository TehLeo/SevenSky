/*
 * Copyright (c) 2017, Juraj Papp
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the copyright holder nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package templates.billboards;

import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderContext;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.Renderer;
import com.jme3.renderer.opengl.GLRenderer;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;
import templates.util.MiscUtils;

/**
 *
 * @author Juraj Papp
 */
public class TextureRenderer {
	public FrameBuffer fb;
	public Camera cam;
	public Texture2D tex;

	public TextureRenderer(int maxWidth, int maxHeight) {
		this(maxWidth, maxHeight, 1);
	}
	public TextureRenderer(int maxWidth, int maxHeight, int samples) {
		fb = new FrameBuffer(maxWidth, maxHeight, samples);
		fb.setDepthBuffer(Image.Format.Depth);
		cam = new Camera(maxWidth, maxHeight);
	}
	public TextureRenderer(Texture2D tex, int samples) {
		this.tex = tex;
		fb = new FrameBuffer(tex.getImage().getWidth(), tex.getImage().getHeight(), samples);
		fb.setColorTexture(tex);
		fb.setDepthBuffer(Image.Format.Depth);
		cam = new Camera(fb.getWidth(), fb.getHeight());
	}
	public TextureRenderer(Texture2D tex, boolean depth) {
		this.tex = tex;
		fb = new FrameBuffer(tex.getImage().getWidth(), tex.getImage().getHeight(), 1);
		fb.setColorTexture(tex);
		if(depth) fb.setDepthBuffer(Image.Format.Depth);
		cam = new Camera(fb.getWidth(), fb.getHeight());
	}
	public void setTexture(Texture2D tex) {
		this.tex = tex;
		fb.setColorTexture(tex);
	}
	private FrameBuffer temp;
	public void enableRender(RenderManager rm) {
		Renderer gl = rm.getRenderer();
		RenderContext rc = (RenderContext)MiscUtils.read(gl, "context");
		temp = rc.boundFB;
		
		gl.setFrameBuffer(fb);
	}
	
	public void endRender(RenderManager rm) {
		Renderer gl = rm.getRenderer();
		gl.setFrameBuffer(temp);
	}
	
}
