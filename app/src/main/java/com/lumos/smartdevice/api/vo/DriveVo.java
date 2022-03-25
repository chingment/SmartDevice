package com.lumos.smartdevice.api.vo;

import java.io.Serializable;

public class DriveVo implements Serializable {
    private String driveId;
    private String name;
    private String comId;
    private int comBaud;
    private String comPrl;

    public String getDriveId() {
        return driveId;
    }

    public void setDriveId(String driveId) {
        this.driveId = driveId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComId() {
        return comId;
    }

    public void setComId(String comId) {
        this.comId = comId;
    }

    public int getComBaud() {
        return comBaud;
    }

    public void setComBaud(int comBaud) {
        this.comBaud = comBaud;
    }

    public String getComPrl() {
        return comPrl;
    }

    public void setComPrl(String comPrl) {
        this.comPrl = comPrl;
    }
}
