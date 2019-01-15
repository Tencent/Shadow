package com.tencent.shadow.demo.webview;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tencent.shadow.demo.main.R;


public class WebActivity extends Activity {

    private String TAG = "WebActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        WebView webView = new WebView(this);
//        webView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(R.layout.layout_webview);
        WebView webView = findViewById(R.id.webview);
        initWebSettings(webView);
        webView.loadUrl("file:///android_asset/seal_helper.html");
        Log.d(TAG,"loadUrl url:");
        webView.setWebViewClient(new WebViewClient(){
            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                return super.shouldInterceptRequest(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG,"shouldOverrideUrlLoading url:"+url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Log.d(TAG,"shouldOverrideUrlLoading request");
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d(TAG,"onPageStarted");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG,"onPageFinished");
            }
        });

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Log.d(TAG,"onProgressChanged :"+newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });
    }

    protected void initWebSettings( WebView webView) {
        WebSettings var1 = webView.getSettings();
        if (var1 != null) {
            int var2 = Build.VERSION.SDK_INT;
            if (var2 >= 8) {
                var1.setPluginState(WebSettings.PluginState.ON);
            }

            var1.setSaveFormData(false);
            var1.setSavePassword(false);
            var1.setBuiltInZoomControls(true);
            var1.setSupportZoom(false);
            var1.setJavaScriptEnabled(true);
            var1.setSavePassword(false);
            var1.setAllowContentAccess(true);
            var1.setJavaScriptCanOpenWindowsAutomatically(true);
            var1.setDomStorageEnabled(true);
            var1.setAppCacheMaxSize(8388608L);
            var1.setAllowFileAccess(true);
            if (Build.VERSION.SDK_INT >= 21) {
                var1.setMixedContentMode(0);
            }
        }
    }
}
