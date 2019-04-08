package test;

import android.webkit.WebView;

public class TestWebView extends WebView {

    WebView getWebView() {
        System.out.println("getWebView");
        return this;
    }
}
