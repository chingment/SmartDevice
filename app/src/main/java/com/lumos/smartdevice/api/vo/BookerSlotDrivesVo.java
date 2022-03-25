package com.lumos.smartdevice.api.vo;

import java.io.Serializable;

public class BookerSlotDrivesVo implements Serializable {
    private BookerDriveLockeqVo lockeq;
    private BookerDriveRfeqVo rfeq;

    public BookerDriveLockeqVo getLockeq() {
        return lockeq;
    }

    public void setLockeq(BookerDriveLockeqVo lockeq) {
        this.lockeq = lockeq;
    }

    public BookerDriveRfeqVo getRfeq() {
        return rfeq;
    }

    public void setRfeq(BookerDriveRfeqVo rfeq) {
        this.rfeq = rfeq;
    }
}
