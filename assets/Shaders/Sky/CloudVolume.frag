#import "Shaders/Templates/Common/Compat.glsllib"

uniform vec3 g_CameraPosition;

uniform sampler2D m_PhaseMap;
uniform sampler3D m_VolumeMap;
uniform sampler3D m_PerlinMap;

uniform vec3 m_SunIrradiance;
uniform vec3 m_SkyIrradiance;
uniform vec3 m_SunDir;

uniform vec3 m_AmbientColor;
uniform vec2 m_CloudOffset;

smooth in vec3 pos;

float cloudy = 0.5;

/*vec4 permute(vec4 x){return mod(((x*34.0)+1.0)*x, 289.0);}
vec4 taylorInvSqrt(vec4 r){return 1.79284291400159 - 0.85373472095314 * r;}
vec3 fade(vec3 t) {return t*t*t*(t*(t*6.0-15.0)+10.0);}

float cnoise(vec3 P){
  vec3 Pi0 = floor(P); // Integer part for indexing
  vec3 Pi1 = Pi0 + vec3(1.0); // Integer part + 1
  Pi0 = mod(Pi0, 289.0);
  Pi1 = mod(Pi1, 289.0);
  vec3 Pf0 = fract(P); // Fractional part for interpolation
  vec3 Pf1 = Pf0 - vec3(1.0); // Fractional part - 1.0
  vec4 ix = vec4(Pi0.x, Pi1.x, Pi0.x, Pi1.x);
  vec4 iy = vec4(Pi0.yy, Pi1.yy);
  vec4 iz0 = Pi0.zzzz;
  vec4 iz1 = Pi1.zzzz;

  vec4 ixy = permute(permute(ix) + iy);
  vec4 ixy0 = permute(ixy + iz0);
  vec4 ixy1 = permute(ixy + iz1);

  vec4 gx0 = ixy0 / 7.0;
  vec4 gy0 = fract(floor(gx0) / 7.0) - 0.5;
  gx0 = fract(gx0);
  vec4 gz0 = vec4(0.5) - abs(gx0) - abs(gy0);
  vec4 sz0 = step(gz0, vec4(0.0));
  gx0 -= sz0 * (step(0.0, gx0) - 0.5);
  gy0 -= sz0 * (step(0.0, gy0) - 0.5);

  vec4 gx1 = ixy1 / 7.0;
  vec4 gy1 = fract(floor(gx1) / 7.0) - 0.5;
  gx1 = fract(gx1);
  vec4 gz1 = vec4(0.5) - abs(gx1) - abs(gy1);
  vec4 sz1 = step(gz1, vec4(0.0));
  gx1 -= sz1 * (step(0.0, gx1) - 0.5);
  gy1 -= sz1 * (step(0.0, gy1) - 0.5);

  vec3 g000 = vec3(gx0.x,gy0.x,gz0.x);
  vec3 g100 = vec3(gx0.y,gy0.y,gz0.y);
  vec3 g010 = vec3(gx0.z,gy0.z,gz0.z);
  vec3 g110 = vec3(gx0.w,gy0.w,gz0.w);
  vec3 g001 = vec3(gx1.x,gy1.x,gz1.x);
  vec3 g101 = vec3(gx1.y,gy1.y,gz1.y);
  vec3 g011 = vec3(gx1.z,gy1.z,gz1.z);
  vec3 g111 = vec3(gx1.w,gy1.w,gz1.w);

  vec4 norm0 = taylorInvSqrt(vec4(dot(g000, g000), dot(g010, g010), dot(g100, g100), dot(g110, g110)));
  g000 *= norm0.x;
  g010 *= norm0.y;
  g100 *= norm0.z;
  g110 *= norm0.w;
  vec4 norm1 = taylorInvSqrt(vec4(dot(g001, g001), dot(g011, g011), dot(g101, g101), dot(g111, g111)));
  g001 *= norm1.x;
  g011 *= norm1.y;
  g101 *= norm1.z;
  g111 *= norm1.w;

  float n000 = dot(g000, Pf0);
  float n100 = dot(g100, vec3(Pf1.x, Pf0.yz));
  float n010 = dot(g010, vec3(Pf0.x, Pf1.y, Pf0.z));
  float n110 = dot(g110, vec3(Pf1.xy, Pf0.z));
  float n001 = dot(g001, vec3(Pf0.xy, Pf1.z));
  float n101 = dot(g101, vec3(Pf1.x, Pf0.y, Pf1.z));
  float n011 = dot(g011, vec3(Pf0.x, Pf1.yz));
  float n111 = dot(g111, Pf1);

  vec3 fade_xyz = fade(Pf0);
  vec4 n_z = mix(vec4(n000, n100, n010, n110), vec4(n001, n101, n011, n111), fade_xyz.z);
  vec2 n_yz = mix(n_z.xy, n_z.zw, fade_xyz.y);
  float n_xyz = mix(n_yz.x, n_yz.y, fade_xyz.x); 
  return 2.2 * n_xyz;
}*/


