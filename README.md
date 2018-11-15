# Shadow PluginLoader

零反射实现的Android免安装运行App，插件框架。

## Shadow PluginLoader是干什么的？

Shadow和这些SDK的目的基本是一致的：
QQ PluginSDK
RePlugin
Atlas
SixGod

一般我们管这类技术叫做"插件框架"，但我们认为这类技术通常有两类目的：
1.将一个App免安装运行起来。目的通常是为新App导入流量。
2.将一个App自身拆分成多个插件，使每个插件可以动态更新。

对于这两个目的，Shadow都是支持的。

## PluginLoader是什么？

当前这个Project属于一个整体的全动态插件框架，这个Project承担其中的PluginLoader的职责，也就是插件框架的核心代码。除此之外，还有Plugin Manager等部分负责插件的下载安装等工作。

## 零反射？

是的，准确地说是完全不依赖于非公开SDK接口，实际上也确实没有用反射调用任何方法或修改任何私有域。Shadow的设计目标是符合Android P对于非公开SDK接口限制访问的策略。基本原理是通过Android Transform API修改插件的字节码的方式为插件App与系统之间添加一层中间件的目的。而其他插件框架都是通过Hack技术修改系统实现达到这一相同目的的。这是Shadow与其他插件框架最大的区别。

得益于完全依赖公开SDK的实现，Shadow运行非常稳定，不怕OEM厂商修改系统实现。因为无论OEM厂商怎么修改系统，也不能修改公开API的行为，否则正常安装的App也会出现异常。

## 项目结构
这是一个标准的Android工程。可以用Android Studio打开`settings.gradle`所在目录。出于习惯，我们没有上库IDE相关的任何文件。

