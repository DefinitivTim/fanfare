// © 2026 DefinitivTim
package de.definitivtim.fanfare;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.ToastAddEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DefinitivTim's Fanfare: lets each quest of FTB Quests choose the kind of its completion toast
 * (task, goal, challenge) with a quest tag. Client only, no network packets, no quest data of its own.
 */
@Mod(value = QuestAchievements.MOD_ID, dist = Dist.CLIENT)
public class QuestAchievements {
    public static final String MOD_ID = "definitivtim_fanfare";
    public static final String VERSION = "0.3.0";
    public static final Logger LOG = LoggerFactory.getLogger("Fanfare");

    public QuestAchievements(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, Settings.SPEC);
        try {
            ConfigScreenHook.register(container);
        } catch (Throwable t) {
            LOG.warn("[Fanfare] config screen could not be registered: " + t);
        }
        NeoForge.EVENT_BUS.addListener(ToastAddEvent.class, ToastSwap::onToastAdd);
        LOG.info("[Fanfare] " + VERSION + " loaded");
    }
}
