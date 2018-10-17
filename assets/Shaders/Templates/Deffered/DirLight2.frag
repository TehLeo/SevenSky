uniform usampler2D m_BufferMap; 

uniform sampler2D m_ReflectionMap;

uniform sampler2D m_CloudShadowMap;
uniform sampler2D m_TransmittanceLut;
uniform	vec3 m_SunDir;
uniform	vec3 m_SunLight;
uniform	float m_Coverage;

uniform float m_ToneMapScale;
uniform float m_ToneMapFrom;
uniform float m_ToneMapTo;

uniform vec2 g_Resolution;
uniform vec3 g_CameraPosition;


uniform vec2 m_CloudOffset;
uniform float m_PLANET_RAD;
uniform float m_PLANET_ATMOS_RAD;

uniform int m_DirLightsSize;
uniform vec3 m_DirLights[12]; //6 lights

#import "Shaders/Templates/Deffered/Deffered.glsllib"


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
/*vec3 getN(vec3 norm) {
	//vec4 w = texture(m_PositionMap, texCoord);
	//vec4 wx = dFdx(w);
	//vec4 wy = dFdy(w);

	ivec2 uv = ivec2(texCoord*g_Resolution);
	vec4 w = texelFetch(m_PositionMap,  uv, 0);
	vec4 wx = texelFetch(m_PositionMap, uv+ivec2(1,0), 0) - w;
	vec4 wy = texelFetch(m_PositionMap, uv+ivec2(0,1), 0) - w;

	//vec4 wx0 = texelFetch(m_PositionMap,  uv+ivec2(-1,0), 0);
	//vec4 wy0 = texelFetch(m_PositionMap,  uv+ivec2(0,-1), 0);
	//vec4 wx = texelFetch(m_PositionMap, uv+ivec2(1,0), 0) - wx0;
	//vec4 wy = texelFetch(m_PositionMap, uv+ivec2(0,1), 0) - wy0;

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
}*/

vec3 lutT(float alt, float mu) {
	//return texture(m_TransmittanceLut, vec2((mu+0.25f)/1.25f, alt));
	//return texture(m_TransmittanceLut, vec2((mu+0.25f)*0.8, pow(alt, 0.25))).rgb;
	return texture(m_TransmittanceLut, vec2(((sign(mu)*pow(abs(mu),0.333333333333333))+0.6)*0.625, pow(alt, 0.25))).rgb;
}
#ifdef PSSM
#import "Common/ShaderLib/Shadows.glsllib"
#ifdef FADE
uniform vec2 m_FadeInfo;
#endif
uniform vec4 m_ViewProjectionMatrixRow2;

const mat4 biasMat = mat4(0.5, 0.0, 0.0, 0.0,
                          0.0, 0.5, 0.0, 0.0,
                          0.0, 0.0, 0.5, 0.0,
                          0.5, 0.5, 0.5, 1.0);
uniform mat4 m_LightViewProjectionMatrix0;
uniform mat4 m_LightViewProjectionMatrix1;
uniform mat4 m_LightViewProjectionMatrix2;
uniform mat4 m_LightViewProjectionMatrix3;
float getShadow(vec4 worldPos, vec3 normal) {
	vec3 lightDir = m_SunDir;
	//normal backface check
	//strange this mult should can be done outside
	vec4 projCoord0 = biasMat * m_LightViewProjectionMatrix0 * worldPos;
    vec4 projCoord1 = biasMat * m_LightViewProjectionMatrix1 * worldPos;
    vec4 projCoord2 = biasMat * m_LightViewProjectionMatrix2 * worldPos;
    vec4 projCoord3 = biasMat * m_LightViewProjectionMatrix3 * worldPos;
	float shadow = 1.0;
	float shadowPosition = m_ViewProjectionMatrixRow2.x * worldPos.x +
						   m_ViewProjectionMatrixRow2.y * worldPos.y + 
						   m_ViewProjectionMatrixRow2.z * worldPos.z +
						   m_ViewProjectionMatrixRow2.w;

	shadow = getDirectionalLightShadows(m_Splits, shadowPosition,
                           m_ShadowMap0,m_ShadowMap1,m_ShadowMap2,m_ShadowMap3,
                           projCoord0, projCoord1, projCoord2, projCoord3);
	#ifdef FADE
       shadow = clamp(max(0.0,mix(shadow, 1.0 ,(shadowPosition - m_FadeInfo.x) * m_FadeInfo.y)),0.0,1.0);            
    #endif
	return shadow;
    //return shadow * m_ShadowIntensity + (1.0 - m_ShadowIntensity);
}
#endif

