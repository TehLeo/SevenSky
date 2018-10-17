#import "Shaders/Templates/Common/Compat.glsllib"

uniform sampler2D m_ColorMap; 
uniform float m_Alpha;
noperspective in vec2 texCoord;

void main() {
	vec4 col = texture(m_ColorMap, texCoord);
	col.a = m_Alpha;
	gl_FragColor = col;
}
