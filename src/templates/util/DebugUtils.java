package templates.util;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Path2D;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
//import templates.image.viewer.VectorPanel;

public class DebugUtils {
    public static Mesh showNormals(Mesh m) {
        Mesh n = new Mesh();
        n.setMode(Mesh.Mode.Lines);
        FloatBuffer pos = m.getFloatBuffer(VertexBuffer.Type.Position);
        FloatBuffer norm = m.getFloatBuffer(VertexBuffer.Type.Normal);
       
        pos.rewind();
        norm.rewind();
        float a,b,c;
        
        int lines = m.getVertexCount();
        
        FloatBuffer fb = BufferUtils.createFloatBuffer(lines*6);
        for(int i = 0; i < fb.capacity(); i+=6) {
            fb.put(a=pos.get()).put(b=pos.get()).put(c=pos.get());
            fb.put(a=(a+norm.get())).put(b=(b+norm.get())).put(c=(c+norm.get()));
        }
        
        ShortBuffer idx = BufferUtils.createShortBuffer(lines*2);
        for(short i = 0; i < idx.capacity(); i++) {
            idx.put(i);
        }
        
        fb.flip();
        idx.flip();
        
        
        
        n.setBuffer(VertexBuffer.Type.Position, 3, fb);
        n.setBuffer(VertexBuffer.Type.Index, 2, idx);
        m.updateBound();
        
        return n;
    }
    public static Geometry showNormals(Mesh m, AssetManager am) {
        Geometry geom = new Geometry("Normals", showNormals(m));
        
        
        Material mat =  new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Magenta);
        geom.setMaterial(mat);
		
		geom.getMesh().updateBound();
        
        return geom;
    }
//    public static VectorPanel plot(float[] data, float x0, float x1, Color col) {
//		VectorPanel panel = new VectorPanel();
//		panel.plot(data, x0, x1, col);
//		createFrame(panel);
//		return panel;
//	}
    public static JFrame displayImage(final Image img) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g); 
                g.drawImage(img, 0, 0, this);
            }
            
        };
        int w = img.getWidth(null), h = img.getHeight(null);
        if(w == -1) w = 100; if(h == -1) h = 100;
        panel.setPreferredSize(new Dimension(w, h));
        return createFrame(panel);             
    }
    public static JFrame displayImages(final Image... img) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g); 
				int x = 0;
                for(int i = 0; i < img.length; i++) {
					g.drawImage(img[i], x, 0, this);
					x+= img[i].getWidth(this);
				}
				
            }
            
        };
		int w = 0; int h = 0;
		for(int i = 0; i < img.length; i++) {
			int w0 = img[i].getWidth(null), h0 = img[i].getHeight(null);
			w += (w0 == -1)?100:w0; h += (h0 == -1)?100:h0;
		}
        
        panel.setPreferredSize(new Dimension(w, h));
        return createFrame(panel);             
    }
	public static void displayImage(final Image img, final float scale) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g); 
				((Graphics2D)g).scale(scale, scale);
                g.drawImage(img, 0, 0, this);
            }
            
        };
        int w = img.getWidth(null), h = img.getHeight(null);
        if(w == -1) w = 100; if(h == -1) h = 100;
        panel.setPreferredSize(new Dimension(w, h));
        createFrame(panel);             
    }
    
    public static JFrame createFrame(JPanel panel) {
        JFrame f = new JFrame();
        f.setFocusableWindowState(false);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(new BorderLayout());
        
        f.add(new JScrollPane(panel), BorderLayout.CENTER);
        
        f.setVisible(true);
        f.setSize(300, 300);
        f.setLocation(0, 0);
//        f.setLocation(0, Toolkit.getDefaultToolkit().getScreenSize().height-f.getHeight());
        return f;
    }    
}
