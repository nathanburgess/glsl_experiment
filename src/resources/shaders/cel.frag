#version 330

in vec2 vtCoord;
in vec3 vNorm;
in vec3 vPos;

out vec4 color;
out vec4 dLight;

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

uniform Material material;
uniform PointLight pointLight;
uniform DirectionalLight directionalLight;
uniform sampler2D texture_sampler;
uniform vec3 ambientLight;
uniform float specularPower;

vec4 calcLightColour(vec3 light_colour, float light_intensity, vec3 position, vec3 to_light_dir, vec3 normal)
{
    vec4 diffuseColour = vec4(0, 0, 0, 0);
    vec4 specColour = vec4(0, 0, 0, 0);
    float diffuseFactor = max(dot(normal, to_light_dir), 0.0);
    diffuseColour = vec4(light_colour, 1.0) * light_intensity * diffuseFactor;
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
    float distance = length(light_direction);
    float attenuationInv = light.att.constant + light.att.linear * distance + light.att.exponent * distance * distance;
    return light_colour / attenuationInv;
}

vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal)
{
    return calcLightColour(light.colour, light.intensity, position, normalize(light.direction), normal);
}

void main() {
    vec4 lights = vec4(ambientLight, 0);
    lights += calcDirectionalLight(directionalLight, vPos, vNorm);
    lights += calcPointLight(pointLight, vPos, vNorm);

    float shade = dot(vec4(vNorm,1), lights);
    if (shade > 0.8) shade = 1.0;
    else if (shade > 0.5) shade = 0.66;
    else shade = 0.33;

    vec4 baseColour;
    if ( material.useColour == 1 )
        baseColour = vec4(material.colour, 1);
    else
        baseColour = texture(texture_sampler, vtCoord);

    color = baseColour * shade;
    color.a = 1;
}