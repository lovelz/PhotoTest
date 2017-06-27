package com.lz.selectphoto.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.lz.selectphoto.R;
import com.lz.selectphoto.adapter.BaseRecyclerAdapter;
import com.lz.selectphoto.adapter.PhotoFolderAdapter;
import com.lz.selectphoto.bean.PhotoFolder;

/**
 * Created by liuzhu
 * on 2017/6/27.
 */

public class FolderPopupWindow extends PopupWindow implements View.OnAttachStateChangeListener,
        BaseRecyclerAdapter.OnItemClickListener{

    private Callback mCallback;
    private RecyclerView mFolderRecycler;
    private PhotoFolderAdapter mAdapter;

    public FolderPopupWindow(Context context, Callback callback){
        super(LayoutInflater.from(context).inflate(R.layout.layout_folder_window, null),
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        this.mCallback = callback;

        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);
        setFocusable(true);

        View contentView = getContentView();
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        contentView.addOnAttachStateChangeListener(this);

        mFolderRecycler = (RecyclerView) contentView.findViewById(R.id.folder_recycler);
        mFolderRecycler.setLayoutManager(new LinearLayoutManager(context));
    }

    public void setAdapter(PhotoFolderAdapter folderAdapter){
        this.mAdapter = folderAdapter;
        mFolderRecycler.setAdapter(folderAdapter);
        mAdapter.setOnItemClickListener(this);

    }

    @Override
    public void onViewAttachedToWindow(View v) {
        final Callback callback = this.mCallback;
        if (callback != null){
            callback.onShow();
        }
    }

    @Override
    public void onViewDetachedFromWindow(View v) {
        final Callback callback = this.mCallback;
        if (callback != null){
            callback.onDismiss();
        }
    }

    @Override
    public void onItemClick(int position, long itemId) {
        final Callback callback = this.mCallback;
        if (callback != null){
            callback.onSelect(this, mAdapter.getItem(position));
        }
    }

    /**
     * 回调哟
     */
    public interface Callback{

        void onSelect(FolderPopupWindow popupWindow, PhotoFolder folder);

        void onDismiss();

        void onShow();
    }
}
