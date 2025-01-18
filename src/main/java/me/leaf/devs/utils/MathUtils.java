package me.leaf.devs.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class MathUtils {

    public static BlockPos toBlockPos(Vec3 vec3) {
        return new BlockPos((int) vec3.x, (int) vec3.y, (int) vec3.z);
    }

}
