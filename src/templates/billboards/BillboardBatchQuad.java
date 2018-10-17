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
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture2D;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

/**
 *
 * @author Juraj Papp
 */
public class BillboardBatchQuad extends Geometry {
		VertexBuffer dataPosVb;
		FloatBuffer dataPos;
		
		VertexBuffer indexVb;
		IntBuffer indexData;
		
		VertexBuffer texSlotVb;
		FloatBuffer texSlotData;
		
		VertexBuffer viewSizeVb;
		FloatBuffer viewSizeData;
		
		VertexBuffer rotVb;
		FloatBuffer rotData;
		
		VertexBuffer texVb;
		FloatBuffer texData;
		
		Texture2D tex;
		
		//some size of items

		public BillboardBatchQuad() {
			super("BillboardBatch");
		}
		public BillboardBatchQuad(AssetManager am, Texture2D tex, int capacity, VertexBuffer.Usage usage) {
			init(am, tex, capacity, usage);
		}
		public void init(AssetManager am, Texture2D tex, int capacity, VertexBuffer.Usage usage) {
			this.tex = tex;
			dataPos = BufferUtils.createFloatBuffer(12*capacity);
			indexData = BufferUtils.createIntBuffer(6*capacity);
			texSlotData = BufferUtils.createFloatBuffer(4*4*capacity);
			viewSizeData = BufferUtils.createFloatBuffer(4*4*capacity);
			rotData = BufferUtils.createFloatBuffer(4*4*capacity);
			texData = BufferUtils.createFloatBuffer(2*4*capacity);
			
			dataPos.clear();                  dataPos.flip();
			indexData.clear();			      indexData.flip();
			texSlotData.clear();           texSlotData.flip();
			viewSizeData.clear();          viewSizeData.flip();
			rotData.clear();				rotData.flip();
			texData.clear();				texData.flip();
			
			dataPosVb = new VertexBuffer(VertexBuffer.Type.Position);
//			dataVb.setInstanced(true);
			dataPosVb.setupData(usage, 3, VertexBuffer.Format.Float, dataPos);
			
			indexVb = new VertexBuffer(VertexBuffer.Type.Index);
			indexVb.setupData(usage, 3, VertexBuffer.Format.UnsignedInt, indexData);
			
			texSlotVb = new VertexBuffer(VertexBuffer.Type.TexCoord2);
//			texSlotVb.setInstanced(true);
			texSlotVb.setupData(usage, 4, VertexBuffer.Format.Float, texSlotData);
			
			viewSizeVb = new VertexBuffer(VertexBuffer.Type.TexCoord3);
//			viewSizeVb.setInstanced(true);
			viewSizeVb.setupData(usage, 4, VertexBuffer.Format.Float, viewSizeData);
			
			rotVb = new VertexBuffer(VertexBuffer.Type.TexCoord4);
//			rotVb.setInstanced(true);
			rotVb.setupData(usage, 4, VertexBuffer.Format.Float, rotData);
			
			texVb = new VertexBuffer(VertexBuffer.Type.TexCoord);
//			rotVb.setInstanced(true);
			texVb.setupData(usage, 2, VertexBuffer.Format.Float, texData);
			
			Quad m = new Quad();
//			FloatBuffer meshFb = (FloatBuffer) m.getBuffer(VertexBuffer.Type.Position).getData();
//			meshFb.rewind();
//			meshFb.put(new float[]{-.5f, -0.5f,      0,
//                               0.5f, -0.5f,      0,
//                               0.5f,  0.5f,      0,
//                               -.5f,  0.5f,      0
//                               });
			m.setBuffer(dataPosVb);
			m.setBuffer(indexVb);
			m.setBuffer(texSlotVb);
			m.setBuffer(viewSizeVb);
			m.setBuffer(rotVb);
			m.setBuffer(texVb);
			setMesh(m);
			Material mat = new Material(am, "MatDefs/BillboardBatch.j3md");
			mat.setTexture("ColorMap", tex);
			setMaterial(mat);
			setCullHint(Spatial.CullHint.Never);
		}
	
