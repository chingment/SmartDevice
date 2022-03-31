package com.lumos.smartdevice.devicectrl;

import com.gg.reader.api.dal.GClient;
import com.gg.reader.api.dal.HandlerTagEpcLog;
import com.gg.reader.api.dal.HandlerTagEpcOver;
import com.gg.reader.api.protocol.gx.LogBaseEpcInfo;
import com.gg.reader.api.protocol.gx.LogBaseEpcOver;
import com.gg.reader.api.protocol.gx.MsgBaseInventoryEpc;
import com.gg.reader.api.protocol.gx.MsgBaseStop;
import com.lumos.smartdevice.utils.LogUtil;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class RfeqCtrlByDs implements IRfeqCtrl {

    private static final String TAG = "RfIdCtrlByDs";

    private static RfeqCtrlByDs mThis = null;

    private GClient client;

    private Map<String, TagInfo> map_TagInfos = new LinkedHashMap<String, TagInfo>();//去重数据源

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
            subHandler(client);
        }
        else {
            LogUtil.d(TAG, "打开串口：" + comId + "，波特：" + comBaud + "，失败");
        }
    }

    public boolean isConnect() {
        return true;
    }

    public boolean  sendOpenRead(long ant) {

        boolean isflag = false;

        int inventoryMode = 1;

        MsgBaseInventoryEpc msg = new MsgBaseInventoryEpc();
        msg.setAntennaEnable(ant);
        msg.setInventoryMode(inventoryMode);


        client.sendSynMsg(msg);

        Set<Map.Entry<String, TagInfo>> entrys = map_TagInfos.entrySet();
        for (Map.Entry<String, TagInfo> entry : entrys) {
            TagInfo tagInfo = entry.getValue();
            if (tagInfo.getAntId() == ant) {
                map_TagInfos.remove(entry.getKey());
            }
        }

        return 0x00 == msg.getRtCode();

    }


    public boolean  sendCloseRead(long ant) {
        MsgBaseStop msgStop = new MsgBaseStop();
        client.sendSynMsg(msgStop);

        return 0x00 == msgStop.getRtCode();
    }

    public Map<String, TagInfo> getRfIds(long ant){

        Map<String, TagInfo> ant_TagInfos=new LinkedHashMap<>();

        Set<Map.Entry<String, TagInfo>> entrys = map_TagInfos.entrySet();
        for(Map.Entry<String, TagInfo> entry:entrys){
            TagInfo tagInfo=entry.getValue();
            if(tagInfo.getAntId()==ant){
                ant_TagInfos.put(entry.getKey(),tagInfo);
            }
        }

        return ant_TagInfos;
    }

    //todo 此处只统计所有天线读取次数和 需要细分天线 自行根据属性 info.getAntId() 统计
    public void pooled6cData(LogBaseEpcInfo info) {
        String key=info.getEpc();
        if (map_TagInfos.containsKey(info.getEpc())) {
            TagInfo tagInfo = map_TagInfos.get(key);
            Long count = map_TagInfos.get(key).getCount();
            count++;
            tagInfo.setRssi(info.getRssi() + "");
            tagInfo.setCount(count);
            map_TagInfos.put(key, tagInfo);
        } else {
            TagInfo tag = new TagInfo();
            tag.setIndex(index);
            tag.setType("6C");
            tag.setEpc(key);
            tag.setAntId(info.getAntId());
            tag.setCount(1l);
            tag.setTid(info.getTid());
            tag.setRssi(info.getRssi() + "");
            map_TagInfos.put(key, tag);
            index++;
        }

    }

    //订阅
    public void subHandler(GClient client) {
        client.onTagEpcLog = new HandlerTagEpcLog() {
            public void log(String readerName, LogBaseEpcInfo info) {
                if (null != info && 0 == info.getResult()) {

                    LogUtil.d(TAG,info.getEpc());

                    if(onReadHandlerListener!=null) {
                        onReadHandlerListener.onData(info.getEpc());
                    }

                    pooled6cData(info);

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
        this.onReadHandlerListener = onReadHandlerListener;
    }


}
