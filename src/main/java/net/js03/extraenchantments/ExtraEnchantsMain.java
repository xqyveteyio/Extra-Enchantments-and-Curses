package net.js03.extraenchantments;

import net.fabricmc.api.ModInitializer;
import net.js03.extraenchantments.config.ExtraEnchantsConfig;
import net.js03.extraenchantments.loot.ModLootTables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtraEnchantsMain implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("extra_enchants");
    public static final ExtraEnchantsConfig CONFIG = ExtraEnchantsConfig.createAndLoad();

    @Override
    public void onInitialize() {
        // The enchantments themselves need no registration: since 1.21 they are datapack entries,
        // and this mod's definitions ship under data/extra_enchantments/enchantment.
        ModLootTables.register();
    }
}
