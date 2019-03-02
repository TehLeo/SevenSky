package test.theleo.sevensky;

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

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.InputListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;
import templates.geom.Vector3d;
import theleo.sevensky.core.SevenSky;
import theleo.sevensky.core.SkyVars;
import theleo.sevensky.core.Space;
import static theleo.sevensky.core.Space.*;
import theleo.sevensky.elements.Clouds;
import theleo.sevensky.elements.Sky;
import theleo.sevensky.generators.WeatherMapGenerator;
import theleo.sevensky.generators.WeatherMapGenerator.WeatherMapData;
import theleo.sevensky.generators.extra.Vector2DLut;
import theleo.sevensky.skyso.nuta.NutationFunction;
import theleo.sevensky.stars.hyg.Stars;
import theleo.sevensky.vsop87.Earth;


public class TestSky extends SimpleApplication {

    public static void main(String[] args) {
        TestSky app = new TestSky();
		app.settings = new AppSettings(true);
//		app.settings.setRenderer(AppSettings.LWJGL_OPENGL3);
        app.start();
    }

	AmbientLight ambient = new AmbientLight();
	SevenSky skySeven;
	Sky sky;
	Clouds clouds;
    @Override
    public void simpleInitApp() {
		getViewPort().setBackgroundColor(ColorRGBA.DarkGray);
		getCamera().setFrustumPerspective(60, getCamera().getWidth() / (float) getCamera().getHeight(),
				0.1f, 1000);
		getCamera().setLocation(new Vector3f(0,10,0));
		flyCam.setDragToRotate(true);
		
		{ 
			Geometry geom = new Geometry("Box", new Box(1, 1, 1));
			geom.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

			Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
			mat.setColor("Diffuse", ColorRGBA.Blue);
			mat.setColor("Ambient", ColorRGBA.Blue);
			mat.setBoolean("UseMaterialColors", true);
			geom.setMaterial(mat);

			rootNode.attachChild(geom);
			
		}
		{
			Geometry floor = new Geometry("Box", new Box(2000, 0.1f, 2000));
			floor.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
			
			Material mat2 = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
			mat2.setColor("Diffuse", ColorRGBA.Gray);
			mat2.setColor("Ambient", ColorRGBA.Gray);
			mat2.setBoolean("UseMaterialColors", true);
			floor.setMaterial(mat2);

			rootNode.attachChild(floor);
		}
		
		SkyVars vars = SkyVars.Earth();
		
		//by default a test weather map is created, check out the content of Clouds.createTestWeatherMap
		//to generate a custom weather map
		//So the next two lines are optional
		WeatherMapData weatherMapData = Clouds.createTestWeatherMap(getAssetManager(), getRenderManager());
		vars.put(Clouds.WeatherMap, weatherMapData);
		
		//or add simple texture as a weather map, eg. artist painted
		//vars.put(Clouds.SimpleWeatherMap, /*texture 2D */);
		
		//clouds animation is achieved by modifing the WeatherMap/SimpleWeatherMap
		
		//Remove high altitude clouds
		//vars.put(Clouds.AltoCoverage, 1f);
		//Add rainbow
		//vars.put(Clouds.RainDensity, 70000f);
		
		skySeven = new SevenSky(vars, getCamera(), getRenderManager(), getAssetManager());
		
		Stars stars = new Stars();
		skySeven.add(stars);
		
		
		sky = new Sky();
		skySeven.add(sky);

		clouds = new Clouds();
		skySeven.add(clouds);
		
		getViewPort().addProcessor(skySeven);

//		Vector3f sunLight = new Vector3f(1,1,0).normalizeLocal().negate();
		Vector3f sunLight = skySeven.c.getVec3(Sky.SunDir).negate();
		
	
		rootNode.addLight(ambient = new AmbientLight(ColorRGBA.Pink));
		
		DirectionalLight light = new DirectionalLight(sunLight);
		light.setColor(ColorRGBA.White);
		rootNode.addLight(light);
		
		FilterPostProcessor fpp = new FilterPostProcessor(getAssetManager());
		fpp.addFilter(new SevenSky.LightFilter(vars));
		getViewPort().addProcessor(fpp);
		
		Vector2f cloudOffset = vars.get(Clouds.CloudOffset);
		
		key(KeyInput.KEY_1, new AnalogListener() {
			@Override
			public void onAnalog(String name, float value, float tpf) {
				cloudOffset.x += tpf;
			}
		});
		
		key(KeyInput.KEY_2, new AnalogListener() {
			@Override
			public void onAnalog(String name, float value, float tpf) {
				cloudOffset.x -= tpf;
			}
		});
		
		//initialize functions that calculate position of Earth based on time, and nutation
		Space.EarthPosition = new Earth();
		Space.NutationFunction = new NutationFunction();
		
		//Julian data + julian time
		dateJD = JDN(18, 10, 2018) + JDTT(5, 30, 0);

		//LATITUDE IS NORTH-SOUTH, LONGITUDE is EAST-WEST
		//double obsLat = 52.3555, obsLon = -1.1743; //ENGLAND   (longitude is negative, since its WEST)
		double obsLat = 35.89857007, obsLon = 14.47291966; //MALTA
		
		{
			Vector3f SunDir = Space.getSunPos(dateJD, obsLon, obsLat).toVector3f().normalize();
			skySeven.setSunDir(SunDir);
		}
		
		
		key(KeyInput.KEY_3, new AnalogListener() {
			@Override
			public void onAnalog(String name, float value, float tpf) {
				dateJD += tpf*0.1f;
				Vector3f SunDir = Space.getSunPos(dateJD, obsLon, obsLat).toVector3f().normalize();
				skySeven.setSunDir(SunDir);
			}
		});	
		key(KeyInput.KEY_4, new AnalogListener() {
			@Override
			public void onAnalog(String name, float value, float tpf) {
				dateJD -= tpf*0.1f;
				Vector3f SunDir = Space.getSunPos(dateJD, obsLon, obsLat).toVector3f().normalize();
				skySeven.setSunDir(SunDir);
			}
		});	
		
		//weather map update
		key(KeyInput.KEY_5, new AnalogListener() {
			@Override
			public void onAnalog(String name, float value, float tpf) {
				//eg. simply rotate the clouds in the weather map for demonstration
				WeatherMapGenerator.rotate(weatherMapData.clouds, 0.1f * tpf);
				
				//alternatively you can animate the cloud geometry as you like
				
				//alternatively provide eg: vector field (image with direction vectors), check Vector2DLut(img) 
//				WeatherMapGenerator.update(weatherMapData.clouds, vectorLut, 0.0001f);
				
				WeatherMapGenerator.update(weatherMapData.cloudGeom, weatherMapData.clouds);
				WeatherMapGenerator.renderMap(weatherMapData.tr, weatherMapData.cloudGeom, getRenderManager());
			}
		});
				
    }
	double dateJD;
	boolean init = false;

