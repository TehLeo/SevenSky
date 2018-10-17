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
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.Texture3D;
import com.jme3.texture.image.ColorSpace;
import com.jme3.util.BufferUtils;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;
import templates.billboards.TextureRenderer;
import templates.misc.QueryAvg;
import theleo.sevensky.core.*;
import static theleo.sevensky.core.SevenSky.PHASE;
import static theleo.sevensky.core.SevenSky.PHASE_495;
import static theleo.sevensky.core.SevenSky.PHASE_615;
import static theleo.sevensky.core.SevenSky.PHASE_780;
import static theleo.sevensky.core.SevenSky.createFullScreenQuad;
import theleo.sevensky.core.SkyVars.Key;
import theleo.sevensky.generators.SkyLutGenerator;
import theleo.sevensky.generators.WeatherMapGenerator;
import theleo.sevensky.generators.WeatherMapGenerator.WeatherMapData;
import static theleo.sevensky.generators.WeatherMapGenerator.createClouds;
import static theleo.sevensky.core.SevenSky.createFullScreenQuad;

/**
 *
 * @author Juraj Papp
 */
public class Clouds implements SkyElement {

	public static Key VOLUMETRIC_CLOUDS_FROM = new Key("VOLUMETRIC_CLOUDS_FROM", Float.class);
	public static Key VOLUMETRIC_CLOUDS_TO = new Key("VOLUMETRIC_CLOUDS_TO", Float.class);

	public static Key TRANSMITTANCE_LUT = new Key("TRANSMITTANCE_LUT", Texture2D.class);
	public static Key CLOUD_PATH_LUT = new Key("CLOUD_PATH_LUT", Texture2D.class);
	public static Key NOISE_DETAIL_MAP = new Key("NOISE_DETAIL_MAP", Texture2D.class);
	public static Key MIE_PHASE_MAP = new Key("MIE_PHASE_MAP", Texture2D.class);
	public static Key HIGH_ALTITUDE_CLOUD_MAP = new Key("HIGH_ALTITUDE_CLOUD_MAP", Texture2D.class);
	public static Key WEATHER_MAP = new Key("WEATHER_MAP", Texture2D.class);

	public static Key SunDir = new Key("SunDir", Vector3f.class);
	public static Key MoonPos = new Key("MoonPos", Vector3f.class);
	public static Key SunIrradiance = new Key("SunIrradiance", Vector3f.class);	//opt	
	public static Key AmbientColor = new Key("AmbientColor", Vector3f.class);

	public static Key CloudOffset = new Key("CloudOffset", Vector3f.class);

	public static Key Scattering = new Key("Scattering", Float.class);
	public static Key DetailScale = new Key("DetailScale", Float.class);
	public static Key SunScale = new Key("SunScale", Float.class);
	public static Key AmbientScale = new Key("AmbientScale", Float.class);
	public static Key LightScale = new Key("LightScale", Float.class);
	public static Key DensityEdge = new Key("DensityEdge", Float.class);
	public static Key Coverage = new Key("Coverage", Float.class);
	public static Key RainDensity = new Key("RainDensity", Float.class);
	public static Key AltoCoverage = new Key("AltoCoverage", Float.class);
	public static Key AltoLightScale = new Key("AltoLightScale", Float.class);
	
	public static Key WeatherMap = new Key("WeatherMap", WeatherMapData.class);
	public static Key SimpleWeatherMap = new Key("SimpleWeatherMap", Texture2D.class);
	
	public static Key CurlNoise = new Key("CurlNoise", Texture2D.class);
	public static Key DetailMap = new Key("DetailNoise", Texture3D.class);
	
	public static float rainDensity = 0f;
	
	QueryAvg query;
	
	SevenSky p;
	Geometry cloudpost;
	
	WeatherMapData weatherMapData;
	Texture2D simpleWeatherMap;


