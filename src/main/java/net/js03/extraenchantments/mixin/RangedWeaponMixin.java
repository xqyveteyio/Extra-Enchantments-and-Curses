package net.js03.extraenchantments.mixin;

import net.js03.extraenchantments.ExtraEnchantsMain;
import net.js03.extraenchantments.registry.ModEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.ThreadLocalRandom;

@Mixin(ProjectileWeaponItem.class)
public abstract class RangedWeaponMixin {

    /**
     * Echo buys its piercing (an enchantment effect component) with extra durability. The upper
     * bound stays above the level so a datapack raising Echo past 8 cannot make the range empty.
     */
    @Redirect(
            method = "shoot",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;)V")
    )
    private void extraEnchantments$echoWear(ItemStack weapon, int amount, LivingEntity shooter, EquipmentSlot slot) {
        if (!ExtraEnchantsMain.CONFIG.echo.effectsDisabled()) {
            int level = ModEnchantments.levelOn(weapon, ModEnchantments.ECHO);
            if (level > 0) {
                amount = ThreadLocalRandom.current().nextInt(level, Math.max(level + 1, 8));
            }
        }
        weapon.hurtAndBreak(amount, shooter, slot);
    }
}
