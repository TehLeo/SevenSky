uniform sampler2D m_NoiseMap;
uniform sampler2D m_TestMap;
uniform vec3 g_CameraPosition;

noperspective in vec3 rayDir;
vec3 worldPos;
vec3 norm;

#import "MatDefs/Templates/Common/Tex3d.glsllib"

#ifdef DEFFERED2
#import "Shaders/Templates/Deffered/Deffered.glsllib"
//layout(location = 0)
out uvec4 FragColor;
#endif

vec3 hash3( vec2 p ){
    vec3 q = vec3( dot(p,vec2(127.1,311.7)), 
				   dot(p,vec2(269.5,183.3)), 
				   dot(p,vec2(419.2,371.9)) );
	return fract(sin(q)*43758.5453);
}
//u - regularity, v - sharpness
float iqnoise( in vec2 x, float u, float v ){
    vec2 p = floor(x);
    vec2 f = fract(x);
		
	float k = 1.0+63.0*pow(1.0-v,4.0);
	
	float va = 0.0;
	float wt = 0.0;
    for( int j=-2; j<=2; j++ )
    for( int i=-2; i<=2; i++ )
    {
        vec2 g = vec2( float(i),float(j) );
		vec3 o = hash3( p + g )*vec3(u,u,1.0);
		vec2 r = g - f + o.xy;
		float d = dot(r,r);
		float ww = pow( 1.0-smoothstep(0.0,1.414,sqrt(d)), k );
		va += o.z*ww;
		wt += ww;
    }
	
    return va/wt;
}

float rand(vec2 n) { 
	return fract(sin(dot(n, vec2(12.9898, 4.1414))) * 43758.5453);
}

/* 0 1*/
float noise(vec2 p){
	vec2 ip = floor(p);
	vec2 u = fract(p);
	u = u*u*(3.0-2.0*u);
	float res = mix(
		mix(rand(ip),rand(ip+vec2(1.0,0.0)),u.x),
		mix(rand(ip+vec2(0.0,1.0)),rand(ip+vec2(1.0,1.0)),u.x),u.y);
	return res*res;
}
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

/**0,1*/
const mat2 m2 = mat2(0.8,-0.6,0.6,0.8);
float fbm( in vec2 p ) {
    float f = 0.0;
    f += 0.5000*noise( p ); p = m2*p*2.02;
    f += 0.2500*noise( p ); p = m2*p*2.03;
    f += 0.1250*noise( p ); p = m2*p*2.01;
    f += 0.0625*noise( p );
    return f/0.9375;
}
/*float fbm( in vec3 p ) {
    float f = 0.0;
    f += 0.5000*noise( p ); p = m2*p*2.02;
    f += 0.2500*noise( p ); p = m2*p*2.03;
    f += 0.1250*noise( p ); p = m2*p*2.01;
    f += 0.0625*noise( p );
    return f/0.9375;
}*/
float fbm(vec3 p) {
	float f = 0.0;
	f += 0.5000*noise( p ); p = p*2.02;
    f += 0.2500*noise( p ); p = p*2.03;
    f += 0.1250*noise( p ); p = p*2.01;
    f += 0.0625*noise( p );
	return f/0.9375;
}

float pattern( in vec2 p ) {
    vec2 q = vec2( fbm( p + vec2(0.0,0.0) ),
                   fbm( p + vec2(5.2,1.3) ) );
    return fbm( p + 4.0*q );
}
float pattern2( in vec2 p ) {
	vec2 q = vec2( fbm( p + vec2(0.0,0.0) ),
				   fbm( p + vec2(5.2,1.3) ) );
	vec2 r = vec2( fbm( p + 4.0*q + vec2(1.7,9.2) ),
				   fbm( p + 4.0*q + vec2(8.3,2.8) ) );
	return fbm( p + 4.0*r );
}

float pattern3(vec3 pos) {
	float r = pattern2(pos.xy*2.0);
	r += pattern2(pos.xz*2.0);
	r *= 0.5;
	return r;

	//return sin(10.0*pos.y)*0.5;//+0.5;
}

vec2 getBump(vec3 pos) {
	/*float yy = pos.y + noise(pos.xz);

	float dx = pattern2(vec2(pos.x, yy));
	float dz = pattern2(vec2(pos.z, yy));

	return vec2(dx, dz);*/
	//pos.x = pos.x + noise(pos.xy);
	//pos.z = pos.z + noise(pos.zy);

	//return vec2(fbm(pos.xy),fbm(pos.zy));

	//pos.x = pos.x + fbm(pos.xy);
	//pos.y = pos.y + fbm(pos.xy);

	//return vec2(iqnoise(pos.xy, 0.1, 0.1), 0.0);

	return vec2(0.0,0.0);
}

