package com.tencent.shadow.demo.webview;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class TestWebView extends WebView {
    public TestWebView(Context context) {
        super(context);
    }

    public TestWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TestWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TestWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public TestWebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
    }
}
