package com.zhlee.doplayer.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CrashHandler implements UncaughtExceptionHandler {
    private static final String TAG = "TVBox";
    private static CrashHandler mInstance = new CrashHandler();
    private Context mContext;
    private Map<String, String> mLogInfo = new HashMap<>();
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyyMMdd_HH-mm-ss");

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return mInstance;
    }

    public void init(Context paramContext) {
        this.mContext = paramContext;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 捕获异常
     *
     * @param paramThread
     * @param paramThrowable
     */
    public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
        // 处理异常
        handleException(paramThrowable);
        LogUtil.log("系统崩溃::" + paramThrowable);
        // 杀死进程
        android.os.Process.killProcess(android.os.Process.myPid());
        //不为零表示非正常退出
        System.exit(1);
    }

    /**
     * 如果有异常 则返回true 如果没异常 则返回false
     *
     * @param paramThrowable
     * @return
     */
    private void handleException(final Throwable paramThrowable) {
        if (paramThrowable != null) {
            CrashHandler.this.getDeviceInfo();
            CrashHandler.this.saveCrashLogToFile(paramThrowable);
        } else {
            LogUtil.log("Launcher崩溃，但崩溃信息为null!");
        }
    }

    /**
     * 保存异常到本地log 同时发送邮件到指定的邮箱
     *
     * @param ex
     * @return
     */

    private String saveCrashLogToFile(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        long timestamp = System.currentTimeMillis();
        String time = mSimpleDateFormat.format(new Date());
        String logBegin = "crash---" + time + "---" + timestamp + "---";
        String logEnd = "---crash---end---";
        sb.append(logBegin).append("\n");
        for (Map.Entry<String, String> entry : mLogInfo.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append("=").append(value).append("\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        sb.append(logEnd).append("\n");
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TVBox/crash/";
                String logFileName = "crash-log" + ".txt";
                File dir = new File(dirPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(dirPath + logFileName, true);
                fos.write(sb.toString().getBytes());
                fos.close();
            }
            return time;
        } catch (Exception e) {
            Log.e(TAG, "保存日志时出现错误", e);
        }
        return null;
    }

    /**
     * 方法 获取设备信息
     */
    private void getDeviceInfo() {
        try {
            // 崩溃时间
            mLogInfo.put("crashTime", getTimeSec());
            LogUtil.log("mLogInfo::" + mLogInfo);
        } catch (Exception e) {
            LogUtil.log("存储mLogInfo时异常!!!");
            e.printStackTrace();
        }
    }

    /**
     * 获取当前时间 精确到秒
     *
     * @return
     */
    public static String getTimeSec() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        return sdf.format(date);
    }

}
