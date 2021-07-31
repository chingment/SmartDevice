package com.lumos.smartdevice.api;

import com.lumos.smartdevice.api.rop.RopDeviceInitData;
import com.lumos.smartdevice.api.rop.RopOwnLoginByAccount;
import com.lumos.smartdevice.api.rop.RopOwnLogout;
import com.lumos.smartdevice.api.rop.RopUserGetList;
import com.lumos.smartdevice.api.rop.RopUserSave;
import com.lumos.smartdevice.db.DbManager;
import com.lumos.smartdevice.model.DeviceBean;
import com.lumos.smartdevice.model.UserBean;
import com.lumos.smartdevice.model.api.DeviceInitDataResultBean;
import com.lumos.smartdevice.model.api.OwnLoginResultBean;
import com.lumos.smartdevice.model.api.OwnLogoutResultBean;
import com.lumos.smartdevice.model.api.UserGetListResultBean;
import com.lumos.smartdevice.model.api.UserSaveResultBean;
import com.lumos.smartdevice.own.AppVar;
import com.lumos.smartdevice.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class ReqStandAlone implements IReqVersion{

    @Override
    public void deviceInitData(RopDeviceInitData rop, final ReqHandler reqHandler) {


        ResultBean result = null;


        DeviceInitDataResultBean rt = new DeviceInitDataResultBean();

        if (rop.getSceneMode().equals(AppVar.SCENE_MODE_1)) {

            DeviceBean device = new DeviceBean();
            device.setDeviceId("1224567");
            device.setSceneMode(rop.getSceneMode());
            device.setVersionMode(rop.getVesionMode());
            device.setCabinets(DbManager.getInstance().getCabinets());

            rt.setDevice(device);

            result = new ResultBean<>(ResultCode.SUCCESS, "获取成功", rt);

        } else {

            result = new ResultBean<>(ResultCode.FAILURE, "该场景模式未启用");
        }

        reqHandler.sendSuccessMessage(result.toJSONString());

    }

    @Override
    public void ownLoginByAccount(RopOwnLoginByAccount rop, final ReqHandler reqHandler) {

        reqHandler.sendBeforeSendMessage();

        ResultBean result = null;

        UserBean user=DbManager.getInstance().checkUserPassword(rop.getUserName(), rop.getPassword(), "2");

        if (user==null) {
            result = new ResultBean(ResultCode.FAILURE, "用户密码错误");
            reqHandler.sendSuccessMessage(result.toJSONString());
            return;
        }


        OwnLoginResultBean ret=new OwnLoginResultBean();
        ret.setUserId(user.getUserId());
        ret.setUserName(user.getUserName());
        ret.setFullName(user.getFullName());
        result = new ResultBean<>(ResultCode.SUCCESS, "登录成功",ret);

        reqHandler.sendSuccessMessage(result.toJSONString());

    }

    @Override
    public void ownLogout(RopOwnLogout rop, final ReqHandler reqHandler) {

        reqHandler.sendBeforeSendMessage();


        OwnLogoutResultBean ret=new OwnLogoutResultBean();

        ret.setUserId(rop.getUserId());

        ResultBean result = new ResultBean<>(ResultCode.SUCCESS, "退出成功",ret);

        reqHandler.sendSuccessMessage(result.toJSONString());

    }


    @Override
    public void userSave(RopUserSave rop, final ReqHandler reqHandler) {

        reqHandler.sendBeforeSendMessage();

        if(rop==null) {
            reqHandler.sendSuccessMessage(ResultUtil.isFailureJson("参数不能空"));
            return;
        }

        if(StringUtil.isEmptyNotNull(rop.getUserName())){
            reqHandler.sendSuccessMessage(ResultUtil.isFailureJson("用户名不能为空"));
            return;
        }

        if(StringUtil.isEmptyNotNull(rop.getPassword())){
            reqHandler.sendSuccessMessage(ResultUtil.isFailureJson("密码不能为空"));
            return;
        }

        if(StringUtil.isEmptyNotNull(rop.getFullName())){
            reqHandler.sendSuccessMessage(ResultUtil.isFailureJson("姓名不能为空"));
            return;
        }

        String userName=rop.getUserName().trim();
        String password=rop.getPassword().trim();
        String fullName=rop.getFullName().trim();

        Boolean userIsExist = DbManager.getInstance().checkUserIsExist(userName);

        if (userIsExist) {
            reqHandler.sendSuccessMessage(ResultUtil.isFailureJson("用户名已经存在"));
            return;
        }

        DbManager.getInstance().addUser(userName,password,fullName, "3");

        UserSaveResultBean ret = new UserSaveResultBean();

        ResultBean result = new ResultBean<>(ResultCode.SUCCESS, "保存成功", ret);

        reqHandler.sendSuccessMessage(result.toJSONString());

    }

    @Override
    public void userGetList(RopUserGetList rop, final ReqHandler reqHandler) {
        reqHandler.sendBeforeSendMessage();
        ResultBean result = null;
        UserGetListResultBean ret = new UserGetListResultBean();
        List<UserBean> users=DbManager.getInstance().GetUsers(rop.getPageIndex(),rop.getPageSize(),"3");
        ret.setItems(users);
        result = new ResultBean<>(ResultCode.SUCCESS, "保存成功", ret);
        reqHandler.sendSuccessMessage(result.toJSONString());
    }

}
