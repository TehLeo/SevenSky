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

import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderContext;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.Renderer;
import com.jme3.renderer.opengl.GLRenderer;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.shader.UniformBindingManager;
import com.jme3.texture.Texture2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import templates.billboards.BillboardTexture.MappedRegion;
import templates.util.MiscUtils;

/**
 *
 * @author Juraj Papp
 */
public class BillboardData {
		public Spatial original;
		public Texture2D view;
		/**(offX, offY, sizeX, sizeY)*/
		public Vector4f textureSlot;
		public Vector4f viewSize;
		public Vector3f extents;
		public Vector3f offset;
		public float height;
		
		public BillboardTexture btex;
		public MappedRegion region;
		
		static Vector3f[] dirs = new Vector3f[] {
			new Vector3f(0,0,-1),
			new Vector3f(-1,0,-1).normalizeLocal(),
			new Vector3f(-1,0,0),
			new Vector3f(-1,0,1).normalizeLocal(),
			Vector3f.UNIT_Z, 
			new Vector3f(1,0,1).normalizeLocal(),
			Vector3f.UNIT_X,
			new Vector3f(1,0,-1).normalizeLocal()
		};
		
		public static BillboardData createBillboard(TextureRenderer tr, RenderManager rm, Spatial sp, int width, int height, BillboardTexture tex, boolean clip2px) {
			MappedRegion rect = tex.aquire(width, height);
			if(rect == null) return null;
			
			int images = 8;
			float imgMult = 1f/images;
			
			Rectangle rr = new Rectangle(rect.rect);
			if(clip2px) {
				rr.x += 1; rr.y += 1;
				rr.width -= 2; rr.height -= 2;
			}
			
			BillboardData data = new BillboardData();
			data.original = sp;
			data.btex = tex;
			data.region = rect;
			data.view = tex.tex;
			data.textureSlot = fromRect(rr.x, rr.y,
					rr.width, rr.height, tex.tex.getImage().getWidth(),
					tex.tex.getImage().getHeight());
			data.textureSlot.z *= imgMult;
//			data.textureSlot.w = 1f;
			
			System.out.println(data.textureSlot);
			
			ArrayList<Geometry> geoms = new ArrayList<Geometry>();
			sp.depthFirstTraversal((sp2)->{if(sp2 instanceof Geometry) geoms.add((Geometry)sp2);});
			
			if(geoms.isEmpty()) {
				//Clean
				System.err.println("Empty spatial");
				return null;
			}
			
			Quaternion originalRot = sp.getLocalRotation().clone();
			sp.setLocalRotation(Quaternion.IDENTITY);
			Vector3f originalScale = sp.getLocalScale().clone();
			sp.setLocalScale(1f, 1f, 1f);
			
			BoundingVolume bv = sp.getWorldBound();
			BoundingBox bb = (BoundingBox)bv;
			Renderer r = rm.getRenderer();
			UniformBindingManager um = MiscUtils.read(rm, "uniformBindingManager");
			Camera originalCam = rm.getCurrentCamera();
						
			tr.enableRender(rm);
			
			r.setViewPort(rr.x, rr.y, rr.width, rr.height);
			r.setClipRect(rr.x, rr.y, rr.width, rr.height);
//			r.setClipRect(rr.x+1, rr.y+1, rr.width-2, rr.height-2);
			
			r.setBackgroundColor(new ColorRGBA(0, 0, 0, 0));
			r.clearBuffers(true, true, true);
			System.out.println("rect "  +rr);
			
			Camera cam = tr.cam;
			cam.setParallelProjection(true);
			
			Vector3f up = Vector3f.UNIT_Y;
			Vector3f extents = bb.getExtent(new Vector3f());
			data.extents = new Vector3f(extents);
			data.offset = bb.getCenter().subtract(sp.getWorldTranslation());
			
			int len = dirs.length;
			
			int halfLen = len>>1;
			Vector2f[] size = new Vector2f[halfLen];
			for(int i = 0; i < halfLen; i++) {
				Vector3f dir = dirs[i];
				Vector3f tmp = dir.cross(up);
				float dotX = FastMath.abs(extents.x*tmp.x)+FastMath.abs(extents.y*tmp.y)+FastMath.abs(extents.z*tmp.z);
				float frustumSize = FastMath.abs(extents.dot(up));
				size[i] = new Vector2f(dotX, frustumSize);
			}
			
			for(int i = 0; i < len; i++) {
				Vector3f dir = dirs[i];
				Vector3f center = new Vector3f(bb.getCenter());
				Vector3f tmp = dir.cross(up);
				
//				float dotX = FastMath.abs(extents.x*tmp.x)+FastMath.abs(extents.y*tmp.y)+FastMath.abs(extents.z*tmp.z);
//				float frustumSize = FastMath.abs(extents.dot(up));

				Vector2f camSize = size[i%halfLen];
				tmp.multLocal(2*camSize.x*(3-i)+camSize.x);
				center.addLocal(tmp);
				cam = new Camera(128*8, 128);
				
				cam.lookAtDirection(dir, Vector3f.UNIT_Y);
				cam.setLocation(center);

				cam.setParallelProjection(true);
				
				float aspect = (float) cam.getWidth() / cam.getHeight();
				cam.setFrustum(-1000, 1000, -aspect * camSize.x, aspect * camSize.x, camSize.y, -camSize.y);
				
				um.setCamera(cam, cam.getViewMatrix(), cam.getProjectionMatrix(), cam.getViewProjectionMatrix());
//				rm.setCamera(cam, false);

				for(int k = 0; k < geoms.size(); k++) {
					rm.renderGeometry(geoms.get(k));
				}
			}
			
			tr.endRender(rm);
			
			sp.setLocalRotation(originalRot);
			sp.setLocalScale(originalScale);
			if(originalCam != null) {
				originalCam.update();
				rm.setCamera(originalCam, originalCam.isParallelProjection());
			}
//			r.setViewPort(0, 0, originalCam.getWidth(), originalCam.getHeight());

			data.viewSize = new Vector4f();
			for(int i = 0; i < size.length; i++) data.viewSize.set(i, size[i].x*2f);
			data.height = size[0].y*2f;

			return data;
		}
		
		public static Vector4f fromRect(int x, int y, int w, int h, int imageW, int imageH) {
			float iw = 1f/imageW, ih = 1f/imageH;
			return new Vector4f(x*iw, y*ih, w*iw, h*ih);
		}
	}