package com.zhlee.doplayer.utils;

/**
     * Post异步请求接口
     */
    public interface XCallBack {
        void onResponse(String result);

        void onError(Throwable throwable);
    }
