package com.lz.selectphoto.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lz.selectphoto.R;
import com.lz.selectphoto.adapter.BaseRecyclerAdapter;
import com.lz.selectphoto.adapter.PhotoFolderAdapter;
import com.lz.selectphoto.adapter.PhotoSelectAdapter;
import com.lz.selectphoto.bean.PhotoFolder;
import com.lz.selectphoto.bean.PhotoInfo;
import com.lz.selectphoto.utils.TDevice;
import com.lz.selectphoto.utils.ToastUtils;
import com.lz.selectphoto.view.FolderPopupWindow;
import com.lz.selectphoto.view.SpaceGridItemDecoration;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 照片选择界面
 * Created by liuzhu
 * on 2017/6/5.
 */

public class PhotoSelectActivity extends AppCompatActivity implements View.OnClickListener,
        PhotoSelectAdapter.onPhotoSelectListener, BaseRecyclerAdapter.OnItemClickListener{

    private static final String TAG = PhotoSelectActivity.class.getSimpleName();

    private static final int PREVIEW_RESULT_CODE = 1;
    private static final int PREVIEW_CROP_CODE = 2;

    //显示照片列表的RecyclerView
    private RecyclerView mPhotoRecycler;

    private PhotoLoaderListener mLoaderListener = new PhotoLoaderListener();
    private PhotoSelectAdapter photoSelectAdapter;
    private Button btSend;
    private FolderPopupWindow mFolderPopup;
    private LinearLayout mFolderSelect;
    private ImageView mFolderIcon;
    private PhotoFolderAdapter mFolderAdapter;
    private TextView mFolderName;
    private FrameLayout mPhotoBottom;

    //文件夹下的全部图片
    private ArrayList<PhotoInfo> mAllPhoto = new ArrayList<>();
    //选中的图片
    private List<PhotoInfo> mSelectPhoto = new ArrayList<>();
    private ArrayList<PhotoInfo> photoInfoList;

    //是否为单选
    private boolean isRadio;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_select);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        initView();

        initData();

    }

    private void initView() {
        mFolderName = (TextView) findViewById(R.id.select_folder_name);
        mFolderIcon = (ImageView) findViewById(R.id.folder_select_icon);
        mPhotoRecycler = (RecyclerView) findViewById(R.id.photo_select_recycler);
        btSend = (Button) findViewById(R.id.photo_send);
        mPhotoBottom = (FrameLayout) findViewById(R.id.photo_select_bottom);

        mFolderSelect = (LinearLayout) findViewById(R.id.photo_folder_select);
        mFolderSelect.setOnClickListener(this);
        findViewById(R.id.photo_preview).setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        btSend.setOnClickListener(this);
    }

    private void initData() {

        Bundle buildBundle = getIntent().getBundleExtra("photo_build");
        isRadio = buildBundle.getBoolean("is_radio");
        mPhotoBottom.setVisibility(isRadio ? View.GONE : View.VISIBLE);

        //set layoutManager
        mPhotoRecycler.setLayoutManager(new GridLayoutManager(this, 3));
        mPhotoRecycler.addItemDecoration(new SpaceGridItemDecoration((int) TDevice.dipToPx(getResources(), 2)));
        //set adapter
        photoSelectAdapter = new PhotoSelectAdapter(this, isRadio);
        mPhotoRecycler.setAdapter(photoSelectAdapter);
        photoSelectAdapter.setOnPhotoSelectListener(this);

        mFolderAdapter = new PhotoFolderAdapter(this);

        getSupportLoaderManager().initLoader(0, null, mLoaderListener);

        photoSelectAdapter.setOnItemClickListener(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PREVIEW_RESULT_CODE){
            if (data.getExtras() == null) return;
            Bundle extras = data.getExtras();
            //是否为返回
            boolean isBackSelect = extras.getBoolean("is_back_select");
            if (isBackSelect){
                //刷新界面
                List<PhotoInfo> resultPhotoList = (List<PhotoInfo>) extras.getSerializable("photo_preview_result");
                if (resultPhotoList == null || resultPhotoList.size() == 0) return;
                Log.d(TAG, "resultPhotoList-->  " + resultPhotoList.size());

                //首先将所有的图片数字置0（防止后续添加时乱序）
                for (PhotoInfo photoInfo : photoInfoList){
                    photoInfo.setPhotoNumber(0);
                }
                //清除已选择图片集合
                mSelectPhoto.clear();

                //将返回选中的数字添加至photoInfoList中
                for (PhotoInfo photoInfo : photoInfoList){
                    for (PhotoInfo info : resultPhotoList){
                        //判断是否相等
                        if (photoInfo.getPhotoId() == info.getPhotoId()){
                            photoInfo.setPhotoNumber(info.getPhotoNumber());
                            //添加已选择图片
                            mSelectPhoto.add(info);
                        }
                    }
                }

                //刷新
                photoSelectAdapter.notifyDataSetChanged();
                btSend.setText("发送（" + mSelectPhoto.size() + "）");
            } else {
                //返回选择的图片
                List<PhotoInfo> resultPhotoList = (List<PhotoInfo>) extras.getSerializable("photo_preview_result");
                Intent intent = new Intent();
                intent.putExtra("photo_select_result", (Serializable) resultPhotoList);
                setResult(RESULT_OK, intent);
                finish();
            }
        } else if (resultCode == RESULT_OK && requestCode == PREVIEW_CROP_CODE) {
            if (data.getExtras() == null) return;
            String cropPath = data.getStringExtra("crop_path");
            Intent intent = new Intent();
            intent.putExtra("already_crop_path", cropPath);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.photo_folder_select) {
            //弹出选择图片文件夹
            if (mFolderPopup == null){
                FolderPopupWindow folderPopupWindow = new FolderPopupWindow(this, new FolderPopupWindow.Callback() {
                    @Override
                    public void onSelect(FolderPopupWindow popupWindow, PhotoFolder folder) {
//                        mAllPhoto.clear();
//                        mAllPhoto = folder.getPhotoInfoList();
                        notifyPhotoAdapter(folder.getPhotoInfoList());
                        mPhotoRecycler.scrollToPosition(0);
                        popupWindow.dismiss();
                        mFolderName.setText(folder.getFolderName());
                    }

                    @Override
                    public void onDismiss() {
                        mFolderIcon.setImageResource(R.drawable.up_pull);
                    }

                    @Override
                    public void onShow() {
                        mFolderIcon.setImageResource(R.drawable.down_pull);
                    }
                });
                folderPopupWindow.setAdapter(mFolderAdapter);
                mFolderPopup = folderPopupWindow;
            }
            mFolderPopup.showAsDropDown(mFolderSelect);
        } else if (v.getId() == R.id.photo_preview){
            //预览图片
            if (mSelectPhoto == null || mSelectPhoto.size() == 0) {
                ToastUtils.showTextToast(this, "请选择图片");
                return;
            }
            Intent intent = new Intent(this, PhotoViewActivity.class);
            intent.putExtra("photo_preview", (Serializable) mSelectPhoto);
            startActivityForResult(intent, PREVIEW_RESULT_CODE);
        } else if (v.getId() == R.id.photo_send){
            //返回选择的图片
            if (mSelectPhoto == null || mSelectPhoto.size() == 0) {
                ToastUtils.showTextToast(this, "请选择图片");
                return;
            }
            Intent intent = new Intent();
            intent.putExtra("photo_select_result", (Serializable) mSelectPhoto);
            setResult(RESULT_OK, intent);
            finish();
        } else if (v.getId() == R.id.iv_back){
            finish();
        }

    }

    /**
     * 回调回来选择的图片（可能为选中，也可能为取消选中）
     * 通过getPhotoNumber()来判断是否为选中状态
     * @param photoInfo
     */
    @Override
    public void onPhotoSelect(PhotoInfo photoInfo) {
        if (photoInfo.getPhotoNumber() == 0){
            for (PhotoInfo info : mSelectPhoto){
                if (info.getPhotoId() == photoInfo.getPhotoId()){
                    photoInfo = info;
                }
            }
            mSelectPhoto.remove(photoInfo);
            btSend.setText("发送（" + mSelectPhoto.size() + "）");
        }else {
            mSelectPhoto.add(photoInfo);
            btSend.setText("发送（" + mSelectPhoto.size() + "）");
        }
    }

    @Override
    public void onItemClick(int position, long itemId) {
        Intent intent;
        if (isRadio) {
            intent = new Intent(this, PhotoCropActivity.class);
            intent.putExtra("crop_photo_path", photoSelectAdapter.getItem(position).getPhotoPath());
            startActivityForResult(intent, PREVIEW_CROP_CODE);
        } else {
            intent = new Intent(this, PhotoViewActivity.class);
            intent.putExtra("photo_preview", (Serializable) photoSelectAdapter.getDatas());
            intent.putExtra("photo_preview_position", position);
            startActivityForResult(intent, PREVIEW_RESULT_CODE);
        }
    }

    private class PhotoLoaderListener implements LoaderManager.LoaderCallbacks<Cursor> {

        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,                //图片路径
                MediaStore.Images.Media.DISPLAY_NAME,        //图片名称
                MediaStore.Images.Media.DATE_ADDED,          //图片写入时间
                MediaStore.Images.Media._ID,                 //图片的唯一ID
                MediaStore.Images.Media.MINI_THUMB_MAGIC,    //图片缩略图
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME, //图片所在的文件夹名称
                MediaStore.Images.Media.SIZE                 //图片大小
        };

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (id == 0){
                return new CursorLoader(PhotoSelectActivity.this,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        null, null, IMAGE_PROJECTION[2] + " DESC");//根据图片写入时间降序
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null){
                photoInfoList = new ArrayList<>();
                final ArrayList<PhotoFolder> photoFolderList = new ArrayList<>();

                //添加默认的文件夹，里面包含所有的照片
                PhotoFolder defaultFolder = new PhotoFolder();
                defaultFolder.setFolderName("最近照片");
                defaultFolder.setFolderPath("");
                photoFolderList.add(defaultFolder);

                int count = data.getCount();
                if (count > 0){
                    data.moveToFirst();
                    do {
                        String photoPath = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                        String photoName = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                        int photoId = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[3]));
                        String photoFolderName = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[5]));
                        long photoSize = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[6]));

                        //设置PhotoInfo相关信息
                        PhotoInfo photoInfo = new PhotoInfo();
                        photoInfo.setPhotoId(photoId);
                        photoInfo.setPhotoPath(photoPath);
                        photoInfo.setPhotoName(photoName);
                        photoInfo.setPhotoFolderName(photoFolderName);
                        photoInfo.setPhotoNumber(0);
                        photoInfo.setPhotoSize(photoSize);

                        //添加至集合中
                        photoInfoList.add(photoInfo);

                        File photoFile = new File(photoPath);
                        //得到照片文件的上级目录
                        File folderFile = photoFile.getParentFile();
                        PhotoFolder photoFolder = new PhotoFolder();
                        //设置照片文件的属性
                        photoFolder.setFolderName(folderFile.getName());
                        photoFolder.setFolderPath(folderFile.getAbsolutePath());

                        if (!photoFolderList.contains(photoFolder)){
                            photoFolder.getPhotoInfoList().add(photoInfo);
                            photoFolder.setFolderCover(photoInfo.getPhotoPath());//默认相册封面
                            photoFolderList.add(photoFolder);
                        }else {
                            PhotoFolder f = photoFolderList.get(photoFolderList.indexOf(photoFolder));
                            f.getPhotoInfoList().add(photoInfo);
                        }

                    }while (data.moveToNext());
                }

                mAllPhoto.clear();
                mAllPhoto = photoInfoList;
                photoSelectAdapter.setAllPhoto(photoInfoList);

                notifyPhotoAdapter(photoInfoList);

                //设置最近照片
                defaultFolder.getPhotoInfoList().addAll(photoInfoList);
                defaultFolder.setFolderCover(photoInfoList.size() > 0 ? photoInfoList.get(0).getPhotoPath() : null);

                mFolderAdapter.reset(photoFolderList);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

    /**
     * 刷新照片列表
     * @param photoInfoList
     */
    private void notifyPhotoAdapter(ArrayList<PhotoInfo> photoInfoList) {
        Log.d(TAG, "photoInfoList" + photoInfoList.size());
        photoSelectAdapter.clear();
        photoSelectAdapter.addAll(photoInfoList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSelectPhoto.clear();
    }
}
