package com.lz.selectphoto.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.lz.selectphoto.R;
import com.lz.selectphoto.bean.PhotoInfo;
import com.lz.selectphoto.view.PhotoPreview;

import java.util.List;

/**
 * 图片预览适配器
 * Created by liuzhu
 * on 2017/7/19.
 */

public class PhotoPreviewAdapter extends PagerAdapter {

    private Context mContext;
    private List<PhotoInfo> mPhotoInfoList;
    private LayoutInflater mInflater;

    public PhotoPreviewAdapter(Context context, List<PhotoInfo> photoInfoList){
        this.mContext = context;
        this.mPhotoInfoList = photoInfoList;
        mInflater = LayoutInflater.from(context);
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
        View view = mInflater.inflate(R.layout.layout_photo_view, container, false);
        PhotoPreview photoPreview = (PhotoPreview) view.findViewById(R.id.photo_view_item);
        Glide.with(mContext).load(mPhotoInfoList.get(position).getPhotoPath()).into(photoPreview);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
