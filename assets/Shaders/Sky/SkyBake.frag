
#define M_PI 3.1415926535897932384626433832795

//uniform float TimeOfDay; // range 0.0 -> 1.0 (0.0 = Midnight, 0.5 = Midday, etc)

//uniform float m_TimeOfDay; // = 0.4;
uniform vec3 m_SunDir;
uniform vec3 m_MoonDir;

varying vec2 fpPos;

const float RADIUS_EARTH = 6360e3;
const float RADIUS_ATMOSPHERE = 6420e3;
const float RAYLEIGH_SCALE_HEIGHT = 7994.0;
const float MIE_SCALE_HEIGHT = 1200.0;
const float SUN_INTENSITY = 20.0;
const float MOON_INTENSITY = 2.0; //1.46;

const float SUN_G = 0.76;

const vec3 betaR = vec3( 5.5e-6, 13.0e-6, 22.4e-6 );    // Rayleigh scattering coefficients at sea level
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

vec3 computeIncidentLight( in Ray r, float g, vec3 sunDirection ) {
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

    for( int i = 0; i < numSamples ; i++ )
    {
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
    return(  ( sumR * phaseR * betaR + sumM * phaseM * betaM ) );
}

void main() {
   
    //float a = mod( m_TimeOfDay - 0.5, 1.0 ) * 2.0 * M_PI;

    //sunDirection = normalize( vec3( 0.0, cos( a ), sin( a ) ) );

    //float x = 2 * ( gl_FragCoord.x + 0.5 ) / ( width  - 1 ) - 1;
    //float y = 2 * ( gl_FragCoord.y + 0.5 ) / ( height - 1 ) - 1;

    //float z2 = x * x + y * y;
	float z2 = dot(fpPos, fpPos);

    if( z2 <= 1.0 )
    {
        float phi   = atan( fpPos.y, fpPos.x );
        float theta = acos( 1.0 - z2 );

        vec3 dir = vec3( sin( theta ) * cos( phi ), cos( theta ), sin( theta ) * sin( phi ) );
        vec3 pos = vec3( 0.0, RADIUS_EARTH + 1.0, 0.0 );

		
        gl_FragColor = vec4(
			SUN_INTENSITY * computeIncidentLight(Ray(pos, normalize(dir)), SUN_G, m_SunDir) +
			MOON_INTENSITY * computeIncidentLight(Ray(pos, normalize(dir)), 0.9, m_MoonDir )

			, 1.0 );
    }
    else
    {
        gl_FragColor = vec4( 0.0 );
    }
}