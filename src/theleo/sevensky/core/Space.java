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
package theleo.sevensky.core;

import com.jme3.math.Vector3f;
import static java.lang.Math.*;
import templates.geom.Vector3d;
import theleo.sevensky.color.ColorMapping;
//import theleo.sevensky.vsop87.Earth;


/**
 *
 * @author Juraj Papp
 */
public class Space {
	public static SphericalPosition EarthPosition = null;
	public static AbstractNutationFunction NutationFunction = new ZeroNutationFunction();
	
	public static final double J2000 = JDN(1, 1, 2000)+JDTT(12, 0, 0);
	public static final double AU_KM = 149597870.7; //[km]
	public static final double C_KM = 299792.458;
	
	public static final double AU = 149597870700.0; //[m]
	public static final double ATM_TO_PA = 101325; //[Pa]
	public static final double M_TO_NM = 1e9;
	public static final double NM_TO_M = 1e-9;
	
	public static  final double JD_SEC = 1.1574074074074073E-5;
	
	public static final double PI = Math.PI;
	public static final double TWO_PI = Math.PI+Math.PI;
	public static final double MIN_TO_DEG = 1.0/60.0;
	public static final double SEC_TO_DEG = 1.0/3600.0;
	
	public static final double STEFAN_BOLTZMANN = 5.67e-8; //[W/(m2K4)] upgrade
	public static final double WIEN = 2.897772917e-3; //[Km]
	public static final double BOLTZMANN = 1.3806485279e-23; //[J/K]	
	public static final double PLANCK = 6.62607004081e-34; //[J/s] 
	public static final double LIGHT_SPEED = 299792458;//[m/s]
	
	public static final double SOLID_ANGLE_SPHERE = 4.0*PI; 
	
	public static final double SOLAR_CONSTANT = 1360.8; //[W/m2]
	public static final double SOLAR_LUMINOSITY = 3.8269693916066276E26; //[W]
	
	
	/**
	 * Solar Photosphere temperature {@value} [K]
	 */
	public static final double SOLAR_PHOTOSPHERE_TEMP = 5772;
	
	
	/**
	 * Natural logarithm of 10
	 */
	public static final double LN_10 = 2.302585092994046;


		
	/**
	 * Calculates the distance to a star given its parallax in arcseconds.
	 * 
	 * @param parallaxArcSec - star parallax in arcseconds
	 * @return approx distance to star in parsecs
	 */
	public static double toParsecs(double parallaxArcSec) {
		return 1.0/parallaxArcSec;
	}
	/**
	 * Calculates luminosity of a star given its radius and temperature.
	 * 
	 * @param starRadiusM [m]
	 * @param starTempK [K]
	 * @return Luminosity [W]
	 */
	public static double starLuminosity(double starRadiusM, double starTempK) {
		starTempK = starTempK*starTempK;
		return 4.0*STEFAN_BOLTZMANN*(starRadiusM*starRadiusM)*starTempK;
	}
	/**
	 * Calculates the star radius given its luminosity and temperature.
	 * 
	 * @param starLuminosityW - luminosity of star in [W]
	 * @param starTempK - star temperature [K]
	 * @return star radius in meters [m]
	 */
	public static double starRadius(double starLuminosityW, double starTempK) {
		starTempK = starTempK*starTempK;
		return sqrt(starLuminosityW/(4.0*STEFAN_BOLTZMANN*starTempK));
	}
	/**
	 * Computes color in XYZ, given temperature in Kelvin.
	 * To convert to RGB you can multiply the color by XYZ->RGB matrix,
	 * such as ColorMapping.sRGB_D65.
	 * 
	 * @param TempK - black body temperature [L]
	 * @return color in XYZ format
	 */
	public static Vector3f blackbodyXYZ(double TempK) {
		int min = ColorMapping.getMinWaveLength();
		int max = ColorMapping.getMaxWaveLength();
		Vector3f sum = new Vector3f();
		for(int i = min; i <= max; i++) {
			float r = (float)Space.blackbodySpectralIrradiance(i*Space.NM_TO_M, TempK);
			Vector3f xyz = ColorMapping.getXYZ(i);
			sum.addLocal(r*xyz.x, r*xyz.y, r*xyz.z);
		}
		return sum;
	}	
	/**
	 * Calculates the star irradiance at given distance.
	 * 
	 * @param starLuminosity - star luminosity [W]
	 * @param distanceM - distance [m]
	 * @return irradiance [W/m2]
	 */
	public static double starIrradiance(double starLuminosity, double distanceM) {
		return starLuminosity/(4.0*PI*distanceM*distanceM);
	}

	/**
	 * Calculates approximate temperature from color index (B-V).
	 * 
	 * @param colorIndex - B-V
	 * @return temperature [K]
	 */
	public static double temperature(double colorIndex) {
		return 4600 * (1 / (0.92 * colorIndex + 1.7) + 1 / (0.92 * colorIndex + 0.62));
	}
	
