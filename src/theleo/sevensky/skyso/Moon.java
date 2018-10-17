package theleo.sevensky.skyso;

import static java.lang.Math.*;

public class Moon {
	public static double Moon_L(double t) {
		t *= 10;
		double ll = 3.8104 + 8399.7091*t;
		double m = 6.2300 + 628.3019*t;
		double mm = 2.3554 + 8328.6911*t;
		double d = 5.1985 + 7771.3772*t;
		double f = 1.6280 + 8433.4663*t;
		
		double d2 = d+d;
		
		return ll
				+0.1098*sin(mm)
				+0.0222*sin(d2-mm)
				+0.0115*sin(d2)
				+0.0037*sin(mm+mm)
				-0.0032*sin(m)
				-0.0020*sin(f+f)
				+0.0010*sin(d2-mm-mm)
				+0.0010*sin(d2-m-mm)
				+0.0009*sin(d2+mm)
				+0.0008*sin(d2-m)
				+0.0007*sin(mm-m)
				-0.0006*sin(d)
				-0.0005*sin(m+mm);
	}
	public static double Moon_B(double t) {
		t *= 10;
		double mm = 2.3554 + 8328.6911*t;
		double f = 1.6280 + 8433.4663*t;
		double d = 5.1985 + 7771.3772*t;
		double d2 = d+d;
		
		return +0.0895*sin(f)
				+0.0049*sin(mm+f)
				+0.0048*sin(mm-f)
				+0.0030*sin(d2-f)
				+0.0010*sin(d2+f-mm)
				+0.0008*sin(d2-f-mm)
				+0.0006*sin(d2+f);
	}
	public static double Moon_R(double t) {
		t *= 10;
		double d = 5.1985 + 7771.3772*t;
		double d2 = d+d;
		double m = 6.2300 + 628.3019*t;
		double mm = 2.3554 + 8328.6911*t;
		return 1.0/ ( +0.016593
				+0.000904*cos(mm)
				+0.000166*cos(d2-mm)
				+0.000137*cos(d2)
				+0.000049*cos(mm+mm)
				+0.000015*cos(d2+mm)
				+0.000009*cos(d2-m));
	}
}
