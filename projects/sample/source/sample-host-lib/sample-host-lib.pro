#让宿主在打包时能够keep住插件要使用到的类名和方法
-keep class com.tencent.shadow.sample.host.lib.HostUiLayerProvider{
    public *;
}
