#!/bin/sh

if [ "$1" == "b" ]; then
  ./gradlew build
elif [ "$1" == "i" ]; then
  adb uninstall com.tencent.shadow.sample.host
  adb install projects/sample/source/sample-host/build/outputs/apk/debug/sample-host-debug.apk
else
  ./gradlew build
  adb uninstall com.tencent.shadow.sample.host
  adb install projects/sample/source/sample-host/build/outputs/apk/debug/sample-host-debug.apk
fi
