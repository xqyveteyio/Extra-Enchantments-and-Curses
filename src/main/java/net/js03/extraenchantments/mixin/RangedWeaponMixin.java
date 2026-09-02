package net.js03.extraenchantments.mixin;

import net.js03.extraenchantments.ExtraEnchantsMain;
import net.js03.extraenchantments.registry.ModEnchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.ThreadLocalRandom;

@Mixin(RangedWeaponItem.class)
public abstract class RangedWeaponMixin {

    /**
     * Echo buys its piercing (an enchantment effect component) with extra durability. The upper
     * bound stays above the level so a datapack raising Echo past 8 cannot make the range empty.
     */
    @Redirect(
            method = "shootAll",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;damage(ILnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/EquipmentSlot;)V")
    )
    private void extraEnchantments$echoWear(ItemStack weapon, int amount, LivingEntity shooter, EquipmentSlot slot) {
        if (!ExtraEnchantsMain.CONFIG.echo.effectsDisabled()) {
            int level = ModEnchantments.levelOn(weapon, ModEnchantments.ECHO);
            if (level > 0) {
                amount = ThreadLocalRandom.current().nextInt(level, Math.max(level + 1, 8));
            }
        }
        weapon.damage(amount, shooter, slot);
    }
}
