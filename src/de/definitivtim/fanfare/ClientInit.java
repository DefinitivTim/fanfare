// © 2026 DefinitivTim
package de.definitivtim.fanfare;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.ToastAddEvent;
import net.neoforged.neoforge.common.NeoForge;

/** Everything that only exists on the client: toast settings, config screen, toast replacement. */
public final class ClientInit {
    private ClientInit() {}

    public static void init(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, Settings.SPEC);
        try {
            ConfigScreenHook.register(container);
        } catch (Throwable t) {
            QuestAchievements.LOG.warn("[Fanfare] config screen could not be registered: " + t);
        }
        NeoForge.EVENT_BUS.addListener(ToastAddEvent.class, ToastSwap::onToastAdd);
    }
}
