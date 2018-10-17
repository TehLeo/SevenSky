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

import com.jme3.math.Vector3f;
import java.util.ArrayList;

/**
 *
 * @author Juraj Papp
 */
public class Path3 /*implements BMesh*/ {
	public ArrayList<Vector3d> points = new ArrayList<Vector3d>();
	public void add(double x, double y, double z) {
		points.add(new Vector3d(x,y,z));
	}
	public void close() {
		if(!points.isEmpty()) {
			points.add(points.get(0));
		}
	}
	
	public ArrayList<Quad4> extrude(float x, float y, float z) {
		ArrayList<Quad4> l = new ArrayList<>();
		if(points.isEmpty()) return l;
		Vector3d a = points.get(0);
		for(int i = 1; i < points.size(); i++) {
			Vector3d b = points.get(i);
			Quad4 q = new Quad4(a,b,b,a);
			q.c.addLocal(x, y, z);
			q.d.addLocal(x, y, z);
			l.add(q);
			a = b;
		}
		return l;
	}

//	@Override
//	public int estimateVertexCount() {
//
//	}
//	@Override
//	public int estimateTriangleCount() {
//
//	}
//	@Override
//	public void build(FloatBuffer pos, IndexBuffer ib, int pOff, int ibOff) {
//
//	}
}
