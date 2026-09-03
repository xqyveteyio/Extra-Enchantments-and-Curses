package net.js03.extraenchantments.registry;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Comparator;

/**
 * A creative tab holding a book for every level of every enchantment this mod ships, so they can
 * be found without searching for each name. The entries are read from the dynamic registry rather
 * than a hardcoded list, which keeps datapack additions and removals in sync.
 */
public final class ModItemGroups {

    public static final RegistryKey<ItemGroup> ENCHANTED_BOOKS = RegistryKey.of(
            RegistryKeys.ITEM_GROUP, Identifier.of(ModEnchantments.NAMESPACE, "enchanted_books"));

    // Regular enchantments first, curses last, each block sorted by registry name.
    private static final Comparator<RegistryEntry.Reference<Enchantment>> DISPLAY_ORDER = Comparator
            .comparing((RegistryEntry.Reference<Enchantment> entry) -> entry.isIn(EnchantmentTags.CURSE))
            .thenComparing(entry -> entry.registryKey().getValue().getPath());

    private ModItemGroups() {
    }

    public static void register() {
        Registry.register(Registries.ITEM_GROUP, ENCHANTED_BOOKS, FabricItemGroup.builder()
                .displayName(Text.translatable("itemGroup.extra_enchantments.enchanted_books"))
                .icon(() -> new ItemStack(Items.ENCHANTED_BOOK))
                .entries((displayContext, entries) -> displayContext.lookup()
                        .getOptionalWrapper(RegistryKeys.ENCHANTMENT)
                        .ifPresent(wrapper -> addBooks(entries, wrapper)))
                .build());
    }

    private static void addBooks(ItemGroup.Entries entries, RegistryWrapper.Impl<Enchantment> wrapper) {
        wrapper.streamEntries()
                .filter(entry -> entry.registryKey().getValue().getNamespace().equals(ModEnchantments.NAMESPACE))
                .sorted(DISPLAY_ORDER)
                .forEach(entry -> {
                    for (int level = 1; level <= entry.value().getMaxLevel(); level++) {
                        entries.add(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(entry, level)));
                    }
                });
    }
}
