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
package templates.glsl;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import templates.util.Mathf;
import templates.glsl.GSLSFloat.*;

/**
 *
 * @author Juraj Papp
 */
public class GSLSJmeVec {
	public static Vector2f vec2(double a) { return vec2(a, a); }
	public static Vector2f vec2(double a, double b) {
		return new Vector2f((float)a, (float)b);
	}
	public static Vector3f vec3(double a) { return vec3(a, a, a); }
	public static Vector3f vec3(Vector2f a, double b) { return vec3(a.x, a.y, b); }
	public static Vector3f vec3(double a, Vector2f b) { return vec3(a, b.x, b.y); }
	public static Vector3f vec3(double a, double b, double c) {
		return new Vector3f((float)a, (float)b, (float)c);
	}
	public static Vector4f vec4(double a) { return vec4(a, a, a, a); }
	public static Vector4f vec4(Vector2f a, double b, double c) { return vec4(a.x, a.y, b, c); }
	public static Vector4f vec4(double a, Vector2f b, double c) { return vec4(a, b.x, b.y, c); }
	public static Vector4f vec4(double a, double b, Vector2f c) { return vec4(a, b, c.x, c.y); }
	public static Vector4f vec4(Vector2f a, Vector2f b) { return vec4(a.x, a.y, b.x, b.y); }
	public static Vector4f vec4(Vector3f a, double b) { return vec4(a.x, a.y, a.z, b); }
	public static Vector4f vec4(double a, Vector3f b) { return vec4(a, b.x, b.y, b.z); }
	public static Vector4f vec4(double a, double b, double c, double d) {
		return new Vector4f((float)a, (float)b, (float)c, (float)d);
	}
	
	public static final Vector4f fract(Vector4f v) {
		return vec4(GSLSFloat.fract(v.x), GSLSFloat.fract(v.y),
				GSLSFloat.fract(v.z), GSLSFloat.fract(v.w));
	}
	public static final Vector4f clamp(Vector4f v, float min, float max) {
		return new Vector4f(
			Mathf.clamp(v.x, min, max), 
			Mathf.clamp(v.y, min, max),
			Mathf.clamp(v.z, min, max), 
			Mathf.clamp(v.w, min, max));
	}
	public static final float dot(Vector3f a, Vector3f b) {return a.dot(b);}	
	
	public static final Vector3f exp(Vector3f a) {
		return new Vector3f(GSLSFloat.exp(a.x), GSLSFloat.exp(a.y), GSLSFloat.exp(a.z));
	}
	public static final int toRGB(Vector3f v) {
		return 0xff000000 | (Mathf.clamp((int)(v.x*255f),0,255)<<16) |
				(Mathf.clamp((int)(v.y*255f),0,255)<<8) |
				Mathf.clamp((int)(v.z*255f),0,255);
	} 
	public static final int toARGB(Vector4f v) {
		return (Mathf.clamp((int)(v.w*255f),0,255)<<24) | (Mathf.clamp((int)(v.x*255f),0,255)<<16) |
				(Mathf.clamp((int)(v.y*255f),0,255)<<8) |
				Mathf.clamp((int)(v.z*255f),0,255);
	} 
}
