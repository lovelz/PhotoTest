package com.lz.selectphoto.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.lz.selectphoto.bean.PhotoInfo;

import java.util.List;

/**
 * 图片预览适配器
 * Created by liuzhu
 * on 2017/7/19.
 */

public class PhotoPreviewAdapter extends PagerAdapter {

    private Context mContext;
    private List<PhotoInfo> mPhotoInfoList;

    public PhotoPreviewAdapter(Context context, List<PhotoInfo> photoInfoList){
        this.mContext = context;
        this.mPhotoInfoList = photoInfoList;
    }

    @Override
    public int getCount() {
        return mPhotoInfoList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
