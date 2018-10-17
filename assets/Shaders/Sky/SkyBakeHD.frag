#import "Shaders/Templates/Common/Compat.glsllib"

#define M_PI 3.1415926535897932384626433832795

//uniform float TimeOfDay; // range 0.0 -> 1.0 (0.0 = Midnight, 0.5 = Midday, etc)

//uniform float m_TimeOfDay; // = 0.4;
uniform vec3 m_SunDir;
uniform vec3 m_MoonPos;

uniform vec3 m_SunIrradiance;// = vec3(677.5281);

varying vec2 fpPos;

const float RADIUS_EARTH = 6360e3;
const float RADIUS_ATMOSPHERE = 6420e3;
const float RAYLEIGH_SCALE_HEIGHT = 7994.0;
const float MIE_SCALE_HEIGHT = 1200.0;
//const float SUN_INTENSITY = 20.0; 
const float MOON_INTENSITY = 2.0*5.0; //1.46;
const float PI = 3.141592654;

//solar extraterrestrial irradiance [ W/m^2 ] intervals for visible light

const float SUN_G = 0.76;

const vec3 betaR = vec3( 5.5e-6, 13.0e-6, 22.4e-6 );    // Rayleigh scattering coefficients at sea level
//const vec3 betaM = vec3( 210e-5 );                       // Mie scattering coefficients at sea level
const vec3 betaM = vec3( 21e-6 );                       // Mie scattering coefficients at sea level

//vec3 sunDirection = vec3( 0, 1, 0 );

//const int numSamples = 16;
//const int numSamplesLight = 8;

const int numSamples = 16;
const int numSamplesLight = 8;

struct Ray
{
    vec3 o; //origin
    vec3 d; //direction (should always be normalized)
};

struct Sphere
{
    vec3 pos;   //center of sphere position
    float rad;  //radius
};

const Sphere SPHERE_EARTH      = Sphere( vec3( 0 ), RADIUS_EARTH );
const Sphere SPHERE_ATMOSPHERE = Sphere( vec3( 0 ), RADIUS_ATMOSPHERE );

bool intersect( in Ray ray, in Sphere sphere, out float t0, out float t1 ) {
    vec3 oc = ray.o - sphere.pos;
    float b = 2.0 * dot(ray.d, oc);
    float c = dot(oc, oc) - sphere.rad*sphere.rad;
    float disc = b * b - 4.0 * c;

    if (disc < 0.0)
        return false;

   float q;
    if (b < 0.0)       
		q = (-b - sqrt(disc))/2.0;  
    else  
		q = (-b + sqrt(disc))/2.0;  
    t0 = q; 
    t1 = c / q;     // make sure t0 is smaller than t1     
if (t0 > t1) {
        // if t0 is bigger than t1 swap them around
        float temp = t0;
        t0 = t1;
        t1 = temp;
	}

    // if t1 is less than zero, the object is in the ray's negative direction
    // and consequently the ray misses the sphere
    if (t1 < 0.0)
        return false;

    if( t0 < 0.0 ) {
        t0 = 0;
    }

    return ( true );
}

