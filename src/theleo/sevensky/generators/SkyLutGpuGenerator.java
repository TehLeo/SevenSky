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

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import templates.billboards.TextureRenderer;
import templates.misc.QueryAvg;
import theleo.sevensky.core.*;
import theleo.sevensky.elements.Sky;

/**
 *
 * @author Juraj Papp
 */
public class SkyLutGpuGenerator {
	public SkyLutType type;
	
	SkyVars c;
	Texture2D transmittanceLut;
	int width, height;
	public Texture2D tex;
	public TextureRenderer tr;
	public Geometry quad;
	Camera viewCam;
	
	public QueryAvg query;
	
	public SkyLutGpuGenerator(SkyVars c, Texture2D transmittanceLut,
			Texture2D pathLengthLut, AssetManager am, RenderManager rm,
			Camera viewCam,
			SkyLutType type,
			int texW, int texH, Image.Format fmt) {
		
		this.c = c; width = texW; height = texH; this.type = type;
		this.viewCam = viewCam;
				
		tex = new Texture2D(texW, texH, fmt);
		tex.setMinFilter(Texture.MinFilter.BilinearNearestMipMap);
		tex.setMagFilter(Texture.MagFilter.Bilinear);
//		tex.setWrap(Texture.WrapMode.EdgeClamp);
		tex.setWrap(Texture.WrapAxis.S, Texture.WrapMode.Repeat);
		tex.setWrap(Texture.WrapAxis.T, Texture.WrapMode.EdgeClamp);
		
		tr = new TextureRenderer(tex, false);
		
		tr.cam.copyFrom(viewCam);
		tr.cam.resize(tex.getImage().getWidth(), tex.getImage().getHeight(), false);
		//rm.setCamera(tr.cam, tr.cam.isParallelProjection());
		

		
		quad = SevenSky.createFullScreenQuad(viewCam.getWidth() / (float) viewCam.getHeight(), viewCam.getProjectionMatrix().invert());
//		quad = TestTemplate.createFullScreenQuad(tex.getImage().getWidth() / (float) tex.getImage().getHeight(), viewCam.getProjectionMatrix().invert());
		Material mat = new Material(am, "MatDefs/Sky/SkyInscatterLut.j3md");
		if(type == SkyLutType.VIEW) mat.selectTechnique("View", rm);
		mat.setFloat("PLANET_RAD", c.getF(Sky.PLANET_RADIUS));
		mat.setFloat("PLANET_ATMOS_RAD", c.getF(Sky.ATMOSPHERE_RADIUS));
		mat.setFloat("RAYLEIGH_SCALE_HEIGHT", c.getF(Sky.RAYLEIGH_SCALE_HEIGHT));
		mat.setFloat("MIE_SCALE_HEIGHT", c.getF(Sky.MIE_SCALE_HEIGHT));
		mat.setFloat("SUN_MIE_G", c.getF(Sky.SUN_MIE_G));
		mat.setFloat("SUN_INTENSITY", c.getF(Sky.SUN_INTENSITY));
		mat.setFloat("MOON_MIE_G", c.getF(Sky.MOON_MIE_G));
		mat.setFloat("MOON_INTENSITY", c.getF(Sky.MOON_INTENSITY));
		mat.setVector3("SCATTER_RAY", c.get(Sky.RAYLEIGH_SCATTERING));
		mat.setVector3("SCATTER_MIE", c.get(Sky.MIE_SCATTERING));
		mat.setTexture("TransmittanceLut", transmittanceLut);
		mat.setTexture("PathLengthLut", pathLengthLut);
		mat.getAdditionalRenderState().setDepthTest(false);
		mat.getAdditionalRenderState().setDepthWrite(false);
		quad.setMaterial(mat);
		
	}
	public void enableDebug() {
		query = new QueryAvg("SkyLutGpuGenerator");
	}
	boolean noSunDir = true;
	boolean noMoonDir = true;
	Vector3f lastSunDir = new Vector3f();
	Vector3f lastMoonDir = new Vector3f();
	Vector3f lastCamDir = new Vector3f();
	float lastAltitude = -1f;
	
	public void render(RenderManager rm, Vector3f sunDir, Vector3f moonDir, float altitude) {
		render(rm, sunDir, moonDir, altitude, true);
	}
	
	public void render(RenderManager rm, Vector3f sunDir, Vector3f moonDir, float altitude, boolean finish) {
//		sunDir = sunDir.normalize();
//		moonDir = moonDir.normalize();
		altitude = Math.max(altitude, 0);
		
		Vector3f camDir = viewCam.getDirection();
		
		if(((sunDir == null && noSunDir) || (sunDir != null && lastSunDir.x == sunDir.x && lastSunDir.y == sunDir.y && lastSunDir.z == sunDir.z)) &&
		   ((moonDir == null && noMoonDir) || (moonDir != null && lastMoonDir.x == moonDir.x && lastMoonDir.y == moonDir.y && lastMoonDir.z == moonDir.z)) &&
				lastCamDir.x == camDir.x && lastCamDir.y == camDir.y && lastCamDir.z == camDir.z &&
			    lastAltitude == altitude) return;

		noSunDir = sunDir == null;
		noMoonDir = moonDir == null;		
		
		lastCamDir.set(camDir);
		lastAltitude = altitude;
		
//		if(type == SkyLutType.VIEW) quad.getMaterial().setMatrix4("ViewMatrix", viewCam.getViewMatrix());
//		if(type == SkyLutType.VIEW) {
			
//			quad.getMaterial().setMatrix4("ViewMatrix", viewCam.getViewMatrix());
//		}
		
		Camera cam = rm.getCurrentCamera();
//		if(type == SkyLutType.VIEW) {
////			tr.cam.copyFrom(viewCam);
////			tr.cam.resize(tex.getImage().getWidth(), tex.getImage().getHeight(), false);
////			rm.setCamera(tr.cam, tr.cam.isParallelProjection());
//			tr.cam.setLocation(viewCam.getLocation());
//			tr.cam.setRotation(viewCam.getRotation());
//			
////			quad.getMaterial().setMatrix4("ViewMatrix", viewCam.getViewMatrix());
//			
//
////			quad.getMaterial().setMatrix4("ViewMatrix", tr.cam.getViewMatrix());
//			rm.setCamera(tr.cam, tr.cam.isParallelProjection());
//		}
		tr.cam.setLocation(viewCam.getLocation());
		tr.cam.setRotation(viewCam.getRotation());

		rm.setCamera(tr.cam, tr.cam.isParallelProjection());
		//rm.setCamera(viewCam, viewCam.isParallelProjection());
		tr.enableRender(rm);
		
		Material mat = quad.getMaterial();
		mat.setFloat("Altitude", altitude);
		
		if(sunDir != null) {
			lastSunDir.set(sunDir);
			mat.setVector3("SunDir", sunDir.normalize());
		}
		else mat.clearParam("SunDir");
		if(moonDir != null) {
			lastMoonDir.set(moonDir);
			mat.setVector3("MoonPos", moonDir.normalize().multLocal(384400));
		}
		else mat.clearParam("MoonPos");
		
		if(query != null) { 
			query.beginTimeElapsed();
//			System.out.println("RENDER " + sunDir + ", " + altitude);
//			System.out.println("QueryRes: " + query.getAvg());
		}
	
		mat.render(quad, rm);
		
		if(query != null) query.end();
		if(cam != null) rm.setCamera(cam, cam.isParallelProjection());

		if(finish) {
			tr.endRender(rm);
		}
	}
	
}
