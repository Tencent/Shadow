# 此文件配置**使用**这个lib时应该添加的Proguard规则.

#kotlin一般性配置 START
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}
#kotlin一般性配置 END

-keep class org.slf4j.**{*;}
-dontwarn org.slf4j.impl.**