演示如何将自定义接口动态化，使得宿主能够使用apk中的实现

sample-hello-api:定义宿主api接口
sample-hello-api-holder:将 api 动态化，宿主通过这个包提供的方法来获取apk中的实现

宿主引入 apk 包，implementation project(':sample-hello-api-holder')
