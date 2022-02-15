package com.tencent.shadow.sample.api.hello;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2021/9/6
 * @description 实现宿主自定义接口
 * @usage null
 */
public class SampleHelloWorld implements IHelloWorldImpl {
    @Override
    public void sayHelloWorld(Context context, TextView textView) {
        String text = "这是apk中的实现：" + SampleHelloWorld.class.toString();
        if (textView == null) {
            return;
        }
        textView.setText(text);
    }

    @Override
    public void onCreate(Bundle bundle) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onDestroy() {

    }
}
