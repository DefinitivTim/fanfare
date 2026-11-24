> Renamed on 2026-11-22: formerly `farbound_quest_achievements` (0.2.0). New mod ID `definitivtim_fanfare`, new package, own icon, version 0.3.0. Behaviour is unchanged: the jar was produced by renaming the compiled 0.2.0 jar, not by a new compile.
> Config files are now named after the new mod ID. Remove the old jar when installing this one.

# 0.4.0 (2026-11-24): Chapter Progress task

New quest task type `definitivtim_fanfare:chapter_progress` ("Chapter Progress" / "Kapitel-Fortschritt").
Shows `done/total` for the quests of a chapter and completes when all are done.

## Design decisions (DefinitivTim)
- Lives in Fanfare, not in a separate mod. Accepted consequence: Fanfare is no longer client only.
- Chapter is selectable in the editor. Not set (stored as 0) means the chapter the quest is in, so moving the quest
  to another chapter moves the target along.
- Switch "Count optional quests", default off.
- Completes automatically, no click needed.

## How it works
- Registered in the mod constructor with `TaskTypes.register` (public API of FTB Quests, same way as More Quest Types),
  on both sides. `@Mod` no longer has `dist = CLIENT`. Client parts (config, config screen, toast listener) moved to
  `ClientInit`, called only when `FMLEnvironment.dist == CLIENT`, so a dedicated server never loads client classes.
- `ChapterProgressTask extends Task`. `autoSubmitOnPlayerTick() = 20`: FTB Quests calls `submitTask` once a second per
  online player while the task is open and the quest can be started. `submitTask` sets the progress to the number of
  counted quests the team has completed. `TeamData.setProgress` clamps to `getMaxProgress`.
- `getMaxProgress` = number of counted quests (at least 1; an empty chapter counts as done). Recomputed at most once a
  second and after `readData`, `readNetData`, editor changes and `clearCachedData`.
- Not counted: the own quest, every quest that depends on it (transitively, via `Quest.getDependants`), repeatable
  quests (`canBeRepeated`), optional quests unless switched on, and quests that contain a Chapter Progress task for the
  same chapter (two of them would block each other).
- Not taken into account: team specific exclusion by "other questline" (`isExcludedByOtherQuestline`), because
  `getMaxProgress` has no team. Quests excluded that way still count.
- Data: SNBT keys `chapter` (long, only written when set) and `include_optional` (only written when true).
  Network: `writeVarLong(chapter)`, `writeBoolean(includeOptional)`.
- Editor: `ConfigQuestObject<Chapter>(QuestObjectType.CHAPTER)` like `RandomReward` does for tables, plus `addBool`.
- Title in the quest view: "Complete chapter <name>", icon: the chapter icon. Type icon: `Icons.BOOK`.

## Sides and compatibility
- Server without Fanfare but a quest file with this task: FTB Quests cannot create the task ("Unknown task type").
- Client without Fanfare: FTB Quests skips unknown task types in its type table on login, so joining works as long as
  no quest uses this task. Once one does, every client needs Fanfare. NeoForge does not enforce this.
- If `TaskTypes.register` fails after an FTB Quests update, the log says
  `quest task type chapter_progress could not be registered` and quests using it will not load. Toasts keep working.

## New stubs
`nbt/CompoundTag`, `core/HolderLookup`, `network/FriendlyByteBuf`, `network/RegistryFriendlyByteBuf`,
`server/level/ServerPlayer`, plus `Component.translatable(String, Object...)` and
`ResourceLocation.fromNamespaceAndPath`. Every descriptor was checked against calls in FTB Quests 2101.1.38 bytecode.

## Verified
- Compiled with `javac --release 21` against FTB Quests 2101.1.38, FTB Library 2101.1.39, FTB Teams 2101.1.12,
  Architectury 13.0.12, Fancy Toasts 1.5.1 (from the Farbound Staging profile).
- All references to FTB Quests, FTB Library and Fancy Toasts resolved against the real jars. All Minecraft and
  NeoForge references are also used by FTB Quests or by Fanfare 0.3.0.
- Toast classes are bytecode identical to 0.3.0 (constant pool indices aside).
- Not tested in game.

# DefinitivTim's Fanfare 0.2.0

Client side addon for FTB Quests (NeoForge 1.21.1). Each quest or chapter can choose the kind of its completion toast.

## How to use
Open the quest in the FTB Quests editor, edit its tags and add one of:

| Tag | Toast |
|---|---|
| `toast_task` | "Quest Complete!", square frame, yellow title, no extra sound |
| `toast_goal` | "Goal Reached!", rounded frame, yellow title, level up sound |
| `toast_challenge` | "Challenge Complete!", spiked frame, purple title, fanfare |
| `toast_standard` | the unchanged toast of FTB Quests, even if a default is set |

FTB Quests only allows lower case letters, digits and underscores in tags, hence the underscore.
Quests without a tag use the default from the config (FTB_STANDARD = unchanged).
The same tags work on chapters. "Disable toast" of FTB Quests still switches the toast off completely.

## Config
`config/definitivtim_fanfare-client.toml`, in game: mod list, DefinitivTim's Fanfare, Config.
Default kind for quests and for chapters, sounds on/off, icon frame on/off, display time.

## How it works
Listens to NeoForge's `ToastAddEvent`. When FTB Quests adds its `ToastQuestObject`, the mod reads the tags of the
completed object, cancels that toast and adds its own. If FTB Quests changes internally, the mod switches itself off
and the normal toast is shown (log line `disabled, FTB Quests toast is kept`).

## With Fancy Toasts (0.2.0)
Fancy Toasts 1.5.1 takes quest toasts over before NeoForge's event fires, so 0.1.0 never saw them. 0.2.0 adds two
mixins into Fancy Toasts: after `FTBQuestsCompat.getDisplayInfo` the type, colours and headline of the toast data are
set from the quest tag, and in `ToastHandler.handleFTBQuestsToast` the sound is taken from the Fancy Toasts setting for
advancements of that kind. The look is then the Fancy Toasts look for task, goal or challenge advancements.
Log line: `first Fancy Toasts quest toast restyled`. The settings `iconFrame` and `displaySeconds` only apply to the
vanilla style toast used without Fancy Toasts.

## Install
Client only. Not needed on the server. Requires FTB Quests 2101.1.38 or newer.
Tags are part of the quest data, so they reach all players with the quest files. Players without this mod see the
normal FTB Quests toast.

## Status and risks
- Not tested in game. Verified: compile against FTB Quests 2101.1.38 and FTB Library, link check of all references.
- Reads the private field `object` of `ToastQuestObject` by reflection. An FTB Quests update can break that (safe fallback).
- The Fancy Toasts bridge sets private fields of its `AdvancementDisplay` by reflection and targets internals of 1.5.1. An update can break it (safe fallback: normal Fancy Toasts quest toast).
- If the completion style in the FTB Quests client config is not "toast", FTB Quests shows no toast and this mod does nothing.

## License note
FTB Quests is All Rights Reserved. This mod contains none of its code and only calls it at runtime.

## Build
`javac --release 21` against `stubs` plus the FTB Quests and FTB Library jars, then `jar cfm` with
`res/neoforge.mods.toml` in `META-INF`. Sources, stubs and resources are in `_build_src.zip`.

## License
MIT, see `LICENSE`. Free to use in modpacks and in other projects.
Mods this one patches at runtime keep their own licenses. This mod contains none of their code.

## AI notice
The code of DefinitivTim's Fanfare was written by Claude (Anthropic), an AI. The idea, the requirements, the design decisions and
the testing come from DefinitivTim: he decided what the mod does and tested it in game in his modpack.
