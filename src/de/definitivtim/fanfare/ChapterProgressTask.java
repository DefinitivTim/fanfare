// © 2026 DefinitivTim
package de.definitivtim.fanfare;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.quest.Chapter;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.QuestObject;
import dev.ftb.mods.ftbquests.quest.QuestObjectType;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import dev.ftb.mods.ftbquests.util.ConfigQuestObject;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * Task "Chapter progress": counts the completed quests of a chapter (x/y) and completes itself once all of them are
 * done. The chapter can be picked in the editor; without a choice it is the chapter this quest is in.
 *
 * Never counted: the quest that holds this task, quests that depend on it (directly or further down the line, they
 * could never be done first), repeatable quests, and other quests that count the same chapter (two of them would wait
 * for each other forever). Optional quests are counted only if the switch in the editor is on.
 */
public class ChapterProgressTask extends Task {
    /** Recount at most once a second; the quest list of a chapter only changes while editing. */
    private static final long RECOUNT_NANOS = 1_000_000_000L;

    /** 0 means: the chapter this quest is in. */
    private long chapterId;
    private boolean includeOptional;

    private List<Quest> counted;
    private long countedAt;

    public ChapterProgressTask(long id, Quest quest) {
        super(id, quest);
    }

    @Override
    public TaskType getType() {
        return FanfareTasks.CHAPTER_PROGRESS;
    }

    /** The chapter that is counted. */
    public Chapter targetChapter() {
        if (this.chapterId != 0L) {
            Chapter chosen = getQuestFile().getChapter(this.chapterId);
            if (chosen != null) {
                return chosen;
            }
        }
        return getQuestChapter();
    }

    @Override
    public long getMaxProgress() {
        return Math.max(1, countedQuests().size());
    }

    @Override
    public int autoSubmitOnPlayerTick() {
        return 20;
    }

    @Override
    public void submitTask(TeamData teamData, ServerPlayer player, ItemStack craftedItem) {
        if (teamData.isCompleted(this) || !checkTaskSequence(teamData)) {
            return;
        }
        long done = completedCount(teamData);
        if (done != teamData.getProgress(this)) {
            teamData.setProgress(this, done);
        }
    }

    private long completedCount(TeamData teamData) {
        List<Quest> quests = countedQuests();
        if (quests.isEmpty()) {
            return 1L;
        }
        long done = 0L;
        for (Quest quest : quests) {
            if (teamData.isCompleted(quest)) {
                done++;
            }
        }
        return done;
    }

    private List<Quest> countedQuests() {
        long now = System.nanoTime();
        List<Quest> list = this.counted;
        if (list == null || now - this.countedAt > RECOUNT_NANOS) {
            list = collectQuests();
            this.counted = list;
            this.countedAt = now;
        }
        return list;
    }

    private List<Quest> collectQuests() {
        Chapter chapter = targetChapter();
        if (chapter == null) {
            return Collections.emptyList();
        }
        Quest own = getQuest();
        Set<Long> after = dependantsOf(own);
        List<Quest> list = new ArrayList<>();
        for (Quest quest : chapter.getQuests()) {
            if (quest == own || after.contains(quest.id) || quest.canBeRepeated()) {
                continue;
            }
            if (!this.includeOptional && quest.isOptional()) {
                continue;
            }
            if (countsChapter(quest, chapter)) {
                continue;
            }
            list.add(quest);
        }
        return list;
    }

    /** All quests that need the given quest first, directly or further down the line. */
    private static Set<Long> dependantsOf(Quest start) {
        Set<Long> seen = new HashSet<>();
        ArrayDeque<Quest> open = new ArrayDeque<>();
        open.add(start);
        while (!open.isEmpty()) {
            Quest quest = open.poll();
            for (QuestObject next : quest.getDependants()) {
                if (next instanceof Quest && seen.add(next.id)) {
                    open.add((Quest) next);
                }
            }
        }
        return seen;
    }

    private static boolean countsChapter(Quest quest, Chapter chapter) {
        for (Task task : quest.getTasks()) {
            if (task instanceof ChapterProgressTask && ((ChapterProgressTask) task).targetChapter() == chapter) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Component getAltTitle() {
        Chapter chapter = targetChapter();
        if (chapter == null) {
            return super.getAltTitle();
        }
        return Component.translatable("definitivtim_fanfare.task.chapter_progress.title", chapter.getTitle());
    }

    @Override
    public Icon getAltIcon() {
        try {
            Chapter chapter = targetChapter();
            if (chapter != null) {
                return chapter.getIcon();
            }
        } catch (Throwable t) {
            // fall back to the icon of the task type
        }
        return super.getAltIcon();
    }

    @Override
    public void writeData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.writeData(nbt, provider);
        if (this.chapterId != 0L) {
            nbt.putLong("chapter", this.chapterId);
        }
        if (this.includeOptional) {
            nbt.putBoolean("include_optional", true);
        }
    }

    @Override
    public void readData(CompoundTag nbt, HolderLookup.Provider provider) {
        super.readData(nbt, provider);
        this.chapterId = nbt.getLong("chapter");
        this.includeOptional = nbt.getBoolean("include_optional");
        this.counted = null;
    }

    @Override
    public void writeNetData(RegistryFriendlyByteBuf buffer) {
        super.writeNetData(buffer);
        buffer.writeVarLong(this.chapterId);
        buffer.writeBoolean(this.includeOptional);
    }

    @Override
    public void readNetData(RegistryFriendlyByteBuf buffer) {
        super.readNetData(buffer);
        this.chapterId = buffer.readVarLong();
        this.includeOptional = buffer.readBoolean();
        this.counted = null;
    }

    @Override
    public void fillConfigGroup(ConfigGroup config) {
        super.fillConfigGroup(config);
        Chapter chosen = this.chapterId == 0L ? null : getQuestFile().getChapter(this.chapterId);
        config.add("chapter", new ConfigQuestObject<Chapter>(QuestObjectType.CHAPTER), chosen, value -> {
            this.chapterId = value == null ? 0L : value.id;
            this.counted = null;
        }, null).setNameKey("definitivtim_fanfare.task.chapter_progress.chapter");
        config.addBool("include_optional", this.includeOptional, value -> {
            this.includeOptional = value.booleanValue();
            this.counted = null;
        }, false).setNameKey("definitivtim_fanfare.task.chapter_progress.include_optional");
    }

    @Override
    public void clearCachedData() {
        super.clearCachedData();
        this.counted = null;
    }
}
