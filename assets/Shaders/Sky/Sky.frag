varying vec3 oF_ex;
varying vec3 oL_in;
void main() {
	vec3 L_0 = vec3(0.0);
	vec3 L = L_0 * oF_ex + oL_in;
	gl_FragColor = vec4(L, 1.0);
}

