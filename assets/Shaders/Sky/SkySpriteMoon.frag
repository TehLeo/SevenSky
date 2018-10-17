#import "Shaders/Templates/Common/Compat.glsllib"

uniform sampler2D m_ColorMap;
//Sun Dir relative to Moon Dir
uniform vec3 m_SunMoonDir;
uniform vec3 m_MoonDir;

smooth in vec2 texCoord;

#ifdef DEFFERED2
#import "Shaders/Templates/Deffered/Deffered.glsllib"
//layout(location = 0)
out uvec4 FragColor;
#endif


/*vec4 quat(vec3 d, vec3 z) {
	vec4 quat = vec4(cross(z,d),
		sqrt(dot(z,z)*dot(d,d)) + dot(z,d));
	//quat *= inversesqrt(dot(quat,quat));
	quat = normalize(quat);
	return quat;
}
vec3 qmult(vec4 q, vec3 v) { 
	return v + 2.0*cross(cross(v, q.xyz) + q.w*v, q.xyz);
}*/
void main() {
	vec4 col = texture(m_ColorMap, texCoord);
	vec2 fpPos = texCoord*2.0-1.0;
	vec3 dir = vec3(fpPos.x, sqrt(1.0 - length(fpPos)), fpPos.y);

	//vec4 q = quat(vec3(0.0,1.0,0.0), m_MoonDir);
	//dir = qmult(q, dir);

	//float l = max(dot(dir, m_SunMoonDir), 0.0);
	//l = pow(l, 0.7);
	float l = 1.0;

	l = l*1.2+0.1;

	#ifdef DEFFERED
		gl_FragData[0] = vec4(l*col.rgb, col.a);
		gl_FragData[1] = vec4(0.0, 0.0, 0.0, 1.0);
	#elif DEFFERED2
		FragColor = pack(vec4(l*col.rgb, col.a));
	#else
		gl_FragColor = vec4(l*col.rgb, col.a);
	#endif
}

