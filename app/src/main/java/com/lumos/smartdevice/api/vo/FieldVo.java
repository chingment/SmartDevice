package com.lumos.smartdevice.api.vo;

import java.io.Serializable;

public class FieldVo implements Serializable {
    private int value;
    private String text;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
