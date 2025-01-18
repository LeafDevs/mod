#version 150

uniform sampler2D DiffuseSampler;
uniform vec2 OutSize;
uniform float Time;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);
    
    // Calculate distance from edge
    float dist = length(color.rgb);
    
    // Create pulsing glow effect
    float glowIntensity = 2.0 + sin(Time * 2.0) * 0.5;
    vec3 glowColor = vec3(0.5, 0.2, 0.8); // Purple glow
    
    // Apply glow based on distance and intensity
    vec3 finalColor = color.rgb + glowColor * (1.0 - dist) * glowIntensity;
    
    // Adjust glow effect for cylinder
    float edgeFactor = smoothstep(0.0, 0.1, dist); // Smooth transition at edges
    finalColor *= edgeFactor;
    
    fragColor = vec4(finalColor, color.a);
}