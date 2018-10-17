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
package theleo.sevensky.elements;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.opengl.GLImageFormat;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.util.BufferUtils;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.lwjgl.opengl.GL11;
import templates.misc.QueryAvg;
import templates.util.MiscUtils;
import theleo.sevensky.core.*;
import static theleo.sevensky.core.SevenSky.createFullScreenQuad;
import theleo.sevensky.core.SkyVars.Key;
import theleo.sevensky.generators.SkyLutGenerator;
import theleo.sevensky.generators.SkyLutGpuGenerator;
import theleo.sevensky.generators.SkyLutType;

/**
 *
 * @author Juraj Papp
 */
public class Sky implements SkyElement {
	public static Key RAYLEIGH_SCATTERING = new Key("RAYLEIGH_SCATTERING", Vector3f.class);
	public static Key RAYLEIGH_ABSORBTION = new Key("RAYLEIGH_ABSORBTION", Vector3f.class);
	public static Key RAYLEIGH_SCALE_HEIGHT = new Key("RAYLEIGH_SCALE_HEIGHT", Float.class);
	public static Key MIE_SCATTERING = new Key("MIE_SCATTERING", Vector3f.class);
	public static Key MIE_ABSORBTION = new Key("MIE_ABSORBTION", Vector3f.class);
	public static Key MIE_SCALE_HEIGHT = new Key("MIE_SCALE_HEIGHT", Float.class);
	public static Key SUN_MIE_G = new Key("SUN_MIE_G", Float.class);
	public static Key MOON_MIE_G = new Key("MOON_MIE_G", Float.class);
	public static Key SUN_INTENSITY = new Key("SUN_INTENSITY", Float.class);
	public static Key MOON_INTENSITY = new Key("MOON_INTENSITY", Float.class);

	public static Key TRANSMITTANCE_LUT = new Key("TRANSMITTANCE_LUT", Texture2D.class);
	public static Key PATH_LENGTH_LUT = new Key("PATH_LENGTH_LUT", Texture2D.class);

	public static Key SunDir = new Key("SunDir", Vector3f.class);
	public static Key SunLight = new Key("SunDir", Vector3f.class);
	public static Key MoonPos = new Key("MoonPos", Vector3f.class);
	
	QueryAvg query;

	SevenSky p;
	Geometry skypost;
	
	public SkyLutGpuGenerator gen;
	public SkyLutGpuGenerator skyLightGen;
	
	ByteBuffer skyLightBuf;
	public Vector3f[] lights = new Vector3f[6]; //x+, x-, z+, z-, y+, y-

	@Override
	public void init(SevenSky p) {
		this.p = p;
		
		int skySize = 128;
		int lutSize = 512;
		
		for (int i = 0; i < lights.length; i++) {
			lights[i] = new Vector3f();
		}
		
		SkyVars vars = p.c;
		if(!vars.has(TRANSMITTANCE_LUT)) {
			vars.put(TRANSMITTANCE_LUT, SkyLutGenerator.createTransmittanceLut3(vars, lutSize));
		}
		if(!vars.has(PATH_LENGTH_LUT)) {
			vars.put(PATH_LENGTH_LUT, SkyLutGenerator.createPathLengthLut3(vars, lutSize));
		}
		
		gen = new SkyLutGpuGenerator(p.c, vars.get(TRANSMITTANCE_LUT), vars.get(PATH_LENGTH_LUT), p.am, p.rm,
				p.cam, SkyLutType.VIEW, skySize, skySize, Image.Format.RGBA16F);
		gen.enableDebug();
		skypost = createSkyQuad(p.am, p.cam);
		
		
		
		int skyLightSize = 16;

		skyLightGen = new SkyLutGpuGenerator(vars, vars.get(TRANSMITTANCE_LUT), vars.get(PATH_LENGTH_LUT), p.am, p.rm,
				p.cam, SkyLutType.LUT_2D, skyLightSize, skyLightSize, Image.Format.RGBA16F);
		skyLightGen.enableDebug();
		
		skyLightBuf = BufferUtils.createByteBuffer(skyLightSize * skyLightSize * 4 * 2);
		skyLightBuf.clear();
		skyLightBuf.limit(skyLightBuf.capacity());
	}
	
