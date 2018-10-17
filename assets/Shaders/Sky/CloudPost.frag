#import "Shaders/Templates/Common/Compat.glsllib"

uniform vec3 g_CameraPosition;

uniform sampler2D m_WeatherMap;
uniform sampler2D m_LayerCloudMap;
uniform sampler2D m_PhaseMap;
//uniform sampler3D m_PerlinMap;
uniform sampler3D m_DetailMap;
uniform sampler2D m_TransmittanceLut;
uniform sampler2D m_CloudPathLut;
#ifdef LIGHT_SUN
uniform vec3 m_SunDir;
#endif
#ifdef LIGHT_MOON
uniform vec3 m_MoonPos;
#endif

uniform vec3 m_AmbientColor;
uniform vec2 m_CloudOffset;
//uniform vec3 m_TempSubPix;
//uniform float m_Temp;
//uniform float m_Temp2;
//uniform vec3 m_SpherePoint[4];

smooth in vec3 pos;

//uniform float m_SunStep;
//uniform float m_SunShadow;
//uniform float m_CloudScale;
uniform float m_DetailScale;
uniform float m_SunScale;
uniform float m_AmbientScale;
uniform float m_LightScale;
uniform float m_DensityEdge;
uniform float m_RainDensity;

uniform float m_AltoCoverage;
uniform float m_AltoLightScale;

uniform float m_Coverage;

uniform vec3 m_SunIrradiance;

uniform float m_PLANET_RAD;
uniform float m_PLANET_ATMOS_RAD;


float cloudy = 0.5;

uniform float m_ScatteringFactor;

//float ScatteringFactor = 0.176714642934;
//float ExtinctionFactor = 0.176714642934;

vec3 lutT(float alt, float mu) {
	//return textureLod(m_TransmittanceLut, vec2((mu+0.25f)*0.8, pow(alt, 0.25)), 0).rgb;
	//return textureLod(m_TransmittanceLut, vec2((mu+0.25f)*0.8, pow(alt, 0.25)), 1).rgb;
	return textureLod(m_TransmittanceLut, vec2(((sign(mu)*pow(abs(mu),0.333333333333333))+0.6)*0.625, pow(alt, 0.25)), 0).rgb;

}
vec2 lutC(float alt, float mu) {
	//return textureLod(m_CloudPathLut, vec2((pow(mu,0.333333333333333))*0.5+0.5, pow(alt, 0.25)), 1).rg;
	return textureLod(m_CloudPathLut, vec2(mu*0.5+0.5, pow(alt, 0.25)), 0).rg;
	//return textureLod(m_CloudPathLut, vec2(mu*0.5+0.5, pow(alt, 0.25)), 1).rg;
}

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

/*
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
	//float v = texture(m_PerlinMap, p*0.0001).r;
	float v = texture(m_PerlinMap, p*0.001).r;

	return v;
	//return max(v-0.6,0.0);
	//return min(v+0.4,1.0);
}*/
/*const mat3 m = mat3( 0.00,  0.80,  0.60,
                    -0.80,  0.36, -0.48,
                    -0.60, -0.48,  0.64 ) * 1.7;*/
//const mat2 m = mat2(0.8,-0.6,0.6,0.8);
/*
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
}*/

#define PLANET_RAD 6360000.0
#define CLOUDS_FROM 2000.0
#define CLOUDS_TO 3000.0

float baseLen = 0.0;

//#define CLOUDS_FROM 0.0
//#define CLOUDS_TO 128.0
/*

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
}*/
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

