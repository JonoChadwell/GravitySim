#version 330 core 
in vec3 fragNor;
in vec3 lightDir;
in vec3 worldPosition;
out vec4 color;

uniform vec3 MatAmb;
uniform vec3 MatDif;
uniform float MatShn;
uniform vec3 MatSpc;
uniform vec3 LightPos;
uniform vec3 LightColor;
uniform vec3 EyePos;

void main()
{
   vec3 normal = normalize(fragNor);
   vec3 light = normalize(lightDir);
   vec3 diffuse = max(MatDif * (dot(normal, light)) * LightColor, vec3(0.0));
   vec3 viewVector = normalize(EyePos - worldPosition);
   vec3 reflectionVector = normalize(-light + 2 * (dot(normal, light)) * normal);
   vec3 specular = max(MatSpc * pow((dot(reflectionVector, viewVector)), MatShn) * LightColor, vec3(0.0));
   vec3 ambient = max(MatAmb, vec3(0.0));
   //color = vec4(normal, 1.0);
   color = vec4(diffuse + specular + ambient, 1.0);
}
