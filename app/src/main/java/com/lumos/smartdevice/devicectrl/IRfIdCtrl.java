package com.lumos.smartdevice.devicectrl;

import java.util.List;

public interface IRfIdCtrl {
    void setReadHandler(OnReadHandlerListener listener);
    boolean  sendRead();

    public  interface OnReadHandlerListener {
        void onData(List<String> epcs);
    }

}
