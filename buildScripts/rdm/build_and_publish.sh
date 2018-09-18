export ANDROID_HOME=$ANDROID_SDK
export JAVA_HOME=$JDK8
export PATH=$JDK8/bin:$GRADLE_HOME/bin:$PATH

function checkError()
{
#如果前一条命令出错了则终止构建
if [ $? != 0 ];
then
exit 1
fi
}

rm bin/*.*

sh gradlew --daemon cleanThenPublishSdkToMaven
checkError

cd $WORKSPACE/bin

touch BUILD_DONE



