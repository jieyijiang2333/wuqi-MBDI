# 无期：人格鉴定（MBDI）

《无期迷途》同人二创的安卓人格测试应用：七维性格画像 → 匹配禁闭者。

> **本项目已停止更新，代码开源供学习与参考。**
> 非官方作品，与《无期迷途》官方无关；角色、名称与相关设定版权归官方所有。

## 功能

- **98 名禁闭者档案**：狂厄级 / 高危级 / 普管级 / 特殊四种等级，图鉴可按等级筛选
- **两栏五档鉴定**
  - 严格：30 / 40 / 50 题，纯人格匹配，不做收集补全
  - 娱乐：12 / 20 题，会优先补全还没收集到的角色
- **局长档案**：完成一次鉴定后自动生成玩家自己的七维画像
- **图鉴 / 狂厄特化 / 成就** 收集系统
- **6 套配色 + 深色模式**，深色模式可跟随系统
- **保存结果图**：离屏绘制 1080×1620 档案图并写入相册

## 构建

| 项 | 版本 |
| --- | --- |
| JDK | 17 |
| Android SDK | 36 |
| Gradle | 8.13（wrapper 已带） |
| minSdk | 29 |

```bash
# 1. 签名（可选；不配置则产出未签名 APK）
cp keystore.properties.example keystore.properties
# 编辑 keystore.properties，填入你自己的 keystore

# 2. 构建
./gradlew assembleRelease
```

产物：`app/build/outputs/apk/release/app-release.apk`

## 目录结构

```
app/src/main/java/com/wqmtmbdi/mbcc/
├── data/        题库、角色库（GameCharsV15.kt）、文案
├── engine/      DcpmEngine 鉴定算法（含测试金标参考实现）
├── ui/          测试 / 图鉴 / 共鸣 / 我的 / 主题 等页面
└── ...
app/src/test/    引擎黄金测试（./gradlew test）
```

## 数据来源

角色七维数据整理自官方美术设定集第一册；后续角色因官方未公布数据，未收录。
题库、文案与界面为本项目原创。

## 许可证

- **代码**：[GNU Affero General Public License v3.0](LICENSE)（AGPL-3.0）
- **角色、名称、设定**：版权归《无期迷途》官方所有，不在本协议授权范围内

本项目为非商业同人作品，与官方无任何关联。
