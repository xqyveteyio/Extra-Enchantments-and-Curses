package net.js03.extraenchantments.effect;

import net.js03.extraenchantments.ExtraEnchantsMain;
import net.js03.extraenchantments.registry.ModEnchantments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.player.Player;

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

    public static void onDamaged(ServerLevel level, LivingEntity victim, DamageSource source) {
        freezingThorns(victim, source.getEntity());

        if (source.getEntity() instanceof LivingEntity shooter && source.is(DamageTypeTags.IS_PROJECTILE)) {
            resonatingShot(level, shooter, victim);
            targetLock(level, shooter, victim);
            shadowShot(shooter, victim);
        }
    }

    private static void freezingThorns(LivingEntity victim, Entity attacker) {
        if (ExtraEnchantsMain.CONFIG.freezingThorns.effectsDisabled()) {
            return;
        }
        if (!(attacker instanceof LivingEntity) || attacker instanceof Blaze || attacker instanceof MagmaCube) {
            return;
        }
        int level = ModEnchantments.equipmentLevel(victim, ModEnchantments.FREEZING_THORNS);
        if (level <= 0 || ThreadLocalRandom.current().nextInt(1, 5) > level) {
            return;
        }
        if (!CombatEffects.tooHotToFreeze(victim) && !attacker.isFullyFrozen() && !attacker.isInLava()) {
            attacker.setTicksFrozen(400);
        }
    }

    private static void resonatingShot(ServerLevel level, LivingEntity shooter, LivingEntity victim) {
        if (ExtraEnchantsMain.CONFIG.resonatingShot.effectsDisabled()) {
            return;
        }
        int enchantLevel = ModEnchantments.equipmentLevel(shooter, ModEnchantments.RESONATING_SHOT);
        if (enchantLevel <= 0 || ThreadLocalRandom.current().nextInt(35) > enchantLevel) {
            return;
        }
        victim.hurtServer(level, shooter.damageSources().generic(), shooter.getHealth() * 0.75f);
    }

    private static void targetLock(ServerLevel level, LivingEntity shooter, LivingEntity victim) {
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
        victim.hurtServer(level, shooter.damageSources().indirectMagic(shooter, shooter), damage);
        if (damage >= 32f) {
            boolean player = victim instanceof Player;
            shooter.level().playSound(null, victim.getX(), victim.getY(), victim.getZ(),
                    player ? SoundEvents.PLAYER_ATTACK_CRIT : SoundEvents.ARROW_HIT_PLAYER,
                    SoundSource.MASTER, player ? 2f : 1f, 1f);
        }
        if (damage < TARGET_LOCK_MAX_DAMAGE) {
            state.extraEnchantments$setTargetLockDamage(damage * 2f);
        }
    }

    private static void shadowShot(LivingEntity shooter, LivingEntity victim) {
        if (ExtraEnchantsMain.CONFIG.shadowShot.effectsDisabled() || !victim.isDeadOrDying()) {
            return;
        }
        if (!shooter.level().isDarkOutside() || shooter.level().isThundering()) {
            return;
        }
        if (ModEnchantments.equipmentLevel(shooter, ModEnchantments.SHADOW_SHOT) <= 0) {
            return;
        }
        if (ThreadLocalRandom.current().nextInt(15) > 2) {
            return;
        }
        shooter.level().playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(),
                SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.MASTER, 1f, 1f);
        shooter.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 220, 254, false, false, true));
        shooter.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 220, 254, false, false, true));
    }
}
