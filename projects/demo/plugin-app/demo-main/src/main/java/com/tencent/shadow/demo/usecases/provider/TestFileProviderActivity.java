package com.tencent.shadow.demo.usecases.provider;

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

import com.tencent.shadow.demo.gallery.R;

import java.io.File;

public class TestFileProviderActivity extends Activity {

    private static final int REQUEST_CODE = 1001;

    private ImageView mImageView;
    private File mFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test_file_provider);
        mImageView = findViewById(R.id.photo);

        String fileName = String.valueOf(System.currentTimeMillis());
        String filePath = getFilesDir() + "/images/" + fileName + ".jpg";
        mFile = new File(filePath);
        if (!mFile.getParentFile().exists()) {
            mFile.getParentFile().mkdir();
        }
//                if (!mFile.exists()) {
//                    try {
//                        File.createTempFile(fileName, ".jpg", new File(getFilesDir().getAbsolutePath() + "/images/"));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }


        findViewById(R.id.go_take_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri contentUri;
                if (targetSdkVersion() >= Build.VERSION_CODES.N) {
                    contentUri = FileProvider.getUriForFile(TestFileProviderActivity.this,
                            "com.tencent.shadow.demo_install.fileprovider", mFile);
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
