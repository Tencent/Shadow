package com.tencent.shadow.sample.plugin;

import android.os.Bundle;

import androidx.annotation.NonNull;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class FlutterAppActivity extends FlutterActivity {



    public static CachedEngineIntentBuilder withCachedEngine(@NonNull String cachedEngineId) {
        return new CachedEngineIntentBuilder(FlutterAppActivity.class, cachedEngineId);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        StatusBarCompat.setStatusBarColor(getWindow(),
//                getResources().getColor(R.color.transparent), true,true);

        super.onCreate(savedInstanceState);

        initChannel("crm");
    }


    private void initChannel(String engineId) {
        FlutterEngine engine =  FlutterEngineCache.getInstance().get(engineId);
        if(null == engine)
            return;
        MethodChannel nativeChannel = new MethodChannel(engine.getDartExecutor(), engineId);
        nativeChannel.setMethodCallHandler(new MethodChannel.MethodCallHandler() {
            @Override
            public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
                switch (methodCall.method) {
                    case "close":
                        finish();
                        // 跳转原生页面
//                        Intent jumpToNativeIntent = new Intent(getActivity(), NativeActivity.class);
//                        jumpToNativeIntent.putExtra("name", (String) methodCall.argument("name"));
//                        //因为写的demo所以直接采用了魔法数字 方便文章中看的直观
//                        startActivityForResult(jumpToNativeIntent, 1001);
                        break;
                    default:
                        result.notImplemented();
                        break;
                }
            }
        });
    }
}