    @Override
    public void simpleUpdate(float tpf) {
		if(sky != null && sky.skyLightGen != null && !init) {
			init = true;
			Picture pic = new Picture("");
	//		pic.setTexture(am, pathLengthLut, false);
			pic.setTexture(getAssetManager(), sky.gen.tex, false);
			pic.setCullHint(Spatial.CullHint.Never);
			pic.getMaterial().getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
			pic.setLocalTranslation(0, getCamera().getHeight() - 128, 0);

			pic.setWidth(128);
			pic.setHeight(128);
	//		rootNode.attachChild(pic);
	//		pic.setWidth(gen.tex.getImage().getWidth());
	//		pic.setHeight(gen.tex.getImage().getHeight());
	//		pic.setWidth(app.getCamera().getWidth());
	//		pic.setHeight(app.getCamera().getHeight());
			guiNode.attachChild(pic);
		}
		
		
		if(skySeven == null) return; 
		Vector3f amb = new Vector3f();
		for(Vector3f v : sky.lights)
			amb.addLocal(v);
//		amb.mult(0.05f/sky.lights.length);
		//amb.normalizeLocal();
				
		ambient.setColor(new ColorRGBA(amb.x, amb.y, amb.z, 1f));
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
	
	
	private static int actionc = 0;
    
    public void trigger(Trigger t, InputListener a) {
        trigger(t, a, "trigger_"+(++actionc));
    }
    public void trigger(Trigger t, InputListener a, String name) {
        inputManager.addMapping(name, t);
        inputManager.addListener(a, name);
    }
    public void key(int keyInput, InputListener a) {
        key(keyInput, a, "trigger_"+(++actionc));
    }
    public void key(int keyInput, InputListener a, String name) {
        trigger(new KeyTrigger(keyInput), a, name);
    }
}
