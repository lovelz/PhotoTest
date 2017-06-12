package com.lz.selectphoto.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.SharedPreferencesCompat;

/**
 * Created by liuzhu
 * on 2017/5/18.
 */

public class SPUtils {

    private static SPUtils mInstance;

    private final SharedPreferences sp;

    private SPUtils(Context context){
        sp = context.getSharedPreferences("gion.pref", Context.MODE_PRIVATE);
    }

    /**
     * 单例模式获取对象
     * @param context
     * @return
     */
    public static SPUtils getInstance(Context context){
        if (mInstance == null){
            synchronized (SPUtils.class){
                if (mInstance == null){
                    mInstance = new SPUtils(context);
                    return mInstance;
                }
            }
        }
        return mInstance;
    }

    /**
     * 保存string类型的数据
     * @param key
     * @param data
     */
    public void putStringData(String key, String data){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, data);
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }

    /**
     * 得到string类型的数据
     * @param key
     * @param defaultValue
     * @return
     */
    public String getStringData(String key, String defaultValue){
        return sp.getString(key, defaultValue);
    }

    /**
     * 保存int类型的数据
     * @param key
     * @param value
     */
    public void putIntData(String key, int value){
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }

    /**
     * 得到int类型的数据
     * @param key
     * @param defaultValue
     * @return
     */
    public int getIntData(String key, int defaultValue){
        return sp.getInt(key, defaultValue);
    }
}
