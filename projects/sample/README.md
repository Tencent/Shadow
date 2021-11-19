# Sample

在Shadow框架下，应用由几部分构成。
宿主应用打包了很简单的一些接口，并在Manifest中注册了壳子代理组件，
还打包了插件管理器（manager）的动态升级逻辑。
manager负责下载、安装插件，还带有一个动态的View表达Loading态。
而"插件"则不光包含业务App，还包含Shadow的核心实现，即loader和runtime。
"插件"中的业务App和loader、runtime是同一个版本的代码编译出的，
因此loader可以包含一些业务逻辑，针对业务进行特殊处理。
由于loader是多实例的，因此同一个宿主中可以有多种不同的loader实现。
manager在加载"插件"时，首先需要先加载"插件"中的runtime和loader，
再通过loader的Binder（插件应该处于独立进程中避免native库冲突）操作loader进而加载业务App。

在这个Sample目录下，提供了两种示例工程：

## 源码依赖SDK的Sample(`projects/sample/source`)

***
要测试这个Sample请用Android Studio直接打开clone版本库的根目录。
***

* `sample-host`是宿主应用
* `sample-manager`是插件管理器的动态实现
* `sample-plugin/sample-loader`是loader的动态实现，业务主要在这里定义插件组件和壳子代理组件的配对关系等。
* `sample-constant`是在前3者中共用的相同字符串常量。
* `sample-plugin/sample-runtime`是runtime的动态实现，业务主要在这里定义壳子代理组件的实际类。
* `sample-plugin/sample-base-lib`是业务App的一部分基础代码，是一个aar库。
* `sample-plugin/sample-base`是一个apk模块壳子，将`sample-base-lib`打包在其中。既用于正常安装运行开发`sample-base-lib`
  ，又用于编译`sample-base`插件。
* `sample-plugin/sample-app`是依赖`sample-base-lib`开发的更多业务代码。它编译出的插件apk没有打包`sample-base-lib`
  ，会在插件运行时依赖`sample-base`插件。

`sample-app`和`sample-base`构成了一个多插件示例，请注意`sample-app/build.gradle`中的`dependsOn = ['sample-base']`设置。

这些工程中对Shadow SDK的依赖完全是源码级的依赖，因此修改Shadow SDK的源码后可以直接运行生效。

使用时可以直接在Android Studio中选择运行`sample-host`模块。
`sample-host`在构建中会自动打包manager和"插件"到assets中，在运行时自动释放模拟下载过程。

## 二进制Maven依赖SDK的Sample(`projects/sample/maven`)

***
要测试这个Sample请用Android Studio *分别* 打开`projects/sample/maven/host-project`
,`projects/sample/maven/manager-project`,`projects/sample/maven/plugin-project`三个目录。
***

源码依赖SDK的Sample中对Shadow SDK的依赖配置不适用于正式业务接入。
Shadow实现了完整的Maven发布脚本，支持方便的Maven依赖。

`maven`目录下的3个目录分别演示了3个工程。
这3个工程在实际业务中大概率上是3个不同的代码库。
因此，在这个演示中没有试图做着3个工程间的任何依赖关系，
甚至**3个工程中依赖的Shadow版本都是独立配置的**，
使用时请注意这一点。

### 自行发布SDK到Maven仓库方法

在`buildScripts/gradle/maven.gradle`文件中配置了Shadow的Maven发布脚本。
正式使用时，请修改其中的两个GroupID变量：`coreGroupId`、`dynamicGroupId`，
以及`setScm`方法中的两个URL到自己的版本库地址上。

然后将`mavenLocal()`改为自己发布的目标Maven仓库。

执行`./gradlew publish`即可将Shadow SDK发布到Maven仓库。

构件的版本号可以在`build/pom`目录中查看生成的pom文件中查看。

在这个Sample的3个工程的`build.gradle`文件中都有`shadow_version`定义，
将这个定义值改为刚刚发布的版本号（生成的pom中写的版本号）。

### 运行方法

这个演示工程没有实现下载功能，而是假设下载的文件直接位于指定路径。
因此运行前需要手工用adb命令将指定内容push到指定位置。

编译插件，在`plugin-project`目录中运行：
```
./gradlew packageDebugPlugin

adb push build/plugin-debug.zip /data/local/tmp
```

编译PluginManager，在`manager-project`目录中运行：
```
./gradlew assembleDebug
adb push sample-manager/build/outputs/apk/debug/sample-manager-debug.apk /data/local/tmp
```

最后可以用Android Studio打开`host-project`直接运行`sample-host`模块。

`plugin-project`中的`plugin-normal-apk`模块也可以直接安装运行，演示不使用Shadow时插件的运行情况。
