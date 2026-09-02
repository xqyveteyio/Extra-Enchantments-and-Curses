package net.js03.extraenchantments.registry;

import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Optional;

/**
 * Since 1.21 enchantments are datapack entries rather than registered classes, so all this mod
 * keeps in code are the keys. The definitions live in {@code data/extra_enchantments/enchantment}
 * and can be overridden by any datapack.
 */
public final class ModEnchantments {

    public static final String NAMESPACE = "extra_enchantments";

    public static final RegistryKey<Enchantment> LIFESTEAL = of("lifesteal");
    public static final RegistryKey<Enchantment> FRENZY = of("frenzy");
    public static final RegistryKey<Enchantment> GUARDING_STRIKE = of("guarding_strike");
    public static final RegistryKey<Enchantment> PAIN_CYCLE = of("pain_cycle");
    public static final RegistryKey<Enchantment> SOUL_REAPER = of("soul_reaper");
    public static final RegistryKey<Enchantment> FREEZING_ASPECT = of("freezing_aspect");
    public static final RegistryKey<Enchantment> ILLAGERS_BANE = of("illagers_bane");
    public static final RegistryKey<Enchantment> FISHERMANS_BLADE = of("fishermans_blade");
    public static final RegistryKey<Enchantment> ENIGMA_RESONATOR = of("enigma_resonator");
    public static final RegistryKey<Enchantment> REACH = of("reach");
    public static final RegistryKey<Enchantment> SWIFTNESS = of("swiftness");
    public static final RegistryKey<Enchantment> EXPERIENCE_CATALYST = of("experience_catalyst");

    public static final RegistryKey<Enchantment> BURNING_THORNS = of("burning_thorns");
    public static final RegistryKey<Enchantment> ICE_PROTECTION = of("ice_protection");
    public static final RegistryKey<Enchantment> FREEZING_THORNS = of("freezing_thorns");
    public static final RegistryKey<Enchantment> OVERSHIELD = of("overshield");
    public static final RegistryKey<Enchantment> HELLWALKER = of("hellwalker");
    public static final RegistryKey<Enchantment> SPECTRAL_VISION = of("spectral_vision");
    public static final RegistryKey<Enchantment> ELECTRIFIED = of("electrified");
    public static final RegistryKey<Enchantment> ENERGIZED = of("energized");
    public static final RegistryKey<Enchantment> COLD_FEET = of("cold_feet");

    public static final RegistryKey<Enchantment> ELECTRIFYING_SHOT = of("electrifying_shot");
    public static final RegistryKey<Enchantment> RESONATING_SHOT = of("resonating_shot");
    public static final RegistryKey<Enchantment> SHADOW_SHOT = of("shadow_shot");
    public static final RegistryKey<Enchantment> ECHO = of("echo");
    public static final RegistryKey<Enchantment> LEVITATIONAL_SHOT = of("levitational_shot");
    public static final RegistryKey<Enchantment> INCANDESCENT = of("incandescent");
    public static final RegistryKey<Enchantment> SUPERCHARGE = of("supercharge");
    public static final RegistryKey<Enchantment> TARGET_LOCK = of("target_lock");

    public static final RegistryKey<Enchantment> CURSE_OF_ZEUS = of("zeus_curse");
    public static final RegistryKey<Enchantment> CURSE_OF_BLINDNESS = of("blindness_curse");
    public static final RegistryKey<Enchantment> CURSE_OF_WITHERING = of("withering_curse");
    public static final RegistryKey<Enchantment> CURSE_OF_NAUSEA = of("nausea_curse");
    public static final RegistryKey<Enchantment> CURSE_OF_WEAKNESS = of("weakness_curse");
    public static final RegistryKey<Enchantment> CURSE_OF_INCOMPATIBILITY = of("incompatibility_curse");
    public static final RegistryKey<Enchantment> CURSE_OF_FRAGILITY = of("fragility_curse");
    public static final RegistryKey<Enchantment> CURSE_OF_SLOWNESS = of("slowness_curse");
    public static final RegistryKey<Enchantment> CURSE_OF_UNDEAD = of("undead_curse");
    public static final RegistryKey<Enchantment> CURSE_OF_ATTRITION = of("attrition_curse");

    private ModEnchantments() {
    }

    private static RegistryKey<Enchantment> of(String path) {
        return RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(NAMESPACE, path));
    }

    /**
     * Reads the level straight off the stack's enchantment component, so it needs no registry
     * access and works on either side.
     */
    public static int levelOn(ItemStack stack, RegistryKey<Enchantment> key) {
        if (stack.isEmpty()) {
            return 0;
        }
        ItemEnchantmentsComponent enchantments = EnchantmentHelper.getEnchantments(stack);
        for (RegistryEntry<Enchantment> entry : enchantments.getEnchantments()) {
            if (entry.matchesKey(key)) {
                return enchantments.getLevel(entry);
            }
        }
        return 0;
    }

    /**
     * Highest level across the slots the enchantment declares, which needs the definition itself
     * and therefore the world's dynamic registry. Returns 0 when a datapack removed the entry —
     * a supported way of switching an enchantment off entirely.
     */
    public static int equipmentLevel(LivingEntity entity, RegistryKey<Enchantment> key) {
        return entry(entity.getWorld(), key)
                .map(entry -> EnchantmentHelper.getEquipmentLevel(entry, entity))
                .orElse(0);
    }

    public static Optional<RegistryEntry<Enchantment>> entry(World world, RegistryKey<Enchantment> key) {
        return world.getRegistryManager()
                .getOptional(RegistryKeys.ENCHANTMENT)
                .flatMap(registry -> registry.getEntry(key))
                .map(reference -> reference);
    }
}
