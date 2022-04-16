package com.lumos.smartdevice.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.lumos.smartdevice.BuildConfig;
import com.lumos.smartdevice.app.AppContext;
import com.lumos.smartdevice.utils.LogUtil;
import com.lumos.smartdevice.utils.SHA256Encrypt;
import com.lumos.smartdevice.utils.StringUtil;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by tiansj on 15/2/27.
 */
public class HttpClient {

    private static final int CONNECT_TIME_OUT = 10;
    private static final int WRITE_TIME_OUT = 60;
    private static final int READ_TIME_OUT = 60;
    private static final int MAX_REQUESTS_PER_HOST = 10;
    private static final String TAG = HttpClient.class.getSimpleName();
    //private static final String UTF_8 = "UTF-8";
    private static final OkHttpClient client;
    //json请求
    private static final MediaType MediaType_JSON = MediaType.parse("application/json; charset=utf-8");

    static {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS);
        builder.writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS);
        builder.readTimeout(READ_TIME_OUT, TimeUnit.SECONDS);

        builder.networkInterceptors().add(new LoggingInterceptor());
        builder.addInterceptor((new RetryIntercepter(3)));

        client = builder.build();
        client.dispatcher().setMaxRequestsPerHost(MAX_REQUESTS_PER_HOST);
    }

    /**
     * LoggingInterceptor
     */
    static class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request request = chain.request();
            return chain.proceed(request);
        }
    }

    static class RetryIntercepter implements Interceptor {

        public int maxRetry;//最大重试次数
        private int retryNum = 0;//假如设置为3次重试的话，则最大可能请求4次（默认1次+3次重试）

        public RetryIntercepter(int maxRetry) {
            this.maxRetry = maxRetry;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);
            LogUtil.d(TAG,"response.isSuccessful():"+response.isSuccessful());
            while (!response.isSuccessful() && retryNum < maxRetry) {
                retryNum++;
                LogUtil.d(TAG,"maxRetry:"+maxRetry+",retryNum=" + retryNum);
                response = chain.proceed(request);
            }
            return response;
        }
    }

    public static boolean isNetworkAvailable() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) AppContext.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo == null || !networkInfo.isAvailable() || !networkInfo.isConnected();
        } catch (Exception e) {
            LogUtil.v("ConnectivityManager", e.getMessage());
        }
        return true;
    }

    public static void myPost(String url, Object prm, final HttpResponseHandler handler) {

        try
        {
            if (isNetworkAvailable()) {
                handler.sendFailureMessage("网络连接不可用,请检查设置",null);
                return;
            }


            Request.Builder requestBuilder = new Request.Builder().url(url);


            String data = JSON.toJSONString(prm);

            LogUtil.i(TAG,"Request.url:" + url);
            LogUtil.i(TAG,"Request.postData:" + data);


            RequestBody body = RequestBody.create(MediaType_JSON, data);


            requestBuilder.addHeader("appId", "" + BuildConfig.APPLICATION_ID);
            requestBuilder.addHeader("appKey", "" + BuildConfig.APPKEY);
            String currenttime = (System.currentTimeMillis() / 1000) + "";
            requestBuilder.addHeader("timestamp", String.valueOf((System.currentTimeMillis() / 1000)));
            String sign = getSign(BuildConfig.APPLICATION_ID, BuildConfig.APPKEY, BuildConfig.APPSECRET, data, currenttime);
            requestBuilder.addHeader("sign", "" + sign);
            requestBuilder.addHeader("version", BuildConfig.VERSION_NAME);

            requestBuilder.post(body);


            client.newCall(requestBuilder.build()).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    String body = "";

                    if (response.body() != null) {
                        body = response.body().string();
                    }

                    LogUtil.i(TAG, "Request.onSuccess=>>" + body);

                    if (handler != null) {
                        handler.sendSuccessMessage(body);
                    }

                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException ex) {
                    String msg = "读取数据服务发生异常";

                    if (ex instanceof SocketTimeoutException) {
                        msg = "读取数据服务连接超时";
                    } else if (ex instanceof ConnectException) {
                        msg = "读取数据服务连接失败";

                    } else if (ex instanceof UnknownHostException) {
                        msg = "读取数据服务网络异常或连接不存在";
                    }

                    if(handler!=null) {
                        handler.sendFailureMessage(msg, ex);
                    }
                }
            });
        } catch (Exception ex) {
            LogUtil.e(TAG,ex);
            LogUtil.e(TAG,"Request.Exception=>>"+ex.getMessage());
            if(handler!=null) {
                handler.sendFailureMessage("数据提交发生异常", ex);
            }
        }
    }


    public static String myPost(String url, Object prm) {

        try
        {

            Request.Builder requestBuilder = new Request.Builder().url(url);


            String data = JSON.toJSONString(prm);

            LogUtil.i(TAG,"Request.url:" + url);
            LogUtil.i(TAG,"Request.postData:" + data);

            RequestBody body = RequestBody.create(MediaType_JSON, data);

            requestBuilder.addHeader("appId", "" + BuildConfig.APPLICATION_ID);
            requestBuilder.addHeader("appKey", "" + BuildConfig.APPKEY);
            String currenttime = (System.currentTimeMillis() / 1000) + "";
            requestBuilder.addHeader("timestamp", String.valueOf((System.currentTimeMillis() / 1000)));
            String sign = getSign(BuildConfig.APPLICATION_ID, BuildConfig.APPKEY, BuildConfig.APPSECRET, data, currenttime);
            requestBuilder.addHeader("sign", "" + sign);
            requestBuilder.addHeader("version", BuildConfig.VERSION_NAME);

            requestBuilder.post(body);

            Response response = client.newCall(requestBuilder.build()).execute();//得到Response 对象


            String str_response=response.body().string();

            LogUtil.i(TAG, "Request.onSuccess=>>" + str_response);

            return str_response;

        } catch (Exception ex) {
            LogUtil.e(TAG,ex);
            LogUtil.e(TAG,"Request.Exception=>>"+ex.getMessage());

            return null;
        }
    }


    public static void myPostFile(String url, Map<String, String> fields, Map<String, String> filePaths, final HttpResponseHandler handler) {

        try {
            if (isNetworkAvailable()) {
                if(handler!=null) {
                    handler.sendFailureMessage("网络连接不可用,请检查设置", null);
                }
                return;
            }

            if (handler != null) {
                handler.handleBeforeSendMessage();
            }



            Request.Builder requestBuilder = new Request.Builder().url(url);

            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);


            String data="";
            if (fields != null) {
                if (fields.size() > 0) {
                    for (Map.Entry<String, String> entry : fields.entrySet()) {
                        builder.addFormDataPart(entry.getKey(), entry.getValue());
                    }

                    data = JSON.toJSONString(fields);
                }
            }

            if (filePaths == null)
                return;

            if (filePaths.size() < 1)
                return;


            for (Map.Entry<String, String> entry : filePaths.entrySet()) {

                File file = new File( entry.getValue());
                String fileName = file.getName();
                RequestBody fileBody = RequestBody.create(MediaType.parse("image/png"), file);
                builder.addFormDataPart(entry.getKey(), fileName, fileBody);
            }




            RequestBody requestBody = builder.build();


            requestBuilder.addHeader("appId", "" + BuildConfig.APPLICATION_ID);
            requestBuilder.addHeader("appKey", "" + BuildConfig.APPKEY);
            String currenttime = (System.currentTimeMillis() / 1000) + "";
            requestBuilder.addHeader("timestamp", String.valueOf((System.currentTimeMillis() / 1000)));
            String sign = getSign(BuildConfig.APPLICATION_ID, BuildConfig.APPKEY, BuildConfig.APPSECRET, data, currenttime);
            requestBuilder.addHeader("sign", "" + sign);
            requestBuilder.addHeader("version", BuildConfig.VERSION_NAME);


            requestBuilder.post(requestBody);


            client.newCall(requestBuilder.build()).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    String body = "";

                    if (response.body() != null) {
                        body = response.body().string();
                    }

                    LogUtil.d(TAG, "Request.onSuccess=>>" + body);

                    if (handler != null) {
                        handler.sendSuccessMessage(body);
                    }

                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException ex) {
                    LogUtil.e(TAG,"Request.onFailure=>>"+ex.getMessage());
                    String msg = "读取数据服务发生异常";
                    if (ex instanceof SocketTimeoutException) {
                        msg = "读取数据服务连接超时";
                    } else if (ex instanceof ConnectException) {
                        msg = "读取数据服务连接失败";

                    } else if (ex instanceof UnknownHostException) {
                        msg = "读取数据服务网络异常或连接不存在";
                    }

                    if (handler != null) {
                        handler.sendFailureMessage(msg, ex);
                    }
                }
            });
        } catch (Exception ex) {
            LogUtil.e(TAG,ex);
            LogUtil.e(TAG,"Request.Exception=>>"+ex.getMessage());
            if (handler != null) {
                handler.sendFailureMessage("数据提交发生异常", ex);
            }
        }
    }

    public static String getSign(String appId, String appKey, String appSecret, String data, String currenttime) {
        // 待加密
        String queryStr =appId+ appKey + appSecret + currenttime + data;
//        LogUtil.e(TAG, "queryStr>>==>>" + queryStr);
        String sortedStr = StringUtil.sortString(queryStr);
//        LogUtil.e(TAG, "sortedStr>>==>>" + sortedStr);
        String sha256edStr = SHA256Encrypt.bin2hex(sortedStr).toLowerCase();
//        LogUtil.e(TAG, "sha256edStr>>==>>" + sha256edStr);
//        String base64Str = Base64.encodeToString(sha256edStr.getBytes(), Base64.NO_WRAP);
//        String base64Str = StringUtils.replaceEnter(Base64.encodeToString(sha256edStr.getBytes(), Base64.NO_WRAP), "");
//        LogUtil.e(TAG, "加密后>>==>>" + base64Str);
        return sha256edStr;
    }
}
