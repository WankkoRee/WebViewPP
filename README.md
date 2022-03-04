# Enable WebView Debugging [启用 WebView 调试]

[![Kotlin](https://img.shields.io/badge/-Kotlin-7F52FF?style=flat&logo=Kotlin&logoColor=white)](#)
[![Xposed](https://img.shields.io/badge/-Xposed-3DDC84?style=flat&logo=Android&logoColor=white)](#)
[![GitHub Repo stars](https://img.shields.io/github/stars/WankkoRee/EnableWebViewDebugging?label=Github%20Stars&style=flat "GitHub Repo stars")](https://github.com/WankkoRee/EnableWebViewDebugging)
[![GitHub Downloads](https://img.shields.io/github/downloads/WankkoRee/EnableWebViewDebugging/total?label=GitHub%20Downloads&logo=github&style=flat)](https://github.com/WankkoRee/EnableWebViewDebugging/releases)
[![LSPosed Downloads](https://img.shields.io/github/downloads/Xposed-Modules-Repo/cn.wankkoree.xposed.enablewebviewdebugging/total?label=LSPosed%20Downloads&logo=Android&style=flat&labelColor=F48FB1&logoColor=ffffff)](https://modules.lsposed.org/module/cn.wankkoree.xposed.enablewebviewdebugging)

Enable WebView debugging and add vConsole in it. Support WebView, TBS X5, UC U4, Crosswalk(XWalk), XWeb.
>启用 WebView 调试并添加 vConsole，支持 WebView、TBS X5、UC U4、Crosswalk(XWalk)、XWeb。

## Todo / 计划

- [x] 增加对`UC U4`的支持
- [x] 增加对`Crosswalk`的支持
- [x] 增加对`XWeb`的支持
- [] 增加注入`eruda`

## Credits / 感谢

- [feix760/WebViewDebugHook](https://github.com/feix760/WebViewDebugHook)
  
  Referenced the implementation about WebView debugging.
  >参考了 WebView 调试的相关实现。

- [kooritea/debugwebview](https://github.com/kooritea/debugwebview)

  The inspiration for vConsole injection.
  >vConsole 注入的灵感来源。

## Support Engine / 内核支持情况

| Engine Name | Version | Debugging | vConsole | Comment |
| :----: | :----: | :----: | :----: | :----: |
| WebView | X | ✅ | ✅ | 部分 App 由于重写、继承等原因，需要针对性适配 |
| TBS X5 | X | ✅ | ✅ | |
| UC U4 | X ~ `3.21.0.82` | ❓ | ❓ | 未找到合适的测试目标 |
| UC U4 | `3.21.0.174` ~ X | ✅ | ✅ | 一些阿里系的 App 使用了魔改包，可能无法开启 debugging |
| Crosswalk | X | ✅ | ❓ | 未找到合适的测试目标 |
| XWeb | X | ✅ | ✅ | 由 Crosswalk 二改而来的引擎，大概只有微信在用 |

## Support App / 应用支持情况

> - ✅: It is supported in the general case / 默认可用
> - ⭕: There are some problems / 存在一些问题
> - ❌: Not supported, special adaptation is required / 不支持，需要特殊适配
> - ✳️: It has been specially adapted / 已特殊适配

| App Name | Package Name | Version | Engine | Debugging | vConsole |
| :----: | :----: | :----: | :----: | :----: | :----: |
| QQ音乐 | com.tencent.qqmusic | 11.0.0.10(2510) | TBS X5 045412 | ✅ | ✅ |
| 京东 | com.jingdong.app.mall | 10.4.0(92610) | WebView | ✅ | ✅ |
| 京喜 | com.jd.pingou | 3.11.0(6373) | WebView | ✅ | ✅ |
| 企业微信 | com.tencent.wework | 4.0.0(18764) | WebView | ✅ | ✅ |
| 小黑盒 | com.max.xiaoheihe | 1.3.128(102) | WebView | ✅ | ✅ |
| 招商银行 | cmb.pb | 9.4.1(941) | WebView | ✅ | ✅ |
| 拼多多 | com.xunmeng.pinduoduo | 5.83.0(58301) | WebView | ⭕ | ❌ |
| 腾讯地图 | com.tencent.map | 9.18.1(1552) | WebView | ✅ | ✅ |
| 航旅纵横 | com.umetrip.android.msky.app | 7.2.4(412) | WebView | ✅ | ✅ |
| 起点读书 | com.qidian.QDReader | 7.9.186(674) | TBS X5 045913 | ✅ | ✅ |
| 哔哩哔哩 | tv.danmaku.bili | 6.56.0(6560300) | WebView | ✅ | ✳️ |
| 网易云音乐 | com.netease.cloudmusic | 7.0.0(167) | WebView | ✅ | ✳️ |
| 知乎 | com.zhihu.android | 7.14.0(5222) | WebView | ✅ | ✳️ |
| 浙江移动手机营业厅 | com.example.businesshall | 7.3.0(2021072301) | WebView | ✅ | ✅ |
| 京东金融 | com.jd.jrapp | 6.0.30(280) | WebView | ✅ | ✅ |
| QQ邮箱 | com.tencent.androidqqmail | 6.2.0(10149054) | WebView | ✅ | ✳️ |
| 飞书 | com.ss.android.lark | 5.5.3(50535) | WebView | ✅ | ✅ |
| 闲鱼 | com.taobao.idlefish | 7.3.50(259) | UC U4 3.22.1.196 | ❌ | ✅ |
| 微信 | com.tencent.mm | 8.0.19(2080) | XWeb 3185 | ✅ | ✳️ |
| ... | ... | ... | ... | ... | ... |
