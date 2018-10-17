#define PI 3.1415926535897932384626433832795

uniform sampler2D m_ColorMap; 
uniform sampler2D m_MilkyWay;
noperspective in vec2 texCoord;
noperspective in vec3 rayDir;


#ifdef DEFFERED2
#import "Shaders/Templates/Deffered/Deffered.glsllib"
//layout(location = 0)
out uvec4 FragColor;
#endif

/*
ivec2 t = ivec2(gl_FragCoord.xy*0.5);
float w = 0;
vec3 sum = vec3(0.0);

void add(int x, int y, float w0) {
	vec3 col = texelFetch(m_ColorMap, ivec2(t.x+x,t.y+y), 0).rgb;
	//float dep = texelFetch(m_NormalMap, ivec2(t.x+x,t.y+y), 0).w;
	//vec3 pos = texelFetch(m_PositionMap, ivec2(t.x+x,t.y+y), 0).rgb;

	//w0 = step(distance(d,pos),1.0);
	//w0 = w0*step(abs(d-dep), 0.95);

	//sum += col*w0;
	w += w0;
	sum += col*w0;
}

vec3 smoothTex() {
	float v1 = 0.125;
	float v2 = 0.0625;

	add(0,0,0.25);

	add(0,1,v1);
	add(1,0,v1);
	add(-1,0,v1);
	add(0,-1,v1);

	add(1,1,v2);
	add(1,-1,v2);
	add(-1,1,v2);
	add(-1,-1,v2);

	return sum/w;
}*/

void main() {
	//vec4 col = texture(m_ColorMap, texCoord);
	//vec4 col = vec4(smoothTex(), 1.0);
	//vec4 col = test();
	//vec4 col = vec4(texelFetch(m_ColorMap, t, 1).rgb, 1.0);
	vec4 col = textureLod(m_ColorMap, texCoord, 1.0);

	#ifdef MILKY_WAY

		vec3 dir = normalize(rayDir);
		float ele = asin(dir.y);
		float x = 0.5*(atan(dir.z, dir.x) + PI)/PI;
		vec4 buf = texture(m_MilkyWay, vec2(x, clamp(ele/PI+0.5, 0.0, 1.0)));

		col.rgb = col.rgb*(col.a) + buf.rgb*(1.0-col.a);
		//col.rgb += buf.rgb;

	#endif

	//vec4 col = vec4(0.0,0.5,0.4,1.0);
	//col.a = 1.0;

	#ifdef DEFFERED
		gl_FragData[0] = col;
		//gl_FragData[1] = vec4(0.0, 0.0,0.0,0.0);
		//gl_FragData[2] = vec4(0.0,0.0,0.0,0.0);	
		//gl_FragData[2] = vec4(worldPos,0.0);	
	#elif DEFFERED2
		FragColor = pack(col);
	#else
		gl_FragColor = col;
	#endif
}
