package com.lz.selectphoto.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuzhu
 * on 2017/6/6.
 */

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter {

    protected Context mContext;
    protected LayoutInflater mInflater;
    protected List<T> mDatas;

    private OnItemClickListener onItemClickListener;
    private OnClickListener onClickListener;

    public BaseRecyclerAdapter(Context context){
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);

        mDatas = new ArrayList<>();

        initListener();
    }

    private void initListener() {
        onClickListener = new OnClickListener() {
            @Override
            public void onClick(int adapterPosition, long itemId) {
                if (onItemClickListener != null){
                    onItemClickListener.onItemClick(adapterPosition, itemId);
                }
            }
        };
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = onCreateDefaultHolder(parent, viewType);
        if (holder != null){
            holder.itemView.setTag(holder);
            holder.itemView.setOnClickListener(onClickListener);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        onBindDefaultViewHolder(holder, position);
    }

    protected abstract RecyclerView.ViewHolder onCreateDefaultHolder(ViewGroup parent, int viewType);

    protected abstract void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, int position);

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    /**
     * 添加多个数据
     * @param datas
     */
    public void addAll(List<T> datas){
        if (datas != null){
            mDatas.addAll(datas);
            notifyItemRangeInserted(this.mDatas.size(), datas.size());
        }
    }

    /**
     * 得到指定位置的数据
     * @param position
     * @return
     */
    public final T getItem(int position){
        if (position < 0 || position > mDatas.size()){
            return null;
        }
        return mDatas.get(position);
    }

    /**
     * 清除所有数据
     */
    public final void clear(){
        this.mDatas.clear();
        notifyDataSetChanged();
    }

    /**
     * 重置数据
     * @param datas
     */
    public final void reset(List<T> datas){
        if (datas != null){
            clear();
            addAll(datas);
        }
    }

    public static abstract class OnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) v.getTag();
            onClick(holder.getAdapterPosition(), holder.getItemId());
        }

        public abstract void onClick(int adapterPosition, long itemId);
    }

    public interface OnItemClickListener{
        void onItemClick(int position, long itemId);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
