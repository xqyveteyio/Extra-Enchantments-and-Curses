package net.js03.extraenchantments.mixin;

import net.js03.extraenchantments.ExtraEnchantsMain;
import net.js03.extraenchantments.registry.ModEnchantments;
import net.minecraft.block.PowderSnowBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PowderSnowBlock.class)
public class PowderSnowMixin {

    @Inject(method = "canWalkOnPowderSnow", at = @At("HEAD"), cancellable = true)
    private static void extraEnchantments$coldFeetWalk(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (ExtraEnchantsMain.CONFIG.coldFeet.effectsDisabled()) {
            return;
        }
        if (entity instanceof LivingEntity living
                && ModEnchantments.levelOn(living.getEquippedStack(EquipmentSlot.FEET),
                        ModEnchantments.COLD_FEET) > 0) {
            cir.setReturnValue(true);
        }
    }
}
