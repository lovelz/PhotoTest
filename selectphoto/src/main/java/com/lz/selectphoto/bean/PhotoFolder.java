package com.lz.selectphoto.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 文件夹相关信息
 * Created by liuzhu
 * on 2017/6/6.
 */

public class PhotoFolder implements Serializable{

    //文件夹名称
    private String folderName;

    //文件夹路径
    private String folderPath;

    //文件夹封面
    private String folderCover;

    //文件夹内照片的数量
    private int number;

    //文件夹内所有照片
    private ArrayList<PhotoInfo> photoInfoList = new ArrayList<>();

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public String getFolderCover() {
        return folderCover;
    }

    public void setFolderCover(String folderCover) {
        this.folderCover = folderCover;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public ArrayList<PhotoInfo> getPhotoInfoList() {
        return photoInfoList;
    }

    public void setPhotoInfoList(ArrayList<PhotoInfo> photoInfoList) {
        this.photoInfoList = photoInfoList;
    }

    @Override
    public boolean equals(Object obj) {
        //判断文件夹路径是否相同
        if (obj != null && obj instanceof PhotoFolder){
            if (((PhotoFolder) obj).getFolderPath() == null && folderPath != null){
                return false;
            }
            String oPath = ((PhotoFolder) obj).getFolderPath().toLowerCase();
            return oPath.equals(this.folderPath.toLowerCase());
        }
        return false;
    }
}
