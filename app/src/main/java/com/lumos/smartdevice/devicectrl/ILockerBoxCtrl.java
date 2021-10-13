package com.lumos.smartdevice.devicectrl;

public interface ILockerBoxCtrl {
    void open(String id,OnOpenListener onOpenListener);

    public  interface OnOpenListener{
        void onSuccess();
        void onFailure();
    }
}
