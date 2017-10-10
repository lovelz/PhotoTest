package com.lz.selectphoto.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bumptech.glide.Glide;
import com.lz.selectphoto.R;
import com.lz.selectphoto.utils.StreamUtil;
import com.lz.selectphoto.utils.TDevice;
import com.lz.selectphoto.view.CropFloatView;
import com.lz.selectphoto.view.ZoomImageView;

import java.io.FileOutputStream;

/**
 * 照片裁剪界面
 * Created by lovelz
 * on 2017/10/10.
 */

public class PhotoCropActivity extends AppCompatActivity implements View.OnClickListener {

    private ZoomImageView cropPhoto;
    private CropFloatView cropFloat;
    private int cropLength;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.actiivty_photo_crop);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        String cropPhotoPath = getIntent().getStringExtra("crop_photo_path");
        cropLength = (int) TDevice.dp2px(this, 200);

        cropPhoto = (ZoomImageView) findViewById(R.id.crop_photo);
        cropFloat = (CropFloatView) findViewById(R.id.crop_float);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.crop_sure).setOnClickListener(this);

        Glide.with(this).load(cropPhotoPath).centerCrop().into(cropPhoto);
        setCropWidth(cropLength);
        setCropHeight(cropLength);

        start();

    }

    public void start() {
        int height = (int) (TDevice.getScreenHeight(this) - TDevice.dp2px(this, 50));
        int width = TDevice.getDisplayMetrics(this).widthPixels;
        int mHOffset = (width - cropLength) / 2;
        int mVOffset = (height - cropLength) / 2;
        cropPhoto.setHOffset(mHOffset);
        cropPhoto.setVOffset(mVOffset);
        cropFloat.setHOffset(mHOffset);
        cropFloat.setVOffset(mVOffset);
    }

    public void setCropWidth(int mCropWidth) {
        cropFloat.setCropWidth(mCropWidth);
        cropPhoto.setCropWidth(mCropWidth);
    }

    public void setCropHeight(int mCropHeight) {
        cropFloat.setCropHeight(mCropHeight);
        cropPhoto.setCropHeight(mCropHeight);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {
            finish();
        } else if (v.getId() == R.id.crop_sure) {
            Bitmap bitmap = null;
            FileOutputStream os = null;
            try {
                bitmap = cropPhoto.cropBitmap();
                String path = getFilesDir() + "/crop.jpg";
                os = new FileOutputStream(path);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.flush();
                os.close();

                Intent intent = new Intent();
                intent.putExtra("crop_path", path);
                setResult(RESULT_OK, intent);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bitmap != null) bitmap.recycle();
                StreamUtil.close(os);
            }
        }
    }
}
