# Random Drop - 随机掉落

一个让 Minecraft 世界彻底"失控"的 Fabric 模组。挖方块、打怪、合成——全部随机掉落。

## 功能

| 功能 | 说明 |
|------|------|
| 随机方块掉落 | 挖掘任意方块时，掉落物从全游戏物品中随机抽取 |
| 随机生物掉落 | 击杀生物时，掉落物完全随机 |
| 随机合成结果 | 合成配方产出的物品被随机替换 |

## 配置

所有功能可独立开关，配置文件位于 `config/random-drop.json`：

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

- `randomBlockDropsEnabled` — 随机方块掉落（默认开启）
- `randomCraftingResultsEnabled` — 随机合成结果（默认开启）
- `randomMobDropsEnabled` — 随机生物掉落（默认开启）
- `blockDropBlacklist` — 方块掉落黑名单
- `craftingResultBlacklist` — 合成结果黑名单
- `mobDropBlacklist` — 生物掉落黑名单

## 安装

1. 安装 Fabric Loader ≥ 0.19.2
2. 安装 Fabric API
3. 从 [Releases](https://github.com/355416wql/Random-drop/releases) 页面下载最新 jar，放入 `mods` 文件夹
4. 启动游戏，享受混乱

## 适用版本

Minecraft 1.21.11

## 协议

CC0-1.0
