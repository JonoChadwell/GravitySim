#version 330 core 
in vec3 fragNor;
in vec3 lightDir;
in vec3 worldPosition;
out vec4 color;

uniform vec3 MatAmb;
uniform vec3 MatDif;
uniform float MatShnOrScale;
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
      vec3 diffuse = max(MatDif * (dot(normal, light)) * LightColor * 3 / distance(LightPos, worldPosition), vec3(0.0));
      vec3 viewVector = normalize(EyePosOrTailPos - worldPosition);
      vec3 reflectionVector = normalize(-light + 2 * (dot(normal, light)) * normal);
      vec3 specular = max(
            MatSpc * pow((dot(reflectionVector, viewVector)), MatShnOrScale) * LightColor * 3
                  / (distance(LightPos, worldPosition) + distance(worldPosition, EyePosOrTailPos)),
            vec3(0.0));
      vec3 ambient = max(MatAmb, vec3(0.0));
      color = vec4(diffuse + specular + ambient, Opacity);
   } else {
      float dist = distance(EyePosOrTailPos, worldPosition);
      float sunDist = distance(LightPos, worldPosition);
      float transparency = min(Opacity * MatShnOrScale / dist / dist / sunDist / 5 - 0.02, Opacity / pow(sunDist, 0.5));
      if (transparency > 0.01) {
         color = vec4(0,0,0, transparency);
      } else {
         discard;
      }
   }
}
