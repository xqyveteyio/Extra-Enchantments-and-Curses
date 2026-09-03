package net.js03.extraenchantments;

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
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ExtraEnchantsItemGroup {
    public static final String NAMESPACE = "extra_enchantments";
    public static final RegistryKey<ItemGroup> ENCHANTED_BOOKS_KEY =
            RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier(NAMESPACE, "enchanted_books"));

    // Regular enchantments come first, curses last, each block sorted by registry name.
    private static final Comparator<Enchantment> DISPLAY_ORDER = Comparator
            .comparing((Enchantment enchantment) -> enchantment.isCursed())
            .thenComparing(enchantment -> Registries.ENCHANTMENT.getId(enchantment).getPath());

    public static void register() {
        Registry.register(Registries.ITEM_GROUP, ENCHANTED_BOOKS_KEY, FabricItemGroup.builder()
                .displayName(Text.translatable("itemGroup.extra_enchantments.enchanted_books"))
                .icon(() -> new ItemStack(Items.ENCHANTED_BOOK))
                .entries((displayContext, entries) -> {
                    for (Enchantment enchantment : modEnchantments()) {
                        for (int level = enchantment.getMinLevel(); level <= enchantment.getMaxLevel(); level++) {
                            entries.add(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantment, level)));
                        }
                    }
                })
                .build());
    }

    private static List<Enchantment> modEnchantments() {
        return Registries.ENCHANTMENT.getEntrySet().stream()
                .filter(entry -> entry.getKey().getValue().getNamespace().equals(NAMESPACE))
                .map(Map.Entry::getValue)
                .sorted(DISPLAY_ORDER)
                .toList();
    }
}
