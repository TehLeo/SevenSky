#import "Shaders/Templates/Common/Compat.glsllib"

//uniform vec3 g_CameraPosition;
//uniform vec2 g_FrustumNearFar;
//uniform mat4 g_ProjectionMatrix;
uniform float g_Time;
uniform mat4 g_ViewProjectionMatrixInverse;

uniform sampler2D m_ColorMap; 
uniform sampler2D m_NormalMap;

uniform sampler2D m_NoiseMap; 

noperspective in vec2 texCoord;
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
vec3 getPosition(float depth, vec2 uv){
	vec4 pos = vec4(uv, depth, 1.0) * 2.0 - 1.0;
	pos = g_ViewProjectionMatrixInverse * pos;
	return pos.xyz / pos.w;
}

void main() {
	//vec3 dir = normalize(rayDir);

	vec4 col = texture(m_ColorMap, texCoord);
	vec4 n = texture(m_NormalMap, texCoord);
	n.xyz = n.xyz*2.0-1.0;

	
	vec3 pos = getPosition(n.w, texCoord);

	float noise = texture(m_NoiseMap, pos.xz*0.05+g_Time*0.05).r;


	float w = linDepth(n.w);
	float d = w*99.9;

	col.rgb = mix(col.rgb, vec3(1.0), clamp(d*0.2 * noise, 0.0, 1.0));
	//col.rgb = mix(col.rgb, vec3(1.0), clamp(w-max((-5.0+pos.y)*0.1, 0.0), 0.0, 1.0));

	//gl_FragColor = vec4(vec3(d*0.2), 1.0);

	gl_FragColor = vec4(col.rgb, 1.0);
}

