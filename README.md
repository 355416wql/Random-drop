# Random Drop

A Fabric mod that turns Minecraft's loot system into complete chaos. Block drops, mob drops, crafting results — all randomized.

## Features

| Feature | Description |
|---------|-------------|
| Random Block Drops | Breaking any block drops a random item from the entire game |
| Random Mob Drops | Killing any mob drops a completely random item |
| Random Crafting Results | Crafting recipes produce random items instead of the intended result |

## Configuration

All features can be toggled independently in `config/random-drop.json`:

```json
{
  "randomBlockDropsEnabled": true,
  "randomCraftingResultsEnabled": true,
  "randomMobDropsEnabled": true,
  "blockDropBlacklist": ["minecraft:air", "minecraft:barrier", "..."],
  "craftingResultBlacklist": ["minecraft:air", "minecraft:barrier", "..."],
  "mobDropBlacklist": ["minecraft:air", "minecraft:barrier", "..."]
}
```

- `randomBlockDropsEnabled` — Random block drops (enabled by default)
- `randomCraftingResultsEnabled` — Random crafting results (enabled by default)
- `randomMobDropsEnabled` — Random mob drops (enabled by default)
- `blockDropBlacklist` — Items excluded from block drops
- `craftingResultBlacklist` — Items excluded from crafting results
- `mobDropBlacklist` — Items excluded from mob drops

## Installation

1. Install Fabric Loader ≥ 0.19.2
2. Install Fabric API
3. Download the latest jar from [Releases](https://github.com/355416wql/Random-drop/releases) and place it in your `mods` folder
4. Launch the game and embrace the chaos

## Supported Versions

Minecraft 1.21.11

## License

CC0-1.0
