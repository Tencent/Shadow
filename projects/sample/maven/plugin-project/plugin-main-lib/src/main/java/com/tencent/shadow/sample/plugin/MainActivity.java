package com.tencent.shadow.sample.plugin;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView vTv = findViewById(R.id.vTv);

        String id = getIntent().getStringExtra("id");
        final String url = "main?"+ (!TextUtils.isEmpty(id) ? id : "id=5837") + "&platform=android";

        FlutterHelper.init(MainActivity.this,
                url,"crm");
        vTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlutterHelper.start(MainActivity.this,
                        url,"crm");
//                Toast.makeText(MainActivity.this,t,Toast.LENGTH_LONG).show();
            }
        });

        ImageView vIvIcon = findViewById(R.id.vIvIcon);
//        vIvIcon.setImageBitmap(getImg("ic_add_file.png"));
    }

    private Bitmap getImg(String file) {
        Bitmap bmp = null;
        //获取AssetsMng对象
        AssetManager am = getResources().getAssets();
        try {
            //打开文件,返回输入流
            InputStream is = am.open(file);
            //Bitmap工厂解码输入流,得到bmp对象
            bmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }
}