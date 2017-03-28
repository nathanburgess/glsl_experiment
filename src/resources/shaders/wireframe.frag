#version 330

in vec2 vtCoord;
in vec3 vNorm;
in vec3 vPos;

out vec4 fragColor;
out vec4 dTex;

struct Attenuation
{
    float constant;
    float linear;
    float exponent;
};

struct PointLight
{
    vec3 colour;
    vec3 position;
    float intensity;
    Attenuation att;
};

struct DirectionalLight
{
    vec3 colour;
    vec3 direction;
    float intensity;
};

struct Material
{
    vec3 colour;
    int useColour;
    float reflectance;
};

uniform sampler2D texture_sampler;
uniform vec3 ambientLight;
uniform float specularPower;
uniform Material material;
uniform PointLight pointLight;
uniform DirectionalLight directionalLight;

vec4 calcLightColour(vec3 light_colour, float light_intensity, vec3 position, vec3 to_light_dir, vec3 normal)
{
    vec4 diffuseColour = vec4(0, 0, 0, 0);
    vec4 specColour = vec4(0, 0, 0, 0);

    // Diffuse Light
    float diffuseFactor = max(dot(normal, to_light_dir), 0.0);
    diffuseColour = vec4(light_colour, 1.0) * light_intensity * diffuseFactor;

    // Specular Light
    vec3 camera_direction = normalize(-position);
    vec3 from_light_dir = -to_light_dir;
    vec3 reflected_light = normalize(reflect(from_light_dir , normal));
    float specularFactor = max( dot(camera_direction, reflected_light), 0.0);
    specularFactor = pow(specularFactor, specularPower);
    specColour = light_intensity  * specularFactor * material.reflectance * vec4(light_colour, 1.0);

    return (diffuseColour + specColour);
}

vec4 calcPointLight(PointLight light, vec3 position, vec3 normal)
{
    vec3 light_direction = light.position - position;
    vec3 to_light_dir  = normalize(light_direction);
    vec4 light_colour = calcLightColour(light.colour, light.intensity, position, to_light_dir, normal);

    // Apply Attenuation
    float distance = length(light_direction);
    float attenuationInv = light.att.constant + light.att.linear * distance + light.att.exponent * distance * distance;
    return light_colour / attenuationInv;
}

vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal)
{
    return calcLightColour(light.colour, light.intensity, position, normalize(light.direction), normal);
}


vec4 checkForIntersection()
{
    for(int i = 0; i < triangleCount; i++)
    {
        vec3 a = triangles.v0;
        vec3 b = triangles.v1;
        vec3 c = triangles.v2;

        float area = length((cross(a*b, a*c)) * 0.5f) * 2;
        float alpha = length(cross(vPos*b, vPos*c)) / area;
        float beta = length(cross(vPos*c, vPos*a)) / area;
        float gamma = 1 - alpha - beta;
        vec3 bary = vec3(alpha, beta, gamma);
        if(any(lessThan(bary, vec3(0.02))))
            return vec4(1,1,1,1);
    }
    return vec4(0);
}

void main()
{
    vec4 lights = vec4(ambientLight, 1.0);
    lights += calcDirectionalLight(directionalLight, vPos, vNorm);
    lights += calcPointLight(pointLight, vPos, vNorm);

    dTex = texture(texture_sampler, vtCoord) * vec4(material.colour,1) * material.useColour * lights * fragColor * triangleCount;

    if ( material.useColour == 1 )
        fragColor = vec4(material.colour, 1);
    else
        fragColor = texture(texture_sampler, vtCoord);

    //fragColor = checkForIntersection();

    fragColor *= lights;
}