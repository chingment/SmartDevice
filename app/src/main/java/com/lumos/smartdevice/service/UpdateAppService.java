package com.lumos.smartdevice.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.BuildConfig;
import com.lumos.smartdevice.api.ReqUrl;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.rop.RetDeviceCheckAppVersion;
import com.lumos.smartdevice.http.HttpClient;
import com.lumos.smartdevice.ostctrl.OstCtrlInterface;
import com.lumos.smartdevice.utils.DeviceUtil;
import com.lumos.smartdevice.utils.FileUtil;
import com.lumos.smartdevice.utils.JsonUtil;
import com.lumos.smartdevice.utils.LogUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 检测安装更新文件的助手类
 *
 * @author G.Y.Y
 *
 */

public class UpdateAppService extends Service {

    private static final String TAG = "UpdateAppService";

    private DownloadManager download_manager;
    private DownloadCompleteReceiver download_receiver;

    @Override
    public void onCreate() {
        LogUtil.d(TAG, "onCreate...");

        download_manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG, "onStartCommand...");


        Thread thread=new Thread(new Runnable() {
            @Override
            public void run()
            {
                checkUpdate();
            }
        });

        thread.start();


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {

        if (download_receiver != null) {
            unregisterReceiver(download_receiver);
        }

        super.onDestroy();
    }

    private void checkUpdate() {


        sendBroadcastMsg(1, "正在检查最新版本");

        Map<String, Object> params = new HashMap<>();
        params.put("deviceId", DeviceUtil.getDeviceId());
        params.put("appId", BuildConfig.APPLICATION_ID);
        params.put("appKey", BuildConfig.APPKEY);

        String response = HttpClient.myPost(ReqUrl.device_checkAppVersion, params);

        ResultBean<RetDeviceCheckAppVersion> result = JsonUtil.toResult(response, new TypeReference<ResultBean<RetDeviceCheckAppVersion>>() {
        });

        if (result.getCode() != ResultCode.SUCCESS) {
            sendBroadcastMsg(2, result.getMsg());
            return;
        }

        RetDeviceCheckAppVersion ret = result.getData();

        if (ret == null) {
            sendBroadcastMsg(2, "已经是最新版本");
            return;
        }

        if (ret.getVersionName() == null || ret.getDownloadUrl() == null) {
            sendBroadcastMsg(2, "已经是最新版本");
            return;
        }

        int cp = compareVersion(ret.getVersionName(), BuildConfig.VERSION_NAME);
        if (cp != 1) {
            sendBroadcastMsg(2, "已经是最新版本");
            return;
        }

        downloadManagerApk(ret.getDownloadUrl());

    }

    private void sendBroadcastMsg(int status, String message) {
        Intent intent = new Intent();
        intent.setAction("action.smartdevice.app.update");
        intent.putExtra("status", status);
        intent.putExtra("message", message);
        sendBroadcast(intent);
    }

    private void downloadManagerApk(String downpath) {

        DownloadManager.Request manager_rquest = new DownloadManager.Request(Uri.parse(downpath));
        manager_rquest.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        manager_rquest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        manager_rquest.setVisibleInDownloadsUi(true);

        String filePath;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            filePath = Environment.getExternalStorageDirectory() + "/Download";
        }

        String downloadUpdateApkFilePath = filePath + File.separator + "smartdevices" + System.currentTimeMillis() + ".apk";

        if (FileUtil.isFileExist(downloadUpdateApkFilePath)) {
            deleteFile(downloadUpdateApkFilePath);
        }

        Uri fileUri = Uri.fromFile(new File(downloadUpdateApkFilePath));

        manager_rquest.setDestinationUri(fileUri);

        download_manager.enqueue(manager_rquest);

        sendBroadcastMsg(3, "正在下载中");

        download_receiver = new DownloadCompleteReceiver();

        registerReceiver(download_receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

    private int compareVersion(String version1, String version2) {

        if (version1 == null)
            return 0;

        if (version2 == null)
            return 0;

        if (version1.equals(version2)) {
            return 0;
        }

        String[] version1Array = version1.split("\\.");
        String[] version2Array = version2.split("\\.");
        int index = 0;
        // 获取最小长度值
        int minLen = Math.min(version1Array.length, version2Array.length);
        int diff = 0;
        // 循环判断每位的大小
        while (index < minLen
                && (diff = Integer.parseInt(version1Array[index])
                - Integer.parseInt(version2Array[index])) == 0) {
            index++;
        }
        if (diff == 0) {
            // 如果位数不一致，比较多余位数
            for (int i = index; i < version1Array.length; i++) {
                if (Integer.parseInt(version1Array[i]) > 0) {
                    return 1;
                }
            }

            for (int i = index; i < version2Array.length; i++) {
                if (Integer.parseInt(version2Array[i]) > 0) {
                    return -1;
                }
            }
            return 0;
        } else {
            return diff > 0 ? 1 : -1;
        }
    }

    private void queryDownloadStatus(long downId) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downId);
        Cursor c = download_manager.query(query);
        if (c != null && c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));

//            int reasonIdx = c.getColumnIndex(DownloadManager.COLUMN_REASON);
//            int titleIdx = c.getColumnIndex(DownloadManager.COLUMN_TITLE);
//            int fileSizeIdx = c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
//            int bytesDLIdx = c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
//            String title = c.getString(titleIdx);
//            int fileSize = c.getInt(fileSizeIdx);
//            int bytesDL = c.getInt(bytesDLIdx);
//            // Translate the pause reason to friendly text.
//            int reason = c.getInt(reasonIdx);

            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                case DownloadManager.STATUS_PENDING:
                case DownloadManager.STATUS_RUNNING:
                    // 正在下载，不做任何事情
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    sendBroadcastMsg(4, "正在下载中");
                    installAPK(downId);
                    // 完成
                    break;
                case DownloadManager.STATUS_FAILED:
                    sendBroadcastMsg(2, "下载失败");
                    // 清除已下载的内容，重新下载
                    break;
            }
        }
    }

    private void installAPK(long downId) {

        Uri uri = download_manager.getUriForDownloadedFile(downId);

        String path = uri.getPath();

        OstCtrlInterface.getInstance().installApk(UpdateAppService.this, path);
    }

    class DownloadCompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            queryDownloadStatus(downId);
        }
    }
}