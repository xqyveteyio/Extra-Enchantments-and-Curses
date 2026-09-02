package net.js03.extraenchantments.effect;

import net.js03.extraenchantments.ExtraEnchantsMain;
import net.js03.extraenchantments.registry.ModEnchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Effects that fire when an entity takes damage: the victim's own retaliation enchantments, and
 * the shooter-side bow and crossbow enchantments that need to see the hit land.
 */
public final class OnDamagedEffects {

    private static final float TARGET_LOCK_BASE_DAMAGE = 2f;
    private static final float TARGET_LOCK_MAX_DAMAGE = 512f;

    private OnDamagedEffects() {
    }

    public static void onDamaged(LivingEntity victim, DamageSource source) {
        if (victim.getWorld().isClient()) {
            return;
        }
        freezingThorns(victim, source.getAttacker());

        if (source.getAttacker() instanceof LivingEntity shooter && source.isIn(DamageTypeTags.IS_PROJECTILE)) {
            resonatingShot(shooter, victim);
            targetLock(shooter, victim);
            shadowShot(shooter, victim);
        }
    }

    private static void freezingThorns(LivingEntity victim, Entity attacker) {
        if (ExtraEnchantsMain.CONFIG.freezingThorns.effectsDisabled()) {
            return;
        }
        if (!(attacker instanceof LivingEntity) || attacker instanceof BlazeEntity
                || attacker instanceof MagmaCubeEntity) {
            return;
        }
        int level = ModEnchantments.equipmentLevel(victim, ModEnchantments.FREEZING_THORNS);
        if (level <= 0 || ThreadLocalRandom.current().nextInt(1, 5) > level) {
            return;
        }
        if (!victim.getWorld().getDimension().ultrawarm() && !attacker.isFrozen() && !attacker.isInLava()) {
            attacker.setFrozenTicks(400);
        }
    }

    private static void resonatingShot(LivingEntity shooter, LivingEntity victim) {
        if (ExtraEnchantsMain.CONFIG.resonatingShot.effectsDisabled()) {
            return;
        }
        int level = ModEnchantments.equipmentLevel(shooter, ModEnchantments.RESONATING_SHOT);
        if (level <= 0 || ThreadLocalRandom.current().nextInt(35) > level) {
            return;
        }
        victim.damage(shooter.getDamageSources().generic(), shooter.getHealth() * 0.75f);
    }

    private static void targetLock(LivingEntity shooter, LivingEntity victim) {
        if (ExtraEnchantsMain.CONFIG.targetLock.effectsDisabled()
                || !(shooter instanceof EnchantmentState state)) {
            return;
        }
        if (ModEnchantments.equipmentLevel(shooter, ModEnchantments.TARGET_LOCK) <= 0) {
            return;
        }
        if (victim != state.extraEnchantments$targetLockTarget() || !victim.isAlive()) {
            state.extraEnchantments$setTargetLockTarget(victim);
            state.extraEnchantments$setTargetLockDamage(TARGET_LOCK_BASE_DAMAGE);
            return;
        }
        float damage = state.extraEnchantments$targetLockDamage();
        victim.damage(shooter.getDamageSources().indirectMagic(shooter, shooter), damage);
        if (damage >= 32f) {
            shooter.getWorld().playSound(null, victim.getBlockPos(),
                    victim instanceof PlayerEntity
                            ? SoundEvents.ENTITY_PLAYER_ATTACK_CRIT
                            : SoundEvents.ENTITY_ARROW_HIT_PLAYER,
                    SoundCategory.MASTER, victim instanceof PlayerEntity ? 2f : 1f, 1f);
        }
        if (damage < TARGET_LOCK_MAX_DAMAGE) {
            state.extraEnchantments$setTargetLockDamage(damage * 2f);
        }
    }

    private static void shadowShot(LivingEntity shooter, LivingEntity victim) {
        if (ExtraEnchantsMain.CONFIG.shadowShot.effectsDisabled() || !victim.isDead()) {
            return;
        }
        if (!shooter.getWorld().isNight() || shooter.getWorld().isThundering()) {
            return;
        }
        if (ModEnchantments.equipmentLevel(shooter, ModEnchantments.SHADOW_SHOT) <= 0) {
            return;
        }
        if (ThreadLocalRandom.current().nextInt(15) > 2) {
            return;
        }
        shooter.getWorld().playSound(null, shooter.getBlockPos(), SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE,
                SoundCategory.MASTER, 1f, 1f);
        shooter.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 220, 254, false, false, true));
        shooter.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 220, 254, false, false, true));
    }
}
