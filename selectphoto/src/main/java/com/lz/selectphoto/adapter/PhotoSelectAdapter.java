package com.lz.selectphoto.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lz.selectphoto.R;
import com.lz.selectphoto.bean.PhotoInfo;

import java.util.ArrayList;

/**
 * 照片列表适配器
 * Created by liuzhu
 * on 2017/6/5.
 */

public class PhotoSelectAdapter extends RecyclerView.Adapter{

    private Context mContext;
    private ArrayList<PhotoInfo> mPhotoInfoList;
    private LayoutInflater mInflater;

    public PhotoSelectAdapter(Context context){
        this.mContext = context;
        mPhotoInfoList = new ArrayList<>();

        mInflater = LayoutInflater.from(context);

    }

    @Override
    public int getItemCount() {
        return mPhotoInfoList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhotoHolder(mInflater.inflate(R.layout.layout_photo_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PhotoHolder photoHolder = (PhotoHolder) holder;
        Glide.with(mContext).load(mPhotoInfoList.get(position).getPhotoPath()).into(photoHolder.ivPhoto);
    }

    private class PhotoHolder extends RecyclerView.ViewHolder{

        ImageView ivPhoto;
        private PhotoHolder(View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.photo_item_icon);
        }
    }

    /**
     * 添加数据，刷新列表
     * @param photoInfoList
     */
    public void addAll(ArrayList<PhotoInfo> photoInfoList){
        if (photoInfoList != null){
            mPhotoInfoList.addAll(photoInfoList);
            notifyItemRangeInserted(this.mPhotoInfoList.size(), photoInfoList.size());
        }
    }
}
