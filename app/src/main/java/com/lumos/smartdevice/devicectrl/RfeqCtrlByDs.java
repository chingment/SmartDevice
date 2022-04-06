package com.lumos.smartdevice.devicectrl;

import com.gg.reader.api.dal.GClient;
import com.gg.reader.api.dal.HandlerTagEpcLog;
import com.gg.reader.api.dal.HandlerTagEpcOver;
import com.gg.reader.api.protocol.gx.LogBaseEpcInfo;
import com.gg.reader.api.protocol.gx.LogBaseEpcOver;
import com.gg.reader.api.protocol.gx.MsgBaseInventoryEpc;
import com.gg.reader.api.protocol.gx.MsgBaseStop;
import com.lumos.smartdevice.utils.LogUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RfeqCtrlByDs implements IRfeqCtrl {

    private static final String TAG = "RfIdCtrlByDs";

    private static RfeqCtrlByDs mThis = null;

    private GClient client;
    private boolean isConnect;
    private String comId;
    private int comBaud;
    private String comPrl;

    private long index=0;


    private Map<String, TagInfo> map_TagInfos = new LinkedHashMap<String, TagInfo>();//去重数据源

    private RfeqCtrlByDs(String comId, int comBaud,String comPrl) {
        client = new GClient();
        this.comId = comId;
        this.comBaud = comBaud;
        this.comPrl = comPrl;
    }

    public static RfeqCtrlByDs getInstance(String comId, int comBaud,String comPrl) {
        LogUtil.i(TAG, "comId:" + comId + ",comBaud:" + comBaud);
        if (mThis == null) {
            synchronized (RfeqCtrlByDs.class) {
                if (mThis == null) {
                    mThis = new RfeqCtrlByDs(comId,comBaud,comPrl);
                }
            }
        }
        return mThis;
    }

    private void connect() {
        String param = "/dev/" + comId + ":" + comBaud;
        if (client.openAndroidSerial(param, 1000)) {
            this.isConnect = true;
            LogUtil.d(TAG, "打开串口：" + comId + "，波特：" + comBaud + "，成功");
            subHandler(client);
        } else {
            LogUtil.d(TAG, "打开串口：" + comId + "，波特：" + comBaud + "，失败");
        }
    }

    public boolean isConnect() {
        return isConnect;
    }

    private List<Integer> getAntIds(String ant){

        return new ArrayList<>();
    }

    private long convertAntennaEnable(String ant){

        long i_ant=0;

        String[] s_ant = ant.split(",");


//        StringBuffer buffer = new StringBuffer();
//        for (String box : checkBoxMap.values()) {
//
//
//            if(box.equals("1")||box.equals("2")){
//                buffer.append(1);
//            } else {
//                buffer.append(0);
//            }
//
//        }

        //  Long c= Long.valueOf(buffer.reverse().toString(), 2);


        return i_ant;
    }

    public boolean sendOpenRead(String ant) {


        List<Integer> antIds=getAntIds(ant);

        MsgBaseInventoryEpc msg = new MsgBaseInventoryEpc();
        msg.setAntennaEnable(convertAntennaEnable(ant));
        msg.setInventoryMode(1);

        Set<Map.Entry<String, TagInfo>> entrys = map_TagInfos.entrySet();

        for (Map.Entry<String, TagInfo> entry : entrys) {
            TagInfo tagInfo = entry.getValue();
            if (antIds.contains(tagInfo.getAntId())) {
                map_TagInfos.remove(entry.getKey());
            }
        }

        client.sendSynMsg(msg);

        return 0x00 == msg.getRtCode();

    }

    public boolean sendCloseRead() {
        MsgBaseStop msgStop = new MsgBaseStop();


        client.sendSynMsg(msgStop);

        return 0x00 == msgStop.getRtCode();
    }

    public Map<String, TagInfo> getRfIds(String ant){

        Map<String, TagInfo> ant_TagInfos=new LinkedHashMap<>();

        Set<Map.Entry<String, TagInfo>> entrys = map_TagInfos.entrySet();
        for(Map.Entry<String, TagInfo> entry:entrys){
            TagInfo tagInfo=entry.getValue();
            if(tagInfo.getAntId()==1l){
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
