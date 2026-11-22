// © 2026 DefinitivTim
package de.definitivtim.fanfare;

/** How a completion is announced. FTB_STANDARD leaves the toast of FTB Quests untouched. */
public enum ToastKind {
    FTB_STANDARD(null, null, 0),
    TASK("task", "advancements/task_frame_obtained", 0xFFFFFF00),
    GOAL("goal", "advancements/goal_frame_obtained", 0xFFFFFF00),
    CHALLENGE("challenge", "advancements/challenge_frame_obtained", 0xFFFF88FF);

    /** Part of the quest tag (toast_task, toast_goal, toast_challenge) and of the language key. */
    public final String key;
    public final String frameSprite;
    public final int titleColor;

    ToastKind(String key, String frameSprite, int titleColor) {
        this.key = key;
        this.frameSprite = frameSprite;
        this.titleColor = titleColor;
    }
}
