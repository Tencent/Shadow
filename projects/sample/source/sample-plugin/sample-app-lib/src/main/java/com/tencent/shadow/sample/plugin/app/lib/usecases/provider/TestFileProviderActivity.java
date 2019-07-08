/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tencent.shadow.sample.plugin.app.lib.usecases.provider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.tencent.shadow.sample.plugin.app.lib.R;
import com.tencent.shadow.sample.plugin.app.lib.gallery.cases.entity.UseCase;

import java.io.File;


public class TestFileProviderActivity extends Activity {
    private static final String TAG = "TestFileProviderActivity";
    private static final String KEY_FILE_PATH = "filePath";

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
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_FILE_PATH, mFile.getAbsolutePath());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String filePath = savedInstanceState.getString(KEY_FILE_PATH);
        if (!TextUtils.isEmpty(filePath)) {
            mFile = new File(filePath);
        }
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

    @SuppressLint("LongLogTag")
    private void setPic() {
        if (mFile == null || !mFile.exists()) {
            Log.w(TAG, "setPic: file don't exist");
            return;
        }

        mImageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                // Get the dimensions of the View
                int targetW = mImageView.getWidth();
                int targetH = mImageView.getHeight();
                mImageView.getViewTreeObserver().removeOnPreDrawListener(this);
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
                return false;
            }
        });
    }
}
