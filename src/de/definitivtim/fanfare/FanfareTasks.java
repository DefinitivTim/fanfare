// © 2026 DefinitivTim
package de.definitivtim.fanfare;

import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import dev.ftb.mods.ftbquests.quest.task.TaskTypes;
import net.minecraft.resources.ResourceLocation;

/** Task types this mod adds to FTB Quests. Registered on server and client alike, before any quest file is read. */
public final class FanfareTasks {
    private FanfareTasks() {}

    public static TaskType CHAPTER_PROGRESS;

    public static void register() {
        try {
            CHAPTER_PROGRESS = TaskTypes.register(
                    ResourceLocation.fromNamespaceAndPath(QuestAchievements.MOD_ID, "chapter_progress"),
                    ChapterProgressTask::new,
                    () -> Icons.BOOK);
            QuestAchievements.LOG.info("[Fanfare] quest task type chapter_progress registered");
        } catch (Throwable t) {
            QuestAchievements.LOG.warn("[Fanfare] quest task type chapter_progress could not be registered, quests using it will not load: " + t);
        }
    }
}
