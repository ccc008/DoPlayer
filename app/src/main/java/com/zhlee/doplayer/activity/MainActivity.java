package com.zhlee.doplayer.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zhlee.doplayer.R;
import com.zhlee.doplayer.bean.VideoBean;
import com.zhlee.doplayer.utils.FileUtils;
import com.zhlee.doplayer.utils.LogUtil;
import com.zhlee.doplayer.utils.StringUtils;
import com.zhlee.doplayer.utils.ThreadUtil;
import com.zhlee.doplayer.utils.ToastUtils;
import com.zhlee.doplayer.utils.XCallBack;
import com.zhlee.doplayer.utils.XDownLoadCallBack;
import com.zhlee.doplayer.utils.XUtils;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.btn_start)
    Button btnStart;
    @BindView(R.id.btn_stop)
    Button btnStop;
    @BindView(R.id.btn_clear_db)
    Button btnClearDb;
    @BindView(R.id.btn_clear_all)
    Button btnClearAll;
    @BindView(R.id.btn_open_local)
    Button btnOpenLocal;
    @BindView(R.id.btn_open_online)
    Button btnOpenOnline;

    // 要申请的权限
    private String[] permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //权限数组下标
    //权限申请返回码
    private int requestCodePre = 321;
    //系统设置权限申请返回码
    private int requestCodeSer = 123;

    // 请求地址
    public static final String DO_URL = "https://iapp.ddccs.net/api.php";
    // 视频存储地址
    public static final String DOWNLOAD_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Video/";


    private MainActivity act;
    private XUtils xUtils;

    // 标记下载状态及是否继续下载
    private boolean isDownloading = false;
    private ProgressDialog pd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 初始化前的一些准备
        initFirst();
        // 检查权限
        checkPermissions();
        // 初始化数据
        initData();
        // 注册监听
        initListener();
    }

    private void initListener() {
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnOpenLocal.setOnClickListener(this);
        btnClearDb.setOnClickListener(this);
        btnClearAll.setOnClickListener(this);
        btnOpenOnline.setOnClickListener(this);
    }

    private void initData() {

    }

    private void initFirst() {
        act = this;
        ButterKnife.bind(this);
        xUtils = XUtils.getInstance();
        pd = new ProgressDialog(act);
    }

    private void getVideoUrl() {
        if (isDownloading) {
            xUtils.get(DO_URL, null, null, new XCallBack() {
                @Override
                public void onResponse(String result) {
                    // 解析结果 获取视频地址
                    String url = StringUtils.parseUrl(result);
                    if (!TextUtils.isEmpty(url)) {
                        // 判断当前视频地址是否已经存储到数据库中
                        List<VideoBean> videoBeans = DataSupport.where("url = ?", url).find(VideoBean.class);
                        if (videoBeans != null && videoBeans.size() > 0) {
                            LogUtil.log("已经存在::" + videoBeans);
                            // 已经存在 则重新获取下载地址
                            getVideoUrl();
                        } else {
                            downloadVideo(url);
                        }
                    } else {
                        LogUtil.log("获取的地址为空!");
                        // 获取到的地址为空 重新获取
                        getVideoUrl();
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    LogUtil.log("获取地址失败::" + throwable.getMessage());
                }
            });
        } else {
            LogUtil.log("已取消下载!");
        }
    }

    private void downloadVideo(String url) {
        xUtils.download(url, DOWNLOAD_DIR, new XDownLoadCallBack() {
            @Override
            public void onSuccess(File result) {
                LogUtil.log("视频下载完成!");

                // 存储到数据库
                VideoBean videoBean = new VideoBean();
                videoBean.setUrl(url);
                videoBean.setName(result.getName());
                videoBean.save();

                // 下载完成 继续下载下一个
                getVideoUrl();
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {

            }

            @Override
            public void onError(Throwable throwable) {
                LogUtil.log("视频下载失败::" + throwable.getMessage());
                // 下载失败 继续下载下一个
                getVideoUrl();
            }

            @Override
            public void onStart() {

            }
        });
    }


    /**
     * 检查运行时权限
     */
    private void checkPermissions() {
        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean permissionFlags = false;
            for (String permissionStr : permissions) {
                // 检查该权限是否已经获取
                int per = ContextCompat.checkSelfPermission(act, permissionStr);
                // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                if (per != PackageManager.PERMISSION_GRANTED) {
                    permissionFlags = true;
                }
            }
            if (permissionFlags) {
                // 如果有权限没有授予允许，就去提示用户请求授权
                ActivityCompat.requestPermissions(act, permissions, requestCodePre);
            }
        }
    }


    /**
     * 用户权限申请的回调方法grantResults授权结果
     *
     * @param requestCode  是我们自己定义的权限请求码
     * @param permissions  是我们请求的权限名称数组
     * @param grantResults 是我们在弹出页面后是否允许权限的标识数组，数组的长度对应的是权限名称数
     *                     组的长度，数组的数据PERMISSION_GRANTED表示允许权限，PERMISSION_DENIED表示我们点击了禁止权限
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == requestCodePre) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 判断该权限是否已经授权
                boolean grantFlas = false;
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        //-----------存在未授权-----------
                        grantFlas = true;
                    }
                }
                if (grantFlas) {
                    //-----------未授权-----------
                    // 判断用户是否点击了不再提醒。(检测该权限是否还可以申请)
                    // shouldShowRequestPermissionRationale合理的解释应该是：如果应用之前请求过此权限
                    //但用户拒绝了请求且未勾选"Don’t ask again"(不在询问)选项，此方法将返回 true。
                    //注：如果用户在过去拒绝了权限请求，并在权限请求系统对话框中勾选了
                    //"Don’t ask again" 选项，此方法将返回 false。如果设备规范禁止应用具有该权限，此方法会返回 false。
                    boolean shouldShowRequestFlas = false;
                    for (String per : permissions) {
                        if (shouldShowRequestPermissionRationale(per)) {
                            //-----------存在未授权-----------
                            shouldShowRequestFlas = true;
                        }
                    }
                    if (shouldShowRequestFlas) {
                        // 用户还是想用我的 APP 的
                        // 提示用户去应用设置界面手动开启权限
                        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert);
                        builder.setTitle("提示");
                        builder.setMessage("当前还有必要权限没有授权，是否前往授权？");
                        builder.setCancelable(false);
                        builder.setPositiveButton("前往", (dialog, which) -> {
                            dialog.dismiss();
                            goToAppSetting();
                        });
                        builder.setNegativeButton("放弃", (dialog, which) -> dialog.dismiss());
                        builder.show();
                    }
                } else {
                    //-----------授权成功-----------
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // 跳转到当前应用的设置界面
    private void goToAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, requestCodeSer);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                if (!isDownloading) {
                    LogUtil.log("启动下载...");
                    isDownloading = true;
                    // 获取视频地址
                    getVideoUrl();
                } else {
                    LogUtil.log("下载中...");
                }
                break;
            case R.id.btn_stop:
                isDownloading = false;
                LogUtil.log("取消下载,,,");
                break;
            case R.id.btn_clear_db:
                LogUtil.log("清空数据库...");
                showClearDbDialog();
                break;
            case R.id.btn_clear_all:
                LogUtil.log("清空数据库及文件...");
                showClearAllDialog();
                break;
            case R.id.btn_open_local:
                LogUtil.log("启动本地播放...");
                startActivity(new Intent(act, LocalPlayActivity.class));
                break;
            case R.id.btn_open_online:
                LogUtil.log("启动在线播放...");
                ToastUtils.showToast(act,"暂未开放!");
                break;
        }
    }

    private void showClearDbDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        builder.setTitle("警告");
        builder.setMessage("你确定要清空数据库么？");
        builder.setPositiveButton("确定", (dialogInterface, i) -> {
            DataSupport.deleteAll(VideoBean.class);
            LogUtil.log("已清空数据库!");
            ToastUtils.showToast(act, "已清空数据库!");
        });
        builder.setNegativeButton("取消", (dialogInterface, i) -> {

        });
        builder.show();
    }

    private void showClearAllDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        builder.setTitle("警告");
        builder.setMessage("你确定要清空数据库及文件么？");
        builder.setPositiveButton("确定", (dialogInterface, i) -> {
            // 开始清空文件 显示进度框
            showProgressDialog("标题", "正在清空数据...");
            ThreadUtil.runOnBackThread(() -> {
                DataSupport.deleteAll(VideoBean.class);
                LogUtil.log("已清空数据库!");
                FileUtils.deleteFile(new File(DOWNLOAD_DIR));
                LogUtil.log("已清空文件!");
                ThreadUtil.runOnUIThread(() -> {
                    // 清空文件成功 隐藏进度框
                    dismissProgressDialog();
                    ToastUtils.showToast(act, "已清空数据库及文件!");
                });
            });
        });
        builder.setNegativeButton("取消", (dialogInterface, i) -> {

        });
        builder.show();
    }

    private void showProgressDialog(String title, String content) {
        pd.setTitle(title);
        pd.setMessage(content);
        pd.setCancelable(false);
        pd.show();
    }

    private void dismissProgressDialog() {
        if (pd != null && pd.isShowing() && act != null) {
            pd.dismiss();
        }
    }

}
