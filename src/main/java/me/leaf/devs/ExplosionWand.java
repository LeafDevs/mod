package me.leaf.devs;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;


import me.leaf.devs.effects.Explosion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;


public class ExplosionWand extends Item {
    public ExplosionWand() {
        super(new Item.Properties().craftRemainder(net.minecraft.world.item.Items.STICK));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        // Get the player's look vector and eye position
        double reach = 30.0D;
        Vec3 eyePosition = player.getEyePosition();
        Vec3 lookVector = player.getLookAngle();
        Vec3 endPos = eyePosition.add(lookVector.x * reach, lookVector.y * reach, lookVector.z * reach);
        
        // Perform the raycast
        BlockHitResult hitResult = level.clip(new ClipContext(eyePosition, endPos, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        Vec3 explosionPos = hitResult.getLocation();
        
        // Create visual explosion effect
        EffectRenderer.addEffect(new Explosion(level, explosionPos));
        
        if (level.isClientSide) {
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }
        
        // Get blocks in explosion radius
        int radius = 5; // Slightly smaller radius
        int spreadRadius = radius * 2; // Reduced spread radius
        
        // Pre-calculate squared distances for optimization
        double mainRadiusSq = (radius * 2) * (radius * 2);
        double spreadRadiusSq = (spreadRadius * 2) * (spreadRadius * 2);
        
        for(int x = -spreadRadius; x <= spreadRadius; x++) {
            for(int y = -radius; y <= radius; y++) { // Reduced vertical range
                for(int z = -spreadRadius; z <= spreadRadius; z++) {
                    BlockPos pos = new BlockPos(
                        (int)explosionPos.x + x,
                        (int)explosionPos.y + y, 
                        (int)explosionPos.z + z
                    );
                    
                    // Quick air check first to skip unnecessary calculations
                    if(level.getBlockState(pos).isAir()) {
                        continue;
                    }
                    
                    // Distance check optimization
                    double distanceFromCenter = pos.distToCenterSqr(explosionPos.x, explosionPos.y, explosionPos.z);
                    if(distanceFromCenter > spreadRadiusSq) {
                        continue;
                    }
                    
                    boolean inMainRadius = distanceFromCenter <= mainRadiusSq;
                    boolean inSpreadArea = distanceFromCenter <= spreadRadiusSq;
                    
                    var blockState = level.getBlockState(pos);
                    if(blockState.liquid()) {
                        continue;
                    }

                    // Simplified distance ratio calculation
                    double distanceRatio = Math.sqrt(distanceFromCenter) / (radius * 2);
                    double removalChance = 1.0 - distanceRatio;

                    // Handle special blocks with reduced checks
                    if(blockState.getBlock().defaultBlockState().is(net.minecraft.tags.BlockTags.LOGS)) {
                        if(level.random.nextFloat() < 0.4) {
                            handleBlockPlacement(level, pos, Blocks.COAL_BLOCK.defaultBlockState());
                        }
                        continue;
                    }

                    if(blockState.getBlock().defaultBlockState().is(net.minecraft.tags.BlockTags.LEAVES)) {
                        level.removeBlock(pos, false);
                        continue;
                    }

                    // Main explosion logic with optimized block handling
                    if(inMainRadius) {
                        if(level.random.nextFloat() < removalChance * 0.8) {
                            level.removeBlock(pos, false);
                        } else if(level.random.nextFloat() < 0.3) { // Reduced falling block frequency
                            Block[] charredBlocks = {
                                Blocks.NETHERRACK, 
                                Blocks.MAGMA_BLOCK,
                                Blocks.BLACKSTONE
                            };
                            Block randomBlock = charredBlocks[level.random.nextInt(charredBlocks.length)];
                            
                            FallingBlockEntity fallingBlock = FallingBlockEntity.fall(
                                level, 
                                pos,
                                randomBlock.defaultBlockState()
                            );
                            
                            if(fallingBlock != null) {
                                Vec3 direction = new Vec3(
                                    pos.getX() - explosionPos.x,
                                    pos.getY() - explosionPos.y,
                                    pos.getZ() - explosionPos.z
                                ).normalize();
                                
                                double speed = 0.6 + level.random.nextDouble() * 0.3;
                                fallingBlock.setDeltaMovement(
                                    direction.x * speed,
                                    direction.y * speed + 0.2,
                                    direction.z * speed
                                );
                            }
                            level.removeBlock(pos, false);
                        }
                    }
                    // Only check floating blocks in main radius to reduce lag
                    if(inMainRadius && level.random.nextFloat() < 0.5) {
                        checkAndHandleFloatingBlocks(level, pos.above());
                    }
                }
            }
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
     // Helper method to handle block placement and prevent floating blocks
     private void handleBlockPlacement(Level level, BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        BlockPos below = pos.below();
        if(level.getBlockState(below).isAir()) {
            // If there's air below, create a falling block
            FallingBlockEntity.fall(level, pos, state);
        } else {
            // If there's support below, place the block normally
            level.setBlock(pos, state, 3);
        }
    }

    // Helper method to check and handle floating blocks
    private void checkAndHandleFloatingBlocks(Level level, BlockPos pos) {
        BlockPos below = pos.below();
        var state = level.getBlockState(pos);
        
        if(!state.isAir() && level.getBlockState(below).isAir()) {
            // Find the first non-air block below
            BlockPos ground = below;
            while(level.getBlockState(ground).isAir() && ground.getY() > level.getMinBuildHeight()) {
                ground = ground.below();
            }
            
            if(ground.getY() > level.getMinBuildHeight()) {
                // Either create falling block or move block down
                if(ground.getY() < below.getY() - 5) {
                    FallingBlockEntity.fall(level, pos, state);
                    level.removeBlock(pos, false);
                } else {
                    level.setBlock(ground.above(), state, 3);
                    level.removeBlock(pos, false);
                }
            }
        }
    }
}