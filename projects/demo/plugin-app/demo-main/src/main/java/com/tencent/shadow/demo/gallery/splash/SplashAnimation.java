package com.tencent.shadow.demo.gallery.splash;

import android.content.Context;
import android.os.Handler;

import com.tencent.shadow.demo.gallery.util.ToastUtil;

public class SplashAnimation implements ISplashAnimation{

    private AnimationListener mAnimationListener;

    private Context mContext;

    public SplashAnimation(Context context){
        mContext = context;
    }


    @Override
    public void start() {
        ToastUtil.showToast(mContext,"animation start");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mAnimationListener != null){
                    mAnimationListener.onAnimationEnd();
                }
            }
        },2000);
    }

    @Override
    public void stop() {

    }

    @Override
    public void setAnimationListener(AnimationListener animationListener) {
        mAnimationListener = animationListener;
    }
}
