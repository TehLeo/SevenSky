uniform mat4 g_ViewProjectionMatrix;
uniform vec3 g_CameraPosition;

uniform vec3 m_LookAt;
attribute vec3 inPosition;
attribute mat2x4 inInstanceData;

smooth out vec2 texCoord;
flat out vec3 wPos;
flat out float density;
flat out vec3 sphCol;
flat out float sunAtten;
smooth out vec3 rayDir;

void main() {	
	//texCoord = inPosition.xy*0.5+0.5;
	wPos = inInstanceData[0].xyz;
	texCoord = inPosition.xy;
	density = inInstanceData[0].w;
	sphCol = inInstanceData[1].rgb;
	sunAtten = inInstanceData[1].w;

	vec3 up = vec3(0.0,1.0,0.0);

	vec3 dir = normalize(m_LookAt-inInstanceData[0].xyz);
	vec3 upXdir = normalize(cross(up,dir));
	vec3 third = normalize(cross(dir,upXdir));

	mat3x3 mat = mat3x3(upXdir, third, dir);
	vec3 pos = mat*(inPosition)+inInstanceData[0].xyz;

	rayDir = pos-g_CameraPosition;
	//vec4 pos = g_WorldViewProjectionMatrix*vec4(inPosition+inInstanceData.xyz, 1.0);
	//vec4 pos = g_WorldViewProjectionMatrix*vec4(inPosition, 1.0);
    gl_Position = g_ViewProjectionMatrix*vec4(pos, 1.0);
}