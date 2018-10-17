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
package templates.image;

import com.jme3.math.FastMath;
import com.jme3.math.Vector4f;
import com.jme3.renderer.opengl.GLImageFormat;
import com.jme3.renderer.opengl.GLImageFormats;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.image.ColorSpace;
import com.jme3.util.BufferUtils;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import static templates.glsl.GSLSJmeVec.vec4;

/**
 *
 * @author Juraj Papp
 */
public class HDImage {
	public static final int CHANNEL_GRAY = 1;
	public static final int CHANNEL_RGB = 3;
	public static final int CHANNEL_RGBA = 4;
	
	
	public int width, height, channel;
	public float[] data;

	public HDImage() {	}
	public HDImage(Vector4f[] d, int w, int h) {
		width = w;
		height = h;
		channel = CHANNEL_RGBA;
		data = new float[w*h*4];
		for(int i = 0; i < d.length; i++) {
			int j = i<<2;
			Vector4f v = d[i];
			data[j++] = v.x;
			data[j++] = v.y;
			data[j++] = v.z;
			data[j++] = v.w;
		}
	}

	public HDImage(int width, int height, int channel, float[] data) {
		this.width = width;
		this.height = height;
		this.channel = channel;
		this.data = data;
	}
	
	public static boolean open(DataInputStream dis) throws IOException {
		byte h = (byte)dis.read();
		byte d = (byte)dis.read();
		byte i = (byte)dis.read();
		return h == 'h' && d == 'd' && 'i' == i;
	}
	
	public static HDImage readHeader(DataInputStream dis) throws IOException {
		int w = dis.readInt();
		int h = dis.readInt();
		int ch = dis.readInt();
		return new HDImage(w, h, ch, null);
	}
	
	public static void read(DataInputStream dis, HDImage h) throws IOException {		
		float data[] = new float[h.width*h.height*h.channel];
		h.data = data;
		for(int i = 0; i < data.length; i++) data[i] = dis.readFloat();
	}
	
	public static void open(DataOutputStream os) throws IOException {
		os.write((byte)'h');
		os.write((byte)'d');
		os.write((byte)'i');
	}
	
	public static void writeHeader(DataOutputStream os, int w, int h, int channel) throws IOException {
		os.writeInt(w);
		os.writeInt(h);
		os.writeInt(channel);
	}
	
	public static void write(DataOutputStream os, float[] data) throws IOException {		
		for(int i = 0; i < data.length; i++) os.writeFloat(data[i]);
	}
	public static void write(DataOutputStream os, Vector4f[] data) throws IOException {		
		for(int i = 0; i < data.length; i++) {
			Vector4f v = data[i];
			os.writeFloat(v.x);
			os.writeFloat(v.y);
			os.writeFloat(v.z);
			os.writeFloat(v.w);
		}
	}
	
	public static HDImage read(String file) {
		try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
			if(!open(dis)) throw new IllegalArgumentException("Not a hdi file: " + file);
			HDImage hdi = HDImage.readHeader(dis);
			HDImage.read(dis, hdi);
			return hdi;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static HDImage read(InputStream is) {
		try (DataInputStream dis = new DataInputStream(new BufferedInputStream(is))) {
			if(!open(dis)) throw new IllegalArgumentException("Not a hdi file.");
			HDImage hdi = HDImage.readHeader(dis);
			HDImage.read(dis, hdi);
			return hdi;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static Texture2D readHalfFloat(InputStream is) {
		try (DataInputStream dis = new DataInputStream(new BufferedInputStream(is))) {
			if(!open(dis)) throw new IllegalArgumentException("Not a hdi file.");
			HDImage hdi = HDImage.readHeader(dis);
			if(hdi.channel != CHANNEL_RGBA) throw new IllegalArgumentException("Channel RGBA expected, found: " + hdi.channel + ".");
			int len = hdi.width*hdi.height*4;
			
			ByteBuffer b = BufferUtils.createByteBuffer(len*2);
			b.clear();
			for(int i = 0; i < len; i++)
				b.putShort(FastMath.convertFloatToHalf(dis.readFloat()));
			b.flip();
			System.out.println(len + ", " + b.limit() + ", " + b.position() + ", " + b.capacity());
			Image img = new Image(Image.Format.RGBA16F, hdi.width, hdi.height, b, ColorSpace.Linear);
						
			Texture2D tex = new Texture2D(img);
			tex.setMinFilter(Texture.MinFilter.BilinearNearestMipMap);
			tex.setMagFilter(Texture.MagFilter.Bilinear);
			tex.setWrap(Texture.WrapMode.EdgeClamp);
			dis.close();
			return tex;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Texture2D asHalfFloat() {
		if(channel != CHANNEL_RGBA) throw new IllegalArgumentException("Channel RGBA expected, found: " + channel + ".");
		ByteBuffer b = BufferUtils.createByteBuffer(width*height*4*2);
		b.clear();
		for(int i = 0; i < data.length; i++)
			b.putShort(FastMath.convertFloatToHalf(data[i]));
		b.flip();
		Image img = new Image(Image.Format.RGBA16F, width, height, b, ColorSpace.Linear);
		Texture2D tex = new Texture2D(img);
		tex.setMinFilter(Texture.MinFilter.BilinearNearestMipMap);
		tex.setMagFilter(Texture.MagFilter.Bilinear);
		tex.setWrap(Texture.WrapMode.EdgeClamp);
		return tex;
	}
}
