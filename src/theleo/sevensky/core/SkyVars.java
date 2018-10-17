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
package theleo.sevensky.core;

import com.jme3.math.Vector3f;
import java.util.HashMap;
import theleo.sevensky.elements.Clouds;
import theleo.sevensky.elements.Sky;

/**
 *
 * @author Juraj Papp
 */
public class SkyVars {
	public static class Key {
		public String key;
		public Class type;
		public Key(String key, Class type) {
			this.key = key;
			this.type = type;
		}
		@Override
		public int hashCode() {
			return key.hashCode()^type.hashCode();
		}
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof Key) {
				Key k = (Key)obj;
				return key.equals(k.key) && type.equals(k.type);
			}
			return false;
		}
	}
	private HashMap<Key, Object> map = new HashMap<Key, Object>();
	
	public boolean has(Key key) { return map.containsKey(key); }
	public Object put(Key key, Object val) { if(val == null) return map.remove(key); else { if(key.type.isInstance(val)) return map.put(key, val); else throw new IllegalArgumentException("Key " + key.key + ": Value " + val + " is not of type " + key.type); } }
	public Object remove(Key key) { return map.remove(key); } 
	public <T> T get(Key key) { return (T)map.get(key); }
	public Vector3f getVec3(Key key) { return (Vector3f)map.get(key); }
	public float getF(Key key) { return (float)map.get(key); }
	public HashMap<Key,Object> map() {return map;}

	public SkyVars () {}
	
	
	public static SkyVars Earth() {
		SkyVars c = new SkyVars();
		c.put(Sky.PLANET_RADIUS, 6360e3f);
		c.put(Sky.ATMOSPHERE_RADIUS, 6420e3f);
		c.put(Sky.RAYLEIGH_SCALE_HEIGHT, 7994f);
		c.put(Sky.MIE_SCALE_HEIGHT, 1200f);
		c.put(Sky.SUN_MIE_G, 0.76f);
		c.put(Sky.MOON_MIE_G, 0.76f);
		c.put(Sky.SUN_INTENSITY, 22f);
		c.put(Sky.MOON_INTENSITY, 3f);
		
		c.put(Sky.RAYLEIGH_SCATTERING, new Vector3f(5.5e-6f, 13.0e-6f, 22.4e-6f));
		c.put(Sky.MIE_SCATTERING, new Vector3f(21e-6f, 21e-6f, 21e-6f));
		
		c.put(Sky.SunDir, new Vector3f(-1,0.1f,1).normalizeLocal());
		c.put(Sky.MoonPos, new Vector3f(1,0.1f,0.5f).normalizeLocal());
		
		
		c.put(Sky.SunLight, new Vector3f(1,1,1));
		
		c.put(Clouds.VOLUMETRIC_CLOUDS_FROM, 2000f);
		c.put(Clouds.VOLUMETRIC_CLOUDS_TO, 3000f);
		c.put(Clouds.Scattering, 0.001f);
		c.put(Clouds.LightScale, 0.5f);
		c.put(Clouds.Coverage, 1f);
		c.put(Clouds.DetailScale, 10f);
		c.put(Clouds.SunScale, 0.1f);
		c.put(Clouds.AmbientScale, 0.00005f);
		c.put(Clouds.DensityEdge, 1f);
		c.put(Clouds.RainDensity, 0f);
		c.put(Clouds.AltoCoverage, 0f);
		c.put(Clouds.AltoLightScale, 0.2f);
		
		return c;
	}
}
