package com.zhlee.doplayer.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * 土司展示类
 * Created by lizh on 2016/9/29.
 */
public class ToastUtils {
    /**
     * 显示toast
     *
     * @param msg 土司内容
     */
    public static void showToast(Context ctx, final String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }
}