mat3x3 rotationAlign(vec3 d, vec3 z) {
  vec3 v = cross( z, d );
  float c = dot( z, d );
  float k = 1.0/(1.0+c);
  return mat3x3( v.x*v.x*k + c,   v.y*v.x*k - v.z,  v.z*v.x*k + v.y,
          v.x*v.y*k + v.z,  v.y*v.y*k + c,   v.z*v.y*k - v.x,
          v.x*v.z*k - v.y,  v.y*v.z*k + v.x,  v.z*v.z*k + c  );
}
float filterwidth(vec2 v) {
  vec2 fw = max(abs(dFdx(v)), abs(dFdy(v)));
  return max(fw.x, fw.y);
}
float linDepth(float depth) {
	float Near = 0.01;//g_FrustumNearFar.x;
	float Far = 250.0;//g_FrustumNearFar.y;
	//float z_n = 2.0 * depth - 1.0;
	float z_n = depth;

	float z_e = (2.0 * Near) / 
		(Far + Near - z_n* (Far-Near));
	//float z_e = (2.0 * g_FrustumNearFar.x) / 
	//	(g_FrustumNearFar.y + g_FrustumNearFar.x - z_n* (g_FrustumNearFar.y-g_FrustumNearFar.x));
	return z_e;
}
float checker(vec2 uv) {
  return step(1.0, mod(floor(uv.x) + floor(uv.y), 2.0));
}
float checkerf(vec2 uv) {
	float width = filterwidth(uv);
	vec2 p0 = uv - .5 * width;
	vec2 p1 = uv + .5 * width;
	#define BUMPINT(x) \
		  (floor((x)/2.0) + 2.f * max(((x)/2.0) - floor((x)/2.0) - .5f, 0.f))
	vec2 i = (BUMPINT(p1) - BUMPINT(p0)) / width;
	//vec2 i = BUMPINT(uv);
	return i.x * i.y + (1.0 - i.x) * (1.0 - i.y);
}

#import "MatDefs/Templates/Common/Bump.glsllib"
//#import "Shaders/Templates/Noise/Ashima/noise2D.glsllib"
//#import "Shaders/Templates/Noise/Ashima/noise3D.glsllib"

vec2 planemap(vec3 uv, vec3 n) {
	vec3 a,b;
	perpVectors(n, a, b);
	return vec2(length(a*uv), length(b*uv));
}
float tritex(sampler2D tex, vec3 uv, vec3 n) {
	return dot(vec3(texture(tex, uv.yz).r,texture(tex, uv.xz).r,texture(tex, uv.xy).r), abs(n));
}
vec3 tritex3(sampler2D tex, vec3 uv, vec3 n) {
	vec3 x = texture(tex, uv.yz).rgb;
	vec3 y = texture(tex, uv.xz).rgb;
	vec3 z = texture(tex, uv.xy).rgb;
	n = abs(n);
	return x*n.x+y*n.y+z*n.z;
}
float triplanar(sampler2D tex, vec3 uv, vec3 n) {
	n = pow(abs(n), vec3(32.0));
	//n -= vec3(min(n.x,min(n.y,n.z)));
	n /= n.x+n.y+n.z;
	return dot(vec3(texture(tex, uv.yz).r,texture(tex, uv.xz).r,texture(tex, uv.xy).r), n);
}
float triplanar(vec3 xyz, vec3 uv, vec3 n) {
	n = pow(abs(n), vec3(32.0));
	//n -= vec3(min(n.x,min(n.y,n.z)));
	n /= n.x+n.y+n.z;
	return dot(xyz, n);
}
vec3 triplanar3(sampler2D tex, vec3 uv, vec3 n) {
	vec3 x = texture(tex, uv.yz).rgb;
	vec3 y = texture(tex, uv.xz).rgb;
	vec3 z = texture(tex, uv.xy).rgb;
	n = abs(n);
	n = pow(abs(n), vec3(32.0));
	//n -= vec3(min(n.x,min(n.y,n.z)));
	n /= n.x+n.y+n.z;
	return x*n.x+y*n.y+z*n.z;
}

