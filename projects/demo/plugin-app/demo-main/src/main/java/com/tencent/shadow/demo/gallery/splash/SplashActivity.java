package com.tencent.shadow.demo.gallery.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tencent.shadow.demo.gallery.MainActivity;
import com.tencent.shadow.demo.gallery.R;
import com.tencent.shadow.demo.gallery.cases.UseCaseManager;
import com.tencent.shadow.demo.gallery.cases.entity.UseCase;
import com.tencent.shadow.demo.gallery.util.ToastUtil;

public class SplashActivity extends Activity {

    private SplashAnimation mSplashAnimation;

    private int caseId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_splash);

        caseId = getIntent().getIntExtra("caseId",-1);

        mSplashAnimation = new SplashAnimation(this);
        mSplashAnimation.start();

        mSplashAnimation.setAnimationListener(new ISplashAnimation.AnimationListener() {
            @Override
            public void onAnimationEnd() {
                finish();

                if(caseId != -1){
                    UseCase useCase = UseCaseManager.findTestCaseById(caseId);
                    if (useCase != null) {
                        startActivity(new Intent(SplashActivity.this, useCase.pageClass));
                        return;
                    }else {
                        ToastUtil.showToast(SplashActivity.this,"没有找到对应的测试用例，请检测是否在TestCaseManager 中正确注册了");
                    }
                }
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
        });
    }
}
