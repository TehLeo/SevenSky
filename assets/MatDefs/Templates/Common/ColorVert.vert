uniform mat4 g_WorldViewProjectionMatrix;
attribute vec4 inPosition;
attribute vec4 inColor;
varying vec4 vertColor;

void main() { 
    vertColor = inColor;
    gl_Position = g_WorldViewProjectionMatrix*inPosition; 
}