	@Override
	public void init(SevenSky p) {
		this.p = p;
		
		SkyVars vars = p.c;
		int lutSize = 512;
		if(!vars.has(TRANSMITTANCE_LUT)) {
			vars.put(TRANSMITTANCE_LUT, SkyLutGenerator.createTransmittanceLut3(vars, lutSize));
		}
		if(!vars.has(CLOUD_PATH_LUT)) {
			vars.put(CLOUD_PATH_LUT, SkyLutGenerator.createCloudPathLut3(vars, lutSize));
		}
		
		if(!vars.has(WeatherMap)) {
			if(!vars.has(SimpleWeatherMap)) {
				vars.put(WeatherMap, createTestWeatherMap(p.am, p.rm));
			}
		}
		
		if(!vars.has(CurlNoise)) {
			Texture curlNoise = p.am.loadTexture("Textures/noise/curlnoisetest.png");
			curlNoise.setMinFilter(Texture.MinFilter.BilinearNearestMipMap);
			curlNoise.setMagFilter(Texture.MagFilter.Bilinear);
			curlNoise.setWrap(Texture.WrapMode.Repeat);
			vars.put(CurlNoise, curlNoise);
		}
		
		if(!vars.has(DetailMap)) {
			Texture3D detailMap = load3dTexFloat("/Textures/clddetail.3d", Image.Format.Luminance32F, 32, 32, 32);
			detailMap.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
	//			detailMap.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
	//			detailMap.setMinFilter(Texture.MinFilter.NearestNearestMipMap);
	//			detailMap.setMinFilter(Texture.MinFilter.BilinearNearestMipMap);
			detailMap.setMagFilter(Texture.MagFilter.Bilinear);
			detailMap.setWrap(Texture.WrapMode.Repeat);
			vars.put(DetailMap, detailMap);
		}
		
		weatherMapData = vars.get(WeatherMap);
		simpleWeatherMap = vars.get(SimpleWeatherMap);
		vars.put(WEATHER_MAP, getWeatherMap());
		
		cloudpost = createCloudQuad(p.am, p.rm, p.cam);
		cloudpost.getMaterial().setVector3("SunIrradiance", new Vector3f(1, 1, 1));
	}
	
	public void enableDebug() {
		query = new QueryAvg("SkyPost");
	}

	@Override
	public void prepareRenderView() {
	}

	@Override
	public void renderView() {
		if(query != null) query.beginTimeElapsed();

		cloudpost.getMaterial().setVector3("SunDir", p.lastLightDir);

		cloudpost.getMaterial().render(cloudpost, p.rm);

		if(query != null) query.end();
	}
	
