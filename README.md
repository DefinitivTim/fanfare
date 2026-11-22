<img src="logo.png" width="256" alt="DefinitivTim's Fanfare logo">

# DefinitivTim's Fanfare

Lets every quest of [FTB Quests](https://www.curseforge.com/minecraft/mc-mods/ftb-quests-forge) choose how its
completion is announced: as a task, a goal or a challenge.

- Minecraft 1.21.1, NeoForge 21.1
- Needs FTB Quests 2101.1.38 or newer. Works together with Fancy Toasts 1.5.1
- Client side only. Players without the mod see the normal FTB Quests toast
- Mod ID `definitivtim_fanfare`, current version 0.3.0
- License: MIT. Made with AI assistance, see [AI notice](#ai-notice)

## How to use

Open a quest in the FTB Quests editor, edit its tags and add one of these. The same tags work on chapters.

| Tag | Toast |
|---|---|
| `toast_task` | "Quest Complete!", look of a task advancement |
| `toast_goal` | "Goal Reached!", look and sound of a goal advancement |
| `toast_challenge` | "Challenge Complete!", look and fanfare of a challenge advancement |
| `toast_standard` | the unchanged toast, even if a default is set in the config |

FTB Quests only allows lower case letters, digits and underscores in tags. Quests without a tag use the default
from the config. "Disable toast" of FTB Quests still switches the toast off completely.

## With and without Fancy Toasts

With Fancy Toasts installed the quest toast keeps the Fancy Toasts look, and this mod sets its type, colours,
headline and sound from the tag. Without it the mod shows its own toast in the look of a vanilla advancement toast.

## Config

`config/definitivtim_fanfare-client.toml`, or in game: mod list, DefinitivTim's Fanfare, Config.
Default kind for quests and for chapters, sounds on or off. Icon frame and display time only apply without Fancy Toasts.

## Status

The mod reads internals of FTB Quests and of Fancy Toasts. After an update of either it can switch itself off, the
normal toast is then shown and the log says so. FTB Quests is All Rights Reserved: this mod contains none of its
code and only calls it at runtime.

## Building

There is no Gradle project yet. The released jar is compiled with plain `javac --release 21` against the small
stand-in classes in `stubs/` plus the jars of the patched mods, then packed with the files from `res/`.
`docs/DEVELOPMENT_NOTES.md` has the details and the history of every change.

## License

MIT, see [LICENSE](LICENSE). Free to use in modpacks and in other projects.
The mods this one patches at runtime keep their own licenses. This mod contains none of their code.

## AI notice

The code of DefinitivTim's Fanfare was written by Claude (Anthropic), an AI. The idea, the requirements, the design decisions and
the testing come from DefinitivTim: he decided what the mod does and tested it in game in his modpack. Bug reports
and pull requests are welcome.
