#import "Shaders/Templates/Common/Compat.glsllib"

uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_WorldMatrix;
attribute vec3 inPosition;

attribute vec3 inNormal;
attribute vec2 inTexCoord;
varying vec3 normal;
varying vec3 worldPos;
varying vec3 worldPosLocal;

varying vec2 texCoord;

void main() { 
	//NormalMatrix otherwise, no rotation, no scale
	//normal = inNormal;
	texCoord = inTexCoord;
	//rotation supported, no non uniform scales

	normal = (g_WorldMatrix*vec4(inNormal, 0.0)).xyz;

	worldPos = (g_WorldMatrix*vec4(inPosition, 1.0)).xyz;
	//worldPosFract = (g_WorldMatrix*vec4(fract(inPosition), 1.0)).xyz;
	worldPosLocal = ((g_WorldMatrix*vec4(((inPosition)), 0.0)).xyz);
	gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);
}