float noise3D(vec3 uv) {
	uv.xz = uv.xz + vec2(sin(uv.y),cos(uv.y));
	return texture(m_TestMap, uv.xz).r;
}

float snoise(vec3 uv) {
	return noise3D(uv);
	//return fbm(uv.xz);
	//return snoise(uv.xz+uv.yz);
	//return texture(m_NoiseMap, uv.xz+uv.yz).r;
	//return texture(m_NoiseMap, uv.xz).r;
}
vec3 h2D(vec3 uv, float s) {
	return vec3(clamp(fbm(uv.yz*s)*1.5, 0.5, 0.7),
			clamp(fbm(uv.xz*s)*1.5, 0.5, 0.7),
			clamp(fbm(uv.xy*s)*1.5, 0.5, 0.7));
}
//2d tiling
/*vec3 sampleTexture(vec3 uv) {
	//simple gradient tex
	vec2 uv2 = uv.xz + noise(uv.xz)*0.25;

	vec2 uv2i = floor(uv2);
	vec2 uv2f = fract(uv2);

	//float x = abs(rand(uv2i))*2.0-1.0;
	//float y = sign(x)*sqrt(1.0-x*x);

	float a = rand(uv2i)*6.28;
	float x = sin(a);
	float y = cos(a);

	vec2 uv2r = vec2(y*(uv2f.x-0.5) - x*(uv2f.y-0.5), x*(uv2f.x-0.5) + y*(uv2f.y-0.5))+0.5;
	uv2r *= rand(uv2i*17.7)+0.5;
	vec2 uv2rf = uv2r;//fract(uv2r);

	//float r= step(abs(uv2rf.x-0.5), 0.3)*step(abs(uv2rf.y-0.5), 0.3);
	float r = uv2rf.y*step(abs(uv2rf.x-0.5), 0.4)*step(abs(uv2rf.y-0.5), 0.4);

	return vec3(r);
	//return vec3(0.9);
}*/

vec3 sampleTexture(vec3 uv) {
	vec3 a = vec3(87.0, 67.0, 57.0)/255.0;
	vec3 b = vec3(25.0, 19.0, 15.0)/255.0;

	//float n = triplanar(m_NoiseMap, uv*0.05, norm);
	//n = clamp(n*1.5, 0.0, 1.0);

	return mix(a,b,r);
}
float sampleHeight(vec2 uv) {
	uv *= 2.0;
	vec2 p = uv;
	//p.x += step(1.0, mod(p.y,2.0)) * 0.5;
	p.y *= 2.0;

	vec3 a = vec3(87.0, 67.0, 57.0)/255.0;
	vec3 c = vec3(167.0, 105.0, 82.0)/255.0;
	col = mix(a,c,r_rand(p.y));

	p.x = d_brick(p.x, p.y, 0.5);
	p = d_tile(p, 1.0);
	float s = s_brick(p, 0.9);
	return s;
}