	/**
	 * Calculates the maximum wavelength produced
	 * by a blackbody with given temperature.
	 * 
	 * @param tempK - temperature [K]
	 * @return max. wavelength [m] at the temperature
	 */
	public static double blackbodyMaxWave(double tempK) {
		return WIEN/tempK;
	}
	
	/**
	 * Calculates the blackbody spectral radiance at given wavelength and temperature.
	 * 
	 * @param wavelenM - waveleght [m]
	 * @param tempK - temperature [K]
	 * @return spectral radiance [W/(m2 sr m)] 
	 */
	public static double blackbodyRadiance(double wavelenM, double tempK) {
		double lam = wavelenM*wavelenM;
		lam = lam*lam*wavelenM;
		return (2.0*PLANCK*LIGHT_SPEED*LIGHT_SPEED) 
				/ (lam*(exp((PLANCK*LIGHT_SPEED)/(wavelenM*BOLTZMANN*tempK))-1));
	}
	
	/**
	 * Calculates the blackbody spectral irradiance at given wavelength and temperature.
	 * 
	 * @param wavelenM - waveleght [m]
	 * @param tempK - temperature [K]
	 * @return spectral radiance [W/(m2 m)] 
	 */
	public static double blackbodySpectralIrradiance(double wavelenM, double tempK) {
		double lam = wavelenM*wavelenM;
		lam = lam*lam*wavelenM;
		return (2.0*PI*PLANCK*LIGHT_SPEED*LIGHT_SPEED) 
				/ (lam*(exp((PLANCK*LIGHT_SPEED)/(wavelenM*BOLTZMANN*tempK))-1));
	}
	
	/**
	 * 
	 * Calculates the irradiance of blackbody given its temperature.
	 * 
	 * @param tempK - temperature [K]
	 * @return Irradiance [W/m2] 
	 */
	public static double blackbodyIrradiance(double tempK) {
		tempK *= tempK;
		return STEFAN_BOLTZMANN*tempK*tempK;
	}
	
	/**
	 * Calculates the integral [0, wavelenM] of irradiance of blackbody given its temperature.
	 * 
	 * @param wavelenM wavelength [m]
	 * @param tempK temperature [K]
	 * @param steps number of steps, eg. 128
	 * @return Irradiance in inverval [0,wavelenM] [W/m2]
	 */
	public static double blackbodyIrradianceIntegral(double wavelenM, double tempK, int steps) {		
		double b = (PLANCK*LIGHT_SPEED)/(BOLTZMANN*tempK);
		
		double step = wavelenM/steps;
		double w = step*0.5;
		double sum = 0.0;
		for(int i = 0; i < steps; i++) {
			double lam = w*w;
			sum += step / (lam*lam*w*(exp(b/w)-1));
			w += step;
		}
		return (2.0*PI*PLANCK*LIGHT_SPEED*LIGHT_SPEED)*sum;
	}
	
	/**
	 * Calculates the rayleight scattering coefficinet for
	 * parcticular wavelength.
	 * 
	 * @param wavelengthNM - eg: 700
	 * @param n - refractive index of air eg: 1.00029
	 * @param N - molecular number density of standart atmosphere
	 *			eg: 2.504e25
	 * @return 
	 */
	public static double rayleight(double wavelengthNM, double n, double N) {
		n = n*n-1;
		n *= n;
		wavelengthNM *= 1e-9;
		wavelengthNM *= wavelengthNM;
		wavelengthNM *= wavelengthNM;
		
		return 8*PI*PI*PI*n*0.3333333333333* ( /*p(h)*/ 1 )/(N*wavelengthNM);
	}
	
	/**
	 * 
	 * @param dewPointTempC
	 * @return saturation vapour pressure
	 */
	public static double saturationVapourPa(double dewPointTempC) {
		double T = dewPointTempC + 273.15;
		if(dewPointTempC < 0) {
			double A1 = -13.928169;
			double A2 = 34.7078238;
			double PHI = T/273.16;
			double Y = A1*(1-pow(PHI, -1.5)) + A2*(1-pow(PHI, -1.25));
			return 611.657*exp(Y);
		}
		
		double K1 = 1.16705214528E+03;
		double K2 = -7.24213167032E+05;
		double K3 = -1.70738469401E+01;
		double K4 = 1.20208247025E+04;
		double K5 = -3.23255503223E+06;
		double K6 = 1.49151086135E+01;
		double K7 = -4.82326573616E+03;
		double K8 = 4.05113405421E+05;
		double K9 = -2.38555575678E-01;
		double K10 = 6.50175348448E+02;
		
		double Omega = T + K9/(T-K10);
		double O2 = Omega*Omega;
		double A = O2 + K1*Omega + K2;
		double B = K3*O2 + K4*Omega + K5;
		double C = K6*O2 + K7*Omega + K8;
		double X = -B + sqrt(B*B-4*A*C);
		double SV = (2.0*C/X);
		SV *= SV;
		SV *= SV;
		return 1e6*SV;
	}
	
