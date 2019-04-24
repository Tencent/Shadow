package com.tencent.shadow.demo.usecases.interfaces;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.tencent.shadow.demo.gallery.R;
import com.tencent.shadow.demo.gallery.cases.entity.UseCase;
import com.tencent.shadow.demo.interfaces.HostTestInterface;
import com.tencent.shadow.demo.usecases.BaseAndroidTestActivity;

public class TestHostInterfaceActivity extends BaseAndroidTestActivity {

    public static class Case extends UseCase {
        @Override
        public String getName() {
            return "测试访问宿主中的类";
        }

        @Override
        public String getSummary() {
            return "测试白名单中配置的宿主中的类";
        }

        @Override
        public Class getPageClass() {
            return TestHostInterfaceActivity.class;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_common);

    }

    public void doClick(View view){
        TextView textView = findViewById(R.id.text);
        textView.setText(HostTestInterface.getText());
    }
}