attribute vec3 inPosition;
attribute vec2 inTexCoord;

noperspective out vec2 texCoord;
varying vec3 rayDir;
void main() { 
    texCoord = inTexCoord;
    gl_Position = vec4(inPosition, 1.0);
}