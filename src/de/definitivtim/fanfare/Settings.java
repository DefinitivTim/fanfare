// © 2026 DefinitivTim
package de.definitivtim.fanfare;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Client settings, file definitivtim_fanfare-client.toml, editable in game through the mod list. */
public final class Settings {
    private Settings() {}

    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.EnumValue<ToastKind> DEFAULT_QUEST;
    private static final ModConfigSpec.EnumValue<ToastKind> DEFAULT_CHAPTER;
    private static final ModConfigSpec.BooleanValue SOUNDS;
    private static final ModConfigSpec.BooleanValue FRAME;
    private static final ModConfigSpec.IntValue SECONDS;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();
        b.comment("A quest or chapter picks its toast with a tag: toast_task, toast_goal, toast_challenge or toast_standard. Tags are set in the quest editor of FTB Quests. Without such a tag the defaults below apply.").push("toasts");
        DEFAULT_QUEST = b.comment("Toast for quests without a toast tag. FTB_STANDARD keeps the toast of FTB Quests.")
                .defineEnum("defaultQuestToast", ToastKind.FTB_STANDARD);
        DEFAULT_CHAPTER = b.comment("Toast for chapters without a toast tag. FTB_STANDARD keeps the toast of FTB Quests.")
                .defineEnum("defaultChapterToast", ToastKind.FTB_STANDARD);
        SOUNDS = b.comment("Play a sound: none for task, level up for goal, fanfare for challenge.")
                .define("sounds", true);
        FRAME = b.comment("Draw the advancement frame of the kind around the icon.").define("iconFrame", true);
        SECONDS = b.comment("How long the toast stays, in seconds.").defineInRange("displaySeconds", 5, 2, 15);
        b.pop();
        SPEC = b.build();
    }

    private static <T> T read(ModConfigSpec.ConfigValue<T> value, T fallback) {
        try {
            T v = value.get();
            return v == null ? fallback : v;
        } catch (Throwable t) {
            return fallback;
        }
    }

    public static ToastKind defaultQuest() {
        return read(DEFAULT_QUEST, ToastKind.FTB_STANDARD);
    }

    public static ToastKind defaultChapter() {
        return read(DEFAULT_CHAPTER, ToastKind.FTB_STANDARD);
    }

    public static boolean sounds() {
        return read(SOUNDS, Boolean.TRUE).booleanValue();
    }

    public static boolean iconFrame() {
        return read(FRAME, Boolean.TRUE).booleanValue();
    }

    public static int displaySeconds() {
        return read(SECONDS, Integer.valueOf(5)).intValue();
    }
}
