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
import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import templates.geom.Vector3d;
import theleo.sevensky.core.*;
import static theleo.sevensky.core.SevenSky.createFullScreenQuad;

/**
 *
 * @author Juraj Papp
 */
public class Planet implements SkyElement {
	double baseJD;
	double obsLon = 35;
	double obsLat = 15;
	
	
	Texture tex;
	SevenSky s;
	SphericalPosition pos;
	float planetRad;

	public Planet(Texture tex, SphericalPosition pos, float radKm) {
		this.tex = tex;
		this.pos = pos;
		planetRad = radKm;
	}

	Geometry quad;

	@Override
	public void init(SevenSky s) {
		this.s = s;
		quad = createPlanetQuad(s.am, s.cam, tex);
	}

	@Override
	public void prepareRenderView() {

	}

	@Override
	public void renderView() {
		double t = Space.JD2000Millenium(baseJD);
		Vector3d earthPos = Space.getEarthPos(baseJD, obsLon, obsLat);
		Vector3f dir = Space.getDirRad(baseJD, pos.getLon(t), pos.getLat(t), pos.getRad(t), earthPos, obsLon, obsLat);
//				renderPlanet(jupQuad, dir.normalizeLocal(), rm);
//				
////				renderPlanet(textQuad, dir.normalizeLocal(), rm, 1, 70);
//				
//				dir = getDirRad(baseJD, Mars.Mars_L(t), Mars.Mars_B(t), Mars.Mars_R(t), earthPos, obsLon, obsLat);		
////				renderPlanet(marsQuad, dir, rm);
//				renderPlanet(marsQuad, dir.normalize(), rm, 6779, dir.length()*Space.AU_KM*0.1f);
		renderPlanet(quad, dir.normalize(), s.rm, planetRad * 100, dir.length() * Space.AU_KM * 0.1f);
	}
	public Geometry createPlanetQuad(AssetManager am, Camera cam, String texture) {
		return createPlanetQuad(am, cam, am.loadTexture(texture));
	}

	public Geometry createPlanetQuad(AssetManager am, Camera cam, Texture texture) {
		Geometry moonquad = createFullScreenQuad(cam.getWidth() / (float) cam.getHeight(), cam.getProjectionMatrix().invert());
		moonquad.setCullHint(Spatial.CullHint.Never);

		Material mat = new Material(am, "MatDefs/Sky/SkySprite.j3md");
		//mat.setColor("Color", ColorRGBA.Gray);
		mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
		mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
		mat.getAdditionalRenderState().setDepthTest(false);

		Texture test = texture;
		test.setMinFilter(Texture.MinFilter.BilinearNearestMipMap);
		test.setMagFilter(Texture.MagFilter.Bilinear);
		mat.setTexture("ColorMap", test);

		moonquad.setMaterial(mat);

		return moonquad;
	}
	
	public void renderPlanet(Geometry quad, Vector3f d, RenderManager rm) {
		renderPlanet(quad, d, rm, 1737, s.MoonEarthDist);
	}

	public void renderPlanet(Geometry quad, Vector3f d, RenderManager rm, double Planet_rad_km, double distKm) {
		Vector3f dir = new Vector3f(d);
		Quaternion q = new Quaternion();
		q.lookAt(dir, Vector3f.UNIT_Y);

		dir.multLocal(100);
		dir.addLocal(s.cam.getLocation());
		Matrix4f worldMatrix = new Matrix4f();
		worldMatrix.setRotationQuaternion(q);
		worldMatrix.setTranslation(dir);

//			float MOON_RAD = 1737.0f; //km
		float mult = (float) (0.1f / distKm);
		float scale = (float) (Planet_rad_km * mult * 1000f);

		scale *= 2;
		worldMatrix.setScale(scale, scale, scale);

		rm.setWorldMatrix(worldMatrix);
		quad.getMaterial().render(quad, rm);

		worldMatrix.loadIdentity();
		rm.setWorldMatrix(worldMatrix);
	}
}
