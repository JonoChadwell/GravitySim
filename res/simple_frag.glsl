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
uniform vec3 EyePosOrTailPos;
uniform float Opacity;
uniform int Mode;

void main()
{
   if (Mode == 0) {
      vec3 normal = normalize(fragNor);
      vec3 light = normalize(lightDir);
      vec3 diffuse = max(MatDif * (dot(normal, light)) * LightColor, vec3(0.0));
      vec3 viewVector = normalize(EyePosOrTailPos - worldPosition);
      vec3 reflectionVector = normalize(-light + 2 * (dot(normal, light)) * normal);
      vec3 specular = max(MatSpc * pow((dot(reflectionVector, viewVector)), MatShn) * LightColor, vec3(0.0));
      vec3 ambient = max(MatAmb, vec3(0.0));
      color = vec4(diffuse + specular + ambient, Opacity);
   } else {
      float dist = distance(EyePosOrTailPos, worldPosition);
      float transparency = min(Opacity / dist / 5 - 0.02, Opacity);
      if (transparency > 0) {
         color = vec4(0,0,0, transparency);
      } else {
         discard;
      }
   }
}
