package com.lz.selectphoto.bean;

import java.io.Serializable;

/**
 * 照片相关信息
 * Created by liuzhu
 * on 2017/6/5.
 */

public class PhotoInfo implements Serializable{

    //照片ID
    private int photoId;

    //照片路径
    private String photoPath;

    //照片名称
    private String photoName;

    //照片所在文件夹名称
    private String photoFolderName;

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getPhotoFolderName() {
        return photoFolderName;
    }

    public void setPhotoFolderName(String photoFolderName) {
        this.photoFolderName = photoFolderName;
    }
}
