uniform vec3 g_CameraPosition;
uniform vec2 g_FrustumNearFar;
uniform mat4 g_ProjectionMatrix;
uniform mat4 g_ViewProjectionMatrixInverse;
			   		 
uniform sampler2D m_ColorMap; 
uniform sampler2D m_NormalMap; 

uniform int m_PointLightsSize;
uniform vec4 m_PointLights[32]; //16 lights

#ifdef AmbientLight
uniform vec3 m_AmbientLight;
#endif

#ifdef ShadowMap
uniform sampler2D m_ShadowMap;
#endif
noperspective in vec2 texCoord;
smooth in vec3 rayDir;
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
/*vec3 rgb2hsv(vec3 c) {
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}
vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}*/
#ifdef ShadowMap
float Shadow(vec3 dir) {
	/*vec3 W = inPosition+m_ItemPos-m_LightPos;
	float dist = length(W);
	
	float inclination = acos(W.z/dist) * 0.636619772;//* 0.318309886;		//0, pi
	float azimuth = atan(W.y, W.x)       * 0.318309886;//* 0.159154943;		//0, 2pi
	
	gl_Position = vec4(inclination-1.0, azimuth, dist*0.1-1.0, 1.0);
	*/
	float dist = length(dir);
	//float inclination = acos(dir.y/dist) * 0.636619772;//* 0.318309886;		//0, pi
	//float azimuth = atan(dir.z, dir.x)       * 0.318309886;//* 0.159154943;		//0, 2pi
	//return texture(m_ShadowMap, vec2(inclination-1.0, azimuth)*0.5+0.5).r;
	
	#define PI 3.141592654

	float inclination = acos(dir.y/dist) * 0.318309886;// * 0.636619772;//* 0.318309886;		//0, pi
	//float azimuth = atan(dir.z, dir.x)       * 0.318309886;//* 0.159154943;		//0, 2pi
	vec2 UV = inclination*normalize(dir.zx);
	UV.x = UV.x*0.5-0.5;
	UV = UV*0.5+0.5;
	
	return texture(m_ShadowMap, UV).r;
}
#endif
void main() {
	vec3 dir = normalize(rayDir);
	

	vec4 col = texture(m_ColorMap, texCoord);
	vec4 n = texture(m_NormalMap, texCoord);
	n.xyz = n.xyz*2.0-1.0;
	//n = n*2.0-1.0;
	//vec3 w = texture(m_PositionMap, texCoord).rgb;
	//float d = texture(m_DepthMap, texCoord).r;

	//vec3 wdir = normalize(w-g_CameraPosition);
	//vec3 pos = g_CameraPosition+dir*linDepth(n.w);
	//vec3 pos = g_CameraPosition+dir*linDepth(d);
	//vec3 pos = g_CameraPosition+wdir*linDepth(d);
	//vec3 pos = g_CameraPosition+dir*d;
	vec3 pos = getPosition(n.w, texCoord);
	//vec3 pos = texture(m_PositionMap, texCoord).rgb;

	//float u = dot(n.rgb, vec3(1.0));

	/*vec3[] lights = vec3[16](
		vec3(1.5, 1.5, 1.5),
		vec3(9.5, 1.5, -0.49999976),
		vec3(9.5, 1.5, 1.5000001),
		vec3(13.5, 1.5, -0.5),
		vec3(12.5, 1.5, 5.5),

		vec3(18.0, 1.5, 3.0),
		vec3(20.0, 1.5, 3.0),
		vec3(18.0, 1.5, 7.0),
		vec3(20.0, 1.5, 7.0),
		vec3(34.5, 1.5, 6.5000005),

		vec3(36.5, 1.5, 6.5),
		vec3(32.5, 1.5, 6.5000005),
		vec3(41.0, 1.5, 6.0),
		vec3(39.0, 1.5, 6.0000005),
		vec3(41.0, 1.5, 4.0),

		vec3(39.0, 1.5, 4.0)
	);*/


	vec3 u = vec3(0.0);
	for(int i = 0; i < m_PointLightsSize; i++) {
//	for(int i = 0; i < 1; i++) {
		vec4 lPos = m_PointLights[i+i];
		vec4 lCol = m_PointLights[i+i+1];
		
		//Lambert
		//float z = max(dot(n.xyz,normalize(lPos.xyz-pos)), 0.0);
		//w
		//float z = max(dot(n.xyz,normalize(lPos.xyz-pos)), 0.0);
		//float z = max(dot(n.xyz,normalize(lPos.xyz-pos))+0.0125, 0.0)*0.987654321;
		float z = (dot(n.xyz,normalize(lPos.xyz-pos)));
		if(isnan(dir.x)) {
			u = vec3(1.0, 1.0, 1.0);
			break;
		}
		z = pow(z, 0.125);

		float dist = distance(pos, lPos.xyz);
		//float att = 1.0 / (1.0 + lPos.w*dist + lCol.w*dist*dist);
		float att = z / (1.0 + lPos.w*dist + lCol.w*dist*dist);
		u = u + att*lCol.rgb;
		#ifdef ShadowMap
		if(i == 0) {
			float val = Shadow(normalize(pos-lPos.xyz))*10.0;
			//u = vec3((val-dist)/20.0);
			//u = vec3((max(val-dist, 0.0))/10.0);
			if((val > 9.9 && dist > 9.9) || val < dist-2.0) u = vec3(0.0);
		}
		#endif
	}
	//u = vec3(1.0);

	//gl_FragColor = vec4(vec2(d), w.x, 1.0);

	if(n.w >= 1.0) gl_FragColor = vec4(vec3(1.0, 0.0, 0.0), 1.0);
    else {
		#ifdef AmbientLight
			u += m_AmbientLight;
		#endif
		//u = vec3(1.0);
		if(isnan(u.r)) {
			u = vec3(0.1);
		}
		gl_FragColor = vec4(col.rgb*u, 1.0);
		
		//try value change
		//float maxcol = 1.0/max(col.r, max(col.g, col.b));
		//gl_FragColor = vec4(col.rgb*(u*maxcol), 1.0);
		//try hue change
		//vec3 hsv = rgb2hsv(col.rgb);
		//hsv.r = fract(hsv.r+u.x*5.0);
		//gl_FragColor = vec4(hsv2rgb(hsv), 1.0); //hue change
		//gl_FragColor = vec4(hsv2rgb(hsv)*col.rgb, 1.0); //hue light

		//gl_FragColor = vec4(vec3(linDepth(d)), 1.0);
		//gl_FragColor = vec4(vec3(linDepth(d)), 1.0);
		//gl_FragColor = vec4(abs(w-pos), 1.0);

		//gl_FragColor = vec4(dir, 1.0);
		
		//gl_FragColor = vec4(vec3(linDepth(n.w)), 1.0);
		//gl_FragColor = vec4(pos, 1.0);
		//gl_FragColor = vec4(col.rgb*u, 1.0);
		//gl_FragColor = vec4(abs(wdir-dir), 1.0);
		//gl_FragColor = vec4(n.w, n.w, n.w, 1.0);

	}
}

