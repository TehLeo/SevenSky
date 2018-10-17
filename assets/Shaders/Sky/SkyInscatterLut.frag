#define PI 3.1415926535897932384626433832795

uniform float m_PLANET_RAD;
uniform float m_PLANET_ATMOS_RAD;

uniform vec3 m_SCATTER_RAY;
uniform vec3 m_SCATTER_MIE;

uniform float m_RAYLEIGH_SCALE_HEIGHT;
uniform float m_MIE_SCALE_HEIGHT;

uniform sampler2D m_TransmittanceLut;
uniform sampler2D m_PathLengthLut;

uniform float m_Altitude;
#ifdef SUN
uniform vec3 m_SunDir;
uniform float m_SUN_MIE_G;
uniform float m_SUN_INTENSITY;
#endif

#ifdef MOON
uniform vec3 m_MoonPos;
uniform float m_MOON_MIE_G;
uniform float m_MOON_INTENSITY;
#endif

#ifdef VIEW
noperspective in vec3 pos;
#else
noperspective in vec2 fpPos;
#endif

vec3 lutT(float alt, float mu) {
	//return texture(m_TransmittanceLut, vec2((mu+0.25f)/1.25f, alt));
	//return texture(m_TransmittanceLut, vec2((mu+0.25f)*0.8, pow(alt, 0.25))).rgb;
	return texture(m_TransmittanceLut, vec2(((sign(mu)*pow(abs(mu),0.333333333333333))+0.6)*0.625, pow(alt, 0.25))).rgb;
}
float lutP(float alt, float mu) {
	return texture(m_PathLengthLut, vec2((sign(mu)*pow(abs(mu),0.333333333333333))*0.5+0.5, pow(alt, 0.25))).r;
	//return texture(m_PathLengthLut, vec2(mu*0.5+0.5, pow(alt, 0.25))).r;
}
float sign2(float x) {
	return step(0.0, x)*2.0 - 1.0;
}
bool calcForSphere(vec3 rayPosR, vec3 rayDir, float rad, out vec2 res) {
    float b = 2.0 * dot(rayDir, rayPosR);
    float c = dot(rayPosR, rayPosR) - rad*rad;
    float disc = b * b - 4.0 * c;
	
    if (disc < 0.0) return false;

	float q = (-b + sign2(b)*sqrt(disc))*0.5;  
	if(q == 0) return false;
	c /= q;

	float t0 = min(q, c); 
    float t1 = max(q, c);   

	//if(t1 < 0.0) return false;
	
	res = vec2(t0, t1);   

	return true;
}

/*bool isNaN(vec3 res) {
	return isinf(res.x) || isinf(res.y) || isinf(res.z) || 
		   isnan(res.x) || isnan(res.y) || isnan(res.z);
}*/

//#define iSteps 16
//#define jSteps 8
/*
vec2 rsi(vec3 r0, vec3 rd, float sr) {
    // ray-sphere intersection that assumes
    // the sphere is centered at the origin.
    // No intersection when result.x > result.y
    float a = dot(rd, rd);
    float b = 2.0 * dot(rd, r0);
    float c = dot(r0, r0) - (sr * sr);
    float d = (b*b) - 4.0*a*c;
    if (d < 0.0) return vec2(1e5,-1e5);
    return vec2(
        (-b - sqrt(d))/(2.0*a),
        (-b + sqrt(d))/(2.0*a)
    );
}*/

float ATM_SIZE_INV = 1.0/(m_PLANET_ATMOS_RAD-m_PLANET_RAD);


