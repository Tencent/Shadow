package com.tencent.cubershi.mock_interface;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.webkit.ClientCertRequest;
import android.webkit.HttpAuthHandler;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.SafeBrowsingResponse;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;

/**
 * Created by owenguo on 2018/7/8.
 */

public class MockWebView extends WebView {

    private Context mContext;

    public MockWebView(Context context) {
        super(context);
        mContext = context;
    }

    public MockWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public MockWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public MockWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
    }

    public MockWebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
        mContext = context;
    }

    @Override
    public void loadUrl(String url) {
        if(url.startsWith("file:///android_asset")){
            url = url.replace("file:///android_asset","http://android.asset");
        }
        super.loadUrl(url);
    }

    @Override
    public void setWebViewClient(WebViewClient client) {
        super.setWebViewClient(new WarpWebViewClient(client,mContext));
    }

    class WarpWebViewClient extends WebViewClient{

        private WebViewClient mWebViewClient;
        private Context mContext;

        public WarpWebViewClient(WebViewClient webViewClient,Context context){
            mWebViewClient = webViewClient;
            mContext = context;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return mWebViewClient.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return mWebViewClient.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            mWebViewClient.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            mWebViewClient.onPageFinished(view, url);
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            mWebViewClient.onLoadResource(view, url);
        }

        @Override
        public void onPageCommitVisible(WebView view, String url) {
            mWebViewClient.onPageCommitVisible(view, url);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            if(url.startsWith("http://android.asset/")){
                String filePath = url.substring("http://android.asset/".length());
                String mime = "text/html";
                if (filePath.contains(".css")){
                    mime = "text/css";
                }else if(filePath.contains(".js")){
                    mime = "application/x-javascript";
                }else if(filePath.contains(".jpg") || filePath.contains(".gif") ||
                        filePath.contains(".png") || filePath.contains(".jpeg")){
                    mime = "image/*";
                }
                try {
                    return new WebResourceResponse(mime,"utf-8",mContext.getAssets().open(filePath));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return mWebViewClient.shouldInterceptRequest(view, url);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            return mWebViewClient.shouldInterceptRequest(view, request);
        }

        @Override
        public void onTooManyRedirects(WebView view, Message cancelMsg, Message continueMsg) {
            mWebViewClient.onTooManyRedirects(view, cancelMsg, continueMsg);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            mWebViewClient.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            mWebViewClient.onReceivedError(view, request, error);
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            mWebViewClient.onReceivedHttpError(view, request, errorResponse);
        }

        @Override
        public void onFormResubmission(WebView view, Message dontResend, Message resend) {
            mWebViewClient.onFormResubmission(view, dontResend, resend);
        }

        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            mWebViewClient.doUpdateVisitedHistory(view, url, isReload);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            mWebViewClient.onReceivedSslError(view, handler, error);
        }

        @Override
        public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
            mWebViewClient.onReceivedClientCertRequest(view, request);
        }

        @Override
        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
            mWebViewClient.onReceivedHttpAuthRequest(view, handler, host, realm);
        }

        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            return mWebViewClient.shouldOverrideKeyEvent(view, event);
        }

        @Override
        public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
            mWebViewClient.onUnhandledKeyEvent(view, event);
        }

        @Override
        public void onScaleChanged(WebView view, float oldScale, float newScale) {
            mWebViewClient.onScaleChanged(view, oldScale, newScale);
        }

        @Override
        public void onReceivedLoginRequest(WebView view, String realm, String account, String args) {
            mWebViewClient.onReceivedLoginRequest(view, realm, account, args);
        }

        @Override
        public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
            return mWebViewClient.onRenderProcessGone(view, detail);
        }

        @Override
        public void onSafeBrowsingHit(WebView view, WebResourceRequest request, int threatType, SafeBrowsingResponse callback) {
            mWebViewClient.onSafeBrowsingHit(view, request, threatType, callback);
        }
    }
}
