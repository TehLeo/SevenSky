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
package theleo.sevensky.generators;

import com.jme3.math.FastMath;
import com.jme3.math.Vector4f;
import java.awt.image.BufferedImage;
import templates.glsl.Fragment;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.image.ColorSpace;
import templates.glsl.FragmentViewer;
import static templates.glsl.GSLSJmeVec.*;
import static templates.glsl.GSLSFloat.*;
import static templates.util.Mathf.*;
import static com.jme3.math.FastMath.sin;
import static com.jme3.math.FastMath.cos;
import templates.image.HDImage;
import templates.image.viewer.HDImageViewer;
import theleo.sevensky.core.SkyVars;
import theleo.sevensky.elements.Clouds;
import theleo.sevensky.elements.Sky;
import theleo.sevensky.generators.extra.PathLut;
import theleo.sevensky.generators.extra.TransmittanceLut;

/**
 *
 * @author Juraj Papp
 */
public class SkyLutGenerator {
	static FragmentViewer v;

	public static void main(String[] args) {
		v = new FragmentViewer(frag, 512);

				
		SkyVars c = SkyVars.Earth();
		
//		System.out.println(atmosRay(0.75f, 0));
//		if(1 == 1) return;
//		v.init();
//		v.vp.zoom = 512/1;
//		v.vp.yOff = 512;

		int renderSize = 256;
		
//		Vector4f[] lut = createTransmittanceLut2(c, renderSize);
//		try(DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("sunlutdot25.hdi")))) {
//			HDImage.open(dos);
//			HDImage.writeHeader(dos, renderSize, renderSize, HDImage.CHANNEL_RGBA);
//			HDImage.write(dos, lut);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}

//		HDImage img = HDImage.read("assets/Textures/sunlutdot25.hdi");
//		new HDImageViewer(img).start();

		
		
		
//		BufferedImage i = createSunTransmittanceLut(c, renderSize);
//		BufferedImage i = createTransmittanceLut(c, renderSize);
//		BufferedImage i = createInscatterLut(c, renderSize, new Vector3f(0,1,0).normalizeLocal(), 500);
//		DebugUtils.displayImage(i).setLocationRelativeTo(null);
		
		renderSize = 512;
		Vector4f[] lut = createInscatterLut2(c, renderSize, new Vector3f(0,1,1).normalizeLocal(), 0);
//		Vector4f[] lut = createPathLengthLut2(c, renderSize);
//		Vector4f[] lut = createCloudPathLut2(c, renderSize);
//		Vector4f[] lut = createTransmittanceLut2(c, renderSize);

//		for(Vector4f l : lut) {
//			l.x = 1f-FastMath.exp(-4*l.x);
//			l.y = 1f-FastMath.exp(-4*l.y);
//			l.z = 1f-FastMath.exp(-4*l.z);
//		}

		HDImageViewer hd = new HDImageViewer(new HDImage(lut, renderSize, renderSize));
		hd.multiply = 100;
		hd.start();
		
//		v = createInscatterLut3(c, renderSize, new Vector3f(0,1,0).normalizeLocal(), 500);
//		v.init();
//		PathLut plut = new PathLut(createPathLengthLut2(c, renderSize), renderSize, renderSize);
//		
//		Vector4f[] v = new Vector4f[renderSize*50];
//		for(int i = 0; i < renderSize; i++) {
//			Vector4f vv = vec4(vec3(plut.data[i+2*plut.width].x), 0.0);
//			for(int j = 0; j < 50; j++) v[i+j*renderSize] = vv;
//		}
//		new HDImageViewer(new HDImage(v, renderSize, 50)).start();
		
		//transmittance table
		//T a-c = Ta-b * T b-c
		
//		try {
//			ImageIO.write(i, "png", new File("sunlutdot25.png"));
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		}
	}
	
	/**
	 * Creates transmittance lookup table,
	 * x axis -> view dot zenith range [-0.25,1]
	 * y axis -> altitude [0-1], 0 on earth, 1 atmosphere boundary
	 * @param c - constants
	 * @param texSize - texture size
	 * @return 
	 */
	public static BufferedImage createTransmittanceLut(SkyVars c, int texSize) {		
		BufferedImage i = FragmentViewer.render((float x, float y) -> 
//				vec4(transmittance(c, y*y*y*y, x*1.25f-0.25f),1),
				vec4(transmittance(c, y*y*y*y, (float)Math.pow(x*1.6f-0.6f, 3)),1),
		texSize, texSize, 0, 0, 1, 1);
		return i;
	}
	public static Vector4f[] createTransmittanceLut2(SkyVars c, int texSize) {
		return FragmentViewer.render2((float x, float y) -> 
//				vec4(transmittance(c, y*y*y*y, x*1.25f-0.25f),1),
				vec4(transmittance(c, y*y*y*y, (float)Math.pow(x*1.6f-0.6f, 3)),1),
//				vec4(transmittance(c, y, x*2-1),1),
		texSize, texSize, 0, 0, 1, 1);
	}
	public static Texture2D createTransmittanceLut3(SkyVars c, int texSize) {
		Image img = new Image(Image.Format.RGBA16F, texSize, texSize, FragmentViewer.renderHalfFloat((float x, float y) -> 
//				vec4(transmittance(c, y*y*y*y, x*1.25f-0.25f),1),
				vec4(transmittance(c, y*y*y*y, (float)Math.pow(x*1.6f-0.6f, 3)),1),
		texSize, texSize, 0, 0, 1, 1), ColorSpace.Linear);
		Texture2D tex = new Texture2D(img);
//		tex.setMinFilter(Texture.MinFilter.BilinearNearestMipMap);
		tex.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
		tex.setMagFilter(Texture.MagFilter.Bilinear);
		tex.setWrap(Texture.WrapMode.EdgeClamp);
		return tex;
	}
	public static BufferedImage createSunTransmittanceLut(SkyVars c, int texSize) {		
		BufferedImage i = FragmentViewer.render((float x, float y) -> 
				vec4(sunTransmittance(c, y, x*1.25f-0.25f),1),
		texSize, texSize, 0, 0, 1, 1);
		return i;
	}
	public static BufferedImage createPathLengthLut(SkyVars c, int texSize) {		
		BufferedImage i = FragmentViewer.render((float x, float y) -> 
				pathLength(c, y*y*y*y, (float)Math.pow(x*2-1, 3)),
//				pathLength(c, y*y*y*y, x*2-1),
		texSize, texSize, 0, 0, 1, 1);
		return i;
	}
	public static Vector4f[] createPathLengthLut2(SkyVars c, int texSize) {
		return FragmentViewer.render2((float x, float y) -> 
				pathLength(c, y*y*y*y, (float)Math.pow(x*2-1, 3)),
//				pathLength(c, y*y*y*y, x*2-1),
		texSize, texSize, 0, 0, 1, 1);
	}
	public static Texture2D createPathLengthLut3(SkyVars c, int texSize) {
		Image img = new Image(Image.Format.R32F, texSize, texSize, FragmentViewer.renderR32((float x, float y) -> 
				pathLength(c, y*y*y*y, (float)Math.pow(x*2-1, 3)),
//				pathLength(c, y*y*y*y,  x*2-1),
		texSize, texSize, 0, 0, 1, 1), ColorSpace.Linear);
		Texture2D tex = new Texture2D(img);
//		tex.setMinFilter(Texture.MinFilter.BilinearNearestMipMap);
		tex.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
		tex.setMagFilter(Texture.MagFilter.Bilinear);
		tex.setWrap(Texture.WrapMode.EdgeClamp);
		return tex;
	}
	
	public static BufferedImage createInscatterLut(SkyVars c, int texSize, Vector3f sunLight, float altitude) {
		try {
//			TransmittanceLut lut = null;
			TransmittanceLut lut = new TransmittanceLut(createTransmittanceLut2(c, 256), 256, 256);
//			TransmittanceLut lut = new TransmittanceLut(ImageIO.read(SkyLutGenerator.class.getResourceAsStream("/Textures/sunlutdot25.png")));
			PathLut	plut = new PathLut(createPathLengthLut2(c, 256), 256, 256);

			BufferedImage i = FragmentViewer.render((float x, float y) -> {
			Vector3f pos = new Vector3f(0,c.getF(Sky.PLANET_RADIUS)+altitude,0);

			float az = (x*2.0f-1.0f)*FastMath.PI;
			float ele = (y-0.5f)*FastMath.PI;
			ele = sin(ele);
			//y = (y*2-1);
			float s = FastMath.sqrt(1-ele*ele);
			Vector3f dir = new Vector3f(s*cos(az),ele,s*sin(az));

			Vector3f res = computeInscatter(c, lut, plut, pos, dir, 0.76f, sunLight.normalize());
			//res.multLocal(100);

			return vec4(res,1);
			}, texSize, texSize, 0, 0, 1, 1);
			return i;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	public static Vector4f[] createInscatterLut2(SkyVars c, int texSize, Vector3f sunLight, float altitude) {
		try {
//			TransmittanceLut lut = null;
			TransmittanceLut lut = new TransmittanceLut(createTransmittanceLut2(c, 256), 256, 256);
//			TransmittanceLut lut = new TransmittanceLut(ImageIO.read(SkyLutGenerator.class.getResourceAsStream("/Textures/sunlutdot25.png")));
			PathLut	plut = new PathLut(createPathLengthLut2(c, 256), 256, 256);

			Vector4f[] i = FragmentViewer.render2((float x, float y) -> {
			Vector3f pos = new Vector3f(0,c.getF(Sky.PLANET_RADIUS)+altitude,0);

			float az = (x*2.0f-1.0f)*FastMath.PI;
			float ele = (y-0.5f)*FastMath.PI;
			ele = sin(ele);
			//y = (y*2-1);
			float s = FastMath.sqrt(1-ele*ele);
			Vector3f dir = new Vector3f(s*cos(az),ele,s*sin(az));

			Vector3f res = computeInscatter(c, lut, plut, pos, dir, 0.76f, sunLight.normalize());
			//res.multLocal(100);

			return vec4(res,1);
			}, texSize, texSize, 0, 0, 1, 1);
			return i;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	public static FragmentViewer createInscatterLut3(SkyVars c, int texSize, Vector3f sunLight, float altitude) {
		try {
//			TransmittanceLut lut = null;
			TransmittanceLut lut = new TransmittanceLut(createTransmittanceLut2(c, 256), 256, 256);
//			TransmittanceLut lut = new TransmittanceLut(ImageIO.read(SkyLutGenerator.class.getResourceAsStream("/Textures/sunlutdot25.png")));
			PathLut	plut = new PathLut(createPathLengthLut2(c, 256), 256, 256);

			FragmentViewer f = new FragmentViewer((float x, float y) -> {
				if(x < 0 || y < 0 || x > 1 || y > 1) return vec4(0.0,0.0,0.0,1.0);
				
			Vector3f pos = new Vector3f(0,c.getF(Sky.PLANET_RADIUS)+altitude,0);

			float az = (x*2.0f-1.0f)*FastMath.PI;
			float ele = (y-0.5f)*FastMath.PI;
			ele = sin(ele);
			//y = (y*2-1);
			float s = FastMath.sqrt(1-ele*ele);
			Vector3f dir = new Vector3f(s*cos(az),ele,s*sin(az));

			Vector3f res = computeInscatter(c, lut, plut, pos, dir, 0.76f, sunLight.normalize());
			res.multLocal(100);
			//res.multLocal(0.0001f);

			return vec4(res,1);
			}, 512);
			return f;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	public static BufferedImage createCloudPathLut(SkyVars c, int texSize) {		
		BufferedImage i = FragmentViewer.render((float x, float y) -> 
				vec4(cloudPath(c, y*y*y*y, x*2-1), 0, 1),
		texSize, texSize, 0, 0, 1, 1);
		return i;
	}
	public static Vector4f[] createCloudPathLut2(SkyVars c, int texSize) {		
		Vector4f[] i = FragmentViewer.render2((float x, float y) -> 
				vec4(cloudPath(c, y*y*y*y, x*2-1), 0, 1),
		texSize, texSize, 0, 0, 1, 1);
		return i;
	}
	public static Texture2D createCloudPathLut3(SkyVars c, int texSize) {		
		Image img = new Image(Image.Format.RG32F, texSize, texSize, FragmentViewer.renderRG32((float x, float y) -> 
				vec4(cloudPath(c, y*y*y*y, x*2-1), 0, 1),
		texSize, texSize, 0, 0, 1, 1), ColorSpace.Linear);
		Texture2D tex = new Texture2D(img);
//		tex.setMinFilter(Texture.MinFilter.BilinearNearestMipMap);
		tex.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
		tex.setMagFilter(Texture.MagFilter.Bilinear);
		tex.setWrap(Texture.WrapMode.EdgeClamp);
		return tex;
	}
//	public static Texture2D createCloudPathLut3(Constants c, int texSize) {		
//		 i = FragmentViewer.render3((float x, float y) -> 
//				vec4(cloudPath(c, y, x*2-1), 0, 1),
//		texSize, texSize, 0, 0, 1, 1);
//		return i;
//	}
	public static Vector2f cloudPath(SkyVars c, float alt, float mu) {
		Vector3f origin = new Vector3f(0, 1+c.getF(Sky.PLANET_RADIUS)+alt*(c.getF(Sky.ATMOSPHERE_RADIUS)-c.getF(Sky.PLANET_RADIUS)), 0);
		Vector3f dir = new Vector3f(sqrt(1-mu*mu),mu,0);
		
//		ArrayList<Float> list = new ArrayList<>(6);

		Vector2f res = new Vector2f();
		
		Vector2f from = new Vector2f();
		if(calcForSphere(origin, dir, c.getF(Sky.PLANET_RADIUS)+c.getF(Clouds.VOLUMETRIC_CLOUDS_FROM), from)) {
			if(from.x >= 0) {}
			else if(from.y >= 0) {from.x = from.y;}
			else from = null;
		} else from = null;
		
		Vector2f to = new Vector2f();
		if(calcForSphere(origin, dir, c.getF(Sky.PLANET_RADIUS)+c.getF(Clouds.VOLUMETRIC_CLOUDS_TO), to)) {
			if(to.x >= 0) {}
			else if(to.y >= 0) {to.x = to.y;}
			else to = null;
		} else to = null;
		
		Vector2f earth = new Vector2f();
		if(calcForSphere(origin, dir, c.getF(Sky.PLANET_RADIUS), earth)) {
//			earth.x = Math.min(Math.abs(earth.x), Math.abs(earth.y));
			if(earth.x >= 0) {}
			else if(earth.y >= 0) {earth.x = earth.y;}
			else earth = null;
		} else earth = null;
		
		if(origin.y <= c.getF(Sky.PLANET_RADIUS)) {
			//inside planet rad..
			//treat planet as transparent (eg. sea below sea level, or valley)
			res.x = from.x;
			res.y = to.y;
		}
		else if(origin.y <= c.getF(Sky.PLANET_RADIUS)+c.getF(Clouds.VOLUMETRIC_CLOUDS_FROM)) {
			if(earth == null) {
				res.x = from.x;
				res.y = to.y;
			}
		}
		else if(origin.y <= c.getF(Sky.PLANET_RADIUS)+c.getF(Clouds.VOLUMETRIC_CLOUDS_TO)) {
			res.x = 0;
			if(from != null && to != null) {
				res.y = Math.min(from.x, to.x);
			}
			else if(from != null) {
				res.y = from.x;
			}
			else if(to != null) {
				res.y = to.x;
			}
			else throw new IllegalArgumentException();
		}
		else {
			if(to != null) {
				res.x = to.x;
				if(from != null) res.y = from.x;
				else res.x = to.y;
			}
		}		
		return res;
		
		
//		Collections.sort(list);
//		if(list.isEmpty()) {
//			//skip
////			System.out.println("Skip");
//			return new Vector2f(0,0);
//		}
//		else if(list.size() == 1) {
//			//inside, just one
////			System.out.println(list.get(0));
//			return new Vector2f(0,list.get(0));
//		}
//		else {
//			//from, to
//			float f1 = list.get(0);
//			float f2 = list.get(1);
//			if(earth != null) {
//				if(f1 == earth.x || f1 == earth.y) {
////					System.out.println("Skip");
//					return new Vector2f(0,0);
//				}
//			}
////			System.out.println(f1 + ", " + f2);
//			return new Vector2f(f1, f2);
//		}
		//return null;
	}
	
	/**
	 * 
	 * @param altAbs - altitude, 0 = center, 1 = atmosphere
	 * @param mu - zenith dot view
	 * @return distance from alt to atmosphere
	 */
	public static float atmosRay(float altAbs, float mu) {	
		float dx = sqrt(1-mu*mu);
		float dy = mu;
		
		float x1 = 0;
		float x2 = dx;
		float y1 = altAbs;
		
		float D = -x2*y1;
		
		float disc = sign2(mu)*sqrt(1f-D*D);
		float x = D*dy + sign2(dy)*dx*disc;  // +-
		float y = -D*dx + abs(dy)*disc;   // +-
		
		x-= x1;
		y-= y1;
		
		return sqrt(x*x+y*y);
	}
	
	/**
	 * 
	 * @param c - constants
	 * @param scale - scale height
	 * @param alt - from [0-1] ground, atmos
	 * @param mu - view dot zenith
	 */
	public static float opticalDepth(SkyVars c, 
			float scale, float alt, float mu) {
		int SAMPLES = 128;
		
		float altAbs = (c.getF(Sky.PLANET_RADIUS)+alt*(c.getF(Sky.ATMOSPHERE_RADIUS)-c.getF(Sky.PLANET_RADIUS)))/c.getF(Sky.ATMOSPHERE_RADIUS);
		float dist = atmosRay(altAbs, mu) * c.getF(Sky.ATMOSPHERE_RADIUS);
		
		float step = dist/SAMPLES;
		
		float dx = sqrt(1-mu*mu)*step;
		float dy = mu*step;
		
		float x = dx*0.5f;
		float y = altAbs*c.getF(Sky.ATMOSPHERE_RADIUS)+dy*0.5f;
		//float x = step*0.5f;
		//float y = altAbs*c.getF(Sky.ATMOSPHERE_RADIUS)+step*0.5f;
		
//		if(v.debug) {
//			System.out.println("input " + alt + ", " + mu + " =  " + dist + ", " + altAbs);
//		}
		
		float sum = 0;
		for(int i = 0; i < SAMPLES; i++) {
			float h = sqrt(x*x+y*y)-c.getF(Sky.PLANET_RADIUS);
			h = max(h,0);
			
			sum += exp(-h/scale);
			
			x += dx;
			y += dy;
		}
		return sum*step;
	}
	static Vector2f rsi(Vector3f r0, Vector3f rd, float sr) {
		// ray-sphere intersection that assumes
		// the sphere is centered at the origin.
		// No intersection when result.x > result.y
		float a = dot(rd, rd);
		float b = 2.0f * dot(rd, r0);
		float c = dot(r0, r0) - (sr * sr);
		float d = (b*b) - 4.0f*a*c;
		if (d < 0.0) return vec2(1e5,-1e5);
		return vec2(
			(-b - sqrt(d))/(2.0*a),
			(-b + sqrt(d))/(2.0*a)
		);
	}
	public static Vector4f pathLength2(SkyVars c, float alt, float mu) {
		Vector3f origin = new Vector3f(0, 1+c.getF(Sky.PLANET_RADIUS)+alt*(c.getF(Sky.ATMOSPHERE_RADIUS)-c.getF(Sky.PLANET_RADIUS)), 0);
		Vector3f dir = new Vector3f(sqrt(1-mu*mu),mu,0);
		
		Vector3f r0 = origin;
		Vector3f r = dir;
		float rAtmos = c.getF(Sky.ATMOSPHERE_RADIUS);
		float rPlanet = c.getF(Sky.PLANET_RADIUS);
		
		Vector2f p = rsi(r0, r, rAtmos);
		if (p.x > p.y) return vec4(0,0,0,0);
		p.y = min(p.y, rsi(r0, r, rPlanet).x);
		float pathLength = p.y-p.x;
		return vec4(pathLength);
	}
	public static Vector4f pathLength(SkyVars c, float alt, float mu) {
		Vector3f origin = new Vector3f(0, 1+c.getF(Sky.PLANET_RADIUS)+alt*(c.getF(Sky.ATMOSPHERE_RADIUS)-c.getF(Sky.PLANET_RADIUS)), 0);
//		Vector3f origin = new Vector3f(0, c.getF(Sky.PLANET_RADIUS)+alt*1000+1, 0);
		Vector3f dir = new Vector3f(sqrt(1-mu*mu),mu,0);
		
			//float t0, t1;
		Vector2f t = new Vector2f();
		if(!calcForSphere(origin, dir, c.getF(Sky.ATMOSPHERE_RADIUS), t)) {
			System.out.println("here");
			return vec4(0.0);
		} 
//			if( !intersect( r, SPHERE_ATMOSPHERE, t0, t1 ) )
//			{
//				return vec3( 1.0 );
//			}
		t.x = Math.max(t.x, 0);
		Vector2f t2 = new Vector2f();
		
		if(calcForSphere(origin, dir, c.getF(Sky.PLANET_RADIUS), t2)) {
			if(t2.x >= 0) {
				t.y = t2.x;
			}
			else if(t2.y >= 0) {
				t.y = t2.y;
			}
		} 

//		Vector3f betaR = c.SCATTER_RAY;
//		Vector3f betaM = c.SCATTER_MIE;    

//		int numSamples = 32;
//		int numSamplesLight = 16;

//			int numSamples = 16;
//			int numSamplesLight = 4;
	

		float pathLength = t.y-t.x;

//		float segmentLength = pathLength / numSamples;
//		float tCurrent = t.x;

//		Vector3f sumR = vec3( 0.0 );
//		Vector3f sumM = vec3( 0.0 );
//
//		float opticalDepthR = 0.0f;
//		float opticalDepthM = 0.0f;

		float g = c.getF(Sky.SUN_MIE_G);
//		float mu = dot( dir, sunDirection );
		float phaseR = (float) (3.0 / ( 16.0 * FastMath.PI ) * ( 1.0 + mu * mu ));
		float phaseM = (float) (3.0 / (  8.0 * FastMath.PI ) * ( ( 1.0 - g * g ) * ( 1.0 + mu * mu ) ) / ( ( 2.0 + g * g ) * Math.pow( 1.0 + g * g - 2.0 * g * mu, 1.5 ) ) );
		
//		System.out.println("tx " + tCurrent);
//		return new Vector4f(pathLength, phaseR, phaseM, 0.0f);
		return vec4(vec3(pathLength), mu);
	}
	public static Vector3f sunTransmittance(SkyVars c, float alt, float mu) {
		Vector3f samplePosition = new Vector3f(
				0,c.getF(Sky.PLANET_RADIUS)+alt*(c.getF(Sky.ATMOSPHERE_RADIUS)-c.getF(Sky.PLANET_RADIUS)),0
		);
		Vector3f sunDirection = new Vector3f(sqrt(1-mu*mu),mu,0);

		Vector2f l2 = new Vector2f(-1,-1);
		calcForSphere(samplePosition, sunDirection, c.getF(Sky.PLANET_RADIUS), l2);
		boolean skip = max(l2.x, l2.y) > 0;
		
		if(skip) return vec3(0);
		
		Vector3f t = transmittance(c, alt, mu);
		return t;
	}
	public static Vector3f transmittance(SkyVars c, float alt, float mu) {
		float dR = opticalDepth(c, c.getF(Sky.RAYLEIGH_SCALE_HEIGHT), alt, mu);
		float dM = opticalDepth(c, c.getF(Sky.MIE_SCALE_HEIGHT), alt, mu);
		
//			float v = atmosRay(x, y);

		Vector3f res = exp((((Vector3f)c.get(Sky.RAYLEIGH_SCATTERING)).mult(dR).add(((Vector3f)c.get(Sky.MIE_SCATTERING)).mult(dM))).negate());
		//			Vector3f res = exp(c.SCATTER_RAY.mult(-dR)).add(c.SCATTER_MIE.mult(-dM));
		return res;
	}
	public static Vector3f computeInscatter(SkyVars c, TransmittanceLut lut, PathLut plut, Vector3f origin, Vector3f dir, float g, Vector3f sunDirection) {
				
		//float t0, t1;
		Vector2f t = new Vector2f();
		if(!calcForSphere(origin, dir, c.getF(Sky.ATMOSPHERE_RADIUS), t)) return vec3(1.0); 
//			if( !intersect( r, SPHERE_ATMOSPHERE, t0, t1 ) )
//			{
//				return vec3( 1.0 );
//			}
		t.x = Math.max(t.x, 0);
		Vector2f t2 = new Vector2f();
		if(calcForSphere(origin, dir, c.getF(Sky.PLANET_RADIUS), t2)) {
			if(t2.x > 0) {
				t.y = t2.x;
			}
			else if(t2.y > 0) {
				t.y = t2.y;
			}
		} 

		Vector3f betaR = c.get(Sky.RAYLEIGH_SCATTERING);
		Vector3f betaM = c.get(Sky.MIE_SCATTERING);    

		int numSamples = 32;
		int numSamplesLight = 16;

//			int numSamples = 16;
//			int numSamplesLight = 4;

		float ALT = (origin.y-c.getF(Sky.PLANET_RADIUS))/(c.getF(Sky.ATMOSPHERE_RADIUS)-c.getF(Sky.PLANET_RADIUS));
//		System.out.println("ALT " + ALT);
//		float segmentLength2 = plut.lookup2((origin.y-c.getF(Sky.PLANET_RADIUS))/(c.getF(Sky.ATMOSPHERE_RADIUS)-c.getF(Sky.PLANET_RADIUS)), dir.y).x;
		float pathLength2 = plut.lookup2(ALT, dir.y).x;
		float pathLength = t.y-t.x;
		
		if(v.debug) {
			System.out.println("alt " + ALT + ", mu: " + dir.y);
			System.out.println("PathLen " + pathLength + ", lut: " + pathLength2 );
		}
		
		
		float segmentLength = pathLength/ numSamples;
				
		//if(1 == 1) return vec3(abs(pathLength2-pathLength));
		
		float tCurrent = t.x;

		Vector3f sumR = vec3( 0.0 );
		Vector3f sumM = vec3( 0.0 );

		float opticalDepthR = 0.0f;
		float opticalDepthM = 0.0f;

		float mu = dot( dir, sunDirection );
		float phaseR = (float) (3.0 / ( 16.0 * FastMath.PI ) * ( 1.0 + mu * mu ));
		float phaseM = (float) (3.0 / (  8.0 * FastMath.PI ) * ( ( 1.0 - g * g ) * ( 1.0 + mu * mu ) ) / ( ( 2.0 + g * g ) * Math.pow( 1.0 + g * g - 2.0 * g * mu, 1.5 ) ) );

		
		
		boolean useLut = lut != null;
		
		for( int i = 0; i < numSamples ; i++ ) {
			Vector3f samplePosition = origin.add(dir.mult( tCurrent + 0.5f * segmentLength ));
			float height = samplePosition.length() - c.getF(Sky.PLANET_RADIUS);
//				System.out.println("i " + i + ": " + height);

			// compute optical depth for light

			float hr = exp( -height / c.getF(Sky.RAYLEIGH_SCALE_HEIGHT) ) * segmentLength;
			float hm = exp( -height / c.getF(Sky.MIE_SCALE_HEIGHT)      ) * segmentLength;

			opticalDepthR += hr;
			opticalDepthM += hm;

			// light optical depth

//				Ray lightRay = Ray( samplePosition, sunDirection );

			float ViewDZenith = samplePosition.normalize().dot(sunDirection);
			float altitude = height / (c.getF(Sky.ATMOSPHERE_RADIUS)-c.getF(Sky.PLANET_RADIUS));
			
			Vector2f l2 = new Vector2f(-1,-1);
			calcForSphere(samplePosition, sunDirection, c.getF(Sky.PLANET_RADIUS), l2);
			boolean skip = max(l2.x, l2.y) > 0;
//			skip = false;
			if(!skip) {
				if(useLut) {
					Vector3f tau3 = betaR .mult ( opticalDepthR);
					tau3.addLocal(betaM.mult( 1.1f * ( opticalDepthM)));
					Vector3f attenuation3 = exp( tau3.negate() );

					Vector4f lookupAtten = lut.lookup2(altitude, ViewDZenith);
					attenuation3.multLocal(lookupAtten.x, lookupAtten.y, lookupAtten.z);

					sumR.addLocal(attenuation3.mult(hr));
					sumM.addLocal(attenuation3.mult(hm));
				}
				else {
					Vector2f l = new Vector2f();
					calcForSphere(samplePosition, sunDirection, c.getF(Sky.ATMOSPHERE_RADIUS), l);
					//float lmin, lmax;
					//intersect( lightRay, SPHERE_ATMOSPHERE, lmin, lmax );

					float segmentLengthLight = Math.max(l.x, l.y) / numSamplesLight;
					float tCurrentLight = 0;
					float opticalDepthLightR = 0;
					float opticalDepthLightM = 0;

					int j = 0;

					for( ; j < numSamplesLight ; j++ )
					{
						Vector3f samplePositionLight = samplePosition.add(sunDirection.mult(tCurrentLight + 0.5f * segmentLengthLight ));

						float heightLight = samplePositionLight.length() - c.getF(Sky.PLANET_RADIUS);

	//					if(heightLight < 0) heightLight = 0;
						if(heightLight < 0) {
//							System.out.println("Breaking");
							break;
						}

						opticalDepthLightR += exp( -heightLight / c.getF(Sky.RAYLEIGH_SCALE_HEIGHT) ) * segmentLengthLight;
						opticalDepthLightM += exp( -heightLight / c.getF(Sky.MIE_SCALE_HEIGHT)      ) * segmentLengthLight;

						tCurrentLight += segmentLengthLight;
					}


	//				
	//				if(j == numSamples != skip) 
	//					throw new IllegalArgumentException("Skip");
	//								
					if( j == numSamplesLight ) {
						Vector3f tau = betaR .mult ( opticalDepthR + opticalDepthLightR );
							tau.addLocal(betaM.mult( 1.1f * ( opticalDepthM + opticalDepthLightM )));
						Vector3f attenuation = exp( tau.negate() );

		//				Vector3f tau2 = betaR .mult (  opticalDepthLightR );
		//					tau2.addLocal(betaM.mult( 1.1f * ( opticalDepthLightM )));
		//				Vector3f attenuation2 = exp( tau2.negate() );



		//				attenuation3.multLocal(attenuation2);

	//					if(useLut) {
	//						Vector3f tau3 = betaR .mult ( opticalDepthR);
	//						tau3.addLocal(betaM.mult( 1.1f * ( opticalDepthM)));
	//						Vector3f attenuation3 = exp( tau3.negate() );
	//
	//						Vector4f lookupAtten = lut.lookup2(altitude, ViewDZenith);
	//						attenuation3.multLocal(lookupAtten.x, lookupAtten.y, lookupAtten.z);
	//
	//						sumR.addLocal(attenuation3.mult(hr));
	//						sumM.addLocal(attenuation3.mult(hm));
	//					}
	//					else {
							sumR.addLocal(attenuation.mult(hr));
							sumM.addLocal(attenuation.mult(hm));
	//					}
					}
				}
			}
			
			tCurrent += segmentLength;
		}
		//float T = exp( -(13.0e-6f*opticalDepthR+21.0e-6f*opticalDepthM) );
//			System.out.println("segment " + segmentLength);
//			System.out.println("op " + opticalDepthR + ", " + opticalDepthM);
		Vector3f T = exp((betaR.mult(opticalDepthR).add(betaM.mult(opticalDepthM))).negate());

//			System.out.println("T " + T);
		//return(  ( sumR * phaseR * betaR + sumM * phaseM * betaM ) );
		Vector3f InScatter = sumM.mult(phaseR).mult(betaR).addLocal(sumM.mult(phaseM).mult(betaM));
		if(InScatter.x != InScatter.x) throw new IllegalArgumentException("NaN " + InScatter.x);
		if(InScatter.y != InScatter.y) throw new IllegalArgumentException("NaN " + InScatter.y);
		if(InScatter.z != InScatter.z) throw new IllegalArgumentException("NaN " + InScatter.z);
//			if(InScatter.x != InScatter.x) InScatter.x = 0;
//			if(InScatter.y != InScatter.y) InScatter.y = 0;
//			if(InScatter.z != InScatter.z) InScatter.z = 0;
//			
//			return T.add(InScatter);
//			return T;
		return InScatter;
	}
	public static boolean calcForSphere(Vector3f rayPosR, Vector3f rayDir, float rad, Vector2f res) {
			float b = 2.0f * dot(rayDir, rayPosR);
			float c = dot(rayPosR, rayPosR) - rad*rad;
			float disc = b * b - 4.0f * c;

			if (disc < 0.0) return false;

			float q = (-b + sign2(b)*sqrt(disc))*0.5f; 
			if(q == 0) {
				if(c == 0) {
					res.set(0, 0);
					return true;
				}
				return false;
			}
			c /= q;

			float t0 = min(q, c); 
			float t1 = max(q, c);   

			//if(t1 < 0.0) return false;

			res.set(t0, t1);   
			return true;
		}
	static Fragment frag = new Fragment() {
		
		SkyVars c = SkyVars.Earth();
		@Override
		public Vector4f draw(float x, float y) {
			return vec4(transmittance(c, y, x*1.25f-0.25f),1); 
		}
	};
}