/*
vec4 permute(vec4 x) {
  return mod((34.0 * x + 1.0) * x, 289.0);
}
vec3 permute(vec3 x) {
  return mod((34.0 * x + 1.0) * x, 289.0);
}

vec4 dist(vec4 x, vec4 y, vec4 z) {
  //return manhattanDistance ?  abs(x) + abs(y) + abs(z) :  (x * x + y * y + z * z);
	return abs(x) + abs(y) + abs(z);
	//return (x * x + y * y + z * z);
}

vec2 worley(vec3 P, float jitter) {
	float K = 0.142857142857; // 1/7
	float Ko = 0.428571428571; // 1/2-K/2
	float K2 = 0.020408163265306; // 1/(7*7)
	float Kz = 0.166666666667; // 1/6
	float Kzo = 0.416666666667; // 1/2-1/6*2

	vec3 Pi = mod(floor(P), 289.0);
 	vec3 Pf = fract(P);
	vec4 Pfx = Pf.x + vec4(0.0, -1.0, 0.0, -1.0);
	vec4 Pfy = Pf.y + vec4(0.0, 0.0, -1.0, -1.0);
	vec4 p = permute(Pi.x + vec4(0.0, 1.0, 0.0, 1.0));
	p = permute(p + Pi.y + vec4(0.0, 0.0, 1.0, 1.0));
	vec4 p1 = permute(p + Pi.z); // z+0
	vec4 p2 = permute(p + Pi.z + vec4(1.0)); // z+1
	vec4 ox1 = fract(p1*K) - Ko;
	vec4 oy1 = mod(floor(p1*K), 7.0)*K - Ko;
	vec4 oz1 = floor(p1*K2)*Kz - Kzo; // p1 < 289 guaranteed
	vec4 ox2 = fract(p2*K) - Ko;
	vec4 oy2 = mod(floor(p2*K), 7.0)*K - Ko;
	vec4 oz2 = floor(p2*K2)*Kz - Kzo;
	vec4 dx1 = Pfx + jitter*ox1;
	vec4 dy1 = Pfy + jitter*oy1;
	vec4 dz1 = Pf.z + jitter*oz1;
	vec4 dx2 = Pfx + jitter*ox2;
	vec4 dy2 = Pfy + jitter*oy2;
	vec4 dz2 = Pf.z - 1.0 + jitter*oz2;
	vec4 d1 = dist(dx1, dy1, dz1);
	vec4 d2 = dist(dx2, dy2, dz2);


	// Do it right and sort out both F1 and F2
	vec4 d = min(d1,d2); // F1 is now in d
	d2 = max(d1,d2); // Make sure we keep all candidates for F2
	d.xy = (d.x < d.y) ? d.xy : d.yx; // Swap smallest to d.x
	d.xz = (d.x < d.z) ? d.xz : d.zx;
	d.xw = (d.x < d.w) ? d.xw : d.wx; // F1 is now in d.x
	d.yzw = min(d.yzw, d2.yzw); // F2 now not in d2.yzw
	d.y = min(d.y, d.z); // nor in d.z
	d.y = min(d.y, d.w); // nor in d.w
	d.y = min(d.y, d2.x); // F2 is now in d.y
	return sqrt(d.xy); // F1 and F2

}*/

vec3 Weather(vec2 posXZ) { 
	//vec3 w = texture(m_WeatherMap, posXZ*0.0001*m_CloudScale).rgb;
	vec3 w = texture(m_WeatherMap, posXZ*0.0001 + m_CloudOffset).rgb;
	//w.r = max(w.r-0.5,0.0);
	return w;
}
float HeightScale(vec2 weatherGB, float alt) {
	alt = max(alt, 0.0);
	
	float middle = (CLOUDS_TO+CLOUDS_FROM)*0.5f;
	float CLOUD_LAYER_HALF = (CLOUDS_TO-CLOUDS_FROM)*0.5f;
	
	float a = mix(middle, CLOUDS_FROM, weatherGB.x);
	float h = (weatherGB.x+weatherGB.y)*CLOUD_LAYER_HALF;
	//float a = mix(CLOUDS_FROM, CLOUDS_TO, weatherGB.y);
	//float h = weatherGB.x*(CLOUDS_TO-CLOUDS_FROM);
	//float x = pos.y;
	return (alt-a)*(alt-a-h)*(-4.0/(h*h));
	//if(x > 1000 ) return 1.0;
	//return 0.0;
}
//float CloudShape(vec3 pos) {
	//return texture(m_PerlinMap, pos*0.001953125*m_CloudScale).r;
