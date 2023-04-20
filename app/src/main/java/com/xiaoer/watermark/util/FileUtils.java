package com.xiaoer.watermark.util;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.github.kyuubiran.ezxhelper.HookFactory;
import com.github.kyuubiran.ezxhelper.finders.MethodFinder;
import com.xiaoer.watermark.BuildConfig;

import org.lsposed.hiddenapibypass.HiddenApiBypass;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class FileUtils {
    private static volatile FileUtils instance;
    private static final String SCHEME = "xiaoer";
    private static final String KEY_ACTION = "action";
    private static final String KEY_FILE_NAME = "fileName";
    private static final String KEY_FILE_DATA = "fileData";
    public static final String ROOT_PATH = "/data/system/";

    public static final int HOOK_TAG = 1248774172;

    public static final String ACTION_READ_FILE = "readFile";
    public static final String ACTION_WRITE_FILE = "writeFile";

    public static final String ACTION_DELETE_FILE = "deleteFile";

    private String dataDir;

    private FileUtils() {
    }

    public static FileUtils getInstance() {
        if (instance == null) {
            synchronized (FileUtils.class) {
                if (instance == null) {
                    instance = new FileUtils();
                }
            }
        }
        return instance;
    }

    public void initZygote() {
        String name = BuildConfig.APPLICATION_ID.replaceAll("\\.", "_");
        dataDir = ROOT_PATH + name;
        logD("setDataPath: " + dataDir);
    }

    @SuppressLint("PrivateApi")
    public void hookAMS(XC_LoadPackage.LoadPackageParam lpparam) throws ClassNotFoundException {
        Class<?> aClass = lpparam.classLoader.loadClass("com.android.server.am.ActivityManagerService");
        MethodFinder methodFinder = MethodFinder.fromClass(aClass);
        Method method = methodFinder.filterByName("setProcessMemoryTrimLevel").first();
        HookFactory.createMethodHook(method, hookFactory -> hookFactory.before(param -> {
            String url = (String) param.args[0];
            int tag = (int) param.args[1];
            if (tag == HOOK_TAG) {
                if(TextUtils.isEmpty(url)){
                    logD("setProcessMemoryTrimLevel: url is empty");
                    return;
                }
                logD("handleUrl:" + url);
                Uri data = Uri.parse(url);
                String schema = data.getScheme();
                if (!TextUtils.equals(SCHEME, schema.toLowerCase(Locale.ROOT))){
                    logD("scheme illegal");
                    return;
                }
                String action = data.getQueryParameter(KEY_ACTION);
                String fileName = data.getQueryParameter(KEY_FILE_NAME);

                if(TextUtils.equals(action, ACTION_READ_FILE)){
                    String fileData = readFileImpl(fileName);
                    param.setThrowable(new IllegalArgumentException(fileData));

                }else if(TextUtils.equals(action, ACTION_WRITE_FILE)){
                    String fileData = data.getQueryParameter(KEY_FILE_DATA);
                    param.setResult(saveFileImpl(fileName, fileData));

                } else if(TextUtils.equals(action, ACTION_DELETE_FILE)){
                    param.setResult(deleteFileImpl(fileName));
                }
            } else {
                logD("setProcessMemoryTrimLevel: is not our care url");
            }
        }));
    }

    public boolean saveFile(Context context, String fileName, String fileData){
        Uri.Builder builder = Uri.parse(SCHEME + "://hook").buildUpon();
        builder.appendQueryParameter(KEY_ACTION, ACTION_WRITE_FILE);
        builder.appendQueryParameter(KEY_FILE_NAME, fileName);
        builder.appendQueryParameter(KEY_FILE_DATA, fileData);
        return (boolean) mySetProcessMemoryTrimLevel(context, builder.toString());
    }

    public String readFile(Context context, String fileName){
        Uri.Builder builder = Uri.parse(SCHEME + "://hook").buildUpon();
        builder.appendQueryParameter(KEY_ACTION, ACTION_READ_FILE);
        builder.appendQueryParameter(KEY_FILE_NAME, fileName);
        return (String) mySetProcessMemoryTrimLevel(context, builder.toString());
    }

    public boolean deleteFile(Context context, String fileName){
        Uri.Builder builder = Uri.parse(SCHEME + "://hook").buildUpon();
        builder.appendQueryParameter(KEY_ACTION, ACTION_DELETE_FILE);
        builder.appendQueryParameter(KEY_FILE_NAME, fileName);
        return (boolean) mySetProcessMemoryTrimLevel(context, builder.toString());
    }

    private Object mySetProcessMemoryTrimLevel(Context context, String url){
        String result = "";
        if(context == null){
            return result;
        }
        try {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            return HiddenApiBypass.invoke(activityManager.getClass(), activityManager, "setProcessMemoryTrimLevel", url, HOOK_TAG, 1);
        }catch (Exception e){
            result = e.getCause() instanceof IllegalArgumentException ? e.getCause().getMessage() : "";
        }
        return TextUtils.isEmpty(result) ? false : result;
    }

    private boolean saveFileImpl(String fileName, String content) {
        File jsonFile = getFile(fileName);
        if (!jsonFile.exists()) {
            File jsonFileDirectory = new File(dataDir + "/");
            jsonFileDirectory.mkdirs();
        }
        logD("FileUtils saveFile: " + content);
        try {
            FileOutputStream outputStream = new FileOutputStream(jsonFile);
            outputStream.write(content.getBytes());
            outputStream.close();
            return true;
        } catch (IOException e) {
            logE("FileUtils saveFile: " + e.getCause());
            e.printStackTrace();
        }
        return false;
    }

    private boolean deleteFileImpl(String fileName){
        File jsonFile = getFile(fileName);
        if (jsonFile.exists()) {
            return jsonFile.delete();
        }
        return false;
    }

    private String readFileImpl(String fileName){
        File jsonFile = getFile(fileName);
        String result = "";
        try {
            if (jsonFile.exists()) {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int n;
                InputStream input = new FileInputStream(jsonFile);
                while ((n = input.read(buffer)) != -1) {
                    output.write(buffer, 0, n);
                }
                result = new String(output.toByteArray(), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            logE("FileUtils get "+ jsonFile.getName() + " err: " + e.getCause());
        }
        logD("FileUtils get "+ jsonFile.getName() + " success: " + result);
        return result;
    }

    private File getFile(String fileName){
        return new File(dataDir + "/" + fileName);
    }

    private void logD(String message){
        Log.d("yys", message);
    }

    private void logE(String message){
        Log.e("yys", message);
    }

}
