package net.js03.extraenchantments.mixin;

import net.js03.extraenchantments.ExtraEnchantsMain;
import net.js03.extraenchantments.registry.ModEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
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

    @Inject(method = "isEnchantmentCompatible", at = @At("HEAD"), cancellable = true)
    private static void extraEnchantments$curseOfIncompatibility(
            Collection<Holder<Enchantment>> existing,
            Holder<Enchantment> candidate,
            CallbackInfoReturnable<Boolean> cir) {
        if (ExtraEnchantsMain.CONFIG.curseOfIncompatibility.effectsDisabled()) {
            return;
        }
        if (candidate.is(ModEnchantments.CURSE_OF_INCOMPATIBILITY)) {
            if (!existing.isEmpty()) {
                cir.setReturnValue(false);
            }
            return;
        }
        for (Holder<Enchantment> entry : existing) {
            if (entry.is(ModEnchantments.CURSE_OF_INCOMPATIBILITY)) {
                cir.setReturnValue(false);
                return;
            }
        }
    }
}
