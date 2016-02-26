#version 330 core 
in vec3 fragNor;
in vec3 lightDir;
in vec3 worldPosition;
in vec4 fragColor;
out vec4 color;

uniform vec3 MatAmb;
uniform vec3 MatDif;
uniform float MatShn;
uniform vec3 MatSpc;
uniform int DrawMode;
uniform vec3 LightPos;
uniform vec3 LightColor;

void main()
{
	vec3 normal = normalize(fragNor);
	vec3 light = normalize(lightDir);
	
	if (DrawMode == 1) {
	    vec3 diffuse = max(MatDif * (dot(normal, light)) * LightColor, vec3(0.0));
	    vec3 viewVector = normalize(vec3(0.0) - worldPosition);
	    vec3 reflectionVector = normalize(-light + 2 * (dot(normal, light)) * normal);
	    vec3 specular = max(MatSpc * pow((dot(reflectionVector, viewVector)), MatShn) * LightColor, vec3(0.0));
	    vec3 ambient = max(MatAmb * LightColor, vec3(0.0));
	    color = vec4(diffuse + specular + ambient, 1.0);
    } else if (DrawMode == 2) {
        color = fragColor;
    } else {
        color = vec4(normal, 1.0);
    }
}