float mod289(float x){return x - floor(x * (1.0 / 289.0)) * 289.0;}
vec4 mod289(vec4 x){return x - floor(x * (1.0 / 289.0)) * 289.0;}
vec4 perm(vec4 x){return mod289(((x * 34.0) + 1.0) * x);}

float noise(vec3 p){
    vec3 a = floor(p);
    vec3 d = p - a;
    d = d * d * (3.0 - 2.0 * d);

    vec4 b = a.xxyy + vec4(0.0, 1.0, 0.0, 1.0);
    vec4 k1 = perm(b.xyxy);
    vec4 k2 = perm(k1.xyxy + b.zzww);

    vec4 c = k2 + a.zzzz;
    vec4 k3 = perm(c);
    vec4 k4 = perm(c + 1.0);

    vec4 o1 = fract(k3 * (1.0 / 41.0));
    vec4 o2 = fract(k4 * (1.0 / 41.0));

    vec4 o3 = o2 * d.z + o1 * (1.0 - d.z);
    vec2 o4 = o3.yw * d.x + o3.xz * (1.0 - d.x);

    return o4.y * d.y + o4.x * (1.0 - d.y);
}

float wmap(vec3 p) {
	p.xz = mod(p.xz, 200.0)-100.0;
	return step(dot(p.xz,p.xz), 2500.0)*step(10.0, p.y)*step(p.y, 25.0);
}

float noisee(vec3 p) {
	float v = texture(m_PerlinMap, p*0.0005).r;
	return max(v-0.6,0.0);
}
/*const mat3 m = mat3( 0.00,  0.80,  0.60,
                    -0.80,  0.36, -0.48,
                    -0.60, -0.48,  0.64 ) * 1.7;*/
//const mat2 m = mat2(0.8,-0.6,0.6,0.8);

float fbm(vec3 p) {
	float f = 0.0;
	f += 0.5000*noisee( p ); p = p*2.02;
    f += 0.2500*noisee( p ); p = p*2.03;
    f += 0.1250*noisee( p ); p = p*2.01;
    f += 0.0625*noisee( p );
	return f/0.9375;
}
const float  AMPLITUDE_FACTOR = 0.707f;     
const float  FREQUENCY_FACTOR = 2.5789f;  
// Increase frequency by some factor each new octave
float SampleMediumDensity( vec3 _Position) {
	vec3  UVW = _Position * 0.002;  // Let’s start with some low frequency
	float    Amplitude = 1.0;
	float V = Amplitude * noise( UVW );  
	Amplitude *= AMPLITUDE_FACTOR; UVW *= FREQUENCY_FACTOR;
	V += Amplitude * noise( UVW );  Amplitude *= AMPLITUDE_FACTOR; UVW *= FREQUENCY_FACTOR;
	V += Amplitude * noise( UVW );  Amplitude *= AMPLITUDE_FACTOR; UVW *= FREQUENCY_FACTOR;
	V += Amplitude * noise( UVW );  Amplitude *= AMPLITUDE_FACTOR; UVW *= FREQUENCY_FACTOR;

	float DensityFactor = 1.0;
	float DensityBias = -1.5 * DensityFactor;
	return clamp( DensityFactor * V + DensityBias, 0.0, 1.0);   
	//return 0.0;
}
float scale(float f, float s) {
	return max(f-s, 0.0)/(1.0-s);
}

//vec3 spherical(vec3 p) {
	//r = length(p);
	//theta = atan2(z,x);
	//phi = atan2(sqrt(x*x+z*z),y);