vec3 computeInscatter(vec3 dir, float g, vec3 L) {
	vec3 origin = vec3(0.0, m_PLANET_RAD+m_Altitude, 0.0);

/*	vec2 t;
	if(!calcForSphere(origin, dir, m_PLANET_ATMOS_RAD, t)) return vec4(0.0,0.0,0.0,1.0);
	t.x = max(t.x, 0.0);
	vec2 t2;
	if(calcForSphere(origin, dir, m_PLANET_RAD, t2)) {
		if(t2.x > 0) {
			t.y = t2.x;
		}
		else if(t2.y > 0) {
			t.y = t2.y;
		}
	}
*/

	float pathLength = lutP(m_Altitude*ATM_SIZE_INV, dir.y);
	
	//float T = 0.04038721 + (0.9976623 - 0.04038721)/(1.0 + pow(dir.y/0.1717867,1.340516));
	//float T = dir.y;

	//if(1 == 1) return vec4(pathLength2-pathLength);
	
	//float pathLength = t.y - t.x;
	//int numSamples = 4;
	int numSamples = int(4.0*(1.0 + step(512.0, pathLength) + step(1024.0, pathLength)
					   + step(2048.0, pathLength)));
	//int numSamples = 16;

	//int numSamples = 16;
	//float segmentLength = ( t.y - t.x ) / numSamples;
	float segmentLength = pathLength / float(numSamples);
	//float tCurrent = t.x;
	float tCurrent = 0.0;

	vec3 sumR = vec3( 0.0 );
	vec3 sumM = vec3( 0.0 );

	float opticalDepthR = 0.0;
	float opticalDepthM = 0.0;

	float mu = dot( dir, L );
	//float phaseR = (3.0 / ( 16.0 * PI ) * ( 1.0 + mu * mu ));
	float phaseR = 0.059683104 * ( 1.0 + mu * mu );
	//float phaseM = (3.0 / (  8.0 * PI ) * ( ( 1.0 - g * g ) * ( 1.0 + mu * mu ) ) / ( ( 2.0 + g * g ) * pow( 1.0 + g * g - 2.0 * g * mu, 1.5 ) ) );
	float phaseM = g*g; phaseM = (0.119366207 * ( ( 1.0 - phaseM ) * ( 1.0 + mu * mu ) ) / ( ( 2.0 + phaseM ) * pow( 1.0 + phaseM - 2.0 * g * mu, 1.5 ) ) );
	

	/*float mu = dot(dir, L);
    float mumu = mu * mu;
    float gg = g * g;
    float phaseR = 3.0 / (16.0 * PI) * (1.0 + mumu);
    float phaseM = 3.0 / (8.0 * PI) * ((1.0 - gg) * (mumu + 1.0)) / (pow(1.0 + gg - 2.0 * mu * g, 1.5) * (2.0 + gg));
	*/
	for( int i = 0; i < numSamples ; i++ ) {
		vec3 samplePosition = origin + dir * ( tCurrent + 0.5 * segmentLength );
		float height = length(samplePosition) - m_PLANET_RAD;

		float hr = exp( -height / m_RAYLEIGH_SCALE_HEIGHT ) * segmentLength;
		float hm = exp( -height / m_MIE_SCALE_HEIGHT      ) * segmentLength;

		opticalDepthR += hr;
		opticalDepthM += hm;

		float ViewDZenith = dot(normalize(samplePosition),L);
		float altitude = height * ATM_SIZE_INV;

		vec3 lookupAtten = lutT(altitude, ViewDZenith);
		vec3 tau3 = m_SCATTER_RAY * opticalDepthR + m_SCATTER_MIE * opticalDepthM;
		vec3 attenuation3 = exp( -tau3 ) * lookupAtten.rgb;
		sumR += attenuation3*hr;
		sumM += attenuation3*hm;

		tCurrent += segmentLength;
	}
	//return vec3(pathLength*0.000001);
	//return vec4(vec3(opticalDepthR), 1.0);
	//return vec4(vec3(pathLength*0.0001), 1.0);
	//return vec4(vec3(exp(-segmentLength*0.01)), 1.0);

	//return vec4(phaseR*m_SCATTER_RAY*1000000.0, 1.0);
	//Vector3f T = exp((m_SCATTER_RAY.mult(opticalDepthR).add(m_SCATTER_MIE.mult(opticalDepthM))).negate());
	return phaseR*sumR*m_SCATTER_RAY + phaseM*sumM*m_SCATTER_MIE;
}

