#import "Shaders/Templates/Common/Compat.glsllib"

uniform vec3 m_Range;

uniform sampler2D m_ColorMap; 
noperspective in vec2 texCoord;

void main() {
	vec3 col = texture(m_ColorMap, texCoord).rgb;

	col = (col-m_Range.x)/(m_Range.y-m_Range.x);
	col = pow(col, vec3(m_Range.z));

	gl_FragColor = vec4(col, 1.0);
}

