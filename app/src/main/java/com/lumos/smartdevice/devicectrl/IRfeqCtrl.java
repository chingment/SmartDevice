package com.lumos.smartdevice.devicectrl;

import java.util.List;

public interface IRfeqCtrl {
    void setReadHandler(OnReadHandlerListener listener);
    boolean  sendRead();

    public  interface OnReadHandlerListener {
        void onData(List<String> epcs);
    }

}
