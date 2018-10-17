/*----------------------------------------------------------------------
**
**  Copyright (C) 2012
**  Standards Of Fundamental Astronomy Board
**  of the International Astronomical Union.
**
**  =====================
**  SOFA Software License
**  =====================
**
**  NOTICE TO USER:
**
**  BY USING THIS SOFTWARE YOU ACCEPT THE FOLLOWING SIX TERMS AND
**  CONDITIONS WHICH APPLY TO ITS USE.
**
**  1. The Software is owned by the IAU SOFA Board ("SOFA").
**
**  2. Permission is granted to anyone to use the SOFA software for any
**     purpose, including commercial applications, free of charge and
**     without payment of royalties, subject to the conditions and
**     restrictions listed below.
**
**  3. You (the user) may copy and distribute SOFA source code to others,
**     and use and adapt its code and algorithms in your own software,
**     on a world-wide, royalty-free basis.  That portion of your
**     distribution that does not consist of intact and unchanged copies
**     of SOFA source code files is a "derived work" that must comply
**     with the following requirements:
**
**     a) Your work shall be marked or carry a statement that it
**        (i) uses routines and computations derived by you from
**        software provided by SOFA under license to you; and
**        (ii) does not itself constitute software provided by and/or
**        endorsed by SOFA.
**
**     b) The source code of your derived work must contain descriptions
**        of how the derived work is based upon, contains and/or differs
**        from the original SOFA software.
**
**     c) The names of all routines in your derived work shall not
**        include the prefix "iau" or "sofa" or trivial modifications
**        thereof such as changes of case.
**
**     d) The origin of the SOFA components of your derived work must
**        not be misrepresented;  you must not claim that you wrote the
**        original software, nor file a patent application for SOFA
**        software or algorithms embedded in the SOFA software.
**
**     e) These requirements must be reproduced intact in any source
**        distribution and shall apply to anyone to whom you have
**        granted a further right to modify the source code of your
**        derived work.
**
**     Note that, as originally distributed, the SOFA software is
**     intended to be a definitive implementation of the IAU standards,
**     and consequently third-party modifications are discouraged.  All
**     variations, no matter how minor, must be explicitly marked as
**     such, as explained above.
**
**  4. You shall not cause the SOFA software to be brought into
**     disrepute, either by misuse, or use for inappropriate tasks, or
**     by inappropriate modification.
**
**  5. The SOFA software is provided "as is" and SOFA makes no warranty
**     as to its use or performance.   SOFA does not and cannot warrant
**     the performance or results which the user may obtain by using the
**     SOFA software.  SOFA makes no warranties, express or implied, as
**     to non-infringement of third party rights, merchantability, or
**     fitness for any particular purpose.  In no event will SOFA be
**     liable to the user for any consequential, incidental, or special
**     damages, including any lost profits or lost savings, even if a
**     SOFA representative has been advised of such damages, or for any
**     claim by any third party.
**
**  6. The provision of any version of the SOFA software under the terms
**     and conditions specified herein does not imply that future
**     versions will also be made available under the same terms and
**     conditions.
*
**  In any published work or commercial product which uses the SOFA
**  software directly, acknowledgement (see www.iausofa.org) is
**  appreciated.
**
**  Correspondence concerning SOFA software should be addressed as
**  follows:
**
**      By email:  sofa@ukho.gov.uk
**      By post:   IAU SOFA Center
**                 HM Nautical Almanac Office
**                 UK Hydrographic Office
**                 Admiralty Way, Taunton
**                 Somerset, TA1 2DN
**                 United Kingdom
**
**--------------------------------------------------------------------*/
package theleo.sevensky.skyso.nuta;

import static java.lang.Math.*;
import theleo.sevensky.core.AbstractNutationFunction;
import theleo.sevensky.core.Space;

public class NutationFunction implements AbstractNutationFunction {
	
	
	public static final double TWO_PI = Math.PI + Math.PI;

	@Override
	public double[] nutationInLongitudeObliquity(double date1) {
		return _nutationInLongitudeObliquity(date1);
	}

