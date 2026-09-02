package net.js03.extraenchantments.mixin;

import net.js03.extraenchantments.ExtraEnchantsMain;
import net.minecraft.block.MagmaBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MagmaBlock.class)
public class MagmaBlockMixin {

    @Redirect(
            method = "onSteppedOn",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z")
    )
    private boolean extraEnchantments$hellWalker(Entity entity, DamageSource source, float amount) {
        if (!ExtraEnchantsMain.CONFIG.hellwalker.effectsDisabled()
                && entity instanceof LivingEntity living
                && EnchantmentHelper.getEquipmentLevel(ExtraEnchantsMain.HELLWALKER, living) > 0) {
            return false;
        }
        return entity.damage(source, amount);
    }
}