//	return texture(m_PerlinMap, pos*0.001953125*m_CloudScale).r;
//}
float CloudDetail(vec3 pos, float d) {
	//if(d < 0.2) return texture(m_DetailMap, pos*0.001953125).r;
	//if(d > 0.0 && d < 0.2) return texture(m_DetailMap, pos*0.00001).r;

	pos = pos*0.0001 + vec3(m_CloudOffset.x, 0.0, m_CloudOffset.y);
	pos = pos * m_DetailScale;

	//pos = pos*0.001953125*m_DetailScale*32.0;
	//pos.xz += m_CloudOffset;
	
	//return 1.0-textureLod(m_DetailMap, pos*0.001953125*m_DetailScale, 0.0).r;
	return 1.0-texture(m_DetailMap, pos).r;
	//return 1.0-texelFetch(m_DetailMap, ivec3(mod(pos*32.0, 32.0)), 0).r;
	//return noise(pos);
	//vec2 w = worley(pos, 1.0);
	//return w.y-w.x;
}
float HeightGradient(vec2 weatherGB, float alt) {
	return (alt-CLOUDS_FROM)/(CLOUDS_TO-CLOUDS_FROM);
	//return (pos.y-CLOUDS_FROM)/(CLOUDS_TO-CLOUDS_FROM);
	//return 1.0;
}
float Density(vec3 pos) {
	//if(pos.y > CLOUDS_TO || pos.y < CLOUDS_FROM) return 0.0;
	float alt = length(vec3(0.0, m_PLANET_RAD, 0.0)+pos) - m_PLANET_RAD;

	//TODO DIV BY ZERO
	vec3 wtr = Weather(pos.xz);
	float d = max(wtr.r+m_Coverage-1.0, 0.0)/m_Coverage;//*2.0;

	if(d <= 0.0) return 0.0;
	
	//HEIGHT SCALE
	d *= HeightScale(wtr.gb, alt);

	//#ifdef USE_CLOUD_SHAPE 
	//	#ifdef USE_MULT_SHAPE
	//		d *= CloudShape(pos);
	//	#else 
	//		d -= CloudShape(pos);
	//	#endif
	//#endif

	#ifdef USE_CLOUD_DETAIL
		#ifdef USE_MULT_DETAIL
			d *= CloudDetail(pos, d);
		#else 
			d -= CloudDetail(pos, d);
		#endif
	#endif
//	d -= CloudShape(pos)*CloudDetail(pos, d);

	//GRADIENT
	d *= HeightGradient(wtr.gb, alt);
	
	//if(d > m_DensityEdge) return 1.0;
	//return 0.0;
	return clamp(d, 0.0, 1.0);
}

float Shadow(vec3 from, vec3 to) {
    const float numStep = 4.0;
    float shadow = 1.0;
    float D = 0.0;
    float dd = length(to-from) / numStep;
	int i = 0;
    for(float s=0.5; s<(numStep-0.1); s+=1.0) {
        vec3 pos = from + (to-from)*((s/*+m_Temp*/)/(numStep)) /*+ m_SpherePoint[i]*/;
		D = Density(pos);
        shadow *= exp(-D * dd);
		i++;
    }
    return shadow;
}


vec3 Sunlight(vec3 pos) {
	float shadow = 1.0;
	//vec3 sundir = vec3(0.707106781, 0.707106781, 0.0);
	//vec3 sundir = m_SunDir;
	//float cloudExt = ExtinctionFactor*m_SunShadow;
	/*shadow *= exp(-cloudExt*Density(pos+sundir*(1.0 + m_Temp*1.0)));
	shadow *= exp(-cloudExt*Density(pos+sundir*(2.0 + m_Temp*2.0)));
	shadow *= exp(-cloudExt*Density(pos+sundir*(4.0 + m_Temp*4.0)));
	shadow *= exp(-cloudExt*Density(pos+sundir*(8.0 + m_Temp*8.0)));*/

	/*shadow *= exp(-cloudExt*Density(pos+sundir*m_SunStep*(1.0 )));
	shadow *= exp(-cloudExt*Density(pos+sundir*m_SunStep*(2.0 )));
	shadow *= exp(-cloudExt*Density(pos+sundir*m_SunStep*(4.0 )));
	shadow *= exp(-cloudExt*Density(pos+sundir*m_SunStep*(8.0 )));*/


	//shadow = Shadow(pos+sundir*m_SunStep, pos + sundir*m_SunShadow);

	/*for(int i = 0; i < 4; i++) {
		vec3 p = pos + m_SpherePoint[i] + sundir * ( 1.0 + i*2.0);
		float D = Density(p);
		shadow *= exp(-D);
	}*/

	/*float D = Density(pos + sundir * m_SunStep + m_SpherePoint[0])+
			Density(pos + sundir * m_SunStep * 2.0 + m_SpherePoint[1])+
			Density(pos + sundir * m_SunStep * 3.0 + m_SpherePoint[2])+
			Density(pos + sundir * m_SunStep * 4.0 + m_SpherePoint[3]);
	shadow = exp(-D);*/

	/*float d = (1.0-Density(pos+m_SunDir*5.0))*
		(1.0-Density(pos+m_SunDir*15.0))*
		(1.0-Density(pos+m_SunDir*30.0))*
		(1.0-Density(pos+m_SunDir*90.0));*/
	//float d = 1.0;

	vec3 L;
	#ifdef LIGHT_SUN
		L = m_SunDir;
	#elif LIGHT_MOON
		L = normalize(m_MoonPos-pos);
	#endif

	//Sun LUT: x is dot product (sun dir x pos), y is height [0,1] 60km
	float d = dot(L, normalize(vec3(0.0, m_PLANET_RAD, 0.0)+pos));
	//vec3 sun = normalize(m_SunIrradiance) * texture(m_SunLut, vec2(1.0-(d+0.25)*0.8,pos.y*0.000016667)).rgb;
	float alt = length(vec3(0.0, m_PLANET_RAD, 0.0)+pos) - m_PLANET_RAD;
	vec3 sun = normalize(m_SunIrradiance) * lutT(alt/(m_PLANET_ATMOS_RAD-m_PLANET_RAD), d); //texture(m_SunLut, vec2(1.0-(d+0.25)*0.8,pos.y*0.000016667)).rgb;

	return sun*shadow * m_SunScale; //* vec3(255.0/255.0, 130.0/255.0, 10.0/255.0);    //*m_SunScale;
}

