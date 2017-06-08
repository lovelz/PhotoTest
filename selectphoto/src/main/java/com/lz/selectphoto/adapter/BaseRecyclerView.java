package com.lz.selectphoto.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuzhu
 * on 2017/6/6.
 */

public abstract class BaseRecyclerView<T> extends RecyclerView.Adapter {

    protected Context mContext;
    protected LayoutInflater mInflater;
    protected List<T> mDatas;

    public BaseRecyclerView(Context context){
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);

        mDatas = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = onCreateDefaultHolder(parent, viewType);
        if (holder != null){
            holder.itemView.setTag(holder);
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
     * 清除所有数据
     */
    public final void clear(){
        this.mDatas.clear();
        notifyDataSetChanged();
    }
}
