# FSP SDK Samples - MeetingDemo

这个开源示例项目演示了如何快速集成 FSP SDK，实现音视频会议功能。

在这个示例项目中包含了以下功能：

- 加入FSP Group；
- 多人音频通话；
- 多人视频通话；
- 音视频通话相关设置和ui展示；


FSP SDK支持 iOS / Android / Windows / macOS 等多个平台，你可以查看对应各平台的示例项目：

- [Fsp MeetingDemo Windows](https://github.com/paas-hst/meetingdemo_windows)
- [Fsp MeetingDemo iOS](https://github.com/paas-hst/meetingdemo_ios)
- [Fsp MeetingDemo macOS](https://github.com/paas-hst/meetingdemo_mac)

## <span style="color:red;">注意事项</span>
<span style="color:red;">为了简化demo开发，生成FSP token的代码放在了该客户端程序里，实际项目中，
应该将token生成放在你的服务器程序， 关于token鉴权的具体细节，[参考FSP开发者文档](http://paas.hst.com/tokenTutorial)</span>

## 运行示例程序
首先在 [FSP官网 注册](http://customer.paas.hst.com/register) 注册账号，并创建自己的应用，获取到 AppID。
将FspManager.java文件中对应的常量赋值成创建的应用信息：

```
private static final String APP_ID = "";
private static final String APP_SECRETKEY = "";
```

然后在 [FSP官网 下载](http://paas.hst.com/downloadSDK) 下载SDK 下载 **sdk**，解压后将其中的 aar文件 复制到本项目libs目录下。

最后使用 Android Studio 打开工程，运行app。

## 运行环境
- Android Studio 3.0 +
- FSP SDK暂不支持模拟器，请使用真机调试运行

## 联系我们

- 完整的 API 文档见 [开发者中心](http://paas.hst.com/platformDesc)
- 如果发现demo的bug, 或想优化demo代码， 欢迎提交
- 如果有任何咨询问题, 可以拨打 400-1199-666， 0755 83885517, 或加入QQ群 783541706 提问