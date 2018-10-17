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

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Matrix4f;
import com.jme3.math.Transform;
import com.jme3.math.Vector4f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture2D;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;
import templates.util.MiscUtils;

/**
 *
 * @author Juraj Papp
 */
public class BillboardQuad extends Geometry {
		VertexBuffer dataVb;
		FloatBuffer data;
		
		VertexBuffer texSlotVb;
		FloatBuffer texSlotData;
		
		VertexBuffer viewSizeVb;
		FloatBuffer viewSizeData;
		
		Texture2D tex;
		
		//some size of items

		public BillboardQuad() {
			super("Billboard");
		}
		public BillboardQuad(AssetManager am, Texture2D tex, int capacity, VertexBuffer.Usage usage) {
			init(am, tex, capacity, usage);
		}
		public void init(AssetManager am, Texture2D tex, int capacity, VertexBuffer.Usage usage) {
			this.tex = tex;
			data = BufferUtils.createFloatBuffer(12*capacity);
			texSlotData = BufferUtils.createFloatBuffer(4*capacity);
			viewSizeData = BufferUtils.createFloatBuffer(4*capacity);
			
			data.clear();                  data.flip();
			texSlotData.clear();           texSlotData.flip();
			viewSizeData.clear();          viewSizeData.flip();
			
			dataVb = new VertexBuffer(VertexBuffer.Type.InstanceData);
			dataVb.setInstanced(true);
			dataVb.setupData(usage, 12, VertexBuffer.Format.Float, data);
			
			texSlotVb = new VertexBuffer(VertexBuffer.Type.TexCoord2);
			texSlotVb.setInstanced(true);
			texSlotVb.setupData(usage, 4, VertexBuffer.Format.Float, texSlotData);
			
			viewSizeVb = new VertexBuffer(VertexBuffer.Type.TexCoord3);
			viewSizeVb.setInstanced(true);
			viewSizeVb.setupData(usage, 4, VertexBuffer.Format.Float, viewSizeData);
			
			Quad m = new Quad(1,1);
			FloatBuffer meshFb = (FloatBuffer) m.getBuffer(VertexBuffer.Type.Position).getData();
			meshFb.rewind();
			meshFb.put(new float[]{-.5f, -0.5f,      0,
                               0.5f, -0.5f,      0,
                               0.5f,  0.5f,      0,
                               -.5f,  0.5f,      0
                               });
			m.setBuffer(dataVb);
			m.setBuffer(texSlotVb);
			m.setBuffer(viewSizeVb);
			setMesh(m);
			Material mat = new Material(am, "MatDefs/Billboard.j3md");
			mat.setTexture("ColorMap", tex);
			mat.setBoolean("UseInstancing", true);
			setMaterial(mat);
			setCullHint(Spatial.CullHint.Never);
		}
	
		public void add(Transform t, BillboardData d) {
			Matrix4f m = t.toTransformMatrix();
			float[] arr = new float[12];
			arr[0] = m.m00; arr[1] = m.m01;			 arr[2] = m.m02;  arr[3] = m.m03; 
			arr[4] = m.m10; arr[5] = m.m11*d.height; arr[6] = m.m12;  arr[7] = m.m13+d.offset.y*t.getScale().y; 
			arr[8] = m.m20; arr[9] = m.m21;			 arr[10] = m.m22; arr[11] = m.m23; 
			add(arr, d.textureSlot, d.viewSize);
		}
		public void add(float[] arr, BillboardData data) {
			add(arr, data.textureSlot, data.viewSize);
		}
		int items = 0;
		public void add(float[] arr, Vector4f texSlot, Vector4f size) {
			//int limit = data.limit();System.out.println("limit " + limit);
			int limit = items*12;
			data.limit(limit+12);
			data.position(limit);
			data.put(arr, 0, 12);
			
//			limit = texSlotData.limit();
			limit = items*4;
			texSlotData.limit(limit+4);
			texSlotData.position(limit);
			texSlotData.put(texSlot.x).put(texSlot.y).put(texSlot.z).put(texSlot.w);
			
//			limit = viewSizeData.limit();
			limit = items*4;
			viewSizeData.limit(limit+4);
			viewSizeData.position(limit);
			viewSizeData.put(size.x).put(size.y).put(size.z).put(size.w);
			
			data.flip();
			texSlotData.flip();
			viewSizeData.flip();
			
			dataVb.updateData(data);
			texSlotVb.updateData(texSlotData);
			viewSizeVb.updateData(viewSizeData);
			
			items++;
			MiscUtils.writeInt(mesh, Mesh.class, "instanceCount", items);
//			mesh.updateCounts();
		}
		public void clear() {
			items = 0;
			
			MiscUtils.writeInt(mesh, Mesh.class, "instanceCount", items);
		}

		@Override
		public CullHint getCullHint() {
			if(items == 0) return CullHint.Always;
			return super.getCullHint(); 
		}
		
		public int size() {
			return items;
			//return data.limit()/12;
		}
		public int capacity() {
			return data.capacity()/12;
		}
		public void setDithering(boolean enable) {
			if(enable) getMaterial().setBoolean("Dither", true);
			else getMaterial().clearParam("Dither");
		}
		/***
		 * @param a [0-1] for alpha discard, -1 to disable 
		 */
		public void setAlphaDiscardThreshold(float a) {
			if(a < -0.5) getMaterial().clearParam("AlphaDiscardThreshold");
			else getMaterial().setFloat("AlphaDiscardThreshold", a);
		}
	}
