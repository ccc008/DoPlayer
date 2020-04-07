package com.zhlee.doplayer.utils;

import android.text.TextUtils;

import com.zhlee.doplayer.bean.Res2Bean;

public class StringUtils {

    /**
     * 解析源1返回值
     * @param result
     * @return
     */
    public static String parseUrl1(String result) {
        String url = "";
        if (!TextUtils.isEmpty(result)) {
            try {
                url = result.split("\"")[1];
                url = url.substring(0, url.indexOf("mp4") + 3);
                LogUtil.log("源1url::" + url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return url;
    }

    /**
     * 解析源2返回值
     * @param result
     * @return
     */
    public static String parseUrl2(String result) {
        String url = "";
        if (!TextUtils.isEmpty(result)) {
            try {
                Res2Bean res2Bean = new Res2Bean();
                res2Bean = (Res2Bean) res2Bean.fromJson(result);
                url = res2Bean.getSrc_tv();
                LogUtil.log("源2url::" + url);
            } catch (Exception e) {
                LogUtil.log("解析源2地址失败::" + e.getMessage());
                e.printStackTrace();
            }
        }
        return url;
    }
}
