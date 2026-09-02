package net.js03.extraenchantments.loot;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.js03.extraenchantments.registry.ModEnchantments;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.Map;

/**
 * Upstream shipped full copies of three vanilla loot tables to slip its books in, which silently
 * loses any change another datapack or mod makes to the same table. These are added as an extra
 * pool instead.
 */
public final class ModLootTables {

    private static final Map<ResourceKey<LootTable>, ResourceKey<Enchantment>> BOOKS = Map.of(
            BuiltInLootTables.ANCIENT_CITY, ModEnchantments.OVERSHIELD,
            BuiltInLootTables.BASTION_OTHER, ModEnchantments.HELLWALKER,
            BuiltInLootTables.PIGLIN_BARTERING, ModEnchantments.HELLWALKER
    );

    private static final float BOOK_CHANCE = 0.15f;

    private ModLootTables() {
    }

    public static void register() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (!source.isBuiltin()) {
                return;
            }
            ResourceKey<Enchantment> enchantment = BOOKS.get(key);
            if (enchantment == null) {
                return;
            }
            registries.lookup(Registries.ENCHANTMENT)
                    .flatMap(lookup -> lookup.get(enchantment))
                    .ifPresent(holder -> tableBuilder.withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0f))
                            .when(LootItemRandomChanceCondition.randomChance(BOOK_CHANCE))
                            .add(LootItem.lootTableItem(Items.BOOK)
                                    .apply(EnchantRandomlyFunction.randomEnchantment()
                                            .withOneOf(HolderSet.direct(holder))))));
        });
    }
}
