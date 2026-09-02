package net.js03.extraenchantments.mixin;

import net.js03.extraenchantments.ExtraEnchantsMain;
import net.js03.extraenchantments.effect.EnchantmentState;
import net.js03.extraenchantments.effect.OnDamagedEffects;
import net.js03.extraenchantments.registry.ModEnchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements EnchantmentState {

    @Unique
    private static final EquipmentSlot[] ARMOUR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    @Shadow
    public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    @Unique
    private int extraEnchantments$painCycleHits;
    @Unique
    private float extraEnchantments$targetLockDamage = 2f;
    @Unique
    @Nullable
    private Entity extraEnchantments$targetLockTarget;

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public int extraEnchantments$painCycleHits() {
        return this.extraEnchantments$painCycleHits;
    }

    @Override
    public void extraEnchantments$setPainCycleHits(int hits) {
        this.extraEnchantments$painCycleHits = hits;
    }

    @Override
    public float extraEnchantments$targetLockDamage() {
        return this.extraEnchantments$targetLockDamage;
    }

    @Override
    public void extraEnchantments$setTargetLockDamage(float damage) {
        this.extraEnchantments$targetLockDamage = damage;
    }

    @Override
    @Nullable
    public Entity extraEnchantments$targetLockTarget() {
        return this.extraEnchantments$targetLockTarget;
    }

    @Override
    public void extraEnchantments$setTargetLockTarget(@Nullable Entity target) {
        this.extraEnchantments$targetLockTarget = target;
    }

    // Ice Protection IV or higher on any worn piece prevents freezing. Upstream wrote this as
    // `bl && a < 4 || b < 4 || ...`, and since Java binds && tighter than ||, a single unenchanted
    // slot (level 0 < 4) forced the whole expression true and disabled freeze immunity entirely.
    @Inject(method = "canFreeze", at = @At("HEAD"), cancellable = true)
    private void extraEnchantments$iceProtection(CallbackInfoReturnable<Boolean> cir) {
        if (ExtraEnchantsMain.CONFIG.iceProtection.effectsDisabled()) {
            return;
        }
        for (EquipmentSlot slot : ARMOUR_SLOTS) {
            if (ModEnchantments.levelOn(this.getEquippedStack(slot), ModEnchantments.ICE_PROTECTION) >= 4) {
                cir.setReturnValue(false);
                return;
            }
        }
    }

    @Inject(method = "damage", at = @At("RETURN"))
    private void extraEnchantments$onDamaged(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            OnDamagedEffects.onDamaged((LivingEntity) (Object) this, source);
        }
    }
}
