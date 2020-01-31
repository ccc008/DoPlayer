package com.zhlee.doplayer.utils;

import android.os.Environment;

public class Const {
    // 请求地址
    public static final String DO_URL = "https://iapp.ddccs.net/api.php";
    // 视频存储地址
    public static final String DOWNLOAD_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Video/";

    // 本地播放顺序Key
    public static final String PLAY_ORDER_KEY = "PLAY_ORDER";
    // 正序播放
    public static final String PLAY_ORDER_ASC = "PLAY_ORDER_ASC";
    // 倒序播放
    public static final String PLAY_ORDER_DESC = "PLAY_ORDER_DESC";
    // 随机播放
    public static final String PLAY_ORDER_SHUFFLE = "PLAY_ORDER_SHUFFLE";


}
