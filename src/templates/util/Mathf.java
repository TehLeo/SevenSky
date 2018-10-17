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
package templates.util;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import templates.geom.Vec2i;
import templates.geom.Vector3d;

/**
 * 16,777,217 is the first integer a float cannot represent
 * 
 * 16,760,833 .
 * 16,771,073 .
 * 16,777,217 .
 *-16,777,217 .
 * 
 *
 * @author Juraj Papp
 */
public class Mathf {
//   private static final int   BIG_ENOUGH_INT  = 32*16*1024;//16 * 1024 - 1024 * 15;
//   private static final float BIG_ENOUGH_FLOOR = BIG_ENOUGH_INT;
//   private static final float BIG_ENOUGH_ROUND = BIG_ENOUGH_INT + 0.5f;
   
//   private static final double BIG_ENOUGH_FLOOR = BIG_ENOUGH_INT;
//   private static final double BIG_ENOUGH_ROUND = BIG_ENOUGH_INT + 0.5;

//   public static int fastFloor(float x) {
//      return (int) (x + BIG_ENOUGH_FLOOR) - BIG_ENOUGH_INT;
//   }
	public static boolean isEqual(Vector3f a, Vector3f b, float eps) {
		return Math.abs(a.x-b.x) <= eps && 
			   Math.abs(a.y-b.y) <= eps && 
			   Math.abs(a.z-b.z) <= eps;
	}
	public static boolean isEqual(Vector3d a, Vector3d b, float eps) {
		return Math.abs(a.x-b.x) <= eps && 
			   Math.abs(a.y-b.y) <= eps && 
			   Math.abs(a.z-b.z) <= eps;
	}
	public static boolean isEqual(float x, float y, float eps) {
		return Math.abs(x-y) <= eps;
	}
	public static boolean isEqual(double x, double y, double eps) {
		return Math.abs(x-y) <= eps;
	}
	public static float fract(float x) {
		return x - floor(x);
	}
	public static double fract(double x) {
		return x - Math.floor(x);
	}
	public static float sign2(float x) { return x < 0?-1:1;}
	public static Vector3f max(Vector3f a, Vector3f b) {
		return new Vector3f(Math.max(a.x, b.x),
				Math.max(a.y, b.y),
				Math.max(a.z, b.z));
	}
	public static Vector3f min(Vector3f a, Vector3f b) {
		return new Vector3f(Math.min(a.x, b.x),
				Math.min(a.y, b.y),
				Math.min(a.z, b.z));
	}
	public static Vector3d max(Vector3d a, Vector3d b) {
		return new Vector3d(Math.max(a.x, b.x),
				Math.max(a.y, b.y),
				Math.max(a.z, b.z));
	}
	public static Vector3d min(Vector3d a, Vector3d b) {
		return new Vector3d(Math.min(a.x, b.x),
				Math.min(a.y, b.y),
				Math.min(a.z, b.z));
	}
	public static int mod(int a, int b) {
		return (a%b+b)%b;
	}
	public static float mod(float a, float b) {
		return (a%b+b)%b;
	}
	public static float mix(float x, float y, float a) {
		return x*(1.0f-a)+ y* a;
	}
	public static double mix(double x, double y, double a) {
		return x*(1.0-a)+ y* a;
	}
	public static float clamp(float x, float min, float max) {
       return x <= min? min :(x >= max ? max : x);
	}
	public static int clamp(int x, int min, int max) {
       return x <= min? min :(x >= max ? max : x);
	}
   public static int floor(float x) {
       int cast = (int)x;
       if(x >= 0 || x == cast) return cast;
       return cast-1;
   }
   
   public static int ceil(float x) {
       return -floor(-x);
   }
   //clockwise rotations
	public static Vec2i rotate180(int x, int y) {
		return new Vec2i(-x,-y);
	}
	public static Vec2i rotate90(int x, int y) {
		return new Vec2i(y, -x);
	}
	public static Vec2i rotate270(int x, int y) {
		return new Vec2i(-y, x);
	}
   
    public static Vector2f rotate180(float x, float y) {
		return new Vector2f(-x,-y);
	}
	public static Vector2f rotate90(float x, float y) {
		return new Vector2f(y, -x);
	}
	public static Vector2f rotate270(float x, float y) {
		return new Vector2f(-y, x);
	}
	
	
	
	
	
	public static float[] multNew(float[] arr, float val) {
		float[] store = new float[arr.length];
		mult(arr, val, store);
		return store;
	}
	public static double[] multNew(double[] arr, double val) {
		double[] store = new double[arr.length];
		mult(arr, val, store);
		return store;
	}
	public static float[] set(float[] from, float[] to) {
		System.arraycopy(from, 0, to, 0, from.length);
		return to;
	}
	public static double[] set(double[] from, double[] to) {
		System.arraycopy(from, 0, to, 0, from.length);
		return to;
	}
	public static void mult(float[] a, float[] val) {
		for(int i = 0; i < a.length; i++) a[i] = a[i]*val[i];
	}
	public static void mult(double[] a, double[] val) {
		for(int i = 0; i < a.length; i++) a[i] = a[i]*val[i];
	}
	public static void mult(float[] a, float val) {
		for(int i = 0; i < a.length; i++) a[i] = a[i]*val;
	}
	public static void mult(double[] a, double val) {
		for(int i = 0; i < a.length; i++) a[i] = a[i]*val;
	}
	public static void mult(float[] arr, float val, float[] store) {
		for(int i = 0; i < arr.length; i++) store[i] = arr[i]*val;
	}
	public static void mult(double[] arr, double val, double[] store) {
		for(int i = 0; i < arr.length; i++) store[i] = arr[i]*val;
	}
	public static void add(float[] arr, float val, float[] store) {
		for(int i = 0; i < arr.length; i++) store[i] = arr[i]+val;
	}
	public static void add(double[] arr, double val, double[] store) {
		for(int i = 0; i < arr.length; i++) store[i] = arr[i]+val;
	}
	public static void add(double[] a, double val) {
		for(int i = 0; i < a.length; i++) a[i] = a[i]+val;
	}
	public static void add(float[] a, float val) {
		for(int i = 0; i < a.length; i++) a[i] = a[i]+val;
	}

