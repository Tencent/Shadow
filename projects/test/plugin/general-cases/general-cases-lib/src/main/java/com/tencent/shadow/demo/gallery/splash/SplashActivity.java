package com.tencent.shadow.demo.gallery.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tencent.shadow.demo.gallery.MainActivity;
import com.tencent.shadow.demo.gallery.R;

public class SplashActivity extends Activity {

    private SplashAnimation mSplashAnimation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_splash);

        mSplashAnimation = new SplashAnimation(this);
        mSplashAnimation.start();

        mSplashAnimation.setAnimationListener(new ISplashAnimation.AnimationListener() {
            @Override
            public void onAnimationEnd() {
                finish();

                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
        });
    }
}
