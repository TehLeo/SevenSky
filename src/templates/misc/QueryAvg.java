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
package templates.misc;

import com.jme3.renderer.opengl.GL;
import templates.util.IntAvg;

/**
 *
 * @author Juraj Papp
 */
public class QueryAvg {
	public String title;
	public QueryObject obj;
	public IntAvg avg;

	public QueryAvg(String desc) {
		title = desc;
		obj = QueryObject.create();
		avg = new IntAvg(32);
	}
	public void beginTimeElapsed() {
		begin(GL.GL_TIME_ELAPSED);
	}
	public void begin(int target) {
		int res = obj.getNow();
		if(res != -1) avg.add(res);
		obj.begin(target);
	}
	public void end() {
		obj.end();
	}
	public int getAvg() {
		return avg.avg();
	}
	public float getAvgMs() {
		return getAvg()*0.000001f;
	}
	@Override
	public String toString() {
		return title + ": " + getAvg();
	}
	public String toStringMs() {
		return title + ": " + getAvg()*0.000001f;
	}
}
