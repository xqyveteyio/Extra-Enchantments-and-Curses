package net.js03.extraenchantments.loot;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.js03.extraenchantments.registry.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import java.util.Map;

/**
 * Upstream shipped full copies of three vanilla loot tables to slip its books in, which silently
 * loses any change another datapack or mod makes to the same table. These are added as an extra
 * pool instead.
 */
public final class ModLootTables {

    private static final Map<RegistryKey<LootTable>, RegistryKey<Enchantment>> BOOKS = Map.of(
            LootTables.ANCIENT_CITY_CHEST, ModEnchantments.OVERSHIELD,
            LootTables.BASTION_OTHER_CHEST, ModEnchantments.HELLWALKER,
            LootTables.PIGLIN_BARTERING_GAMEPLAY, ModEnchantments.HELLWALKER
    );

    private static final float BOOK_CHANCE = 0.15f;

    private ModLootTables() {
    }

    public static void register() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (!source.isBuiltin()) {
                return;
            }
            RegistryKey<Enchantment> enchantment = BOOKS.get(key);
            if (enchantment == null) {
                return;
            }
            registries.getOptionalWrapper(RegistryKeys.ENCHANTMENT)
                    .flatMap(wrapper -> wrapper.getOptional(enchantment))
                    .ifPresent(entry -> tableBuilder.pool(LootPool.builder()
                            .rolls(ConstantLootNumberProvider.create(1.0f))
                            .conditionally(RandomChanceLootCondition.builder(BOOK_CHANCE))
                            .with(ItemEntry.builder(Items.BOOK)
                                    .apply(EnchantRandomlyLootFunction.builder(registries)
                                            .option(entry)))));
        });
    }
}
