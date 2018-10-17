uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_WorldMatrix;
attribute vec3 inPosition;

attribute vec3 inNormal;
varying vec3 normal;

#ifdef VERT_TexCoord
attribute vec2 inTexCoord;
varying vec2 texCoord;
#endif

#ifdef VERT_WorldPos
varying vec3 worldPos;
#endif

#ifdef VERT_WorldPosLocal
varying vec3 worldPosLocal;
#endif

#ifdef VERT_Pos
varying vec3 pos;
#endif

void main() { 
	//NormalMatrix otherwise, no rotation, no scale
	//normal = inNormal;
	#ifdef VERT_TexCoord
		texCoord = inTexCoord;
	#endif

	//rotation supported, no non uniform scales
	normal = (g_WorldMatrix*vec4(inNormal, 0.0)).xyz;

	#ifdef VERT_WorldPos
		worldPos = (g_WorldMatrix*vec4(inPosition, 1.0)).xyz;
	#endif
	#ifdef VERT_WorldPosLocal
		worldPosLocal = ((g_WorldMatrix*vec4(((inPosition)), 0.0)).xyz);
	#endif

	#ifdef VERT_Pos
		pos = inPosition;
	#endif

	gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);
}
