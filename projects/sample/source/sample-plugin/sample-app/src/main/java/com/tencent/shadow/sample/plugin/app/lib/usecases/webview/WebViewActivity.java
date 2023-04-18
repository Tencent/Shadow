package com.tencent.shadow.sample.plugin.app.lib.usecases.webview;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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

        WebView webView = new FooWebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/web/test.html?t=" + Math.random());

        setContentView(webView);
    }
}

/**
 * 复现
 * https://github.com/Tencent/Shadow/issues/1175
 */
class FooWebView extends WebView {

    public FooWebView(@NonNull Context context) {
        super(context);
    }

    @Override
    public void setWebViewClient(@NonNull WebViewClient client) {
        FooWebViewClient fooWebViewClient = (FooWebViewClient) client;
        super.setWebViewClient(fooWebViewClient);
    }
}

class FooWebViewClient extends WebViewClient {

}