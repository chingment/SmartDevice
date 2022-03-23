package com.lumos.smartdevice.model;

import java.io.Serializable;

public class BookerDriveLockeqVo implements Serializable{
    private String driveId;
    private String plate;
    private String ant;

    public String getDriveId() {
        return driveId;
    }

    public void setDriveId(String driveId) {
        this.driveId = driveId;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getAnt() {
        return ant;
    }

    public void setAnt(String ant) {
        this.ant = ant;
    }
}
