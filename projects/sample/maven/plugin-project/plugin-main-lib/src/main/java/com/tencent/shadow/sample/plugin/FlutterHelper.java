package com.tencent.shadow.sample.plugin;

import android.app.Activity;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.embedding.engine.dart.DartExecutor;

class FlutterHelper {
    public static void init(Activity context,String router,String engineId) {
        if(FlutterEngineCache.getInstance().contains(engineId))
            return;

        FlutterEngine flutterEngine = new FlutterEngine(context);
        flutterEngine.getNavigationChannel().setInitialRoute(router);
        flutterEngine.getDartExecutor().executeDartEntrypoint(
                DartExecutor.DartEntrypoint.createDefault()
        );
        FlutterEngineCache.getInstance().put(engineId, flutterEngine);
    }

    public static void start(final Activity context,final String router,final String engineId) {


        FlutterEngine engine = FlutterEngineCache.getInstance().get(engineId);
        if(null != engine && null != engine.getDartExecutor()){
            context.startActivity(
                    FlutterAppActivity
                            .withCachedEngine(engineId)
                            .build(context));
            return;
        }
        init(context,router,engineId);

        context.startActivity(
                FlutterAppActivity
                        .withCachedEngine(engineId)
                        .build(context));
    }

}
