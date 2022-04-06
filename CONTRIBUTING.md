# Contributing
我们非常欢迎您向Tencent Shadow提交Issue或Pull Request。

# Issue
在Tencent Shadow开源的初期，我们会密切关注所有Issue反馈。晚些时候再根据反馈的情况制定Issue模板。

反馈问题时，请Fork Shadow的代码库到自己的名下。新建分支，添加可以复现问题的最小改动，提交后push到Github上。然后在Issue单中附上你的代码库地址和分支名即可。

```sh
git clone https://github.com/Tencent/Shadow.git //大概之前你已经这样clone过Shadow的代码库了
cd shadow //切换到你clone的shadow目录
git remote add your_name https://github.com/<your_name>/Shadow.git //把你fork的版本库添加成一个远端
git fetch --all //更新所有远端的代码
git checkout -b new_branch_name origin/dev // 基于Shadow代码库的dev分支新建一个分支
//加上你复现问题的修改
git commit
git push -u your_name  //推送new_branch_name分支到你fork的版本库
```
然后你的分支地址应该类似：`https://github.com/<your_name>/Shadow/tree/new_branch_name`

其他人可以用这样的命令获取到你的分支，看到你的提交做了哪些改动，运行并Debug。
```sh
cd shadow //切换到shadow目录
git fetch https://github.com/<your_name>/Shadow.git new_branch_name
git checkout -b new_branch_name FETCH_HEAD
```

# Pull Request
由于PR会修改代码，因此即便是在开源初期，我们也会对PR谨慎处理。

请注意以下问题：

1. 不要提交无意义改动。
1. 除非是提交复现问题的测试用例，请确保`gradlew testSdk`构建成功（需要连接Android设备）
1. 测试机需要至少有API 28，API 19两种机器，以保证ART和Dalvik虚拟机都能正常工作。
1. 尽量原子化的提交，配有较为清晰的提交信息。

我们会根据大家的PR再调整PR的要求的。

# 开发指引

## Debug编译期代码(Gradle插件、Transform等)

Shadow的`coding`和`core.gradle-plugin`、`core.manifest-parser`、`core.transform`,`core.transform-kit`
等模块都是在插件工程的编译期执行的。如果需要Debug它们，需要额外的配置。

1. 添加`Remote JVM Debug` Configuration。在Android Studio的`Run`菜单中找到`Edit Configuration`。 点"＋"（Add New
   Configuration），选择`Remote JVM Debug`，配置参数一般采用默认值不用修改。
   `Name`可以任意修改成方便识别的名字，稍后在工具栏执行时选择。 复制`Command line arguments for remote JVM`。
   一般的默认值是:`-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005`。
   将这行复制到`gradle.properties`中的`org.gradle.jvmargs=-Xmx4096m`后面作为更多的参数，注意加上空格。 然后将其中的`suspend=n`
   改为`suspend=y`，表示让JVM启动Gradle时等待Debugger连接，再继续执行。

2. 终止正在运行的Gradle Daemon。在命令行执行`./gradlew --stop`，终止掉没有采用新参数的JVM进程。

3. Debug编译期代码。通过`./gradlew`或者Android Studio的Gradle sync，或运行`sample-host`等任务， 都会在一启动时因为前面的`suspend=y`
   卡住。这时再选择刚刚添加的`Remote JVM Debug` Configuration， 点击`Debug`执行按钮，即可连接上Gradle JVM。如果在`ShadowPlugin`
   或者某个Transform代码中设置了断点， 就会正常在断点处暂停。 注意选择`Remote JVM Debug` Configuration的位置同选择`sample-host`
   等模块在同一个菜单中。 并且Android Studio可以同时执行多个Configuration，先运行`sample-host`， 再Debug `Remote JVM Debug`
   Configuration是没有问题的。

4. 在其他不是Shadow源码工程中，也可以同样设置`gradle.properties`参数，在其中执行Gradle任务。
   然后切换到Shadow源码工程中执行Debug `Remote JVM Debug` Configuration， 也可以Debug Shadow在其他工程中的编译期代码执行情况。

5. 还原。回退对`gradle.properties`的修改，然后执行`./gradlew --stop`。以上所有改动的作用即可恢复。

## 虚拟机

启动虚拟机

```shell
~/Library/Android/sdk/emulator/emulator @Pixel_XL_API_28 -noaudio -no-boot-anim -wipe-data -no-snapstorage
```

其中`Pixel_XL_API_28`来自：

```shell
~/Library/Android/sdk/emulator/emulator -list-avds
```

`-noaudio`可以避免耳机切换到通话模式。
`-wipe-data -no-snapstorage`使得虚拟机完全恢复到新建状态。
`-no-boot-anim`稍微加快点启动。

## 清理工作区

由于复合构建的存在，Gradle clean任务不能总是很好的完成清理工作区的目的。

```shell
git clean -fdx -e .idea -e local.properties 
```
