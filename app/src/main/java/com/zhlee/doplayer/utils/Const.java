package com.zhlee.doplayer.utils;

import android.os.Environment;

public class Const {

    // 实际请求地址
    public static String DO_URL = "";
    // 请求地址 - 快手1
    public static final String DO_URL_1 = "https://iapp.ddccs.net/api.php";
    // 请求地址 - 抖音
    public static final String DO_URL_2 = "http://tv.xiaocw.shop/api/api.php?name=dy";

    // 视频存储地址
    public static final String DOWNLOAD_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Video_Online/";

    // 本地播放顺序Key
    public static final String PLAY_ORDER_KEY = "PLAY_ORDER";
    // 正序播放
    public static final String PLAY_ORDER_ASC = "PLAY_ORDER_ASC";
    // 倒序播放
    public static final String PLAY_ORDER_DESC = "PLAY_ORDER_DESC";
    // 随机播放
    public static final String PLAY_ORDER_SHUFFLE = "PLAY_ORDER_SHUFFLE";
    // 我的收藏
    public static final String PLAY_ORDER_FAVOR = "PLAY_ORDER_FAVOR";

    // 播放源key
    public static final String PLAY_RES_KEY = "PLAY_RES";

    // 播放类型 0 本地播放 1 在线播放
    // 本地播放
    public static final int PLAY_TYPE_LOCAL = 0;
    // 在线播放
    public static final int PLAY_TYPE_ONLINE = 1;

    // 删除视频广播
    public static final String ACTION_DELETE_SINGLE_VIDEO = "com.zhlee.doplayer.ACTION.DELETE.SINGLE.VIDEO";
    // 被删除的视频在视频列表中的位置 的 Key
    public static final String VIDEO_DELETE_POSITON_KEY = "deletePositionKey";

}
