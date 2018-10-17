#define M_PI 3.1415926535897932384626433832795
uniform mat4 g_WorldMatrix;


uniform vec3 g_CameraPosition;
uniform sampler2D m_PhaseMap;
uniform sampler2D m_CloudTex;
uniform vec3 m_SizeInv;

smooth in vec2 texCoord;
smooth in vec3 rayDir;

float r_rand(vec2 uv) {
	vec2 v = floor(uv);

	float v1 = dot(v,v)+v.x*v.y;
	return fract((v1+floor(v1*0.007071067811865475))*0.7071067811865475);
}
float r_linear(vec2 uv) {
	float a = r_rand(uv);
	float b = r_rand(uv+vec2(1.0,0.0));
	float c = r_rand(uv+vec2(0.0,1.0));
	float d = r_rand(uv+vec2(1.0,1.0));

	vec2 fr = fract(uv);
	
	return mix(mix(a,b,fr.x), mix(c,d,fr.x),fr.y);
}
vec3 permute(vec3 x) { return mod(((x*34.0)+1.0)*x, 289.0); }
float snoise(vec2 v){
  const vec4 C = vec4(0.211324865405187, 0.366025403784439,
           -0.577350269189626, 0.024390243902439);
  vec2 i  = floor(v + dot(v, C.yy) );
  vec2 x0 = v -   i + dot(i, C.xx);
  vec2 i1;
  i1 = (x0.x > x0.y) ? vec2(1.0, 0.0) : vec2(0.0, 1.0);
  vec4 x12 = x0.xyxy + C.xxzz;
  x12.xy -= i1;
  i = mod(i, 289.0);
  vec3 p = permute( permute( i.y + vec3(0.0, i1.y, 1.0 ))
  + i.x + vec3(0.0, i1.x, 1.0 ));
  vec3 m = max(0.5 - vec3(dot(x0,x0), dot(x12.xy,x12.xy),
    dot(x12.zw,x12.zw)), 0.0);
  m = m*m ;
  m = m*m ;
  vec3 x = 2.0 * fract(p * C.www) - 1.0;
  vec3 h = abs(x) - 0.5;
  vec3 ox = floor(x + 0.5);
  vec3 a0 = x - ox;
  m *= 1.79284291400159 - 0.85373472095314 * ( a0*a0 + h*h );
  vec3 g;
  g.x  = a0.x  * x0.x  + h.x  * x0.y;
  g.yz = a0.yz * x12.xz + h.yz * x12.yw;
  return 130.0 * dot(m, g);
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
#define DROPNOT_POW_1M 0.8380188850168011
#define DROPLETS_IN_M3 1.0E9
float lightIntegral(float x, float s, float A, float B) {
	return s*pow( DROPNOT_POW_1M, A+x+(B-A)*x/s )/
	( log(DROPNOT_POW_1M)*(-A+B+s) );
}
float li(float xs, float A, float B) {
	return lightIntegral(xs, xs, A, B)-lightIntegral(0, xs, A, B);
}
void main() {
	vec4 col = vec4(0.0,0.0,0.0,1.0);

	vec2 rand = g_WorldMatrix[3].xz+texCoord;

	float shape = snoise(rand*2.0)*0.5+0.5;
	float shape2 = snoise(rand*4.0)*0.5+0.5;

	float scale = 2.0;
	float power = 16.0;

	float n1 = pow(snoise(texCoord*scale)*0.5+0.5, 0.3);
	float n2 = pow(snoise(texCoord*scale*2.0)*0.5+0.5, 0.3);
	float n3 = pow(snoise(texCoord*scale*4.0)*0.5+0.5, 0.3);

	n1 *= 1.0-pow((1.0-abs(n1-0.87)),power);
	n2 *= 1.0-pow((1.0-abs(n2-0.87)),power);
	n3 *= 1.0-pow((1.0-abs(n3-0.87)),power);

	float n = (n1+n2+n3)*0.3333;

	n = ( 1.0-n )*0.1+0.9;
	//n = n*0.1+0.9;

	//col.rgb = vec3(n);

	float d = dot(texCoord,texCoord);
	d = d + shape*0.5 - shape2*0.25;

	if(d > 1.0) { 
		discard;
		//gl_FragColor = vec4(0.0,0.0,0.0,1.0);
		//return;
	}
	col.a = min(1.0,(1.0-d)*5.0);
	float c = 1.0;
	
	//if(d+n*0.1 < 0.9) c = 0.95;
	//if(d+n*0.1 < 0.7) c = 0.9;

	//float cTex = texture(m_CloudTex, texCoord*0.5).r;
	//c *= (1.0-(cTex*0.2));

	c = n;

	vec3 cam = g_CameraPosition-g_WorldMatrix[3].xyz;
	vec3 dir = normalize(rayDir);
	vec3 E = -dir;

	cam *= m_SizeInv;
	dir = normalize(dir*m_SizeInv);

	vec3 N = vec3(0.0,1.0,0.0);

	vec2 t;
	float dep = 0.0;
	if(calcForSphere(cam, dir, 1.0, t)) {
		vec3 p0 = (cam+dir*t.x)/m_SizeInv;
		
		N = normalize(p0);

		dep = length((dir*t.x-dir*t.y)/m_SizeInv);
	}

	

	vec3 L = normalize(vec3(1.0,1.0,1.0));

	float ln = dot(L,N)*0.5+0.5;
	ln = ln*0.2+0.8;

	
	float a = dot(L, E);
	float T = pow(DROPNOT_POW_1M, dep);

	/*float atex = -a*0.5+0.5;
	float phase = texture(m_PhaseMap,  vec2(atex, 0.083333333)).r;
	float phase2 = texture(m_PhaseMap, vec2(atex, 0.25)).r;
	float phase3 = texture(m_PhaseMap, vec2(atex, 0.416666667)).r;
	float phase4 = texture(m_PhaseMap, vec2(atex, 0.583333333)).r;
	float phase5 = texture(m_PhaseMap, vec2(atex, 0.75)).r;
	float phase6 = texture(m_PhaseMap, vec2(atex, 0.916666667)).r;
	float phase7 = 0.02454369260617026; //4.0*Math.PI/512.0;


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
	float dA = 1.0;
	float dB = 1.0;
	
	dep *= 10.0;
	
	//c = phase+phase2+phase3+phase4+phase5+phase6+phase7;
	float light = li(dep, dA, dB)*phase;
	light += li(dep*0.5, dA, dB)*phase2 * P[1];
	light += li(dep*0.33333333, dA, dB)*phase3 * P[2];
	light += li(dep*0.25, dA, dB)*phase4 * P[3];
	light += li(dep*0.2, dA, dB)*phase5 * P[4];
	light += li(dep*0.166667, dA, dB)*phase6 * P[5];
	light += phase7 * P7;

	c = light*2.0;*/

	//c *= 1.0-T;
	//c *= ln;
	
	//col.rgb = N*0.5+0.5;

	c *= ln;
	
	col.rgb = vec3(c);
	//col.a = 1.0-T;
	gl_FragColor = col;
}

