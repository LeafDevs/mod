package me.leaf.devs;

import me.leaf.devs.effects.VFXTest;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import me.leaf.devs.effects.Smite;

public class SmiteWand extends Item {
    public SmiteWand() {
        super(new Item.Properties().craftRemainder(net.minecraft.world.item.Items.STICK));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        // Get the player's look vector and eye position
        double reach = 150.0D;
        Vec3 eyePosition = player.getEyePosition();
        Vec3 lookVector = player.getLookAngle();
        Vec3 endPos = eyePosition.add(lookVector.x * reach, lookVector.y * reach, lookVector.z * reach);
        
        // Perform the raycast
        BlockHitResult hitResult = level.clip(new ClipContext(eyePosition, endPos, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        Vec3 targetPos = hitResult.getLocation();
        
        // Create smite effect from sky to target
        Vec3 skyPos = new Vec3(targetPos.x, targetPos.y + 50, targetPos.z);
        EffectRenderer.addEffect(new Smite(level, skyPos, targetPos)); // Swapped targetPos and skyPos since Smite expects (start, end)
        
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
