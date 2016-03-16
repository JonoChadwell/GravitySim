#version 330 core

uniform sampler2D tex;

in vec2 vTexCoord;

void main() {
	vec2 tc = vTexCoord;
	gl_FragColor = texture2D(tex, vec2(tc.x / 2 + 0.5, tc.y / 2 + 0.5));
}
