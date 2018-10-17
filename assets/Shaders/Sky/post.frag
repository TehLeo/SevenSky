#import "Common/ShaderLib/GLSLCompat.glsllib"

uniform vec2 g_FrustumNearFar;

uniform sampler2D m_Texture; 
uniform sampler2D m_DepthTexture; 
uniform sampler2D m_TransmittanceLut;
uniform sampler2D m_CloudShadowMap;
varying vec2 texCoord;
varying vec3 rayDir;

uniform	float m_Coverage;

uniform vec3 m_SunDir;
uniform	vec3 m_SunLight;
uniform vec3 g_CameraPosition;

uniform float m_ToneMapScale;
uniform float m_ToneMapFrom;
uniform float m_ToneMapTo;

uniform vec2 m_CloudOffset;
uniform float m_PLANET_RAD;
uniform float m_PLANET_ATMOS_RAD;

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

#define CLOUDS_FROM 2000.0
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

vec3 lutT(float alt, float mu) {
	return texture2D(m_TransmittanceLut, vec2(((sign(mu)*pow(abs(mu),0.333333333333333))+0.6)*0.625, pow(alt, 0.25))).rgb;
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

void main() {
	vec3 ray = normalize(rayDir);
	vec4 col = texture2D(m_Texture, texCoord);
	float depthN = texture2D(m_DepthTexture, texCoord).r;
	//col.rgb *= texCoord.y;

	if(depthN == 0.0) {
		col.rgb = tonemap(col.rgb);
		gl_FragColor = col;
		return;
	}

	float a = g_FrustumNearFar.y / (g_FrustumNearFar.y - g_FrustumNearFar.x);
    float b = g_FrustumNearFar.y * g_FrustumNearFar.x / (g_FrustumNearFar.x - g_FrustumNearFar.y);
    float dist = b / (depthN - a);

	vec3 wPos = g_CameraPosition + ray*dist;

	float z = 1.0;
	/*//vec2 cloudInter = rsi(wPos+vec3(0.0, m_PLANET_RAD, 0.0), vec3(0.0,1.0,0.0), m_PLANET_ATMOS_RAD);
	vec2 cloudInter = rsi(wPos+vec3(0.0, m_PLANET_RAD, 0.0), m_SunDir, m_PLANET_RAD + CLOUDS_FROM);
	//DEBUG CHECK FOR NOW
	if(cloudInter.x > cloudInter.y || cloudInter.x > 0.0) {
		col.rgb = vec3(1.0, 0.0,0.0);
	}
	else {
		//vec2 coords = wPos.xz + cloudInter.y*m_SunDir.xz;
		//z *= smoothstep(0.0, 0.1, m_Coverage-texture2D(m_CloudShadowMap, coords*0.0001 + m_CloudOffset).r);
	}
	#ifdef PSSM
		z = min(z, getShadow(vec4(wPos, 1.0), n));
	#endif*/

	//z = getShadow(vec4(wPos, 1.0), n);

	//col.rgb += z*m_SunLight;


/*

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

	col.rgb *= exp(-(m_SCATTER_RAY*sumR + m_SCATTER_MIE*sumM));
	col.rgb += (phaseR*sumR*m_SCATTER_RAY + phaseM*sumM*m_SCATTER_MIE) * 21.0;// * 100.0;*/
	
	col.rgb = tonemap(col.rgb);
	gl_FragColor = col;
}
