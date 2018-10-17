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

import com.jme3.math.Vector4f;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import templates.util.Mathf;

/**
 *
 * @author Juraj Papp
 */
public class Vector2DLut implements LookupTable2 {
	public byte[] data;
	public int width, height;
	public int channels;
	public Vector2DLut(BufferedImage img) {
		width = img.getWidth();
		height = img.getHeight();
		DataBuffer buf = img.getRaster().getDataBuffer();
		//data = ((DataBufferInt)buf).getData();
		data = ((DataBufferByte)buf).getData();
		channels = img.getType() == BufferedImage.TYPE_4BYTE_ABGR?4:3;
	}
//	public Vector2DLut(byte[] img, int w, int h) {
//		width = w;
//		height = h;
//		this.data = img;
//	}
	@Override
	public Vector4f lookup(float x, float y) {
		//x = x*0.5f+0.5f; y = y*0.5f-0.5f;
		int xx = Mathf.clamp((int)(x*(width-1)), 0, width-1);
		int yy = height-Mathf.clamp((int)(y*(height-1)), 0, height-1)-1;
		int index = (xx+yy*width)*channels;
		if(channels == 4) index++;
		
		Vector4f v = new Vector4f(data[index+2]&0xff, data[index+1]&0xff, 0, 1f).multLocal(2f/255f);
		v.subtractLocal(1, 1, 1, 0);
		
		System.err.println(index);
		
//		Vector4f v = new Vector4f((argb>>16)&0xff, ((argb)>>8)&0xff, argb&0xff, 1f).multLocal(2f/255f);
//		v.subtractLocal(1, 1, 1, 0);
		return v;
//		return new Vector3f((argb>>16)&0xff, ((argb)>>8)&0xff, argb&0xff).multLocal(1f/255f);
	}
}