float rock(vec3 uv, float scale, float rand) {
	uv *= scale;

	vec2 p = uv.xz;
	float n = texture(m_NoiseMap, p).r;
	//p = m_dirwarp(p, vec2(1.0), n);

	float r = r_rand(uv.xz);
	
	p = d_tile(p,1.0);
	p = d_scale(p, uv.xz+10.0, 0.2);
	//float s = s_paraboloid(p)* step(r, rand);
	float s = s_brick(p, 0.7) * step(r, rand);

	//	s = min(s, 0.8);

	return s;
}
float sampleHeight(vec3 uv) {	
	float s = 0.0;

	vec3 a = vec3(87.0, 67.0, 57.0)/255.0;
	vec3 b = vec3(25.0, 19.0, 15.0)/255.0;

	vec3 c = vec3(167.0, 105.0, 82.0)/255.0;

	s = heightColTriPlanar(uv, 32.0);
	s = max(s, 0.1)*5.0;

	col = mix(b,col,s);

	return s*0.05;
}
vec3 texFilter(vec3 uv, vec3 uvX, vec3 uvY, float detail) {
	const int MaxSamples = 5;
    int sx = 2 + int(clamp( detail*length(uvX-uv), 0.0, float(MaxSamples-2) ));
    int sy = 2 + int(clamp( detail*length(uvY-uv), 0.0, float(MaxSamples-2) ));

    vec3 no = vec3( 0.0f );

    for( int j=0; j < sy; j++ )
    for( int i=0; i < sx; i++ ) {
        vec2 st = vec2( float(i), float(j) )/vec2(float(sx),float(sy));
        //no += sampleTexture( uv + st.x * (uvX-uv) + st.y*(uvY-uv) );
		vec3 val = sampleTexture( uv + st.x * (uvX-uv) + st.y*(uvY-uv) );

		//vec2 dxz = vec2(dFdx(val.r), dFdy(val.r));
		//float y = sqrt(1.0/(1.0+dxz.x*dxz.x+dxz.y*dxz.y));
		//vec3 N = vec3(y*dxz.x,y,y*dxz.y);
		//no += N;
		no += val;
    }

    return no / float(sx*sy);

	//return vec3(sx*sy/float((MaxSamples-1)*(MaxSamples-1)));
}
float hFilter(vec3 uv, vec3 uvX, vec3 uvY, float detail) {
	const int MaxSamples = 5;
    int sx = 2 + int(clamp( detail*length(uvX-uv), 0.0, float(MaxSamples-2) ));
    int sy = 2 + int(clamp( detail*length(uvY-uv), 0.0, float(MaxSamples-2) ));

    float no = 0.0;

    for( int j=0; j < sy; j++ )
    for( int i=0; i < sx; i++ ) {
        vec2 st = vec2( float(i), float(j) )/vec2(float(sx),float(sy));
		float val = sampleHeight( uv + st.x * (uvX-uv) + st.y*(uvY-uv) );

		no += val;
    }

    return no / float(sx*sy);

	//return vec3(sx*sy/float((MaxSamples-1)*(MaxSamples-1)));
}
/*vec3 sampleTextureWithFilter(vec3 uvw,vec3 ddx_uvw, vec3 ddy_uvw, vec3 nor, float mid) {
	const int MaxSamples = 2;
    int sx = 1 + int( clamp( 4.0*length(ddx_uvw-uvw), 0.0, float(MaxSamples-1) ) );
    int sy = 1 + int( clamp( 4.0*length(ddy_uvw-uvw), 0.0, float(MaxSamples-1) ) );

	vec3 no = vec3(0.0);

	//#if 1
    /*for( int j=0; j<MaxSamples; j++ )
    for( int i=0; i<MaxSamples; i++ )
    {
        if( j<sy && i<sx )
        {
            vec2 st = vec2( float(i), float(j) ) / vec2( float(sx),float(sy) );
            no += mytexture( uvw + st.x*(ddx_uvw-uvw) + st.y*(ddy_uvw-uvw), nor, mid );
        }
    }*/
    //#else
    /*for( int j=0; j<sy; j++ )
    for( int i=0; i<sx; i++ )
    {
        vec2 st = vec2( float(i), float(j) )/vec2(float(sx),float(sy));
        no += mytexture( uvw + st.x * (ddx_uvw-uvw) + st.y*(ddy_uvw-uvw), nor, mid );
    }
    //#endif	

	return no / float(sx*sy);
}*/
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
void main() {
	vec3 dir = normalize(rayDir);
	vec2 t2;
	/*if(calcForSphere(vec3(0.0, g_CameraPosition.y+6360.0e3,0.0),dir, 6360.0e3, t2)) {
		float t = t2.x >= 0.0?t2.x:t2.y;
		if(t < 0.0) { discard; return; }
		worldPos = g_CameraPosition + dir * t;
		norm = getN();
	}
	else {
		discard;
		return;
	}*/

	//worldPos = vec3(1.0);
	//norm = vec3(0.0,1.0,0.0);
	r = 0;

	float t = -g_CameraPosition.y/dir.y;
	if(t > 0.0) {
		worldPos = g_CameraPosition + dir*t;
		norm = vec3(0.0,1.0,0.0);
		col = vec3(1.0);
		
	}
	else {
		discard;
		return;
	}
	
	r = sampleHeight(worldPos);
	//vec3 col = sampleTexture(worldPos);
	//r += r_rand(fract(worldPos.xz)*10.0)*0.001;

	//col = vec3(1.0);

	#ifdef DEFFERED
		vec3 n = norm*0.5+0.5;
		gl_FragData[0] = vec4(col, 1.0);
		gl_FragData[1] = vec4(n.x, n.y, n.z, gl_FragCoord.z);
		gl_FragData[2] = vec4(worldPos,r);
	#elif DEFFERED2
		FragColor = pack(vec4(col, 1.0), norm, distance(g_CameraPosition, worldPos));
	#else
		gl_FragColor = vec4(col, 1.0);
	#endif
}

