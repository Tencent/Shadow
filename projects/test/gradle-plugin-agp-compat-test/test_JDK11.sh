#!/usr/bin/env bash

# Shadow工程因需要支持更高版本AGP，本身也需要用更高版本AGP构建。从AGP 8开始要求用JDK 17了。
# 但使用Shadow的工程应该可以使用JDK 11。此脚本需要在JDK 11下测试低版本AGP工程的兼容性。

JAVA_MAJOR_VERSION=$(javap -verbose java.lang.Object | grep "major version" | cut -d " " -f5)
if [[ $JAVA_MAJOR_VERSION -ne 55 ]]; then
  echo "需要JDK 11!"
  exit 1
fi

source ./test_prepare.sh

# 测试版本来源
# AGP release页面：https://developer.android.com/studio/releases/gradle-plugin
# AGP Maven仓库：https://mvnrepository.com/artifact/com.android.tools.build/gradle
# Gradle下载：https://services.gradle.org/distributions/
setGradleVersion 7.5
testUnderAGPVersion 7.4.1
setGradleVersion 7.4
testUnderAGPVersion 7.3.1
setGradleVersion 7.3.3
testUnderAGPVersion 7.2.2
setGradleVersion 7.2
testUnderAGPVersion 7.1.1
setGradleVersion 7.0.2
testUnderAGPVersion 7.0.0
testUnderAGPVersion 4.2.0
testUnderAGPVersion 4.1.0
setGradleVersion 6.1.1
testUnderAGPVersion 4.0.0
setGradleVersion 5.6.4
testUnderAGPVersion 3.6.0
setGradleVersion 5.4.1
testUnderAGPVersion 3.5.0
testUnderAGPVersion 3.4.0
testUnderAGPVersion 3.3.0
testUnderAGPVersion 3.2.0

# 3.1.0在配合aapt2使用--package-id选项时不能设置小于0x7f的值，因此不再支持
#testUnderAGPVersion 3.1.0

#更低的版本支持成本过高
