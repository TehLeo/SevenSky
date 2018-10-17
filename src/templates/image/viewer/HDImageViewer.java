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

import com.jme3.math.Vector4f;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import templates.image.HDImage;
import templates.util.Mathf;

/**
 *
 * @author Juraj Papp
 */
public class HDImageViewer extends JFrame {

	public static void main(String[] args) {
		try {
			new HDImageViewer().start();
		} catch (Exception ex) {
			Logger.getLogger(HDImageViewer.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static HDImage init;

	HDImage hdi;
	BufferedImage img;
//	WritableImage image;

	JPanel iv;

	public float multiply = 1;

	public HDImageViewer() {
	}

	public HDImageViewer(HDImage img) {
		init = img;
	}

	public void reload() {
		int[] pix = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
		int j = 0;
		for (int i = 0; i < pix.length; i++, j += 4) {
			float r = hdi.data[j];
			float g = hdi.data[j + 1];
			float b = hdi.data[j + 2];

//			r = 1.0f-FastMath.exp(-r*multiply);
//			g = 1.0f-FastMath.exp(-g*multiply);
//			b = 1.0f-FastMath.exp(-b*multiply);
			r *= multiply;
			g *= multiply;
			b *= multiply;

			r *= 255;
			g *= 255;
			b *= 255;

			int w = i % hdi.width;
			int h = hdi.height - 1 - i / hdi.width;

			//int a = Mathf.clamp((int)(hdi.data[j+3]*255), 0, 255); 
			int a = 0xff;

			pix[w + h * hdi.width] = (a << 24) | (Mathf.clamp((int) r, 0, 255) << 16)
					| (Mathf.clamp((int) g, 0, 255) << 8) | (Mathf.clamp((int) b, 0, 255));
		}
		iv.repaint();
	}

	public void start() {
		if (init == null) {
			DataInputStream dis = null;
			try {
				//		File hdiFile = new File("/home/leo/Pictures/skytest/hdi/sky.hdi");
				File hdiFile = new File("/home/leo/Pictures/skytest/hdi/skyrender.hdi");
				dis = new DataInputStream(new BufferedInputStream(new FileInputStream(hdiFile)));
				HDImage.open(dis);
				hdi = HDImage.readHeader(dis);
				HDImage.read(dis, hdi);
			} catch (Exception ex) {
				Logger.getLogger(HDImageViewer.class.getName()).log(Level.SEVERE, null, ex);
			} finally {
				try {
					dis.close();
				} catch (IOException ex) {
					Logger.getLogger(HDImageViewer.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		} else {
			hdi = init;
		}

		img = new BufferedImage(hdi.width, hdi.height, BufferedImage.TYPE_INT_ARGB);
		int[] pix = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
		System.out.println("pix " + pix.length + " " + hdi.data.length);
		System.out.println("pix " + hdi.width + " " + hdi.height);

		int j = 0;
		Vector4f max = new Vector4f(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE);
		for (int i = 0; i < pix.length; i++, j += 4) {
			float r = hdi.data[j];
			float g = hdi.data[j + 1];
			float b = hdi.data[j + 2];
			float aa = hdi.data[j + 3];

			max.x = Math.max(max.x, r);
			max.y = Math.max(max.y, g);
			max.z = Math.max(max.z, b);
			max.w = Math.max(max.w, aa);

			r *= 255;
			g *= 255;
			b *= 255;

			int w = i % hdi.width;
			int h = hdi.height - 1 - i / hdi.width;

//			int a = Mathf.clamp((int)(hdi.data[j+3]*255), 0, 255); 
			int a = 0xff;
			pix[w + h * hdi.width] = (a << 24) | (Mathf.clamp((int) r, 0, 255) << 16)
					| (Mathf.clamp((int) g, 0, 255) << 8) | (Mathf.clamp((int) b, 0, 255));
		}
		System.out.println("HDImage: Max value " + max);
//		BufferedImage i = ImageIO.read(new File("/home/leo/Pictures/skytest/sunset.png"));

//		File file = new File("/home/leo/Pictures/skytest/sunset.png");
//		Image image = new Image(file.toURI().toString());
		iv = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(img, 0, 0, this);
			}
		};
		iv.setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));
		iv.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				double x = e.getX();
				double y = e.getY();

				int xx = (int) x;
				int yy = hdi.height - 1 - (int) y;
				if (xx >= 0 && yy >= 0 && xx < hdi.width && yy < hdi.height) {
					int i = yy * hdi.width * 4 + xx * 4;

					System.out.println("Click (" + x + ", " + y
							+ ") RGB: " + hdi.data[i] + ", " + hdi.data[i + 1]
							+ ", " + hdi.data[i + 2] + ", " + hdi.data[i + 3]);
				} else {
					System.out.println("Click (" + x + ", " + y + ") ");
				}
			}
		});

//		WritableImage img = new WritableImage
//		ImageView iw = new ImageView(i);
		final JTextField spinner = new JTextField(""+multiply);
//		spinner.sete(true);

		final JSlider slider = new JSlider(0, 100, 1);

		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				multiply = slider.getValue();
//				multiply = nw.floatValue();
				System.out.println("multiply " + multiply);
				reload();
			}
		});

		spinner.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					multiply = Float.parseFloat(spinner.getText());
					System.out.println("multiply " + multiply);
					reload();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});

		final JFileChooser f = new JFileChooser();

		JButton saveHdi = new JButton("SaveHdi");
		saveHdi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent a) {
				if (JFileChooser.APPROVE_OPTION == f.showOpenDialog(null)) {
					File file = f.getSelectedFile();
					if (file != null) {
						try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
							HDImage.open(dos);
							HDImage.writeHeader(dos, hdi.width, hdi.height, hdi.channel);
							HDImage.write(dos, hdi.data);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		});

		JToolBar bar = new JToolBar();
		bar.add(saveHdi);
		bar.add(spinner);
		bar.add(slider);

		setTitle("HDImageViewer");

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		add(bar, BorderLayout.NORTH);
		add(iv, BorderLayout.CENTER);

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
		reload();
	}

}
