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
 *
 * @author Juraj Papp
 */
public class Triangle3 implements BMesh {
	public Vector3d a = new Vector3d(), b = new Vector3d(), c = new Vector3d();

	public Triangle3() {
	}

	public Triangle3(Vector3d a, Vector3d b, Vector3d c) {
		this.a.set(a);
		this.b.set(b);
		this.c.set(c);
	}
	
	public Vector3d get(int i) {
		if(i == 0) return a;
		if(i == 1) return b;
		if(i == 2) return c;
		return null;
	}
	public Vector3d getOther(int i, int j) {
		i += j;
		if(i == 1) return c;
		if(i == 2) return b;
		if(i == 3) return a;
		return null;
	}
	
	public String toString() {
		return "Triangle [ " + a + ", " + b + ", " + c + "]"; 
	}

	@Override
	public int estimateVertexCount() {
		return 3;
	}

	@Override
	public int estimateTriangleCount() {
		return 1;
	}

	@Override
	public void build(FloatBuffer pos, IndexBuffer ib, int pOff, int ibOff) {
		pos.position(pOff);
		pos.put((float)a.x).put((float)a.y).put((float)a.z);
		pos.put((float)b.x).put((float)b.y).put((float)b.z);
		pos.put((float)c.x).put((float)c.y).put((float)c.z);
		pOff /= 3;
		ib.put(ibOff++, pOff);
		ib.put(ibOff++, pOff+1);
		ib.put(ibOff++, pOff+2);
	}
}	