	public void enableDebug() {
		query = new QueryAvg("SkyPost");
	}

	@Override
	public void prepareRenderView() {
		if (gen.type == SkyLutType.VIEW) {
			Vector3f moonDir = null;
			Vector3f moonPos = p.c.getVec3(MoonPos);
			if (moonPos != null) {
				moonDir = moonPos.subtract(p.cam.getLocation()).normalizeLocal();
			}
			gen.render(p.rm, p.sunDir, moonDir, Math.max(p.cam.getLocation().y, 0));
		}
		
		skyLightGen.render(p.rm, p.sunDir, p.moonDir, Math.max(p.cam.getLocation().y, 0), false);
//		long time = System.currentTimeMillis();
//		skyLightGen.render(rm, sunDir, moonDir, Math.max(cam.getLocation().y, 0), false);

//			Camera cam = rm.getCurrentCamera();
//			rm.setCamera(tr.cam, false);
//
//			tr.enableRender(rm);
//			mat.render(fsQuad, rm);
//
////			if(stars != null) stars.getMaterial().render(stars, rm);
//			//Caps.FrameBufferBlit
//			GLRenderer gl = (GLRenderer) rm.getRenderer();
//			GLRenderer2 gl = (GLRenderer2) rm.getRenderer();
////			gl.copyFrameBuffer(tr.fb, skyLightFb, false, true);
//			gl.copyFrameBuffer(tr.fb, skyLightFb, false);
//
//			gl.glfbo.glBindFramebufferEXT(GLFbo.GL_READ_FRAMEBUFFER_EXT, skyLightFb.getId());
//			//gl.glfbo.glBindFramebufferEXT(GLFbo.GL_DRAW_FRAMEBUFFER_EXT, skyLightFb.getId());
//
////			System.out.println("sky light " + skyLightFb.getWidth()*skyLightFb.getHeight());
//			//readFrameBufferWithGLFormat(fb, byteBuf, glFormat.format, glFormat.dataType);
//			skyLightBuf.clear();
//			//gl.readFrameBufferWithFormat(skyLightFb, skyLightBuf, Image.Format.BGRA8);
//
//			gl.setFrameBuffer(skyLightGen.tr.);
		int w = skyLightGen.tr.fb.getWidth();
		int h = skyLightGen.tr.fb.getHeight();
//

//			GLImageFormat glFormat = gl.texUtil.getImageFormatWithError(skyLightGen.tex.getImage().getFormat(), false);
//			gl.gl.glReadPixels(0, 0, w, h, glFormat.format, glFormat.dataType, skyLightBuf);
		GLImageFormat glFormat = MiscUtils.getFormat(p.rm.getRenderer(), skyLightGen.tex.getImage().getFormat(), false);
		int format = glFormat.format;
		int dataType = glFormat.dataType;
		//Format 6408, 5131

		GL11.glReadPixels(0, 0, w, h, glFormat.format, glFormat.dataType, skyLightBuf);

		skyLightGen.tr.endRender(p.rm);

		for (int i = 0; i < lights.length; i++) {
			lights[i].zero();
		}

		int size = skyLightBuf.capacity();
		int line = 8 * w;
		float wInv = 2f / w;
		float hInv = 2f / h;
//			System.err.println("SIZE " + size + ", " + skyLightBuf.limit());
		for (int i = 0; i < size; i += 8) {
//				float r = (skyLightBuf.get(i)&0xff)*0.003921569f;
//				float g = (skyLightBuf.get(i+1)&0xff)*0.003921569f;
//				float b = (skyLightBuf.get(i+2)&0xff)*0.003921569f;
//				float a = (skyLightBuf.get(i+3)&0xff)*0.003921569f;

			float r = FastMath.convertHalfToFloat(skyLightBuf.getShort(i));
			float g = FastMath.convertHalfToFloat(skyLightBuf.getShort(i + 2));
			float b = FastMath.convertHalfToFloat(skyLightBuf.getShort(i + 4));
			float a = FastMath.convertHalfToFloat(skyLightBuf.getShort(i + 6));

//				float r = skyLightBuf.getFloat(i);
//				float g = skyLightBuf.getFloat(i + 4);
//				float b = skyLightBuf.getFloat(i + 8);
//				float a = skyLightBuf.getFloat(i + 12);
//				float r = (skyLightBuf.get(i)+0.5f)*0.007843137f;
//				float g = (skyLightBuf.get(i+1)+0.5f)*0.007843137f;
//				float b = (skyLightBuf.get(i+2)+0.5f)*0.007843137f;
//				float a = (skyLightBuf.get(i+3)+0.5f)*0.007843137f;
//				byte r = skyLightBuf.get(i);
//				byte g = skyLightBuf.get(i+1);
//				byte b = skyLightBuf.get(i+2);
//				byte a = skyLightBuf.get(i+3);
			//System.err.println(i+ ": " + r + ", " + g + ", " + b + ", " + a);
			float pX = (i % line) / 8;
			float pY = (i / line);

			float xx = (pX + 0.5f) * wInv - 1f;
			float yy = (pY + 0.5f) * hInv - 1f;

			float az = xx * FastMath.PI;
			float ele = FastMath.sin(yy * 0.5f * FastMath.PI);
			float s = FastMath.sqrt(1.0f - ele * ele);
			Vector3f dir = new Vector3f(s * FastMath.cos(az), ele, s * FastMath.sin(az));

//				System.out.println("i " + i + ", " + xx + ", " + yy + ", " + dir);
//				float cx = (i % line) * 0.25f * 0.25f + 0.5f;
//				float cy = 0.5f + i / line;
//				cx = cx / w - 0.5f;
//				cx += cx;
//				cy = cy / h - 0.5f;
//				cy += cy;
//
//				float xzLen = FastMath.sqrt(cx * cx + cy * cy);
//				float rad = 1.0f - xzLen;
//				float mult = FastMath.sqrt(1.0f - rad * rad) / (xzLen);
//				Vector3f vec = new Vector3f(cx * mult, rad, cy * mult);
//				System.err.println("cx " + cx + ", " + cy + ": " + vec + ", " + vec.length() + ", " + r + ", " + g + ", " + b);
			//x, -x, z, -z, y, -y
			float p = Math.abs(dir.x);
			lights[dir.x >= 0 ? 0 : 1].addLocal(r * p, g * p, b * p);
			p = Math.abs(dir.z);
			lights[dir.z >= 0 ? 2 : 3].addLocal(r * p, g * p, b * p);
			p = Math.abs(dir.y);
			lights[dir.y >= 0 ? 4 : 5].addLocal(r * p, g * p, b * p);

//				sphericalToCartesianY(PI, PI, 1.0);
		}
//			for (int i = 0; i < lights.length; i++) {
//				System.out.println("l " + i + ", " + lights[i]);
//			}

//			if(weatherMapData != null) {
////				WeatherMapGenerator.rotate(weatherMapData.clouds, 0.0001f);
//				WeatherMapGenerator.update(weatherMapData.clouds, vectorLut, 0.0001f);
//				
//				WeatherMapGenerator.update(weatherMapData.cloudGeom,
//						weatherMapData.clouds);
//				WeatherMapGenerator.renderMap(weatherMapData.tr,
//						weatherMapData.cloudGeom, rm);
//			}
//		time = System.currentTimeMillis() - time;
//			System.err.println("Time ms: " + time );
//			System.err.println("Light " + Arrays.toString(lights));
//
//			if (cam != null) {
//				rm.setCamera(cam, cam.isParallelProjection());
//			}
//			tr.endRender(rm);
	}