/*vec3 getColor(in Ray r, float g, vec3 sunDirection ) {
	float t0, t1;

    if( !intersect( r, SPHERE_ATMOSPHERE, t0, t1 ) ) {
        return vec3( 1.0 );
    }
	float airmass = t1 / 60000.0;

	vec3 B = r.o + r.d * t1; 
	if( !intersect( Ray(r.o, sunDirection), SPHERE_ATMOSPHERE, t0, t1 ) ) {
        return vec3( 1.0 );
    }
	vec3 C = r.o + sunDirection*t1;
	vec3 A = r.o;

	float ABC = 0.5*length(cross(B-A, C-A));
	float OBC = 0.5*length(cross(B,C));
	float angle = abs(asin(length(cross(normalize(B), normalize(C)))));
	float BOCArea = PI * RADIUS_ATMOSPHERE * RADIUS_ATMOSPHERE * angle / ( 2.0 * PI);
	float area = ABC + BOCArea - OBC;

	float mu = dot( r.d, sunDirection );
    float phaseR = 3.0 / ( 16.0 * M_PI ) * ( 1.0 + mu * mu );
    float phaseM = 3.0 / (  8.0 * M_PI ) * ( ( 1.0 - g * g ) * ( 1.0 + mu * mu ) ) / ( ( 2.0 + g * g ) * pow( 1.0 + g * g - 2.0 * g * mu, 1.5 ) );

	area *= 0.01 ;
	//area = abs(ABC*0.001) ;


	vec3 extR = exp(-betaR * airmass * 70000.0);

	float ray = area / RAYLEIGH_SCALE_HEIGHT;
	float mie = area / MIE_SCALE_HEIGHT;
	mie *= 0.1;

	vec3 rad = (  (  ray * phaseR * betaR +  mie * phaseM * betaM ) );
	//vec3 rad = ray * phaseR * betaR ; 

	//km to airmass
	vec3 sun = vec3(1.0) * exp(-betaR * airmass * 30000.0);

	rad += abs(sun * pow(max(mu, 0.0), 1024.0));

	return rad;
}*/
vec3 getColor(in Ray r, float g, vec3 sunDirection ) {
	const vec3 BETA_S_R = vec3(5.8e-6, 13.5e-6,33.1e-6); //wave 680,550,440
	const float BETA_S_M = 210e-5;
	const float BETA_S_M_DIV_BETA_E_M = 0.9;

	float t0, t1;

    if( !intersect( r, SPHERE_ATMOSPHERE, t0, t1 ) ) {
        return vec3( 1.0 );
    }
	float airmass = t1 / 60000.0;

	vec3 B = r.o + r.d * t1; 
	if( !intersect( Ray(r.o, sunDirection), SPHERE_ATMOSPHERE, t0, t1 ) ) {
        return vec3( 1.0 );
    }
	vec3 C = r.o + sunDirection*t1;
	vec3 A = r.o;

	float ABC = 0.5*length(cross(B-A, C-A));
	float OBC = 0.5*length(cross(B,C));
	float angle = abs(asin(length(cross(normalize(B), normalize(C)))));
	float BOCArea = PI * RADIUS_ATMOSPHERE * RADIUS_ATMOSPHERE * angle / ( 2.0 * PI);
	float area = ABC + BOCArea - OBC;

	float mu = dot( r.d, sunDirection );
    float phaseR = 3.0 / ( 16.0 * M_PI ) * ( 1.0 + mu * mu );
    float phaseM = 3.0 / (  8.0 * M_PI ) * ( ( 1.0 - g * g ) * ( 1.0 + mu * mu ) ) / ( ( 2.0 + g * g ) * pow( 1.0 + g * g - 2.0 * g * mu, 1.5 ) );

	area *= 0.01 ;
	//area = abs(ABC*0.001) ;


	vec3 extR = exp(-betaR * airmass * 70000.0);

	float ray = area / RAYLEIGH_SCALE_HEIGHT;
	float mie = area / MIE_SCALE_HEIGHT;
	mie *= 0.1;

	vec3 rad = (  (  ray * phaseR * betaR +  mie * phaseM * betaM ) );
	//vec3 rad = ray * phaseR * betaR ; 

	//km to airmass
	vec3 sun = vec3(1.0) * exp(-betaR * airmass * 30000.0);

	rad += abs(sun * pow(max(mu, 0.0), 1024.0));

	return rad;
}


