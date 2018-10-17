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
package theleo.sevensky.stars.hyg;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import templates.geom.Vector3d;
import theleo.sevensky.core.*;
import static theleo.sevensky.core.SevenSky.createFullScreenQuad;
import static theleo.sevensky.core.Space.*;
import theleo.sevensky.stars.hyg.HygCatalogReader;
import theleo.sevensky.stars.hyg.HygRecord;

/**
 *
 * @author Juraj Papp
 */
public class Stars implements SkyElement {
//		public static Key observerLongitude = new Key("observerLongitude", Float.class);
//		public static Key observerLatitude = new Key("observerLatitude", Float.class);

	public static class Star {

		public String name;
		public double ra, dec;
		public double raMot, decMot;
		public double appMag;
		public double earthPower; //[W/m2]

		public double colorIndex;
		public double temperature;
		public double distance;

		/**
		 * Percentage of irradiance for each spectral band.
		 */
		public Vector3f RGB = new Vector3f(1, 1, 1);

		@Override
		public String toString() {
			return name + " " + appMag + "(" + ra + " ~ " + raMot + ", " + dec + " ~ " + decMot + ")";
		}

	}
	public static double[] BANDS = new double[]{
		780e-9, 615e-9,
		615e-9, 495e-9,
		495e-9, 380e-9
	};

	public static void calcColor(Star s) {
		double tempK = s.temperature;
		double[] vals = new double[6];
		vals[0] = blackbodyIrradianceIntegral(BANDS[0], tempK, 128);
		vals[1] = blackbodyIrradianceIntegral(BANDS[1], tempK, 128);
		if (BANDS[1] == BANDS[2]) {
			vals[2] = vals[1];
		} else {
			vals[2] = blackbodyIrradianceIntegral(BANDS[2], tempK, 128);
		}
		vals[3] = blackbodyIrradianceIntegral(BANDS[3], tempK, 128);
		if (BANDS[3] == BANDS[4]) {
			vals[4] = vals[3];
		} else {
			vals[4] = blackbodyIrradianceIntegral(BANDS[4], tempK, 128);
		}
		vals[5] = blackbodyIrradianceIntegral(BANDS[5], tempK, 128);

		double R = vals[0] - vals[1];
		double G = vals[2] - vals[3];
		double B = vals[4] - vals[5];

		//double scale = 1.0/Math.max(Math.max(R,G),B);
		double scale = 1.0 / blackbodyIrradiance(tempK);

		s.RGB.set((float) (R * scale), (float) (G * scale), (float) (B * scale));
	}

	public static Geometry initStars(ArrayList<Star> starList, AssetManager am, double baseJD, double obsLon, double obsLat) {
//		try {
//			double minMag=Float.MAX_VALUE, maxMag=Float.MIN_VALUE;
//			YaleCatalogReader y = new YaleCatalogReader();
//			y.load();
//			for(YaleRecord rec : y.records) {
//				Star star = new Star();
//				star.name = rec.name;
//				star.ra = HMS(rec.RAh, rec.RAm, rec.RAs);
//				star.dec = DMS(rec.DEd, rec.DEm, rec.DEs);
//				if(rec.DE_SIGN == '-') star.dec = -star.dec;
//				star.raMot = DMS(0,0,rec.annualProperMotionRA);
//				star.decMot = DMS(0,0,rec.annualProperMotionDE);
//				
//				star.appMag = rec.VisualMagnitude;
//				starList.add(star);
//				
//				minMag = Math.min(star.appMag, minMag);
//				maxMag = Math.max(star.appMag, maxMag);
//			
//				if(rec.harvardRevisedNumber == 2491) {
//					System.out.println("");
//				}
//			}
//			System.out.println("Min " + minMag + ", Max " + maxMag);
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//		}

		try {
			double powerSum = 0.0;
			double minMag = Float.MAX_VALUE, maxMag = Float.MIN_VALUE;
			HygCatalogReader y = new HygCatalogReader();
			y.load9k();
//			y.loadAll();
			double ONE_WATT_BRIGHTNESS_INV = 1.0 / 36492675.003699; //

			for (HygRecord rec : y.records) {
				Star star = new Star();
				star.name = rec.proper;
				star.ra = rec.rarad;
				star.dec = rec.decrad;
				star.raMot = rec.pmrarad;
				star.decMot = rec.pmdecrad;
				star.colorIndex = rec.ci;

//				if(!"Lib".equals(rec.con))continue;
//				if(!"Leo".equals(rec.con))continue;
//				if(!"Cap".equals(rec.con))continue;
				if (rec.ci != rec.ci) {
					System.out.println("Skipping star");
					continue;
				}
				star.temperature = Space.temperature(rec.ci);

				calcColor(star);

//				star.appMag = Math.min(rec.mag, 4.5);
				star.appMag = rec.mag;
				star.earthPower = Math.pow(10.0, -0.4 * rec.mag) * ONE_WATT_BRIGHTNESS_INV;
				star.RGB.multLocal((float) star.earthPower);
				if (star.RGB.x != star.RGB.x) {
					System.err.println("Error NaN color");
					System.exit(0);
				}
				starList.add(star);

				powerSum += star.earthPower;
				minMag = Math.min(star.appMag, minMag);
				maxMag = Math.max(star.appMag, maxMag);

			}
			//2.6451396017964844E-6
			System.out.println("Integrated starlight sphere: " + powerSum);
			System.out.println("Min " + minMag + ", Max " + maxMag);
		} catch (Exception e) {
			e.printStackTrace();
		}

//		try {
//			Scanner in = new Scanner(TestTemplate.class.getResourceAsStream("stars.txt"));
//			while(in.hasNextLine()) {
//				String line = in.nextLine().trim();
//				if(line.isEmpty() || line.charAt(0) == '#') continue;
//				String[] items = line.split("[|]");
//				String ra = items[2].substring(items[2].indexOf(',')+1);
//				String[] raMotDec = items[3].split(",");
//				String[] decMot = items[4].split(",");
//				
//				Star star = new Star();
//				star.name = items[0].substring(0,items[0].indexOf(','));
//				star.ra = HMS(ra);
//				star.dec = DMS(raMotDec[1]);
//				
//				star.raMot = HMS(0,0,Double.parseDouble(raMotDec[0])*0.001);
//				star.decMot = DMS(0,0,Double.parseDouble(decMot[0])*0.001);
//				
//				star.appMag = Double.parseDouble(decMot[1]);
//				starList.add(star);
//			}
//		}
//		catch(Exception e) {e.printStackTrace();}
		Mesh mesh = new Mesh();
		mesh.setMode(Mesh.Mode.Points);

		int i = 0, si = 0;
		float[] starPos = new float[starList.size() * 3];
		float[] size = new float[starList.size()];
		for (Star s : starList) {
			double[] azAlt = observerRaDec(GMST(baseJD) + equationOfEquinoxes(baseJD), s.ra, s.dec, obsLon, obsLat);
			Vector3d dir = sphericalToCartesianY(azAlt[0], azAlt[1], 1.0).normalizeLocal();
			starPos[i++] = (float) dir.x;
			starPos[i++] = (float) dir.y;
			starPos[i++] = (float) dir.z;

			float f = (float) s.appMag;
			f = FastMath.interpolateLinear(FastMath.saturate((10f - (f + 3.5f)) * 0.1f), 0.1f, 0.3f);

			size[si++] = f;
		}

		mesh.setBuffer(VertexBuffer.Type.Position, 3, starPos);
		mesh.setBuffer(VertexBuffer.Type.Size, 1, size);
		mesh.updateCounts();

		Geometry _stars = new Geometry("stars", mesh);
		_stars.setCullHint(Spatial.CullHint.Never);

		Material mat = new Material(am, "MatDefs/Sky/Stars.j3md");
		mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
		_stars.setMaterial(mat);

		return _stars;
	}