//	return vec3(atan(p.z,p.x), atan(length(p.xz),p.y), length(p));
//}
vec3 curve(vec3 p) {
	float len = length(p);
	return vec3(2.0 * len * asin(p.x/p.y), 2.0 * len * asin(p.z/p.y), len);
}

#define PLANET_RAD 6360000.0
#define CLOUDS_FROM 2000.0
#define CLOUDS_TO 4000.0

float baseLen = 0.0;

float map2(vec3 p) {
	//to spherical coordinates
	//
	//p = curve(p);
	//float n = texture(m_PerlinMap, p.xy*0.0001).x;


	float n = fbm(p*0.001);
	float a = n*0.1;
	//float n = texture(m_PerlinMap, p.xz*0.001).x;

	//float precision collapse
	
	

	//float a = n - 0.4 - 0.000000000000000000000000000001*(p.z-PLANET_RAD-CLOUDS_FROM);

	//float a = n - 0.4 - 0.001*( p.y - baseLen);

	//float a = scale((n - 1.0 - 0.01*(p.z-PLANET_RAD-CLOUDS_FROM)), 0.4);
	//float a = scale(n - 0.01*(p.z-PLANET_RAD-CLOUDS_FROM), 0.4);
	return a;
}
/*float mapH(vec3 p) {

	return texture(m_PerlinMap, p.xz*0.001).x;
	//vec2 coord = curve(p).xy*0.0001;
	//return texture(m_PerlinMap, coord).x;
}*/
/*vec3 map2N(vec3 p) {
	float h = mapH(p);
	float hx = mapH(p+vec3(1000.0/512.0,0.0,0.0));
	float hz = mapH(p+vec3(0.0,0.0,1000.0/512.0));
	
	return normalize(cross(normalize(vec3(1.0,hx-h,0.0)), normalize(vec3(0.0,hz-h,1.0))));
}*/

float rand(float n){return fract(sin(n) * 43758.5453123);}
float rand(vec2 n) { 
	return fract(sin(dot(n, vec2(12.9898, 4.1414))) * 43758.5453);
}
float noise(float p){
	float fl = floor(p);
  float fc = fract(p);
	return mix(rand(fl), rand(fl + 1.0), fc);
}
	
float noise(vec2 n) {
	const vec2 d = vec2(0.0, 1.0);
  vec2 b = floor(n), f = smoothstep(vec2(0.0), vec2(1.0), fract(n));
	return mix(mix(rand(b), rand(b + d.yx), f.x), mix(rand(b + d.xy), rand(b + d.yy), f.x), f.y);
}
float sign2(float x) {
    return step(0.0, x)*2.0 - 1.0;
}

