package com.zhlee.doplayer.utils;

import android.os.Environment;

public class Const {
    // 请求地址
    public static final String DO_URL = "https://iapp.ddccs.net/api.php";
    // 视频存储地址
    public static final String DOWNLOAD_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Video/";

}
