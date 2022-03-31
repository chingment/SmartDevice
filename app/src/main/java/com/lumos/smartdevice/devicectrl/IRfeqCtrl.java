package com.lumos.smartdevice.devicectrl;

import java.util.List;

public interface IRfeqCtrl {
    boolean isConnect();
    void setReadHandler(OnReadHandlerListener listener);
    boolean  sendOpenRead(long ant);
    boolean  sendCloseRead(long ant);

    public  interface OnReadHandlerListener {
        void onData(String epcs);
    }

}
