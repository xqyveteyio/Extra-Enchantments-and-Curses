# 更多附魔与诅咒

[English](README.md) | 简体中文

一个为 Minecraft 添加 29 个附魔和 10 个诅咒的 Fabric 模组。本仓库是
[03-JS/Extra-Enchantments-and-Curses](https://github.com/03-JS/Extra-Enchantments-and-Curses) 的社区分支，原仓库已于
2024 年 9 月归档。

## 运行环境

| | |
| --- | --- |
| Minecraft | 26.1，含 26.1.1 与 26.1.2 修补版 |
| 加载器 | Fabric Loader 0.19.5+ |
| 前置 | [Fabric API](https://modrinth.com/mod/fabric-api)、[owo-lib](https://modrinth.com/mod/owo-lib) |
| Java | 25+ |

Minecraft 从 2026 年起不再使用 `1.x` 版本号，改为按「年份 + 当年第几次更新」编号，26.1 就是 1.21.11 之后的那一次更新。
每次更新都需要单独构建，旧版本分别在 [`1.21.1-fabric`](../../tree/1.21.1-fabric) 和
[`1.20.1-fabric`](../../tree/1.20.1-fabric) 分支。

## 内容一览

所有附魔都通过原版途径获得——附魔台、铁砧、村民交易、钓鱼和箱子战利品。不新增任何物品，也没有合成配方。

**武器** — 吸血、狂热、护佑、痛苦循环、收魂、冰冻附加、灾厄杀手、水栖之刃、生命共振、速击

**工具** — 长柄、催化

**弓与弩** — 回响、雷矢、共振射击、夜影、飘浮、白炽、蓄能、锁定

**盔甲** — 护罩、燃烧、冻结、抗冻、雷佑、充能、照耀、地狱行者、冰足

**诅咒** — 闪电、失明、凋零、反胃、虚弱、祛魔、易碎、缓慢、亡灵、慢击

## 配置

每个附魔都可以在 Mod Menu 里单独关闭，也可以直接编辑
`config/extra-enchantments-and-curses-config.json5`。

其余属性——最高等级、附魔消耗、稀有度、可附魔的物品、以及互斥关系——都定义在
`data/extra_enchantments/enchantment/` 下的 JSON 里，用数据包即可修改。这是因为 Minecraft 1.21 起附魔本身就是数据驱动的。

## 构建

```bash
./gradlew build
```

产物在 `build/libs/` 目录下。本机没有 Java 25 的话，Gradle 会自动下载一份。

## 许可

沿用上游的 MIT 许可，见 [LICENSE](LICENSE)。
