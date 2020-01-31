package com.zhlee.doplayer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhlee.doplayer.R;
import com.zhlee.doplayer.base.BaseRecAdapter;
import com.zhlee.doplayer.base.BaseRecViewHolder;
import com.zhlee.doplayer.utils.Const;
import com.zhlee.doplayer.utils.DoX509TrustManager;
import com.zhlee.doplayer.utils.LogUtil;
import com.zhlee.doplayer.utils.StringUtils;
import com.zhlee.doplayer.utils.XCallBack;
import com.zhlee.doplayer.utils.XUtils;
import com.zhlee.doplayer.view.DoVideoPlayer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OnlinePlayActivity extends AppCompatActivity {

    @BindView(R.id.rv_video)
    RecyclerView rvVideo;
    private List<String> urlList;
    private ListVideoAdapter videoAdapter;
    private PagerSnapHelper snapHelper;
    private LinearLayoutManager layoutManager;
    private int currentPosition;
    private OnlinePlayActivity act;
    private XUtils xUtils;

    public static final int MIN_COUNT = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏顶部状态栏
        initWindowTab();
        setContentView(R.layout.activity_video);
        // 初始化前的一些准备
        initFirst();
        // 初始化数据
        initData();
        // 初始化控件
        initView();
        // 注册监听
        initListener();
    }

    private void initWindowTab() {
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void initFirst() {
        act = this;
        ButterKnife.bind(this);
        xUtils = XUtils.getInstance();
    }

    private void initView() {
        urlList = new ArrayList<>();
        // 开始缓存视频地址 每次10条
        startCache();

        snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rvVideo);

        videoAdapter = new ListVideoAdapter(urlList);
        layoutManager = new LinearLayoutManager(act, LinearLayoutManager.VERTICAL, false);
        rvVideo.setLayoutManager(layoutManager);
        rvVideo.setAdapter(videoAdapter);
    }

    private void startCache() {
        LogUtil.log("开始缓存...");
        for (int i = 0; i < MIN_COUNT; i++) {
            xUtils.get(Const.DO_URL, null, null, new XCallBack() {
                @Override
                public void onResponse(String result) {
                    // 解析结果 获取视频地址
                    String url = StringUtils.parseUrl(result);
                    if (!TextUtils.isEmpty(url)) {
                        xUtils.head(url, null, null, new XCallBack() {
                            @Override
                            public void onResponse(String result) {
                                LogUtil.log("文件地址可用::" + result);
                                urlList.add(url);
                                videoAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                LogUtil.log("文件地址不可用::" + url);
                                LogUtil.log("文件地址不可用异常::" + throwable.getMessage());
                            }
                        });
                    } else {
                        LogUtil.log("获取的地址为空!");
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    LogUtil.log("获取地址失败::" + throwable.getMessage());
                }
            });
        }
    }

    /**
     * 判断文件是否存在
     *
     * @param httpPath
     * @return
     */
    private static Boolean isActive(String httpPath) {
        boolean isActive = false;
        try {
            SSLContext sslcontext = SSLContext.getInstance("SSL");//第一个参数为协议,第二个参数为提供者(可以缺省)
            TrustManager[] tm = {new DoX509TrustManager()};
            sslcontext.init(null, tm, new SecureRandom());
            HostnameVerifier ignoreHostnameVerifier = (s, sslSession) -> {
                LogUtil.log("WARNING: Hostname is not matched for cert.");
                return true;
            };
            HttpsURLConnection.setDefaultHostnameVerifier(ignoreHostnameVerifier);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());
            URL url = new URL(httpPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn.getResponseCode() >= 400) {
                LogUtil.log("文件不存在");
                isActive = false;
            } else {
                LogUtil.log("文件存在");
                isActive = true;
            }
            conn.disconnect();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            LogUtil.log("1::" + e);
        } catch (KeyManagementException e) {
            e.printStackTrace();
            LogUtil.log("2::" + e);
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.log("3::" + e);
        }
        return isActive;
    }


    private void initData() {

    }

    private void initListener() {
        rvVideo.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE://停止滚动
                        View view = snapHelper.findSnapView(layoutManager);
                        //当前固定后的item position
                        int position = recyclerView.getChildAdapterPosition(view);
                        if (currentPosition != position) {
                            //如果当前position 和 上一次固定后的position 相同, 说明是同一个, 只不过滑动了一点点, 然后又释放了
                            DoVideoPlayer.releaseAllVideos();
                            RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
                            if (viewHolder instanceof OnlinePlayActivity.VideoViewHolder) {
                                ((OnlinePlayActivity.VideoViewHolder) viewHolder).mp_video.startVideo();
                            }
                        }
                        currentPosition = position;

                        if (urlList.size() - currentPosition < MIN_COUNT) {
                            startCache();
                        }
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING://拖动
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING://惯性滑动
                        break;
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        DoVideoPlayer.releaseAllVideos();
    }


    class ListVideoAdapter extends BaseRecAdapter<String, OnlinePlayActivity.VideoViewHolder> {
        public ListVideoAdapter(List<String> list) {
            super(list);
        }

        @Override
        public void onHolder(OnlinePlayActivity.VideoViewHolder holder, String bean, int position) {
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;

            holder.mp_video.setUp(bean, "第" + position + "个视频", DoVideoPlayer.STATE_NORMAL);
            if (position == 0) {
                holder.mp_video.startVideo();
            }

            Glide.with(context).load(bean).into(holder.mp_video.thumbImageView);
            holder.tv_title.setText("第" + position + "个视频");
        }

        @Override
        public OnlinePlayActivity.VideoViewHolder onCreateHolder() {
            return new VideoViewHolder(getViewByRes(R.layout.item_video));
        }
    }

    public class VideoViewHolder extends BaseRecViewHolder {
        public View rootView;
        public DoVideoPlayer mp_video;
        public TextView tv_title;

        public VideoViewHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            this.mp_video = rootView.findViewById(R.id.mp_video);
            this.tv_title = rootView.findViewById(R.id.tv_title);
        }
    }
}
