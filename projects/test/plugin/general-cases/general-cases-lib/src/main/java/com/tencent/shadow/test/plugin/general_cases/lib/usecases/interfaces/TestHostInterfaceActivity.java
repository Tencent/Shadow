package com.tencent.shadow.test.plugin.general_cases.lib.usecases.interfaces;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.tencent.shadow.test.lib.plugin_use_host_code_lib.interfaces.HostTestInterface;
import com.tencent.shadow.test.lib.plugin_use_host_code_lib.other.HostOtherInterface;
import com.tencent.shadow.test.plugin.general_cases.lib.R;
import com.tencent.shadow.test.plugin.general_cases.lib.gallery.cases.entity.UseCase;
import com.tencent.shadow.test.plugin.general_cases.lib.usecases.BaseAndroidTestActivity;

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
        setContentView(R.layout.layout_host_interface);

    }

    public void doClick(View view){
        TextView textView = findViewById(R.id.text);
        textView.setText(HostTestInterface.getText());
    }

    public void doClick1(View view){
        String str = "";
        try {
            str = HostOtherInterface.getText();
        } catch (NoClassDefFoundError e) {
            str = "ClassNotFound";
        }
        TextView textView = findViewById(R.id.text);
        textView.setText(str);
    }
}