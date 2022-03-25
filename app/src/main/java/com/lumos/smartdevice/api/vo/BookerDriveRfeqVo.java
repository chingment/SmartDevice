package com.lumos.smartdevice.api.vo;

import java.io.Serializable;

public class BookerDriveRfeqVo implements Serializable {
    private String driveId;
    private String ant;

    public String getDriveId() {
        return driveId;
    }

    public void setDriveId(String driveId) {
        this.driveId = driveId;
    }

    public String getAnt() {
        return ant;
    }

    public void setAnt(String ant) {
        this.ant = ant;
    }
}