	public Texture2D getWeatherMap() {
		if (weatherMapData != null) {
			return weatherMapData.tr.tex;
		}
		return simpleWeatherMap;
	}
	public Texture3D load3dTexFloat(String tex, Image.Format fmt, int wx, int wy, int wz) {
		ArrayList<ByteBuffer> list = new ArrayList<ByteBuffer>();
		ByteBuffer b = BufferUtils.createByteBuffer(wx * wy * wz * 4);
		list.add(b);
		b.clear();

		try (DataInputStream dis = new DataInputStream(new BufferedInputStream(getClass().getResourceAsStream(tex)))) {
			while (b.hasRemaining()) {
				b.putFloat(dis.readFloat());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		b.flip();
		Image image = new Image(fmt, wx, wy, wz, list, ColorSpace.Linear);

		return new Texture3D(image);
	}
	public Geometry createCloudQuad(AssetManager am, RenderManager rm, Camera cam) {
		Geometry skypost = createFullScreenQuad(cam.getWidth() / (float) cam.getHeight(), cam.getProjectionMatrix().invert());
		skypost.setCullHint(Spatial.CullHint.Never);
		Material skypostMat = new Material(am, "MatDefs/Sky/CloudPost.j3md");
		skypostMat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
		skypostMat.getAdditionalRenderState().setDepthTest(false);
		skypostMat.getAdditionalRenderState().setDepthWrite(false);
		skypostMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.PremultAlpha);
//			skypostMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
//			skypostMat.setTexture("ColorMap", sky);

//			simpleWeatherMap = (Texture2D)am.loadTexture("Textures/testweathermap2.png");
////			simpleWeatherMap = (Texture2D)am.loadTexture("Textures/testweathermapA.png");
//			simpleWeatherMap.setMinFilter(Texture.MinFilter.BilinearNearestMipMap);
//			simpleWeatherMap.setMagFilter(Texture.MagFilter.Bilinear);
//			simpleWeatherMap.setWrap(Texture.WrapMode.Repeat);
//			skypostMat.setTexture("WeatherMap", simpleWeatherMap);


//		weatherMapData = createTestWeatherMap(am, rm);
		skypostMat.setTexture("WeatherMap", getWeatherMap());
		
		SkyVars vars = p.c;


		
		skypostMat.setTexture("LayerCloudMap", vars.get(CurlNoise));
		

//			Texture sunLut = am.loadTexture("Textures/sunlutdot25.png");
//			sunLut.setMinFilter(Texture.MinFilter.BilinearNearestMipMap);
//			sunLut.setMagFilter(Texture.MagFilter.Bilinear);
//			sunLut.setWrap(Texture.WrapMode.EdgeClamp);
//			skypostMat.setTexture("SunLut", sunLut);
		skypostMat.setTexture("TransmittanceLut", vars.get(Sky.TRANSMITTANCE_LUT));
		skypostMat.setTexture("CloudPathLut", vars.get(Clouds.CLOUD_PATH_LUT));

		skypostMat.setFloat("PLANET_RAD", vars.getF(Sky.PLANET_RADIUS));
		skypostMat.setFloat("PLANET_ATMOS_RAD", vars.getF(Sky.ATMOSPHERE_RADIUS));

//			skypostMat.setBoolean("UseCloudShape", false);
//			skypostMat.setBoolean("UseMultShape", true);
		skypostMat.setBoolean("UseCloudDetail", true);
		skypostMat.setBoolean("UseMultDetail", false);

		skypostMat.setFloat("Coverage", vars.getF(Coverage));

//			skypostMat.setFloat("SunStep", 10f);
//			skypostMat.setFloat("SunShadow", 20f);
		skypostMat.setFloat("ScatteringFactor", vars.getF(Scattering));
		skypostMat.setFloat("CloudScale", 1);
		skypostMat.setFloat("DetailScale", vars.getF(DetailScale));
		skypostMat.setFloat("SunScale", vars.getF(DetailScale));
		skypostMat.setFloat("AmbientScale", vars.getF(AmbientScale));
		skypostMat.setFloat("LightScale", vars.getF(LightScale));
		skypostMat.setFloat("DensityEdge", vars.getF(DensityEdge));
		skypostMat.setFloat("RainDensity", vars.getF(RainDensity));

		skypostMat.setFloat("AltoCoverage", vars.getF(AltoCoverage));
		skypostMat.setFloat("AltoLightScale", vars.getF(AltoLightScale));

		//phase
//			ByteBuffer bb = BufferUtils.createByteBuffer(
//				6* PHASE.length*4);
		ByteBuffer bb = BufferUtils.createByteBuffer(
				4 * PHASE_495.length * 4);
		bb.clear();
		FloatBuffer fb = bb.asFloatBuffer();
		for (int i = 0; i < PHASE_495.length; i++) {
			fb.put((float) PHASE_780[i]);
			fb.put((float) PHASE_615[i]);
			fb.put((float) PHASE_495[i]);

			fb.put(0);
		}
//			for(double d : PHASE) fb.put((float)d);
//			for(double d : PHASE2) fb.put((float)d);
//			for(double d : PHASE3) fb.put((float)d);
//			for(double d : PHASE4) fb.put((float)d);
//			for(double d : PHASE5) fb.put((float)d);
//			for(double d : PHASE6) fb.put((float)d);
		//		for(double d : PHASE7) fb.put((float)d);

		fb.flip();
//			Texture2D phase = new Texture2D(
//					new Image(Image.Format.RGBA32F, PHASE.length, 6,
//							bb, ColorSpace.Linear));
		Texture2D phase = new Texture2D(
				new Image(Image.Format.RGBA32F, PHASE.length, 1,
						bb, ColorSpace.Linear));

		skypostMat.setTexture("PhaseMap", phase);

//			Texture3D tex = load3dTexFloat("/Textures/cldnoise.3d", Image.Format.Luminance32F, 128, 128, 128);
//		
////			System.out.println("CAPACITY " + b.limit()+ "/" + b.capacity()) ;
////			tex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
////			tex.setMagFilter(Texture.MagFilter.Nearest);
////			tex.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
//			tex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
////			tex.setMinFilter(Texture.MinFilter.NearestNearestMipMap);
////			tex.setMinFilter(Texture.MinFilter.BilinearNearestMipMap);
//			tex.setMagFilter(Texture.MagFilter.Bilinear);
//			tex.setWrap(Texture.WrapMode.Repeat);
//			
//			skypostMat.setTexture("PerlinMap", tex);
		
		
		
		skypostMat.setTexture("DetailMap", vars.get(DetailMap));

//			//cloud
//			Perlin p = new Perlin(8);
//			double scale = 8.0 / 128.0;
//
//			ArrayList<ByteBuffer> list = new ArrayList<ByteBuffer>();
//
//			ByteBuffer b = BufferUtils.createByteBuffer(128 * 128 * 128 * 4);
//			list.add(b);
//			b.clear();
//			for (int i = 0; i < 128; i++) {
//				for (int z = 0; z < 128; z++) {
//					for (int x = 0; x < 128; x++) {
//						//byte r = ((byte)(FastMath.nextRandomFloat()*255f));
//						//b.put(r).put(r).put(r).put(r);
////						byte r = (byte)FastMath.clamp(64.0f-FastMath.sqrt((x-64)*(x-64) + (z-64)*(z-64) + (i-64)*(i-64)), 0.0f, 255.0f);
//						//if(r != 0) r = 127;
//						double d = p.OctavePerlin(x * scale, i * scale, z * scale, 1, 0.25) * 255;
//						byte r = (byte) d;
//
//						b.put(r).put(r).put(r).put(r);
//					}
//				}
//			}
//			b.flip();
//
//			Image image = new Image(Image.Format.BGRA8, 128, 128, 128, list, ColorSpace.Linear);
////			GLRenderer2
//
//			Texture3D tex = new Texture3D(image);
//			tex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
//			tex.setMagFilter(Texture.MagFilter.Nearest);
//			tex.setWrap(Texture.WrapMode.MirroredRepeat);
//
////			Texture test = am.loadTexture("Textures/clouds16s50d75p.png");
////			test.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
////			test.setMagFilter(Texture.MagFilter.Nearest);
////			test.setWrap(Texture.WrapMode.Repeat);
//			skypostMat.setTexture("PerlinMap", tex);
		skypost.setMaterial(skypostMat);

		return skypost;
	}
	
	public static WeatherMapGenerator.WeatherMapData createTestWeatherMap(AssetManager am, RenderManager rm) {	
		WeatherMapGenerator.random = new Random(0);
		
		WeatherMapGenerator.Cloud[] clouds = createClouds(5, 12, 0.1f, 0.3f, 0.5f, 40);
//		WeatherMapGenerator.Cloud[] clouds = createClouds(5, 12, 0.1f, 0.3f, 0.5f, 20);
//		WeatherMapGenerator.Cloud[] clouds = createClouds(5, 12, 0.2f, 0.4f, 0.75f, 10);
		WeatherMapGenerator.randomHeight(clouds, 0.25f, 1f);
		
		Texture2D noise = (Texture2D)am.loadTexture("Textures/clouds16s50d75p.png");
		Geometry cloudGeom = WeatherMapGenerator.buildGeom(am, noise, clouds);
		
		TextureRenderer tr = WeatherMapGenerator.createRenderer(512);
		WeatherMapGenerator.renderMap(tr, cloudGeom, rm);
		return new WeatherMapGenerator.WeatherMapData(clouds, cloudGeom, tr);
	}
}
