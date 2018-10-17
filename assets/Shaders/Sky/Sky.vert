uniform mat4 g_WorldViewProjectionMatrix;
attribute vec3 inPosition;
attribute vec3 inNormal;

varying vec3 oF_ex;
varying vec3 oL_in;

uniform vec3 m_sundir;
uniform float m_Ghg;
uniform vec3 m_scatterRay; //B_sc ray
uniform vec3 m_scatterMie; //B_sc mie

#define B_ex (m_scatterRay+m_scatterMie)
#define PI 3.14159265358979323846



float phase(float theta) {
	float ct = cos(theta);
	return 3.0/(16.0*PI) * (1.0 + ct*ct );
}
float mieHG(float theta) {
	return (1.0-m_Ghg)*(1.0-m_Ghg) / (4.0*PI * pow(1.0+m_Ghg*m_Ghg - 2.0*m_Ghg*cos(theta), 3.0/2.0));
}
vec3 B_sc(float theta) {
	return m_scatterRay*phase(theta); // + m_scatterMie*mieHG(theta);
}
vec3 F_ex(float s) {
	return exp(-B_ex*s);
}
vec3 L_in(float s, float theta) {
	vec3 E_sun0 = vec3(200.0);
	vec3 E_sun = E_sun0*F_ex(s);
	return 1.0/B_ex * E_sun * B_sc(theta) * (1.0 - exp(-B_ex*s));
}


vec3 L(float s, float theta) {
	vec3 L_0 = vec3(0.0);
	return L_0 * F_ex(s) + L_in(s, theta);
}

bool raySkyIntersect(vec3 pos, vec3 dir, float rad, out vec2 res) {
	float b = 2.0 * dot(pos, dir);
	float c = dot(pos, pos) - rad*rad;

	float disc = b*b - 4.0 * c; //*a(1)
	if(disc < 0.0) false;
	float q = -0.5 * (b + sign(b)*sqrt(disc));
	float t1 = q; //  /a(1);
	float t2 = c / q;
	res = vec2(min(t1,t2), max(t1,t2));
	return true;
}
/*bool raySkyIntersect(float posy, vec3 dir, float rad, out vec2 res) {
	float b = 2.0 * posy * dir.y;
	float c = posy*posy - rad*rad;
	float disc = b*b - 4.0 * c; //*a(1)
	if(disc < 0.0) false;
	float q = -0.5 * (b + sign(b)*sqrt(disc));
	float t1 = q; // / a(1);
	float t2 = c / q;
	res = vec2(min(t1,t2), max(t1,t2));
	return true;
}*/

#define EARTH_RAD 6360.0e3
#define ATMOS_RAD 6420.0e3

