// © 2026 DefinitivTim
package de.definitivtim.fanfare;

import java.util.function.Supplier;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/** Adds the Config button in the mod list, using the config screen that ships with NeoForge. */
public final class ConfigScreenHook {
    private ConfigScreenHook() {}

    public static void register(ModContainer container) {
        Supplier<IConfigScreenFactory> factory = () -> (mod, parent) -> new ConfigurationScreen(mod, parent);
        container.registerExtensionPoint(IConfigScreenFactory.class, factory);
    }
}
