package com.lz.selectphoto.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
    private static Toast toast = null;
    private static Toast customToast = null;

    public static void showTextToast(Context context, String msg) {
        if (toast == null) {
            toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        }
        toast.setText(msg);
        toast.show();
    }

}