float Pr(float u) {
	// u is cos0, [-1,1] / dot product
	// Expanded (3.0 / 8.0 ) * ( 1.0 + u * u )
	return 0.375 + 0.375*u*u;
}
vec3 getColor2(in Ray r, float g, vec3 sunDirection, float sunInt ) {
	const vec3 BETA_S_R = vec3(5.8e-6, 13.5e-6,33.1e-6); //wave 680,550,440
	//const float BETA_S_M = 210e-5;
	const float BETA_S_M = 210e-5 ;
	const float BETA_S_M_DIV_BETA_E_M = 0.9;

	float t0, t1;

    if( !intersect( r, SPHERE_ATMOSPHERE, t0, t1 ) ) {
        return vec3( 1.0 );
    }
	int reps = 8;

	float len = ( t1 - t0 ) / 8;
    float t = t0 + 0.5 * len;

	float airmass = t1 / 60000.0;
	float mu = dot( r.d, sunDirection );
	// Fex(s)

	vec3 Bex = BETA_S_R + BETA_S_M;

	//float phaseR = (3.0 / ( 16.0 * M_PI )) * ( 1.0 + mu * mu );
	//normalized over [-1,1]
	float phaseR = (3.0 / 8.0 ) * ( 1.0 + mu * mu );
    float phaseM = (3.0 / (  8.0 * M_PI )) * ( ( 1.0 - g * g ) * ( 1.0 + mu * mu ) ) / ( ( 2.0 + g * g ) * pow( 1.0 + g * g - 2.0 * g * mu, 1.5 ) );

	float s = airmass * 60000.0;
	vec3 Fex = exp(-Bex * s);
	vec3 L0 = vec3(1.0);

	vec3 Esun = L0;//??

	
	vec3 Bsc = BETA_S_R*phaseR + BETA_S_M*phaseM;

	//vec3 Lin = (1.0/Bex) * Esun * Bsc * (1.0 - Fex);
	vec3 Lin = ((phaseR+phaseM)/Bex) * Esun * Bsc * (1.0 - Fex);
	//vec3 L = L0 * Fex + Lin;
	vec3 L = vec3(0.0);

	vec3 tr = vec3(0.0);
	vec3 tm = vec3(0.0);

	float dhr = 0.0;
	float dhm = 0.0;
	for(int i = 0; i < 16; i++) {
		vec3 pos = r.o + r.d * t;
		float h = length(pos) - RADIUS_EARTH;

		float hr = exp( -h / RAYLEIGH_SCALE_HEIGHT ) * len;
        float hm = exp( -h / MIE_SCALE_HEIGHT      ) * len;

		dhr += hr;
		dhm += hm;


		Ray lightRay = Ray(pos,sunDirection);
        float lmin, lmax;
        intersect( lightRay, SPHERE_ATMOSPHERE, lmin, lmax );
		float emin, emax;
		bool earth = intersect( lightRay, SPHERE_EARTH, emin, emax );

		vec3 pos2 = lightRay.o + lightRay.d * 0.5;
        float h2 = length(pos2) - RADIUS_EARTH;

		float depR = exp( -h2 / RAYLEIGH_SCALE_HEIGHT ) * lmax;
		float depM = exp( -h2 / MIE_SCALE_HEIGHT      ) * lmax;

		vec3 att = exp(-( (dhr+depR)*betaR + (dhm+depM)*betaM) );

		if(!earth) {
			tr += hr * att;
			tm += hm * att;
		}
		else {
			
		}
		t += len;
	}
	L = ( tr * phaseR * betaR + tm * phaseM * betaM );

	//L += Fex * pow(max(mu,0.0), 1024.0);

	//L += sunInt*exp(250000.0*(mu-1.0));

	L *= 0.5;

	return L;
}
vec3 getColorMoon(in Ray r, float g, vec3 moonPos, float sunInt ) {
	//const vec3 BETA_S_R = vec3(5.8e-6, 13.5e-6,33.1e-6); //wave 680,550,440
	//const float BETA_S_M = 210e-5;
	//const float BETA_S_M = 210e-5 ;
	//const float BETA_S_M_DIV_BETA_E_M = 0.9;

	float t0, t1;

    if( !intersect( r, SPHERE_ATMOSPHERE, t0, t1 ) ) {
        return vec3( 1.0 );
    }
	int reps = 8;

	float len = ( t1 - t0 ) / 8;
    float t = t0 + 0.5 * len;

	float airmass = t1 / 60000.0;
	// Fex(s)

	//vec3 Bex = BETA_S_R + BETA_S_M;

	//float phaseR = (3.0 / ( 16.0 * M_PI )) * ( 1.0 + mu * mu );
	//normalized over [-1,1]
	

	//float s = airmass * 60000.0;
	//vec3 Fex = exp(-Bex * s);
	//vec3 L0 = vec3(1.0);

	//vec3 Esun = L0;//??

	
	//vec3 Bsc = BETA_S_R*phaseR + BETA_S_M*phaseM;

	//vec3 Lin = (1.0/Bex) * Esun * Bsc * (1.0 - Fex);
	//vec3 Lin = ((phaseR+phaseM)/Bex) * Esun * Bsc * (1.0 - Fex);
	//vec3 L = L0 * Fex + Lin;
	vec3 L = vec3(0.0);

	vec3 tr = vec3(0.0);
	vec3 tm = vec3(0.0);

	float dhr = 0.0;
	float dhm = 0.0;
	for(int i = 0; i < 16; i++) {
		vec3 pos = r.o + r.d * t;
		float h = length(pos) - RADIUS_EARTH;

		float hr = exp( -h / RAYLEIGH_SCALE_HEIGHT ) * len;
        float hm = exp( -h / MIE_SCALE_HEIGHT      ) * len;

		dhr += hr;
		dhm += hm;


		Ray lightRay = Ray(pos,normalize(moonPos-pos));
        float lmin, lmax;
        intersect( lightRay, SPHERE_ATMOSPHERE, lmin, lmax );
		float emin, emax;
		bool earth = intersect( lightRay, SPHERE_EARTH, emin, emax );

		vec3 pos2 = lightRay.o + lightRay.d * 0.5;
        float h2 = length(pos2) - RADIUS_EARTH;

		float depR = exp( -h2 / RAYLEIGH_SCALE_HEIGHT ) * lmax;
		float depM = exp( -h2 / MIE_SCALE_HEIGHT      ) * lmax;

		vec3 att = exp(-( (dhr+depR)*betaR + (dhm+depM)*210e-5) );
		//vec3 att = exp(-( (dhr+depR)*betaR + (dhm+depM)*betaM) );

		float mu = dot( r.d, lightRay.d );
		float phaseR = (3.0 / 8.0 ) * ( 1.0 + mu * mu );
		float phaseM = (3.0 / (  8.0 * M_PI )) * ( ( 1.0 - g * g ) * ( 1.0 + mu * mu ) ) / ( ( 2.0 + g * g ) * pow( 1.0 + g * g - 2.0 * g * mu, 1.5 ) );

		if(!earth) {
			tr += hr * att * phaseR;
			tm += hm * att * phaseM;
		}
		else {
			
		}
		t += len;
	}
	//L = ( tr * betaR + tm * betaM );
	L = ( tr * betaR + tm * 210e-5 );

	//L += Fex * pow(max(mu,0.0), 1024.0);
	//L += sunInt*exp(5000.0*(mu-1.0));

	return L;
}

