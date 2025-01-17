#version 150

uniform sampler2D DiffuseSampler;
uniform float Time;
uniform vec2 OutSize;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);
    
    // Flash intensity based on time
    float flashIntensity = max(0.0, 1.0 - (Time * 2.0));
    flashIntensity *= flashIntensity; // Square it for smoother falloff
    
    // Add white flash, stronger near the top of the screen
    float heightFactor = 1.0 - texCoord.y; // More intense at top
    float flash = flashIntensity * heightFactor * 0.8;
    
    // Mix original color with flash
    vec3 flashColor = mix(color.rgb, vec3(1.0, 1.0, 1.0), flash);
    
    // Add slight blue tint to the flash
    vec3 tintedFlash = mix(flashColor, vec3(0.8, 0.8, 1.0), flash * 0.3);
    
    fragColor = vec4(tintedFlash, color.a);
}
