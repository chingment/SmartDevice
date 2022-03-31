package com.lumos.smartdevice.devicectrl;

public interface ILockeqCtrl {
    boolean isConnect();
    boolean sendOpenSlot(String id);
    int getSlotStatus(String id);
}
