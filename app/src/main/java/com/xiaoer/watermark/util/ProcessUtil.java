package com.xiaoer.watermark.util;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.xiaoer.watermark.hook.AddWaterMark;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class ProcessUtil {
    private static final String TAG = "ProcessUtil";

    public static boolean isMainProcess() {
        return isProcess(getPackageName());
    }

    public static boolean isPatchProcess() {
        return isProcess(getPackageName() + ":patch");
    }

    public static boolean isSafeModeProcess() {
        return isProcess(getPackageName() + ":safeMode");
    }

    public static boolean isErrorProcess() {
        return isProcess(getPackageName() + ":error");
    }

    public static boolean isWatchDogProcess() {
        return isProcess(getPackageName() + ":WatchDogService");
    }

    public static boolean isPushProcess() {
        return isProcess(getPackageName() + ":jdpush") || isProcess(getPackageName() + ":pushservice");
    }

    /********
     * 用于application加载优化
     *
     * @param pid 当前Application进程PID
     * @return String 返回进程名称
     *****/
    public static String getProcessName(int pid) {
        InputStreamReader reader = null;
        BufferedReader br = null;
        try {
            reader = new InputStreamReader(new FileInputStream("/proc/" + pid + "/cmdline"));
            br = new BufferedReader(reader);
            char[] data = new char[64];//定义数组  进程名字最长64
            br.read(data);
            int len = 0;
            for (char c : data) {//因为cmdline文件不再文件系统，如果直接readline截取的数据过多
                if (c == 0) {
                    break;
                }
                len++;
            }
            return new String(data, 0, len);//daString.startsWith("com.jingdong.app.mall:");
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }

        }
        return "";

    }

    /**
     * 应用是否处于前台
     *
     * @return
     */
    public static boolean isForeground() {

        ActivityManager activityManager = (ActivityManager) AddWaterMark.getApplication().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(AddWaterMark.getApplication().getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {//IMPORTANCE_BACKGROUND
                    return true;
                } else {
                    return false;
                }

            }
        }
        return false;
    }

    public static boolean isProcess(String pName){
        if(TextUtils.isEmpty(pName))
            return false;
        String processName=null;
        try {
            processName = getProcessName(AddWaterMark.getApplication());
            if (processName == null || processName.length() == 0) {
                processName = "";
            }
        }catch (Throwable e){}
        if(TextUtils.isEmpty(processName)){
            processName = getProcessName(android.os.Process.myPid()).trim();
        }
        return pName.equals(processName);
    }

    private static String processName = null;

    /**
     * add process name cache
     *
     * @param context
     * @return
     */
    public static String getProcessName(final Context context) {
        if (processName != null) {
            return processName;
        }
        //will not null
        processName = getProcessNameInternal(context);
        return processName;
    }

    private static String getProcessNameInternal(final Context context) {
        String process = "";
        if(!TextUtils.isEmpty(process)){
            return process;
        }
        int myPid = android.os.Process.myPid();
        if (context == null || myPid <= 0) {
            return "";
        }
        byte[] b = new byte[128];
        try (FileInputStream in = new FileInputStream("/proc/" + myPid + "/cmdline")) {
            int len = in.read(b);
            if (len > 0) {
                for (int i = 0; i < len; i++) { // lots of '0' in tail , remove them
                    if (b[i] <= 0) {
                        len = i;
                        break;
                    }
                }
                return new String(b, 0, len);
            }

        } catch (Throwable e) {/**/}
        /**/

        return "";
    }

    private static String getPackageName(){
        return AddWaterMark.getApplication().getPackageName();
    }
}
