#import "Shaders/Templates/Common/Compat.glsllib"

attribute vec3 inPosition;
attribute float inSize;

void main() {
	//vec4 pos = g_WorldViewProjectionMatrix*vec4(inPosition, 1.0);
	//pos.w = 1.0;
	gl_PointSize = inSize;//inSize2.0;
    gl_Position = vec4(inPosition, 1.0);
}