	private static double enhancementFactor(double airPressurePa, double tempC) {
		return 1.00062 + 3.14e-8 * airPressurePa + 5.60e-7 * tempC*tempC;
	}
	/**
	 * 
	 * @param airPressurePa
	 * @param dewPointTempC
	 * @return humidity mole fraction
	 */
	public static double humidityMoleFraction(double airPressurePa, double dewPointTempC) {
		return enhancementFactor(airPressurePa, dewPointTempC) * saturationVapourPa(dewPointTempC)/ airPressurePa;
	}
	/**
	 * 
	 * @param relativeHumidity - range [0,1]
	 * @param airPressurePa
	 * @param airTempC
	 * @return humidity mole fraction 
	 */
	public static double humidityMoleFraction(double relativeHumidity, double airPressurePa, double airTempC) {
		return relativeHumidity*enhancementFactor(airPressurePa, airTempC) * saturationVapourPa(airTempC)/ airPressurePa;
	}
	
	/**
	 * Calculates air IOR.
	 * 
	 * https://emtoolbox.nist.gov/Wavelength/Documentation.asp
	 * [Ciddor equation]
	 * 
	 * @param wavelengthNM wavelength in NM eg: 700, range is [230-1690]
	 * @param airPressurePa
	 * @param airTempC 
	 * @param relativeHumidity - range [0, 1]
	 * @param xCO2 - CO2 concentration in umol/mol eg: 450
	 * @return air IOR at temperature: 15 degrees, pressure: 101325 Pa
	 */
	public static double airIOR(double wavelengthNM, double airPressurePa,
			double airTempC, double relativeHumidity,
			double xCO2) {
		wavelengthNM *= 1e-3; // to micrometers
		double t = airTempC;
		double T = airTempC + 273.15;
		
		double xv = humidityMoleFraction(relativeHumidity, airPressurePa, airTempC);
	
		double w0 = 295.235, w1 = 2.6422, w2 = -0.03238, w3 = 0.004028;
		double k0 = 238.0185, k1 = 5792105, k2 = 57.362,  k3 = 167917;
		double a0 = 1.58123e-6, a1 = -2.9331e-8, a2 = 1.1043e-10;
		double b0 = 5.707e-6, b1 = -2.051e-8;
		double c0 = 1.9898e-4, c1 = -2.376e-6;
		double d = 1.83e-11, e = -0.765e-8;
		double pR1 = 101325, TR1 = 288.15;
		double Za = 0.9995922115;
		double psiVS = 0.00985938;
		double R = 8.314472;
		double Mv = 0.018015;
		
		double S = 1/(wavelengthNM*wavelengthNM);
		double Ras = 1e-8*( ( k1/(k0-S)) + (k3/(k2-S))  );
		double Rvs = 1.022e-8*(w0 + w1*S + w2*S*S + w3*S*S*S);
		double Ma = 0.0289635+1.2011e-8*(xCO2-400);
		double Raxs = Ras*(1+5.34e-7*(xCO2-450));
		
		double pT = airPressurePa/T;
		
		
		double Zm = 1-pT*(a0+a1*t+a2*t*t+(b0+b1*t)*xv + (c0+c1*t)*xv*xv) + pT*pT*(d+e*xv*xv);
		double psiAXS = pR1*Ma/(Za*R*TR1);
		double psiV = xv*airPressurePa*Mv/(Zm*R*T);
		double psiA = (1-xv)*airPressurePa*Ma/(Zm*R*T);
		
		return 1+(psiA/psiAXS)*Raxs + (psiV/psiVS)*Rvs;
	}
	
	/**
	 * Converts absorbance into opetical depth.
	 * 
	 * @param opticalDepth [m]
	 * @return 
	 */
	public double absorbance(double opticalDepth) {
		return opticalDepth/LN_10;
	}
	
	/**
	 * Converts optical depth into absorbance.
	 * 
	 * @param absorbance 
	 * @return optical depth [m]
	 */
	public double opticalDepth(double absorbance) {
		return absorbance*LN_10;
	}
	
	/**
	 * Calculates the optical depth given the received and trasmitted flux.
	 * 
	 * @param fluxReceived
	 * @param fluxTransmitted
	 * @return optical depth [m]
	 */
	public double opticalDepth(double fluxReceived, double fluxTransmitted) {
		return Math.log(fluxReceived/fluxTransmitted);
	}
	
	//Right Ascension Declination
	
