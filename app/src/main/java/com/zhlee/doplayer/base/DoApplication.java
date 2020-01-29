package com.zhlee.doplayer.base;

import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;
import com.zhlee.doplayer.view.DoFileNameGenerator;

import org.litepal.LitePalApplication;
import org.xutils.x;

/**
 * 作者： ch
 * 时间： 2018/10/12 0012-上午 11:34
 * 描述：
 * 来源：
 */

public class DoApplication extends LitePalApplication {


    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        DoApplication app = (DoApplication) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .maxCacheSize(1024 * 1024 * 1024)       // 1 Gb for cache
                .fileNameGenerator(new DoFileNameGenerator())
                .build();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化xUtils3.0
        x.Ext.init(this);
    }
}
