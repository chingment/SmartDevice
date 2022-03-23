package com.lumos.smartdevice.model;

import java.io.Serializable;
import java.util.List;

public class IdentityInfoByBorrowerBean implements Serializable {
    private String fullName;
    private String cardNo;
    private int canBorrowQuantity;
    private int borrowedQuantity;
    private int willdueQuantity;
    private int overdueQuantity;
    private FieldBean status;
    private float overdueFine;


    private List<BookerBorrowBookBean> borrowBooks;

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

    public List<BookerBorrowBookBean> getBorrowBooks() {
        return borrowBooks;
    }

    public void setBorrowBooks(List<BookerBorrowBookBean> borrowBooks) {
        this.borrowBooks = borrowBooks;
    }

    public FieldBean getStatus() {
        return status;
    }

    public void setStatus(FieldBean status) {
        this.status = status;
    }
}
