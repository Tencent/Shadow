# 此文件配置**构建**这个lib时应该使用的Proguard规则.

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

-keep class com.tencent.shadow.dynamic.loader.**{*;}
-keep class com.tencent.shadow.dynamic.impl.**{*;}