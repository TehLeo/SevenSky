#import "Shaders/Templates/Common/Compat.glsllib"

#define M_PI 3.1415926535897932384626433832795

uniform sampler2D m_PhaseMap;
uniform vec3 g_CameraPosition;


#ifdef COLOR_MAP
uniform sampler2D m_ColorMap;
#endif
#ifdef COLOR
uniform vec4 m_Color;
#endif
uniform vec3 m_SunDir;
uniform vec3 m_SunIrradiance; 

uniform vec3 m_SkyIrradiance;

flat in vec3 wPos;
smooth in vec2 texCoord;
flat in float sunAtten;
flat in float density;
smooth in vec3 rayDir;

void main2() {
	vec4 col = vec4(1.0);

	float d = dot(texCoord,texCoord);

	if(d > 1.0) { 
		//gl_FragColor = vec4(0.0,0.0,0.0,0.0);
		discard;
		return;
	}


	float dep = 10.0 * 2.0*sqrt(1.0-d);
	float Bex = 0.01*density;
	float atten = exp(-Bex*dep);
	
	float g = 0.76;
	float mu = dot( normalize(rayDir), m_SunDir );
	float phaseU = 1.0 / ( 4.0 * M_PI );
    float phaseR = 1.0 / ( 6.0 * M_PI ) * ( 1.0 + mu * mu );

	//float phase = texture(m_PhaseMap, vec2(mu*0.5+0.5, 0.5)).r;
    //float phaseR = 3.0 / ( 16.0 * M_PI ) * ( 1.0 + mu * mu );
    //float phaseM = 3.0 / (  8.0 * M_PI ) * ( ( 1.0 - g * g ) * ( 1.0 + mu * mu ) ) / ( ( 2.0 + g * g ) * pow( 1.0 + g * g - 2.0 * g * mu, 1.5 ) );
	
	//phaseM = max(phaseM, 0.75);
	//vec3 sunLight = (m_SunIrradiance * sunAtten * phaseR + m_SkyIrradiance * phaseU) * dep;
	//vec3 sunLight = (m_SkyIrradiance * phaseU) * dep;
	
	vec3 sunLight = (0.0*m_SunIrradiance * sunAtten * phaseR) * dep;
	sunLight += m_SkyIrradiance * phaseU * dep;
	
	col = vec4( 0.1*sunLight, 1.0-atten);
	//col = vec4(sunLight,1.0-atten);
	//col = vec4(m_SunIrradiance*sunAtten,1.0-atten);

	//col.rgb *= dep*density;

	//col.rgb *= dep;

	#ifdef COLOR_MAP
		float n = texture(m_ColorMap, (texCoord*0.5+0.5)).r;
		//n = max(n*(1.0-d),0.0);
		//float n = max((1.0-d),0.0);

		col.rgb *= n;
		//col.a *= n;
		//col *= texture(m_ColorMap, texCoord*0.5+0.5);
	#endif
	#ifdef COLOR
		col *= m_Color;
	#endif

	//col.a = 0.0;
	
	gl_FragColor = col;
}
#define DROPLET_XAREA 1.7671458676442588e-10
#define DROPLETS_IN_M3 1.0E9
#define DROPNOT_POW_1M 0.8380188850168011
float sign2(float x) {
    return step(0.0, x)*2.0 - 1.0;
}
bool calcForSphere(vec3 rayPosR, vec3 rayDir, float rad, out vec2 res) {
    float b = 2.0 * dot(rayDir, rayPosR);
    float c = dot(rayPosR, rayPosR) - rad*rad;
    float disc = b * b - 4.0 * c;

    if (disc < 0.0) return false;

	float q = (-b + sign2(b)*sqrt(disc))*0.5;  

	c /= q;

	float t0 = min(q, c); 
    float t1 = max(q, c);   

	//if(t1 < 0.0) return false;
	
	res = vec2(t0, t1);   

	return true;
}
float lightIntegral(float x, float s, float A, float B) {
	return s*pow( DROPNOT_POW_1M, A+x+(B-A)*x/s )/
	( log(DROPNOT_POW_1M)*(-A+B+s) );
}
float li(float xs, float A, float B) {
	return lightIntegral(xs, xs, A, B)-lightIntegral(0, xs, A, B);
}
void main() {
	vec4 col = vec4(0.0);
	float d = dot(texCoord,texCoord);
	if(d > 1.0) { 
		//gl_FragColor = vec4(0.0,0.0,0.0,0.0);
		discard;
		return;
	}
	float radius = 1.0;
	float dep = radius * 2.0* sqrt(1.0-d);
	float T = pow(DROPNOT_POW_1M, dep);

	vec3 E = -normalize(rayDir);
	vec3 L = m_SunDir;
	//vec3 N = vec3(0.0,1.0,0.0);
	vec2 res;
	if(!calcForSphere(g_CameraPosition-wPos ,-E, radius, res)) {
		discard;
		return;
	}
	vec3 A = g_CameraPosition-E*res.x;
	vec3 B = g_CameraPosition-E*res.y;
	vec3 N = normalize(A-wPos);

	float a = dot(L, E);

	float atex = -a*0.5+0.5;
	float phase = texture(m_PhaseMap,  vec2(atex, 0.083333333)).r;
	float phase2 = texture(m_PhaseMap, vec2(atex, 0.25)).r;
	float phase3 = texture(m_PhaseMap, vec2(atex, 0.416666667)).r;
	float phase4 = texture(m_PhaseMap, vec2(atex, 0.583333333)).r;
	float phase5 = texture(m_PhaseMap, vec2(atex, 0.75)).r;
	float phase6 = texture(m_PhaseMap, vec2(atex, 0.916666667)).r;
	float phase7 = 0.02454369260617026; //4.0*Math.PI/512.0;
	
	float u0 = dot(N,L);
	float u = dot(N,E);

	float depth = dep;

	//plane at wPos, normal SunDir
	
	float dA = max(0.0, dot((wPos-A),m_SunDir));
	float dB = max(0.0, dot((wPos-B),m_SunDir)); 

	//dep = distance(A,B)
	/*if(dep - distance(A,B) < 0.01) {
		col.rgb = vec3(1.0,1.0,0.0);
	}*/

	//float light = lightIntegral(dep, dep, dA, dB)
	//			 -lightIntegral(0, dep, dA, dB);
	//light *= (1.0-T) * phase;
	

	//DROPLETS_IN_M3
	float[] P = float[](
		1.0,
		1.767145867956539e30,
		3.12280451863587e20,
		5.518451101543387e+10,
		9.75190806161261,
		1.723304403577079e-9,
		3.045330256012543e-19
	);

	float P7 = 1.0;
	float C = 1e-40; 
	for(int i = 0; i < 6; i++) {
		C *= (dep*DROPLETS_IN_M3-i)/(i+1.0);
		P[i+1] = (P[i+1]*C)*T;
		P7 -= P[i+1];
	}
	//float C = (dep*DROPLETS_IN_M3-0.0)/(0.0+1.0);
	//float C1 = P[1]*C;

	float extra =  radius*radius;

	float light = li(dep, dA, dB)*phase;
	light += li(dep*0.5, dA, dB)*phase2 * P[1];
	light += li(dep*0.33333333, dA, dB)*phase3 * P[2];
	light += li(dep*0.25, dA, dB)*phase4 * P[3];
	light += li(dep*0.2, dA, dB)*phase5 * P[4];
	light += li(dep*0.166667, dA, dB)*phase6 * P[5];
	light += phase7 * P7;
	/*light = light * sunAtten*(1.0-T) * 
		(/*phase + phase2 * P[1]
			   + phase3 * P[2]
			   + phase4 * P[3]
			   + phase5 * P[4]
			   + phase6 /* P[5]
			  // + phase7 * P7
		) ; //* 12.566370614;*/
	//light += sunAtten*phase7 * P7;

	//light *= extra;
	T = 1.0-(1.0-T)*density; 

	col.rgb = vec3((1.0-T)*(sunAtten*m_SunIrradiance*light

		+ m_SkyIrradiance
	));


	//col.rgb = vec3(phase1);

	/*if(a >= 0) {
		//top part
		//col.rgb = vec3(1.0,1.0,0.0);
		col.rgb = vec3(phase)*(u0/(u0+u)) * (1.0-exp(-depth*(1.0/u0+1.0/u)));
	}
	else {
		//bottom with singularity
		col.rgb = vec3(phase)*(u0/(u0+u)) * (exp(depth/u0) - exp(-depth/u));
	}*/
	//col.rgb = vec3(phase*100.0)*u0/(u0+u);
	//col.rgb = (N)*0.5+0.5;
	//col.a = 1.0;
	col.a = (1.0-T);

	gl_FragColor = col;
}

