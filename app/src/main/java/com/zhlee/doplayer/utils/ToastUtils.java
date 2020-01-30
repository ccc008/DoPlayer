package com.zhlee.doplayer.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * 土司展示类
 * Created by lizh on 2016/9/29.
 */
public class ToastUtils {

    private static Toast mToast;

    /**
     * 显示toast
     *
     * @param msg 土司内容
     */
    public static void showToast(Context ctx, final String msg) {
        // 如果是主线程，直接弹toast
        if (mToast == null) {
            mToast = Toast.makeText(ctx, msg, Toast.LENGTH_LONG);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }

    /**
     * 取消Toast
     */
    public static void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

}