vec2 aabb(in vec3 rayPos, in vec3 rayInvDir, in vec3[2] aabb) {
	int sx = rayInvDir.x < 0?1:0;
	int sy = rayInvDir.y < 0?1:0;
	int sz = rayInvDir.z < 0?1:0;

	vec3 v0 = (vec3(aabb[sx].x, aabb[sy].y, aabb[sz].z)-rayPos)*rayInvDir;
	vec3 v1 = (vec3(aabb[1-sx].x, aabb[1-sy].y, aabb[1-sz].z)-rayPos)*rayInvDir;

	return vec2(max(v0.x,max(v0.y, v0.z)), min(v1.x,min(v1.y, v1.z)));
}
bool isNaN(vec3 res) {
	return isinf(res.x) || isinf(res.y) || isinf(res.z) || 
		   isnan(res.x) || isnan(res.y) || isnan(res.z);
}
void main() {
	//ScatteringFactor = m_ScatteringFactor;
	float ExtinctionFactor = m_ScatteringFactor;

	//vec3 origin = vec3(0.0, PLANET_RAD, 0.0) + g_CameraPosition;
	//origin.xz += m_CloudOffset;

	vec3 dir = normalize(pos);


	//float multInv = 1.0/(sqrt(1.0-dir.y*dir.y)/(1.0-dir.y));
	//vec2 coords = multInv*dir.xz*0.5+0.5;

	//vec3 c1 = texture(m_ColorMap, coords).rgb;
	//c1 *= step(0.01, dir.y);
	//float mu = dot(dir, m_SunDir);
	//c1 += exp(250000.0*(mu-1.0));

	/*vec2 t, t2;
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
	}*/

	/*mat4 val = mat4(
		1.0 / 17.0, 13.0 / 17.0, 4.0 / 17.0, 16.0 / 17.0,
		9.0 / 17.0, 5.0 / 17.0,  12.0 / 17.0, 8.0 / 17.0, 
	    3.0 / 17.0, 15.0 / 17.0, 2.0 / 17.0, 14.0 / 17.0, 
		11.0 / 17.0, 7.0 / 17.0,  10.0 / 17.0, 6.0 / 17.0  
	);*/
	mat4 val = mat4(0.0f,    0.5f,    0.125f,  0.625f,
				    0.75f,   0.22f,   0.875f,  0.375f,
	                0.1875f, 0.6875f, 0.0625f, 0.5625,
	                0.9375f, 0.4375f, 0.8125f, 0.3125
	);

	vec3 origin = g_CameraPosition;
		//origin.y = max(origin.y, 0.0);

		 //+ m_TempSubPix;
	/*vec2 t = aabb(origin, 1.0/dir, vec3[2](vec3(-512.0*32.0, CLOUDS_FROM, -512.0*32.0),vec3(512.0*32.0,CLOUDS_TO,512.0*32.0)));
	if(t.x >= t.y || t.y <= 0.0) {
		discard; return;
	}
	t.x = max(t.x, 0.0);*/
	
	vec2 t = lutC(max(origin.y/(m_PLANET_ATMOS_RAD-m_PLANET_RAD), 0.0), dir.y);//normalize(vec3(0.0, m_PLANET_RAD, 0.0)+origin).y);
	
	vec3 L;
	#ifdef LIGHT_SUN
		L = m_SunDir;
	#elif LIGHT_MOON
		L = normalize(m_MoonPos-g_CameraPosition);
	#endif
	vec4 I = vec4(0.0,0.0,0.0,1.0);

	if(t.y <= 0.0 
		|| t.x > 100000.0
			) {
		if(m_RainDensity > 0.0) {
			float mu = dot( dir, L );
			vec3 phaseM2 = texture(m_PhaseMap, vec2(mu*0.5+0.5, 0.5)).rgb;
			phaseM2 *= 1.0/512.0;
			I.rgb += phaseM2*m_SunIrradiance*m_RainDensity;
			gl_FragColor = I;
		}
		else discard;
		return;
	}

	

	//float len = t.y-t.x;

	const int steps = 16;

	float d = t.x;
	float d2 = t.y;
	//float add = (CLOUDS_TO-CLOUDS_FROM)/float(steps);
	//float d = max(t.x, t.y); //if t.x is negative, then t.y
	//float d2 = max(t2.x, t2.y);

	//float d = max(t.x, t.y); //if t.x is negative, then t.y
	//float d2 = max(t2.x, t2.y);
	//if(d2 < 0.0) { d2 = d; d = 0.0; }
	/*if(length(origin) > PLANET_RAD+CLOUDS_FROM) {
		if(t.x >= 0.0 && t.y >= 0.0) { d2 = min(t.x, t.y); }
		d = 0.0;
	}*/
	

	float add = (d2-d)/float(steps);
	add = min(add, 2.0*(CLOUDS_TO-CLOUDS_FROM)/float(steps));
	//float add = (CLOUDS_TO-CLOUDS_FROM)/float(steps);
	
	/*if( d < 0.0 ) {
		#ifdef DEFFERED
			gl_FragData[0] = vec4(1.0,0.0,0.0,1.0);
			gl_FragData[1] = vec4(0.0, 0.0, 0.0, 1.0);
		#else
			gl_FragColor = vec4(1.0,1.0,0.0,1.0);
		#endif
		return;
	}*/

	/*vec3 p = origin+dir*d;
	float v = map2(p);
	v = max(v, 0.0);
	a += v;*/

	baseLen = CLOUDS_FROM;//d*dir.y;

	vec3 c = vec3(1.0);
	//float extinction = 1.0;
	//vec3 scattering = vec3(0.0);

	//float ScatteringFactor = 21e-6 * 0.01;
	//float ExtinctionFactor = 21e-3 * 1.2;
	
	

	//float dd = noisee(dir*d);

	const float M_PI = 3.141592654;
	float g = 0.76;
	float g2 = -0.5;
	float mu = dot( dir, L );

	//float atex = dot(dir, m_SunDir)*0.5+0.5;
	vec3 phaseM = texture(m_PhaseMap, vec2(mu*0.5+0.5, 0.5)).rgb;
	phaseM *= 1.0/512.0;
	//float phaseMie


	//float mu = dot( dir, vec3(0.0,1.0,0.0) );
	//float phaseM = 1.0/(4.0*M_PI);
	//float phaseM = (3.0 / (  8.0 * M_PI )) * ( ( 1.0 - g * g ) * ( 1.0 + mu * mu ) ) / ( ( 2.0 + g * g ) * pow( 1.0 + g * g - 2.0 * g * mu, 1.5 ) );
	float phaseHG = 0.5 * ( 1.0 - g*g ) / pow( 1.0 + g*g - 2.0*g*mu, 1.5);
	float phaseHG2 = 0.5 * ( 1.0 - g2*g2 ) / pow( 1.0 + g2*g2 - 2.0*g2*mu, 1.5);
	float phase = 0.5*(phaseHG+phaseHG2);
	//vec3 phase = phaseM;

	//phase = 1.0/(4.0*M_PI);
	//phaseM *= 0.01;
	//float atex = dot(dir, m_SunDir)*0.5+0.5;
	//float phaseM = texture(m_PhaseMap,  vec2(atex, 0.083333333)).r;
	//phaseM *= 1.0/512.0;

	float PhaseAmbient = 1.0 * m_AmbientScale;
	vec3 AmbientColor = PhaseAmbient*m_AmbientColor;


	
	
	//I.rgb = vec3(curl.x);

	//I = vec4(vec3(CloudShape(g_CameraPosition+dir*d)), 0.0);
	//I = vec4(vec3(CloudDetail(g_CameraPosition+dir*d, 0)), 0.0);

	vec3 p = g_CameraPosition
			 + dir*val[int(gl_FragCoord.x)%4][int(gl_FragCoord.y)%4]*add
			/* + m_TempSubPix*/ + dir*d; //+vec3(m_CloudOffset.x, 0.0, m_CloudOffset.y)*10.0;
	//p += dir * m_Temp * add;
	//p += m_DensityEdge * dir * add;

	for(int i = 0; i < steps; i++) {
		//float v = cnoise(p * 0.1)*wmap(p);
		//float density = noisee(p);//map2(p);
		float density = Density(p);
		//vec3 Light = Sunlight(p);

		if(density > 0.0){
			float extinCoeff = ExtinctionFactor*density;
			float T = exp(-extinCoeff*add);
			vec3 Light = Sunlight(p)*(phase*(1.0-exp(-extinCoeff*add*2.0)));
			
			//if(I.a == 1.0) Light *= (1.0-Density(p+m_SunDir*10.0)); 
			//if(Density(p+m_SunDir*50.0) > 0.1) Light *= 0.0;
			Light += AmbientColor;
			//Light *= (phase*(1.0-exp(-extinCoeff*add*2.0))); // + PhaseAmbient * AmbientColor;
			vec3 I0 = (Light-Light*T)/max(extinCoeff,0.0000001);
			
			I.rgb += I.a*I0;
			I.a *= T;
		}

		/*if(I0.x == 0.0 && I0.y == 0.0 && I0.z == 0.0) {
				I = vec4(1.0, 0.0, 0.0, 0.0);
				break;
			}*/
		
		/*
		//if(density > 0.0) {
		
			//float scattCoeff = ScatteringFactor*density;
			float extinCoeff = ExtinctionFactor*density;
			float T = exp(-extinCoeff*add);

			//extinction *= exp(-extinCoeff*add);
			//float3  SunColor = ComputeSunColor( Position );  
			//float3  AmbientColor = ComputeAmbientColor( Position, ExtinctionCoeff);  

			//vec3 StepScattering = scattCoeff * add * vec3(1.0);
			//vec3 Light = phaseM * SunColor + PhaseAmbient * AmbientColor;
			vec3 Light = phase*Sunlight(p)*(1.0-exp(-extinCoeff*add*2.0)); // + PhaseAmbient * AmbientColor;
			
			//Light += m_AmbientScale*m_AmbientColor*(pos.y-CLOUDS_FROM)/(CLOUDS_TO-CLOUDS_FROM);
			//if(isnan(Light.x) || isinf(Light.x)) {

			//scattering += extinction*StepScattering;

			vec3 I0 = (Light-Light*T)/max(extinCoeff,0.0000001);
			I.rgb += I.a*I0;
			//I.rgb += scattCoeff*I.a*Light*add;
			I.a *= T;
			//if(I.a < 0.5) break;
			//if(I.a < 0.01) break;
		//}
		*/
		p += dir*add;
	}
	I.rgb *= m_LightScale;
	//I.rgb *= phase;
	
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

	//vec4 col = vec4(vec3(  (/*inscatter +*/ 400.0 *  scattering) *  (1.0-extinction) ), (1.0-extinction) );
	
	//vec4 col = vec4(vec3(1.0)*(dd), 1.0-dd);

	//vec4 col = vec4(1.0, 1.0, 1.0, 1.0-a);
	//col.rgb = exp(-(1.0-a)* add) * phaseM * betaM * 10000.0 ;

	//a = d/20000.0;

	I.rgb *= (1.0-I.a);
	if(I.a > 0.5) {
		vec3 alto = g_CameraPosition+dir*t.y;
		vec3 curl1 = texture(m_LayerCloudMap, alto.xz*0.0004+m_CloudOffset*0.5).rgb;
		vec3 curl2 = texture(m_LayerCloudMap, alto.xz*0.0004+m_CloudOffset.yx*0.5).rgb;
		vec3 curl3 = texture(m_LayerCloudMap, alto.xz*0.0004+m_CloudOffset.xx*0.5).rgb;
		vec3 curl = max(curl1.x * curl2.y * curl3.z - m_AltoCoverage, 0.0)*(Sunlight(alto)+AmbientColor);		
		I.rgb += (I.a*m_AltoLightScale)*curl*I.a;
		////I.a *= (1.0-curl.y);		
	}
	I.rgb += phaseM*m_SunIrradiance*m_RainDensity;

	I.a = 1.0-I.a;
	//I.a = 1.0;

	//ifdef DEFFERED
	//	gl_FragData[0] = vec4(I.rgb, 1.0-I.a);
	//	gl_FragData[1] = vec4(0.0, 0.0, 0.0, 1.0);
	//#else
		gl_FragColor = I;
	//#endif
}

