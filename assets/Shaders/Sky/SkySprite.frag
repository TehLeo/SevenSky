#import "Shaders/Templates/Common/Compat.glsllib"

#ifdef DEFFERED2
#import "Shaders/Templates/Deffered/Deffered.glsllib"
//layout(location = 0)
out uvec4 FragColor;
#endif

#ifdef COLOR_MAP
uniform sampler2D m_ColorMap;
#endif
#ifdef COLOR
uniform vec4 m_Color;
#endif
smooth in vec2 texCoord;

void main() {
	vec4 col = vec4(1.0);
	
	#ifdef COLOR_MAP
		col *= texture(m_ColorMap, texCoord);
	#endif
	#ifdef COLOR
		col *= m_Color;
	#endif

	#ifdef DEFFERED
		gl_FragData[0] = vec4(l*col.rgb, col.a);
		gl_FragData[1] = vec4(0.0, 0.0, 0.0, 1.0);
	#elif DEFFERED2
		FragColor = pack(vec4(l*col.rgb, col.a));
	#else
		gl_FragColor = col;
	#endif
}