	public static double toRadians3(double deg, double min, double sec) {
		return Math.toRadians(deg+min*MIN_TO_DEG+sec*SEC_TO_DEG);
	}
	public static double[] eclipticToEquatorial(double eLon, double eLat, double e) {
		return eclipEquat(1.0, eLon, eLat, e);
	}
	public static double[] equatorialToEcliptic(double eLon, double eLat, double e) {
		return eclipEquat(-1.0, eLon, eLat, e);
	}
	public static double JDTT(int hour, int minute, double second) {
		return 0.041666666666666664*(hour-12.0)+6.944444444444445E-4*minute+1.1574074074074073E-5*second;
	}
	public static String JDTT(double jdtt) {
		long h = (((long)floor(jdtt/0.041666666666666664))+12); jdtt -= 0.041666666666666664*(h-12);
		h = h%24;
		int m = (int)floor(jdtt/6.944444444444445E-4); jdtt -= 6.944444444444445E-4*m;
		double s = jdtt/1.1574074074074073E-5;
		return h+":"+m+":"+s;
	}
	public static double DMS(int deg, int minute, double second) {
		return Math.toRadians(deg + 0.016666667*minute+0.000277778*second);
	}
	public static double HMS(int hours, int minute, double second) {
		return 15.0*Math.toRadians(hours + 0.016666667*minute+0.000277778*second);
	}
	public static double HMS(String hms) {
		String[] s = hms.split(":");
		return HMS(Integer.parseInt(s[0]), Integer.parseInt(s[1]),
				Double.parseDouble(s[2]));
	}
	public static double DMS(String dms) {
		String[] s = dms.split(":");
		return DMS(Integer.parseInt(s[0]), Integer.parseInt(s[1]),
				Double.parseDouble(s[2]));
	}
	public static double JD2000Millenium(double JD) {
		return (JD - 2451545) / 365250;
	}
	public static double JDN(int day, int month, int year) {
//		if(month <= 2) { year--; month += 12; }
//		return ((int)(365.25*(year+4716))) + ((int)(30.6001*(month+1))) + day - 1524.5;
		int a = (14-month) / 12;
		int y = year + 4800 - a;
		int m = month + 12*a - 3;
		
		double jdn = day + floor((153*m+2)/5.0) + 365.0*y
				+ floor(y/4.0) - floor(y/100.0) +
				floor(y/400.0) - 32045.0;
		return jdn;
	}

	private static double[] eclipEquat(double sign, double a, double b, double e) {
		double y = cos(e)*sin(a)-tan(b)*sin(e)*sign;
		double x = cos(a); 
		double ascLon = atan2(y, x);
		double decLat = asin(sin(b)*cos(e)+cos(b)*sin(e)*sin(a)*sign);

//		if(x < 0) asc += PI;
		ascLon -= TWO_PI*floor(ascLon/TWO_PI);
		return new double[] {ascLon, decLat};
	}
	
	
//	public static void main(String[] args) {
//		double rad = toRadians3(0,0,2.45);
//		
//		outA("lon", toRadians3(139, 41, 10));
//		outA("lat", toRadians3(4, 52, 31));
//		
//		double[] eq = eclipticToEquatorial(toRadians3(139, 41, 10),
//					toRadians3(4, 52, 31), toRadians(23.441884));
//		
//		System.out.println("ASC " + toDegrees(eq[0]));
//		
//		outH("asc", eq[0]);
//		outA("dec", eq[1]);
//		
//		double[] ecl = equatorialToEcliptic(eq[0], eq[1], toRadians(23.441884));
//		outA("Lon", ecl[0]);
//		outA("Lat", ecl[1]);
//		
////		outA("obl", obliquityOfTheEcliptic(2451545.0));
//	}
	
//	public static void main(String[] args) {
//		double L = toRadians(26.8388);
//		double RA = toRadians(26.6580);
//		double Decl = toRadians(11.0084);	
//		observer(L, RA, Decl);
//	}
	
