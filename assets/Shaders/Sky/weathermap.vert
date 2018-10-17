attribute vec3 inPosition;
attribute vec2 inTexCoord2;

attribute vec2 inTexCoord3;

noperspective out float density;
noperspective out vec2 extra;
void main() {
	density = inPosition.z;
	extra = inTexCoord2;
    gl_Position = vec4(inPosition.xy
				+inTexCoord3
			, 0.0, 1.0);
}