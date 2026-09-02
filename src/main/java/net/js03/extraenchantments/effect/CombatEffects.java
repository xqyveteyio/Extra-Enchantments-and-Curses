package net.js03.extraenchantments.effect;

import net.js03.extraenchantments.ExtraEnchantsMain;
import net.js03.extraenchantments.registry.ModEnchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.entity.passive.TraderLlamaEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import java.util.concurrent.ThreadLocalRandom;

/**
 * The melee half of the mod. Shared by the player and mob attack paths, which upstream kept as two
 * verbatim copies.
 */
public final class CombatEffects {

    private CombatEffects() {
    }

    public static boolean isHostileOrNeutral(Entity target) {
        return target instanceof HostileEntity || target instanceof PlayerEntity
                || target instanceof HoglinEntity || target instanceof BeeEntity
                || target instanceof DolphinEntity || target instanceof GoatEntity
                || target instanceof GolemEntity || target instanceof LlamaEntity
                || target instanceof TraderLlamaEntity || target instanceof PandaEntity
                || target instanceof PolarBearEntity || target instanceof WolfEntity
                || target instanceof PufferfishEntity || target instanceof SlimeEntity
                || target instanceof MagmaCubeEntity || target instanceof PhantomEntity
                || target instanceof EnderDragonEntity;
    }

    public static void onAttack(LivingEntity attacker, Entity target) {
        if (attacker.getWorld().isClient() || !(target instanceof LivingEntity victim)) {
            return;
        }
        boolean worthwhile = isHostileOrNeutral(target);

        freezingAspect(attacker, victim);
        enigmaResonator(attacker, victim);
        painCycle(attacker, victim, worthwhile);
        soulReaper(attacker, worthwhile);
        onKill(attacker, victim, worthwhile);
    }

    private static void freezingAspect(LivingEntity attacker, LivingEntity victim) {
        if (ExtraEnchantsMain.CONFIG.freezingAspect.effectsDisabled()) {
            return;
        }
        int level = ModEnchantments.equipmentLevel(attacker, ModEnchantments.FREEZING_ASPECT);
        if (level > 0 && !victim.isFrozen() && !victim.isInLava()
                && !victim.getWorld().getDimension().ultrawarm()) {
            victim.setFrozenTicks(level * 360);
        }
    }

    private static void enigmaResonator(LivingEntity attacker, LivingEntity victim) {
        if (ExtraEnchantsMain.CONFIG.enigmaResonator.effectsDisabled()) {
            return;
        }
        int level = ModEnchantments.equipmentLevel(attacker, ModEnchantments.ENIGMA_RESONATOR);
        if (level <= 0 || ThreadLocalRandom.current().nextInt(35) > level) {
            return;
        }
        if (victim.getRecentDamageSource() == null) {
            return;
        }
        attacker.getWorld().playSound(null, victim.getBlockPos(), SoundEvents.ENTITY_ARROW_HIT_PLAYER,
                SoundCategory.MASTER, 1f, 1f);
        victim.damage(victim.getDamageSources().generic(), attacker.getHealth() * 0.75f);
    }

    private static void painCycle(LivingEntity attacker, LivingEntity victim, boolean worthwhile) {
        if (ExtraEnchantsMain.CONFIG.painCycle.effectsDisabled() || !worthwhile) {
            return;
        }
        if (ModEnchantments.equipmentLevel(attacker, ModEnchantments.PAIN_CYCLE) <= 0) {
            return;
        }
        if (!(attacker instanceof EnchantmentState state)) {
            return;
        }
        int threshold = ExtraEnchantsMain.CONFIG.painCycleThreshold();
        if (state.extraEnchantments$painCycleHits() >= threshold) {
            if (victim.getRecentDamageSource() != null) {
                victim.damage(attacker.getDamageSources().magic(), 20);
                attacker.getWorld().playSound(null, victim.getBlockPos(), SoundEvents.ENTITY_ARROW_HIT_PLAYER,
                        SoundCategory.MASTER, 1f, 1f);
                state.extraEnchantments$setPainCycleHits(0);
            }
            return;
        }
        state.extraEnchantments$setPainCycleHits(state.extraEnchantments$painCycleHits() + 1);
        float cost = ExtraEnchantsMain.CONFIG.painCycleHealthCost();
        if (attacker.getHealth() <= cost) {
            attacker.damage(attacker.getDamageSources().magic(), 100);
        } else {
            attacker.setHealth(attacker.getHealth() - cost);
        }
        attacker.getWorld().playSound(null, attacker.getBlockPos(), SoundEvents.PARTICLE_SOUL_ESCAPE.value(),
                SoundCategory.MASTER, 3f, 1f);
    }

    private static void soulReaper(LivingEntity attacker, boolean worthwhile) {
        if (ExtraEnchantsMain.CONFIG.soulReaper.effectsDisabled() || !worthwhile) {
            return;
        }
        if (ModEnchantments.equipmentLevel(attacker, ModEnchantments.SOUL_REAPER) <= 0) {
            return;
        }
        ThreadLocalRandom random = ThreadLocalRandom.current();
        if (random.nextInt(6) <= 1) {
            attacker.getWorld().playSound(null, attacker.getBlockPos(), SoundEvents.PARTICLE_SOUL_ESCAPE.value(),
                    SoundCategory.MASTER, 3f, 1f);
            attacker.heal(random.nextInt(1, 5));
        }
    }

    private static void onKill(LivingEntity attacker, LivingEntity victim, boolean worthwhile) {
        if (!worthwhile || !victim.isDead()) {
            return;
        }

        if (!ExtraEnchantsMain.CONFIG.frenzy.effectsDisabled()) {
            int level = ModEnchantments.equipmentLevel(attacker, ModEnchantments.FRENZY);
            if (level > 0 && ThreadLocalRandom.current().nextInt(3) == 0) {
                attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 120, level - 1, false, true, true));
                attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 120, level - 1, false, true, true));
            }
        }

        if (!ExtraEnchantsMain.CONFIG.guardingStrike.effectsDisabled()) {
            int level = ModEnchantments.equipmentLevel(attacker, ModEnchantments.GUARDING_STRIKE);
            if (level > 0) {
                attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 120, level - 1, false, true, true));
            }
        }

        if (!ExtraEnchantsMain.CONFIG.lifesteal.effectsDisabled()) {
            int level = ModEnchantments.equipmentLevel(attacker, ModEnchantments.LIFESTEAL);
            if (level > 0) {
                attacker.getWorld().playSound(null, attacker.getBlockPos(), SoundEvents.PARTICLE_SOUL_ESCAPE.value(),
                        SoundCategory.MASTER, 3f, 1f);
                attacker.heal(victim.getMaxHealth() * 0.001f * level);
            }
        }
    }
}
