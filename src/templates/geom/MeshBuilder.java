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
package templates.geom;

import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.mesh.IndexBuffer;
import com.jme3.util.BufferUtils;
import static com.jme3.util.BufferUtils.createShortBuffer;
import static com.jme3.util.BufferUtils.createVector3Buffer;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.List;

/**
 *
 * @author Juraj Papp
 */
public class MeshBuilder {
	public static void update(Mesh mesh, BMesh... m) {
//		int vC = 0, tC = 0;
//        for(int i = 0; i < m.length; i++) {
//            vC += m[i].estimateVertexCount();
//            tC += m[i].estimateTriangleCount();
//        }
//        FloatBuffer pos = BufferUtils.createFloatBuffer(vC*3);
//        IndexBuffer ib = IndexBuffer.createIndexBuffer(vC, tC*3);

		FloatBuffer pos = mesh.getFloatBuffer(VertexBuffer.Type.Position);
		IndexBuffer ib = mesh.getIndexBuffer();
		
        int vC = 0; int tC = 0;
        for(int i = 0; i < m.length; i++) {
            m[i].build(pos, ib, vC*3, tC*3);
            vC += m[i].estimateVertexCount();
            tC += m[i].estimateTriangleCount();
        }
        mesh.getBuffer(VertexBuffer.Type.Position).setUpdateNeeded();
        mesh.getBuffer(VertexBuffer.Type.Index).setUpdateNeeded();
        mesh.updateBound();
	}
	
    public static Mesh build(BMesh... m) {
        int vC = 0, tC = 0;
        for(int i = 0; i < m.length; i++) {
            vC += m[i].estimateVertexCount();
            tC += m[i].estimateTriangleCount();
        }
        FloatBuffer pos = BufferUtils.createFloatBuffer(vC*3);
        IndexBuffer ib = IndexBuffer.createIndexBuffer(vC, tC*3);
        vC = 0; tC = 0;
        for(int i = 0; i < m.length; i++) {
            m[i].build(pos, ib, vC*3, tC*3);
            vC += m[i].estimateVertexCount();
            tC += m[i].estimateTriangleCount();
        }
        Mesh mesh = new Mesh();
        mesh.setBuffer(VertexBuffer.Type.Position, 3, pos);
        Buffer buf = ib.getBuffer();
        if(buf instanceof ShortBuffer) {
            mesh.setBuffer(VertexBuffer.Type.Index, 3, (ShortBuffer)buf);
        }
        else mesh.setBuffer(VertexBuffer.Type.Index, 3, (IntBuffer)buf);
        
        mesh.updateBound();
        mesh.updateCounts();
        mesh.setStatic();
        return mesh;
    }
     public static Mesh build(List<? extends BMesh> m) {
        int vC = 0, tC = 0;
        for(int i = 0; i < m.size(); i++) {
            BMesh b = m.get(i);
            vC += b.estimateVertexCount();
            tC += b.estimateTriangleCount();
        }
        FloatBuffer pos = BufferUtils.createFloatBuffer(vC*3);
        IndexBuffer ib = IndexBuffer.createIndexBuffer(vC, tC*3);
        vC = 0; tC = 0;
        for(int i = 0; i < m.size(); i++) {
            BMesh b = m.get(i);
            b.build(pos, ib, vC*3, tC*3);
            vC += b.estimateVertexCount();
            tC += b.estimateTriangleCount();
        }
        Mesh mesh = new Mesh();
        mesh.setBuffer(VertexBuffer.Type.Position, 3, pos);
        Buffer buf = ib.getBuffer();
        if(buf instanceof ShortBuffer) {
            mesh.setBuffer(VertexBuffer.Type.Index, 3, (ShortBuffer)buf);
        }
        else mesh.setBuffer(VertexBuffer.Type.Index, 3, (IntBuffer)buf);
        
        mesh.updateBound();
        mesh.updateCounts();
        mesh.setStatic();
        return mesh;
    }
}
