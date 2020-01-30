package com.zhlee.doplayer.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * 文件操作工具类
 * Created by lWX410104 on 2016/10/25.
 */
public class FileUtils {

    /**
     * 保存String到文件
     *
     * @param launcherPath 保存文件路径
     * @param str          文件内容
     */
    public static void saveString2File(String launcherPath, String str) {
        File launcherFile = new File(launcherPath);
        if (!launcherFile.getParentFile().exists()) {
            launcherFile.getParentFile().mkdirs();
        }
        try {
            // 第二个参数为true则追加 false则覆盖 不写默认false
            FileOutputStream fos = new FileOutputStream(launcherFile, false);
            fos.write(str.getBytes());
            fos.write("\n".getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取文件中内容转成String
     *
     * @param launcherPath 文件路径
     * @return json str
     */
    static String readFile2String(String launcherPath) {
        StringBuilder fileData = new StringBuilder();
        File launcherFile = new File(launcherPath);
//        LogUtil.log("文件是否存在::" + launcherFile.exists());
        if (launcherFile.exists()) {
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(launcherPath));
                char[] buf = new char[1024];
                int numRead;
                while ((numRead = reader.read(buf)) != -1) {
                    String readData = String.valueOf(buf, 0, numRead);
                    fileData.append(readData);
                }
                //缓冲区使用完必须关掉
                reader.close();
                return fileData.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 递归删除文件
     *
     * @param file 要删除的文件或文件夹
     */
    public static void deleteFile(File file) {
        //判断给定的是否是目录
        if (file.isDirectory()) {
            //得到这个目录下的所有文件和目录
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isFile()) {
                    //如果是文件删除
                    f.delete();
                } else {
                    //说明是个目录
                    //递归删除其所有子文件
                    deleteFile(f);
                }
            }
            //foreach结束后
            //目录中的所有文件已经全部删除，所以将目录删掉
//            file.delete();
        } else {
            file.delete();
        }
    }

    /**
     * 展示图片
     *
     * @param filepath 本地图片绝对路径
     * @param img      展示图片的ImageView
     */
    public static void showPic(ImageView img, String filepath) {
        File file = new File(filepath);
        if (file.exists()) {
            Bitmap bm = BitmapFactory.decodeFile(filepath);
            //将图片显示到ImageView中
            img.setImageBitmap(bm);
        }
    }
}
