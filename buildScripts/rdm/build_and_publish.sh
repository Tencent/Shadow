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
checkError
fi

sh gradlew --daemon :shadow-sdk:shadow-loader:publishReleasePublicationToMavenRepository
checkError
sh gradlew --daemon :shadow-sdk:shadow-runtime:publishReleasePublicationToMavenRepository
checkError
sh gradlew --daemon :shadow-sdk:shadow-transform:publishReleasePublicationToMavenRepository
checkError

cd $WORKSPACE/bin

if   [   $PUBLISH_RELEASE   ];
then
touch PUBLISH_SUCCESS
fi



