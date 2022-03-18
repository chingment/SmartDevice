package com.lumos.smartdevice.model;

import java.io.Serializable;
import java.util.List;

public class IdentityInfoByBorrowerBean implements Serializable {
    private String fullName;
    private String cardNo;
    private int canBorrowQuantity;
    private int borrowedQuantity;
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
