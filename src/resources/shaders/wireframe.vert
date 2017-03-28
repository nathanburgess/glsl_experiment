#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;
layout (location=2) in vec3 vertexNormal;

struct triangle {
  vec3 v0;
  vec3 v1;
  vec3 v2;
};

layout(location=3) in triangle[] inTriangles;

out vec2 vtCoord;
out vec3 vNorm;
out vec3 vPos;
out triangle[] triangles;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;

void main()
{
    vec4 mvPos = modelViewMatrix * vec4(position, 1.0);
    gl_Position = projectionMatrix * mvPos;
    vtCoord = texCoord;
    vNorm = normalize(modelViewMatrix * vec4(vertexNormal, 0.0)).xyz;
    vPos = mvPos.xyz;
    triangles = inTriangles;
}