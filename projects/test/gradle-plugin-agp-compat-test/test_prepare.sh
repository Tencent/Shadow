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

  echo ./gradlew -PSetGradleVersion=false -PTestAGPVersion=$TestAGPVersion -PShadowVersion=$ShadowVersion :stub-project:packageA1B2DebugPlugin
  ./gradlew -PSetGradleVersion=false -PTestAGPVersion=$TestAGPVersion -PShadowVersion=$ShadowVersion :stub-project:packageA1B2DebugPlugin
}
