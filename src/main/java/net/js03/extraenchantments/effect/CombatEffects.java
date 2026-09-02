package net.js03.extraenchantments.effect;

import net.js03.extraenchantments.ExtraEnchantsMain;
import net.js03.extraenchantments.registry.ModEnchantments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.animal.dolphin.Dolphin;
import net.minecraft.world.entity.animal.equine.Llama;
import net.minecraft.world.entity.animal.fish.Pufferfish;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.golem.AbstractGolem;
import net.minecraft.world.entity.animal.panda.Panda;
import net.minecraft.world.entity.animal.polarbear.PolarBear;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.ThreadLocalRandom;

/**
 * The melee half of the mod. Shared by the player and mob attack paths, which upstream kept as two
 * verbatim copies.
 */
public final class CombatEffects {

    private CombatEffects() {
    }

    public static boolean isHostileOrNeutral(Entity target) {
        return target instanceof Monster || target instanceof Player
                || target instanceof Hoglin || target instanceof Bee
                || target instanceof Dolphin || target instanceof Goat
                || target instanceof AbstractGolem || target instanceof Llama
                || target instanceof Panda || target instanceof PolarBear
                || target instanceof Wolf || target instanceof Pufferfish
                || target instanceof Slime || target instanceof MagmaCube
                || target instanceof Phantom || target instanceof EnderDragon;
    }

    public static void onAttack(ServerLevel level, LivingEntity attacker, Entity target) {
        if (!(target instanceof LivingEntity victim)) {
            return;
        }
        boolean worthwhile = isHostileOrNeutral(target);

        freezingAspect(attacker, victim);
        enigmaResonator(level, attacker, victim);
        painCycle(level, attacker, victim, worthwhile);
        soulReaper(attacker, worthwhile);
        onKill(attacker, victim, worthwhile);
    }

    private static void freezingAspect(LivingEntity attacker, LivingEntity victim) {
        if (ExtraEnchantsMain.CONFIG.freezingAspect.effectsDisabled()) {
            return;
        }
        int level = ModEnchantments.equipmentLevel(attacker, ModEnchantments.FREEZING_ASPECT);
        if (level > 0 && !victim.isFullyFrozen() && !victim.isInLava() && !tooHotToFreeze(victim)) {
            victim.setTicksFrozen(level * 360);
        }
    }

    /**
     * DimensionType lost its {@code ultraWarm} flag; the dimension-wide value of the attribute that
     * makes water evaporate is what vanilla now uses to mean "too hot for ice to survive here".
     */
    public static boolean tooHotToFreeze(Entity entity) {
        return entity.level().environmentAttributes().getDimensionValue(EnvironmentAttributes.WATER_EVAPORATES);
    }

    private static void enigmaResonator(ServerLevel level, LivingEntity attacker, LivingEntity victim) {
        if (ExtraEnchantsMain.CONFIG.enigmaResonator.effectsDisabled()) {
            return;
        }
        int enchantLevel = ModEnchantments.equipmentLevel(attacker, ModEnchantments.ENIGMA_RESONATOR);
        if (enchantLevel <= 0 || ThreadLocalRandom.current().nextInt(35) > enchantLevel) {
            return;
        }
        if (victim.getLastDamageSource() == null) {
            return;
        }
        playSound(attacker, victim, SoundEvents.ARROW_HIT_PLAYER, 1f);
        victim.hurtServer(level, victim.damageSources().generic(), attacker.getHealth() * 0.75f);
    }

    private static void painCycle(ServerLevel level, LivingEntity attacker, LivingEntity victim, boolean worthwhile) {
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
            if (victim.getLastDamageSource() != null) {
                victim.hurtServer(level, attacker.damageSources().magic(), 20);
                playSound(attacker, victim, SoundEvents.ARROW_HIT_PLAYER, 1f);
                state.extraEnchantments$setPainCycleHits(0);
            }
            return;
        }
        state.extraEnchantments$setPainCycleHits(state.extraEnchantments$painCycleHits() + 1);
        float cost = ExtraEnchantsMain.CONFIG.painCycleHealthCost();
        if (attacker.getHealth() <= cost) {
            attacker.hurtServer(level, attacker.damageSources().magic(), 100);
        } else {
            attacker.setHealth(attacker.getHealth() - cost);
        }
        playSound(attacker, attacker, SoundEvents.SOUL_ESCAPE.value(), 3f);
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
            playSound(attacker, attacker, SoundEvents.SOUL_ESCAPE.value(), 3f);
            attacker.heal(random.nextInt(1, 5));
        }
    }

    private static void onKill(LivingEntity attacker, LivingEntity victim, boolean worthwhile) {
        if (!worthwhile || !victim.isDeadOrDying()) {
            return;
        }

        if (!ExtraEnchantsMain.CONFIG.frenzy.effectsDisabled()) {
            int level = ModEnchantments.equipmentLevel(attacker, ModEnchantments.FRENZY);
            if (level > 0 && ThreadLocalRandom.current().nextInt(3) == 0) {
                attacker.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 120, level - 1, false, true, true));
                attacker.addEffect(new MobEffectInstance(MobEffects.SPEED, 120, level - 1, false, true, true));
            }
        }

        if (!ExtraEnchantsMain.CONFIG.guardingStrike.effectsDisabled()) {
            int level = ModEnchantments.equipmentLevel(attacker, ModEnchantments.GUARDING_STRIKE);
            if (level > 0) {
                attacker.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 120, level - 1, false, true, true));
            }
        }

        if (!ExtraEnchantsMain.CONFIG.lifesteal.effectsDisabled()) {
            int level = ModEnchantments.equipmentLevel(attacker, ModEnchantments.LIFESTEAL);
            if (level > 0) {
                playSound(attacker, attacker, SoundEvents.SOUL_ESCAPE.value(), 3f);
                attacker.heal(victim.getMaxHealth() * 0.001f * level);
            }
        }
    }

    private static void playSound(LivingEntity source, Entity at, net.minecraft.sounds.SoundEvent sound, float volume) {
        source.level().playSound(null, at.getX(), at.getY(), at.getZ(), sound, SoundSource.MASTER, volume, 1f);
    }
}
