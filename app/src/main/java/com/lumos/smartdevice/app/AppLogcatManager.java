package com.lumos.smartdevice.app;

import android.os.Build;

import com.lumos.smartdevice.api.ReqUrl;
import com.lumos.smartdevice.http.HttpClient;
import com.lumos.smartdevice.http.HttpResponseHandler;
import com.lumos.smartdevice.utils.FileUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AppLogcatManager {

    public static void uploadLogcat2Server(String cmd,String action) {

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

        FileOutputStream fos=null;
        try {

            Process process = Runtime.getRuntime().exec(cmd);//抓取当前的缓存日志
            BufferedReader buffRead = new BufferedReader(new InputStreamReader(process.getInputStream()));//获取输入流
            //Runtime.getRuntime().exec("logcat -c");//清除是为了下次抓取不会从头抓取
            String line = null;
            String newline = System.getProperty("line.separator");
            int logCount=0;

            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());

            String fileName ="logcat_"+ Build.SERIAL+"_"+action+"_"+timestamp;
            String fileExt=".log";

            String path = AppFileUtil.getLogDir();
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String filePath = path + "/" + fileName+fileExt;

            fos = new FileOutputStream(filePath);

            while ((line = buffRead.readLine()) != null) {//循环读取每一行

                fos.write((line).getBytes());//追加内容
                fos.write(newline.getBytes());//换行

                logCount++;
                if (logCount > 1000) {//判断是否大于1000行 退出
                    break;
                }
            }

            Runtime.getRuntime().exec("logcat -c");

            fos.flush();
            fos.close();
            fos = null;

            HashMap<String, String> fields = new HashMap<>();
            fields.put("folder", "device");
            fields.put("fileName", fileName);
            fields.put("keepFileName", "0");

            Map<String, String> filePaths = new HashMap<>();
            filePaths.put("file",filePath);

            HttpClient.myPostFile(ReqUrl.device_Upload, fields, filePaths, new HttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    if(response!=null){
                        if(response.contains("上传成功")){
                            FileUtil.deleteFile(filePath);
                        }
                    }
                }
            });


        } catch (Exception var1) {

            if(fos!=null){
                try {
                    fos.flush();
                    fos.close();
                    fos = null;
                }
                catch (Exception var2){

                }
            }
        }
    }

}
