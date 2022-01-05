package com.lumos.smartdevice.api;


import com.lumos.smartdevice.db.dao.ConfigDao;
import com.lumos.smartdevice.db.DbManager;
import com.lumos.smartdevice.own.AppVar;

public class ReqInterface {

    private static IReqVersion iReq;

    public static IReqVersion getInstance(){

        if (iReq == null) {
            int version_mode= DbManager.getInstance().getConfigIntValue(ConfigDao.FIELD_VERSION_MODE);

            switch (version_mode) {
                case AppVar.VERSION_MODE_1:
                    iReq = new ReqStandAlone();
                    break;
                case AppVar.VERSION_MODE_2:
                    iReq = new ReqNetWord();
                    break;
            }
        }


        return iReq;
    }

    public static IReqVersion getInstance(int version_mode) {

        if (version_mode==AppVar.VERSION_MODE_0)
            return getInstance();

        IReqVersion iReq = null;


        switch (version_mode) {
            case 1:
                iReq = new ReqStandAlone();
                break;
            case 2:
                iReq = new ReqNetWord();
                break;
        }

        return iReq;
    }

}
