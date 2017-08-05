package com.lz.selectphoto.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lz.selectphoto.R;
import com.lz.selectphoto.adapter.PhotoPreviewAdapter;
import com.lz.selectphoto.bean.PhotoInfo;
import com.lz.selectphoto.utils.ToastUtils;
import com.lz.selectphoto.view.PreviewPager;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuzhu
 * on 2017/7/24.
 */

public class PhotoViewActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener{

    private PreviewPager vpPhoto;
    //显示的图片集合
    private List<PhotoInfo> photoInfoList;
    //当前图片所在的位置
    private TextView tvPosition;
    //当前图片选择状态
    private TextView tvNumber;
    //原图总大小
    private TextView mOriginalSize;
    //选择原图图标
    private ImageView ivOriginalIcon;

    //点击进来的位置
    private int currentPosition;

    //已选择的图片
    private List<PhotoInfo> mSelectPhoto = new ArrayList<>();

    //初始化选中图片大小
    private long mMaxSize = 0;

    //是否为原图发送
    private boolean isOriginal = false;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        photoInfoList = (List<PhotoInfo>) getIntent().getSerializableExtra("photo_preview");
        currentPosition = getIntent().getIntExtra("photo_preview_position", 0);

        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.photo_view_send).setOnClickListener(this);
        findViewById(R.id.photo_view_original_select).setOnClickListener(this);
        vpPhoto = (PreviewPager) findViewById(R.id.photo_view_pager);
        tvPosition = (TextView) findViewById(R.id.photo_view_position);
        tvNumber = (TextView) findViewById(R.id.photo_view_number);
        mOriginalSize = (TextView) findViewById(R.id.photo_view_original_size);
        ivOriginalIcon = (ImageView) findViewById(R.id.photo_view_select_icon);
        tvNumber.setOnClickListener(this);

        initView();

        initData();
    }

    private void initView() {
        //初始化位置信息
        tvPosition.setText((currentPosition + 1) + "/" + photoInfoList.size());
        PhotoPreviewAdapter previewAdapter = new PhotoPreviewAdapter(this, vpPhoto, photoInfoList);
        vpPhoto.setAdapter(previewAdapter);
        vpPhoto.addOnPageChangeListener(this);
        //设置位置
        vpPhoto.setCurrentItem(currentPosition);
    }

    private void initData() {
        //初始化最先的数字圆点状态（此时onPageSelected并不会触发）
        int currentNumber = photoInfoList.get(currentPosition).getPhotoNumber();
        if (currentNumber != 0){
            tvNumber.setText(currentNumber + "");
            tvNumber.setBackgroundResource(R.drawable.shape_select_bg);
        }else {
            tvNumber.setText("");
            tvNumber.setBackgroundResource(R.drawable.shape_unselect_bg);
        }

        for (PhotoInfo photoInfo : photoInfoList){
            if (photoInfo.getPhotoNumber() != 0){
                mSelectPhoto.add(photoInfo);
                mMaxSize += photoInfo.getPhotoSize();
            }
        }

        mOriginalSize.setText(getFormatSize(mMaxSize));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back){
            //返回
            backSelectPhoto();
        }else if (v.getId() == R.id.photo_view_send){
            //选择发送
            if (mSelectPhoto == null || mSelectPhoto.size() == 0){
                ToastUtils.showTextToast(this, "请选择图片");
                return;
            }

            Intent intent = new Intent();
            intent.putExtra("is_back_select", false);
            intent.putExtra("photo_preview_result", (Serializable) mSelectPhoto);
            setResult(RESULT_OK, intent);
            finish();
        } else if (v.getId() == R.id.photo_view_number){
            //改变选中状态
            changePhotoNumber();
        } else if (v.getId() == R.id.photo_view_original_select){
            if (mSelectPhoto == null || mSelectPhoto.size() == 0){
                ToastUtils.showTextToast(this, "请选择图片");
                return;
            }
            //选择原图发送
            isOriginal = !isOriginal;
            if (isOriginal){
                ivOriginalIcon.setImageResource(R.drawable.original_select_icon);
                mOriginalSize.setVisibility(View.VISIBLE);
            }else {
                ivOriginalIcon.setImageResource(R.drawable.shape_unselect_bg);
                mOriginalSize.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 返回上一层时需要刷新数据
     */
    private void backSelectPhoto() {
        Intent intent = new Intent();
        intent.putExtra("photo_preview_result", (Serializable) mSelectPhoto);
        intent.putExtra("is_back_select", true);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 改变照片数字信息（改变所有的）
     */
    private void changePhotoNumber() {
        int changePosition = vpPhoto.getCurrentItem();
        int currentNumber = photoInfoList.get(changePosition).getPhotoNumber();
        if (currentNumber == 0) {
            //从不选中到选中状态（只需得到当前最大的数字即可）
            int maxNumber = 0;
            for (int i = 0; i < photoInfoList.size(); i++) {
                //遍历得到最大的数字
                int photoNumber = photoInfoList.get(i).getPhotoNumber();
                if (maxNumber < photoNumber) maxNumber = photoNumber;
            }

            if (maxNumber > 8){
                ToastUtils.showTextToast(this, "最多只能选择9张图片哦");
                return;
            }
            photoInfoList.get(changePosition).setPhotoNumber(maxNumber + 1);
            //改变数字圆点的状态
            tvNumber.setText((maxNumber + 1) + "");
            tvNumber.setBackgroundResource(R.drawable.shape_select_bg);

            //改变选择状态以及大小
            mSelectPhoto.add(photoInfoList.get(changePosition));
            mMaxSize += photoInfoList.get(changePosition).getPhotoSize();
            mOriginalSize.setText(getFormatSize(mMaxSize));

        } else {
            //从选中到不选中状态（需要将比这个数字大的都减去1）
            for (PhotoInfo photoInfo : photoInfoList){
                int photoNumber = photoInfo.getPhotoNumber();
                if (photoNumber > currentNumber){
                    //比较大小减1
                    photoInfo.setPhotoNumber(photoNumber - 1);
                }
            }

            photoInfoList.get(changePosition).setPhotoNumber(0);//置零
            //改变数字圆点的状态
            tvNumber.setText("");
            tvNumber.setBackgroundResource(R.drawable.shape_unselect_bg);

            //改变选择状态以及大小
            mSelectPhoto.remove(photoInfoList.get(changePosition));
            mMaxSize -= photoInfoList.get(changePosition).getPhotoSize();
            mOriginalSize.setText(getFormatSize(mMaxSize));
        }
    }

    /**
     * 格式化单位
     * @param size
     * @return
     */
    private String getFormatSize(long size){
        if (size < 1000){
            return size + "Byte";
        }

        double kbSize = size / 1024;
        if (kbSize < 1000){
            BigDecimal result1 = new BigDecimal(Double.toString(kbSize));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double mSize = kbSize / 1024;
        if (mSize < 1000){
            BigDecimal result2 = new BigDecimal(Double.toString(mSize));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        return "0KB";
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //选择其它页面时改变数字圆点的状态
        tvPosition.setText((position + 1) + "/" + photoInfoList.size());
        int currentNumber = photoInfoList.get(position).getPhotoNumber();
        if (currentNumber != 0){
            //为选中（带有数字标签）
            tvNumber.setText(currentNumber + "");
            tvNumber.setBackgroundResource(R.drawable.shape_select_bg);
        }else {
            //不为选中
            tvNumber.setText("");
            tvNumber.setBackgroundResource(R.drawable.shape_unselect_bg);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onBackPressed() {
        backSelectPhoto();
    }
}
