package com.zhlee.doplayer.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.danikula.videocache.HttpProxyCacheServer;
import com.zhlee.doplayer.R;
import com.zhlee.doplayer.base.DoApplication;
import com.zhlee.doplayer.bean.FavoriteBean;
import com.zhlee.doplayer.utils.Const;
import com.zhlee.doplayer.utils.FileUtils;
import com.zhlee.doplayer.utils.LogUtil;
import com.zhlee.doplayer.utils.ToastUtils;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.List;

import cn.jzvd.JzvdStd;


/**
 * 作者： ch
 * 时间： 2018/8/17 0017-下午 5:14
 * 描述：
 * 来源：
 */


public class DoVideoPlayer extends JzvdStd {
    public RelativeLayout rlTouchHelp;
    private ImageView ivStart;
    private LinearLayout llStart;
    private ImageView ivFavor;

    private Context ctx;

    // 当前视频所在列表中位置 默认-1
    private int currentPosition = -1;
    // 当前播放地址
    private String currentUrl;
    // 播放类型 0 本地播放 1 在线播放
    private int playType;
    // 是否是已收藏的 已收藏的不允许长按删除
    private boolean isFavor;


    public DoVideoPlayer(Context context) {
        super(context);
        this.ctx = context;
    }

    public DoVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.ctx = context;
    }

    @Override
    public void onAutoCompletion() {
        thumbImageView.setVisibility(View.GONE);
        if (screen == SCREEN_FULLSCREEN) {
            onStateAutoComplete();
            setUp((String) jzDataSource.getCurrentUrl(), jzDataSource.title, SCREEN_FULLSCREEN);
        } else {
            super.onAutoCompletion();
            setUp((String) jzDataSource.getCurrentUrl(), jzDataSource.title, SCREEN_NORMAL);
        }
        //循环播放
        startVideo();
    }

    /**
     * 设置当前视频在视频列表中的位置
     * @param currentPosition
     */
    public void setCurrentPosition(int currentPosition){
        this.currentPosition = currentPosition;
    }

    /**
     * 设置播放类型 0 本地播放 1 在线播放
     * @param playType
     */
    public void setPlayType(int playType) {
        this.playType = playType;
    }

    @Override
    public void setUp(String url, String title, int screen) {
        super.setUp(url, title, screen);
        if (url.startsWith("http")) {
            //
            HttpProxyCacheServer proxy = DoApplication.getProxy(ctx);
            String proxyUrl = proxy.getProxyUrl(url);
            // 播放URL为HTTP开头的认为是在线播放 隐藏收藏按钮
            ivFavor.setVisibility(GONE);
            super.setUp(proxyUrl, title, screen);
        } else {
            currentUrl = url;
            // 初始化收藏图标状态
            initFavorIcon(currentUrl);
            super.setUp(url, title, screen);
        }

    }

    /**
     * 初始化收藏logo
     *
     * @param currentUrl
     */
    private void initFavorIcon(String currentUrl) {
        if (playType == Const.PLAY_TYPE_LOCAL) {
            // 判断当前视频地址是否已经存储到我的收藏中
            List<FavoriteBean> favoriteBeans = DataSupport.where("url = ?", currentUrl).find(FavoriteBean.class);
            if (favoriteBeans != null && favoriteBeans.size() > 0) {
                // 已经存在
                FavoriteBean favoriteBean = favoriteBeans.get(0);
                if (favoriteBean != null) {
                    ivFavor.setImageResource(R.mipmap.icon_favor_on);
                    // 标记当前已收藏
                    isFavor = true;
                } else {
                    ivFavor.setImageResource(R.mipmap.icon_favor_off);
                    isFavor = false;
                }
            } else {
                ivFavor.setImageResource(R.mipmap.icon_favor_off);
                isFavor = false;
            }
        } else if (playType == Const.PLAY_TYPE_ONLINE) {
            // 在线播放是隐藏收藏按钮
            ivFavor.setVisibility(GONE);
        }
    }

    @Override
    public void init(final Context context) {
        super.init(context);

        rlTouchHelp = findViewById(R.id.rl_touch_help);
        llStart = findViewById(R.id.ll_start);
        ivStart = findViewById(R.id.iv_start);
        ivFavor = findViewById(R.id.iv_favor);
        resetPlayView();

        rlTouchHelp.setOnClickListener(v -> {
            if (isPlay()) {
                goOnPlayOnPause();
            } else {
                //暂停
                if (state == STATE_PAUSE) {
                    goOnPlayOnResume();
                } else {
                    startVideo();
                }
            }
            resetPlayView();
        });

        llStart.setOnClickListener(v -> {
            if (isPlay()) {
                goOnPlayOnPause();
            } else {
                //暂停
                if (state == STATE_PAUSE) {
                    goOnPlayOnResume();
                } else {
                    startVideo();
                }
            }
            resetPlayView();
        });

        ivFavor.setOnClickListener(view -> {
            // 判断当前视频地址是否已经存储到我的收藏中
            List<FavoriteBean> favoriteBeans = DataSupport.where("url = ?", currentUrl).find(FavoriteBean.class);
            if (favoriteBeans != null && favoriteBeans.size() > 0) {
                LogUtil.log("已经存在::" + favoriteBeans);
                // 已经存在 则移除收藏
                FavoriteBean favoriteBean = favoriteBeans.get(0);
                if (favoriteBean != null) {
                    favoriteBean.delete();
                    ivFavor.setImageResource(R.mipmap.icon_favor_off);
                    isFavor = false;
                }
            } else {
                // 否则加入收藏
                FavoriteBean fb = new FavoriteBean(currentUrl);
                boolean save = fb.save();
                if (save) {
                    ivFavor.setImageResource(R.mipmap.icon_favor_on);
                    isFavor = true;
                }
            }
        });

        if (playType == Const.PLAY_TYPE_LOCAL) {
            // 本地播放时 长按删除 只删除文件
            rlTouchHelp.setOnLongClickListener(view -> {
                LogUtil.log("currentUrl::" + currentUrl);
                if (isFavor) {
                    LogUtil.log("已收藏视频不允许删除！");
                    ToastUtils.showToast(ctx,"已收藏视频不允许删除！");
                } else {
                    FileUtils.deleteFile(new File(currentUrl));
                    // 删除成功 发送广播
                    Intent intent = new Intent(Const.ACTION_DELETE_SINGLE_VIDEO);
                    intent.putExtra(Const.VIDEO_DELETE_POSITON_KEY,currentPosition);
                    ctx.sendBroadcast(intent);
                }
                return true;
            });
        }
    }

    @Override
    public void startVideo() {
        if (screen == SCREEN_FULLSCREEN) {
            startFullscreenDirectly(ctx, DoVideoPlayer.class, jzDataSource);
            onStatePreparing();
            llStart.setVisibility(VISIBLE);
        } else {
            super.startVideo();
            llStart.setVisibility(GONE);
        }
        resetPlayView();
    }


    private void resetPlayView() {
        if (isPlay()) {
            ivStart.setBackgroundResource(R.mipmap.video_play_parse);
        } else {
            ivStart.setBackgroundResource(R.mipmap.stop);
        }
    }

    /**
     * 是否播放
     *
     * @return
     */
    private boolean isPlay() {
        if (state == STATE_PREPARING || state == STATE_PLAYING || state == -1) {
            return true;
        }

        return false;
    }

}
