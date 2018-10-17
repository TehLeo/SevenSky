attribute vec3 inPosition;
attribute vec2 inTexCoord;

#ifdef VIEW
uniform mat4 g_ViewMatrix;
noperspective out vec3 pos;
#else 
noperspective out vec2 fpPos;
#endif

void main() {
	#ifdef VIEW
		vec3 v = vec3(inTexCoord.x, inTexCoord.y, -1.0)*mat3(g_ViewMatrix);
		//pos = normalize(v.xyz);
		pos = v.xyz;
	#else 
		fpPos = inPosition.xy;
	#endif

	//vec4 pos = g_WorldViewProjectionMatrix*vec4(inPosition, 1.0);
    gl_Position = vec4(inPosition, 1.0);
}