	@Override
	public void renderView() {
		Material mat = skypost.getMaterial();

		Vector3f sunDir = p.c.getVec3(SunDir);
		Vector3f moonDir = null;
		Vector3f moonPos = p.c.getVec3(MoonPos);
		if (moonPos != null) {
			moonDir = moonPos.subtract(p.cam.getLocation()).normalizeLocal();
		}

		if(query != null) query.beginTimeElapsed();
		if (sunDir == null) {
			mat.clearParam("SunDir");
		} else {
			mat.setVector3("SunDir", p.c.getVec3(SunDir));
		}
		if (moonDir == null) {
			mat.clearParam("MoonDir");
		} else {
			mat.setVector3("MoonDir", moonDir);
		}
		SkyVars vars = p.c;
		float alt = Math.max(p.cam.getLocation().y, 0)/
						(vars.getF(Sky.ATMOSPHERE_RADIUS)-vars.getF(Sky.PLANET_RADIUS));
		skypost.getMaterial().setVector3("SunCol", SkyLutGenerator.transmittance(vars, alt, sunDir.y));
		skypost.getMaterial().setVector3("MoonCol", SkyLutGenerator.transmittance(vars, alt, moonDir.y).mult(0.2f));
		
		skypost.getMaterial().render(skypost, p.rm);
		if(query != null) query.end();
	}
	
	
	public Geometry createSkyQuad(AssetManager am, Camera cam) {
		Geometry skypost = createFullScreenQuad(cam.getWidth() / (float) cam.getHeight(), cam.getProjectionMatrix().invert());
//			Geometry skypost ;
//			if(gen.type == SkyLutType.LUT_2D) skypost = createFullScreenQuad(cam.getWidth() / (float) cam.getHeight(), cam.getProjectionMatrix().invert());
//			else skypost = createFullScreenQuad(gen.tex.getImage().getWidth()/(float)gen.tex.getImage().getHeight(), cam.getProjectionMatrix().invert());
		skypost.setCullHint(Spatial.CullHint.Never);
		Material skypostMat = new Material(am, "MatDefs/Sky/SkyPost.j3md");
		skypostMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.PremultAlpha);
//			skypostMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Off);
		skypostMat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
		skypostMat.getAdditionalRenderState().setDepthTest(false);
		skypostMat.getAdditionalRenderState().setDepthWrite(false);
//			skypostMat.setTexture("ColorMap", sky);
		skypostMat.setTexture("ColorMap", gen.tex);
		if (gen.type == SkyLutType.VIEW) {
			skypostMat.setBoolean("UseViewLut", true);
		}

//			Texture test = am.loadTexture("Textures/test2.png");
//			test.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
//			test.setMagFilter(Texture.MagFilter.Nearest);
//			skypostMat.setTexture("TestMap", test);
		skypost.setMaterial(skypostMat);

		return skypost;
	}
	public Geometry createMoonQuad(AssetManager am, Camera cam) {
		Geometry moonquad = createFullScreenQuad(cam.getWidth() / (float) cam.getHeight(), cam.getProjectionMatrix().invert());
		moonquad.setCullHint(Spatial.CullHint.Never);

		Material mat = new Material(am, "MatDefs/Sky/SkySpriteMoon.j3md");
		//mat.setColor("Color", ColorRGBA.Gray);
		mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
		mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
		mat.getAdditionalRenderState().setDepthTest(false);

		Texture test = am.loadTexture("Textures/FullMoon256.png");
		test.setMinFilter(Texture.MinFilter.BilinearNearestMipMap);
		test.setMagFilter(Texture.MagFilter.Bilinear);
		mat.setTexture("ColorMap", test);

		moonquad.setMaterial(mat);

		return moonquad;
	}
}
