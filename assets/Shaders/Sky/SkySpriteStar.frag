#import "Shaders/Templates/Common/Compat.glsllib"

#ifdef COLOR_MAP
uniform sampler2D m_ColorMap;
#endif
#ifdef COLOR
uniform vec4 m_Color;
#endif
smooth in vec2 texCoord;

#ifdef DEFFERED2
#import "Shaders/Templates/Deffered/Deffered.glsllib"
out uvec4 FragColor;
#endif

void main() {
	vec4 col = vec4( pow( max((0.5-length(texCoord-0.5)),0.0), 0.5  ) );
	
	#ifdef COLOR_MAP
		col *= texture(m_ColorMap, texCoord);
	#endif
	#ifdef COLOR
		col *= m_Color;
	#endif
	
	#ifdef DEFFERED
		gl_FragData[0] = col;
		gl_FragData[1] = vec4(0.0, 0.0, 0.0, 1.0);
		//gl_FragData[2] = vec4(0.0, 0.0, 0.0, 0.0);
	#elif DEFFERED2
		FragColor = pack(col);
	#else
		gl_FragColor = col;
	#endif
}