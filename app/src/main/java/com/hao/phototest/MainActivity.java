package com.hao.phototest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lz.selectphoto.activity.PhotoSelectActivity;
import com.lz.selectphoto.bean.PhotoInfo;
import com.lz.selectphoto.utils.ToastUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int SELECT_PHOTO_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView enterSelect = (TextView) findViewById(R.id.enter_select_photo);
        enterSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, PhotoSelectActivity.class), SELECT_PHOTO_CODE);
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == SELECT_PHOTO_CODE){
            if (data.getExtras() == null) return;
            List<PhotoInfo> selectPhotoList = (List<PhotoInfo>) data.getExtras().getSerializable("photo_select_result");
            if (selectPhotoList == null || selectPhotoList.size() == 0) return;
            ToastUtils.showTextToast(MainActivity.this, selectPhotoList.size() + "");
        }
    }
}
