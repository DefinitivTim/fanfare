// © 2026 DefinitivTim
package de.definitivtim.fanfare;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DefinitivTim's Fanfare: lets each quest of FTB Quests choose the kind of its completion toast
 * (task, goal, challenge) with a quest tag, and adds the quest task type "Chapter progress".
 * The toast part is client only. The task type is registered on both sides, because FTB Quests
 * reads quest files on the server and syncs them to the clients.
 */
@Mod(QuestAchievements.MOD_ID)
public class QuestAchievements {
    public static final String MOD_ID = "definitivtim_fanfare";
    public static final String VERSION = "0.4.0";
    public static final Logger LOG = LoggerFactory.getLogger("Fanfare");

    public QuestAchievements(ModContainer container) {
        FanfareTasks.register();
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientInit.init(container);
        }
        LOG.info("[Fanfare] " + VERSION + " loaded");
    }
}