vec3 computeInscatterPos(vec3 dir, float g, vec3 Lpos) {
	//vec3 origin = vec3(0.0, m_PLANET_RAD+m_Altitude, 0.0);

/*	vec2 t;
	if(!calcForSphere(origin, dir, m_PLANET_ATMOS_RAD, t)) return vec4(0.0,0.0,0.0,1.0);
	t.x = max(t.x, 0.0);
	vec2 t2;
	if(calcForSphere(origin, dir, m_PLANET_RAD, t2)) {
		if(t2.x > 0) {
			t.y = t2.x;
		}
		else if(t2.y > 0) {
			t.y = t2.y;
		}
	}
*/

	float pathLength = lutP(m_Altitude*ATM_SIZE_INV, dir.y);
	
	//float T = 0.04038721 + (0.9976623 - 0.04038721)/(1.0 + pow(dir.y/0.1717867,1.340516));
	//float T = dir.y;

	//if(1 == 1) return vec4(pathLength2-pathLength);
	
	//float pathLength = t.y - t.x;
	//int numSamples = 4;
	int numSamples = int(4.0*(1.0 + step(512.0, pathLength) + step(1024.0, pathLength)
					   + step(2048.0, pathLength)));
	//int numSamples = 16;

	//int numSamples = 16;
	//float segmentLength = ( t.y - t.x ) / numSamples;
	float segmentLength = pathLength / float(numSamples);
	//float tCurrent = t.x;
	float tCurrent = 0.0;

	vec3 sumR = vec3( 0.0 );
	vec3 sumM = vec3( 0.0 );

	float opticalDepthR = 0.0;
	float opticalDepthM = 0.0;

	/*float mu = dot(dir, L);
    float mumu = mu * mu;
    float gg = g * g;
    float phaseR = 3.0 / (16.0 * PI) * (1.0 + mumu);
    float phaseM = 3.0 / (8.0 * PI) * ((1.0 - gg) * (mumu + 1.0)) / (pow(1.0 + gg - 2.0 * mu * g, 1.5) * (2.0 + gg));
	*/
	for( int i = 0; i < numSamples ; i++ ) {
		vec3 samplePosition = dir * ( tCurrent + 0.5 * segmentLength );
		samplePosition.y += m_Altitude;
		vec3 L = normalize(Lpos-samplePosition);
		samplePosition.y += m_PLANET_RAD;
		float height = length(samplePosition) - m_PLANET_RAD;

		float hr = exp( -height / m_RAYLEIGH_SCALE_HEIGHT ) * segmentLength;
		float hm = exp( -height / m_MIE_SCALE_HEIGHT      ) * segmentLength;

		opticalDepthR += hr;
		opticalDepthM += hm;

		float mu = dot( dir, L );
		//float phaseR = (3.0 / ( 16.0 * PI ) * ( 1.0 + mu * mu ));
		float phaseR = 0.059683104 * ( 1.0 + mu * mu );
		//float phaseM = (3.0 / (  8.0 * PI ) * ( ( 1.0 - g * g ) * ( 1.0 + mu * mu ) ) / ( ( 2.0 + g * g ) * pow( 1.0 + g * g - 2.0 * g * mu, 1.5 ) ) );
		float phaseM = g*g; phaseM = (0.119366207 * ( ( 1.0 - phaseM ) * ( 1.0 + mu * mu ) ) / ( ( 2.0 + phaseM ) * pow( 1.0 + phaseM - 2.0 * g * mu, 1.5 ) ) );
	

		float ViewDZenith = dot(normalize(samplePosition),L);
		float altitude = height * ATM_SIZE_INV;

		vec3 lookupAtten = lutT(altitude, ViewDZenith);
		vec3 tau3 = m_SCATTER_RAY * opticalDepthR + m_SCATTER_MIE * opticalDepthM;
		vec3 attenuation3 = exp( -tau3 ) * lookupAtten.rgb;
		sumR += phaseR*attenuation3*hr;
		sumM += phaseM*attenuation3*hm;

		tCurrent += segmentLength;
	}
	//return vec3(pathLength*0.000001);
	//return vec4(vec3(opticalDepthR), 1.0);
	//return vec4(vec3(pathLength*0.0001), 1.0);
	//return vec4(vec3(exp(-segmentLength*0.01)), 1.0);

	//return vec4(phaseR*m_SCATTER_RAY*1000000.0, 1.0);
	//Vector3f T = exp((m_SCATTER_RAY.mult(opticalDepthR).add(m_SCATTER_MIE.mult(opticalDepthM))).negate());
	return sumR*m_SCATTER_RAY + sumM*m_SCATTER_MIE;
}

