uniform sampler2D m_ColorMap; 
uniform sampler2D m_NormalMap; 
uniform sampler2D m_PositionMap; 

uniform sampler2D m_ReflectionMap;

uniform sampler2D m_CloudShadowMap;
uniform sampler2D m_TransmittanceLut;
uniform	vec3 m_SunDir;
uniform	float m_Coverage;

uniform vec2 g_Resolution;
uniform vec3 g_CameraPosition;

uniform int m_DirLightsSize;
uniform vec3 m_DirLights[12]; //6 lights


#ifdef AmbientLight
uniform vec3 m_AmbientLight;
#endif

noperspective in vec2 texCoord;
smooth in vec3 rayDir;

vec4 quat(vec3 d, vec3 z) {
	vec4 quat = vec4(cross(z,d),
		sqrt(dot(z,z)*dot(d,d)) + dot(z,d));
	quat *= inversesqrt(dot(quat,quat));
	return quat;
}
vec3 qmult(vec4 q, vec3 v) { 
	return v + 2.0*cross(cross(v, q.xyz) + q.w*v, q.xyz);
}
float r_rand(vec2 uv) {
	vec2 v = floor(uv);

	float v1 = dot(v,v)+v.x*v.y;
	return fract((v1+floor(v1*0.007071067811865475))*0.7071067811865475);
	//return fract(v1*0.7071067811865475);
}
vec4 wPos;
vec3 getN(vec3 norm) {
	//vec4 w = texture(m_PositionMap, texCoord);
	//vec4 wx = dFdx(w);
	//vec4 wy = dFdy(w);

	ivec2 uv = ivec2(texCoord*g_Resolution);
	vec4 w = texelFetch(m_PositionMap,  uv, 0);
	wPos = w;
	vec4 wx = texelFetch(m_PositionMap, uv+ivec2(1,0), 0) - w;
	vec4 wy = texelFetch(m_PositionMap, uv+ivec2(0,1), 0) - w;

	/*vec4 wx0 = texelFetch(m_PositionMap,  uv+ivec2(-1,0), 0);
	vec4 wy0 = texelFetch(m_PositionMap,  uv+ivec2(0,-1), 0);
	vec4 wx = texelFetch(m_PositionMap, uv+ivec2(1,0), 0) - wx0;
	vec4 wy = texelFetch(m_PositionMap, uv+ivec2(0,1), 0) - wy0;*/

	//vec3 uv   = w.xyz;
	vec3 uvdx = wx.xyz;
	vec3 uvdy = wy.xyz;

	//float r   = w.w;
	float drx = wx.w;
	float dry = wy.w;

	vec3 normF = normalize(cross(uvdx, uvdy));
	vec3 N = normalize(cross(uvdx + drx*normF, uvdy + dry*normF));

	//vec4 q = quat(normF, N);
	//return qmult(q, norm);

	vec4 q = quat(normF, norm);
	N = qmult(q, N);
	//N.x += r_rand(fract(w.xy)*1000.0)*0.05 - 0.025;
	//N.z += r_rand(fract(w.yz)*1000.0)*0.05 - 0.025;
	//N = normalize(N);
	return N;
	//return vec3(sin((w.y+norm.y)*20.0));
}
vec3 lutT(float alt, float mu) {
	//return texture(m_TransmittanceLut, vec2((mu+0.25f)/1.25f, alt));
	return texture(m_TransmittanceLut, vec2((mu+0.25f)*0.8, pow(alt, 0.25))).rgb;
}
void main() {
	vec3 ray = normalize(rayDir);

	vec4 col = texture(m_ColorMap, texCoord);
	vec4 n = texture(m_NormalMap, texCoord);

	if(n.w == 0.0) {
		col.rgb = 1.0-exp(-col.rgb);
		gl_FragColor = vec4(col.rgb, 1.0);
		return;
	}

	n.xyz = n.xyz*2.0-1.0;

	vec3 N = n.xyz;

	n.xyz = getN(n.xyz);
	
	vec3 u = vec3(0.0);
	for(int i = 0; i < m_DirLightsSize; i++) {
		vec3 lDir = m_DirLights[i+i];
		vec3 lCol = m_DirLights[i+i+1];

		float z = max(dot(n.xyz, -lDir.xyz), 0.0);
		//if(z > 0.0) {
		//	vec3 R = reflect(-lDir.xyz, n.xyz);
		//	float intSpec = max(dot(R,ray), 0.0);
		//	u += vec3(pow(intSpec, 128.0))*0.5;
		//}
		u += z*lCol.rgb;
	}
	float z = max(dot(n.xyz, m_SunDir), 0.0);
	z *= smoothstep(0.0, 0.1, m_Coverage-texture(m_CloudShadowMap, wPos.xz*0.001).r);
	u += z;
	//u += z*lCol.rgb;

	//u = vec3(1.0);

	float metal = 0.0;
	float diffuse = 1.0 - metal;
	float roughness = 0.0;

	//u = vec3(max(dot(n.xyz, vec3(-1.0,1.0,1.0)), 0.0));
	//u = max(u, 0.2);
	//u = vec3(1.0);
	//metal = max(0.004, metal);
	
	//metal
	vec3 R = reflect(ray, n.xyz);
	if(R.y > 0.0 && metal > 0.0) {
		float multInv = 1.0/(sqrt(1.0-R.y*R.y)/(1.0-R.y));
		vec2 coords = multInv*R.xz*0.5+0.5;
		vec3 Rcol = textureLod(m_ReflectionMap, coords, roughness*5.0).rgb;
		Rcol = 1.0-exp(-Rcol);
		u = diffuse*u + metal*Rcol;
	}
	else u = diffuse*u + metal*vec3(0.37254902,0.203921569,0);

	#ifdef AmbientLight
		u += m_AmbientLight;
	#endif
	//u = 1.0-exp(-col.rgb*u);

	u = col.rgb*u;

	//Now, transmittance... & inscattered light
	float dist = distance(wPos.xyz, g_CameraPosition);

	float m_RAYLEIGH_SCALE_HEIGHT = 7994;
	float m_MIE_SCALE_HEIGHT = 1200;

	float ATM_SIZE_INV = 1.0/60000.0;
	vec3 m_SCATTER_RAY = vec3(5.5e-6, 13.0e-6, 22.4e-6);
	vec3 m_SCATTER_MIE = vec3(21e-6);

	float g = 0.76;
	float mu = dot( ray, m_SunDir );
	//float phaseR = (3.0 / ( 16.0 * PI ) * ( 1.0 + mu * mu ));
	float phaseR = 0.059683104 * ( 1.0 + mu * mu );
	//float phaseM = (3.0 / (  8.0 * PI ) * ( ( 1.0 - g * g ) * ( 1.0 + mu * mu ) ) / ( ( 2.0 + g * g ) * pow( 1.0 + g * g - 2.0 * g * mu, 1.5 ) ) );
	float phaseM = g*g; phaseM = (0.119366207 * ( ( 1.0 - phaseM ) * ( 1.0 + mu * mu ) ) / ( ( 2.0 + phaseM ) * pow( 1.0 + phaseM - 2.0 * g * mu, 1.5 ) ) );
	
	float hr = exp( -g_CameraPosition.y / m_RAYLEIGH_SCALE_HEIGHT ) * dist;
	float hm = exp( -g_CameraPosition.y / m_MIE_SCALE_HEIGHT      ) * dist;

	float ViewDZenith = m_SunDir.y;//dot(normalize(samplePosition),L);
	float altitude = g_CameraPosition.y * ATM_SIZE_INV;

	vec3 lookupAtten = lutT(altitude, ViewDZenith);
	//vec3 tau3 = m_SCATTER_RAY * opticalDepthR + m_SCATTER_MIE * ( 1.1 * opticalDepthM);
	vec3 tau3 = m_SCATTER_RAY * hr + m_SCATTER_MIE * ( 1.1 * hm);
	vec3 attenuation3 = exp( -tau3 ) * lookupAtten.rgb;
	vec3 sumR = attenuation3*hr;
	vec3 sumM = attenuation3*hm;

	u *= exp(-(m_SCATTER_RAY*sumR + m_SCATTER_MIE*sumM));
	u += (phaseR*sumR*m_SCATTER_RAY + phaseM*sumM*m_SCATTER_MIE) * 100.0;

	//gamma
	u = pow(u, vec3(0.454545455));

	gl_FragColor = vec4(u, 1.0);
	//gl_FragColor = vec4(n.xyz*0.5+0.5, 1.0);
}

