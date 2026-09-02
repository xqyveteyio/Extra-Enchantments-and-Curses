package net.js03.extraenchantments.mixin;

import net.js03.extraenchantments.ExtraEnchantsMain;
import net.js03.extraenchantments.effect.CombatEffects;
import net.js03.extraenchantments.registry.ModEnchantments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin {

    // getItemBySlot is inherited from LivingEntity rather than declared on Player, so @Shadow
    // cannot resolve it against this target; go through the cast instance instead.
    @Unique
    private ItemStack extraEnchantments$equipped(EquipmentSlot slot) {
        return ((Player) (Object) this).getItemBySlot(slot);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void extraEnchantments$tick(CallbackInfo ci) {
        Player self = (Player) (Object) this;
        if (self.level().isClientSide()) {
            return;
        }

        if (!ExtraEnchantsMain.CONFIG.curseOfUndead.effectsDisabled()
                && ModEnchantments.levelOn(this.extraEnchantments$equipped(EquipmentSlot.HEAD),
                        ModEnchantments.CURSE_OF_UNDEAD) > 0
                && self.level().canSeeSky(self.blockPosition())
                && self.level().isBrightOutside()
                && !self.isOnFire()
                && !self.level().isRaining()) {
            self.igniteForSeconds(7f);
        }

        // Spectral Vision's Darkness immunity; the glowing half is an enchantment effect component.
        if (!ExtraEnchantsMain.CONFIG.spectralVision.effectsDisabled()
                && ModEnchantments.levelOn(this.extraEnchantments$equipped(EquipmentSlot.HEAD),
                        ModEnchantments.SPECTRAL_VISION) > 0) {
            self.removeEffect(MobEffects.DARKNESS);
        }
    }

    @Inject(method = "attack", at = @At("TAIL"))
    private void extraEnchantments$attack(Entity target, CallbackInfo ci) {
        Player self = (Player) (Object) this;
        if (self.level() instanceof ServerLevel level) {
            CombatEffects.onAttack(level, self, target);
        }
    }

    @ModifyVariable(method = "giveExperiencePoints", at = @At("HEAD"), argsOnly = true)
    private int extraEnchantments$experienceCatalyst(int experience) {
        if (ExtraEnchantsMain.CONFIG.xpCatalyst.effectsDisabled()) {
            return experience;
        }
        int level = ModEnchantments.levelOn(this.extraEnchantments$equipped(EquipmentSlot.MAINHAND),
                ModEnchantments.EXPERIENCE_CATALYST);
        return level > 0 ? experience * level : experience;
    }

    @ModifyVariable(method = "causeFoodExhaustion", at = @At("HEAD"), argsOnly = true)
    private float extraEnchantments$energized(float exhaustion) {
        if (ExtraEnchantsMain.CONFIG.energized.effectsDisabled()) {
            return exhaustion;
        }
        return ModEnchantments.levelOn(this.extraEnchantments$equipped(EquipmentSlot.LEGS),
                ModEnchantments.ENERGIZED) > 0
                ? exhaustion / 2
                : exhaustion;
    }
}