	public static double[] observer(double JD, double GMST, double lon, double lat) {
		double LON = -84.39733;
		double LAT = 33.775867;
		return observer(JD, GMST, lon, lat, LON, LAT);
	}
	public static double[] observer(double JD, double GMST, double lon, double lat,
			double oLON, double oLAT) {
		
		double obliq = obliquityOfTheEcliptic(JD);
		double nuta  = nutationOfObliquityOfTheEcliptic(JD);
		
//		out("obliq", toDegrees(obliq));
//		out("nuta", toDegrees(nuta));
		
		double[] RAdec = eclipticToEquatorial(lon, lat, obliq+nuta);
		double RA = RAdec[0];
		double Dec = RAdec[1];
		
		double UT = 0.0;
		
//		double GMST0 = toDegrees(L)/15.0 + 12;
		
		
//		System.out.println("GMST0 " + GMST);
//		double LON = +15;
//		double LAT = +60;
		
		
		double SIDETIME = toDegrees(GMST)/15.0 + UT + oLON/15.0;
		
//		System.out.println("SIDETIME " + SIDETIME);
		
		double HA = SIDETIME*15.0 - toDegrees(RA);
		
//		double[] azAzim = toAzimAlt(LAT, toRadians(HA), Dec);
//		outA("az", azAzim[0]);
//		outA("alt", azAzim[1]);
			
//		System.out.println("HA " + HA);
		
		double x = cos(toRadians(HA)) * cos(Dec); 
		double y = sin(toRadians(HA)) * cos(Dec); 
		double z = sin(Dec);  
		
//		System.out.println("x " + x );
//		System.out.println("x " + y );
//		System.out.println("x " + z );
		
		double xhor = x * sin(toRadians(oLAT)) - z * cos(toRadians(oLAT));
		double yhor = y;
		double zhor = x * cos(toRadians(oLAT)) + z * sin(toRadians(oLAT));
		
//		System.out.println("x " + xhor);
//		System.out.println("x " + yhor);
//		System.out.println("x " + zhor);
		
		double azimuth  = atan2(yhor, xhor) + PI;
//		double altitude = atan2( zhor, sqrt(xhor*xhor+yhor*yhor) );
		double altitude = asin(zhor); //= atan2( zhor, sqrt(xhor*xhor+yhor*yhor) );
		
		
		//parallax
						 
//		outA("az", azimuth);
//		outA("alt", altitude);
		
		return new double[] {azimuth, altitude};
	}
	public static double[] observerRaDec(double GMST, double RA, double Dec,
			double oLON, double oLAT) {
		
//		double obliq = obliquityOfTheEcliptic(JD);
//		double nuta  = nutationOfObliquityOfTheEcliptic(JD);
//		
//		out("obliq", toDegrees(obliq));
//		out("nuta", toDegrees(nuta));
//		
//		double[] RAdec = eclipticToEquatorial(lon, lat, obliq+nuta);
//		double RA = RAdec[0];
//		double Dec = RAdec[1];
		
		double UT = 0.0;
		
//		double GMST0 = toDegrees(L)/15.0 + 12;
		
		
//		System.out.println("GMST0 " + GMST);
//		double LON = +15;
//		double LAT = +60;
		
		
		double SIDETIME = toDegrees(GMST)/15.0 + UT + oLON/15.0;
		
//		System.out.println("SIDETIME " + SIDETIME);
		
		double HA = SIDETIME*15.0 - toDegrees(RA);
		
//		double[] azAzim = toAzimAlt(LAT, toRadians(HA), Dec);
//		outA("az", azAzim[0]);
//		outA("alt", azAzim[1]);
			
//		System.out.println("HA " + HA);
		
		double x = cos(toRadians(HA)) * cos(Dec); 
		double y = sin(toRadians(HA)) * cos(Dec); 
		double z = sin(Dec);  
		
//		System.out.println("x " + x );
//		System.out.println("x " + y );
//		System.out.println("x " + z );
		
		double xhor = x * sin(toRadians(oLAT)) - z * cos(toRadians(oLAT));
		double yhor = y;
		double zhor = x * cos(toRadians(oLAT)) + z * sin(toRadians(oLAT));
		
//		System.out.println("x " + xhor);
//		System.out.println("x " + yhor);
//		System.out.println("x " + zhor);
		
		double azimuth  = atan2(yhor, xhor) + toRadians(180);
//		double altitude = atan2( zhor, sqrt(xhor*xhor+yhor*yhor) );
		double altitude = asin(zhor); //= atan2( zhor, sqrt(xhor*xhor+yhor*yhor) );
		
		
		//parallax
						 
//		outA("az", azimuth);
//		outA("alt", altitude);
		
		return new double[] {azimuth, altitude};
	}
	public static Vector3d sphericalToCartesian(double l, double b, double r)	{
		double rcosb = r * cos(b);
		return new Vector3d(rcosb * cos(l), rcosb * sin(l), r * sin(b));
	}
	public static Vector3d sphericalToCartesianY(double l, double b, double r)	{
		double rcosb = r * cos(b);
		return new Vector3d(rcosb * cos(l), r * sin(b), rcosb * sin(l));
	}
	public static Vector3d cartesianToSpherical(double x, double y, double z) {
		double xy = x*x + y*y;
		Vector3d res = new Vector3d();
		if (xy > 0) {
			res.x = range2PI(atan2(y, x));
			res.y = atan2(z, sqrt(xy));
			res.z = sqrt(xy + z*z);
		} else {		
			res.x = 0.0;
			if (z == 0.0) res.y = 0.0;
			else res.y = (z > 0.0) ? PI/2. : -PI/2.;
			res.z = abs(z);
		}
		return res;
	}
	public static Vector3d cartesianToSphericalY(double x, double y, double z) {
		double xy = x*x + z*z;
		Vector3d res = new Vector3d();
		if (xy > 0) {
			res.x = range2PI(atan2(z, x));
			res.y = atan2(y, sqrt(xy));
			res.z = sqrt(xy + y*y);
		} else {		
			res.x = 0.0;
			if (y == 0.0) res.y = 0.0;
			else res.y = (y > 0.0) ? PI/2. : -PI/2.;
			res.z = abs(y);
		}
		return res;
	}
	public static Vector3d parralax(double tha, double tdec, double phi, double ht,
			double rho, double aha, double adec) {
		double /*last_phi = 1000.0, last_ht = -1000.0,*/ xobs, zobs;

		/* avoid calcs involving the same phi and ht */
//		if (phi != last_phi || ht != last_ht) {
			double cphi, sphi, robs, e2 = (2 - 1/298.257)/298.257;
			cphi = cos(phi);
			sphi = sin(phi);
			robs = 1/sqrt(1 - e2 * sphi * sphi);

			/* observer coordinates: x to meridian, y east, z north */
			xobs = (robs + ht) * cphi;
			zobs = (robs*(1-e2) + ht) * sphi;
//			last_phi  =  phi;
//			last_ht  =  ht;
//		}

//		sphcart(-tha, tdec, *rho, &x, &y, &z);
//		cartsph(x - xobs, y, z - zobs, aha, adec, rho);
		Vector3d xyz = sphericalToCartesian(-tha, tdec, rho);
		xyz = cartesianToSpherical(xyz.x - xobs, xyz.y, xyz.z - zobs/*, aha, adec, rho*/);
		
		//*aha *= -1;
		xyz.x *= -1;
		xyz.x = range2PI(xyz.x);
		
		return new Vector3d(xyz.z, xyz.x, xyz.y);
		
		//return rho, aha, adec
	}
	/** latitude, ha, dec*/
	public static double parallacticLHD(double lt, double ha, double dec) {
		double A, b, cc, sc, B;

		A = ha;
		b = PI/2 - lt;
		cc = sin(dec);
		sc = cos(dec);
		B = solve_sphere (A, b, cc, sc /*,NULL, &B*/)[1];

		if (B > PI)
			B -= 2*PI;
		return (B);
	}
	public static double[] solve_sphere (double A, double b, double cc, double sc
			/*,double cap, double Bp*/) {
		double cb = cos(b), sb = sin(b);
		double sA, cA = cos(A);
		double x, y;
		double ca;
		double B;
		
		double cap, Bp;

		ca = cb*cc + sb*sc*cA;
		if (ca >  1.0) ca =  1.0;
		if (ca < -1.0) ca = -1.0;
		cap = ca;

		/*if (!Bp)
			return;*/

		if (sc < 1e-7)
			B = cc < 0 ? A : PI-A;
		else {
			sA = sin(A);
			y = sA*sb*sc;
			x = cb - ca*cc;
			B = y != 0.0 ? (x != 0.0 ? atan2(y,x) : (y>0 ? PI/2 : -PI/2)) : (x>=0 ? 0 : PI);
		}
		Bp = B;
		Bp = range2PI(Bp);
		return new double[] { cap, Bp};
	}
	public static double[] toAzimAlt(double lt, double ha, double dec) {
		double slt, clt;
//		double cap, B;

//		if (lt != last_lt) {
			slt = sin(lt);
			clt = cos(lt);
//			last_lt = lt;
//		}

		double capB[] = solve_sphere(-ha, PI/2.0-dec, slt, clt);
		double az = capB[1];
		double alt = PI/2.0 - acos(capB[0]);
		return new double[] {az, alt};
	}
	
