uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_ProjectionMatrix;
uniform mat4 g_ViewProjectionMatrix;
attribute vec3 inPosition;

smooth out vec2 texCoord;

void main() {	
	texCoord = inPosition.xy*0.5+0.5;
	vec4 pos = g_WorldViewProjectionMatrix*vec4(inPosition, 1.0);
    gl_Position = pos;
}