# Extra Enchantments & Curses — Fabric 1.20.1

A community-maintained fork of [03-JS/Extra-Enchantments-and-Curses](https://github.com/03-JS/Extra-Enchantments-and-Curses),
which was archived on 2024-09-03. The upstream README explicitly permits forking, and the project is MIT licensed.

Upstream shipped a Fabric branch targeting **1.20.2** and a Forge branch targeting **1.20.1**, but no Fabric build for
**1.20.1**. This fork fills that gap and carries bug fixes that upstream never received.

## Building

```bash
./gradlew build
```

The finished jar lands in `build/libs/`. Requires JDK 17 or newer.

## Differences from upstream 1.9

### Overshield no longer corrupts max health

Upstream implemented the Overshield health bonus by writing into the `generic.max_health` attribute's **base value**,
and remembered the value to restore with `getAttributeInstance(GENERIC_MAX_HEALTH).getValue()`. Two things go wrong:

- `getValue()` returns the base value **plus every attribute modifier**, including max health modifiers contributed by
  other mods. That total was then written back into the base value, so each equip/unequip cycle permanently inflated
  the player's max health by the size of the other mod's bonus. Equipping a Crystal Heart from *Artifacts* (+10) and
  toggling an Overshield chestplate grew max health by 10 points every cycle, without bound.
- The base value is persisted in the player's NBT, so any bonus that failed to be subtracted again — a crash, a
  relog at the wrong moment, or removing the mod — stayed forever.

The bonus is now a plain `EntityAttributeModifier` with a fixed UUID, added as a *temporary* modifier and re-asserted
every tick. Nothing is ever written to the player's save data, so no state can leak.

### Overshield is no longer silently capped to one armour piece

Upstream applied the bonus with four consecutive `setBaseValue(previous + level * 2)` calls, one per armour slot. Each
call overwrote the previous one, so only the last matching slot counted (boots winning over leggings over chestplate
over helmet) and multiple enchanted pieces never stacked. Levels from all four slots are now summed.

Related: `lastOvLevel`, used to subtract the bonus again, resolved slots in the *opposite* priority order (helmet
first), so a mismatched pair of enchanted pieces subtracted more than it ever added and permanently drained max health.
That code path is gone.

### Disabling Overshield in the config no longer kills every player

When `overshield.effectsDisabled` was set, upstream ran `setBaseValue(previousMaxHealth)` every tick, where
`previousMaxHealth` is a `static` field defaulting to `0.0`. Enabling that option set every player's max health base
value to zero. The option is now honoured by simply not applying the modifier.

### Overshield state is per-player

`previousMaxHealth` was `static`, i.e. shared by every player on a server. There is no per-player mutable state left.

### The Overshield bonus is configurable

New config option `overshieldHealthPerLevel`, default **4** health points (2 hearts) per level per enchanted piece, so
Overshield V on a chestplate grants +20 points (10 hearts). Upstream hardcoded 2. Set it back to `2` to restore the
original balance.

### Miscellaneous

- Removed four `System.out.println` debug statements that fired on every Overshield equip change.
- Fixed a stray semicolon before an import in `Overshield.java`.

## Migrating from upstream

Players who used the upstream build may already have an inflated `generic.max_health` base value baked into their save.
This fork does not touch the base value, so it cannot repair it automatically. Check and repair affected players with:

```
/attribute <player> minecraft:generic.max_health base get
/attribute <player> minecraft:generic.max_health base set 20
```

Have the player take off all Overshield-enchanted gear first.

## Credits

All enchantment and curse designs, art and translations are the work of **JS03**. This fork only ports the code to
1.20.1 and fixes bugs.
