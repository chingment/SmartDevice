package com.lumos.smartdevice.devicectrl;

import com.gg.reader.api.dal.GClient;
import com.gg.reader.api.dal.HandlerTagEpcLog;
import com.gg.reader.api.dal.HandlerTagEpcOver;
import com.gg.reader.api.protocol.gx.LogBaseEpcInfo;
import com.gg.reader.api.protocol.gx.LogBaseEpcOver;
import com.gg.reader.api.protocol.gx.MsgBaseInventoryEpc;
import com.lumos.smartdevice.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class RfeqCtrlByDs implements IRfeqCtrl {

    private static final String TAG = "RfIdCtrlByDs";

    private static RfeqCtrlByDs mThis = null;

    private GClient client;

   // private Map<String, TagInfo> tagInfoMap = new LinkedHashMap<String, TagInfo>();

    private List<String> epcs=new ArrayList<>();

    private long index=0;

    private RfeqCtrlByDs(){
        client = new GClient();
    }

    public static RfeqCtrlByDs getInstance(String comId, int comBaud) {
        LogUtil.i(TAG, "comId:" + comId + ",comBaud:" + comBaud);
        if (mThis == null) {
            synchronized (RfeqCtrlByDs.class) {
                if (mThis == null) {
                    mThis = new RfeqCtrlByDs();
                    mThis.connect(comId, comBaud);
                }
            }
        }
        return mThis;
    }

    private void connect(String comId,int comBaud) {
        String param = "/dev/" + comId + ":" + comBaud;
        if(client.openAndroidSerial(param, 1000)){
            LogUtil.d(TAG, "打开串口：" + comId + "，波特：" + comBaud+"，成功");
        }
        else {
            LogUtil.d(TAG, "打开串口：" + comId + "，波特：" + comBaud + "，失败");
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