	public static final double SIDEREAL_DAY_SEC = 23*60*60+56*60+4.0916; 
	public static double rangePIPI(double val) {
		return val - TWO_PI * floor((val + Math.PI) / TWO_PI);
	}
	public static double range2PI(double val) {
		return val - TWO_PI * floor((val) / TWO_PI);
	}
	public static double equationOfEquinoxes(double JD) {
		//Greenwich apparent ST = GMST + equation of the equinoxes

		//*  Arcseconds to radians
      double DAS2R = 4.848136811095359935899141e-6;

//		*  2Pi
//      DOUBLE PRECISION D2PI
//      PARAMETER (D2PI = 6.283185307179586476925287D0)

	//*  Reference epoch (J2000), JD
      //DOUBLE PRECISION DJ0
      double DJ0 = 2451545.0;

//*  Days per Julian century
//      DOUBLE PRECISION DJC
      double DJC = 36525.0;

//      DOUBLE PRECISION T, OM, DPSI, DEPS, EPS0
//      DOUBLE PRECISION iau_ANPM, iau_OBL80

//* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

//*  Interval between fundamental epoch J2000.0 and given epoch (JC).
      double T = ( ( JD-DJ0 ) /* + EPOCH2 */ ) / DJC;

//*  Longitude of the mean ascending node of the lunar orbit on the
//*  ecliptic, measured from the mean equinox of date.
      double OM = rangePIPI( ( 450160.280 + ( -482890.539 +
                      ( 7.455 + 0.008 * T ) * T ) * T ) * DAS2R
                    + (-5.0*T % 1.0) * TWO_PI );

		double DPSI = NutationFunction.nutationInLongitudeObliquity(JD)[0];
		double EPS0 = obliquityOfTheEcliptic(JD);
		
//		System.out.println("nuta " + Arrays.toString(nutaLatObli));
//*  Nutation components and mean obliquity.
//      CALL iau_NUT80 ( EPOCH1, EPOCH2, DPSI, DEPS )
//      EPS0 = iau_OBL80 ( EPOCH1, EPOCH2 )

//*  Equation of the equinoxes.
      return DPSI * cos(EPS0) + DAS2R * ( 0.00264 * sin(OM) + 0.000063 * sin(OM+OM));

//*  Finished.
	}
	public static double GMST(double JD) {
		//GMST (in seconds at UT1=0) = 24110.54841 + 8640184.812866 * T
		//		   + 0.093104 * T^2 - 0.0000062 * T^3
		double d = JD - 2451545.0;
		double T = d / 36525;
//		double GMTS = 24110.54841 + 8640184.812866 * T + 0.093104 * T*T - 0.0000062 * T*T*T;		
		double GMTS = 7.272205216643039903848712e-5*(
				86400.0 * ( JD % 1.0) +
				24110.54841 - 43200 + (8640184.812866 + (0.093104 - 0.0000062*T)*T)*T);		
		return range2PI(GMTS);
	}
//	public static double GMTS2(double JD) {
//	double t, gmst;
//
/* TT Julian centuries since J2000.0. */
//   t = ((tta - DJ00) + ttb) / DJC;
//
/* Greenwich mean sidereal time, IAU 2006. */
//   gmst = iauAnp(iauEra00(uta, utb) +
//                  (    0.014506     +
//                  (  4612.156534    +
//                  (     1.3915817   +
//                  (    -0.00000044  +
//                  (    -0.000029956 +
//                  (    -0.0000000368 )
//          * t) * t) * t) * t) * t) * DAS2R);
//
//   return gmst;
//	}
	
