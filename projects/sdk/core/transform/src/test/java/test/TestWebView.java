package test;

import android.content.Context;
import android.webkit.WebView;

public class TestWebView extends WebView {

    public TestWebView(Context context) {
        super(context);
    }

    WebView getWebView() {
        System.out.println("getWebView");
        return this;
    }

    void testNewWebView(){
        WebView webView = new WebView(new Context());
    }
}
