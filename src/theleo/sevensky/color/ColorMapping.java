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
package theleo.sevensky.color;

import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import java.util.Scanner;

/**
 *
 * @author Juraj Papp
 */
public class ColorMapping {
	public static Matrix3f sRGB_D65 = new Matrix3f(3.2404542f, -1.5371385f, -0.4985314f, 
										-0.9692660f,  1.8760108f,  0.0415560f,
										0.0556434f, -0.2040259f,  1.0572252f);
	private static Vector3f[] data;
	static {
		int i = 0;
		data = new Vector3f[441];
		int wave = 390;
		try(Scanner in = new Scanner(ColorMapping.class.getResourceAsStream("lin2012xyz2e_1_7sf.csv"))) {
			while(in.hasNextLine()) {
				String line = in.nextLine();
				String[] s = line.split(",");
				
				int w = Integer.parseInt(s[0]);
				if(w != wave) throw new IllegalArgumentException(wave + " != " + w);
				wave++;
				float x = Float.parseFloat(s[1]);
				float y = Float.parseFloat(s[2]);
				float z = Float.parseFloat(s[3]);
				data[i] = new Vector3f(x,y,z);
				i++;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static int getMinWaveLength() { return 390;}
	public static int getMaxWaveLength() { return 830;}
	public static Vector3f getXYZ(int waveNM) {
		waveNM = (waveNM - getMinWaveLength());
		if(waveNM < 0 && waveNM > data.length) return null;
		return data[waveNM];
	}
}
