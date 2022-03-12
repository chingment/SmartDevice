package com.lumos.smartdevice.devicectrl;

public interface ILockerBoxCtrl {
    void open(String id,OnListener onOpenListener);

    public  interface OnListener{
        void onSendCommandSuccess();
        void onSendCommnadFailure();
        void onOpenSuccess();
        void onOpenFailure();
    }
}
