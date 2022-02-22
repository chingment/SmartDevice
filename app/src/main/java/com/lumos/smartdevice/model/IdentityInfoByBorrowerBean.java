package com.lumos.smartdevice.model;

import java.util.List;

public class IdentityInfoByBorrowerBean {
    private String signName;
    private String cardNo;
    private int canBorrowQuantity;
    private int borrowedQuantity;
    private float overdueFine;
    private List<BookerBorrowBookBean> borrowBooks;


    public String getSignName() {
        return signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
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
}