	@Override
	public double nutationOfObliquityOfTheEcliptic(double JDTT) {
		return _nutationOfObliquityOfTheEcliptic(JDTT);
	}

	
	
	
	
	public static double rangePIPI(double val) {
		return val - TWO_PI * floor((val + Math.PI) / TWO_PI);
	}

	public static double range2PI(double val) {
		return val - TWO_PI * floor((val) / TWO_PI);
	}
	
	public static double obliquityOfTheEcliptic(double JD) {
		double DJ0 = 2451545.0;
		double DJC = 36525.0;
		double DAS2R = 4.848136811095359935899141e-6;
		
		double t, eps0;
		/* Interval between fundamental epoch J2000.0 and given date (JC). */
		   t = ((JD - DJ0) /* + date2*/) / DJC;

		/* Mean obliquity of date. */
		   eps0 = DAS2R * (84381.448  +
						  (-46.8150   +
						  (-0.00059   +
						  ( 0.001813) * t) * t) * t);

		return eps0;
	}
	
	/** http://www.neoprogrammics.com/nutations/
	 * @param JDTT
	 * @return  */
	public static double _nutationOfObliquityOfTheEcliptic(double JDTT) {
		double T  = (JDTT - 2451545) / 36525;
		double T2 = T * T;
		double T3 = T * T2;

		double w1 = toRadians(297.85036 + 445267.11148*T  - 0.0019142*T2 + T3/189474);
		double w2 = toRadians(357.52772 + 35999.05034*T   - 0.0001603*T2 - T3/300000);
		double w3 = toRadians(134.96298 + 477198.867398*T + 0.0086972*T2 + T3/56250);
		double w4 = toRadians(93.27191  + 483202.017538*T - 0.0036825*T2 + T3/327270);
		double w5 = toRadians(125.04452 - 1934.136261*T   + 0.0020708*T2 + T3/450000);

		double w = cos(w5)*(92025 + 8.9*T);
		w = w + cos(2*(w4 - w1 + w5))*(5736 - 3.1*T);
		w = w + cos(2*(w4 + w5))*(977 - 0.5*T);
		w = w + cos(2*w5)*(0.5*T - 895);
		w = w + cos(w2)*(54 - 0.1*T);
		w = w + cos(w2 + 2*(w4 - w1 + w5))*(224 - 0.6*T);
		w = w + cos(w3 + 2*(w4 + w5))*(129 - 0.1*T);
		w = w + cos(2*(w4 - w1 + w5) - w2)*(0.3*T - 95);
		w = w + 200*cos(2*w4 + w5);
		w = w - 70*cos(2*(w4 - w1) + w5);
		w = w - 53*cos(2*(w4 + w5) - w3);
		w = w - 33*cos(w3 + w5);
		w = w + 26*cos(2*(w1 + w4 + w5) - w3);
		w = w + 32*cos(w5 - w3);
		w = w + 27*cos(w3 + 2*w4 + w5);
		w = w - 24*cos(2*(w4 - w3) + w5);
		w = w + 16*cos(2*(w1 + w4 + w5));
		w = w + 13*cos(2*(w3 + w4 + w5));
		w = w - 12*cos(w3 + 2*(w4 - w1 + w5));
		w = w - 10*cos(2*w4 + w5 - w3);
		w = w - 8*cos(2*w1 - w3 + w5);
		w = w + 7*cos(2*(w2 - w1 + w4 + w5));
		w = w - 7*cos(w3);
		w = w + 9*cos(w2 + w5);
		w = w + 7*cos(w3 + w5 - 2*w1);
		w = w + 6*cos(w5 - w2);
		w = w + 5*cos(2*(w1 + w4) - w3 + w5);
		w = w + 3*cos(w3 + 2*(w4 + w1 + w5));
		w = w - 3*cos(w2 + 2*(w4 + w5));
		w = w + 3*cos(2*(w4 + w5) - w2);
		w = w + 3*cos(2*(w1 + w4) + w5);
		w = w - 3*cos(2*(w3 + w4 + w5 - w1));
		w = w - 3*cos(w3 + 2*(w4 - w1) + w5);
		w = w + 3*cos(2*(w1 - w3) + w5);
		w = w + 3*cos(2*w1 + w5);
		w = w + 3*cos(2*(w4 - w1) + w5 - w2);
		w = w + 3*cos(w5 - 2*w1);
		w = w + 3*cos(2*(w3 + w4) + w5);

		return toRadians(w / 36000000);
	}
	public static double[] _nutationInLongitudeObliquity(double date1) {
		/*
**  - - - - - - - - -
**   i a u N u t 8 0
**  - - - - - - - - -
**
**  Nutation, IAU 1980 model.
**
**  This function is part of the International Astronomical Union's
**  SOFA (Standards Of Fundamental Astronomy) software collection.
**
**  Status:  canonical model.
**
**  Given:
**     date1,date2   double    TT as a 2-part Julian Date (Note 1)
**
**  Returned:
**     dpsi          double    nutation in longitude (radians)
**     deps          double    nutation in obliquity (radians)
**
**  Notes:
**
**  1) The TT date date1+date2 is a Julian Date, apportioned in any
**     convenient way between the two arguments.  For example,
**     JD(TT)=2450123.7 could be expressed in any of these ways,
**     among others:
**
**            date1          date2
**
**         2450123.7           0.0       (JD method)
**         2451545.0       -1421.3       (J2000 method)
**         2400000.5       50123.2       (MJD method)
**         2450123.5           0.2       (date & time method)
**
**     The JD method is the most natural and convenient to use in
**     cases where the loss of several decimal digits of resolution
**     is acceptable.  The J2000 method is best matched to the way
**     the argument is handled internally and will deliver the
**     optimum resolution.  The MJD method and the date & time methods
**     are both good compromises between resolution and convenience.
**
**  2) The nutation components are with respect to the ecliptic of
**     date.
**
**  Called:
**     iauAnpm      normalize angle into range +/- pi
**
**  Reference:
**
**     Explanatory Supplement to the Astronomical Almanac,
**     P. Kenneth Seidelmann (ed), University Science Books (1992),
**     Section 3.222 (p111).
**
**  This revision:  2008 September 30
**
**  SOFA release 2012-03-01
**
**  Copyright (C) 2012 IAU SOFA Board.  See notes at end.
		 */
		double t, el, elp, f, d, om, dp, de, arg, s, c;
		int j;

		/* Units of 0.1 milliarcsecond to radians */
		double DAS2R = 4.848136811095359935899141e-6;
		double U2R = DAS2R / 1e4;

		/* ------------------------------------------------ */
 /* Table of multiples of arguments and coefficients */
 /* ------------------------------------------------ */

 /* The units for the sine and cosine coefficients are 0.1 mas and */
 /* the same per Julian century */
//   static const struct {
//      int nl,nlp,nf,nd,nom; /* coefficients of l,l',F,D,Om */
//      double sp,spt;        /* longitude sine, 1 and t coefficients */
//      double ce,cet;        /* obliquity cosine, 1 and t coefficients */
//   } x[] = {
		double[][] x = {
			/* 1-10 */
			{0, 0, 0, 0, 1, -171996.0, -174.2, 92025.0, 8.9},
			{0, 0, 0, 0, 2, 2062.0, 0.2, -895.0, 0.5},
			{-2, 0, 2, 0, 1, 46.0, 0.0, -24.0, 0.0},
			{2, 0, -2, 0, 0, 11.0, 0.0, 0.0, 0.0},
			{-2, 0, 2, 0, 2, -3.0, 0.0, 1.0, 0.0},
			{1, -1, 0, -1, 0, -3.0, 0.0, 0.0, 0.0},
			{0, -2, 2, -2, 1, -2.0, 0.0, 1.0, 0.0},
			{2, 0, -2, 0, 1, 1.0, 0.0, 0.0, 0.0},
			{0, 0, 2, -2, 2, -13187.0, -1.6, 5736.0, -3.1},
			{0, 1, 0, 0, 0, 1426.0, -3.4, 54.0, -0.1},
			/* 11-20 */
			{0, 1, 2, -2, 2, -517.0, 1.2, 224.0, -0.6},
			{0, -1, 2, -2, 2, 217.0, -0.5, -95.0, 0.3},
			{0, 0, 2, -2, 1, 129.0, 0.1, -70.0, 0.0},
			{2, 0, 0, -2, 0, 48.0, 0.0, 1.0, 0.0},
			{0, 0, 2, -2, 0, -22.0, 0.0, 0.0, 0.0},
			{0, 2, 0, 0, 0, 17.0, -0.1, 0.0, 0.0},
			{0, 1, 0, 0, 1, -15.0, 0.0, 9.0, 0.0},
			{0, 2, 2, -2, 2, -16.0, 0.1, 7.0, 0.0},
			{0, -1, 0, 0, 1, -12.0, 0.0, 6.0, 0.0},
			{-2, 0, 0, 2, 1, -6.0, 0.0, 3.0, 0.0},
			/* 21-30 */
			{0, -1, 2, -2, 1, -5.0, 0.0, 3.0, 0.0},
			{2, 0, 0, -2, 1, 4.0, 0.0, -2.0, 0.0},
			{0, 1, 2, -2, 1, 4.0, 0.0, -2.0, 0.0},
			{1, 0, 0, -1, 0, -4.0, 0.0, 0.0, 0.0},
			{2, 1, 0, -2, 0, 1.0, 0.0, 0.0, 0.0},
			{0, 0, -2, 2, 1, 1.0, 0.0, 0.0, 0.0},
			{0, 1, -2, 2, 0, -1.0, 0.0, 0.0, 0.0},
			{0, 1, 0, 0, 2, 1.0, 0.0, 0.0, 0.0},
			{-1, 0, 0, 1, 1, 1.0, 0.0, 0.0, 0.0},
			{0, 1, 2, -2, 0, -1.0, 0.0, 0.0, 0.0},
			/* 31-40 */
			{0, 0, 2, 0, 2, -2274.0, -0.2, 977.0, -0.5},
			{1, 0, 0, 0, 0, 712.0, 0.1, -7.0, 0.0},
			{0, 0, 2, 0, 1, -386.0, -0.4, 200.0, 0.0},
			{1, 0, 2, 0, 2, -301.0, 0.0, 129.0, -0.1},
			{1, 0, 0, -2, 0, -158.0, 0.0, -1.0, 0.0},
			{-1, 0, 2, 0, 2, 123.0, 0.0, -53.0, 0.0},
			{0, 0, 0, 2, 0, 63.0, 0.0, -2.0, 0.0},
			{1, 0, 0, 0, 1, 63.0, 0.1, -33.0, 0.0},
			{-1, 0, 0, 0, 1, -58.0, -0.1, 32.0, 0.0},
			{-1, 0, 2, 2, 2, -59.0, 0.0, 26.0, 0.0},
			/* 41-50 */
			{1, 0, 2, 0, 1, -51.0, 0.0, 27.0, 0.0},
			{0, 0, 2, 2, 2, -38.0, 0.0, 16.0, 0.0},
			{2, 0, 0, 0, 0, 29.0, 0.0, -1.0, 0.0},
			{1, 0, 2, -2, 2, 29.0, 0.0, -12.0, 0.0},
			{2, 0, 2, 0, 2, -31.0, 0.0, 13.0, 0.0},
			{0, 0, 2, 0, 0, 26.0, 0.0, -1.0, 0.0},
			{-1, 0, 2, 0, 1, 21.0, 0.0, -10.0, 0.0},
			{-1, 0, 0, 2, 1, 16.0, 0.0, -8.0, 0.0},
			{1, 0, 0, -2, 1, -13.0, 0.0, 7.0, 0.0},
			{-1, 0, 2, 2, 1, -10.0, 0.0, 5.0, 0.0},
			/* 51-60 */
			{1, 1, 0, -2, 0, -7.0, 0.0, 0.0, 0.0},
			{0, 1, 2, 0, 2, 7.0, 0.0, -3.0, 0.0},
			{0, -1, 2, 0, 2, -7.0, 0.0, 3.0, 0.0},
			{1, 0, 2, 2, 2, -8.0, 0.0, 3.0, 0.0},
			{1, 0, 0, 2, 0, 6.0, 0.0, 0.0, 0.0},
			{2, 0, 2, -2, 2, 6.0, 0.0, -3.0, 0.0},
			{0, 0, 0, 2, 1, -6.0, 0.0, 3.0, 0.0},
			{0, 0, 2, 2, 1, -7.0, 0.0, 3.0, 0.0},
			{1, 0, 2, -2, 1, 6.0, 0.0, -3.0, 0.0},
			{0, 0, 0, -2, 1, -5.0, 0.0, 3.0, 0.0},
			/* 61-70 */
			{1, -1, 0, 0, 0, 5.0, 0.0, 0.0, 0.0},
			{2, 0, 2, 0, 1, -5.0, 0.0, 3.0, 0.0},
			{0, 1, 0, -2, 0, -4.0, 0.0, 0.0, 0.0},
			{1, 0, -2, 0, 0, 4.0, 0.0, 0.0, 0.0},
			{0, 0, 0, 1, 0, -4.0, 0.0, 0.0, 0.0},
			{1, 1, 0, 0, 0, -3.0, 0.0, 0.0, 0.0},
			{1, 0, 2, 0, 0, 3.0, 0.0, 0.0, 0.0},
			{1, -1, 2, 0, 2, -3.0, 0.0, 1.0, 0.0},
			{-1, -1, 2, 2, 2, -3.0, 0.0, 1.0, 0.0},
			{-2, 0, 0, 0, 1, -2.0, 0.0, 1.0, 0.0},
			/* 71-80 */
			{3, 0, 2, 0, 2, -3.0, 0.0, 1.0, 0.0},
			{0, -1, 2, 2, 2, -3.0, 0.0, 1.0, 0.0},
			{1, 1, 2, 0, 2, 2.0, 0.0, -1.0, 0.0},
			{-1, 0, 2, -2, 1, -2.0, 0.0, 1.0, 0.0},
			{2, 0, 0, 0, 1, 2.0, 0.0, -1.0, 0.0},
			{1, 0, 0, 0, 2, -2.0, 0.0, 1.0, 0.0},
			{3, 0, 0, 0, 0, 2.0, 0.0, 0.0, 0.0},
			{0, 0, 2, 1, 2, 2.0, 0.0, -1.0, 0.0},
			{-1, 0, 0, 0, 2, 1.0, 0.0, -1.0, 0.0},
			{1, 0, 0, -4, 0, -1.0, 0.0, 0.0, 0.0},
			/* 81-90 */
			{-2, 0, 2, 2, 2, 1.0, 0.0, -1.0, 0.0},
			{-1, 0, 2, 4, 2, -2.0, 0.0, 1.0, 0.0},
			{2, 0, 0, -4, 0, -1.0, 0.0, 0.0, 0.0},
			{1, 1, 2, -2, 2, 1.0, 0.0, -1.0, 0.0},
			{1, 0, 2, 2, 1, -1.0, 0.0, 1.0, 0.0},
			{-2, 0, 2, 4, 2, -1.0, 0.0, 1.0, 0.0},
			{-1, 0, 4, 0, 2, 1.0, 0.0, 0.0, 0.0},
			{1, -1, 0, -2, 0, 1.0, 0.0, 0.0, 0.0},
			{2, 0, 2, -2, 1, 1.0, 0.0, -1.0, 0.0},
			{2, 0, 2, 2, 2, -1.0, 0.0, 0.0, 0.0},
			/* 91-100 */
			{1, 0, 0, 2, 1, -1.0, 0.0, 0.0, 0.0},
			{0, 0, 4, -2, 2, 1.0, 0.0, 0.0, 0.0},
			{3, 0, 2, -2, 2, 1.0, 0.0, 0.0, 0.0},
			{1, 0, 2, -2, 0, -1.0, 0.0, 0.0, 0.0},
			{0, 1, 2, 0, 1, 1.0, 0.0, 0.0, 0.0},
			{-1, -1, 0, 2, 1, 1.0, 0.0, 0.0, 0.0},
			{0, 0, -2, 0, 1, -1.0, 0.0, 0.0, 0.0},
			{0, 0, 2, -1, 2, -1.0, 0.0, 0.0, 0.0},
			{0, 1, 0, 2, 0, -1.0, 0.0, 0.0, 0.0},
			{1, 0, -2, -2, 0, -1.0, 0.0, 0.0, 0.0},
			/* 101-106 */
			{0, -1, 2, 0, 1, -1.0, 0.0, 0.0, 0.0},
			{1, 1, 0, -2, 1, -1.0, 0.0, 0.0, 0.0},
			{1, 0, -2, 2, 0, -1.0, 0.0, 0.0, 0.0},
			{2, 0, 0, 2, 0, 1.0, 0.0, 0.0, 0.0},
			{0, 0, 2, 4, 2, -1.0, 0.0, 0.0, 0.0},
			{0, 1, 0, 1, 0, 1.0, 0.0, 0.0, 0.0}
		};

		/* Number of terms in the series */
		int NT = x.length;// (int) (sizeof x / sizeof x[0]);

		/*--------------------------------------------------------------------*/
		double DJ0 = 2451545.0;
		double DJC = 36525.0;
		/* Interval between fundamental epoch J2000.0 and given date (JC). */
		t = ((date1 - DJ0) /*+ date2*/) / DJC;

		/* --------------------- */
 /* Fundamental arguments */
 /* --------------------- */

 /* Mean longitude of Moon minus mean longitude of Moon's perigee. */
		el = rangePIPI(
				(485866.733 + (715922.633 + (31.310 + 0.064 * t) * t) * t)
				* DAS2R + (1325.0 * t % 1.0) * TWO_PI);

		/* Mean longitude of Sun minus mean longitude of Sun's perigee. */
		elp = rangePIPI(
				(1287099.804 + (1292581.224 + (-0.577 - 0.012 * t) * t) * t)
				* DAS2R + (99.0 * t % 1.0) * TWO_PI);

		/* Mean longitude of Moon minus mean longitude of Moon's node. */
		f = rangePIPI(
				(335778.877 + (295263.137 + (-13.257 + 0.011 * t) * t) * t)
				* DAS2R + (1342.0 * t % 1.0) * TWO_PI);

		/* Mean elongation of Moon from Sun. */
		d = rangePIPI(
				(1072261.307 + (1105601.328 + (-6.891 + 0.019 * t) * t) * t)
				* DAS2R + (1236.0 * t % 1.0) * TWO_PI);

		/* Longitude of the mean ascending node of the lunar orbit on the */
 /* ecliptic, measured from the mean equinox of date. */
		om = rangePIPI(
				(450160.280 + (-482890.539 + (7.455 + 0.008 * t) * t) * t)
				* DAS2R + (-5.0 * t % 1.0) * TWO_PI);

		/* --------------- */
 /* Nutation series */
 /* --------------- */

 /* Initialize nutation components. */
		dp = 0.0;
		de = 0.0;

		/* Sum the nutation terms, ending with the biggest. */
		for (j = NT - 1; j >= 0; j--) {
			
//			int nl,nlp,nf,nd,nom; /* coefficients of l,l',F,D,Om */
////      double sp,spt;        /* longitude sine, 1 and t coefficients */
////      double ce,cet;  

			/* Form argument for current term. */
//			arg = (double) x[j].nl * el
//					+ (double) x[j].nlp * elp
//					+ (double) x[j].nf * f
//					+ (double) x[j].nd * d
//					+ (double) x[j].nom * om;
			arg = (double) x[j][0] * el
					+ (double) x[j][1] * elp
					+ (double) x[j][2] * f
					+ (double) x[j][3] * d
					+ (double) x[j][4] * om;

			/* Accumulate current nutation term. */
//			s = x[j].sp + x[j].spt * t;
//			c = x[j].ce + x[j].cet * t;
			s = x[j][5] + x[j][6] * t;
			c = x[j][7] + x[j][8] * t;
			if (s != 0.0) {
				dp += s * sin(arg);
			}
			if (c != 0.0) {
				de += c * cos(arg);
			}
		}

		/* Convert results from 0.1 mas units to radians. */
		// *dpsi = dp * U2R;
		//*deps = de * U2R;
		return new double[]{dp * U2R, de * U2R};
	}
}