	SevenSky s;
	Geometry stars;
	Geometry starquad;
	ArrayList<Star> starList;

	@Override
	public void init(SevenSky s) {
		this.s = s;
		starList = new ArrayList<Star>();
		starquad = createStarQuad(s.am, s.cam);
		stars = initStars(starList, s.am, Space.JDN(1, 1, 2000), 14.47291966f, 35.89857007f);
	}

	@Override
	public void prepareRenderView() {
	}

	@Override
	public void renderView() {
		Quaternion q = new Quaternion();
		Matrix4f worldMatrix;

		VertexBuffer vbPos = stars.getMesh().getBuffer(VertexBuffer.Type.Position);
		FloatBuffer pos = (FloatBuffer) vbPos.getData();
		FloatBuffer size = (FloatBuffer) (stars.getMesh().getBuffer(VertexBuffer.Type.Size)).getData();
		pos.rewind();
		size.rewind();
		int i = 0;
		while (pos.hasRemaining()) {
			Vector3f p = new Vector3f(pos.get(), pos.get(), pos.get());
			q.lookAt(p, Vector3f.UNIT_Y);
			p.multLocal(100);
			p.addLocal(s.cam.getLocation());

			worldMatrix = new Matrix4f();
			worldMatrix.loadIdentity();
			worldMatrix.setRotationQuaternion(q);
			worldMatrix.setTranslation(p);

			//			float MOON_RAD = 1737.0f; //km
			//			float mult = (float)(0.1f / protoSky.MoonEarthDist);
			float scale = size.get();
			//			scale = s*2.0f;
			//			float scale = MOON_RAD*mult*1000f;
			worldMatrix.setScale(scale, scale, scale);

			s.rm.setWorldMatrix(worldMatrix);

			Star star = starList.get(i++);
			Vector3f RGB = star.RGB;
			//			Vector3f RGB = new Vector3f(1,1,1);
			ColorRGBA rgba = new ColorRGBA(RGB.x * SevenSky.starMult, RGB.y * SevenSky.starMult, RGB.z * SevenSky.starMult, 1.0f);
			rgba.r = 1.0f - FastMath.exp(-4f * rgba.r);
			rgba.g = 1.0f - FastMath.exp(-4f * rgba.g);
			rgba.b = 1.0f - FastMath.exp(-4f * rgba.b);

			starquad.getMaterial().setColor("Color", rgba);
			starquad.getMaterial().render(starquad, s.rm);

		}
	}

	public Geometry createStarQuad(AssetManager am, Camera cam) {
//			Geometry starquad = createFullScreenQuad(cam.getWidth()/(float)cam.getHeight(), cam.getProjectionMatrix().invert());
		Geometry starquad = createFullScreenQuad();
		starquad.setCullHint(Spatial.CullHint.Never);

		Material mat = new Material(am, "MatDefs/Sky/SkySpriteStar.j3md");
		//mat.setColor("Color", ColorRGBA.Gray);
		mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Off);
//			mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Screen);
//			mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
		mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
		mat.getAdditionalRenderState().setDepthTest(false);
		mat.getAdditionalRenderState().setDepthWrite(false);

		starquad.setMaterial(mat);

		return starquad;
	}
}
