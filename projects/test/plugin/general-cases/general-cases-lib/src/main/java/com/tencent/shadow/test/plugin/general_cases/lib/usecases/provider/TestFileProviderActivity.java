package com.tencent.shadow.test.plugin.general_cases.lib.usecases.provider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.ImageView;

import com.tencent.shadow.test.plugin.general_cases.lib.R;
import com.tencent.shadow.test.plugin.general_cases.lib.gallery.cases.entity.UseCase;

import java.io.File;

public class TestFileProviderActivity extends Activity {

    public static class Case extends UseCase {
        @Override
        public String getName() {
            return "FileProvider相关测试";
        }

        @Override
        public String getSummary() {
            return "通过使用系统相机拍照来测试FileProvider";
        }

        @Override
        public Class getPageClass() {
            return TestFileProviderActivity.class;
        }
    }

    private static final int REQUEST_CODE = 1001;

    private ImageView mImageView;
    private File mFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test_file_provider);
        mImageView = findViewById(R.id.photo);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 1001);
        }

        findViewById(R.id.go_take_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = String.valueOf(System.currentTimeMillis());
                String filePath = getFilesDir() + "/images/" + fileName + ".jpg";
                mFile = new File(filePath);
                if (!mFile.getParentFile().exists()) {
                    mFile.getParentFile().mkdir();
                }

                Uri contentUri;
                if (targetSdkVersion() >= Build.VERSION_CODES.N) {
                    contentUri = FileProvider.getUriForFile(TestFileProviderActivity.this,
                            "com.tencent.shadow.test.plugin.general_cases.fileprovider", mFile);
//                    contentUri = Uri.parse("content://com.tencent.shadow.contentprovider.authority/com.tencent.shadow.test.plugin.general_cases.lib.gallery.fileprovider" +
//                            "/name/data/data/com.tencent.shadow.test.hostapp/files/images/1548417832706.jpg");
                } else {
                    contentUri = Uri.fromFile(mFile);
                }
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    private int targetSdkVersion() {
        return getApplicationContext().getApplicationInfo().targetSdkVersion;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            setPic();
        }
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mFile.getAbsolutePath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mFile.getAbsolutePath(), bmOptions);
        mImageView.setImageBitmap(bitmap);
    }
}
