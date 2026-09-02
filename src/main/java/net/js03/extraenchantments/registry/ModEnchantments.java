package net.js03.extraenchantments.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

import java.util.Optional;

/**
 * Since 1.21 enchantments are datapack entries rather than registered classes, so all this mod
 * keeps in code are the keys. The definitions live in {@code data/extra_enchantments/enchantment}
 * and can be overridden by any datapack.
 */
public final class ModEnchantments {

    public static final String NAMESPACE = "extra_enchantments";

    public static final ResourceKey<Enchantment> LIFESTEAL = of("lifesteal");
    public static final ResourceKey<Enchantment> FRENZY = of("frenzy");
    public static final ResourceKey<Enchantment> GUARDING_STRIKE = of("guarding_strike");
    public static final ResourceKey<Enchantment> PAIN_CYCLE = of("pain_cycle");
    public static final ResourceKey<Enchantment> SOUL_REAPER = of("soul_reaper");
    public static final ResourceKey<Enchantment> FREEZING_ASPECT = of("freezing_aspect");
    public static final ResourceKey<Enchantment> ILLAGERS_BANE = of("illagers_bane");
    public static final ResourceKey<Enchantment> FISHERMANS_BLADE = of("fishermans_blade");
    public static final ResourceKey<Enchantment> ENIGMA_RESONATOR = of("enigma_resonator");
    public static final ResourceKey<Enchantment> REACH = of("reach");
    public static final ResourceKey<Enchantment> SWIFTNESS = of("swiftness");
    public static final ResourceKey<Enchantment> EXPERIENCE_CATALYST = of("experience_catalyst");

    public static final ResourceKey<Enchantment> BURNING_THORNS = of("burning_thorns");
    public static final ResourceKey<Enchantment> ICE_PROTECTION = of("ice_protection");
    public static final ResourceKey<Enchantment> FREEZING_THORNS = of("freezing_thorns");
    public static final ResourceKey<Enchantment> OVERSHIELD = of("overshield");
    public static final ResourceKey<Enchantment> HELLWALKER = of("hellwalker");
    public static final ResourceKey<Enchantment> SPECTRAL_VISION = of("spectral_vision");
    public static final ResourceKey<Enchantment> ELECTRIFIED = of("electrified");
    public static final ResourceKey<Enchantment> ENERGIZED = of("energized");
    public static final ResourceKey<Enchantment> COLD_FEET = of("cold_feet");

    public static final ResourceKey<Enchantment> ELECTRIFYING_SHOT = of("electrifying_shot");
    public static final ResourceKey<Enchantment> RESONATING_SHOT = of("resonating_shot");
    public static final ResourceKey<Enchantment> SHADOW_SHOT = of("shadow_shot");
    public static final ResourceKey<Enchantment> ECHO = of("echo");
    public static final ResourceKey<Enchantment> LEVITATIONAL_SHOT = of("levitational_shot");
    public static final ResourceKey<Enchantment> INCANDESCENT = of("incandescent");
    public static final ResourceKey<Enchantment> SUPERCHARGE = of("supercharge");
    public static final ResourceKey<Enchantment> TARGET_LOCK = of("target_lock");

    public static final ResourceKey<Enchantment> CURSE_OF_ZEUS = of("zeus_curse");
    public static final ResourceKey<Enchantment> CURSE_OF_BLINDNESS = of("blindness_curse");
    public static final ResourceKey<Enchantment> CURSE_OF_WITHERING = of("withering_curse");
    public static final ResourceKey<Enchantment> CURSE_OF_NAUSEA = of("nausea_curse");
    public static final ResourceKey<Enchantment> CURSE_OF_WEAKNESS = of("weakness_curse");
    public static final ResourceKey<Enchantment> CURSE_OF_INCOMPATIBILITY = of("incompatibility_curse");
    public static final ResourceKey<Enchantment> CURSE_OF_FRAGILITY = of("fragility_curse");
    public static final ResourceKey<Enchantment> CURSE_OF_SLOWNESS = of("slowness_curse");
    public static final ResourceKey<Enchantment> CURSE_OF_UNDEAD = of("undead_curse");
    public static final ResourceKey<Enchantment> CURSE_OF_ATTRITION = of("attrition_curse");

    private ModEnchantments() {
    }

    private static ResourceKey<Enchantment> of(String path) {
        return ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(NAMESPACE, path));
    }

    /**
     * Reads the level straight off the stack's enchantment component, so it needs no registry
     * access and works on either side.
     */
    public static int levelOn(ItemStack stack, ResourceKey<Enchantment> key) {
        if (stack.isEmpty()) {
            return 0;
        }
        ItemEnchantments enchantments = stack.getEnchantments();
        for (Holder<Enchantment> holder : enchantments.keySet()) {
            if (holder.is(key)) {
                return enchantments.getLevel(holder);
            }
        }
        return 0;
    }

    /**
     * Highest level across the slots the enchantment declares, which needs the definition itself
     * and therefore the level's dynamic registry. Returns 0 when a datapack removed the entry —
     * a supported way of switching an enchantment off entirely.
     */
    public static int equipmentLevel(LivingEntity entity, ResourceKey<Enchantment> key) {
        return holder(entity.level(), key)
                .map(h -> EnchantmentHelper.getEnchantmentLevel(h, entity))
                .orElse(0);
    }

    /**
     * Resolves a key against the level's dynamic registry. Returns empty when a datapack removed
     * the entry, which is a supported way of switching an enchantment off entirely.
     */
    public static Optional<Holder<Enchantment>> holder(Level level, ResourceKey<Enchantment> key) {
        return level.registryAccess()
                .lookup(Registries.ENCHANTMENT)
                .flatMap(registry -> registry.get(key))
                .map(reference -> reference);
    }
}
