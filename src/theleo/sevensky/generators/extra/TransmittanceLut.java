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
package theleo.sevensky.generators.extra;

import theleo.sevensky.generators.extra.LookupTable2;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import templates.util.Mathf;


/**
 *
 * @author Juraj Papp
 */
public class TransmittanceLut implements LookupTable2 {
	public Vector4f[] data;
	public int width, height;
	public TransmittanceLut(Vector4f[] img, int w, int h) {
		width = w;
		height = h;
		this.data = img;
//		data = ((DataBufferInt)img.getRaster().getDataBuffer()).getData();
	}
	public Vector4f lookup2(float alt, float mu) {
		//mu=x*1.25f-0.25f
		//x = (mu+0.25f) / 1.25
		return lookup((mu+0.25f)/1.25f, alt);
	}
	@Override
	public Vector4f lookup(float x, float y) {
		int xx = Mathf.clamp((int)(x*(width-1)), 0, width-1);
		int yy = Mathf.clamp((int)(y*(height-1)), 0, height-1);
		return data[xx+yy*width];
//		int argb = data[xx+yy*width];
//		return new Vector3f((argb>>16)&0xff, ((argb)>>8)&0xff, argb&0xff).multLocal(1f/255f);
	}
}
