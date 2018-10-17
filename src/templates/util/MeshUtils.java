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
package templates.util;

import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.math.FastMath;
import com.jme3.math.Transform;
import com.jme3.math.Triangle;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.mesh.IndexBuffer;
import com.jme3.util.BufferUtils;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import templates.geom.Quad4;
import templates.geom.Triangle3;
import templates.geom.Vector3d;
import static templates.util.MiscUtils.pack;

/**
 *
 * @author Juraj Papp
 */
public class MeshUtils {
	
	public static IndexBuffer generateAdjacencyIndex(Mesh m) {
		return generateAdjacencyIndex(m.getIndexBuffer(), m.getVertexCount());
	}
	public static IndexBuffer generateAdjacencyIndex(IndexBuffer ib, int vertexCount) {
		HashMap<Long, Integer> map = new HashMap<Long, Integer>();
		int size = ib.size();
		IndexBuffer adj = IndexBuffer.createIndexBuffer(vertexCount, size+size);
		
		for(int i = 0; i < size; i += 3)
			map.put(pack(ib.get(i),ib.get(i+1)), ib.get(i+2));
		for(int i = 0; i < size; i += 3) {
			int a = ib.get(i), b = ib.get(i+1), c = ib.get(i+2);
			int a2 = map.getOrDefault(pack(b,a),a),
					b2 = map.getOrDefault(pack(c,b),b),
					c2 = map.getOrDefault(pack(a,c),c);
			
			int ii = i+i;
			adj.put(ii, a);
			adj.put(ii+1, a2);
			adj.put(ii+2, b);
			adj.put(ii+3, b2);
			adj.put(ii+4, c);
			adj.put(ii+5, c2);
		}
		
		return adj;
	}
	