vec3 SkyColor(vec3 orig, vec3 dir, float tmin, float tmax) {
	vec2 res;
	if (!raySkyIntersect(orig, dir, ATMOS_RAD, res) || res.y < 0) return vec3(0.0);
	//if (!raySphereIntersect(orig, dir, atmosphereRadius, t0, t1) || t1 < 0) return 0;
	if (res.x > tmin && res.x > 0) tmin = res.x;
	if (res.y < tmax) tmax = res.y;
	
	int numSamples = 16;
	int numSamplesLight = 8;

	//int numSamples = 4;
	//int numSamplesLight = 2;

	float Hr = 7994.0;
	float Hm = 1200.0;

	float segmentLength = (tmax - tmin) / numSamples;
	float tCurrent = tmin;
	vec3 sumR = vec3(0.0);
	vec3 sumM = vec3(0.0); // mie and rayleigh contribution
	float opticalDepthR = 0.0, opticalDepthM = 0.0;
	float mu = dot(dir, m_sundir); // mu in the paper which is the cosine of the angle between the sun direction and the ray direction
	float phaseR = 3.0 / (16.0 * PI) * (1.0 + mu * mu);
	float g = 0.76;
	//float phaseM = (3.0 / (8.0 * PI)) * ((1.0 - g * g) * (1.0 + mu * mu)) / ((2.0 + g * g) * pow(1.0 + g * g - 2.0 * g * mu, 1.5));
	float phaseM = (3.0/(8.0*PI))*((1.0-g*g) 
		*(1.0+mu*mu))/((2.0+g*g)*pow(1.0+g*g-2.0*g*mu, 1.5f));

	for (int i = 0; i < numSamples; ++i) {
		vec3 samplePosition = orig + (tCurrent + segmentLength * 0.5f) * dir;
		float height = length(samplePosition) - EARTH_RAD;
		// compute optical depth for light
		float hr = exp(-height / Hr) * segmentLength;
		float hm = exp(-height / Hm) * segmentLength;
		opticalDepthR += hr;
		opticalDepthM += hm;

		// light optical depth
		//float t0Light, t1Light;
		raySkyIntersect(samplePosition, m_sundir, ATMOS_RAD, res);
		//raySphereIntersect(samplePosition, sunDirection, atmosphereRadius, t0Light, t1Light);


		float segmentLengthLight = res.x / numSamplesLight;
		float tCurrentLight = 0.0;
		float opticalDepthLightR = 0.0;
		float opticalDepthLightM = 0.0;
		int j;
		for (j = 0; j < numSamplesLight; ++j) {
			vec3 samplePositionLight = samplePosition + (tCurrentLight + segmentLengthLight * 0.5f) * -m_sundir;
			float heightLight = length(samplePositionLight) - EARTH_RAD;
			if (heightLight < 0.0) break;
			opticalDepthLightR += exp(-heightLight / Hr) * segmentLengthLight;
			opticalDepthLightM += exp(-heightLight / Hm) * segmentLengthLight;
			tCurrentLight += segmentLengthLight;
		}
		if (j == numSamplesLight) {
			vec3 tau = m_scatterRay * (opticalDepthR + opticalDepthLightR) + m_scatterMie * 1.1f * (opticalDepthM + opticalDepthLightM);
			vec3 attenuation = exp(-tau);
			sumR += attenuation * hr;
			sumM += attenuation * hm;
		}
		tCurrent += segmentLength;
	}
	return (sumR * m_scatterRay * phaseR + sumM * m_scatterMie * phaseM) * 20.0;
} 

void main() {
	/*vec2 res;
	raySkyIntersect(EARTH_RAD, inNormal, ATMOS_RAD, res);

	float H_R = 8.0e3;

	float s = res.y;
	float cu = dot(m_sundir, inNormal);
	float phaseR = 3.0/(16.0*PI)*(1.0+cu*cu); 
	float g = 0.76f;
	float phaseM = 3.0/(8.0*PI)*((1.0-g*g) 
		*(1.0+cu*cu))/((2.0+g*g)*pow(1.0+g*g-2.0*g*cu, 1.5f));
	

	float h = inNormal.y*s*0.5;
	vec3 B_sc = m_scatterRay*exp(-h/H_R)*phaseR ;//+ m_scatterMie*phaseM;

	vec3 E_sun0 = vec3(200.0);
	vec3 E_sun = E_sun0*F_ex(s);
	oL_in = 1.0/B_ex * E_sun * B_sc * (1.0 - exp(-B_ex*s));
	*/
	vec3 s = SkyColor(vec3(0.0,EARTH_RAD,0.0), inNormal, 0.0, 100000000000000.0);
	
	//s.x = s.x < 1.413f ? pow(s.x * 0.38317, 1.0 / 2.2) : (1.0 - exp(-s.x));
	//s.y = s.y < 1.413f ? pow(s.y * 0.38317, 1.0 / 2.2) : (1.0 - exp(-s.y));
	//s.z = s.z < 1.413f ? pow(s.z * 0.38317, 1.0 / 2.2) : (1.0 - exp(-s.z)); 

	oL_in = s;

	oF_ex = vec3(0.0);//F_ex(s);
	//oL_in = L_in(s, acos(dot(m_sundir, inNormal)));
	//oL_in = inNormal;

	vec4 pos = g_WorldViewProjectionMatrix*vec4(inPosition, 1.0);
	//pos.w = 1.0;
    gl_Position = pos;
}