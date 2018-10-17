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
package theleo.sevensky.stars.hyg;

/**
 *
 * @author Juraj Papp
 */
public class HygRecord {

	/**
	 * The database primary key.
	 */
	public int id;
	/**
	 * The star's ID in the Hipparcos catalog, if known or -1.
	 */
	public int hip ;
	/**
	 *  The star's ID in the Henry Draper catalog, if known or -1.
	 */
	public int hd ;
	/**
	 * The star's ID in the Harvard Revised catalog, which is the same as its number in the Yale Bright Star Catalog or -1.
	 */
	public int hr ;
	/**
	 * The star's ID in the third edition of the Gliese Catalog of Nearby Stars or -1.
	 */
	public int gl ;
	/**
	 * The Bayer / Flamsteed designation, primarily from the Fifth Edition of the Yale Bright Star Catalog. This is a combination of the two designations. The Flamsteed number, if present, is given first; then a three-letter abbreviation for the Bayer Greek letter; the Bayer superscript number, if present; and finally, the three-letter constellation abbreviation. Thus Alpha Andromedae has the field value "21Alp And", and Kappa1 Sculptoris (no Flamsteed number) has "Kap1Scl".
	 */
	public String bf ;
	/**
	 * A common name for the star, such as "Barnard's Star" or "Sirius". I have taken these names primarily from the Hipparcos project's web site, which lists representative names for the 150 brightest stars and many of the 150 closest stars. I have added a few names to this list. Most of the additions are designations from catalogs mostly now forgotten (e.g., Lalande, Groombridge, and Gould ["G."]) except for certain nearby stars which are still best known by these designations.
	 */
	public String proper ;
	/**
	 * Right ascension for epoch and equinox 2000.0.
	 */
	public double ra ;
	/**
	 * Declination for epoch and equinox 2000.0.
	 */
	public double dec ;
	/**
	 * The star's distance in parsecs, the most common unit in astrometry. To convert parsecs to light years, multiply by 3.262. Double.NaN indicates missing or dubious (e.g., negative) parallax data in Hipparcos.
	 */
	public double dist ;
	/**
	 * The star's proper motion in right ascension in milliarcseconds per year.
	 */
	public double pmra ;
	/**
	 * The star's proper motion in declination, in milliarcseconds per year.
	 */
	public double pmdec ;
	/**
	 * The star's radial velocity in km/sec, where known or Double.NaN.
	 */
	public double rv ;
	/**
	 * The star's apparent visual magnitude.
	 */
	public double mag ;
	/**
	 * The star's absolute visual magnitude (its apparent magnitude from a distance of 10 parsecs).
	 */
	public double absmag ;
	/**
	 * The star's spectral type, if known.
	 */
	public String spect ;
	/**
	 * The star's color index (blue magnitude - visual magnitude), where known.
	 */
	public double ci ;
	/**
	 * The Cartesian coordinates of the star, in a system based on the equatorial coordinates as seen from Earth. +X is in the direction of the vernal equinox (at epoch 2000), +Z towards the north celestial pole, and +Y in the direction of R.A. 6 hours, declination 0 degrees.
	 */
	public double x, y, z ;
	/**
	 * The Cartesian velocity components of the star, in the same coordinate system described immediately above. They are determined from the proper motion and the radial velocity (when known). The velocity unit is parsecs per year; these are small values (around 1 millionth of a parsec per year), but they enormously simplify calculations using parsecs as base units for celestial mapping.
	 */
	public double vx, vy, vz ;
	/**
	 *  The positions in radians, and proper motions in radians per year.
	 */
	public double rarad, decrad, pmrarad, pmdecrad;
	/**
	 * The Bayer designation as a distinct value
	 */
	public String bayer ;
	/**
	 * The Flamsteed number as a distinct value or -1.
	 */
	public int flam ;
	/**
	 * The standard constellation abbreviation
	 */
	public String con ;
	/**
	 *  Identifies a star in a multiple star system. comp = ID of companion star, comp_primary = ID of primary star for this component, and base = catalog ID or name for this multi-star system. Currently only used for Gliese stars.
	 */
	public int comp ;
	/**
	 *  Identifies a star in a multiple star system. comp = ID of companion star, comp_primary = ID of primary star for this component, and base = catalog ID or name for this multi-star system. Currently only used for Gliese stars.
	 */
	public int comp_primary ;
	/**
	 *  Identifies a star in a multiple star system. comp = ID of companion star, comp_primary = ID of primary star for this component, and base = catalog ID or name for this multi-star system. Currently only used for Gliese stars.
	 */
	public String base ;
	/**
	 * Star's luminosity as a multiple of Solar luminosity.
	 */
	public double lum ;
	/**
	 * Star's standard variable star designation, when known.
	 */
	public String var ;
	/**
	 * Star's approximate magnitude range, for variables. This value is based on the Hp magnitudes for the range in the original Hipparcos catalog, adjusted to the V magnitude scale to match the "mag" field.
	 */
	public double var_min, var_max ;

	public HygRecord() {
	}
	
	public HygRecord(String[] data) {		
		this.id = i(data[0]);
		this.hip = i(data[1], -1);
		this.hd = i(data[2], -1);
		this.hr = i(data[3], -1);
		this.gl = i(data[4], -1);
		this.bf = data[5];
		this.proper = data[6];
		this.ra = d(data[7], Double.NaN);
		this.dec = d(data[8], Double.NaN);
		this.dist = d(data[9], Double.NaN);
			if(dist == dist && dist >= 10000) dist = Double.NaN;
		this.pmra = d(data[10], Double.NaN);
		this.pmdec = d(data[11], Double.NaN);
		this.rv = d(data[12], Double.NaN);
		this.mag = d(data[13], Double.NaN);
		this.absmag = d(data[14], Double.NaN);
		this.spect = data[15];
		this.ci = d(data[16], Double.NaN);
		this.x = d(data[17], Double.NaN);
		this.y = d(data[18], Double.NaN);
		this.z = d(data[19], Double.NaN);
		this.vx = d(data[20], Double.NaN);
		this.vy = d(data[21], Double.NaN);
		this.vz = d(data[22], Double.NaN);
		this.rarad = d(data[23], Double.NaN);
		this.decrad = d(data[24], Double.NaN);
		this.pmrarad = d(data[25], Double.NaN);
		this.pmdecrad = d(data[26], Double.NaN);
		this.bayer = data[27];
		this.flam = i(data[28], -1);
		this.con = data[29];
		this.comp = i(data[30], -1);
		this.comp_primary = i(data[31], -1);
		this.base = data[32];
		this.lum = d(data[33], Double.NaN);
		this.var = data[34];
		this.var_min = d(data[35], Double.NaN);
		this.var_max = d(data[36], Double.NaN);		
	}	
	static int i(String s) { return Integer.parseInt(s); }
	static int i(String s, int def) {
		try {
			return Integer.parseInt(s);
		}
		catch (NumberFormatException e) {
			
		}
		return def;
	}
	static double d(String s) { return Double.parseDouble(s); }
	static double d(String s, double def) {
		try {
			return Double.parseDouble(s);
		}
		catch (NumberFormatException e) {
			
		}
		return def;
	}
}
