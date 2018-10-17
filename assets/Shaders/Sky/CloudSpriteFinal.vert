uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_ViewProjectionMatrix;
uniform mat4 g_WorldMatrix;
uniform vec3 g_CameraPosition;
uniform vec3 g_CameraLeft;
uniform vec3 g_CameraUp;
uniform vec3 m_SizeInv;

attribute vec3 inPosition;
smooth out vec2 texCoord;
smooth out vec3 rayDir;

/*uniform mat4 g_ViewProjectionMatrix;

uniform vec3 m_LookAt;

flat out vec3 wPos;
flat out float density;
flat out vec3 sphCol;
flat out float sunAtten;
*/

void main() {	
	//texCoord = inPosition.xy*0.5+0.5;
	/*wPos = inInstanceData[0].xyz;
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
	*/

	texCoord = inPosition.xy;
	//rayDir = inPosition-g_CameraPosition;
	//gl_Position = g_WorldViewProjectionMatrix*vec4(inPosition, 1.0);

	//vec3 point = (g_WorldMatrix*vec4(inInstanceData[0].xyz, 1.0)).xyz;
	vec3 point = (g_WorldMatrix*vec4(vec3(0.0), 1.0)).xyz;
	//scale len(w[0]), len(w[1]), len(w[2])

	vec3 size = 1.0/m_SizeInv;

	vec3 pos = g_CameraLeft*inPosition.x*size
				 + g_CameraUp*inPosition.y*size;
	//pos = point + inInstanceData[0].w*pos;
	pos = point + 1.0*pos;

	//worldPos = pos;

	rayDir = pos-g_CameraPosition;
    gl_Position = g_ViewProjectionMatrix * vec4(pos, 1.0);

}