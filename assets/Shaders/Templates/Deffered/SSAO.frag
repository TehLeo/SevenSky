uniform mat4 g_ViewProjectionMatrix;
uniform mat4 g_ViewProjectionMatrixInverse;
uniform sampler2D m_RandomMap;

noperspective in vec2 texCoord;
varying vec3 rayDir;

#define COLORMAP
#define NORMALMAP
#import "Shaders/Templates/Deffered/DFilterLib.glsllib"

const float g_Bias = 0.0;
/*float AO(vec3 P, vec3 N, vec2 uv) {
	vec4 n2 = texture(m_NormalMap, uv);
	vec3 P2 = glPos(n2.w, uv);
	vec3 PP2 = P2-P;
	return max(0.0, dot(N, normalize(PP2))-g_Bias)*(1.0/(1.0 + length(PP2)));
}*/
/*vec2 reflection(in vec2 v1,in vec2 v2){
    vec2 result= 2.0 * dot(v2, v1) * v2;
    result=v1-result;
    return result;
}*/
/*vec3 randomHemispherePoint(Vector3f store) {
    randomSpherePoint(store);
    store.y = Math.abs(store.y);
   return store;
}*/
/*vec3 rotate(Vector3f dir, Vector3f x, Vector3f y, Vector3f z) {
	dir.set(dir.x*x.x + dir.y*y.x + dir.z*z.x,
			dir.x*x.y + dir.y*y.y + dir.z*z.y,
			dir.x*x.z + dir.y*y.z + dir.z*z.z);
	return dir;
}*/
/** rotate from (0,1,0) to dir*/

mat3 rotMatrix(vec3 dir) {
	vec3 v1 = (abs(dir.x) > abs(dir.y))?vec3(dir.z, 0.0, -dir.x):vec3(0.0, -dir.z, dir.y);
	v1 = normalize(v1);
	return mat3(v1, dir, cross(dir,v1));
	/*if(Math.abs(dir.x) > Math.abs(dir.y)) store1.set(dir.z, 0, -dir.x).normalizeLocal();             
	else store1.set(0, -dir.z, dir.y).normalizeLocal();
	dir.cross(store1, store2);*/
}
vec4 col;
vec3 AO(vec3 P, vec3 N, vec2 uv) {
	vec4 n2 = glNorm(uv);
	vec3 P2 = glPos(n2.w, uv);
	vec3 PP2 = P2-P;
	float dist = length(PP2);
	//if(dist > 0.55) return 0.0;
	//float o = max(0.0, dot(N, normalize(PP2))-g_Bias)*(1.0/(1.0 + dist));
	//return sign(o);
	float o = step(0.1, max(0.0, dot(N, normalize(PP2))));
	return mix(col.rgb, texture(m_ColorMap, uv).rgb, o);
}
mat3 rot;
vec3 rand;
vec2 UV(vec3 P, vec3 dir) {
	const float sampleDistance = 0.1;
	dir = reflect(dir, rand);
	dir.y = abs(dir.y);
	dir = normalize(dir);
	
	vec3 SP = P+(rot*dir)*sampleDistance; //Sample Point w space
	vec4 offset = vec4(SP, 1.0);
	offset = g_ViewProjectionMatrix * offset;
	offset.xy /= offset.w;
	offset.xy = offset.xy * 0.5 + 0.5;
	return offset.xy;
}
float doAmbientOcclusion(in vec2 tcoord, in vec2 uv, in vec3 original, in vec3 cnorm) {
	const float INTENSITY = 3.0;
	
    vec3 newp = glPos(tcoord + uv);
    vec3 diff = newp - original;
    vec3 v = normalize(diff);
    float d = length(diff) /* * SCALE*/;
 
    float ret = max(0.0, dot(cnorm, v) /* - BIAS*/) * (INTENSITY / (1.0 + d));
    return ret;
}
float repeat(vec3 p, vec4 n) {
	const vec2 KERNEL[16] = vec2[](vec2(0.53812504, 0.18565957), vec2(0.13790712, 0.24864247), vec2(0.33715037, 0.56794053), vec2(-0.6999805, -0.04511441), vec2(0.06896307, -0.15983082), vec2(0.056099437, 0.006954967), vec2(-0.014653638, 0.14027752), vec2(0.010019933, -0.1924225), vec2(-0.35775623, -0.5301969), vec2(-0.3169221, 0.106360726), vec2(0.010350345, -0.58698344), vec2(-0.08972908, -0.49408212), vec2(0.7119986, -0.0154690035), vec2(-0.053382345, 0.059675813), vec2(0.035267662, -0.063188605), vec2(-0.47761092, 0.2847911));
	const float SAMPLE_RAD = 0.1; 
    //vec3 p = getPosition(vTexCoords);
    //vec3 n = getNormal(vTexCoords);
    //vec2 rand = getRandom(vTexCoords);
	vec2 rand = vec2(0.707,0.707);
 
    float fColor = 0.0;

	float rad = SAMPLE_RAD/(n.w*10.0); ///n.w;
 
    const int ITERATIONS = 16;
    for(int j = 0; j < ITERATIONS; ++j)
    {
        //vec2 coord = reflect(KERNEL[j], rand) * SAMPLE_RAD;
		vec2 coord = KERNEL[j] * rad;
        fColor += doAmbientOcclusion(texCoord, coord, p, n.xyz);
    }
 
    fColor = 1.0 - fColor / ITERATIONS;
	return fColor;
}
void main() {
	vec3 dir = normalize(rayDir);
	col = texture(m_ColorMap, texCoord);

	vec4 n = glNorm();
	vec3 P = glPos(n.w, texCoord);

	rot = rotMatrix(n.xyz);

	vec3 o = vec3(0.0);
	//float s = 0.05;

	rand = texture(m_RandomMap, texCoord).xyz;

	//o += AO(P, n.xyz, UV(P, normalize(vec3(0,1,0))));

	o += AO(P, n.xyz, UV(P, vec3(1,1,1)));
	o += AO(P, n.xyz, UV(P, vec3(-1,1,-1)));
	o += AO(P, n.xyz, UV(P, vec3(-1,1,1)));
	o += AO(P, n.xyz, UV(P, vec3(1,1,-1)));
	o = o * 0.25;

	o = clamp(o, 0.0, 1.0);
	//o = mix(0.5, 1.0, o);

	//o += repeat(P, n);

	//vec2 uv = UV(P, normalize(vec3(1,1,1)));
	
	//vec4 n2 = glNorm(uv);
	//vec3 P2 = glPos(n2.w, uv);
	//vec3 PP2 = P2-P;
	//float dist = length(PP2);
	//if(dist > 0.55) return 0.0;
	//float o = max(0.0, dot(N, normalize(PP2))-g_Bias)*(1.0/(1.0 + dist));
	//return sign(o);
	//float dd = step(0.05, max(0.0, dot(n.xyz, normalize(PP2))));

	//gl_FragColor = vec4(vec3(dd), col.a);
	gl_FragColor = vec4(o, col.a);
	//gl_FragColor = vec4(vec3(length(PP2)), col.a);
//	gl_FragColor = vec4(col.rgb*o, col.a);
}