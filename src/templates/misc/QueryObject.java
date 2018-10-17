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
import com.jme3.util.BufferUtils;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL15;

/**
 *
 * @author Juraj Papp
 */
public class QueryObject {
	public final int queryId;
	protected boolean canBegin = true;
	protected int target;
	protected boolean canEnd = false;
	public QueryObject(int queryId) {
		this.queryId = queryId;
	}
	public void begin(int target) {
		if(canBegin) {
			canBegin = false;
			canEnd = true;
			this.target = target;
			GL15.glBeginQuery(target, queryId);
		}
	}
	public void end() {
		if(canEnd)	{
			canEnd = false;
			GL15.glEndQuery(target);
		}
	}

	public boolean canBegin() {
		return canBegin;
	}

	public int getNow() {
		if(isResultAvailable()) return getResult();
		return -1;
	}


	public boolean isResultAvailable() {
		return GL15.glGetQueryObjecti(queryId, GL15.GL_QUERY_RESULT_AVAILABLE) == GL.GL_TRUE;
	}
	public int getResult() {
		canBegin = true;
		return GL15.glGetQueryObjecti(queryId, GL15.GL_QUERY_RESULT);
	}
	public void destroy() {
		GL15.glDeleteQueries(queryId);
	}

	public static QueryObject create() {
		IntBuffer intBuf1 = BufferUtils.createIntBuffer(1);
		GL15.glGenQueries(intBuf1);
		QueryObject q = new QueryObject(intBuf1.get(0));
		BufferUtils.destroyDirectBuffer(intBuf1);
		return q;
	}
//	public static QueryObject[] create(int n) { return null;}
}
