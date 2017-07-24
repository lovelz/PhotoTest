package com.lz.selectphoto.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.lz.selectphoto.R;
import com.lz.selectphoto.adapter.PhotoPreviewAdapter;
import com.lz.selectphoto.bean.PhotoInfo;

import java.util.List;

/**
 * Created by liuzhu
 * on 2017/7/24.
 */

public class PhotoViewActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener{

    private ViewPager vpPhoto;
    private List<PhotoInfo> photoInfoList;
    private TextView tvPosition;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        photoInfoList = (List<PhotoInfo>) getIntent().getSerializableExtra("photo_preview");

        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.photo_view_send).setOnClickListener(this);
        vpPhoto = (ViewPager) findViewById(R.id.photo_view_pager);
        tvPosition = (TextView) findViewById(R.id.photo_view_position);

        initView();
    }

    private void initView() {
        tvPosition.setText(1 + "/" + photoInfoList.size());
        PhotoPreviewAdapter previewAdapter = new PhotoPreviewAdapter(this, photoInfoList);
        vpPhoto.setAdapter(previewAdapter);
        vpPhoto.addOnPageChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back){
            finish();
        }else if (v.getId() == R.id.photo_view_send){
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        tvPosition.setText((position + 1) + "/" + photoInfoList.size());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
