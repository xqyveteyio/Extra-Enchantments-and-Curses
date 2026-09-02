# Extra Enchantments & Curses

English | [简体中文](README.zh-CN.md)

A Fabric mod that adds 29 enchantments and 10 curses to Minecraft. It is a community-maintained fork of
[03-JS/Extra-Enchantments-and-Curses](https://github.com/03-JS/Extra-Enchantments-and-Curses), which was archived in
September 2024.

## Versions

Minecraft breaks mod compatibility between releases, so each supported version has its own branch and its own jar.
This branch holds no mod code — only the build workflow.

| Minecraft | Branch | Mod version | Java |
| --- | --- | --- | --- |
| 26.1 (incl. 26.1.1 / 26.1.2) | [`26.1-fabric`](../../tree/26.1-fabric) | 1.11.0 | 25 |
| 1.21.1 | [`1.21.1-fabric`](../../tree/1.21.1-fabric) | 1.10.0 | 21 |
| 1.20.1 | [`1.20.1-fabric`](../../tree/1.20.1-fabric) | 1.9.2 | 17 |

Minecraft dropped the `1.x` numbering in 2026 and now numbers releases by year and game drop, so 26.1 is the drop that
follows 1.21.11. Every version needs [Fabric API](https://modrinth.com/mod/fabric-api) and
[owo-lib](https://modrinth.com/mod/owo-lib).

## What's inside

Everything is obtained the normal way — the enchanting table, anvils, villager trades, fishing, and chest loot. Nothing
is craftable and no new items are added.

**Weapons** — Lifesteal, Frenzy, Guarding Strike, Pain Cycle, Soul Reaper, Freezing Aspect, Illager's Bane,
Fisherman's Blade, Enigma Resonator, Swiftness

**Tools** — Reach, Experience Catalyst

**Bows and crossbows** — Echo, Electrifying Shot, Resonating Shot, Shadow Shot, Levitational Shot, Incandescent,
Supercharge, Target Lock

**Armour** — Overshield, Burning Thorns, Freezing Thorns, Ice Protection, Electrified, Energized, Spectral Vision,
Hellwalker, Cold Feet

**Curses** — Zeus, Blindness, Withering, Nausea, Weakness, Incompatibility, Fragility, Slowness, the Undead, Attrition

Each enchantment can be switched off individually through Mod Menu. On 1.21 and newer, everything else — maximum level,
enchanting cost, rarity, which items accept an enchantment, and which enchantments conflict — is defined in JSON and can
be changed with a datapack.

## Building

Under **Actions → Build → Run workflow**. Leave the input at `all` to build every version, or name the ones you want:

```
26.1-fabric, 1.21.1-fabric
```

The workflow picks the right Java version per branch and uploads one artifact per branch. New version branches are
detected automatically as long as they are named `<version>-fabric`.

To build locally, check out the branch you want and run `./gradlew build`; the jar lands in `build/libs/`.

## Licence

MIT, inherited from upstream. See [LICENSE](../../blob/1.20.1-fabric/LICENSE).
