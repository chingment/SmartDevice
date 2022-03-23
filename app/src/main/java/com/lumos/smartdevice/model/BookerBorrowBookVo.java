package com.lumos.smartdevice.model;

import java.io.Serializable;

public class BookerBorrowBookVo implements Serializable {
    private String borrowId;
    private String skuId;
    private String skuImgUrl;
    private String skuName;
    private String skuCumCode;
    private String skuRfId;
    private String borrowTime;
    private String returnTime;
    private String expireTime;
    private float overdueFine;
    private FieldVo borrowWay;
    private FieldVo status;
    private String renewLastTime;
    private int renewCount;
    private boolean isWilldue;
    private boolean isOverdue;
    private boolean canRenew;
    private boolean canReturn;
    private boolean needPay;


    public String getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(String borrowId) {
        this.borrowId = borrowId;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getSkuImgUrl() {
        return skuImgUrl;
    }

    public void setSkuImgUrl(String skuImgUrl) {
        this.skuImgUrl = skuImgUrl;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getSkuCumCode() {
        return skuCumCode;
    }

    public void setSkuCumCode(String skuCumCode) {
        this.skuCumCode = skuCumCode;
    }

    public String getSkuRfId() {
        return skuRfId;
    }

    public void setSkuRfId(String skuRfId) {
        this.skuRfId = skuRfId;
    }

    public String getBorrowTime() {
        return borrowTime;
    }

    public void setBorrowTime(String borrowTime) {
        this.borrowTime = borrowTime;
    }

    public String getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(String returnTime) {
        this.returnTime = returnTime;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public float getOverdueFine() {
        return overdueFine;
    }

    public void setOverdueFine(float overdueFine) {
        this.overdueFine = overdueFine;
    }

    public FieldVo getBorrowWay() {
        return borrowWay;
    }

    public void setBorrowWay(FieldVo borrowWay) {
        this.borrowWay = borrowWay;
    }

    public FieldVo getStatus() {
        return status;
    }

    public void setStatus(FieldVo status) {
        this.status = status;
    }

    public String getRenewLastTime() {
        return renewLastTime;
    }

    public void setRenewLastTime(String renewLastTime) {
        this.renewLastTime = renewLastTime;
    }

    public int getRenewCount() {
        return renewCount;
    }

    public void setRenewCount(int renewCount) {
        this.renewCount = renewCount;
    }

    public boolean isWilldue() {
        return isWilldue;
    }

    public void setWilldue(boolean willdue) {
        isWilldue = willdue;
    }

    public boolean isOverdue() {
        return isOverdue;
    }

    public void setOverdue(boolean overdue) {
        isOverdue = overdue;
    }

    public boolean isCanRenew() {
        return canRenew;
    }

    public void setCanRenew(boolean canRenew) {
        this.canRenew = canRenew;
    }

    public boolean isCanReturn() {
        return canReturn;
    }

    public void setCanReturn(boolean canReturn) {
        this.canReturn = canReturn;
    }

    public boolean isNeedPay() {
        return needPay;
    }

    public void setNeedPay(boolean needPay) {
        this.needPay = needPay;
    }
}
