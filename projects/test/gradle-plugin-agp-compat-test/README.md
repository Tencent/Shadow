## core.gradle-plugin模块的AGP各版本黑盒测试

准备一个桩工程`stub-project`，通过命令行参数控制其AGP版本和Shadow版本。

自动化测试脚本: `test.sh`。其中先编译Shadow，发布到本地Maven，然后用这个Shadow版本进行测试。

注意脚本会echo出执行的命令，如果遇到测试失败，可复制命令手工重新执行。

### 确定实际使用的AGP版本：

查看`stub-project/build/intermediates/app_metadata/pluginDebug/app-metadata.properties`
