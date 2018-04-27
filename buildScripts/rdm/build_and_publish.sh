export ANDROID_HOME=$ANDROID_SDK
export JAVA_HOME=$JDK8
export PATH=$JDK8/bin:$GRADLE_HOME/bin:$PATH

export SHORT_REV=$(git rev-parse --short $GIT_COMMIT)

function checkError()
{
#如果前一条命令出错了则终止构建
if [ $? != 0 ];
then
exit 1
fi
}

rm bin/*.*

if   [   $clean   ];
then
sh gradlew clean
checkError()
fi

if   [   $PUBLISH_RELEASE   ];
then
sh gradlew --daemon assembleRelease
else
sh gradlew --daemon assembleDebug
fi
checkError()

sh gradlew --daemon :lib:publish

checkError()

cd $WORKSPACE/bin

if   [   $PUBLISH_RELEASE   ];
then
cp $WORKSPACE/example/build/outputs/apk/release/example-release-unsigned.apk ./example-release-unsigned-$MajorVersion.$MinorVersion.$FixVersion.$BuildNo-$SHORT_REV.apk
checkError()
cp $WORKSPACE/lib/build/outputs/aar/release/lib-release.aar ./lib-release-$MajorVersion.$MinorVersion.$FixVersion.$BuildNo-$SHORT_REV.apk
checkError()
cp $WORKSPACE/lib/build/outputs/mapping/release/mapping.txt ./lib-release-mapping-$MajorVersion.$MinorVersion.$FixVersion.$BuildNo-$SHORT_REV.txt
checkError()
else
cp $WORKSPACE/example/build/outputs/apk/debug/example-debug.apk ./example-debug-$MajorVersion.$MinorVersion.$FixVersion.$BuildNo-$SHORT_REV.apk
checkError()
cp $WORKSPACE/lib/build/outputs/aar/debug/lib-debug.aar ./lib-debug-$MajorVersion.$MinorVersion.$FixVersion.$BuildNo-$SHORT_REV.apk
checkError()
fi



