package com.lz.selectphoto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.lz.selectphoto.activity.PhotoSelectActivity;

/**
 * Created by liuzhu
 * on 2017/6/5.
 */

public class PhotoBuild {

    public static final int PHOTO_SELECT_RESULT = 1;

    private Activity mActivity;
    private Bundle photoBundle;

    private PhotoBuild(Activity activity, Build build) {
        this.mActivity = activity;
        photoBundle = new Bundle();
        photoBundle.putInt("max_photo_number", build.maxNumber);
        photoBundle.putBoolean("is_radio", build.isRadio);
        photoBundle.putBoolean("is_crop", build.isCrop);
    }

    public static class Build {
        private Activity activity;
        private int maxNumber = 3;
        private boolean isRadio = false;
        private boolean isCrop = true;

        public Build(Activity activity) {
            this.activity = activity;
        }

        public Build setMaxNumber(int maxNumber) {
            this.maxNumber = maxNumber;
            return this;
        }

        public Build setRadio(boolean radio) {
            isRadio = radio;
            return this;
        }

        public Build setCrop(boolean crop) {
            isCrop = crop;
            return this;
        }

        public PhotoBuild build() {
            return new PhotoBuild(activity, this);
        }
    }

    public void start() {
        Intent intent = new Intent(mActivity, PhotoSelectActivity.class);
        intent.putExtra("photo_build", photoBundle);
        mActivity.startActivityForResult(intent, PHOTO_SELECT_RESULT);
    }

}
