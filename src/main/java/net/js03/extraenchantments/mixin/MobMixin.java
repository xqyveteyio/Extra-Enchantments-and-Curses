package net.js03.extraenchantments.mixin;

import net.js03.extraenchantments.effect.CombatEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public abstract class MobMixin {

    @Inject(method = "tryAttack", at = @At("RETURN"))
    private void extraEnchantments$attack(Entity target, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            CombatEffects.onAttack((MobEntity) (Object) this, target);
        }
    }
}
