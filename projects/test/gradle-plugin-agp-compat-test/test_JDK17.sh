#!/usr/bin/env bash

# 从Gradle 7.5和AGP 8.0开始支持和要求使用JDK 17，所以这个脚本中的待测版本需要在JDK 17环境下测试。

JAVA_MAJOR_VERSION=$(javap -verbose java.lang.Object | grep "major version" | cut -d " " -f5)
if [[ $JAVA_MAJOR_VERSION -ne 61 ]]; then
  echo "需要JDK 17!"
  exit 1
fi

source ./test_prepare.sh

# 测试版本来源
# AGP release页面：https://developer.android.com/studio/releases/gradle-plugin
# AGP Maven仓库：https://mvnrepository.com/artifact/com.android.tools.build/gradle
# Gradle下载：https://services.gradle.org/distributions/
setGradleVersion 8.6
testUnderAGPVersion 8.4.0-rc02
setGradleVersion 8.4
testUnderAGPVersion 8.3.2
setGradleVersion 8.2.1
testUnderAGPVersion 8.2.0
setGradleVersion 8.0.2
testUnderAGPVersion 8.1.4
testUnderAGPVersion 8.0.2
setGradleVersion 7.5.1
testUnderAGPVersion 7.4.1
