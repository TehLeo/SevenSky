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
package templates.image.viewer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 *
 * @author Juraj Papp
 */
public class VectorPanel extends JPanel {
	public float MIN_ZOOM = 1e-15f, MAX_ZOOM = 1e15f;
	public static class ColorShape {
		public Color color;
		public Shape shape;
		public boolean fill = false;

		public ColorShape() {}
		public ColorShape(Color color, Shape shape) {
			this.color = color;
			this.shape = shape;
		}
		public ColorShape(Color color, Shape shape, boolean fill) {
			this.color = color;
			this.shape = shape;
			this.fill = fill;
		}
	}
	
	public ArrayList<ColorShape> shapes = new ArrayList<ColorShape>();
	int x=-1,y, mX, mY;
	public float xOff, yOff, zoom=1;
	public boolean spaceBar = false;
	public VectorPanel() {
		MouseAdapter m = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON2) {
					x = e.getX();
					y = e.getY();
				}
			}
			
			@Override
            public void mouseReleased(MouseEvent e) {
                if(x != -1) {
                    xOff += e.getX() - x;
                    yOff += e.getY() - y;
                    
                    System.out.println("XOFF " + xOff + ", " + yOff);
                    x = -1;
                    repaint();
                }
            }

			@Override
			public void mouseMoved(MouseEvent e) {
				mX = e.getX();
				mY = e.getY();
				if(spaceBar) mouseDragged(e);
			}			
            @Override
            public void mouseDragged(MouseEvent e) {
                if(x != -1) {
                    xOff += e.getX() - x;
                    yOff += e.getY() - y;
                    x = e.getX();
                    y = e.getY();
                    repaint();
                }
            }
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if(e.isControlDown() || e.isAltDown() || e.isAltGraphDown() ||
						e.isShiftDown()) return;
				float z = zoom;
                zoom = Math.min(Math.max(zoom - zoom*e.getWheelRotation()*0.1f, MIN_ZOOM), MAX_ZOOM);
                if(z != zoom) {
                    float zz = (z-zoom)/(z); //1-zoom/z

                    xOff += (e.getX()-xOff)*zz;
                    yOff += (e.getY()-yOff)*zz;
 
                    repaint();
                }
			}
		};
		addMouseListener(m);
		addMouseMotionListener(m);
		addMouseWheelListener(m);
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {System.out.println("key");
				if(e.getKeyCode() == KeyEvent.VK_SPACE) {
					if(x != -1) {
						x = mX;
						y = mY;
					}
					spaceBar = true;
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_SPACE) {
					spaceBar = false;
					x = -1;
				}
			}
		});
	}
	
	public float x(int x) { return (x-xOff)/zoom;}
	public float y(int y) { return -(y-yOff)/zoom;}
	
	@Override
	protected void paintComponent(Graphics g) {
//		super.paintComponent(g); 
		Color bg = getBackground();
		if(bg != null) {
			g.setColor(bg);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
		
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.translate(xOff, yOff);
		g2.scale(zoom, -zoom);
		g2.setStroke(new BasicStroke(1f/zoom));
		
		
//		g.drawImage(img, 0, 0, this);
		draw2D(g2);
	}
	public void plot(float[] data, float x0, float x1, Color col) {
		float step = (x1-x0)/(data.length-1);
		Path2D.Float path = new Path2D.Float();
		path.moveTo(x0, data[0]);
		for(int i = 1; i < data.length; i++) {
			path.lineTo(x0+step*(i-1), data[i]);
		}
		shapes.add(new VectorPanel.ColorShape(col, path));
	}
	public void plot(double[] data, float x0, float x1, Color col) {
		float step = (x1-x0)/(data.length-1);
		Path2D.Float path = new Path2D.Float();
		path.moveTo(x0, data[0]);
		for(int i = 1; i < data.length; i++) {
			path.lineTo(x0+step*i, data[i]);
		}
		shapes.add(new VectorPanel.ColorShape(col, path));
	}
	public void draw2D(Graphics2D g) {
//		g.drawRect(50, 50, 10, 10);
		for(ColorShape s : shapes) {
			g.setColor(s.color);
			if(s.fill) g.fill(s.shape);
			else g.draw(s.shape);
		}
	}
}
