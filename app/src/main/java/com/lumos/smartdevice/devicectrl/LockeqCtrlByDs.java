package com.lumos.smartdevice.devicectrl;


import android.VendingMachine.symvdio;

import com.lumos.smartdevice.utils.LogUtil;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
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
                    mThis.connect();
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

    public boolean setLight(String ant){

        int[] arr_ant =getAnt(ant);

        if(arr_ant==null||arr_ant.length<2)
            return  false;

        int[] rowData = new int[16];
        rowData[0] = arr_ant[0];
        rowData[1] = 99;
        rowData[2] = arr_ant[1];

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

    public int getSlotStatus(String ant) {

        int status = -1;

        int[] arr_ant = getAnt(ant);

        if (arr_ant == null || arr_ant.length < 2)
            return 0;

        int[] var1 = sym.SN_MV_Get_ColData(arr_ant[0]);

        int door = arr_ant[1];

        if (door >= 1 && door <= 8) {
            int i_status = var1[4];
            int[] s_status = byteToBit(i_status);
            status = s_status[door-1];
        } else if (door >= 9 && door <= 16) {
            int i_status = var1[3];
            int[] s_status = byteToBit(i_status);
            status = s_status[door-9];
        } else if (door >= 17 && door <= 24) {
            int i_status = var1[2];
            int[] s_status = byteToBit(i_status);
            status = s_status[door-17];
        }

        return status;
    }

    public HashMap<Integer,int[]> getSlotStatus(){
        return null;
    }

    public boolean isConnect(){
        return isConnect;
    }

    public static  int[] byteToBit(int i) {

        byte b = (byte) i;

        String s_b = "" + (byte) ((b >> 0) & 0x1) +
                (byte) ((b >> 1) & 0x1) +
                (byte) ((b >> 2) & 0x1) +
                (byte) ((b >> 3) & 0x1) +
                (byte) ((b >> 4) & 0x1) +
                (byte) ((b >> 5) & 0x1) +
                (byte) ((b >> 6) & 0x1) +
                (byte) ((b >> 7) & 0x1);

        int[] arr_s = new int[s_b.length()];


        for (int x = 0; x < arr_s.length; x++) {
            arr_s[x] = Integer.valueOf(s_b.substring(x, x + 1));
        }

        return arr_s;
    }
}
