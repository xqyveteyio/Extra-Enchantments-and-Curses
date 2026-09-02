package net.js03.extraenchantments.mixin;

import net.js03.extraenchantments.ExtraEnchantsMain;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

@Mixin(BowItem.class)
public abstract class BowMixin {

    @Unique
    private static int extraEnchantments$echoLevel(ItemStack bow) {
        if (ExtraEnchantsMain.CONFIG.echo.effectsDisabled()) {
            return 0;
        }
        return EnchantmentHelper.getLevel(ExtraEnchantsMain.ECHO, bow);
    }

    @Redirect(
            method = "onStoppedUsing(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V")
    )
    private void extraEnchantments$echoWear(ItemStack bow, int amount, LivingEntity holder, Consumer<LivingEntity> onBreak) {
        int level = extraEnchantments$echoLevel(bow);
        if (level > 0) {
            // Echo buys piercing with durability. The upper bound stays above the level so that
            // configs raising the maximum level past 8 cannot make the range empty.
            amount = ThreadLocalRandom.current().nextInt(level, Math.max(level + 1, 8));
        }
        bow.damage(amount, holder, onBreak);
    }

    @Redirect(
            method = "onStoppedUsing(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z")
    )
    private boolean extraEnchantments$echoPierce(World world, Entity projectile, ItemStack bow, World unusedWorld, LivingEntity user, int remainingUseTicks) {
        int level = extraEnchantments$echoLevel(bow);
        if (level > 0 && projectile instanceof PersistentProjectileEntity arrow) {
            arrow.setPierceLevel((byte) level);
        }
        return world.spawnEntity(projectile);
    }
}
