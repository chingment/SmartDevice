package com.lumos.smartdevice.devicectrl;

public interface ILockeqCtrl {
    boolean isConnect();
    boolean sendOpenSlot(String ant);
    int getSlotStatus(String ant);
    boolean setLight(String ant);
}
