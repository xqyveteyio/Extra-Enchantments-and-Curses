package net.js03.extraenchantments.mixin;

import net.js03.extraenchantments.ExtraEnchantsMain;
import net.js03.extraenchantments.config.ExtraEnchantsConfig;
import net.js03.extraenchantments.enchantments.ColdFeet;
import net.js03.extraenchantments.enchantments.HellWalker;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Unique
    private static final EquipmentSlot[] ARMOUR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    @Shadow
    public abstract boolean addStatusEffect(StatusEffectInstance effect);
    @Shadow
    public abstract ItemStack getEquippedStack(EquipmentSlot var1);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "applyMovementEffects", at = @At("HEAD"))
    private void applyMovementEffects(BlockPos pos, CallbackInfo ci) {
        LivingEntity casted = (LivingEntity) (Object) this;
        int i = EnchantmentHelper.getEquipmentLevel(ExtraEnchantsMain.HELLWALKER, casted);
        if (i > 0 && !ExtraEnchantsMain.CONFIG.hellwalker.effectsDisabled()) {
            HellWalker.freezeLava(casted, this.getWorld(), pos);
        }

        int j = EnchantmentHelper.getEquipmentLevel(ExtraEnchantsMain.COLD_FEET, casted);
        if (j > 1 && !ExtraEnchantsMain.CONFIG.coldFeet.effectsDisabled()) {
            ColdFeet.freezeWater(casted, this.getWorld(), pos);
        }
    }

    @Inject(method = "canFreeze", at = @At("HEAD"), cancellable = true)
    private void extraEnchantments$iceProtection(CallbackInfoReturnable<Boolean> cir) {
        if (ExtraEnchantsMain.CONFIG.iceProtection.effectsDisabled()) {
            return;
        }
        for (EquipmentSlot slot : ARMOUR_SLOTS) {
            if (EnchantmentHelper.getLevel(ExtraEnchantsMain.ICE_PROTECTION, this.getEquippedStack(slot)) >= 4) {
                cir.setReturnValue(false);
                return;
            }
        }
    }

}
