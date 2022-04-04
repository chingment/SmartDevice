package com.lumos.smartdevice.devicectrl;


import android.VendingMachine.symvdio;

import com.lumos.smartdevice.utils.LogUtil;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LockeqCtrlByDs implements ILockeqCtrl {

    private static final String TAG = "LockeqCtrlByDs";

    private static LockeqCtrlByDs mThis= null;

    private symvdio sym;
    private String comId;
    private int comBaud;
    private String comPrl;

    private boolean isConnect=false;

    private LockeqCtrlByDs(String comId, int comBaud,String comPrl) {
        try {
            sym = new symvdio();
            this.comId = comId;
            this.comBaud = comBaud;
            this.comPrl = comPrl;
        } catch (Exception ex) {
            sym = null;
        }
    }

    public static LockeqCtrlByDs getInstance(String comId, int comBaud,String comPrl) {
        LogUtil.i(TAG, "comId:" + comId + ",comBaud:" + comBaud);
        if (mThis == null) {
            synchronized (LockeqCtrlByDs.class) {
                if (mThis == null) {
                    mThis = new LockeqCtrlByDs(comId,comBaud,comPrl);
                }
            }
        }
        return mThis;
    }

    public void connect() {

        File file = new File("/dev/" + comId);
        if (file.exists()) {
            int rc_status = sym.Connect(comId, comBaud);
            LogUtil.d(TAG, "打开串口：" + comId + "，波特：" + comBaud + "，状态为：" + rc_status);
            if(rc_status==0) {
                isConnect = true;
            }
        } else {
            LogUtil.d(TAG, "打开串口：" + comId + "，波特：" + comBaud + "，失败");
        }

    }

    public boolean  sendOpenSlot(String ant) {

        int[] arr_ant =getAnt(ant);

        if(arr_ant==null||arr_ant.length<2)
            return  false;

        int[] rowData = new int[16];
        rowData[0] = arr_ant[0];
        rowData[1] = arr_ant[1];

        int var0 = sym.SN_MV_Set_RowData(rowData);
        return var0 == 0;
    }

    private int[] getAnt(String ant) {

        int[] i_ant = new int[2];

        try {

            String[] s_ant = ant.split(",");

            i_ant[0] = Integer.parseInt(s_ant[0]);
            i_ant[1] = Integer.parseInt(s_ant[1]);

            return  i_ant;
        } catch (Exception ex) {
            return null;
        }
    }


    public int  getSlotStatus(String ant) {

        int[] arr_ant = getAnt(ant);

        if (arr_ant == null || arr_ant.length < 2)
            return 0;

        int[] var1 = sym.SN_MV_Get_ColData(arr_ant[0]);

        return 0;
    }

    public boolean isConnect(){
        return isConnect;
    }
}
