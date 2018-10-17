#import "Shaders/Templates/Common/Compat.glsllib"

uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_WorldMatrix;
attribute vec3 inPosition;

attribute vec3 inNormal;
varying vec3 normal;
varying vec3 worldPos;

void main() { 
	//NormalMatrix otherwise, no rotation, no scale
	//normal = inNormal;

	//rotation supported, no non uniform scales
	normal = (g_WorldMatrix*vec4(inNormal, 0.0)).xyz;

	worldPos = (g_WorldMatrix*vec4(inPosition, 1.0)).xyz;
	gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);
}
