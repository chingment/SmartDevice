package com.lumos.smartdevice.devicectrl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IRfeqCtrl {
    boolean isConnect();
    void setReadHandler(OnReadHandlerListener listener);
    boolean  sendOpenRead(long ant);
    boolean  sendCloseRead(long ant);
    Map<String, TagInfo> getRfIds(long ant);

    public  interface OnReadHandlerListener {
        void onData(String epcs);
    }

}
