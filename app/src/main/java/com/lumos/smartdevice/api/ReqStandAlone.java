package com.lumos.smartdevice.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.api.rop.RopDeviceInitData;
import com.lumos.smartdevice.api.rop.RopLoginByAccount;
import com.lumos.smartdevice.db.DbManager;
import com.lumos.smartdevice.model.DeviceBean;
import com.lumos.smartdevice.model.api.DeviceInitDataResultBean;
import com.lumos.smartdevice.own.AppVar;

import java.util.Map;

public class ReqStandAlone implements IReqVersion{

    @Override
    public void deviceInitData(RopDeviceInitData rop, final ReqHandler reqHandler) {

        ResultBean result = null;


        DeviceInitDataResultBean rt=new DeviceInitDataResultBean();

       if(rop.getSceneMode().equals(AppVar.SCENE_MODE_1)) {

           DeviceBean device = new DeviceBean();
           device.setDeviceId("1224567");
           device.setSceneMode(rop.getSceneMode());
           device.setVersionMode(rop.getVesionMode());

//            HashMap<String, CabinetBean> cabinets=new HashMap<>();
//            CabinetBean cabinet=new CabinetBean();
//            cabinet.setCabinetId("cabinet1");
//            cabinet.setName("箱子01");
//            cabinet.setComId("sys1");
//            cabinet.setComBaud(19200);
//            cabinet.setComPrl("LV-DSE-V");
//            cabinet.setLayout("[[\"1-1-1-0\",\"2-2-1-0\"],[\"3-3-1-0\",\"4-4-1-1\"],[\"5-5-1-0\",\"6-6-1-0\"]]");
//            cabinets.put(cabinet.getCabinetId(),cabinet);

           device.setCabinets(DbManager.getInstance().getCabinets());

           rt.setDevice(device);

           result = new ResultBean<>(ResultCode.SUCCESS, "获取成功", rt);

           reqHandler.sendSuccessMessage(result.toJSONString());
       }

    }

    @Override
    public void loginByAccount(RopLoginByAccount rop, final ReqHandler reqHandler) {

        ResultBean result = null;

        if (!DbManager.getInstance().checkUserPassword(rop.getUserName(), rop.getPassword(), "2")) {
            result = new ResultBean(ResultCode.FAILURE, "用户密码错误");
            reqHandler.sendSuccessMessage(result.toJSONString());
            return;
        }

        result = new ResultBean(ResultCode.SUCCESS, "登录成功");

        reqHandler.sendSuccessMessage(result.toJSONString());

    }
}
