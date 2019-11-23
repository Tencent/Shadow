# 此文件配置**使用**这个lib时应该添加的Proguard规则.

#kotlin一般性配置 START
#-keep class kotlin.** { *; }
#-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}
#kotlin一般性配置 END

#kotlin优化性能 START
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}
#kotlin优化性能 END

-keep class org.slf4j.**{*;}
-keep class com.tencent.shadow.loader.**{*;}
-keep class com.tencent.shadow.runtime.**{*;}
-dontwarn org.slf4j.impl.**