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

import com.jme3.scene.mesh.IndexBuffer;
import java.nio.FloatBuffer;

/**
 * a b
 * d c
 * @author Juraj Papp
 */
public class Quad4 implements BMesh {
	public Vector3d a = new Vector3d(), b = new Vector3d(1,0,0),
			c = new Vector3d(1,0,1), d = new Vector3d(0,0,1);

	public Quad4() {
		
	}
	public Quad4(Vector3d a, Vector3d b, Vector3d c, Vector3d d) {
		this.a.set(a);
		this.b.set(b);
		this.c.set(c);
		this.d.set(d);
	}
	@Override
	public int estimateVertexCount() {
		return 4;
	}

	@Override
	public int estimateTriangleCount() {
		return 2;
	}

	@Override
	public void build(FloatBuffer pos, IndexBuffer ib, int pOff, int ibOff) {
		pos.position(pOff);
		pos.put((float)a.x).put((float)a.y).put((float)a.z);
		pos.put((float)b.x).put((float)b.y).put((float)b.z);
		pos.put((float)c.x).put((float)c.y).put((float)c.z);
		pos.put((float)d.x).put((float)d.y).put((float)d.z);
		pOff /= 3;
		ib.put(ibOff++, pOff);
		ib.put(ibOff++, pOff+1);
		ib.put(ibOff++, pOff+2);
		ib.put(ibOff++, pOff+2);
		ib.put(ibOff++, pOff+3);
		ib.put(ibOff++, pOff);
	}	
	public void buildUV(FloatBuffer uv, int uvPos) {
		uv.position(uvPos);
		uv.put(0).put(0);
		uv.put(0).put(1);
		uv.put(1).put(1);
		uv.put(1).put(0);
	}
	@Override
	public String toString() {
		return "Quad [ " + a + ", " + b + ", " + c + "," + d + "]"; 
	}
}