vec2 rsi(vec3 r0, vec3 rd, float sr) {
		// ray-sphere intersection that assumes
		// the sphere is centered at the origin.
		// No intersection when result.x > result.y
		float a = dot(rd, rd);
		float b = 2.0f * dot(rd, r0);
		float c = dot(r0, r0) - (sr * sr);
		float d = (b*b) - 4.0f*a*c;
		if (d < 0.0) return vec2(1e5,-1e5);
		return vec2(
			(-b - sqrt(d))/(2.0*a),
			(-b + sqrt(d))/(2.0*a)
		);
}

vec3 ACESFilm( vec3 x ) {
    float a = 2.51f;
    float b = 0.03f;
    float c = 2.43f;
    float d = 0.59f;
    float e = 0.14f;
    return clamp((x*(a*x+b))/(x*(c*x+d)+e), 0.0, 1.0);
}
vec3 whitePreservingLumaBasedReinhardToneMapping(vec3 color) {
	float gamma = 2.2;
	float white = 2.;
	float luma = dot(color, vec3(0.2126, 0.7152, 0.0722));
	float toneMappedLuma = luma * (1. + luma / (white*white)) / (1. + luma);
	color *= toneMappedLuma / luma;
	color = pow(color, vec3(1. / gamma));
	return color;
}
vec3 tonemap(vec3 c) {
	float range = m_ToneMapTo-m_ToneMapFrom;
	c = (c-m_ToneMapFrom)/range;
	//c = clamp(c, 0.0, 1.0);
	return 1.0-exp(-m_ToneMapScale*c);
	//return c/(c+m_ToneMapScale);
	//return whitePreservingLumaBasedReinhardToneMapping(c);
	//c = pow(c, vec3(0.454545455));
	//return ACESFilm(c);
	//return c*m_ToneMapScale;
	//return ACESFilm(c*m_ToneMapScale);
}
#define CLOUDS_FROM 2000.0
void main() {
	vec3 ray = normalize(rayDir);

	uvec4 buf = texture(m_BufferMap, texCoord);
	vec4 col = getColor(buf);
	vec3 n = getNormal(buf);
	float depth = getDepth(buf);
	
	//vec4 col = texture(m_ColorMap, texCoord);
	//vec4 n = texture(m_NormalMap, texCoord);

	//if(n.w == 0.0) {
	if(depth == 0.0) {
		//col.rgb = 1.0-exp(-col.rgb);
		
		col.rgb = tonemap(col.rgb);
		//col.rgb = pow(col.rgb, vec3(0.454545455));
		//col.rgb = whitePreservingLumaBasedReinhardToneMapping(col.rgb);
		gl_FragColor = vec4(col.rgb, 1.0);
		return;
	}

	vec3 wPos = g_CameraPosition + ray*depth;

	//n.xyz = n.xyz*2.0-1.0;

	//vec3 N = n.xyz;

	//n.xyz = getN(n.xyz);
	
	vec3 u = vec3(0.0);
	//for(int i = 0; i < m_DirLightsSize; i++) {
	for(int i = 0; i < 6; i++) {
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
	//float z = max(dot(n.xyz, m_SunDir), 0.0);
	float z = step(0.0, dot(n.xyz, m_SunDir));
	//vec2 cloudInter = rsi(wPos+vec3(0.0, m_PLANET_RAD, 0.0), vec3(0.0,1.0,0.0), m_PLANET_ATMOS_RAD);
	vec2 cloudInter = rsi(wPos+vec3(0.0, m_PLANET_RAD, 0.0), m_SunDir, m_PLANET_RAD + CLOUDS_FROM);
	//DEBUG CHECK FOR NOW
	if(cloudInter.x > cloudInter.y || cloudInter.x > 0.0) {
		u = vec3(1.0, 0.0,0.0);
	}
	else {
		vec2 coords = wPos.xz + cloudInter.y*m_SunDir.xz;
		z *= smoothstep(0.0, 0.1, m_Coverage-texture(m_CloudShadowMap, coords*0.0001 + m_CloudOffset).r);
	}
	#ifdef PSSM
		z = min(z, getShadow(vec4(wPos, 1.0), n));
	#endif

	//z = getShadow(vec4(wPos, 1.0), n);

	u += z*m_SunLight;

	
	

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

	/*#ifdef AmbientLight
		u += m_AmbientLight;
	#endif*/
	//u = 1.0-exp(-col.rgb*u);

	u = col.rgb*u;

	//Now, transmittance... & inscattered light
	float dist = depth;

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
	u += (phaseR*sumR*m_SCATTER_RAY + phaseM*sumM*m_SCATTER_MIE) * 21.0;// * 100.0;

	//gamma
	//u = pow(u, vec3(0.454545455));
	u = tonemap(u);
	gl_FragColor = vec4(u, 1.0);
	//gl_FragColor = vec4(n.xyz*0.5+0.5, 1.0);
}

