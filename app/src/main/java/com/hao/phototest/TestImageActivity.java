package com.hao.phototest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lz.selectphoto.view.PhotoPreview;

/**
 * Created by liuzhu
 * on 2017/6/28.
 */

public class TestImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image);
        PhotoPreview ivText = (PhotoPreview) findViewById(R.id.photo_img);
        Glide.with(this).load(R.mipmap.vip_center_bg).into(ivText);
    }
}