vec3 ACESFilm( vec3 x ) {
    float a = 2.51f;
    float b = 0.03f;
    float c = 2.43f;
    float d = 0.59f;
    float e = 0.14f;
    return clamp((x*(a*x+b))/(x*(c*x+d)+e), 0.0, 1.0);
}
vec3 whitePreservingLumaBasedReinhardToneMapping(vec3 color) {
	float gamma = 2.2;
	float white = 2.;
	float luma = dot(color, vec3(0.2126, 0.7152, 0.0722));
	float toneMappedLuma = luma * (1. + luma / (white*white)) / (1. + luma);
	color *= toneMappedLuma / luma;
	color = pow(color, vec3(1. / gamma));
	return color;
}
const mat3 XYZ_2_RGB = (mat3(
		 3.2404542,-1.5371385,-0.4985314,
		-0.9692660, 1.8760108, 0.0415560,
		 0.0556434,-0.2040259, 1.0572252
));
vec3 xyz_to_rgb(vec3 xyz) {
    return XYZ_2_RGB * xyz;
}
const float SRGB_ALPHA = 0.055;
float linear_to_srgb(float channel) {
    if(channel <= 0.0031308)
        return 12.92 * channel;
    else
        return (1.0 + SRGB_ALPHA) * pow(channel, 1.0/2.4) - SRGB_ALPHA;
}
vec3 rgb_to_srgb(vec3 rgb) {
    return vec3(
        linear_to_srgb(rgb.r),
        linear_to_srgb(rgb.g),
        linear_to_srgb(rgb.b)
    );
}
void main() {
	#ifdef VIEW
		vec3 dir = normalize(pos);
	#else 
		//float az = (fpPos.x*2.0-1.0)*PI;
		//float ele = sin((fpPos.y-0.5)*PI);

		float az = (fpPos.x)*PI;
		float ele = sin(fpPos.y*0.5*PI);

		float s = sqrt(1.0-ele*ele);
		vec3 dir = vec3(s*cos(az),ele,s*sin(az));
	#endif

	vec4 res = vec4(0.0,0.0,0.0,1.0-lutT(m_Altitude*ATM_SIZE_INV, dir.y).g);

	/*vec3 color = atmosphere(
        normalize(dir),           // normalized ray direction
        //vec3(0,6372e3,0),               // ray origin
        vec3(0.0, m_PLANET_RAD+m_Altitude, 0.0),               // ray origin
        m_SunDir,                        // position of the sun
        22.0,                           // intensity of the sun
        //6371e3,                         // radius of the planet in meters
        m_PLANET_RAD,                         // radius of the planet in meters
        //6471e3,                         // radius of the atmosphere in meters
        m_PLANET_ATMOS_RAD,                         // radius of the atmosphere in meters
        //vec3(5.5e-6, 13.0e-6, 22.4e-6), // Rayleigh scattering coefficient
        m_SCATTER_RAY, // Rayleigh scattering coefficient
        //21e-6,                          // Mie scattering coefficient
        m_SCATTER_MIE.x,                          // Mie scattering coefficient
        //8e3,                            // Rayleigh scale height
        m_RAYLEIGH_SCALE_HEIGHT,                            // Rayleigh scale height
        //1.2e3,                          // Mie scale height
        m_MIE_SCALE_HEIGHT,                          // Mie scale height
        //0.758                           // Mie preferred scattering direction
        m_SUN_MIE_G                           // Mie preferred scattering direction
    );*/
	//color = 1.0 - exp(-1.0 * color);
	//res.rgb = color;

	#ifdef SUN
		res.rgb = m_SUN_INTENSITY*computeInscatter(dir, m_SUN_MIE_G, m_SunDir);
	#endif
	
	#ifdef MOON
		res.rgb += m_MOON_INTENSITY*computeInscatterPos(dir, m_MOON_MIE_G, m_MoonPos);
	#endif

	//res.rgb = 1.0 - exp(-4.0 * res.rgb);


	//res.rgb = computeInscatter2(dir, m_SUN_MIE_G, m_SunDir).rgb;
	//res.rgb *= vec3(0.97122365, 0.9988447, 1.0); //Sun color

	//res.rgb = xyz_to_rgb(res.rgb);
	//res.rgb = rgb_to_srgb(res.rgb);

	//res.rgb *= 100.0;
	//res.rgb = res.rgb/(res.rgb+0.1);
	//res.rgb = whitePreservingLumaBasedReinhardToneMapping(res.rgb);
	//res.rgb = ACESFilm(res.rgb);
	//res.rgb = pow(res.rgb, vec3(0.454545455));
	//res.rgb *= 4.0;

	//res = vec4(fpPos*0.5+0.5,0.0,1.0);

	//if(fpPos.y > 0.99) res.rgb = vec3(1.0,0.0,0.0);
	//if(fpPos.y > 0.997265625) res.rgb = vec3(1.0,0.0,0.0);

	//res.rgb = dir;
	//res.rgb = dir*0.5+0.5;
	//res.rgb *= sin(dir.y*10.0);

	//if(isinf(res.x) || isinf(res.y) || isinf(res.z)) res = vec4(0.0, 1.0, 0.0, 1.0);
	//else if(isnan(res.x) || isnan(res.y) || isnan(res.z)) res = vec4(1.0, 0.0, 0.0, 1.0);
	
	gl_FragColor = res;
}