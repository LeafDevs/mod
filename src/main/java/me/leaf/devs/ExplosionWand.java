package me.leaf.devs;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import team.lodestar.lodestone.registry.common.particle.LodestoneParticleRegistry;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;

import java.awt.Color;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ExplosionWand extends Item {
    private BlockPos savedLocation;

    public ExplosionWand() {
        super(new Item.Properties());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            // Get player eye position and look vector
            Vec3 eyePosition = player.getEyePosition();
            Vec3 lookVector = player.getLookAngle();
            // Calculate end point of ray (100 blocks away)
            Vec3 endPos = eyePosition.add(lookVector.scale(100.0D));
            
            // Do the ray trace
            BlockHitResult hitResult = level.clip(new ClipContext(
                eyePosition,
                endPos,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                player
            ));
            
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockPos pos = hitResult.getBlockPos();
                BlockState state = level.getBlockState(pos);
                
                // Check if the block is not air, water, or lava
                if (!state.isAir() && !state.liquid()) {
                    savedLocation = pos;
                    player.displayClientMessage(net.minecraft.network.chat.Component.literal("Location saved at: " + pos.toShortString()), true);
                    
                    // Render a beam from the sky at a 30 degree slope pointing at the saved position
                    Vec3 beamStart = new Vec3(pos.getX() + 0.5, level.getMaxBuildHeight(), pos.getZ() + 0.5);
                    Vec3 beamEnd = new Vec3(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                    double angle = Math.toRadians(30);
                    beamEnd = beamEnd.add(0, Math.tan(angle) * (pos.getY() - beamStart.y), 0);
                
                }
            }
        }
        
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}