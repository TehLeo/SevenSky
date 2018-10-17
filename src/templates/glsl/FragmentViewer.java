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
package templates.glsl;

import com.jme3.math.FastMath;
import com.jme3.math.Vector4f;
import com.jme3.util.BufferUtils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;
import javax.swing.JFrame;
import static templates.glsl.GSLSJmeVec.toARGB;
import templates.image.viewer.VectorPanel;
import templates.util.DebugUtils;

/**
 *
 * @author Juraj Papp
 */
public class FragmentViewer {
	public boolean debug;
	public Fragment frag;
	public VectorPanel vp;
	BufferedImage img;

	public FragmentViewer(Fragment frag, int imgSize) {
		this.frag = frag;
		img = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_ARGB);
	}

	public BufferedImage render(int imgW, int imgH, float sx, float sy, float wx, float wy) {
		return render(frag, imgW, imgH, sx, sy, wx, wy);
	}

	public static BufferedImage render(Fragment frag, int imgW, int imgH, float sx, float sy, float wx, float wy) {
		BufferedImage img = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_ARGB);
		int[] data = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();

		float stepX = wx / imgW;
		float stepY = wy / imgH;
		int i = 0;
		for (int y = 0; y < imgH; y++) {
			for (int x = 0; x < imgW; x++) {
				data[i++] = toARGB(frag.draw(sx + stepX * x, sy + stepY * (imgH - y - 1)));
			}
		}
		return img;
	}

	public static Vector4f[] render2(Fragment frag, int imgW, int imgH, float sx, float sy, float wx, float wy) {
		Vector4f[] img = new Vector4f[imgW * imgH];

		float stepX = wx / imgW;
		float stepY = wy / imgH;
		int i = 0;
		for (int y = 0; y < imgH; y++) {
			for (int x = 0; x < imgW; x++) {
				img[i++] = frag.draw(sx + stepX * x, sy + stepY * y);
			}
		}
		return img;
	}

	public static ByteBuffer renderHalfFloat(Fragment frag, int imgW, int imgH, float sx, float sy, float wx, float wy) {
		ByteBuffer b = BufferUtils.createByteBuffer(imgW * imgH * 4 * 2);
		b.clear();

		float stepX = wx / imgW;
		float stepY = wy / imgH;
		int i = 0;
		for (int y = 0; y < imgH; y++) {
			for (int x = 0; x < imgW; x++) {
				Vector4f v = frag.draw(sx + stepX * x, sy + stepY * y);
				b.putShort(FastMath.convertFloatToHalf(v.x));
				b.putShort(FastMath.convertFloatToHalf(v.y));
				b.putShort(FastMath.convertFloatToHalf(v.z));
				b.putShort(FastMath.convertFloatToHalf(v.w));
			}
		}
		b.flip();
		return b;
	}

	public static ByteBuffer renderR16(Fragment frag, int imgW, int imgH, float sx, float sy, float wx, float wy) {
		ByteBuffer b = BufferUtils.createByteBuffer(imgW * imgH * 2);
		b.clear();

		float stepX = wx / imgW;
		float stepY = wy / imgH;
		for (int y = 0; y < imgH; y++) {
			for (int x = 0; x < imgW; x++) {
				Vector4f v = frag.draw(sx + stepX * x, sy + stepY * y);
				b.putShort(FastMath.convertFloatToHalf(v.x));
			}
		}
		b.flip();
		return b;
	}

	public static ByteBuffer renderR32(Fragment frag, int imgW, int imgH, float sx, float sy, float wx, float wy) {
		ByteBuffer b = BufferUtils.createByteBuffer(imgW * imgH * 4);
		b.clear();

		float stepX = wx / imgW;
		float stepY = wy / imgH;
		int i = 0;
		for (int y = 0; y < imgH; y++) {
			for (int x = 0; x < imgW; x++) {
				Vector4f v = frag.draw(sx + stepX * x, sy + stepY * y);
				b.putFloat(v.x);
			}
		}
		b.flip();
		return b;
	}

	public static ByteBuffer renderRG16(Fragment frag, int imgW, int imgH, float sx, float sy, float wx, float wy) {
		ByteBuffer b = BufferUtils.createByteBuffer(imgW * imgH * 2 * 2);
		b.clear();

		float stepX = wx / imgW;
		float stepY = wy / imgH;
		for (int y = 0; y < imgH; y++) {
			for (int x = 0; x < imgW; x++) {
				Vector4f v = frag.draw(sx + stepX * x, sy + stepY * y);
				b.putShort(FastMath.convertFloatToHalf(v.x));
				b.putShort(FastMath.convertFloatToHalf(v.y));
			}
		}
		b.flip();
		return b;
	}

	public static ByteBuffer renderRG32(Fragment frag, int imgW, int imgH, float sx, float sy, float wx, float wy) {
		ByteBuffer b = BufferUtils.createByteBuffer(imgW * imgH * 2 * 4);
		b.clear();

		float stepX = wx / imgW;
		float stepY = wy / imgH;
		int i = 0;
		for (int y = 0; y < imgH; y++) {
			for (int x = 0; x < imgW; x++) {
				Vector4f v = frag.draw(sx + stepX * x, sy + stepY * y);
				b.putFloat(v.x);
				b.putFloat(v.y);
			}
		}
		b.flip();
		return b;
	}

	public void repaint() {
//			int w = vp.getWidth();
//			int h = vp.getHeight();
		int w = img.getWidth();
		int h = img.getHeight();
		float zoomI = 1f / vp.zoom;
		float xOff = vp.xOff;
		float yOff = vp.yOff;
		int[] data = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
		debug = false;
		int i = 0;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				data[i++] = toARGB(frag.draw((x - xOff) * zoomI, -(y - yOff) * zoomI));
			}
		}
	}

	public void init() {
		vp = new VectorPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				AffineTransform t = g2.getTransform();
				super.paintComponent(g);
				g2.setTransform(t);

				FragmentViewer.this.repaint();
				g.drawImage(img, 0, 0, this);

			}

			@Override
			public void draw2D(Graphics2D g) {
				super.draw2D(g);

			}
		};
		vp.setBackground(Color.black);

		vp.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					float x = e.getX() - vp.xOff;
					float y = e.getY() - vp.yOff;
					x /= vp.zoom;
					y /= -vp.zoom;
					debug = true;
					Vector4f c = frag.draw(x, y);
					System.out.println("(x " + x + ", " + y + ") " + c);
				}
			}
		});

		//		BufferedImage img = new BufferedImage(IMG_WIDTH, IMG_WIDTH, BufferedImage.TYPE_INT_RGB);
		//		
		//		Graphics2D g = img.createGraphics();
		//				
		//		g.translate(IMG_WIDTH*0.5f, IMG_WIDTH*0.5f);//g.translate(EARTH_RAD, EARTH_RAD);
		//		g.scale(SCALE*IMG_WIDTH/EARTH_RAD, SCALE*IMG_WIDTH/EARTH_RAD);
		//		
		//		
		//		g.dispose();
		//		
		vp.setPreferredSize(new Dimension(512, 512));
		JFrame f = DebugUtils.createFrame(vp);

		f.pack();
		f.setLocationRelativeTo(null);
	}

}
