#import "Shaders/Templates/Common/Compat.glsllib"

#define PI 3.1415926535897932384626433832795

uniform sampler2D m_ColorMap; 
smooth in vec3 pos;

void main() {
	vec3 dir = normalize(pos);
	float ele = asin(dir.y);
	float x = 0.5*(atan(dir.z, dir.x) + PI)/PI;

	vec4 c1 = texture(m_ColorMap, vec2(x, clamp(ele/PI+0.5, 0.0, 1.0)));

	gl_FragColor = c1;
}

