package net.js03.extraenchantments.mixin;

import net.js03.extraenchantments.ExtraEnchantsMain;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.concurrent.ThreadLocalRandom;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @ModifyVariable(
            method = "damage(ILnet/minecraft/util/math/random/Random;Lnet/minecraft/server/network/ServerPlayerEntity;)Z",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private int extraEnchantments$curseOfFragility(int amount) {
        if (amount <= 0 || ExtraEnchantsMain.CONFIG.curseOfFragility.effectsDisabled()) {
            return amount;
        }
        if (EnchantmentHelper.getLevel(ExtraEnchantsMain.CURSE_OF_FRAGILITY, (ItemStack) (Object) this) <= 0) {
            return amount;
        }
        return amount + ThreadLocalRandom.current().nextInt(0, 34);
    }
}