	public static double[] sun3(double JD) {

		double t = (JD - 2451545) / 365250;
		double lon = EarthPosition.getLon(t);
		double lat = EarthPosition.getLat(t);
		double rad = EarthPosition.getRad(t);

		lon = range2PI(lon + PI);
		lat = rangePIPI(lat);

		out("lon", toDegrees(lon));
		out("lat", toDegrees(lat));
		out("rad", rad);
		
		double obliq = obliquityOfTheEcliptic(JD);
		double nuta  = nutationOfObliquityOfTheEcliptic(JD);
		
		out("obliq", toDegrees(obliq));
		out("nuta", toDegrees(nuta));
		
		double[] RAdec = eclipticToEquatorial(lon, lat, obliq+nuta);
		
		outH("ascen", (RAdec[0]));
		outA("declin", (RAdec[1]));
		
		return new double[] {lon, lat, rad};
	}
	public static void sun(double JD) {
		double JDTT = JD;
		double n = JD - 2451544.5;
		
		//mean anomaly
		double M = toRadians(357.5291)+toRadians(0.98560028)*n;
		double gOld = 6.240040768070287 + 0.017201970343643867*n;
		
		M = M % TWO_PI;
		gOld = gOld % TWO_PI;
		
		out("g", M);
		out("gOld", gOld);
		
		//C≈ C1sinM + C2sin(2M) + C3sin(3M) + C4sin(4M) + C5sin(5M) + C6sin(6M)
		
		double C = toRadians(1.9148)*sin(M) +
				 toRadians(0.0200)*sin(2.0*M) + 
				 toRadians(0.0003)*sin(3.0*M);
		//true anomaly	
		double v = M + C;
		
		out("v", v);
		
		
		double L = M + toRadians(102.9373);
		double eLon = L + C;
		
		double LSun = L + toRadians(180.0);
		double eLonSun = LSun + C;
		
		eLon = eLon % TWO_PI;
		eLonSun = eLonSun % TWO_PI;
				
		out("L", L);
		out("eLon", eLon);
		out("LSun", LSun);
		out("eLonSun", eLonSun);
		
//		eLonSun = 6.226416;
		double eLat = -0.000002;
		
		double e = obliquityOfTheEcliptic(JD) + nutationOfObliquityOfTheEcliptic(JDTT);
		
		double[] RAdec = eclipticToEquatorial(eLonSun, eLat, e);
		
		outH("ascen", RAdec[0]);
		outA("declin", RAdec[1]);
		
		double a = toRadians(87.1807);
		double test = toRadians(1.9148)*sin(a) +
				 toRadians(0.0200)*sin(2.0*a) + 
				 toRadians(0.0003)*sin(3.0*a);
		
		out("test", toDegrees(test));
	}
	public static void sun2(double JD) {
		//149597870700 astronomical unit
		//Michalsky?
		
//		double JD = 2451545.0;
//		double JD = 2458015.5; //19/9/2017
//		double JD = 2451545.0;
		double JDTT = JD;
		
		//days since 1 Jan 2000, Terrestrial Time
		double n = JD - 2451544.5;
		
		//The mean longitude of the Sun, corrected for the aberration of light, is:
//		double L = 280.460 + 0.9856474*n;
		double L = 4.894950420143296 + 0.017202792393721553*n;
		
		//The mean anomaly of the Sun (actually, of the Earth in its orbit around the Sun,
		//but it is convenient to pretend the Sun orbits the Earth), is:
//		double g = 357.528 + 0.9856003*n;
//		double g = 6.240040768070287 + 0.017201970343643867*n;
		double g = toRadians(357.5291)+toRadians(0.98560028)*n;
		
		L = L % TWO_PI;
		g = g % TWO_PI;
	
		//the ecliptic longitude of the Sun is:
		
//		double eLon = L + 1.915*Math.sin(g)+0.020*Math.sin(2.0*g);
		double eLon = L + 0.03342305517569141*Math.sin(g)
				+3.4906585039886593E-4*Math.sin(g+g);
		
		//The ecliptic latitude of the Sun is nearly:
		
		double eLat = 0; //toRadians3(0, 0, -0.2); //toRadians(0.9*SEC_TO_DEG);
		
		//distance in au
		double R = 1.00014 - 0.01671*Math.cos(g) - 0.00014*Math.cos(2.0*g);
		
		
		outH("L", L);
		outH("g", g);
		outH("eLon", eLon);
		outA("eLat", eLat);
		out("R", R);
		
		double e = obliquityOfTheEcliptic(JD) + nutationOfObliquityOfTheEcliptic(JDTT);
		
		outA("obli nuta ", e);
		
//		double ascen = atan2(cos(e)*sin(eLon), cos(eLon));
		//a=aTand(Tand(L)*Cosd(e) - Tand(B)*Sind(e)/Cosd(L))
		
		
		double[] RAdec = eclipticToEquatorial(eLon, eLat, e);
		
		outH("ascen", RAdec[0]);
		outA("declin", RAdec[1]);
	}
	//http://www.neoprogrammics.com/obliquity_of_the_ecliptic/
	public static double obliquityOfTheEcliptic(double JD) {
		double n = (JD - 2451545.0) / 3652500.0;        
//		double n = (JD - 2451545.0) / 3652422.0;        
		double t = n;
					//23, 26, 21.448        0, 0, 4680.93
		double e = 0.40909280422232885 - 0.022693789043160606*t; t *= n;
		e -= 7.514612057197807E-6*t; t *= n; //0, 0, 1.55
		e += 0.009692637519582398*t; t *= n; //0, 0, 1999.25
		e -= 2.490972693540796E-4*t; t *= n; //0, 0, 51.38
		e -= 0.0012104343176261784*t; t *= n; //0, 0, 249.67
		e -= 1.893197424732738E-4*t; t *= n; //0, 0, 39.05
		e += 3.4518734094998965E-5*t; t *= n; //0, 0, 7.12
		e += 1.3511757292522766E-4*t; t *= n; //0, 0, 27.87
		e += 2.807071213624213E-5*t; t *= n; //0, 0, 5.79
		e += 1.1877935187183632E-5*t; t *= n; //0, 0, 2.45
		return e;
	}
	
