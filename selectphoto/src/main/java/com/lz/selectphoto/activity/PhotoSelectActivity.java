package com.lz.selectphoto.activity;

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
import android.view.View;
import android.widget.Button;

import com.lz.selectphoto.R;
import com.lz.selectphoto.adapter.PhotoSelectAdapter;
import com.lz.selectphoto.bean.PhotoFolder;
import com.lz.selectphoto.bean.PhotoInfo;
import com.lz.selectphoto.utils.TDevice;
import com.lz.selectphoto.view.SpaceGridItemDecoration;

import java.io.File;
import java.util.ArrayList;

/**
 * 照片选择界面
 * Created by liuzhu
 * on 2017/6/5.
 */

public class PhotoSelectActivity extends AppCompatActivity implements View.OnClickListener{

    //显示照片列表的RecyclerView
    private RecyclerView mPhotoRecycler;

    private PhotoLoaderListener mLoaderListener = new PhotoLoaderListener();
    private PhotoSelectAdapter photoSelectAdapter;
    private Button btSend;

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
        mPhotoRecycler = (RecyclerView) findViewById(R.id.photo_select_recycler);
        btSend = (Button) findViewById(R.id.photo_send);
    }

    private void initData() {
        //set layoutManager
        mPhotoRecycler.setLayoutManager(new GridLayoutManager(this, 3));
        mPhotoRecycler.addItemDecoration(new SpaceGridItemDecoration((int) TDevice.dipToPx(getResources(), 2)));
        //set adapter
        photoSelectAdapter = new PhotoSelectAdapter(this);
        mPhotoRecycler.setAdapter(photoSelectAdapter);

        getSupportLoaderManager().initLoader(0, null, mLoaderListener);
    }

    @Override
    public void onClick(View v) {

    }

    private class PhotoLoaderListener implements LoaderManager.LoaderCallbacks<Cursor> {

        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,               //图片路径
                MediaStore.Images.Media.DISPLAY_NAME,       //图片名称
                MediaStore.Images.Media.DATE_ADDED,         //图片写入时间
                MediaStore.Images.Media._ID,                //图片的唯一ID
                MediaStore.Images.Media.MINI_THUMB_MAGIC,   //图片缩略图
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME //图片所在的文件夹名称
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
                final ArrayList<PhotoInfo> photoInfoList = new ArrayList<>();
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

                        //设置PhotoInfo相关信息
                        PhotoInfo photoInfo = new PhotoInfo();
                        photoInfo.setPhotoId(photoId);
                        photoInfo.setPhotoPath(photoPath);
                        photoInfo.setPhotoName(photoName);
                        photoInfo.setPhotoFolderName(photoFolderName);
                        photoInfo.setPhotoNumber(0);

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

                notifyPhotoAdapter(photoInfoList);

                //设置最近照片
                defaultFolder.getPhotoInfoList().addAll(photoInfoList);
                defaultFolder.setFolderCover(photoInfoList.size() > 0 ? photoInfoList.get(0).getPhotoPath() : null);
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
        photoSelectAdapter.clear();
        photoSelectAdapter.addAll(photoInfoList);
    }

}
