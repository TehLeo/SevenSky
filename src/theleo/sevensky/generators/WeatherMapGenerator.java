/*
 * Copyright (c) 2018, Juraj Papp
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
package theleo.sevensky.generators;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.Renderer;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.mesh.IndexBuffer;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.util.BufferUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.nio.FloatBuffer;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import theleo.sevensky.generators.extra.LookupTable2;
import templates.billboards.TextureRenderer;
import templates.geom.BMesh;
import templates.geom.MeshBuilder;
import templates.glsl.Fragment;
import templates.image.viewer.VectorPanel;
import templates.util.DebugUtils;
import templates.util.Mathf;
import templates.util.MeshUtils;

/**
 *
 * @author Juraj Papp
 */
public class WeatherMapGenerator {
	public static class WeatherMapData {
		public Cloud[] clouds;
		public Geometry cloudGeom;
		public TextureRenderer tr;

		public WeatherMapData() {
		}

		public WeatherMapData(Cloud[] clouds, Geometry cloudGeom, TextureRenderer tr) {
			this.clouds = clouds;
			this.cloudGeom = cloudGeom;
			this.tr = tr;
		}
		
	}
	public static Random random = new Random();
	
	public static void main(String[] args) {
//		MeshBuilder.build(m)
//		Cloud cl = createFromCircle(5, 12, 0, 0);
////		waveX(cl.vertex, 0.25f, 20);
//		scale(cl.vertex, 0.25f, 1f);
//		translate(cl.vertex, 0.5f, 0f);
//		rotate(cl.vertex, 0.7f);


		Cloud[] clouds = createClouds(5, 12, 0.2f, 0.4f, 0.5f, 10);
		
		
		
		VectorPanel vp = createDebugPanel();
		addToDebugPanel(vp, clouds);
		
		JPanel panel = new JPanel(new BorderLayout());
		JToolBar bar = new JToolBar();
		JButton rotate = new JButton("rotate");
		rotate.addActionListener((a)->{
			WeatherMapGenerator.rotate(clouds, 0.05f);
			VectorPanel.ColorShape s = vp.shapes.get(0);
			vp.shapes.clear();
			vp.shapes.add(s);
			addToDebugPanel(vp, clouds);
			panel.repaint();
		});
		bar.add(rotate);

		panel.add(bar, BorderLayout.NORTH);
		panel.add(vp, BorderLayout.CENTER);
		DebugUtils.createFrame(panel);
	}
	
	
	
	public static void renderMap(TextureRenderer tr, Geometry cloudGeom, RenderManager rm) {
		tr.enableRender(rm);
		Renderer r = rm.getRenderer();
		r.setBackgroundColor(ColorRGBA.BlackNoAlpha);
		r.clearBuffers(true, false, false);
		cloudGeom.getMaterial().render(cloudGeom, rm);
		tr.endRender(rm);
	}
	
