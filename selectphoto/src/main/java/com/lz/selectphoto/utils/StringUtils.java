package com.lz.selectphoto.utils;

import android.content.Context;
import android.support.annotation.StringRes;

/**
 * 文本工具
 * Created by liuzhu
 * on 2017/6/8.
 */

public class StringUtils {

    /**
     * 得到strings内的文字
     * @param context
     * @param resId
     * @return
     */
    public static String showResValue(Context context, @StringRes int resId){
        return context.getResources().getString(resId);
    }
}
