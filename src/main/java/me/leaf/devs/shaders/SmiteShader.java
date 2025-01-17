package me.leaf.devs.shaders;

import com.mojang.blaze3d.vertex.PoseStack;

import me.leaf.devs.Main;
import net.minecraft.resources.ResourceLocation;
import team.lodestar.lodestone.systems.postprocess.PostProcessor;

public class SmiteShader extends PostProcessor {  
    public static final SmiteShader INSTANCE = new SmiteShader();  
    // Static block added to turn the shader off by default
    static {  
        INSTANCE.setActive(false);  
    }
    
    @Override  
	public ResourceLocation getPostChainLocation() {  
        return new ResourceLocation(Main.MODID, "shaders");  
    }
    
    @Override  
	public void beforeProcess(PoseStack poseStack) {
	
	}
	
    @Override  
	public void afterProcess() {
	
	}
}