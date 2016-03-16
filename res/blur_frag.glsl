#version 330 core

uniform sampler2D tex;

in vec2 vTexCoord;

void main() {
   vec2 tc = vTexCoord / 2 + 0.5;
   vec3 sum = vec3(0,0,0);
   
   int x,y;
   float dist;
   for (x = -10; x < 11; x++) {
      for (y = -10; y < 11; y++) {
         dist = sqrt(x * x + y * y) + 1;
         if (dist < 11) {
            sum += max((texture2D(tex, vec2(tc.x + x / 400.0, tc.y + y / 400.0)).xyz - (0.3 + dist / 20)) / dist, vec3(0));
         }
      }
   }
   
   gl_FragColor = vec4(texture2D(tex, tc).xyz + sum / 100, 1.0) * 2;
}
