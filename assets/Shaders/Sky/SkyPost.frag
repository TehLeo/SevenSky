#define PI 3.1415926535897932384626433832795

uniform sampler2D m_ColorMap;
uniform vec3 m_SunDir;
uniform vec3 m_MoonDir;
uniform vec3 m_SunCol;
uniform vec3 m_MoonCol;
noperspective in vec3 pos;


#ifdef VIEW
noperspective in vec2 fpPos;
#endif
void main() {

	/*
	float x = 2 * ( gl_FragCoord.x + 0.5 ) / ( width  - 1 ) - 1;
    float y = 2 * ( gl_FragCoord.y + 0.5 ) / ( height - 1 ) - 1;

    float z2 = x * x + y * y; 

    if( z2 <= 1.0 )
    {
        float phi   = atan( y, x );
        float theta = acos( 1 - z2 );

        vec3 dir = vec3( sin( theta ) * cos( phi ), cos( theta ), sin( theta ) * sin( phi ) );
	*/
	vec3 dir = normalize(pos);
	#ifdef VIEW
		vec4 c1 = texture(m_ColorMap, fpPos);
		
		//c1.rgb = abs(c1.rgb-(dir.rgb*0.5+0.5));
		//c1.rgb = c1.rgb;
		//c1.rgb = dir*0.5+0.5;
		//c1.rgb *= sin(dir.y*10.0);
	#else
		float ele = asin(dir.y);
		float x = 0.5*(atan(dir.z, dir.x) + PI)/PI;

		vec4 c1 = texture(m_ColorMap, vec2(x, clamp(ele/PI+0.5, 0.0, 1.0)));
	#endif
	
	//c1 *= 20.0;
	//vec4 c1 = vec4(x, ele/PI+0.5, 0.0, 1.0);
	
	//float multInv = 1.0/(sqrt(1.0-dir.y*dir.y)/(1.0-dir.y));
	//vec2 coords = multInv*dir.xz*0.5+0.5;
	//vec4 c1 = texture(m_ColorMap, coords);


	//c1.rgb *= step(0.01, dir.y);
	float mu = dot(dir, m_SunDir);
	//c1.rgb += exp(250000.0*(mu-1.0));
	//c1.rgb += m_SunCol*exp(25000.0*(mu-1.0));
	/*c1.rgb += m_SunCol*exp(25.0*(mu-1.0));

	mu = dot(dir, m_MoonDir);
	c1.rgb += m_MoonCol*exp(25.0*(mu-1.0));*/
	//c1.rgb += m_MoonCol*exp(25000.0*(mu-1.0));
	//c1.rgb += exp(250000.0*(mu-1.0));

	//c1.a = 1.0-c1.a;
	//c1 = vec4(vec3(c1.a),1.0);

	//c1 = vec4(vec3(0.5),0.5);
	//c1.rgb = vec3(0.0,1.5,1.5);
	
	#ifdef DEFFERED
		gl_FragData[0] = c1;
		//gl_FragData[1] = vec4(0.0, 0.0, 0.0, 1.0);
		//gl_FragData[1] = vec4(0.0, 0.0, 0.0, 1.0);
	#else
		gl_FragColor = c1;
	#endif
}