	public static Cloud[] createClouds(int inner, int outer, float fromScale, float toScale, float innerRad, int number) {
		Cloud[] clouds = new Cloud[number];
		
		clouds[0] = createFromCircle(inner, outer, innerRad);
		for(int i = 1; i < clouds.length; i++) clouds[i] = new Cloud(clouds[0]);
		for(int i = 0; i < clouds.length; i++) {
			Cloud cl = clouds[i];
			scale(cl.vertex, FastMath.interpolateLinear(random.nextFloat(), fromScale, toScale),
					FastMath.interpolateLinear(random.nextFloat(), fromScale, toScale));
			translate(cl.vertex, random.nextFloat()*2f-1f, random.nextFloat()*2f-1f);
			rotate(cl.vertex, random.nextFloat()*FastMath.TWO_PI);
		}
		
		return clouds;
	}
	/**
	 * 
	 * @param clouds -cloud array
	 * @param minSize - minimum size [0,1]
	 * @param maxSize - maximum size [0,1]
	 */
	public static void randomHeight(Cloud[] clouds, float minSize, float maxSize) {
		minSize += minSize;
		maxSize += maxSize;
		for(int i = 0; i < clouds.length; i++) {
			Cloud c = clouds[i];
			
			float size = FastMath.interpolateLinear(random.nextFloat(), minSize, maxSize);
			float from = random.nextFloat();
			
			float bottom = (2f-size)*from;
			float top = (bottom+size)-1;
			
			for(int k = 0; k < c.extra.length; k++) c.extra[k].set(bottom, top);
		}
	}
	public static void update(Geometry g, Cloud[] cld) {
		Mesh m = g.getMesh();
		MeshBuilder.update(m, cld);
		
		FloatBuffer fb = m.getFloatBuffer(VertexBuffer.Type.TexCoord2);
		fb.rewind();
		for(int i = 0; i < cld.length; i++) {
			Cloud c = cld[i];
			for(int j = 0; j < c.extra.length; j++) {
				fb.put(c.extra[j].x);
				fb.put(c.extra[j].y);
			}
		}
		m.getBuffer(VertexBuffer.Type.TexCoord2).setUpdateNeeded();		
	}
	public static Geometry buildGeom(AssetManager am, Texture2D noiseMap, Cloud... m) {
		Mesh mesh = MeshBuilder.build(m);
		
		FloatBuffer fb = BufferUtils.createFloatBuffer(mesh.getVertexCount()*2);
		fb.clear();
		for(int i = 0; i < m.length; i++) {
			Cloud c = m[i];
			for(int j = 0; j < c.extra.length; j++) {
				fb.put(c.extra[j].x);
				fb.put(c.extra[j].y);
			}
		}
		fb.flip();
		mesh.setBuffer(VertexBuffer.Type.TexCoord2, 2, fb);
		
		FloatBuffer fb2 = BufferUtils.createFloatBuffer(0,0,2,0,0,2,2,2);
		VertexBuffer vb = new VertexBuffer(VertexBuffer.Type.TexCoord3);
		vb.setInstanced(true);
		vb.setupData(VertexBuffer.Usage.Static, 2, VertexBuffer.Format.Float, fb2);
		mesh.setBuffer(vb);
		
		return buildGeom(am, noiseMap, mesh);
	}
	public static Geometry buildGeom(AssetManager am, Texture2D noiseMap, Mesh mesh) {
		Geometry g = new Geometry("CloudGeom", mesh);
		g.setCullHint(Spatial.CullHint.Never);
		Material mat = new Material(am, "MatDefs/Sky/WeatherMap.j3md");
		mat.setTexture("ColorMap", noiseMap);
		mat.getAdditionalRenderState().setDepthTest(false);
		mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
		mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Additive);
		g.setMaterial(mat);
		return g;
	}
	public static TextureRenderer createRenderer(int size) {
		Texture2D tex = new Texture2D(size, size, Image.Format.RGBA8);
		tex.setMinFilter(Texture.MinFilter.BilinearNearestMipMap);
		tex.setMagFilter(Texture.MagFilter.Bilinear);
//		tex.setWrap(Texture.WrapMode.MirroredRepeat);
		tex.setWrap(Texture.WrapMode.Repeat);
		return new TextureRenderer(tex, false);
	}
	
	public static VectorPanel createDebugPanel() {
		float scale = 100;
		VectorPanel vp = new VectorPanel();
		vp.shapes.add(new VectorPanel.ColorShape(Color.black, new Rectangle2D.Float(-scale, -scale, scale+scale, scale+scale)));
		return vp;
	}
	public static void addToDebugPanel(VectorPanel vp, Cloud... cl) {
		for(int i = 0; i < cl.length; i++)
			addToDebugPanel(vp, cl[i]);
	}
	public static void addToDebugPanel(VectorPanel vp, Cloud cl) {
		float scale = 100;
		for(int i = 0; i < cl.vertex.length; i++) {
//			vp.shapes.add(new VectorPanel.ColorShape(Color.orange.darker(),
//					new Ellipse2D.Float(cl.vertex[i].x*scale-2.5f, cl.vertex[i].y*scale-2.5f, 5,5)));
		}

		for(int i = 0; i < cl.index.length; i+=3) {
//		for(int i = 0; i < 3*7; i+=3) {
//		for(int i = 3*7; i < 3*8; i+=3) {
			int ai = cl.index[i];
			int bi = cl.index[i+1];
			int ci = cl.index[i+2];
			
			System.out.println("AI " + ai + ", " + bi + ", " + ci);
			
			Vector3f a = cl.vertex[ai];
			Vector3f b = cl.vertex[bi];
			Vector3f c = cl.vertex[ci];
			
			Path2D.Float p = new Path2D.Float();
			p.moveTo(a.x*scale, a.y*scale);
			p.lineTo(b.x*scale, b.y*scale);
			p.lineTo(c.x*scale, c.y*scale);
			p.lineTo(a.x*scale, a.y*scale);
		
			vp.shapes.add(new VectorPanel.ColorShape(Color.black, p));
		}
	}
	
	public static void translate(Vector3f[] pts, float x, float y) {
		for(int i = 0; i < pts.length; i++) {
			pts[i].addLocal(x,y,0);
		}
	}
	public static void scale(Vector3f[] pts, float x, float y) {
		for(int i = 0; i < pts.length; i++) {
			pts[i].multLocal(x,y,1);
		}
	}
	public static void rotate(Vector3f[] pts, float angle) {
		float s = FastMath.sin(angle);
		float c = FastMath.cos(angle);
		
		for(int i = 0; i < pts.length; i++) {
			Vector3f v = pts[i];
			v.set(v.x*c - v.y*s, v.x*s + v.y*c, v.z);
		}
	}
	public static void translate(Cloud[] cl, float x, float y) {
		for(Cloud c : cl) translate(c.vertex, x, y);
	}
	public static void scale(Cloud[] cl, float x, float y) {
		for(Cloud c : cl) scale(c.vertex, x, y);
	}
	public static void rotate(Cloud[] cl, float angle) {
		for(Cloud c : cl) rotate(c.vertex, angle);
	}
	public static Cloud createFromCircle(int inner, int outer, float innerRad/*float outerAngleRNG, float innerRadRNG*/) {
		if(inner < 1 || outer < 3) return null;
		
		int vlen = inner+outer;
		Vector3f[] vertex = new Vector3f[vlen];
		
		int innerTris = Math.max(inner-2, 0);
		int outerTris = inner == 1?outer:((Math.min(inner, outer)<<1)+(Math.abs(inner-outer)));
		int tris = innerTris+outerTris;
		
//		System.out.println("outerTris " + outerTris + ", innerTris " +innerTris);
		
		float angle = 0;
		float step = FastMath.TWO_PI/outer;
		for(int i = 0; i < outer; i++) {
			vertex[i] = new Vector3f(
				FastMath.cos(angle),
				FastMath.sin(angle),
				0
			);
			angle += step;
		}
		
		angle = 0;
		step = FastMath.TWO_PI/inner;
		
		for(int i = 0; i < inner; i++) {
			vertex[outer+i] = new Vector3f(
				innerRad*FastMath.cos(angle),
				innerRad*FastMath.sin(angle),
				0
			);
			angle += step;
		}
		
		int i = 0;
		int[] index = new int[tris*3];
		
		int oI = 0; int iI = outer;
		
		Vector3f d0 = new Vector3f();
		Vector3f d1 = new Vector3f();
		if(inner == 1) {
			
		}
		else {
			for(int k = 0; k < outerTris; k++) {
				Vector3f ao = vertex[oI==outer?0:oI];
				Vector3f ai = vertex[iI==vlen?outer:iI];

				Vector3f bo = null, bi = null;
				if(oI<outer) bo = vertex[(oI+1)%outer];
				if(iI+1<vlen) bi = vertex[iI+1];
				else if(iI+1 == vlen) bi = vertex[outer];
				
				d0.set(ai).subtractLocal(ao);

				float z0 = -1, z1 = -1;
				
				if(bo != null) {
					d1.set(bo).subtractLocal(ai);
					d1.crossLocal(d0);
					z0 = d1.z;
				}
				if(bi != null) {
					d1.set(bi).subtractLocal(ai);
					d1.crossLocal(d0);
					z1 = d1.z;
				}

//				System.out.println("z0, " + z0 + ", z1 " + z1);
				index[i++] = oI==outer?0:oI;
				index[i++] = iI==vlen?outer:iI;
				if(z1 >= 0) {
					iI++;
					index[i++] = iI==vlen?outer:iI;
				}
				else {
					oI++;
					index[i++] = oI==outer?0:oI;
				}
			
			}
			if(innerTris > 0) {
				for(int k = 0; k < innerTris; k++) {
					index[i++] = outer;
					index[i++] = outer+2+k;
					index[i++] = outer+1+k;
				}
			}
		}		
		for(int j = 0; j < inner; j++) vertex[outer+j].z = 1;
		
		return new Cloud(vertex, index, outer);
	}
	
	public static class Cloud implements BMesh {
		public Vector3f[] vertex;
		public Vector2f[] extra;
		public int[] index;
		public int outer;

		public Cloud() {}
		public Cloud(Cloud c) {
			vertex = new Vector3f[c.vertex.length];
			index = new int[c.index.length];
			for(int i = 0; i < vertex.length; i++)
				vertex[i] = new Vector3f(c.vertex[i]);
			for(int i = 0; i < index.length; i++) index[i] = c.index[i];
			extra = new Vector2f[c.extra.length];
			for(int i = 0; i < extra.length; i++) extra[i] = new Vector2f(c.extra[i]);
			c.outer = outer;
		}
		public Cloud(Vector3f[] vertex, int[] index, int outer) {
			this.vertex = vertex;
			this.index = index;
			this.outer = outer;
			extra = new Vector2f[vertex.length];
			for(int i = 0; i < extra.length; i++) extra[i] = new Vector2f(1,1);
		}
		
		@Override
		public int estimateVertexCount() {
			return vertex.length;
		}

		@Override
		public int estimateTriangleCount() {
			return index.length/3;
		}

		@Override
		public void build(FloatBuffer pos, IndexBuffer ib, int pOff, int ibOff) {
			float maxX = vertex[0].x, maxY = vertex[0].y;
//				  minX = vertex[0].x, minY = vertex[0].y;
			for(int i = 1; i < vertex.length; i++) {
				maxX = Math.max(vertex[i].x, maxX);
				maxY = Math.max(vertex[i].y, maxY);
//				minX = Math.min(vertex[i].x, minX);
//				minY = Math.min(vertex[i].y, minY);
			}
			maxX += 1f; maxY += 1f;// minX += 1f; minY += 1f;
			//Bound calculated...
			maxX = Mathf.mod(maxX, 2f)-maxX;
			maxY = Mathf.mod(maxY, 2f)-maxY;
			
			for(int i = 0; i < vertex.length; i++) {
				vertex[i].x += maxX;
				vertex[i].y += maxY;
			}
			
			pos.position(pOff);
			for(int i = 0; i < vertex.length; i++) 
				pos.put(vertex[i].x).put(vertex[i].y).put(vertex[i].z);
			pOff /= 3;
			for(int i = 0; i < index.length; i++) {
				ib.put(ibOff++, pOff+index[i]);
			}
		}		
	}
	public static void update(Cloud c, LookupTable2 f, float tpf) {
		Vector3f[] v = c.vertex;
		Vector3f med = new Vector3f();
		int inner = c.outer-v.length;
		for(int i = c.outer; i < v.length; i++) {
			Vector3f vec = v[i];
			med.addLocal(vec);
		}
		med.multLocal(1f/(v.length-c.outer));
//		System.err.println("Med " + med.x + ", " + med.y);
		Vector4f v4 = f.lookup(med.x*0.5f+0.5f, med.y*0.5f+0.5f);
		System.err.println("lookup " + v4);
		
		translate(v, v4.x*tpf, v4.y*tpf);
	}
	public static void update(Cloud[] cl, LookupTable2 f, float tpf) {
		for(Cloud c : cl) update(c, f, tpf);
	}
}
