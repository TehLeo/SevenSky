uniform sampler2D m_ColorMap;
uniform vec3 g_CameraPosition;
uniform vec3 g_CameraDirection;

varying vec2 texCoord;
varying vec3 normal;
varying vec3 tangent;
varying vec3 worldPos;
varying vec3 worldPosLocal;

#ifdef DEFFERED2
#import "Shaders/Templates/Deffered/Deffered.glsllib"
//layout(location = 0)
out uvec4 FragColor;
#endif

void main() {

	vec3 col = texture(m_ColorMap, texCoord).rgb;

	#ifdef DEFFERED2
		FragColor = pack(vec4(col, 1.0), normalize(normal), distance(g_CameraPosition, worldPos));
	#else
		gl_FragColor = vec4(col, 1.0);
	#endif
}

