package com.lz.selectphoto.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lz.selectphoto.R;
import com.lz.selectphoto.bean.PhotoInfo;
import com.lz.selectphoto.utils.ToastUtils;

import java.util.List;

/**
 * 照片列表适配器
 * Created by liuzhu
 * on 2017/6/5.
 */

public class PhotoSelectAdapter extends BaseRecyclerAdapter<PhotoInfo> {

    private List<PhotoInfo> mAllPhoto;

    public PhotoSelectAdapter(Context context){
        super(context);
    }

    public void setAllPhoto(List<PhotoInfo> mAllPhoto) {
        this.mAllPhoto = mAllPhoto;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultHolder(ViewGroup parent, int viewType) {
        return new PhotoHolder(mInflater.inflate(R.layout.layout_photo_item, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final PhotoHolder photoHolder = (PhotoHolder) holder;
        Glide.with(mContext).load(mDatas.get(position).getPhotoPath()).into(photoHolder.ivPhoto);

        int mPositionNumber = mDatas.get(position).getPhotoNumber();
        photoHolder.tvNumber.setText(mPositionNumber == 0 ? "" : String.valueOf(mPositionNumber));
        photoHolder.tvNumber.setBackgroundResource(mPositionNumber == 0 ?
                R.drawable.sharp_unselect_bg : R.drawable.sharp_select_bg);

        photoHolder.flSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelSelectState(photoHolder.tvNumber, position);
            }
        });
    }

    /**
     * 处理选中与非选中状态
     * @param tvNumber
     * @param position
     */
    private void cancelSelectState(TextView tvNumber, int position) {
        int mNumber = mDatas.get(position).getPhotoNumber();

        if (mNumber == 0) {//选中状态
            int maxNumber = 0;
            for (int i = 0; i < mAllPhoto.size(); i++) {//得到最大的选中数字
                int currentNumber = mAllPhoto.get(i).getPhotoNumber();
                if (currentNumber > maxNumber){
                    maxNumber = currentNumber;
                }
            }
            if (maxNumber == 9){
                ToastUtils.showTextToast(mContext, "最多只能选择9张图片哦");
                return;
            }
            int mNumberText = maxNumber + 1;
            mDatas.get(position).setPhotoNumber(mNumberText);
        }else {//取消状态
            for (int j = 0; j < mAllPhoto.size(); j++){
                int currentNumber = mAllPhoto.get(j).getPhotoNumber();
                if (currentNumber > mNumber){//将取消的数字之后的选中的数字大小-1
                    mAllPhoto.get(j).setPhotoNumber(currentNumber - 1);
                }
            }
            mDatas.get(position).setPhotoNumber(0);
        }

        //设置内容与状态
        int mPositionNumber = mDatas.get(position).getPhotoNumber();
        tvNumber.setText(mPositionNumber == 0 ? "" : String.valueOf(mPositionNumber));
        tvNumber.setBackgroundResource(mPositionNumber == 0 ?
                R.drawable.sharp_unselect_bg : R.drawable.sharp_select_bg);

        if (onPhotoSelectListener != null){
            onPhotoSelectListener.onPhotoSelect(mDatas.get(position));
        }

        notifyDataSetChanged();

    }

    private class PhotoHolder extends RecyclerView.ViewHolder{

        ImageView ivPhoto;
        FrameLayout flSelect;
        TextView tvNumber;
        private PhotoHolder(View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.photo_item_icon);
            flSelect = (FrameLayout) itemView.findViewById(R.id.photo_select);
            tvNumber = (TextView) itemView.findViewById(R.id.photo_number);
        }
    }

    private onPhotoSelectListener onPhotoSelectListener;

    public interface onPhotoSelectListener{
        void onPhotoSelect(PhotoInfo photoInfo);
    }

    public void setOnPhotoSelectListener(PhotoSelectAdapter.onPhotoSelectListener onPhotoSelectListener) {
        this.onPhotoSelectListener = onPhotoSelectListener;
    }
}
