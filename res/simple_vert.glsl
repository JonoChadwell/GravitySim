#version  330 core
layout(location = 0) in vec4 vertPos;
layout(location = 1) in vec3 vertNor;
uniform mat4 P;
uniform mat4 MV;
uniform vec3 MatAmb;
uniform vec3 MatDif;
uniform float MatShn;
uniform vec3 MatSpc;
uniform int DrawMode;
uniform vec3 LightPos;
uniform vec3 LightColor;
out vec3 fragNor;
out vec3 lightDir;
out vec3 worldPosition;
out vec4 fragColor;

void main()
{
	gl_Position = P * MV * vertPos;
	worldPosition = (MV * vertPos).xyz;
	lightDir = LightPos - worldPosition.xyz;
	fragNor = (MV * vec4(vertNor, 0.0)).xyz;
	
    if (DrawMode == 2) {
        vec3 light = normalize(lightDir);
        vec3 normal = normalize(fragNor);
        
        vec3 diffuse = max(MatDif * (dot(normal, light)) * LightColor, vec3(0.0));
	    vec3 viewVector = normalize(vec3(0.0) - worldPosition);
	    vec3 reflectionVector = normalize(-light + 2 * (dot(normal, light)) * normal);
	    vec3 specular = max(MatSpc * pow((dot(reflectionVector, viewVector)), MatShn) * LightColor, vec3(0.0));
	    vec3 ambient = max(MatAmb * LightColor, vec3(0.0));
	    fragColor = vec4(diffuse + specular + ambient, 1.0);
    }
}
