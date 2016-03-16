#version  330 core
layout(location = 0) in vec4 vertPos;
layout(location = 1) in vec3 vertNor;

out vec2 vTexCoord;

void main()
{
   gl_Position = vertPos;
   vTexCoord = vertPos.xy;
}