vec3 computeIncidentLight( in Ray r, float g, vec3 sunDirection, out float T) {
    float t0, t1;

    if( !intersect( r, SPHERE_ATMOSPHERE, t0, t1 ) )
    {
        return vec3( 1.0 );
    }

    float segmentLength = ( t1 - t0 ) / numSamples;
    float tCurrent = t0;

    vec3 sumR = vec3( 0.0 );
    vec3 sumM = vec3( 0.0 );

    float opticalDepthR = 0.0;
    float opticalDepthM = 0.0;

    float mu = dot( r.d, sunDirection );
    float phaseR = 3.0 / ( 16.0 * M_PI ) * ( 1.0 + mu * mu );
    float phaseM = 3.0 / (  8.0 * M_PI ) * ( ( 1.0 - g * g ) * ( 1.0 + mu * mu ) ) / ( ( 2.0 + g * g ) * pow( 1.0 + g * g - 2.0 * g * mu, 1.5 ) );

    for( int i = 0; i < numSamples ; i++ ) {
        vec3    samplePosition = r.o + r.d * ( tCurrent + 0.5 * segmentLength );
        float   height = length( samplePosition ) - RADIUS_EARTH;

        // compute optical depth for light

        float hr = exp( -height / RAYLEIGH_SCALE_HEIGHT ) * segmentLength;
        float hm = exp( -height / MIE_SCALE_HEIGHT      ) * segmentLength;

        opticalDepthR += hr;
        opticalDepthM += hm;

        // light optical depth

        Ray lightRay = Ray( samplePosition, sunDirection );

        float lmin, lmax;

        intersect( lightRay, SPHERE_ATMOSPHERE, lmin, lmax );

        float segmentLengthLight = lmax / numSamplesLight;
        float tCurrentLight = 0;
        float opticalDepthLightR = 0;
        float opticalDepthLightM = 0;
        
        int j = 0;

        for( ; j < numSamplesLight ; j++ )
        {
            vec3 samplePositionLight = lightRay.o + lightRay.d * ( tCurrentLight + 0.5 * segmentLengthLight );

            float heightLight = length( samplePositionLight ) - RADIUS_EARTH;

            if( heightLight < 0 )
            {
                break;
            }

            opticalDepthLightR += exp( -heightLight / RAYLEIGH_SCALE_HEIGHT ) * segmentLengthLight;
            opticalDepthLightM += exp( -heightLight / MIE_SCALE_HEIGHT      ) * segmentLengthLight;

            tCurrentLight += segmentLengthLight;
        }

        if( j == numSamplesLight ) {
            vec3 tau = betaR * ( opticalDepthR + opticalDepthLightR ) + betaM * 1.1 * ( opticalDepthM + opticalDepthLightM );
            vec3 attenuation = exp( -tau );

            sumR += hr * attenuation;
            sumM += hm * attenuation;
        }
        tCurrent += segmentLength;
    }
	T = exp( -(13.0e-6*opticalDepthR+21.0e-5*opticalDepthM) );
    return(  ( sumR * phaseR * betaR + sumM * phaseM * betaM ) );
}

