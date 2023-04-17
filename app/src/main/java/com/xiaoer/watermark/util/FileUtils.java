package com.xiaoer.watermark.util;

import android.os.Environment;

import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import de.robv.android.xposed.XposedBridge;

public class FileUtils {

    private static File androidDirectory;
    private static File configDirectory;
    private static File configFile;
    public static File getAndroidDirectoryFile() {
        if (androidDirectory == null) {
            androidDirectory = new File(Environment.getExternalStorageDirectory(), "Android");
            if (!androidDirectory.exists()) {
                androidDirectory.mkdirs();
            }
        }
        return androidDirectory;
    }

    public static File getConfigDirectoryFile() {
        if (configDirectory == null) {
            configDirectory = new File(getAndroidDirectoryFile(), "data/com.xiaoer.watermark");
            if (configDirectory.exists()) {
                if (configDirectory.isFile()) {
                    configDirectory.delete();
                    configDirectory.mkdirs();
                }
            } else {
                configDirectory.mkdirs();
            }
        }
        return configDirectory;
    }

    public static File getConfigFile() {
        if (configFile == null) {
            configFile = new File(getConfigDirectoryFile(), "waterMarkConfig.json");
            if (configFile.exists() && configFile.isDirectory())
                configFile.delete();
        }
        return configFile;
    }

    public static boolean write2File(String s, File f) {
        boolean success = false;
        FileWriter fw = null;
        try {
            fw = new FileWriter(f);
            fw.write(s);
            fw.flush();
            success = true;
        } catch (Throwable t) {
            XposedBridge.log(t.getMessage());
        }
        close(fw, f);
        return success;
    }

    public static String readFromFile(File f) {
        StringBuilder result = new StringBuilder();
        FileReader fr = null;
        try {
            fr = new FileReader(f);
            char[] chs = new char[1024];
            int len = 0;
            while ((len = fr.read(chs)) >= 0) {
                result.append(chs, 0, len);
            }
        } catch (Throwable t) {
            XposedBridge.log(t.getMessage());
        }
        close(fr, f);
        return result.toString();
    }

    public static void close(Closeable c, File f) {
        try {
            if (c != null)
                c.close();
        } catch (Throwable t) {
            XposedBridge.log(t.getMessage());
        }
    }
}
