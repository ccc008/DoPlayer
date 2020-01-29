package com.zhlee.doplayer.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.Map;

public class XUtils {

    private static final int READ_TIME_OUT = 30 * 1000;
    private static final int CONNECT_TIME_OUT = 30 * 1000;
    private volatile static XUtils instance;
    private Handler handler;

    private XUtils() {
        handler = new Handler(Looper.getMainLooper());
    }

    /**
     * 单例模式
     *
     * @return xUtils对象
     */
    public static XUtils getInstance() {
        if (instance == null) {
            synchronized (XUtils.class) {
                if (instance == null) {
                    instance = new XUtils();
                }
            }
        }
        return instance;
    }

    /**
     * @param url      请求地址
     * @param headers  头
     * @param maps     参数
     * @param callBack 回调
     */
    public void get(String url, Map<String, String> headers, Map<String, String> maps, final XCallBack callBack) {
        RequestParams params = new RequestParams(url);
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                params.addHeader(header.getKey(), header.getValue());
            }
        }
        if (maps != null && !maps.isEmpty()) {
            for (Map.Entry<String, String> entry : maps.entrySet()) {
                params.addQueryStringParameter(entry.getKey(), entry.getValue());
            }
        }
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                onSuccessResponse(result, callBack);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
                handler.post(() -> {
                    if (callBack != null) {
                        callBack.onError(ex);
                    }
                });
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 异步get请求返回结果,json字符串
     *
     * @param result
     * @param callBack
     */
    private void onSuccessResponse(final String result, final XCallBack callBack) {
        handler.post(() -> {
            if (callBack != null) {
                callBack.onResponse(result);
            }
        });
    }

    /**
     * 异步post请求
     *
     * @param url      请求的url
     * @param maps     上传参数合集
     * @param callback 回调接口
     */
    public void post(String url, Map<String, String> maps, final XCallBack callback) {
        RequestParams params = new RequestParams(url);
//        params.setReadTimeout(Const.READ_TIME_OUT);
//        params.setConnectTimeout(Const.CONNECT_TIME_OUT);
        params.setAsJsonContent(true);
        if (maps != null && !maps.isEmpty()) {
            // 键值对形式上传
            for (Map.Entry<String, String> entry : maps.entrySet()) {
                params.addBodyParameter(entry.getKey(), entry.getValue());
            }
        }
        Log.i("XUtils", params.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                handler.post(() -> {
                    if (callback != null) {
                        callback.onResponse(result);
                    }
                });
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                handler.post(() -> {
                    if (callback != null) {
                        callback.onError(ex);
                    }
                });
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }


    /**
     * 异步post请求
     * 请求格式为Json
     *
     * @param url      请求的url
     * @param maps     上传参数合集
     * @param callback 回调接口
     */
    public void postJson(String url, Map<String, String> maps, final XCallBack callback) {
        RequestParams params = new RequestParams(url);
//        params.setReadTimeout(Const.READ_TIME_OUT);
//        params.setConnectTimeout(Const.CONNECT_TIME_OUT);
        params.setAsJsonContent(true);
        if (maps != null && !maps.isEmpty()) {
            // 键值对形式上传
//            for (Map.Entry<String, String> entry : maps.entrySet()) {
//                params.addBodyParameter(entry.getKey(), entry.getValue());
//            }
            // Json形式上传
            JSONObject jsonObject = new JSONObject(maps);
            params.setBodyContent(jsonObject.toString());
        }
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                handler.post(() -> {
                    if (callback != null) {
                        callback.onResponse(result);
                    }
                });
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                handler.post(() -> {
                    if (callback != null) {
                        callback.onError(ex);
                    }
                });
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 异步上传JSON
     *
     * @param url      上传地址
     * @param json     json字符串
     * @param callback 回调
     */
    public void postJson(String url, String json, final XCallBack callback) {
        RequestParams params = new RequestParams(url);
        params.setReadTimeout(READ_TIME_OUT);
        params.setConnectTimeout(CONNECT_TIME_OUT);
        params.setAsJsonContent(true);
        if (json != null) {
            params.setBodyContent(json);
        }
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                handler.post(() -> {
                    if (callback != null) {
                        callback.onResponse(result);
                        LogUtil.log("" + params.getBodyContent());
                    }
                });
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                handler.post(() -> {
                    if (callback != null) {
                        callback.onError(ex);
                    }
                });
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 文件下载
     *
     * @param url      下载文件地址
     * @param callBack 下载文件回调接口
     */
    public Callback.Cancelable download(String url, String saveFilePath, final XDownLoadCallBack callBack) {
        RequestParams params = new RequestParams(url);
        params.setReadTimeout(READ_TIME_OUT);
        params.setConnectTimeout(CONNECT_TIME_OUT);
        params.setAutoRename(true);// 断点续传
        params.setSaveFilePath(saveFilePath + System.currentTimeMillis() + ".mp4");
        return x.http().get(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onSuccess(final File result) {
//                Log.i("XUtils","onSuccess");
                handler.post(() -> {
                    if (callBack != null) {
                        callBack.onSuccess(result);
                    }
                });
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
//                Log.i("XUtils","onError::" + ex.getMessage());
                handler.post(() -> {
                    if (callBack != null) {
                        callBack.onError(ex);
                    }
                });
            }

            @Override
            public void onCancelled(CancelledException cex) {
//                Log.i("XUtils","onCancelled");
            }

            @Override
            public void onFinished() {
//                Log.i("XUtils","onFinished");
            }

            @Override
            public void onWaiting() {
//                Log.i("XUtils","onWaiting");
            }

            @Override
            public void onStarted() {
//                Log.i("XUtils","onStarted");
            }

            @Override
            public void onLoading(final long total, final long current, final boolean isDownloading) {
                handler.post(() -> {
                    if (callBack != null) {
                        callBack.onLoading(total, current, isDownloading);
                    }
                });
            }
        });
    }


    /**
     * 上传文件
     *
     * @param url      上传地址
     * @param file     需上传的文件
     * @param callback 回调接口
     */
    public void postFile(String url, File file, final XCallBack callback) {
        RequestParams params = new RequestParams(url);
        //使用multipart表单上传文件
        params.setMultipart(true);
        // 如果文件没有扩展名, 最好设置contentType参数.
        params.addBodyParameter("file", file, "Content-Type: multipart/form-data;");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                handler.post(() -> {
                    if (callback != null) {
                        callback.onResponse(result);
                    }
                });
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                handler.post(() -> {
                    if (callback != null) {
                        callback.onError(ex);
                    }
                });
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 文件下载 get 请求
     *
     * @param url      下载文件地址
     * @param callBack 下载文件回调接口
     */
    public void getDownload(String url, String saveFilePath, final XDownLoadCallBack callBack) {
        RequestParams params = new RequestParams(url);
//        params.setAutoRename(true);// 断点续传
        params.setSaveFilePath(saveFilePath);
        x.http().get(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onSuccess(final File result) {
                handler.post(() -> {
                    if (callBack != null) {
                        callBack.onSuccess(result);
                    }
                });
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                handler.post(() -> {
                    if (callBack != null) {
                        callBack.onError(ex);
                    }
                });
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {
                handler.post(() -> {
                    if (callBack != null) {
                        callBack.onStart();
                    }
                });
            }

            @Override
            public void onLoading(final long total, final long current, final boolean isDownloading) {
                handler.post(() -> {
                    if (callBack != null) {
                        callBack.onLoading(total, current, isDownloading);
                    }
                });
            }
        });

    }

    /**
     * 异步post请求
     * 请求格式为文件
     *
     * @param url      请求的url
     * @param filepath 上传文件
     * @param callback 回调接口
     */
    public void postFile(String url, String filepath, final XCallBack callback) {
        RequestParams params = new RequestParams(url);
        //使用multipart表单上传文件
        params.setMultipart(true);
        // 如果文件没有扩展名, 最好设置contentType参数.
        params.addBodyParameter("file", new File(filepath), "Content-Type: multipart/form-data;");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                handler.post(() -> {
                    if (callback != null) {
                        callback.onResponse(result);
                    }
                });
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                handler.post(() -> {
                    if (callback != null) {
                        callback.onError(ex);
                    }
                });
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }


}
