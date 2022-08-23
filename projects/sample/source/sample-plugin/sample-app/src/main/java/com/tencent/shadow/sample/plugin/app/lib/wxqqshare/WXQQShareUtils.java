package com.tencent.shadow.sample.plugin.app.lib.wxqqshare;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

import com.tencent.connect.share.QQShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXVideoObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.shadow.sample.plugin.app.lib.Consts;
import com.tencent.shadow.sample.plugin.app.lib.ImageCompressUtil;
import com.tencent.shadow.sample.plugin.app.lib.R;
import com.tencent.shadow.sample.plugin.app.lib.UseCaseApplication;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.io.ByteArrayOutputStream;



/*分享到对话:
       SendMessageToWX.Req.WXSceneSession
        分享到朋友圈:
        SendMessageToWX.Req.WXSceneTimeline ;
        分享到收藏:
        SendMessageToWX.Req.WXSceneFavorite*/
public class WXQQShareUtils implements IUiListener {
    private final String TAG = "WXQQShareUtils";
    private static IWXAPI mWxApi;
    private static Tencent mTencent;
    private static Context mcontext;


    public static void initWXQQ(Context context) {
        mTencent = Tencent.createInstance(Consts.QQ_LOGIN_APP_ID, context);
        //通过WXAPIFactory工厂获取IWXApI的示例
        mWxApi = WXAPIFactory.createWXAPI(context, Consts.APP_ID_WX, true);
        //将应用的appid注册到微信
        mWxApi.registerApp(Consts.APP_ID_WX);
        mcontext = context;
    }

    public static void WXShareText(String text, int scene) {
        if(mTencent == null){
            initWXQQ(UseCaseApplication.intence);
        }
        //初始化一个 WXTextObject 对象，填写分享的文本内容
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;

//用 WXTextObject 对象初始化一个 WXMediaMessage 对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = text;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("text");
        req.message = msg;
        req.scene = scene;
//调用api接口，发送数据到微信
        mWxApi.sendReq(req);
    }

    public static void WXShareImag(Bitmap thumbBmp, int scene) {
        if(mTencent == null){
            initWXQQ(UseCaseApplication.intence);
        }
//初始化 WXImageObject 和 WXMediaMessage 对象
        WXImageObject imgObj = new WXImageObject(thumbBmp);
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;
//设置缩略图
        Bitmap thumb;
        if (thumbBmp != null) {
            try {
                thumb = Bitmap.createScaledBitmap(ImageCompressUtil.INSTANCE.compressImage(thumbBmp, 30), 120, 150, true);
            } catch (Exception e) {
                e.printStackTrace();
                thumb = BitmapFactory.decodeResource(mcontext.getResources(), R.drawable.share_icon);
            }
        } else {
            thumb = BitmapFactory.decodeResource(mcontext.getResources(), R.drawable.share_icon);
        }
        msg.thumbData = bmpToByteArray(thumb, true);

//构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");
        req.message = msg;
        req.scene = scene;
//      req.userOpenId = getOpenId();
//调用api接口，发送数据到微信
        mWxApi.sendReq(req);
    }

    public static void WXVoideShare(String url, String title, String description, Bitmap thumbBmp, int scene) {
        if(mTencent == null){
            initWXQQ(UseCaseApplication.intence);
        }
        //初始化一个WXVideoObject，填写url
        WXVideoObject video = new WXVideoObject();
        video.videoUrl = url;

//用 WXVideoObject 对象初始化一个 WXMediaMessage 对象
        WXMediaMessage msg = new WXMediaMessage(video);
        msg.title = title;
        msg.description = description;
        Bitmap thumb;
        if (thumbBmp != null) {
            try {
                thumb = Bitmap.createScaledBitmap(ImageCompressUtil.INSTANCE.compressImage(thumbBmp, 30), 120, 150, true);
            } catch (Exception e) {
                e.printStackTrace();
                thumb = BitmapFactory.decodeResource(mcontext.getResources(), R.drawable.share_icon);
            }
        } else {
            thumb = BitmapFactory.decodeResource(mcontext.getResources(), R.drawable.share_icon);
        }
        msg.thumbData = bmpToByteArray(thumb, true);

//构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("video");
        req.message = msg;
        req.scene = scene;

//调用api接口，发送数据到微信
        mWxApi.sendReq(req);
    }

    public static void WXWebShare(String url, String title, String description, Bitmap thumbBmp, int scene) {
        if(mTencent == null){
            initWXQQ(UseCaseApplication.intence);
        }
        //初始化一个WXWebpageObject，填写url
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;

//用 WXWebpageObject 对象初始化一个 WXMediaMessage 对象
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = description;
        Bitmap thumb;
        if (thumbBmp != null) {
            try {
                thumb = Bitmap.createScaledBitmap(ImageCompressUtil.INSTANCE.compressImage(thumbBmp, 30), 120, 150, true);
            } catch (Exception e) {
                e.printStackTrace();
                thumb = BitmapFactory.decodeResource(mcontext.getResources(), R.drawable.share_icon);
            }
        } else {
            thumb = BitmapFactory.decodeResource(mcontext.getResources(), R.drawable.share_icon);
        }
        msg.thumbData = bmpToByteArray(thumb, true);

//构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = scene;

//调用api接口，发送数据到微信
        mWxApi.sendReq(req);
    }

    public void shareToQQ(Activity context, String title, String summary, String url, String imageUrl, boolean flag) {
        if(mTencent == null){
            initWXQQ(UseCaseApplication.intence);
        }
        if (mTencent.isSessionValid() && mTencent.getOpenId() == null) {
            Toast.makeText(context, "您还未安装QQ", Toast.LENGTH_LONG).show();
        }
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "弘电脑");
        if (flag) {
            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        }
        mTencent.shareToQQ(context, params, this);
    }

    public void shareToQQImage(Activity context, String imagePath,boolean flag) {
        if(mTencent == null){
            initWXQQ(UseCaseApplication.intence);
        }
        if (mTencent.isSessionValid() && mTencent.getOpenId() == null) {
            Toast.makeText(context, "您还未安装QQ", Toast.LENGTH_LONG).show();
        }
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL,imagePath);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "弘电脑");
        if (flag) {
            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        }
        mTencent.shareToQQ(context, params, this);
    }

    public static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    @Override
    public void onComplete(Object o) {


    }

    @Override
    public void onError(UiError uiError) {

    }

    @Override
    public void onCancel() {

    }

}
