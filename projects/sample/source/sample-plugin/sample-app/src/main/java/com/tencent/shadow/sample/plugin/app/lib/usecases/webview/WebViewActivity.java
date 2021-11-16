package com.tencent.shadow.sample.plugin.app.lib.usecases.webview;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebView;

import com.tencent.shadow.sample.plugin.app.lib.gallery.cases.entity.UseCase;

public class WebViewActivity extends Activity {
    public static class Case extends UseCase {
        @Override
        public String getName() {
            return "WebView测试";
        }

        @Override
        public String getSummary() {
            return "测试WebView是否能正常工作";
        }

        @Override
        public Class getPageClass() {
            return WebViewActivity.class;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebView webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/web/test.html?t=" + Math.random());

        setContentView(webView);
    }
}
