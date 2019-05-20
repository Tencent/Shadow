# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class org.slf4j.**{*;}
-dontwarn org.slf4j.impl.**

-keep class com.tencent.shadow.dynamic.host.**{*;}
-keep class com.tencent.shadow.core.common.**{*;}
-keep class com.tencent.shadow.core.runtime.container.**{*;}

#--start 下面是为了keep 插件访问宿主的白名单类--
-keep class com.tencent.shadow.test.lib.plugin_use_host_code_lib.interfaces.**{*;}
-keep class com.tencent.shadow.test.lib.plugin_use_host_code_lib.other.**{*;}
#--end--