	public static void mult(float[] a, float[] b, float[] store) {
		for(int i = 0; i < a.length; i++) store[i] = a[i]*b[i];
	}
	public static void mult(double[] a, double[] b, double[] store) {
		for(int i = 0; i < a.length; i++) store[i] = a[i]*b[i];
	}
	public static void add(float[] a, float[] b, float[] store) {
		for(int i = 0; i < a.length; i++) store[i] = a[i]+b[i];
	}
	public static void add(double[] a, double[] b, double[] store) {
		for(int i = 0; i < a.length; i++) store[i] = a[i]+b[i];
	}
	public static void add(float[] a, float[] val) {
		for(int i = 0; i < a.length; i++) a[i] = a[i]+val[i];
	}
	public static void add(double[] a, double[] val) {
		for(int i = 0; i < a.length; i++) a[i] = a[i]+val[i];
	}
	public static double sum(double[] arr) {
		double sum = 0;
		for(int i = 0; i < arr.length; i++) sum += arr[i];
		return sum;
	}
	public static float sum(float[] arr) {
		float sum = 0;
		for(int i = 0; i < arr.length; i++) sum += arr[i];
		return sum;
	}
//	public static float[] normalize(float[] arr) {
//		return mult(arr, 1f/sum(arr));
//	}
//	public static double[] normalize(double[] arr) {
//		return mult(arr, 1.0/sum(arr));
//	}
	
//	public static float[] normalize(float[] arr, float val) {
//		return mult(arr, val/sum(arr));
//	}
//	public static double[] normalize(double[] arr, double val) {
//		return mult(arr, val/sum(arr));
//	}
	
	public static float[] normalizeNew(float[] arr, float val) {
		return multNew(arr, val/sum(arr));
	}
	public static double[] normalizeNew(double[] arr, double val) {
		return multNew(arr, val/sum(arr));
	}
	
	public static float[] normalize(float[] arr, float val) {
		mult(arr, val/sum(arr), arr);
		return arr;
	}
	public static double[] normalize(double[] arr, double val) {
		mult(arr, val/sum(arr), arr);
		return arr;
	}
//   public static int fastRound(float x) {
//      return (int) (x + BIG_ENOUGH_ROUND) - BIG_ENOUGH_INT;
//   }

//   public static int fastCeil(float x) {
//       return BIG_ENOUGH_INT - (int)(BIG_ENOUGH_FLOOR-x);
//   }
//    public static void main(String[] args) {
//        System.out.println(floor2(1));
//        System.out.println(floor2(1.1f));
//        System.out.println(floor2(0.1f));
//        System.out.println(floor2(-0.1f));
//        System.out.println(floor2(-2f));
//        
//        System.out.println(Math.floor(-2f));
//    }
//    public static void main(String[] args) {
//        int i = 8388609;
//        float f = i+0.5f;
//        System.out.println("f " + f);
//        
//        float f = -1.5f;
//        int f1 = fastFloor(f);
//        int f2 = (int)Math.floor(f);
//            
//        System.out.println(f1);
//        System.out.println(f2);
//    }
//    public static void main(String[] args) {
//
//        System.out.println("test");
//        for(int j = 0; j < Integer.MAX_VALUE; j++) {
//            int i = j;
//            float f = i+0.5f;
//            if((int)(f) != i) {
//                System.out.println("maxfloat " + i); 
//                System.out.println("f " + f);
//                break;
//            }
//            int f1 = fastFloor(f);
//            int f2 = (int)Math.floor(f);
//            if(f1 != f2 || f1 != (i)) {
//                System.out.println("Error " + f);
//                System.out.println("fast " + f1);
//                System.out.println("math " + f2);
//                break;
//            }
//            
//            f1 = fastFloor(-f);
//            f2 = (int)Math.floor(-f);
//            if(f1 != f2 || f1 != (-i-1)) {
//                System.out.println("Error " + (-i-1));
//                System.out.println("fast " + f1);
//                System.out.println("math " + f2);
//                break;
//            }
//            
//        }
//        System.out.println("done");
//    }
	/**
	 * Volume of a tetrahedron
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @return Volume of a tetrahedron
	 */
	public static double volume(Vector3d a, Vector3d b, Vector3d c, Vector3d d) {
		return b.subtract(a).crossLocal(c.subtract(a)).dot(d.x-a.x, d.y-a.y, d.z-a.z);
	}
	public static Quaternion quat(Vector3f d, Vector3f z, Quaternion store) {
		Vector3f zxd = z.cross(d);
		store.set(zxd.x, zxd.y, zxd.z, FastMath.sqrt(z.dot(z)*d.dot(d)) + z.dot(d));
		store.normalizeLocal();
		return store;
	}
}
