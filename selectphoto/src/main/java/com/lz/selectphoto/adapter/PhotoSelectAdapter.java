package com.lz.selectphoto.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lz.selectphoto.R;
import com.lz.selectphoto.bean.PhotoInfo;

/**
 * 照片列表适配器
 * Created by liuzhu
 * on 2017/6/5.
 */

public class PhotoSelectAdapter extends BaseRecyclerView<PhotoInfo>{

    public PhotoSelectAdapter(Context context){
        super(context);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultHolder(ViewGroup parent, int viewType) {
        return new PhotoHolder(mInflater.inflate(R.layout.layout_photo_item, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, int position) {
        PhotoHolder photoHolder = (PhotoHolder) holder;
        Glide.with(mContext).load(mDatas.get(position).getPhotoPath()).into(photoHolder.ivPhoto);
    }

    private class PhotoHolder extends RecyclerView.ViewHolder{

        ImageView ivPhoto;
        private PhotoHolder(View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.photo_item_icon);
        }
    }

}
