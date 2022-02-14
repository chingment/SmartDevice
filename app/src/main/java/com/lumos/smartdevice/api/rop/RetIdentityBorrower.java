package com.lumos.smartdevice.api.rop;

import java.io.Serializable;

public class RetIdentityBorrower implements Serializable {

    private String signName;
    private String cardNo;
    private int canBorrowQuantity;
    private int borrowedQuantity;
    private float fine;

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

    public float getFine() {
        return fine;
    }

    public void setFine(float fine) {
        this.fine = fine;
    }
}
