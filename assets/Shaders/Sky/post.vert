#import "Common/ShaderLib/GLSLCompat.glsllib"

uniform mat4 g_ProjectionMatrixInverse;
uniform mat4 g_ViewMatrixInverse;

attribute vec3 inPosition;
attribute vec2 inTexCoord;

varying vec2 texCoord;
varying vec3 rayDir;
void main() { 
	/* inverse perspective projection */
	vec2 pos = inPosition.xy * 2.0 - 1.0;

	vec4 reverseVec = vec4(pos, 0.0, 1.0);
	//vec4 reverseVec = vec4(inPosition.xy, 0.0, 1.0);
	reverseVec = g_ProjectionMatrixInverse * reverseVec;
 
	/* inverse modelview, without translation */
	reverseVec.w = 0.0;
	//reverseVec = gl_ModelViewMatrixInverse * reverseVec;
	reverseVec = g_ViewMatrixInverse * reverseVec;
 
	/* send */
	rayDir = reverseVec.rgb;
    texCoord = inTexCoord;

    gl_Position = vec4(pos, 0.0, 1.0);
}