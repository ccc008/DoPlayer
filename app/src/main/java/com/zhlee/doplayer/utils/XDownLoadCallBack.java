package com.zhlee.doplayer.utils;

import java.io.File;

/**
     * 下载接口
     */
    public interface XDownLoadCallBack {
        void onSuccess(File result);

        void onLoading(long total, long current, boolean isDownloading);

        void onError(Throwable throwable);

        void onStart();
    }