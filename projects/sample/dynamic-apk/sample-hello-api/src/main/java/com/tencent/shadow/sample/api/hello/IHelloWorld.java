package com.tencent.shadow.sample.api.hello;

import android.content.Context;
import android.widget.TextView;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2021/9/6
 * @description 定义在宿主里的接口，使用插件apk中的实现
 * @usage 插件打印 hello world
 */
public interface IHelloWorld {
    void sayHelloWorld(Context context, TextView textView);
}
