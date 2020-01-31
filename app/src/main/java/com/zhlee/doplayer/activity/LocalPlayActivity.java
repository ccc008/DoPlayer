package com.zhlee.doplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
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
import com.zhlee.doplayer.utils.ScanUtil;
import com.zhlee.doplayer.view.DoVideoPlayer;

import java.io.File;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocalPlayActivity extends AppCompatActivity {

    @BindView(R.id.rv_video)
    RecyclerView rvVideo;
    private List<String> urlList;
    private ListVideoAdapter videoAdapter;
    private PagerSnapHelper snapHelper;
    private LinearLayoutManager layoutManager;
    private int currentPosition;
    private LocalPlayActivity act;
    // 播放顺序
    private String playKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏顶部状态栏
        initWindowTab();
        setContentView(R.layout.activity_video);
        // 初始化前的一些准备
        initFirst();
        // 初始化Intent
        initIntent();
        // 初始化数据
        initData();
        // 初始化控件
        initView();
        // 注册监听
        initListener();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initIntent();
    }

    private void initWindowTab() {
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void initFirst() {
        act = this;
        ButterKnife.bind(this);
    }

    private void initIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            playKey = intent.getStringExtra(Const.PLAY_ORDER_KEY);
        }
    }

    private void initView() {
        File file = new File(Const.DOWNLOAD_DIR);
        urlList = ScanUtil.getVideoList(file);

        if (Const.PLAY_ORDER_ASC.equalsIgnoreCase(playKey)) {
            // 正序播放
            Collections.sort(urlList);
        } else if (Const.PLAY_ORDER_DESC.equalsIgnoreCase(playKey)) {
            // 倒序播放
            Collections.sort(urlList);
            Collections.reverse(urlList);
        } else if (Const.PLAY_ORDER_SHUFFLE.equalsIgnoreCase(playKey)) {
            // 随机播放
            Collections.shuffle(urlList);
        } else {
            // 默认倒序播放
            Collections.sort(urlList);
            Collections.reverse(urlList);
        }

        snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rvVideo);

        videoAdapter = new ListVideoAdapter(urlList);
        layoutManager = new LinearLayoutManager(act, LinearLayoutManager.VERTICAL, false);
        rvVideo.setLayoutManager(layoutManager);
        rvVideo.setAdapter(videoAdapter);
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
                            if (viewHolder instanceof VideoViewHolder) {
                                ((VideoViewHolder) viewHolder).mp_video.startVideo();
                            }
                        }
                        currentPosition = position;
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


    class ListVideoAdapter extends BaseRecAdapter<String, VideoViewHolder> {
        public ListVideoAdapter(List<String> list) {
            super(list);
        }

        @Override
        public void onHolder(VideoViewHolder holder, String bean, int position) {
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
        public VideoViewHolder onCreateHolder() {
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
