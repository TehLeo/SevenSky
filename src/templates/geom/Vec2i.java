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

/**
 *
 * @author Juraj Papp
 */
public class Vec2i implements Comparable<Vec2i> {
	public int x, y;
	public Vec2i() {
	}
	public Vec2i(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public Vec2i(Vec2i v) {
		this.x = v.x;
		this.y = v.y;
	}
	public void set(Vec2i v) { x = v.x; y = v.y; }
	public void set(int x, int y) {
		this.x = x;
		this.y = y;
	} 
	public boolean equals(int _x, int _y) {
		return x == _x && y == _y;
	}
	public Vec2i add(int xx, int yy) {
		x += xx;
		y += yy;
		return this;
	}
	public Vec2i sub(int xx, int yy) {
		x -= xx;
		y -= yy;
		return this;
	}
	public Vec2i add(Vec2i p) {
		x += p.x;
		y += p.y;
		return this;
	}
	public Vec2i sub(Vec2i p) {
		x -= p.x;
		y -= p.y;
		return this;
	}
	public Vec2i addNew(int xx, int yy) {
		return new Vec2i(x+xx,y+yy);
	}
	public Vec2i subNew(int xx, int yy) {
		return new Vec2i(x-xx,y-yy);
	}
	public Vec2i addNew(Vec2i p) {
		return new Vec2i(x+p.x,y+p.y);
	}
	public Vec2i subNew(Vec2i p) {
		return new Vec2i(x-p.x,y-p.y);
	}
	
	public Vec2i rot180() {
		x = -x;
		y = -y;
		return this;
	}
	public Vec2i rot90() {
		int xx = x;
		x = y;
		y = -xx;
		return this;
	}
	public Vec2i rot270() {
		int xx = x;
		x = -y;
		y = xx;
		return this;
	}
	public Vec2i New() {
		return new Vec2i(x,y);
	}
	public Vec2i rot180New() {
		return new Vec2i(-x,-y);
	}
	public Vec2i rot90New() {
		return new Vec2i(y,-x);
	}
	public Vec2i rot270New() {
		return new Vec2i(-y, x);
	}
	
	@Override
	public String toString() {
		return "("+x+','+y+')'; 
	}

	
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Vec2i) {
			Vec2i i = (Vec2i)obj;
			return x == i.x && y == i.y;
		}
		return false;
	}

	@Override
	public int hashCode() {
		short s1 = (short)x;
		short s2 = (short)y;
		return s1<<16 | s2;
	}

	@Override
	public int compareTo(Vec2i o) {
		if(x < o.x) return -1;
		if(x > o.x) return 1;
		if(y < o.y) return -1;
		if(y > o.y) return 1;
		return 0;
	}
}
