// © 2026 DefinitivTim
package de.definitivtim.fanfare;

import dev.ftb.mods.ftbquests.client.gui.ToastQuestObject;
import dev.ftb.mods.ftbquests.quest.Chapter;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.QuestObject;
import java.lang.reflect.Field;
import net.bivrik.fancytoasts.client.config.data.ToastConfigData;
import net.bivrik.fancytoasts.platform.utility.AdvancementDisplay;
import net.bivrik.fancytoasts.platform.utility.AdvancementType;
import net.bivrik.fancytoasts.platform.utility.FancyAdvancementType;
import net.bivrik.fancytoasts.platform.utility.QuestType;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * With Fancy Toasts installed, quest toasts never reach the normal toast list: Fancy Toasts takes them over and shows
 * its own toast, with a look that only depends on whether a quest, a chapter or the whole book was completed.
 * This changes the data Fancy Toasts built for the toast, so the look and the sound follow the toast tag of the quest.
 * Only loaded through the two Fancy Toasts mixins, so it is never touched without Fancy Toasts.
 */
public final class FancyBridge {
    private FancyBridge() {}

    private static Field typeField;
    private static Field titleColorField;
    private static Field descriptionColorField;
    private static Field announcementField;
    private static boolean broken;
    private static boolean announced;
    /** Kind of the toast that is being handed to Fancy Toasts right now, for the sound lookup that follows. */
    private static ToastKind pending;

    public static void restyle(Toast toast, AdvancementDisplay display) {
        pending = null;
        if (broken || display == null || !(toast instanceof ToastQuestObject)) {
            return;
        }
        try {
            QuestObject object = ToastSwap.completedObject((ToastQuestObject) toast);
            if (object == null) {
                return;
            }
            ToastKind kind = ToastSwap.kindFor(object);
            if (kind == ToastKind.FTB_STANDARD) {
                return;
            }
            FancyAdvancementType fancy = fancyType(kind);
            if (typeField == null) {
                typeField = field("type");
                titleColorField = field("titleColor");
                descriptionColorField = field("descriptionColor");
                announcementField = field("announcement");
            }
            typeField.set(display, fancy.getConventionalType());
            titleColorField.set(display, fancy.getTitleColor());
            descriptionColorField.set(display, fancy.getDescriptionColor());
            if (object instanceof Quest) {
                announcementField.set(display, Component.translatable("definitivtim_fanfare.toast.quest." + kind.key));
            } else if (object instanceof Chapter) {
                announcementField.set(display, Component.translatable("definitivtim_fanfare.toast.chapter." + kind.key));
            }
            pending = kind;
            if (!announced) {
                announced = true;
                QuestAchievements.LOG.info("[Fanfare] first Fancy Toasts quest toast restyled (" + kind + ")");
            }
        } catch (Throwable t) {
            broken = true;
            pending = null;
            QuestAchievements.LOG.warn("[Fanfare] Fancy Toasts bridge disabled, its normal quest toast is kept: " + t);
        }
    }

    /** Sound for the toast: the one Fancy Toasts has configured for advancements of the chosen kind. */
    public static ResourceLocation sound(ToastConfigData data, QuestType questType) {
        ToastKind kind = pending;
        pending = null;
        if (kind != null && !broken && Settings.sounds()) {
            try {
                ResourceLocation id = data.getSoundIdByType(fancyType(kind));
                if (id != null) {
                    return id;
                }
            } catch (Throwable t) {
                // fall through to the sound Fancy Toasts would have used
            }
        }
        return data.getSoundIdByQuestType(questType);
    }

    private static FancyAdvancementType fancyType(ToastKind kind) {
        switch (kind) {
            case GOAL:
                return FancyAdvancementType.GOAL;
            case CHALLENGE:
                return FancyAdvancementType.CHALLENGE;
            default:
                return FancyAdvancementType.TASK;
        }
    }

    private static Field field(String name) throws ReflectiveOperationException {
        Field f = AdvancementDisplay.class.getDeclaredField(name);
        f.setAccessible(true);
        return f;
    }
}
