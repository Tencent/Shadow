# system-class-stub

系统类的桩定义。

新版本的Android系统会引入一些新类型，将其用在Activity等类的新方法签名中。
Shadow所需的Delegate机制，需要clone这些方法，所以也会在Shadow生成的方法
中使用这些新类型。这会导致低版本的系统找不到这些新类型的定义，从而Crash。

因此我们在这个模块中定义这些类型，只需要定义他们的类名即可。由于ClassLoader
的双亲委派机制，在高版本的系统中会优先从BootClassLoader中加载正确的实现类，
只有在低版本系统中找不到正确的实现类才会使用这个模块定义的假类。

最终打包`activity-container`的模块也需要同时打包此模块。
将此模块和`activity-container`分开的目的是使得此模块可以以`runtimeOnly`
方式依赖，避免编译时和系统类冲突。
