package net.js03.extraenchantments.effect;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

/**
 * Per-attacker scratch state for the enchantments that ramp up over consecutive hits. Implemented
 * on {@code LivingEntity} so players and mobs share one copy; upstream kept separate counters in
 * each mixin and a single shared field on the enchantment object itself, which meant every entity
 * in the world fought over the same counter.
 */
public interface EnchantmentState {

    int extraEnchantments$painCycleHits();

    void extraEnchantments$setPainCycleHits(int hits);

    float extraEnchantments$targetLockDamage();

    void extraEnchantments$setTargetLockDamage(float damage);

    @Nullable
    Entity extraEnchantments$targetLockTarget();

    void extraEnchantments$setTargetLockTarget(@Nullable Entity target);
}
