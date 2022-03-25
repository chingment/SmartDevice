package com.lumos.smartdevice.api.vo;

import java.io.Serializable;
import java.util.List;

public class IdentityInfoByBorrowerVo implements Serializable {
    private String fullName;
    private String cardNo;
    private int canBorrowQuantity;
    private int borrowedQuantity;
    private int willdueQuantity;
    private int overdueQuantity;
    private FieldVo status;
    private float overdueFine;


    private List<BookerBorrowBookVo> borrowBooks;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public int getCanBorrowQuantity() {
        return canBorrowQuantity;
    }

    public void setCanBorrowQuantity(int canBorrowQuantity) {
        this.canBorrowQuantity = canBorrowQuantity;
    }

    public int getBorrowedQuantity() {
        return borrowedQuantity;
    }

    public void setBorrowedQuantity(int borrowedQuantity) {
        this.borrowedQuantity = borrowedQuantity;
    }

    public int getWilldueQuantity() {
        return willdueQuantity;
    }

    public void setWilldueQuantity(int willdueQuantity) {
        this.willdueQuantity = willdueQuantity;
    }

    public int getOverdueQuantity() {
        return overdueQuantity;
    }

    public void setOverdueQuantity(int overdueQuantity) {
        this.overdueQuantity = overdueQuantity;
    }

    public float getOverdueFine() {
        return overdueFine;
    }

    public void setOverdueFine(float overdueFine) {
        this.overdueFine = overdueFine;
    }

    public List<BookerBorrowBookVo> getBorrowBooks() {
        return borrowBooks;
    }

    public void setBorrowBooks(List<BookerBorrowBookVo> borrowBooks) {
        this.borrowBooks = borrowBooks;
    }

    public FieldVo getStatus() {
        return status;
    }

    public void setStatus(FieldVo status) {
        this.status = status;
    }
}