	public static double nutationOfObliquityOfTheEcliptic(double JDTT) {
		return NutationFunction.nutationOfObliquityOfTheEcliptic(JDTT);
	}
	
	public static void out(String msg, double angle) {
		System.out.println(msg + ": \t" + angle);
	}
	public static void outA(String msg, double angle) {
		double deg = toDegrees(angle);
		double min = (deg-floor(deg))*60;
		double sec = (min-floor(min))*60;
		System.out.println(msg + ": \t" + angle + "\t" + ((int)deg) + ":" + ((int)min) + ":" + (sec));
	}
	public static void outH(String msg, double angle) {
		double deg = toDegrees(angle/15.0);
		double min = (deg-floor(deg))*60;
		double sec = (min-floor(min))*60;
		System.out.println(msg + ": \t" + angle + "\t" + ((int)deg) + ":" + ((int)min) + ":" + (sec));
	}
	public static Vector3d getEarthPos(double JD, double obsLon, double obsLat) {
		double t = Space.JD2000Millenium(JD);
		double lon = EarthPosition.getLon(t);
		double lat = EarthPosition.getLat(t);
		double rad = EarthPosition.getRad(t);
			lon = range2PI(lon);
		lat = rangePIPI(lat);
		double GAST = GMST(JD) + equationOfEquinoxes(JD);
		double[] azAlt = observer(JD, GAST,
				lon, lat,
				obsLon, obsLat);
		return sphericalToCartesianY(azAlt[0], azAlt[1], rad);
	}
	public static Vector3f getDirRad(double JD, double lon, double lat, double obsLon, double obsLat) {
		lon = range2PI(lon);
		lat = rangePIPI(lat);
		double GAST = GMST(JD) + equationOfEquinoxes(JD);
		double[] azAlt = observer(JD, GAST,
				lon, lat,
				obsLon, obsLat);
		return sphericalToCartesianY(azAlt[0], azAlt[1], 1.0).toVector3f();
	}
	public static Vector3f getDirRad(double JD, double lon, double lat, double rad, Vector3d earthPos, double obsLon, double obsLat) {
		lon = range2PI(lon);
		lat = rangePIPI(lat);
		double GAST = GMST(JD) + equationOfEquinoxes(JD);
		double[] azAlt = observer(JD, GAST,
				lon, lat,
				obsLon, obsLat);
		return sphericalToCartesianY(azAlt[0], azAlt[1], rad).subtractLocal(earthPos).toVector3f();
	}
}
