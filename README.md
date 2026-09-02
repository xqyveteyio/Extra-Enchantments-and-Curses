# Extra Enchantments & Curses

English | [简体中文](README.zh-CN.md)

A Fabric mod that adds 29 enchantments and 10 curses to Minecraft. It is a community-maintained fork of
[03-JS/Extra-Enchantments-and-Curses](https://github.com/03-JS/Extra-Enchantments-and-Curses), which was archived in
September 2024.

## Requirements

| | |
| --- | --- |
| Minecraft | 26.1, including the 26.1.1 and 26.1.2 hotfixes |
| Mod loader | Fabric Loader 0.19.5+ |
| Dependencies | [Fabric API](https://modrinth.com/mod/fabric-api), [owo-lib](https://modrinth.com/mod/owo-lib) |
| Java | 25+ |

Minecraft dropped the `1.x` version numbers in 2026 and now numbers releases by year and game drop,
so 26.1 is the drop that follows 1.21.11. Each drop needs its own build; older ones live on the
[`1.21.1-fabric`](../../tree/1.21.1-fabric) and [`1.20.1-fabric`](../../tree/1.20.1-fabric) branches.

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

## Configuration

Each enchantment can be switched off individually through Mod Menu, or by editing
`config/extra-enchantments-and-curses-config.json5`.

Everything else — maximum level, enchanting cost, rarity, which items accept an enchantment, and which enchantments
conflict — is defined in JSON under `data/extra_enchantments/enchantment/` and can be changed with a datapack, since
Minecraft 1.21 made enchantments data-driven.

## Building

```bash
./gradlew build
```

The jar lands in `build/libs/`. Gradle fetches a Java 25 toolchain by itself if the machine has none.

## Licence

MIT, inherited from upstream. See [LICENSE](LICENSE).
