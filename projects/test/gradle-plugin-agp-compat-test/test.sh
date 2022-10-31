#!/usr/bin/env bash

set -eo pipefail

pushd ../../../
./gradlew publishToMavenLocal
line=$(./gradlew getPublicationVersion | grep "publicationVersion:")
ShadowVersion=${line:19}
popd

function setGradleVersion() {
  local distributionBase=https\://mirrors.tencent.com/gradle/
  if [ "$DISABLE_TENCENT_MAVEN_MIRROR" = true ] ; then
    distributionBase=https\://services.gradle.org/distributions/
  fi

  local GradleVersion=$1
  echo ./gradlew -PSetGradleVersion=true wrapper --gradle-distribution-url ${distributionBase}gradle-$GradleVersion-bin.zip
  ./gradlew -PSetGradleVersion=true wrapper --gradle-distribution-url ${distributionBase}gradle-$GradleVersion-bin.zip
}

function testUnderAGPVersion() {
  local TestAGPVersion=$1
  rm -rf stub-project/build

  echo ./gradlew -PSetGradleVersion=false -PTestAGPVersion=$TestAGPVersion -PShadowVersion=$ShadowVersion :stub-project:assemblePluginA1B2Debug
  ./gradlew -PSetGradleVersion=false -PTestAGPVersion=$TestAGPVersion -PShadowVersion=$ShadowVersion :stub-project:assemblePluginA1B2Debug
}

# 测试版本来源
# https://developer.android.com/studio/releases/gradle-plugin
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
