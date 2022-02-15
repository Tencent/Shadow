/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tencent.shadow.core.runtime;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
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

public class ShadowWebView extends WebView {

    private Context mContext;

    private final String ANDROID_ASSET_PREFIX = "file:///android_asset/";

    private final String REPLACE_ASSET_PREFIX = "http://android.asset/";

    public ShadowWebView(Context context) {
        super(context);
        init(context);
    }

    public ShadowWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ShadowWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(21)
    public ShadowWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public ShadowWebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        setWebViewClient(new WebViewClient());
    }

    @Override
    public void loadUrl(String url) {
        if (url.startsWith(ANDROID_ASSET_PREFIX)) {
            url = url.replace(ANDROID_ASSET_PREFIX, REPLACE_ASSET_PREFIX);
        }
        super.loadUrl(url);
    }

    @Override
    public void setWebViewClient(WebViewClient client) {
        super.setWebViewClient(new WarpWebViewClient(client, mContext));
    }

    class WarpWebViewClient extends WebViewClient {

        private WebViewClient mWebViewClient;
        private Context mContext;

        public WarpWebViewClient(WebViewClient webViewClient, Context context) {
            mWebViewClient = webViewClient;
            mContext = context;
        }

        private WebResourceResponse getInterceptResponse(String url) {
            if (url.startsWith(REPLACE_ASSET_PREFIX)) {
                int end = url.indexOf("?");
                if (end == -1) {
                    end = url.length();
                }
                String filePath = url.substring(REPLACE_ASSET_PREFIX.length(), end);
                String mime = "text/html";
                if (filePath.contains(".css")) {
                    mime = "text/css";
                } else if (filePath.contains(".js")) {
                    mime = "application/x-javascript";
                } else if (filePath.contains(".jpg") || filePath.contains(".gif") ||
                        filePath.contains(".png") || filePath.contains(".jpeg")) {
                    mime = "image/*";
                }
                try {
                    return new WebResourceResponse(mime, "utf-8", mContext.getAssets().open(filePath));
                } catch (IOException ignored) {
                }
            }
            return null;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return mWebViewClient.shouldOverrideUrlLoading(view, url);
        }

        @TargetApi(Build.VERSION_CODES.N)
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

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onPageCommitVisible(WebView view, String url) {
            mWebViewClient.onPageCommitVisible(view, url);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            WebResourceResponse resourceResponse = getInterceptResponse(url);
            if (resourceResponse != null) {
                return resourceResponse;
            }
            return mWebViewClient.shouldInterceptRequest(view, url);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            WebResourceResponse resourceResponse = getInterceptResponse(url);
            if (resourceResponse != null) {
                return resourceResponse;
            }
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


        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            mWebViewClient.onReceivedError(view, request, error);
        }


        @TargetApi(Build.VERSION_CODES.M)
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


        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
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


        @TargetApi(Build.VERSION_CODES.O)
        @Override
        public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
            return mWebViewClient.onRenderProcessGone(view, detail);
        }

        @TargetApi(Build.VERSION_CODES.O_MR1)
        @Override
        public void onSafeBrowsingHit(WebView view, WebResourceRequest request, int threatType, SafeBrowsingResponse callback) {
            mWebViewClient.onSafeBrowsingHit(view, request, threatType, callback);
        }
    }
}
