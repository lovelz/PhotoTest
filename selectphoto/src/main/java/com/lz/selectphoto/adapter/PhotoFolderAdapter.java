package com.lz.selectphoto.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lz.selectphoto.R;
import com.lz.selectphoto.bean.PhotoFolder;

/**
 * Created by liuzhu
 * on 2017/6/27.
 */

public class PhotoFolderAdapter extends BaseRecyclerAdapter<PhotoFolder> {

    public PhotoFolderAdapter(Context context) {
        super(context);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultHolder(ViewGroup parent, int viewType) {
        return new FolderHolder(mInflater.inflate(R.layout.layout_folder_item, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, int position) {
        FolderHolder folderHolder = (FolderHolder) holder;
        folderHolder.tvName.setText(mDatas.get(position).getFolderName());
        Glide.with(mContext).load(mDatas.get(position).getFolderCover()).into(folderHolder.ivCover);
    }

    private class FolderHolder extends RecyclerView.ViewHolder{

        ImageView ivCover;
        TextView tvName;
        private FolderHolder(View itemView) {
            super(itemView);
            ivCover = (ImageView) itemView.findViewById(R.id.folder_cover);
            tvName = (TextView) itemView.findViewById(R.id.folder_name);
        }
    }
}
