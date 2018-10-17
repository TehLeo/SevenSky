uniform sampler2D m_ColorMap; 
noperspective in float density;
noperspective in vec2 extra;
void main() {
	float d = pow(density, 0.5);

	float r = texture(m_ColorMap, gl_FragCoord.xy*0.001953125).r*d;
	
	//vec3 col = vec3(1.0);
	gl_FragColor = vec4(r, extra.x, extra.y, 1.0);
}

