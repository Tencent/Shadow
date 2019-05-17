# Shadow--零反射实现的插件框架
"零反射"显然是一个Shadow最值得强调的特点，没错，Shadow在实现将App免安装运行这件事上没有使用任何反射调用。可以说Shadow是一个完全按照Android系统说明书实现的插件框架，在这个实现中不包含任何Android官方文档上找不到的东西。因此Shadow可以稳定运行在任何版本的Android系统上，包括严格限制非公开SDK接口访问的Android9.0。再换一种说法，Shadow不会触发任何Android 9.0的限制API（包括黑名单、深灰名单、浅灰名单）。

## Shadow的特点
* **复用独立安装App的源码**：插件App的源码原本就是可以正常安装运行的。
* **完全无Hack实现**：从理论上就已经确定无需对任何系统做兼容开发，更无任何隐藏API调用，和Google限制非公开SDK接口访问的策略完全不冲突。
* **全动态实现**：一次性实现完美的插件框架很难，但Shadow将这些实现全部动态化起来，使插件框架的代码成为了插件的一部分。插件的迭代不再受宿主打包了旧版本插件框架所限制。
* **宿主增量极小**：得益于全动态实现，真正合入宿主程序的代码量极小（15KB，160方法数左右）。
* **Kotlin实现**：Core.Loader，Core.Transform核心代码完全用Kotlin实现，代码简洁易维护。

## Shadow的逻辑结构
市面上有很多插件框架，但是除了核心的免安装运行系统组件的目的之外，每个插件框架的功能都有自己的范围。所以先介绍一下Shadow的逻辑结构，以便大家了解Shadow的大致功能范围。
```
Shadow（Shadow框架分为Core和Dynamic两部分）
    Core（Shadow的核心功能）
        Loader（免安装运行实现）
        Runtime（运行时类库）
        Gradle-Plugin（插件App构建插件）
        Manager（插件安装管理器）
    Dynamic（将Core层的所有功能动态化起来）
```

对照这个逻辑结构，可以将Shadow的功能和实现大致描述出来：Shadow通过在插件App的构建过程中添加一个Gradle插件，运用AOP编程思想，以修改App字节码为手段，将一个Shadow Runtime的中间层加入到插件App中。使插件App不再与系统直接交互，进而达到无需Hack系统的目的。同时，Shadow还有另一大特点，即全动态实现，Dynamic层将插件加载和管理以及运行时类库的一切代码都动态化起来，使Shadow可以在应用发布后继续迭代扩充功能、修复Bug。

## 支持特性
* 四大组件
* Fragment（代码添加和Xml添加）
* DataBinding（无需特别支持，但已验证可正常工作）
* 跨进程使用插件Service
* 自定义Theme
* 插件访问宿主类
* So加载
* 分段加载插件（多Apk分别加载或多Apk以此依赖加载）
* 一个Activity中加载多个Apk中的View
* 等等……

## 采用Shadow的项目
* Now
* QQ群视频
* 花样直播

# 代码介绍

## 必须用Android Studio 3.4或更高版本打开工程
由于我们需要在SDK开发工程中有一个调试SDK用的Demo程序，这个Demo程序需要直接源码依赖SDK，才能在修改SDK后直接生效。而SDK中包含了在编译期需要使用到的Runtime和Gradle-Plugin模块，而这两个部分又是编译期的结果。所以为了达到这个目的，我们使用了Gradle的复合构建。但是又由于我们在配置期和编译期同时使用了Runtime这个模块，所以触发了Gradle早期版本的Bug。因此我们必须使用Gradle 5.0或更高版本，而Android Studio在3.4版本才支持Gradle 5.0版本。是的，我们是使用Android Studio 3.4 Preview版本开发的Shadow，好在这个Preview版本非常稳定。

这是一个标准的Android工程。可以用Android Studio直接打开项目根目录。出于习惯，我们没有上库IDE相关的任何文件。

## 首次打开工程时遇到SDK location not found.
https://git.oa.com/shadow/shadow/issues/14

## 编译失败遇到`Failed to find byte code for com/tencent/shadow/runtime/....`
请关闭Android Studio的Instant Run功能。

## 代码结构
项目的代码都在`projects`子目录中，根目录中的其余文件都是Gradle相关的构建脚本。

Shadow SDK的代码都位于`projects/sdk`中。测试代码位于`projects/test`中。整个项目是一个复合构建。测试代码以源码依赖方式依赖了SDK。

## 体验说明
本项目的测试代码是源码级依赖SDK的，因此可以非常便捷的调试SDK源码。但是测试工程的Gradle配置不适合参考。

接入Shadow建议参考Sample工程：
https://git.oa.com/shadow/shadow-basic-sample