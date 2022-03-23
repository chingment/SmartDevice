package com.lumos.smartdevice.devicectrl;

import java.util.List;

public interface IRfeqCtrl {
    void setReadHandler(OnReadHandlerListener listener);
    boolean  sendOpenRead(long ant);
    boolean  sendCloseRead(long ant);

    public  interface OnReadHandlerListener {
        void onData(String epcs);
    }

}
