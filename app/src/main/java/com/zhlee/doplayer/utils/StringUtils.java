package com.zhlee.doplayer.utils;

import android.text.TextUtils;

public class StringUtils {

    public static String parseUrl(String result) {
        String url = "";
        if (!TextUtils.isEmpty(result)) {
            try {
                url = result.split("\"")[1];
                url = url.substring(0, url.indexOf("mp4") + 3);
                LogUtil.log("url::" + url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return url;
    }
}
