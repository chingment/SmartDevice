package com.lumos.smartdevice.model;

import java.io.Serializable;

public class BookerBookVo implements Serializable {
    private String skuId;
    private String imgUrl;
    private String name;
    private String cumCode;
    private String rfId;

    public BookerBookVo(){

    }

    public BookerBookVo(String skuId, String rfId, String name, String cumCode, String  imgUrl) {
        this.skuId = skuId;
        this.rfId = rfId;
        this.name = name;
        this.cumCode = cumCode;
        this.imgUrl = imgUrl;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCumCode() {
        return cumCode;
    }

    public void setCumCode(String cumCode) {
        this.cumCode = cumCode;
    }

    public String getRfId() {
        return rfId;
    }

    public void setRfId(String rfId) {
        this.rfId = rfId;
    }
}
