package templates.util;

import com.jme3.renderer.RenderContext;
import com.jme3.renderer.Renderer;
import com.jme3.renderer.opengl.GLImageFormat;
import com.jme3.renderer.opengl.GLRenderer;
import com.jme3.texture.Image;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class MiscUtils {
    /***
     * One sided clamp function, eg. -1 = clamp(255, 300,...), -2 = clamp(254),
     * 50 = clamp(50)...
     * @param n - an integer
     * @return clamped byte value
     */
    public static byte clmp(int n) {
        return (byte)(((255-n)>>31)|n);
    }
	/**
	 * int aBack = (int)(c >> 32);
		int bBack = (int)c;
	 */
    public static long pack(int a, int b) {
		return (((long)a) << 32) | (b & 0xFFFFFFFFL);
	}
    
    public static <T> T read(Object o, String name) {
        try {
            java.lang.reflect.Field f = o.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return (T)f.get(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	public static boolean write(Object o, String name, Object data) {
        try {
            java.lang.reflect.Field f = o.getClass().getDeclaredField(name);
            f.setAccessible(true);
			f.set(o, data);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
		return false;
    }
	public static boolean write(Object o, String name, Object data, Class c) {
        try {
            java.lang.reflect.Field f = c.getDeclaredField(name);
            f.setAccessible(true);
			f.set(o, data);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
		return false;
    }
//	public static T[] <T> array(T... arr) {
//		T[] t = Array.newInstance(arr.getClass(), arr.length);
//		for(int i = 0; i < arr.length; i++) t[i] = arr[i];
//		return t;
//	}
	public static boolean writeInt(Object o, String name, int data) {
        try {
			Field f = o.getClass().getDeclaredField(name);
			f.setAccessible(true);
			f.setInt(o, data);
			return true;
				
        } catch (Exception e) {
            e.printStackTrace();
        }
		return false;
    }
	public static boolean writeInt(Object o, Class cls, String name, int data) {
        try {
			Field f = cls.getDeclaredField(name);
			f.setAccessible(true);
			f.setInt(o, data);
			return true;
				
        } catch (Exception e) {
            e.printStackTrace();
        }
		return false;
    }
	public static void printFields(Object o) {
		Field[] fields = o.getClass().getDeclaredFields();
		for(Field f : fields) System.out.println(f);
	}
	public static ArrayList<String> readAllLines(InputStream is) throws IOException {
		ArrayList<String> lines = new ArrayList<>();
		String str;
		try(BufferedReader in = new BufferedReader(new InputStreamReader(is))) {
			while ((str = in.readLine()) != null) lines.add(str);
		}
		return lines;
	}
	public static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(Math.max(32, in.available()));
         byte[] buf = new byte[2048];
            long total = 0;
            while (true) {
              int r = in.read(buf);
              if (r == -1) {
                break;
              }
              out.write(buf, 0, r);
              total += r;
        }
        byte[] arr = out.toByteArray();
        out.close();
        return arr;
    }
	public static GLImageFormat getFormat(Renderer r, Image.Format f, boolean isSrgb) {
		Object o = read(r, "texUtil");
		GLImageFormat[][] formats = read(o, "formats");
		if (isSrgb) {
            return formats[1][f.ordinal()];
        } else {
            return formats[0][f.ordinal()];
        }
	}
	public static RenderContext getRenderContext(Renderer r) {
		return read(r, "context");
	}
}
