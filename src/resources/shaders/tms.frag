#version 330

in vec4 vColor;
in vec2 tCoord;

out vec4 fragColor;

struct Material
{
    vec3 colour;
    int useColour;
    float reflectance;
};

uniform Material material;
uniform sampler2D texture_sampler;

void main()
{
    fragColor = vColor * texture(texture_sampler, tCoord);
}