		public void add(Transform t, BillboardData d) {
			Matrix4f m = t.toTransformMatrix();
			float[] arr = new float[12];
//			arr[0] = m.m00; arr[1] = m.m01;			 arr[2] = m.m02;  arr[3] = m.m03; 
//			arr[4] = m.m10; arr[5] = m.m11*d.height; arr[6] = m.m12;  arr[7] = m.m13+d.offset.y*t.getScale().y; 
//			arr[8] = m.m20; arr[9] = m.m21;			 arr[10] = m.m22; arr[11] = m.m23; 

			m.m00 = m.m00*d.viewSize.x;
			m.m11 = m.m11*d.height; m.m13 = m.m13+d.offset.y*t.getScale().y; 

			Vector4f tmp = new Vector4f(-0.5f, -0.5f, 0f, 1f);
			m.mult(tmp, tmp);
			arr[0] = tmp.x; arr[1] = tmp.y; arr[2] = tmp.z;
			tmp.set(0.5f, -0.5f, 0f, 1f);
			m.mult(tmp, tmp);
			arr[3] = tmp.x; arr[4] = tmp.y; arr[5] = tmp.z;
			tmp.set(0.5f, 0.5f, 0f, 1f);
			m.mult(tmp, tmp);
			arr[6] = tmp.x; arr[7] = tmp.y; arr[8] = tmp.z;
			tmp.set(-0.5f, 0.5f, 0f, 1f);
			m.mult(tmp, tmp);
			arr[9] = tmp.x; arr[10] = tmp.y; arr[11] = tmp.z;
			
			add(arr, d.textureSlot, d.viewSize, new Vector4f(m.m03, m.m23, m.m02, m.m22));
//			add(arr, d.textureSlot, d.viewSize, new Vector4f(m.m21, m.m23, m.m12, m.m20));
		}
		public void add(float[] arr, Vector4f rot, BillboardData data) {
			add(arr, data.textureSlot, data.viewSize, rot);
		}
		int items = 0;
		public void add(float[] arr, Vector4f texSlot, Vector4f size, Vector4f rot) {
			//int limit = data.limit();System.out.println("limit " + limit);
			int limit = items*12;
			dataPos.limit(limit+12);
			dataPos.position(limit);
			dataPos.put(arr, 0, 12);
			
//			System.out.println("Putting " + Arrays.toString(arr));
			
			limit = items*6;
			indexData.limit(limit+6);
			indexData.position(limit);
			limit = items*4;
			indexData.put(limit).put(limit+1).put(limit+2)
					.put(limit+2).put(limit+3).put(limit);
			
//			limit = items*6;
//			for(int i = limit; i < limit+6; i++)
//				System.out.println(indexData.get(i)+",");
//			System.out.println("");
			
//			limit = texSlotData.limit();
			limit = items*16;
			texSlotData.limit(limit+16);
			texSlotData.position(limit);
			for(int i = 0; i < 4; i++) texSlotData.put(texSlot.x).put(texSlot.y).put(texSlot.z).put(texSlot.w);
			
//			limit = viewSizeData.limit();
			limit = items*16;
			viewSizeData.limit(limit+16);
			viewSizeData.position(limit);
			for(int i = 0; i < 4; i++) viewSizeData.put(size.x).put(size.y).put(size.z).put(size.w);
			
			limit = items*16;
			rotData.limit(limit+16);
			rotData.position(limit);
			for(int i = 0; i < 4; i++) rotData.put(rot.x).put(rot.y).put(rot.z).put(rot.w);
			
			limit = items*8;
			texData.limit(limit+8);
			texData.position(limit);
			texData.put(0).put(0).put(1).put(0).put(1).put(1).put(0).put(1);
			
			dataPos.flip();
			indexData.flip();
			texSlotData.flip();
			viewSizeData.flip();
			rotData.flip();
			texData.flip();
			
//			System.out.println("dataPos " + dataPos.limit());
//			System.out.println("indexData " + indexData.limit());
//			System.out.println("texSlotData " + texSlotData.limit());
//			System.out.println("viewSizeData " + viewSizeData.limit());
//			System.out.println("rotData " + rotData.limit());
//			System.out.println("texData " + texData.limit());
//			
			dataPosVb.updateData(dataPos);
			indexVb.updateData(indexData);
			texSlotVb.updateData(texSlotData);
			viewSizeVb.updateData(viewSizeData);
			rotVb.updateData(rotData);
			texVb.updateData(texData);
			
			items++;
//			MiscUtils.writeInt(mesh, Mesh.class, "instanceCount", items);
			mesh.updateCounts();
		}
		public void clear() {
			items = 0;
			mesh.updateCounts();
//			MiscUtils.writeInt(mesh, Mesh.class, "instanceCount", items);
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
			return dataPos.capacity()/12;
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
