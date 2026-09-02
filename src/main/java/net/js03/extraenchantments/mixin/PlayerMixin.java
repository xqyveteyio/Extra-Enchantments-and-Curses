package net.js03.extraenchantments.mixin;

import net.js03.extraenchantments.ExtraEnchantsMain;
import net.js03.extraenchantments.effect.CombatEffects;
import net.js03.extraenchantments.registry.ModEnchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerMixin {

    @Shadow
    public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    @Inject(method = "tick", at = @At("HEAD"))
    private void extraEnchantments$tick(CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        if (self.getWorld().isClient()) {
            return;
        }

        if (!ExtraEnchantsMain.CONFIG.curseOfUndead.effectsDisabled()
                && ModEnchantments.levelOn(this.getEquippedStack(EquipmentSlot.HEAD),
                        ModEnchantments.CURSE_OF_UNDEAD) > 0
                && self.getWorld().isSkyVisible(self.getBlockPos())
                && self.getWorld().isDay()
                && !self.isOnFire()
                && !self.getWorld().isRaining()) {
            self.setOnFireFor(7f);
        }

        // Spectral Vision's Darkness immunity; the glowing half is an enchantment effect component.
        if (!ExtraEnchantsMain.CONFIG.spectralVision.effectsDisabled()
                && ModEnchantments.levelOn(this.getEquippedStack(EquipmentSlot.HEAD),
                        ModEnchantments.SPECTRAL_VISION) > 0) {
            self.removeStatusEffect(StatusEffects.DARKNESS);
        }
    }

    @Inject(method = "attack", at = @At("TAIL"))
    private void extraEnchantments$attack(Entity target, CallbackInfo ci) {
        CombatEffects.onAttack((PlayerEntity) (Object) this, target);
    }

    @ModifyVariable(method = "addExperience(I)V", at = @At("HEAD"), argsOnly = true)
    private int extraEnchantments$experienceCatalyst(int experience) {
        if (ExtraEnchantsMain.CONFIG.xpCatalyst.effectsDisabled()) {
            return experience;
        }
        PlayerEntity self = (PlayerEntity) (Object) this;
        int level = ModEnchantments.levelOn(this.getEquippedStack(EquipmentSlot.MAINHAND),
                ModEnchantments.EXPERIENCE_CATALYST);
        return level > 0 ? experience * level : experience;
    }

    @ModifyVariable(method = "addExhaustion(F)V", at = @At("HEAD"), argsOnly = true)
    private float extraEnchantments$energized(float exhaustion) {
        if (ExtraEnchantsMain.CONFIG.energized.effectsDisabled()) {
            return exhaustion;
        }
        PlayerEntity self = (PlayerEntity) (Object) this;
        return ModEnchantments.levelOn(this.getEquippedStack(EquipmentSlot.LEGS),
                ModEnchantments.ENERGIZED) > 0
                ? exhaustion / 2
                : exhaustion;
    }
}
