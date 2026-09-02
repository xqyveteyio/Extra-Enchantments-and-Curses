package net.js03.extraenchantments.mixin;

import net.js03.extraenchantments.ExtraEnchantsMain;
import net.js03.extraenchantments.registry.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

/**
 * Curse of Incompatibility is the one conflict rule that cannot be expressed as an exclusive set,
 * because it conflicts with everything rather than with a fixed list.
 */
@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    @Inject(method = "isCompatible", at = @At("HEAD"), cancellable = true)
    private static void extraEnchantments$curseOfIncompatibility(
            Collection<RegistryEntry<Enchantment>> existing,
            RegistryEntry<Enchantment> candidate,
            CallbackInfoReturnable<Boolean> cir) {
        if (ExtraEnchantsMain.CONFIG.curseOfIncompatibility.effectsDisabled()) {
            return;
        }
        if (candidate.matchesKey(ModEnchantments.CURSE_OF_INCOMPATIBILITY)) {
            if (!existing.isEmpty()) {
                cir.setReturnValue(false);
            }
            return;
        }
        for (RegistryEntry<Enchantment> entry : existing) {
            if (entry.matchesKey(ModEnchantments.CURSE_OF_INCOMPATIBILITY)) {
                cir.setReturnValue(false);
                return;
            }
        }
    }
}
