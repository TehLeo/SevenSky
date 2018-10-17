attribute vec3 inPosition;

varying vec2 fpPos;
void main() {
	fpPos = inPosition.xy;
	//vec4 pos = g_WorldViewProjectionMatrix*vec4(inPosition, 1.0);
    gl_Position = vec4(inPosition, 1.0);
}