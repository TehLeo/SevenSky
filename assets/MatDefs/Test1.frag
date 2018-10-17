#define PI 3.1415926535897932384626433832795

smooth in vec3 pos;


#ifdef VIEW
noperspective in vec2 fpPos;
#endif
void main() {

	
	vec3 dir = normalize(pos);
	


	gl_FragColor = vec4(dir*0.5+0.5, 1.0);
}

