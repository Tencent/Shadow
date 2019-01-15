package com.tencent.shadow.demo.main.splash;

public interface ISplashAnimation {

    void start();

    void stop();

    void setAnimationListener(AnimationListener animationListener);


    interface AnimationListener{
        void onAnimationEnd();
    }
}
