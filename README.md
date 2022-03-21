# Shadow

![Android CI](https://github.com/Tencent/Shadow/workflows/Android%20CI/badge.svg?event=push)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)

## 介绍
Shadow是一个腾讯自主研发的Android插件框架，经过线上亿级用户量检验。
Shadow不仅开源分享了插件技术的关键代码，还完整的分享了上线部署所需要的所有设计。

与市面上其他插件框架相比，Shadow主要具有以下特点：

* **复用独立安装App的源码**：插件App的源码原本就是可以正常安装运行的。
* **零反射无Hack实现插件技术**：从理论上就已经确定无需对任何系统做兼容开发，更无任何隐藏API调用，和Google限制非公开SDK接口访问的策略完全不冲突。
* **全动态插件框架**：一次性实现完美的插件框架很难，但Shadow将这些实现全部动态化起来，使插件框架的代码成为了插件的一部分。插件的迭代不再受宿主打包了旧版本插件框架所限制。
* **宿主增量极小**：得益于全动态实现，真正合入宿主程序的代码量极小（15KB，160方法数左右）。
* **Kotlin实现**：core.loader，core.transform核心代码完全用Kotlin实现，代码简洁易维护。

### 支持特性
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

## 编译与开发环境

### 环境准备
建议直接用最新的稳定版本Android Studio打开工程。目前项目已适配`Android Studio Arctic Fox | 2020.3.1`，
低版本的Android Studio可能因为Gradle版本过高而无法正常打开项目。

然后在IDE中选择`sample-app`或`sample-host`模块直接运行，分别体验同一份代码在正常安装情况下和插件情况下的运行情况。

![选择sample-host直接运行](pics/run-sample-host-in-ide.png)

Shadow的所有代码都位于`projects`目录下的3个目录，分别是：

* `sdk`包含SDK的所有代码
* `test`包含SDK的自动化测试代码
* `sample`包含演示代码

其中`sample`应该是大家体验Shadow的最佳环境。
详见`sample`目录中的[README](projects/sample/README.md)介绍。

## 自己写的测试代码出错？
以我们多年的插件环境下业务开发经验，插件框架是不可能一步到位实现完美的。
因此，我们相信大部分业务在接入时都是需要一定的二次开发工作。
Shadow现有的代码满足的是我们自己的业务现在的需求。得益于全动态的设计，
插件框架和插件本身都是动态发布的，插件包里既有插件代码也有插件框架代码，
所以可以根据新版本插件的需要同时开发插件框架。

例如，ShadowActivity没有实现全所有Activity方法，你写的测试代码可能用到了，
就会出现Method Not Found错误，只需要在ShadowActivity中实现对应方法就可以了。
大部分方法的实现都只是需要简单的转调就能工作正常。

如果遇到不会实现的功能，可以提Issue。最好附上测试代码。

## 后续开发
* 原理与设计说明文档
* 多插件支持的演示工程
* 自动化测试用例补充
* 开源包含下载能力的manager实现

## 贡献代码

详见[CONTRIBUTING.md](CONTRIBUTING.md)

## 许可协议

Tencent Shadow采用`BSD 3-Clause License`，详见[LICENSE](LICENSE.txt)。

## 个人信息保护规则声明

详见[PRIVACY.md](PRIVACY.md)
