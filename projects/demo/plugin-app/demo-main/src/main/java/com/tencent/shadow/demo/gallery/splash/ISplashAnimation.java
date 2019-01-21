package com.tencent.shadow.demo.gallery.splash;

public interface ISplashAnimation {

    void start();

    void stop();

    void setAnimationListener(AnimationListener animationListener);


    interface AnimationListener{
        void onAnimationEnd();
    }
}
