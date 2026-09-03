package net.js03.extraenchantments.registry;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

import java.util.Comparator;

/**
 * A creative tab holding a book for every level of every enchantment this mod ships, so they can
 * be found without searching for each name. The entries are read from the dynamic registry rather
 * than a hardcoded list, which keeps datapack additions and removals in sync.
 */
public final class ModItemGroups {

    public static final ResourceKey<CreativeModeTab> ENCHANTED_BOOKS = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath(ModEnchantments.NAMESPACE, "enchanted_books"));

    // Regular enchantments first, curses last, each block sorted by registry name.
    private static final Comparator<Holder.Reference<Enchantment>> DISPLAY_ORDER = Comparator
            .comparing((Holder.Reference<Enchantment> holder) -> holder.is(EnchantmentTags.CURSE))
            .thenComparing(holder -> holder.key().identifier().getPath());

    private ModItemGroups() {
    }

    public static void register() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ENCHANTED_BOOKS, FabricCreativeModeTab.builder()
                .title(Component.translatable("itemGroup.extra_enchantments.enchanted_books"))
                .icon(() -> new ItemStack(Items.ENCHANTED_BOOK))
                .displayItems((parameters, output) -> parameters.holders()
                        .lookup(Registries.ENCHANTMENT)
                        .ifPresent(lookup -> addBooks(output, lookup)))
                .build());
    }

    private static void addBooks(CreativeModeTab.Output output, HolderLookup<Enchantment> lookup) {
        lookup.listElements()
                .filter(holder -> holder.key().identifier().getNamespace().equals(ModEnchantments.NAMESPACE))
                .sorted(DISPLAY_ORDER)
                .forEach(holder -> {
                    for (int level = 1; level <= holder.value().getMaxLevel(); level++) {
                        output.accept(EnchantmentHelper.createBook(new EnchantmentInstance(holder, level)));
                    }
                });
    }
}
