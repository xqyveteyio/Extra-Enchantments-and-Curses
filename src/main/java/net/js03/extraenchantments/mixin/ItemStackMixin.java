package net.js03.extraenchantments.mixin;

import net.js03.extraenchantments.ExtraEnchantsMain;
import net.js03.extraenchantments.registry.ModEnchantments;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.concurrent.ThreadLocalRandom;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    // Applied before Unbreaking rolls rather than after, so Unbreaking mitigates part of the
    // curse instead of being bypassed entirely.
    @ModifyVariable(
            method = "damage(ILnet/minecraft/server/world/ServerWorld;Lnet/minecraft/server/network/ServerPlayerEntity;Ljava/util/function/Consumer;)V",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private int extraEnchantments$curseOfFragility(int amount) {
        if (amount <= 0 || ExtraEnchantsMain.CONFIG.curseOfFragility.effectsDisabled()) {
            return amount;
        }
        if (ModEnchantments.levelOn((ItemStack) (Object) this, ModEnchantments.CURSE_OF_FRAGILITY) <= 0) {
            return amount;
        }
        return amount + ThreadLocalRandom.current().nextInt(0, 34);
    }
}
