package net.js03.extraenchantments.mixin;

import net.js03.extraenchantments.effect.CombatEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobMixin {

    @Inject(method = "doHurtTarget", at = @At("RETURN"))
    private void extraEnchantments$attack(ServerLevel level, Entity target, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            CombatEffects.onAttack(level, (Mob) (Object) this, target);
        }
    }
}