	public static void transform(ArrayList<Triangle3> tris, Transform t) {
		for(Triangle3 a : tris) {
			a.a.transform(t);
			a.b.transform(t);
			a.c.transform(t);
		}
	}
	public static void add(ArrayList<Triangle3> tris, Vector3d t) {
		for(Triangle3 a : tris) {
			a.a.addLocal(t);
			a.b.addLocal(t);
			a.c.addLocal(t);
		}
	}
	public static Vector3d max(ArrayList<Vector3d> list) {
		if(list.isEmpty()) return null;
		Vector3d min = list.get(0);
		for(int i = 1; i < list.size(); i++) 
			min = Mathf.max(min, list.get(i));
		return min;
	}
	public static Vector3d min(ArrayList<Vector3d> list) {
		if(list.isEmpty()) return null;
		Vector3d min = list.get(0);
		for(int i = 1; i < list.size(); i++) 
			min = Mathf.min(min, list.get(i));
		return min;
	}
//	public static ArrayList<Vector3f> toLine(Mesh m) {
//		ArrayList<Vector3f> pts = new ArrayList<>();
//		IndexBuffer idx = m.getIndexBuffer();
//		FloatBuffer pos = m.getFloatBuffer(VertexBuffer.Type.Position);
//		for(int i = 0; i < idx.size();) {
//			int k = idx.get(i++)*3;
//			pts.add(new Vector3f(pos.get(k), pos.get(k+1), pos.get(k+2)));
//		}
//		return pts;
//	}
	public static double sum(double[] dist) {
		double s = 0;
		for(int i = 0; i < dist.length; i++) s += dist[i];
		return s;
	}
	public static int lineInterpolateIndex(double[] dist, double distSum, double val) {
		if(dist == null) return -1;
		if(val <= 0.0) return 0;
		if(val >= 1.0) return dist.length-1;
		int i = 0;
		double t = val;
		while(i < dist.length && t > dist[i]) { t -= dist[i]; i++; }
		if(i >= dist.length-1) return dist.length-1;
		return i;
	}
//	public static void main(String[] args) {
//		ArrayList<Vector3d> lines = new ArrayList<>();
//		lines.add(Vector3d.ZERO);
//		lines.add(new Vector3d(1,1,0));
//		lines.add(new Vector3d(2,0,0));
////		double[] dist = new double[] {1,1};
////		double distSum = 2;
//		double[] dist = lineDistances(lines);
//		double distSum = sum(dist);
////		System.out.println("dist " + Arrays.toString(dist) + ", " +distSum);
//		Vector3d test = lineInterpolate(lines, dist, distSum, 1);
//		System.out.println("test " + test);
//	}
	public static Vector3d lineInterpolate(List<Vector3d> lines, double[] dist, double distSum, double val) {
		if(lines.isEmpty()) return null;
		if(val <= 0.0) return lines.get(0);
		if(val >= 1.0) return lines.get(lines.size()-1);
		int i = 0;
		double t = val*distSum;
		while(i < dist.length && t > dist[i]) { t -= dist[i]; i++; }
		if(i == lines.size()-1) return lines.get(i);
		System.out.println("i " + i);
		return new Vector3d().interpolateLocal(lines.get(i), lines.get(i+1), t/dist[i]);
	}
	public static double[] lineDistances(List<Vector3d> lines) {
		if(lines.size() <= 1) return null;
		double[] dist = new double[lines.size()-1];
		Vector3d a = lines.get(0);
		for(int i = 1; i < lines.size(); i++) {
			Vector3d b = lines.get(i);
			dist[i-1] = a.distance(b);
			a = b;
		}
		return dist;
	}
	
	
	
	
	public static ArrayList<Vector3d> toLine(Mesh m) {
		ArrayList<Vector3d> pts = new ArrayList<>();
		IndexBuffer idx = m.getIndexBuffer();
		FloatBuffer pos = m.getFloatBuffer(VertexBuffer.Type.Position);
		for(int i = 0; i < idx.size();) {
			int k = idx.get(i++)*3;
			pts.add(new Vector3d(pos.get(k), pos.get(k+1), pos.get(k+2)));
		}
		return pts;
	}
	public static ArrayList<Quad4> connect(ArrayList<Vector3d> line1, ArrayList<Vector3d> line2) {
		if(line1.size() <= 1) return null;
		ArrayList<Quad4> quads = new ArrayList<>(line1.size()-1);
		Vector3d a = line1.get(0);
		Vector3d c = line2.get(0);
		for(int i = 1; i < line1.size(); i++) {
			Vector3d b = line1.get(i);
			Vector3d d = line2.get(i);
			quads.add(new Quad4(a, b, d, c));
			a = b;
			c = d;
		}
		return quads;
	}
	public static ArrayList<Quad4> extrude(ArrayList<Vector3d> lines, Vector3d dir) {
		if(lines.size() <= 1) return null;
		ArrayList<Quad4> quads = new ArrayList<>(lines.size()-1);
		Vector3d a = lines.get(0);
		for(int i = 1; i < lines.size(); i++) {
			Vector3d b = lines.get(i);
			quads.add(new Quad4(a, b, b.add(dir), a.add(dir)));
			a = b;
		}
		return quads;
	}
	public static ArrayList<Quad4> extrude(ArrayList<Vector3d> lines, Vector3d dir0, Vector3d dir) {
		if(lines.size() <= 1) return null;
		ArrayList<Quad4> quads = new ArrayList<>(lines.size()-1);
		Vector3d a = lines.get(0);
		for(int i = 1; i < lines.size(); i++) {
			Vector3d b = lines.get(i);
			quads.add(new Quad4(a.add(dir0), b.add(dir0), b.add(dir), a.add(dir)));
			a = b;
		}
		return quads;
	}
	public static void filterEmpty(ArrayList<Quad4> quads, double limit) {
		Vector3d AB = new Vector3d();
		Vector3d AC = new Vector3d();
		limit = 4.0*limit*limit;
		Iterator<Quad4> iter = quads.iterator();
		while(iter.hasNext()) {
			Quad4 q = iter.next();
			q.b.subtract(q.a, AB);
			q.c.subtract(q.a, AC);
			if(AB.cross(AC, AB).lengthSquared() < limit) iter.remove();
		}
	}
	public static ArrayList<Triangle3> toTriangles(Mesh m, Transform t) {
		ArrayList<Triangle3> tris = toTriangles(m);
		transform(tris, t);
		return tris;
	}
	public static ArrayList<Triangle3> toTriangles(Mesh m) {
		ArrayList<Triangle3> tris = new ArrayList<>();
		IndexBuffer idx = m.getIndexBuffer();
		FloatBuffer pos = m.getFloatBuffer(VertexBuffer.Type.Position);
		
		for(int i = 0; i < idx.size();) {
			Triangle3 t = new Triangle3();
			int k = idx.get(i++)*3;
			t.a.set(pos.get(k), pos.get(k+1), pos.get(k+2));
			k = idx.get(i++)*3;
			t.b.set(pos.get(k), pos.get(k+1), pos.get(k+2));
			k = idx.get(i++)*3;
			t.c.set(pos.get(k), pos.get(k+1), pos.get(k+2));
			tris.add(t);
		}
		return tris;
	}
	public static ArrayList<Object> toQuads(Mesh m) {
		return MeshUtils.toQuads(MeshUtils.toTriangles(m));
	}
	public static ArrayList<Object> toQuads(Mesh m, Transform t) {
		return MeshUtils.toQuads(MeshUtils.toTriangles(m, t));
	}
	public static ArrayList<Object> toQuads(ArrayList<Triangle3> tris) {
		final float eps = 0.01f;
		ArrayList list = new ArrayList();
		Vector3d ab = new Vector3d();
		Vector3d ac = new Vector3d();
		Vector3d ad = new Vector3d();
		Vector3d d;
		Vector3d x = new Vector3d();
		Quad4 q = new Quad4();
		loop:
		while(!tris.isEmpty()) {
			Triangle3 t = tris.remove(tris.size()-1);
			t.b.subtract(t.a, ab);
			t.c.subtract(t.a, ac);
			ab.cross(ac, x);
			
			for(int i = tris.size()-1; i >= 0; i--) {
				Triangle3 u = tris.get(i);
				int a = has(u, t.a, eps); 
				int b = has(u, t.b, eps); 
				int c = has(u, t.c, eps); 
				if(b != -1 && c != -1) {
					d = u.getOther(b,c);
					q.a = t.a;
					q.b = t.b;
					q.c = d;
					q.d = t.c;
				}
				else if(a != -1 && c != -1) {
					d = u.getOther(a,c);
					q.a = t.a;
					q.b = t.b;
					q.c = t.c;
					q.d = d;
				}
				else if(a != -1 && b != -1) {
					d = u.getOther(a,b);
					q.a = t.a;
					q.b	= d;
					q.c = t.b;
					q.d = t.c;
				}
				else continue;
				if(d == null) {
					System.err.println("duplicate triangle " + u + ",\n " + t);
					throw new IllegalArgumentException();
				}
				d.subtract(t.a, ad);
				if(Math.abs(ad.dot(x)) <= eps) {
					tris.remove(i);
					list.add(q);
					q = new Quad4();
					continue loop;
				}
			}
			list.add(t);
		}
		return list;
	}
	public static int has(Triangle3 t, Vector3d a, float eps) {
		if(Mathf.isEqual(t.a, a, eps)) return 0;
		if(Mathf.isEqual(t.b, a, eps)) return 1;
		if(Mathf.isEqual(t.c, a, eps)) return 2;
		return -1;
	}
	public static Mesh simpleUV(Mesh m, float scale) {
		return simpleUV(m, new Vector3f(scale, scale, scale));
	}
	public static Mesh simpleUV(Mesh m, Vector3f scale) {
		FloatBuffer uv = m.getFloatBuffer(VertexBuffer.Type.TexCoord);
		boolean set = false;
		if(uv == null) {
			set = true;
			uv = BufferUtils.createFloatBuffer(m.getVertexCount()*2);
		}
		uv.clear();
		FloatBuffer pos = (FloatBuffer)((VertexBuffer)m.getBuffer(VertexBuffer.Type.Position)).getData();
//		IndexBuffer ib = m.getIndexBuffer();
		
//		int size = ib.size();
//		for(int i = 0; i < size; i++) {
//			int x = ib.get(i)*3;
//			pos.get(x);
//			pos.get(x+1);
//			pos.get(x+2);
//		}

		pos.rewind();
		while(pos.hasRemaining()) {
			float x = pos.get()*scale.x, y = pos.get()*scale.y, z = pos.get()*scale.z;
			uv.put(x).put(z+y);
		}
		pos.rewind();
		uv.flip();
		if(set) m.setBuffer(VertexBuffer.Type.TexCoord, 2, uv);
		else m.getBuffer(VertexBuffer.Type.TexCoord).setUpdateNeeded();
		return m;
	}
	public static float getMeshRadius(Mesh m) {
		FloatBuffer fb = m.getFloatBuffer(VertexBuffer.Type.Position);
		int l = fb.limit();
		float max = 0;
		for(int i = 0; i < l; i+=3) {
			float x = fb.get(i);
			float y = fb.get(i+1);
			float z = fb.get(i+2);
			max = Math.max(max, x*x+y*y+z*z);
		}
		return (float)Math.sqrt(max);
	}
}
