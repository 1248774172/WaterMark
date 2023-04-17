package com.xiaoer.watermark.util;

import android.app.Application;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class NetWorkUtil {

    private volatile static NetWorkUtil mInstance;
    private static Application mApplication;

    private NetWorkUtil() {
    }

    public static NetWorkUtil getInstance(Application application) {
        mApplication = application;
        if (mInstance == null) {
            synchronized (NetWorkUtil.class) {
                if (mInstance == null) {
                    mInstance = new NetWorkUtil();
                }
            }
        }
        return mInstance;
    }

    public void requestByGet(String urlStr,NetWorkCallback<String> callback) {
        new Thread(() -> {
            try {
                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(30 * 1000);//设置超时时长，单位ms
                connection.setRequestMethod("GET");//设置请求格式
                connection.setRequestProperty("Content-Type", "Application/json");//期望返回的数据格式
                connection.setRequestProperty("CharSet", "UTF-8");//设置字符集
                connection.setRequestProperty("Accept-CharSet", "UTF-8");//请求的字符集
                connection.connect();//发送请求

                int responseCode = connection.getResponseCode();//获取返回码
                String json = getJson(connection);
                runOnUiThread(() -> {
                    if(callback == null){
                        return;
                    }
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        callback.onSuccess(json);
                    }else {
                        callback.onFail("!OK");
                    }
                });
            } catch (IOException e) {
                LogUtil.e(e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    private String getJson(HttpURLConnection connection){
        InputStream in;
        try {
            in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder html = new StringBuilder();
            for (String line; (line = reader.readLine()) != null; ) {
                html.append(line);
            }
            in.close();
            return html.toString().trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void runOnUiThread(Runnable runnable) {
        Handler handler = new Handler(mApplication.getMainLooper());
        handler.post(runnable);
    }

    private void requestByPost(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(30 * 1000);//设置超时时长，单位ms
            connection.setRequestMethod("GET");//设置请求格式
            connection.setRequestProperty("Content-Type", "Application/json");//期望返回的数据格式
            connection.setRequestProperty("CharSet", "UTF-8");//设置字符集
            connection.setRequestProperty("Accept-CharSet", "UTF-8");//请求的字符集

            connection.setUseCaches(false);//设置缓存使用
            connection.setDoInput(true);//设置输入流使用
            connection.setDoOutput(true);//设置输出流使用
            connection.connect();

            String data = "username=" + getEncodeValue("小王") + "&number=" + getEncodeValue(
                    "123456");
            OutputStream outputStream = connection.getOutputStream();//获取到输出流
            outputStream.write(data.getBytes());//写入数据
            outputStream.flush();//执行
            outputStream.close();//关闭

            int responseCode = connection.getResponseCode();
            String responseMessage = connection.getResponseMessage();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                //TODO
            }

            runOnUiThread(() -> {

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getEncodeValue(String name) {
        String encode = null;

        try {
            encode = URLEncoder.encode(name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encode;
    }

    public interface NetWorkCallback<T>{
        void onSuccess(T result);
        void onFail(String msg);
        void onCancel(String msg);
    }

}
