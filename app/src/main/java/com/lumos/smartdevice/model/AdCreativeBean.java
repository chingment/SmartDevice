package com.lumos.smartdevice.model;

import java.io.Serializable;

public class AdCreativeBean implements Serializable {

    private String fileType;
    private String fileUrl;

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}