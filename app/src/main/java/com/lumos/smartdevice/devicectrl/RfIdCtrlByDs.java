package com.lumos.smartdevice.devicectrl;

import android.os.Message;
import android.widget.CheckBox;

import com.gg.reader.api.dal.GClient;
import com.gg.reader.api.dal.HandlerGpiOver;
import com.gg.reader.api.dal.HandlerGpiStart;
import com.gg.reader.api.dal.HandlerTag6bLog;
import com.gg.reader.api.dal.HandlerTag6bOver;
import com.gg.reader.api.dal.HandlerTagEpcLog;
import com.gg.reader.api.dal.HandlerTagEpcOver;
import com.gg.reader.api.dal.HandlerTagGbLog;
import com.gg.reader.api.dal.HandlerTagGbOver;
import com.gg.reader.api.protocol.gx.EnumG;
import com.gg.reader.api.protocol.gx.LogAppGpiOver;
import com.gg.reader.api.protocol.gx.LogAppGpiStart;
import com.gg.reader.api.protocol.gx.LogBase6bInfo;
import com.gg.reader.api.protocol.gx.LogBase6bOver;
import com.gg.reader.api.protocol.gx.LogBaseEpcInfo;
import com.gg.reader.api.protocol.gx.LogBaseEpcOver;
import com.gg.reader.api.protocol.gx.LogBaseGbInfo;
import com.gg.reader.api.protocol.gx.LogBaseGbOver;
import com.gg.reader.api.protocol.gx.MsgBaseInventoryEpc;
import com.gg.reader.api.protocol.gx.ParamEpcReadTid;
import com.gg.reader.api.utils.ThreadPoolUtils;
import com.lumos.smartdevice.utils.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RfIdCtrlByDs implements IRfIdCtrl {

    private static final String TAG = "RfIdCtrlByDs";

    private static RfIdCtrlByDs mRfIdCtrlByDs = null;

    private GClient client;

   // private Map<String, TagInfo> tagInfoMap = new LinkedHashMap<String, TagInfo>();

    private List<String> epcs=new ArrayList<>();

    private long index=0;

    private RfIdCtrlByDs(){
        client = new GClient();
    }

    public static RfIdCtrlByDs getInstance(String comId, int comBaud) {
        LogUtil.i(TAG, "comId:" + comId + ",comBaud:" + comBaud);
        if (mRfIdCtrlByDs == null) {
            synchronized (RfIdCtrlByDs.class) {
                if (mRfIdCtrlByDs == null) {
                    mRfIdCtrlByDs = new RfIdCtrlByDs();
                    mRfIdCtrlByDs.connect(comId, comBaud);
                }
            }
        }
        return mRfIdCtrlByDs;
    }

    private void connect(String comId,int comBaud) {

        LogUtil.d(TAG, "打开串口：" + comId + "，波特：" + comBaud);

        String param = "/dev/" + comId + ":" + comBaud;

        if( client.openAndroidSerial(param, 1000)){
            LogUtil.d(TAG, "打开串口：" + comId + "，波特：" + comBaud+",成功");
        }
        else
        {
            LogUtil.d(TAG, "打开串口：" + comId + "，波特：" + comBaud+",失败");
        }

    }


    public boolean  sendRead() {

        int inventoryMode=1;
        long ant=1;
        MsgBaseInventoryEpc msg = new MsgBaseInventoryEpc();
        msg.setAntennaEnable(ant);
        msg.setInventoryMode(inventoryMode);

        subHandler(client);

        client.sendSynMsg(msg);

        return 0x00 == msg.getRtCode();

    }

    //订阅
    public void subHandler(GClient client) {
        client.onTagEpcLog = new HandlerTagEpcLog() {
            public void log(String readerName, LogBaseEpcInfo info) {
                if (null != info && 0 == info.getResult()) {


                    if(!epcs.contains(info.getEpc())) {
                        epcs.add(info.getEpc());
                    }

                    if(onReadHandlerListener!=null) {
                        onReadHandlerListener.onData(epcs);
                    }

//                    Map<String, TagInfo> infoMap = pooled6cData(info);
//
//                    for(Map.Entry entry : infoMap.entrySet()) {
//
//                        System.out.println("value is : " + entry.getKey() + " and value is : " +  entry.getValue());
//
//                    }

                }
            }
        };
        client.onTagEpcOver = new HandlerTagEpcOver() {
            public void log(String readerName, LogBaseEpcOver info) {

            }
        };

    }

    private OnReadHandlerListener onReadHandlerListener;
    @Override
    public void setReadHandler(OnReadHandlerListener onReadHandlerListener) {
        this.epcs.clear();
        this.onReadHandlerListener = onReadHandlerListener;
    }

    //    public List<String> pooled6cEpcs(String epc) {
//        if (epcs.contains(epc)) {
//            TagInfo tagInfo = tagInfoMap.get(info.getTid() + info.getEpc());
//            Long count = tagInfoMap.get(info.getTid() + info.getEpc()).getCount();
//            count++;
//            tagInfo.setRssi(info.getRssi() + "");
//            tagInfo.setCount(count);
//            tagInfoMap.put(info.getTid() + info.getEpc(), tagInfo);
//        } else {
//            TagInfo tag = new TagInfo();
//            tag.setIndex(index);
//            tag.setType("6C");
//            tag.setEpc(info.getEpc());
//            tag.setCount(1l);
//            tag.setTid(info.getTid());
//            tag.setRssi(info.getRssi() + "");
//            tagInfoMap.put(info.getTid() + info.getEpc(), tag);
//            index++;
//        }
//
//        return tagInfoMap;
//    }


//    public Map<String, TagInfo> pooled6cData(LogBaseEpcInfo info) {
//        if (tagInfoMap.containsKey(info.getTid() + info.getEpc())) {
//            TagInfo tagInfo = tagInfoMap.get(info.getTid() + info.getEpc());
//            Long count = tagInfoMap.get(info.getTid() + info.getEpc()).getCount();
//            count++;
//            tagInfo.setRssi(info.getRssi() + "");
//            tagInfo.setCount(count);
//            tagInfoMap.put(info.getTid() + info.getEpc(), tagInfo);
//        } else {
//            TagInfo tag = new TagInfo();
//            tag.setIndex(index);
//            tag.setType("6C");
//            tag.setEpc(info.getEpc());
//            tag.setCount(1l);
//            tag.setTid(info.getTid());
//            tag.setRssi(info.getRssi() + "");
//            tagInfoMap.put(info.getTid() + info.getEpc(), tag);
//            index++;
//        }
//
//        return tagInfoMap;
//    }



}
