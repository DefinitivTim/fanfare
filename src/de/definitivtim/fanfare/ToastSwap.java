// © 2026 DefinitivTim
package de.definitivtim.fanfare;

import dev.ftb.mods.ftbquests.client.gui.ToastQuestObject;
import dev.ftb.mods.ftbquests.quest.Chapter;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.QuestObject;
import java.lang.reflect.Field;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.event.ToastAddEvent;

/**
 * FTB Quests announces every completed quest, chapter and task with the same toast. When such a toast is about to be
 * shown, this looks at the tags of the completed object and replaces the toast with one of the chosen kind.
 * If anything about FTB Quests is not as expected, the original toast is left alone.
 */
public final class ToastSwap {
    private ToastSwap() {}

    private static final String TAG_PREFIX = "toast_";
    private static Field objectField;
    private static boolean broken;
    private static boolean announced;

    public static void onToastAdd(ToastAddEvent event) {
        if (broken) {
            return;
        }
        Toast toast = event.getToast();
        if (!(toast instanceof ToastQuestObject)) {
            return;
        }
        try {
            ToastQuestObject original = (ToastQuestObject) toast;
            QuestObject object = completedObject(original);
            if (object == null) {
                return;
            }
            ToastKind kind = kindFor(object);
            if (kind == ToastKind.FTB_STANDARD) {
                return;
            }
            Component title;
            if (object instanceof Quest) {
                title = Component.translatable("definitivtim_fanfare.toast.quest." + kind.key);
            } else if (object instanceof Chapter) {
                title = Component.translatable("definitivtim_fanfare.toast.chapter." + kind.key);
            } else {
                title = original.getTitle();
            }
            QuestToast replacement = new QuestToast(kind, title, original.getSubtitle(), original.getIcon());
            event.setCanceled(true);
            Minecraft.getInstance().getToasts().addToast(replacement);
            if (!announced) {
                announced = true;
                QuestAchievements.LOG.info("[Fanfare] first toast replaced (" + kind + ")");
            }
        } catch (Throwable t) {
            broken = true;
            QuestAchievements.LOG.warn("[Fanfare] disabled, FTB Quests toast is kept: " + t);
        }
    }

    public static QuestObject completedObject(ToastQuestObject toast) throws ReflectiveOperationException {
        if (objectField == null) {
            Field f = ToastQuestObject.class.getDeclaredField("object");
            f.setAccessible(true);
            objectField = f;
        }
        return (QuestObject) objectField.get(toast);
    }

    public static ToastKind kindFor(QuestObject object) {
        Set<String> tags = object.getTags();
        if (tags != null && !tags.isEmpty()) {
            if (tags.contains(TAG_PREFIX + "standard")) {
                return ToastKind.FTB_STANDARD;
            }
            for (ToastKind kind : ToastKind.values()) {
                if (kind.key != null && tags.contains(TAG_PREFIX + kind.key)) {
                    return kind;
                }
            }
        }
        if (object instanceof Quest) {
            return Settings.defaultQuest();
        }
        if (object instanceof Chapter) {
            return Settings.defaultChapter();
        }
        return ToastKind.FTB_STANDARD;
    }
}
