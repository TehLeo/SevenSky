uniform vec3 g_CameraPosition;
uniform vec2 g_FrustumNearFar;
uniform mat4 g_ProjectionMatrix;
uniform mat4 g_ViewProjectionMatrixInverse;

uniform sampler2D m_ColorMap; 
uniform sampler2D m_NormalMap; 
uniform sampler2D m_PositionMap; 

noperspective in vec2 texCoord;
varying vec3 rayDir;
float linDepth(float depth) {
	float Near = 0.1;//g_FrustumNearFar.x;
	float Far = 100.0;//g_FrustumNearFar.y;
	//float z_n = 2.0 * depth - 1.0;
	float z_n = depth;

	float z_e = (2.0 * Near) / 
		(Far + Near - z_n* (Far-Near));
	//float z_e = (2.0 * g_FrustumNearFar.x) / 
	//	(g_FrustumNearFar.y + g_FrustumNearFar.x - z_n* (g_FrustumNearFar.y-g_FrustumNearFar.x));
	return z_e;
}
/*vec3 getPosition(float depth, vec2 uv){
	vec4 pos = vec4(uv, depth, 1.0) * 2.0 - 1.0;
	pos = g_ViewProjectionMatrixInverse * pos;
	return pos.xyz / pos.w;
}*/
ivec2 t = ivec2(gl_FragCoord.xy);
float w = 0;
vec3 sum = vec3(0.0);

void add(int x, int y, float w0, vec3 d) {
	vec3 col = texelFetch(m_ColorMap, ivec2(t.x+x,t.y+y), 0).rgb;
	//float dep = texelFetch(m_NormalMap, ivec2(t.x+x,t.y+y), 0).w;
	vec3 pos = texelFetch(m_PositionMap, ivec2(t.x+x,t.y+y), 0).rgb;

	w0 = w0*step(distance(d,pos),1.0);
	//w0 = w0*step(abs(d-dep), 0.95);

	sum += col*w0;
	w += w0;
}

void main() {
	//vec3 dir = normalize(rayDir);
	
	//vec4 col = texture(m_ColorMap, texCoord);
	vec4 n = texture(m_NormalMap, texCoord);
	//n.xyz = n.xyz*2.0-1.0;
	//vec3 pos = getPosition(n.w, texCoord);
	

	//gl_FragCoord.x
	vec4 col = texelFetch(m_ColorMap, t, 0);
	vec4 pos = texelFetch(m_PositionMap, t, 0);

	vec3 d = pos.xyz;

	//1/4 1/8 1/16
	float v1 = 0.125;
	float v2 = 0.0625;

	w += 0.25;
	sum += col.rgb*0.25;

	add(0,1,v1, d);
	add(1,0,v1, d);
	add(-1,0,v1, d);
	add(0,-1,v1, d);

	add(1,1,v2, d);
	add(1,-1,v2, d);
	add(-1,1,v2, d);
	add(-1,-1,v2, d);

	
	gl_FragColor = vec4(sum/w, 1.0);
	
}

