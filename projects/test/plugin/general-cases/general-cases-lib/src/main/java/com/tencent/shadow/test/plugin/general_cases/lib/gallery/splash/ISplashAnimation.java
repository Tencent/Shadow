package com.tencent.shadow.test.plugin.general_cases.lib.gallery.splash;

public interface ISplashAnimation {

    void start();

    void stop();

    void setAnimationListener(AnimationListener animationListener);


    interface AnimationListener{
        void onAnimationEnd();
    }
}