vec3 ACESFilm( vec3 x ) {
    float a = 2.51f;
    float b = 0.03f;
    float c = 2.43f;
    float d = 0.59f;
    float e = 0.14f;
    return clamp((x*(a*x+b))/(x*(c*x+d)+e), 0.0, 1.0);
}
void main() {
   
    //float a = mod( m_TimeOfDay - 0.5, 1.0 ) * 2.0 * M_PI;

    //sunDirection = normalize( vec3( 0.0, cos( a ), sin( a ) ) );

    //float x = 2 * ( gl_FragCoord.x + 0.5 ) / ( width  - 1 ) - 1;
    //float y = 2 * ( gl_FragCoord.y + 0.5 ) / ( height - 1 ) - 1;

    //float z2 = x * x + y * y;

	float z2 = dot(fpPos, fpPos);

    if( z2 <= 1.0 ) {
        //float phi   = atan( fpPos.y, fpPos.x );
        //float theta = acos( 1.0 - z2 );
        //vec3 dir = vec3( sin( theta ) * cos( phi ), cos( theta ), sin( theta ) * sin( phi ) );
		
		float xzLen = sqrt(z2);
		float rad = 1.0-xzLen;
		float mult = sqrt(1.0 - rad*rad)/(xzLen);
		vec3 dir = vec3(fpPos.x*mult, rad, fpPos.y*mult);
		

		//vec3 dir = vec3(fpPos.x, sqrt(1.0-fpPos.x*fpPos.x-fpPos.y*fpPos.y), fpPos.y);


		vec3 pos = vec3( 0.0, RADIUS_EARTH + 1.0, 0.0 );

		
      /*  gl_FragColor = vec4(
			SUN_INTENSITY * computeIncidentLight(Ray(pos, normalize(dir)), SUN_G, m_SunDir) +
			MOON_INTENSITY * computeIncidentLight(Ray(pos, normalize(dir)), 0.9, m_MoonDir )

			, 1.0 );*/
		float T;
        vec3 col =
			m_SunIrradiance*	computeIncidentLight(Ray(pos, normalize(dir)), SUN_G, m_SunDir, T);

			/*m_SunIrradiance **/ //getColor2(Ray(pos, normalize(dir)), SUN_G, m_SunDir, 50.0);
			//+
			//MOON_INTENSITY * getColorMoon(Ray(pos, normalize(dir)), 0.9, m_MoonPos+pos, 5.0);
			//MOON_INTENSITY * getColorMoon(Ray(pos, normalize(dir)), 0.9, m_SunDir*384400e3+pos, 1.0);
			
		//vec3 col = vec3(0.5*step(mod(theta,0.2),0.1) + 0.5*step(mod(phi,0.2),0.1));
		//col *= 0.02;
		//col = 1.0 - exp(-1.0 * col * 0.1);
		//col = ACESFilm(col);

		//gl_FragColor = vec4(col, 1.0);
		gl_FragColor = vec4(col, T);
    }
    else {
        gl_FragColor = vec4( 0.0 );
    }
}