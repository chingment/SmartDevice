package com.lumos.smartdevice.devicectrl;

import android.util.Log;

import com.gg.reader.api.dal.GClient;
import com.gg.reader.api.dal.HandlerTagEpcLog;
import com.gg.reader.api.dal.HandlerTagEpcOver;
import com.gg.reader.api.protocol.gx.LogBaseEpcInfo;
import com.gg.reader.api.protocol.gx.LogBaseEpcOver;
import com.gg.reader.api.protocol.gx.MsgBaseInventoryEpc;
import com.gg.reader.api.protocol.gx.MsgBaseStop;
import com.lumos.smartdevice.utils.LogUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RfeqCtrlByDs implements IRfeqCtrl {

    private static final String TAG = "RfeqCtrlByDs";

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
                    mThis.connect();
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

    private long convertAntennaEnable(String ant) {

        long i_ant = -1;

        String[] s_ants = ant.split(",");

        StringBuilder buffer = new StringBuilder();

        String[] m_ants = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};

        if (s_ants == null || s_ants.length <= 0)
            return i_ant;

        for (String m_ant : m_ants) {
            if (Arrays.asList(s_ants).contains(m_ant)) {
                buffer.append(1);
            } else {
                buffer.append(0);
            }
        }


        i_ant = Long.valueOf(buffer.reverse().toString(), 2);

        return i_ant;
    }

    public boolean sendOpenRead(String ant) {

        LogUtil.d(TAG,"sendOpenRead.ant:"+ant);

        MsgBaseInventoryEpc msg = new MsgBaseInventoryEpc();
        msg.setAntennaEnable(convertAntennaEnable(ant));
        msg.setInventoryMode(1);

        map_TagInfos.clear();

        client.sendSynMsg(msg);

        return 0x00 == msg.getRtCode();
    }

    public boolean sendOpenAllRead() {
        return sendOpenRead("1,2,3,4,5,6,7,8");
    }

    public boolean sendCloseRead() {
        MsgBaseStop msgStop = new MsgBaseStop();

        client.sendSynMsg(msgStop);

        return 0x00 == msgStop.getRtCode();
    }

    public Map<String, TagInfo> getRfIds(String ant) {
        LogUtil.d(TAG,"getRfIds.ant:"+ant);
        String[] s_ants = ant.split(",");

        Map<String, TagInfo> ant_TagInfos = new LinkedHashMap<>();

        Set<Map.Entry<String, TagInfo>> entrys = map_TagInfos.entrySet();
        for (Map.Entry<String, TagInfo> entry : entrys) {
            TagInfo tagInfo = entry.getValue();
            if (Arrays.asList(s_ants).contains(String.valueOf(tagInfo.getAntId()))) {
                ant_TagInfos.put(entry.getKey(), tagInfo);
            }
        }

        return ant_TagInfos;
    }

    public  Map<String, TagInfo> getAllRfIds() {
        return getRfIds("1,2,3,4,5,6,7,8");
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
