package me.rjy.android.shadow.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

public class MyMainActivity extends Activity {
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      setContentView(R.layout.layout_my_main_activity);

      Button button = (Button)findViewById(R.id.my_button);
      button.setOnClickListener(v -> {
         Log.d("MyMainActivity", "[rjy] onClick()");
         Intent intent = new Intent();
         intent.setClassName(this, "com.tencent.shadow.sample.plugin.app.lib.gallery.splash.SplashActivity");
         startActivity(intent);
      });


   }
}
