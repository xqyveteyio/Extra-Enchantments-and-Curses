package net.js03.extraenchantments.config;

import io.wispforest.owo.config.annotation.*;

/**
 * Only covers effects this mod still drives from Java. Everything that describes an enchantment
 * rather than its behaviour — weight, maximum level, enchanting cost, which items accept it, which
 * enchantments it conflicts with — moved into {@code data/extra_enchantments/enchantment} when
 * 1.21 made enchantments datapack entries, and is overridden with a datapack instead. So are the
 * effects that map onto vanilla enchantment effect components, such as Overshield's bonus health
 * and Reach's range.
 */
@Modmenu(modId = "extra-enchantments-and-curses")
@Config(name = "extra-enchantments-and-curses-config", wrapperName = "ExtraEnchantsConfig")
public class ConfigModel {

    public static class EnchantmentOptions {
        public boolean effectsDisabled;

        public EnchantmentOptions() {
        }

        public EnchantmentOptions(boolean effectsDisabled) {
            this.effectsDisabled = effectsDisabled;
        }
    }

    @SectionHeader("Enchantments")
    @Nest
    public EnchantmentOptions coldFeet = new EnchantmentOptions(false);
    @Nest
    public EnchantmentOptions echo = new EnchantmentOptions(false);
    @Nest
    public EnchantmentOptions energized = new EnchantmentOptions(false);
    @Nest
    public EnchantmentOptions enigmaResonator = new EnchantmentOptions(false);
    @Nest
    public EnchantmentOptions freezingAspect = new EnchantmentOptions(false);
    @Nest
    public EnchantmentOptions freezingThorns = new EnchantmentOptions(false);
    @Nest
    public EnchantmentOptions frenzy = new EnchantmentOptions(false);
    @Nest
    public EnchantmentOptions guardingStrike = new EnchantmentOptions(false);
    @Nest
    public EnchantmentOptions iceProtection = new EnchantmentOptions(false);
    @Nest
    public EnchantmentOptions lifesteal = new EnchantmentOptions(false);
    @Nest
    public EnchantmentOptions painCycle = new EnchantmentOptions(false);
    @Nest
    public EnchantmentOptions resonatingShot = new EnchantmentOptions(false);
    @Nest
    public EnchantmentOptions shadowShot = new EnchantmentOptions(false);
    @Nest
    public EnchantmentOptions soulReaper = new EnchantmentOptions(false);
    @Nest
    public EnchantmentOptions spectralVision = new EnchantmentOptions(false);
    @Nest
    public EnchantmentOptions targetLock = new EnchantmentOptions(false);
    @Nest
    public EnchantmentOptions xpCatalyst = new EnchantmentOptions(false);

    @SectionHeader("Tuning")
    // Health points, not hearts: one heart is two points. Each Pain Cycle stack costs this much.
    @RangeConstraint(min = 0, max = 20)
    public float painCycleHealthCost = 1.0f;
    // Hits needed before Pain Cycle discharges into the next attack.
    @RangeConstraint(min = 1, max = 32)
    public int painCycleThreshold = 3;

    @SectionHeader("Curses")
    @Nest
    public EnchantmentOptions curseOfFragility = new EnchantmentOptions(false);
    @Nest
    public EnchantmentOptions curseOfIncompatibility = new EnchantmentOptions(false);
    @Nest
    public EnchantmentOptions curseOfUndead = new EnchantmentOptions(false);
}