//rayPosR - Ray pos relative to sphere
//Sphere pos is at zero
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
vec2 aabb(in vec3 rayPos, in vec3 rayInvDir, in vec3[2] aabb) {
	int sx = rayInvDir.x < 0?1:0;
	int sy = rayInvDir.y < 0?1:0;
	int sz = rayInvDir.z < 0?1:0;

	vec3 v0 = (vec3(aabb[sx].x, aabb[sy].y, aabb[sz].z)-rayPos)*rayInvDir;
	vec3 v1 = (vec3(aabb[1-sx].x, aabb[1-sy].y, aabb[1-sz].z)-rayPos)*rayInvDir;

	return vec2(max(v0.x,max(v0.y, v0.z)), min(v1.x,min(v1.y, v1.z)));
}
#define DROPLETS_IN_M3 1.0E9
#define DROPNOT_POW_1M 0.8380188850168011
float lightIntegral(float x, float s, float A, float B) {
	return s*pow( DROPNOT_POW_1M, A+x+(B-A)*x/s )/
	( log(DROPNOT_POW_1M)*(-A+B+s) );
}
float li(float xs, float A, float B) {
	return lightIntegral(xs, xs, A, B)-lightIntegral(0, xs, A, B);
}
void main() {
	vec4 col = vec4(0.0,0.0,0.0,0.0);


	vec3 origin = g_CameraPosition;
	vec3 dir = normalize(pos);

	origin.x += noise(gl_FragCoord.xx*0.7141)*0.33;
	origin.y += noise(gl_FragCoord.yy*0.7141)*0.33;
	origin.z += noise(gl_FragCoord.xy*0.7141)*0.33;

	//col.rgb = dir*0.5+0.5;
	//col.rgb = origin*0.01;

	int xw = 64, yw = 20, zw = 64;

	//texelFetch(m_VolumeMap, ivec3()).r;
	vec2 t = aabb(origin, 1.0/dir, vec3[2](vec3(0.0),vec3(64.0,20.0,64.0)));
	
	if(t.x >= t.y || t.y <= 0.0) {
		discard; return;
	}
	/*if(t.x < t.y && t.y > 0.0) {
		col.rgb = vec3(1.0);
	}*/
	t.x = max(t.x, 0.0);
	float len = t.y-t.x;
	//col.rgb = vec3(len*0.02);
	vec3 p0 = origin+dir*t.x;

	vec3 L = m_SunDir;
	vec3 E = -dir;
	float a = dot(L, E);

	float atex = -a*0.5+0.5;
	float phase = texture(m_PhaseMap,  vec2(atex, 0.083333333)).r;
	float phase2 = texture(m_PhaseMap, vec2(atex, 0.25)).r;
	float phase3 = texture(m_PhaseMap, vec2(atex, 0.416666667)).r;
	float phase4 = texture(m_PhaseMap, vec2(atex, 0.583333333)).r;
	float phase5 = texture(m_PhaseMap, vec2(atex, 0.75)).r;
	float phase6 = texture(m_PhaseMap, vec2(atex, 0.916666667)).r;
	float phase7 = 0.02454369260617026; //4.0*Math.PI/512.0;

	float T = 1.0;
	float light = 0.0;
	float stepSize = 1.0;

	vec3 dirInv = vec3(0.0);
	dirInv.x = dir.x == 0.0?0.0001f:abs(1.0/dir.x);
	dirInv.y = dir.y == 0.0?0.0001f:abs(1.0/dir.y);
	dirInv.z = dir.z == 0.0?0.0001f:abs(1.0/dir.z);

	float ttt = t.x;
	float totalDep = 0.0;
	float totalSunDep = 0.0;

	float MINT = 0.02;
	float MAXDEP = log(MINT)/log(DROPNOT_POW_1M);

	int i = 0;
	for(; i < 64 && ttt < t.y; i++) {

		float tx = dir.x >= 0.0 ? 1.0-fract(p0.x) : (0.01+fract(p0.x-0.01));
		float ty = dir.y >= 0.0 ? 1.0-fract(p0.y) : (0.01+fract(p0.y-0.01));
		float tz = dir.z >= 0.0 ? 1.0-fract(p0.z) : (0.01+fract(p0.z-0.01));

		//float tx = dir.x >= 0.0 ? 1.0-fract(p0.x) : (1.0-fract(p0.x-0.99));
		//float ty = dir.y >= 0.0 ? 1.0-fract(p0.y) : (1.0-fract(p0.y-0.99));
		//float tz = dir.z >= 0.0 ? 1.0-fract(p0.z) : (1.0-fract(p0.z-0.99));

		tx *= dirInv.x;
		ty *= dirInv.y;
		tz *= dirInv.z;

		float tStep = min(tx, min(ty, tz));
		ttt += tStep;

		vec4 attDen = texelFetch(m_VolumeMap, ivec3(p0+0.02*dir), 0);
		float sunDep = attDen.r;
		//float dep = attDen.a*stepSize*tStep;
		float dep = stepSize * tStep;

		if(attDen.a > 0.0) {
			//float T1 = pow(DROPNOT_POW_1M, 1.0);		
			T *= pow(DROPNOT_POW_1M, dep);//T1;
			totalDep += dep;
			//den += attDen.a;

			/*float[] P = float[](
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
			float ll = pow(DROPNOT_POW_1M, sunDep+dep)*phase;
			ll += pow(DROPNOT_POW_1M, sunDep+dep*0.5)*phase2 * P[1];
			ll += pow(DROPNOT_POW_1M, sunDep+dep*0.33333333)*phase3 * P[2];
			ll += pow(DROPNOT_POW_1M, sunDep+dep*0.25)*phase4 * P[3];
			ll += pow(DROPNOT_POW_1M, sunDep+dep*0.2)*phase5 * P[4];
			ll += pow(DROPNOT_POW_1M, sunDep+dep*0.166667)*phase6 * P[5];
			ll += phase7 * P7;
			light += T*ll;*/
		}	
	
		p0 += dir*tStep;
		if(T < MINT) { T = 0.0; break;}
	}
	
	
	totalDep = min(totalDep, MAXDEP);

	/*float light = li(dep, dA, dB)*phase;
	light += li(dep*0.5, dA, dB)*phase2 * P[1];
	light += li(dep*0.33333333, dA, dB)*phase3 * P[2];
	light += li(dep*0.25, dA, dB)*phase4 * P[3];
	light += li(dep*0.2, dA, dB)*phase5 * P[4];
	light += li(dep*0.166667, dA, dB)*phase6 * P[5];
	light += phase7 * P7;
	*/

	//T = 1.0-(1.0-T)*density; 


	/*float[] P = float[](
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
		C *= (totalDep*DROPLETS_IN_M3-i)/(i+1.0);
		P[i+1] = (P[i+1]*C);
		P7 -= P[i+1];
	}*/
	//if(totalDep > 0.0 ) {
		/*float ll = pow(DROPNOT_POW_1M, totalSunDep+totalDep)*phase;
		ll += pow(DROPNOT_POW_1M, (totalSunDep+totalDep)*0.5)*phase2 * P[1];
		ll += pow(DROPNOT_POW_1M, (totalSunDep+totalDep)*0.33333333)*phase3 * P[2];
		ll += pow(DROPNOT_POW_1M, (totalSunDep+totalDep)*0.25)*phase4 * P[3];
		ll += pow(DROPNOT_POW_1M, (totalSunDep+totalDep)*0.2)*phase5 * P[4];
		ll += pow(DROPNOT_POW_1M, (totalSunDep+totalDep)*0.166667)*phase6 * P[5];
		ll += phase7 * P7;
		light += ll;*/
		//col.rgb = vec3(totalDep/MAXDEP);
	//}

	if(totalDep > 0.0) {
		//totalDep = max(totalDep-texture(m_PerlinMap, p0*10.0).r*stepSize*2.0, 0.0);

		light = li(totalDep, 1.0, 1.0)* phase7 * 0.2;
	}

	col.rgb = vec3((/*sunAtten**/m_SunIrradiance*light

		//+ m_SkyIrradiance
	));

	//if(i < 10) col.rgb = vec3(1.0,1.0,0.0);

	//col.rgb = vec3(den*0.01);
	col.a = (1.0-T);
	
	#ifdef DEFFERED
		gl_FragData[0] = col;
		gl_FragData[1] = vec4(0.0, 0.0, 0.0, 1.0);
	#else
		gl_FragColor = col;
	#endif
}
/*void main() {
	vec3 origin = vec3(0.0, PLANET_RAD, 0.0);
	origin.xz += m_CloudOffset;

	vec3 dir = normalize(pos);


	//float multInv = 1.0/(sqrt(1.0-dir.y*dir.y)/(1.0-dir.y));
	//vec2 coords = multInv*dir.xz*0.5+0.5;

	//vec3 c1 = texture(m_ColorMap, coords).rgb;
	//c1 *= step(0.01, dir.y);
	//float mu = dot(dir, m_SunDir);
	//c1 += exp(250000.0*(mu-1.0));

	vec2 t, t2;
	if(!calcForSphere(origin, dir, PLANET_RAD+CLOUDS_FROM, t) ||
			!calcForSphere(origin, dir, PLANET_RAD+CLOUDS_TO, t2)) {
		
		#ifdef DEFFERED
			gl_FragData[0] = vec4(0.5,0.5,0.0,1.0);
			gl_FragData[1] = vec4(0.0, 0.0, 0.0, 1.0);
		#else
			gl_FragColor = vec4(0.5,0.5,0.0,1.0);
		#endif
	
		//discard;
		return;
	}
	//origin += cnoise(dir*1000.0)*10.0;


	const int steps = 32;
	//float add = (CLOUDS_TO-CLOUDS_FROM)/float(steps);
	//float d = max(t.x, t.y); //if t.x is negative, then t.y
	//float d2 = max(t2.x, t2.y);

	float d = -max(t.x, t.y); //if t.x is negative, then t.y
	float d2 = -max(t2.x, t2.y);

	float add = (d2-d)/float(steps);

	

	if( d < 0.0 ) {
		#ifdef DEFFERED
			gl_FragData[0] = vec4(1.0,0.0,0.0,1.0);
			gl_FragData[1] = vec4(0.0, 0.0, 0.0, 1.0);
		#else
			gl_FragColor = vec4(1.0,1.0,0.0,1.0);
		#endif
		return;
	}

	//vec3 p = origin+dir*d;
	//float v = map2(p);
	//v = max(v, 0.0);
	//a += v;

	baseLen = CLOUDS_FROM;//d*dir.y;

	vec3 c = vec3(1.0);
	float extinction = 1.0;
	vec3 scattering = vec3(0.0);

	float ScatteringFactor = 21e-6 * 0.01;
	float ExtinctionFactor = 21e-3 * 1.2;

	float dd = noisee(dir*d);

	const float M_PI = 3.141592654;
	const float SUN_G = 0.76;
	float g = SUN_G;
	float mu = dot( dir, m_SunDir );
	float phaseM = (3.0 / (  8.0 * M_PI )) * ( ( 1.0 - g * g ) * ( 1.0 + mu * mu ) ) / ( ( 2.0 + g * g ) * pow( 1.0 + g * g - 2.0 * g * mu, 1.5 ) );
	
	float PhaseAmbient = 1.0;
	vec3 AmbientColor = m_AmbientColor;
	vec3 SunColor = vec3(1.0);

	vec3 p = dir*d+vec3(m_CloudOffset.x, 0.0, m_CloudOffset.y)*10.0;
	for(int i = 0; i < steps; i++) {
		//float v = cnoise(p * 0.1)*wmap(p);
		float density = noisee(p);//map2(p);
		density = max(density, 0.0);

		float scattCoeff = ScatteringFactor*density;
		float extinCoeff = ExtinctionFactor*density;

		extinction *= exp(-extinCoeff*add);
		//float3  SunColor = ComputeSunColor( Position );  
		//float3  AmbientColor = ComputeAmbientColor( Position, ExtinctionCoeff);  

		//vec3 StepScattering = scattCoeff * add * vec3(1.0);
		vec3 StepScattering = scattCoeff * add * (phaseM * SunColor + PhaseAmbient * AmbientColor);
		scattering += extinction*StepScattering;
		

		//a += v;
		//if(a >= 1.0) {
			//vec3 n = map2N(p);
			//float z = max(dot(n, -m_SunDir), 0.0);
			//c *= z;
			//c = n *0.5+0.5;
		//	c = vec3(1.0);
		//	break;
		//}
		p += dir*add;
	}
	//a *= 0.4;
	//a = min(1.0, a);

	//a /= float(steps);

	//thus optical thickness is (add*a)
	

	//const vec3 betaM = vec3( 21e-6 );                       // Mie scattering coefficients at sea level
	

	//a = exp(-(1.0-a)*2.0) * phaseM;


	//vec4 col = vec4(scattering, extinction);
	//vec4 col = vec4(scattering*(extinction), 1.0-extinction);
	//vec4 col = vec4(vec3( scattering *  (1.0-extinction) ), 1.0-extinction);

	//const vec3 B_ext = vec3(5.8e-6, 13.5e-6,33.1e-6) + 210e-5;
	
	//sumR * phaseR * betaR + sumM * phaseM * betaM

	//vec3 inscatter = d * vec3(5.8e-6, 13.5e-6,33.1e-6) + 0.0*d * 210e-5; 
	
	//exp(-B_ext*d)
	//inscatter
	//extinction *= exp(-B_ext*d);

	vec4 col = vec4(vec3(  (//inscatter +
	 400.0 *  scattering) *  (1.0-extinction) ), (1.0-extinction) );

	//vec4 col = vec4(vec3(1.0)*(dd), 1.0-dd);

	//vec4 col = vec4(1.0, 1.0, 1.0, 1.0-a);
	//col.rgb = exp(-(1.0-a)* add) * phaseM * betaM * 10000.0 ;

	//a = d/20000.0;


	#ifdef DEFFERED
		gl_FragData[0] = col;
		gl_FragData[1] = vec4(0.0, 0.0, 0.0, 1.0);
	#else
		gl_FragColor = col;
	#endif
}*/

